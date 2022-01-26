package com.maryanto.dimas.archetype.oauth.repository;

import com.maryanto.dimas.archetype.oauth.models.OauthApplication;
import com.maryanto.dimas.archetype.oauth.models.OauthClientDetails;
import com.maryanto.dimas.archetype.oauth.models.OauthGrantType;
import com.maryanto.dimas.archetype.oauth.models.OauthScope;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class OauthClientDetailsRepositoryPostgreSQL implements OauthClientDetailsRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    /**
     * get application by client detail id
     *
     * @param clientId
     * @return
     */
    public List<OauthApplication> getApplicationByClientId(String clientId) throws SQLException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("clientName", clientId);
        //language=PostgreSQL
        StringBuilder query = new StringBuilder("select client_app.id               as application_id,\n" +
                "       client_app.name             as application_name,\n" +
                "       client_app.created_by       as created_by,\n" +
                "       client_app.created_date     as created_date,\n" +
                "       client_app.last_update_by   as last_update_by,\n" +
                "       client_app.last_update_date as last_update_date\n" +
                "from resource.client_detail_applications apps\n" +
                "         join resource.client_details client_app on apps.client_detail_id = client_app.id\n" +
                "where apps.app_id = (\n" +
                "    select apps2.app_id\n" +
                "    from resource.client_detail_applications apps2\n" +
                "             join resource.client_details client_detail on apps2.client_detail_id = client_detail.id\n" +
                "    where client_detail.name = :clientName\n" +
                "    limit 1)");
        return jdbcTemplate.query(
                query.toString(),
                params, (resultSet, i) -> new OauthApplication(
                        resultSet.getString("application_id"),
                        resultSet.getString("application_name"),
                        resultSet.getString("created_by"),
                        resultSet.getTimestamp("created_date"),
                        resultSet.getString("last_update_by"),
                        resultSet.getTimestamp("last_update_date")
                )
        );
    }

    /**
     * @param clientId
     * @return will return null value
     * @throws SQLException
     * @since 1.0.4-release
     */
    @Deprecated
    public List<OauthGrantType> getGrantTypeByClientId(String clientId) throws SQLException {
//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("clientId", clientId);
//        //language=PostgreSQL
//        StringBuilder query = new StringBuilder("select grant_type.id as grant_id, grant_type.name as grant_name, grant_type.description as grant_description\n" +
//                "from oauth.grant_types grant_type\n" +
//                "       join resource.client_detail_grant_types res_grant_type on grant_type.id = res_grant_type.grant_type\n" +
//                "       join resource.client_details res on res_grant_type.client_id = res.id\n" +
//                "where res.name = :clientId");
//
//        return jdbcTemplate.query(query.toString(), params, (resultSet, i) -> new OauthGrantType(
//                resultSet.getInt("grant_id"),
//                resultSet.getString("grant_name"),
//                resultSet.getString("grant_description")
//        ));
        return null;
    }

    public List<String> getRedirectUrlsByClientId(String clientId) throws SQLException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("clientId", clientId);
        //language=PostgreSQL
        StringBuilder query = new StringBuilder("select url.id as url_id, url.redirect_uri as redirect_uri\n" +
                "from resource.client_detail_redirect_uris url\n" +
                "       join resource.client_details app on url.client_id = app.id\n" +
                "where app.name = :clientId");
        return jdbcTemplate.query(query.toString(), params, (resultSet, i) -> resultSet.getString("redirect_uri"));
    }

    /**
     * @param clientId
     * @return will return null value
     * @throws SQLException
     * @since 1.0.4-release
     */
    @Deprecated
    public List<OauthScope> getScopesByClientId(String clientId) throws SQLException {
//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("clientId", clientId);
//        //language=PostgreSQL
//        StringBuilder query = new StringBuilder("select scope.id               as scope_id,\n" +
//                "       scope.name             as scope_name,\n" +
//                "       scope.created_by       as created_by,\n" +
//                "       scope.created_date     as created_date,\n" +
//                "       scope.last_update_by   as last_update_by,\n" +
//                "       scope.last_update_date as last_update_date\n" +
//                "from oauth.client_scopes scope\n" +
//                "       join resource.client_detail_scopes res_scope on scope.id = res_scope.scope_id\n" +
//                "       join resource.client_details res on res_scope.client_id = res.id\n" +
//                "where res.name = :clientId");
//        return this.jdbcTemplate.query(query.toString(), params, (resultSet, i) -> new OauthScope(
//                resultSet.getInt("scope_id"),
//                resultSet.getString("scope_name"),
//                resultSet.getString("created_by"),
//                resultSet.getTimestamp("created_date"),
//                resultSet.getString("last_update_by"),
//                resultSet.getTimestamp("last_update_date")
//        ));
        return null;
    }

    public OauthClientDetails getResourceByClientId(String clientId) throws EmptyResultDataAccessException, SQLException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("clientId", clientId);
        //language=PostgreSQL
        StringBuilder query = new StringBuilder("select id,\n" +
                "       name,\n" +
                "       password,\n" +
                "       is_auto_approve,\n" +
                "       token_expired_in_second,\n" +
                "       created_by,\n" +
                "       created_date,\n" +
                "       last_update_by,\n" +
                "       last_update_date\n" +
                "from resource.client_details\n" +
                "where name = :clientId");
        return this.jdbcTemplate.queryForObject(query.toString(), params, (resultSet, i) -> new OauthClientDetails(
                resultSet.getString("id"),
                resultSet.getString("name"),
                resultSet.getString("password"),
                resultSet.getBoolean("is_auto_approve"),
                resultSet.getInt("token_expired_in_second"),
                resultSet.getString("created_by"),
                resultSet.getTimestamp("created_date"),
                resultSet.getString("last_update_by"),
                resultSet.getTimestamp("last_update_date"),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        ));
    }

}
