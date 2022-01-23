package com.maryanto.dimas.archetype.oauth.service;

import com.maryanto.dimas.archetype.oauth.models.OauthApplication;
import com.maryanto.dimas.archetype.oauth.models.OauthClientDetails;
import com.maryanto.dimas.archetype.oauth.models.OauthGrantType;
import com.maryanto.dimas.archetype.oauth.models.OauthScope;
import com.maryanto.dimas.archetype.oauth.repository.OauthClientDetailsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;

@Service
public class OauthClientDetailsService implements Serializable {

    @Autowired
    private OauthClientDetailsRepository repository;

    public OauthClientDetails findByClientId(String clientId) throws EmptyResultDataAccessException, SQLException {
        return repository.getResourceByClientId(clientId);
    }

    public List<OauthScope> findScopeByClientId(String clientId) throws SQLException {
        return repository.getScopesByClientId(clientId);
    }

    public List<OauthGrantType> findGrantTypeByClientId(String clientId) throws SQLException {
        return repository.getGrantTypeByClientId(clientId);
    }

    public List<OauthApplication> findApplicationByClientId(String clientId) throws SQLException {
        return repository.getApplicationByClientId(clientId);
    }

    public List<String> findRedirectUrlsByClientId(String clientId) throws SQLException {
        return repository.getRedirectUrlsByClientId(clientId);
    }
}
