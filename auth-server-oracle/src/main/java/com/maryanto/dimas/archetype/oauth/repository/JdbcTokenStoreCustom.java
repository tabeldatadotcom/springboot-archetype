package com.maryanto.dimas.archetype.oauth.repository;

import com.maryanto.dimas.archetype.oauth.models.OauthAccessTokenExtended;
import com.maryanto.dimas.archetype.oauth.models.OauthAccessTokenHistory;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.DataTablesRequest;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.dao.DaoDataTablesPattern;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface JdbcTokenStoreCustom extends TokenStore, Serializable, DaoDataTablesPattern<OauthAccessTokenExtended> {

    void removeAccessToken(OAuth2AccessToken token, String username);

    Collection<OAuth2AccessToken> findTokensByClientId(String clientId);

    Collection<OAuth2AccessToken> findTokensByUserName(String userName);

    Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName);

    List<OauthAccessTokenHistory> historyByUserAndClientIdDatatables(String username, String clientId, DataTablesRequest<OauthAccessTokenHistory> params);

    Long historyByUsernameAndClientIdDatatables(String username, String clientId, OauthAccessTokenHistory param);

    List<OauthAccessTokenHistory> historyByUserDatatables(String username, DataTablesRequest<OauthAccessTokenHistory> params);

    Long historyByUsernameDatatables(String username, OauthAccessTokenHistory param);

    List<OauthAccessTokenHistory> historyByClientIdDatatables(String clientId, DataTablesRequest<OauthAccessTokenHistory> params);

    Long historyByClientIdDatatables(String clientId, OauthAccessTokenHistory param);
}
