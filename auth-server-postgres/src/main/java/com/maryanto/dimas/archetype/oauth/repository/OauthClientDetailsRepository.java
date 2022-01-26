package com.maryanto.dimas.archetype.oauth.repository;

import com.maryanto.dimas.archetype.oauth.models.OauthApplication;
import com.maryanto.dimas.archetype.oauth.models.OauthClientDetails;
import com.maryanto.dimas.archetype.oauth.models.OauthGrantType;
import com.maryanto.dimas.archetype.oauth.models.OauthScope;
import org.springframework.dao.EmptyResultDataAccessException;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

public interface OauthClientDetailsRepository extends Serializable {

    List<OauthApplication> getApplicationByClientId(String clientId) throws SQLException;

    /**
     * @param clientId
     * @return
     * @throws SQLException
     * @since 1.0.4-release
     */
    @Deprecated
    List<OauthGrantType> getGrantTypeByClientId(String clientId) throws SQLException;

    List<String> getRedirectUrlsByClientId(String clientId) throws SQLException;

    /**
     * @param clientId
     * @return
     * @throws SQLException
     * @since 1.0.4-release
     */
    @Deprecated
    List<OauthScope> getScopesByClientId(String clientId) throws SQLException;

    OauthClientDetails getResourceByClientId(String clientId) throws EmptyResultDataAccessException, SQLException;
}
