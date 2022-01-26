package com.maryanto.dimas.archetype.oauth.repository;

import com.maryanto.dimas.archetype.oauth.models.OauthApplication;
import com.maryanto.dimas.archetype.oauth.models.OauthClientDetails;
import com.maryanto.dimas.archetype.oauth.service.OauthClientDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OauthClientDetailsJdbcLoader implements ClientDetailsService {

    @Autowired
    private OauthClientDetailsService service;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        OauthClientDetails client;
        try {
            client = this.service.findByClientId(clientId);
            client.setApplications(service.findApplicationByClientId(clientId));
            client.setOauthGrantTypes(service.findGrantTypeByClientId(clientId));
            client.setRedirectUrls(service.findRedirectUrlsByClientId(clientId));
            client.setOauthScopes(service.findScopeByClientId(clientId));
        } catch (SQLException sqle) {
            log.error("something wrong!", sqle);
            throw new UsernameNotFoundException("client_id not found!", sqle);
        } catch (EmptyResultDataAccessException erde) {
            log.error("username not found", erde);
            throw new UsernameNotFoundException("client_id not found!", erde);
        }

        return new OauthClientDetailsModel(client);
    }

    private class OauthClientDetailsModel implements ClientDetails {
        private final OauthClientDetails client;

        public OauthClientDetailsModel(OauthClientDetails client) {
            this.client = client;
        }

        @Override
        public String getClientId() {
            return this.client.getName();
        }

        @Override
        public Set<String> getResourceIds() {
            Set<String> collect = this.client.getApplications().stream()
                    .map(OauthApplication::getName)
                    .collect(Collectors.toSet());
            collect.add("oauth2-resource");
            return collect;
        }

        @Override
        public boolean isSecretRequired() {
            return true;
        }

        @Override
        public String getClientSecret() {
            return this.client.getPassword();
        }

        @Override
        public boolean isScoped() {
            return !this.client.getOauthScopes().isEmpty();
        }

        @Override
        public Set<String> getScope() {
//            Set<String> collect = this.client.getOauthScopes().stream()
//                    .map(OauthScope::getName)
//                    .collect(Collectors.toSet());
//            collect.add("read");
            return new HashSet<>(Arrays.asList(
                    "scope_read",
                    "scope_write",
                    "scope_delete",
                    "scope_update"));
        }

        @Override
        public Set<String> getAuthorizedGrantTypes() {
//            return this.client.getOauthGrantTypes().stream()
//                    .map(OauthGrantType::getName)
//                    .collect(Collectors.toSet());
            return new HashSet<>(
                    Arrays.asList(
                            "password",
                            "authorization_code",
                            "implicit",
                            "client_credentials",
                            "refresh_token"));
        }

        @Override
        public Set<String> getRegisteredRedirectUri() {
            return this.client.getRedirectUrls().stream()
                    .map(String::toString)
                    .collect(Collectors.toSet());
        }

        @Override
        public Collection<GrantedAuthority> getAuthorities() {
            return new ArrayList<>();
        }

        @Override
        public Integer getAccessTokenValiditySeconds() {
            return this.client.getExpiredInSecond();
        }

        @Override
        public Integer getRefreshTokenValiditySeconds() {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isAutoApprove(String s) {
            return this.client.isAutoApprove();
        }

        @Override
        public Map<String, Object> getAdditionalInformation() {
            return new HashMap<>();
        }
    }
}
