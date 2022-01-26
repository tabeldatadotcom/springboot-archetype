package com.maryanto.dimas.archetype.oauth.service;

import com.maryanto.dimas.archetype.oauth.models.OauthAccessTokenExtended;
import com.maryanto.dimas.archetype.oauth.models.OauthAccessTokenHistory;
import com.maryanto.dimas.archetype.oauth.repository.JdbcTokenStoreCustom;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.DataTablesRequest;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.DataTablesResponse;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.service.ServiceDataTablesPattern;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.*;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.ConsumerTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class DefaultTokenService implements
        AuthorizationServerTokenServices, ResourceServerTokenServices, ConsumerTokenServices, InitializingBean, ServiceDataTablesPattern<OauthAccessTokenExtended> {

    private int refreshTokenValiditySeconds = 2592000;
    private int accessTokenValiditySeconds = 43200;
    private boolean supportRefreshToken = false;
    private boolean reuseRefreshToken = true;
    private JdbcTokenStoreCustom tokenStore;
    private ClientDetailsService clientDetailsService;
    private TokenEnhancer accessTokenEnhancer;
    private AuthenticationManager authenticationManager;

    public DefaultTokenService(
            JdbcTokenStoreCustom tokenStore,
            ClientDetailsService clientDetailsService,
            TokenEnhancer tokenEnhancer,
            AuthenticationManager authManager) {
        this.tokenStore = tokenStore;
        this.clientDetailsService = clientDetailsService;
        this.accessTokenEnhancer = tokenEnhancer;
        this.authenticationManager = authManager;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.tokenStore, "tokenStore must be set");
    }

    @Transactional
    public OAuth2AccessToken createAccessToken(OAuth2Authentication authentication) throws AuthenticationException {
        OAuth2AccessToken existingAccessToken = this.tokenStore.getAccessToken(authentication);
        OAuth2RefreshToken refreshToken = null;
        if (existingAccessToken != null) {
            if (!existingAccessToken.isExpired()) {
                this.tokenStore.storeAccessToken(existingAccessToken, authentication);
                return existingAccessToken;
            }

            if (existingAccessToken.getRefreshToken() != null) {
                refreshToken = existingAccessToken.getRefreshToken();
                this.tokenStore.removeRefreshToken(refreshToken);
            }

            this.tokenStore.removeAccessToken(existingAccessToken);
        }

        if (refreshToken == null) {
            refreshToken = this.createRefreshToken(authentication);
        } else if (refreshToken instanceof ExpiringOAuth2RefreshToken) {
            ExpiringOAuth2RefreshToken expiring = (ExpiringOAuth2RefreshToken) refreshToken;
            if (System.currentTimeMillis() > expiring.getExpiration().getTime()) {
                refreshToken = this.createRefreshToken(authentication);
            }
        }

        OAuth2AccessToken accessToken = this.createAccessToken(authentication, refreshToken);
        this.tokenStore.storeAccessToken(accessToken, authentication);
        refreshToken = accessToken.getRefreshToken();
        if (refreshToken != null) {
            this.tokenStore.storeRefreshToken(refreshToken, authentication);
        }

        return accessToken;
    }

    @Transactional(
            noRollbackFor = {InvalidTokenException.class, InvalidGrantException.class}
    )
    public OAuth2AccessToken refreshAccessToken(String refreshTokenValue, TokenRequest tokenRequest) throws AuthenticationException {
        if (!this.supportRefreshToken) {
            throw new InvalidGrantException("Invalid refresh token: " + refreshTokenValue);
        } else {
            OAuth2RefreshToken refreshToken = this.tokenStore.readRefreshToken(refreshTokenValue);
            if (refreshToken == null) {
                throw new InvalidGrantException("Invalid refresh token: " + refreshTokenValue);
            } else {
                OAuth2Authentication authentication = this.tokenStore.readAuthenticationForRefreshToken(refreshToken);
                if (this.authenticationManager != null && !authentication.isClientOnly()) {
                    Authentication user = new PreAuthenticatedAuthenticationToken(authentication.getUserAuthentication(), "", authentication.getAuthorities());
                    user = this.authenticationManager.authenticate(user);
                    Object details = authentication.getDetails();
                    authentication = new OAuth2Authentication(authentication.getOAuth2Request(), user);
                    authentication.setDetails(details);
                }

                String clientId = authentication.getOAuth2Request().getClientId();
                if (clientId != null && clientId.equals(tokenRequest.getClientId())) {
                    this.tokenStore.removeAccessTokenUsingRefreshToken(refreshToken);
                    if (this.isExpired(refreshToken)) {
                        this.tokenStore.removeRefreshToken(refreshToken);
                        throw new InvalidTokenException("Invalid refresh token (expired): " + refreshToken);
                    } else {
                        authentication = this.createRefreshedAuthentication(authentication, tokenRequest);
                        if (!this.reuseRefreshToken) {
                            this.tokenStore.removeRefreshToken(refreshToken);
                            refreshToken = this.createRefreshToken(authentication);
                        }

                        OAuth2AccessToken accessToken = this.createAccessToken(authentication, refreshToken);
                        this.tokenStore.storeAccessToken(accessToken, authentication);
                        if (!this.reuseRefreshToken) {
                            this.tokenStore.storeRefreshToken(accessToken.getRefreshToken(), authentication);
                        }

                        return accessToken;
                    }
                } else {
                    throw new InvalidGrantException("Wrong client for this refresh token: " + refreshTokenValue);
                }
            }
        }
    }

    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        return this.tokenStore.getAccessToken(authentication);
    }

    private OAuth2Authentication createRefreshedAuthentication(OAuth2Authentication authentication, TokenRequest request) {
        Set<String> scope = request.getScope();
        OAuth2Request clientAuth = authentication.getOAuth2Request().refresh(request);
        if (scope != null && !scope.isEmpty()) {
            Set<String> originalScope = clientAuth.getScope();
            if (originalScope == null || !originalScope.containsAll(scope)) {
                throw new InvalidScopeException("Unable to narrow the scope of the client authentication to " + scope + ".", originalScope);
            }

            clientAuth = clientAuth.narrowScope(scope);
        }

        OAuth2Authentication narrowed = new OAuth2Authentication(clientAuth, authentication.getUserAuthentication());
        return narrowed;
    }

    protected boolean isExpired(OAuth2RefreshToken refreshToken) {
        if (!(refreshToken instanceof ExpiringOAuth2RefreshToken)) {
            return false;
        } else {
            ExpiringOAuth2RefreshToken expiringToken = (ExpiringOAuth2RefreshToken) refreshToken;
            return expiringToken.getExpiration() == null || System.currentTimeMillis() > expiringToken.getExpiration().getTime();
        }
    }

    public OAuth2AccessToken readAccessToken(String accessToken) {
        return this.tokenStore.readAccessToken(accessToken);
    }

    public OAuth2Authentication loadAuthentication(String accessTokenValue) throws AuthenticationException, InvalidTokenException {
        OAuth2AccessToken accessToken = this.tokenStore.readAccessToken(accessTokenValue);
        if (accessToken == null) {
            throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
        } else if (accessToken.isExpired()) {
            this.tokenStore.removeAccessToken(accessToken);
            throw new InvalidTokenException("Access token expired: " + accessTokenValue);
        } else {
            OAuth2Authentication result = this.tokenStore.readAuthentication(accessToken);
            if (result == null) {
                throw new InvalidTokenException("Invalid access token: " + accessTokenValue);
            } else {
                if (this.clientDetailsService != null) {
                    String clientId = result.getOAuth2Request().getClientId();

                    try {
                        this.clientDetailsService.loadClientByClientId(clientId);
                    } catch (ClientRegistrationException var6) {
                        throw new InvalidTokenException("Client not valid: " + clientId, var6);
                    }
                }

                return result;
            }
        }
    }

    public String getClientId(String tokenValue) {
        OAuth2Authentication authentication = this.tokenStore.readAuthentication(tokenValue);
        if (authentication == null) {
            throw new InvalidTokenException("Invalid access token: " + tokenValue);
        } else {
            OAuth2Request clientAuth = authentication.getOAuth2Request();
            if (clientAuth == null) {
                throw new InvalidTokenException("Invalid access token (no client id): " + tokenValue);
            } else {
                return clientAuth.getClientId();
            }
        }
    }

    public boolean revokeToken(String tokenValue) {
        OAuth2AccessToken accessToken = this.tokenStore.readAccessToken(tokenValue);
        if (accessToken == null) {
            return false;
        } else {
            if (accessToken.getRefreshToken() != null) {
                this.tokenStore.removeRefreshToken(accessToken.getRefreshToken());
            }

            this.tokenStore.removeAccessToken(accessToken);
            return true;
        }
    }

    public boolean revokeTokenByUsername(String tokenValue, String username) {
        OAuth2AccessToken accessToken = this.tokenStore.readAccessToken(tokenValue);
        if (accessToken == null) {
            return false;
        } else {
            if (accessToken.getRefreshToken() != null) {
                this.tokenStore.removeRefreshToken(accessToken.getRefreshToken());
            }

            this.tokenStore.removeAccessToken(accessToken, username);
            return true;
        }
    }

    private OAuth2RefreshToken createRefreshToken(OAuth2Authentication authentication) {
        if (!this.isSupportRefreshToken(authentication.getOAuth2Request())) {
            return null;
        } else {
            int validitySeconds = this.getRefreshTokenValiditySeconds(authentication.getOAuth2Request());
            String value = UUID.randomUUID().toString();
            return (OAuth2RefreshToken) (validitySeconds > 0 ? new DefaultExpiringOAuth2RefreshToken(value, new Date(System.currentTimeMillis() + (long) validitySeconds * 1000L)) : new DefaultOAuth2RefreshToken(value));
        }
    }

    private OAuth2AccessToken createAccessToken(OAuth2Authentication authentication, OAuth2RefreshToken refreshToken) {
        DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(UUID.randomUUID().toString());
        int validitySeconds = this.getAccessTokenValiditySeconds(authentication.getOAuth2Request());
        if (validitySeconds > 0) {
            token.setExpiration(new Date(System.currentTimeMillis() + (long) validitySeconds * 1000L));
        }

        token.setRefreshToken(refreshToken);
        token.setScope(authentication.getOAuth2Request().getScope());
        return (OAuth2AccessToken) (this.accessTokenEnhancer != null ? this.accessTokenEnhancer.enhance(token, authentication) : token);
    }

    protected int getAccessTokenValiditySeconds(OAuth2Request clientAuth) {
        if (this.clientDetailsService != null) {
            ClientDetails client = this.clientDetailsService.loadClientByClientId(clientAuth.getClientId());
            Integer validity = client.getAccessTokenValiditySeconds();
            if (validity != null) {
                return validity;
            }
        }

        return this.accessTokenValiditySeconds;
    }

    protected int getRefreshTokenValiditySeconds(OAuth2Request clientAuth) {
        if (this.clientDetailsService != null) {
            ClientDetails client = this.clientDetailsService.loadClientByClientId(clientAuth.getClientId());
            Integer validity = client.getRefreshTokenValiditySeconds();
            if (validity != null) {
                return validity;
            }
        }

        return this.refreshTokenValiditySeconds;
    }

    protected boolean isSupportRefreshToken(OAuth2Request clientAuth) {
        if (this.clientDetailsService != null) {
            ClientDetails client = this.clientDetailsService.loadClientByClientId(clientAuth.getClientId());
            return client.getAuthorizedGrantTypes().contains("refresh_token");
        } else {
            return this.supportRefreshToken;
        }
    }

    public void setRefreshTokenValiditySeconds(int refreshTokenValiditySeconds) {
        this.refreshTokenValiditySeconds = refreshTokenValiditySeconds;
    }

    public void setAccessTokenValiditySeconds(int accessTokenValiditySeconds) {
        this.accessTokenValiditySeconds = accessTokenValiditySeconds;
    }

    public void setSupportRefreshToken(boolean supportRefreshToken) {
        this.supportRefreshToken = supportRefreshToken;
    }

    public void setReuseRefreshToken(boolean reuseRefreshToken) {
        this.reuseRefreshToken = reuseRefreshToken;
    }

    @Override
    public DataTablesResponse<OauthAccessTokenExtended> datatables(DataTablesRequest<OauthAccessTokenExtended> params) {
        List<OauthAccessTokenExtended> list = tokenStore.datatables(params);
        Long rowCount = tokenStore.datatables(params.getValue());
        return new DataTablesResponse<>(list, params.getDraw(), rowCount, rowCount);

    }

    public DataTablesResponse<OauthAccessTokenHistory> historyByUserAndClientIdDatatables(String username, String clientId, DataTablesRequest<OauthAccessTokenHistory> params) {
        List<OauthAccessTokenHistory> list = tokenStore.historyByUserAndClientIdDatatables(username, clientId, params);
        Long rowCount = tokenStore.historyByUsernameAndClientIdDatatables(username, clientId, params.getValue());
        return new DataTablesResponse<>(list, params.getDraw(), rowCount, rowCount);
    }

    public DataTablesResponse<OauthAccessTokenHistory> historyByUserDatatables(String username, DataTablesRequest<OauthAccessTokenHistory> params) {
        List<OauthAccessTokenHistory> list = tokenStore.historyByUserDatatables(username, params);
        Long rowCount = tokenStore.historyByUsernameDatatables(username, params.getValue());
        return new DataTablesResponse<>(list, params.getDraw(), rowCount, rowCount);
    }

    public DataTablesResponse<OauthAccessTokenHistory> historyByClientIdDatatables(String username, DataTablesRequest<OauthAccessTokenHistory> params) {
        List<OauthAccessTokenHistory> list = tokenStore.historyByClientIdDatatables(username, params);
        Long rowCount = tokenStore.historyByClientIdDatatables(username, params.getValue());
        return new DataTablesResponse<>(list, params.getDraw(), rowCount, rowCount);
    }
}
