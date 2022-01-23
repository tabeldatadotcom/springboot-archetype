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
public class OauthClientDetailsRepositoryOracle11 implements OauthClientDetailsRepository {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    @Override
    public List<OauthApplication> getApplicationByClientId(String clientId) throws SQLException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("clientName", clientId);
        //language=OracleSqlPlus
        StringBuilder query = new StringBuilder("select client_app.ID               as application_id,\n" +
                "       client_app.NAME             as application_name,\n" +
                "       client_app.CREATED_BY       as created_by,\n" +
                "       client_app.CREATED_DATE     as created_date,\n" +
                "       client_app.LAST_UPDATE_BY   as last_update_by,\n" +
                "       client_app.LAST_UPDATE_DATE as last_update_date\n" +
                "from RESOURCE_CLIENT_APPLICATIONS apps\n" +
                "         join RESOURCE_CLIENT_DETAILS client_app on apps.CLIENT_DETAIL_ID = client_app.ID\n" +
                "where apps.APP_ID = (\n" +
                "    select apps2.APP_ID\n" +
                "    from RESOURCE_CLIENT_APPLICATIONS apps2\n" +
                "             join RESOURCE_CLIENT_DETAILS client_detail on apps2.CLIENT_DETAIL_ID = client_detail.ID\n" +
                "    where client_detail.name = :clientName\n" +
                "      and ROWNUM <= 1\n" +
                ")");
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
     * @since 1.0.5-release
     */
    @Override
    @Deprecated
    public List<OauthGrantType> getGrantTypeByClientId(String clientId) throws SQLException {
//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("clientId", clientId);
//        //language=OracleSqlPlus
//        StringBuilder query = new StringBuilder("select GRANT_TYPE.id as grant_id, GRANT_TYPE.NAME as grant_name, GRANT_TYPE.DESCRIPTION as grant_description\n" +
//                "from OAUTH_GRANT_TYPES grant_type\n" +
//                "         join RESOURCE_CLIENT_GRANT_TYPES res_grant_type on grant_type.ID = res_grant_type.GRANT_TYPE\n" +
//                "         join RESOURCE_CLIENT_DETAILS res on res_grant_type.CLIENT_ID = res.ID\n" +
//                "where res.NAME = :clientId");
//
//        return jdbcTemplate.query(query.toString(), params, (resultSet, i) -> new OauthGrantType(
//                resultSet.getInt("grant_id"),
//                resultSet.getString("grant_name"),
//                resultSet.getString("grant_description")
//        ));
        return null;
    }

    @Override
    public List<String> getRedirectUrlsByClientId(String clientId) throws SQLException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("clientId", clientId);
        //language=OracleSqlPlus
        StringBuilder query = new StringBuilder("select url.ID as url_id, url.REDIRECT_URI as redirect_uri\n" +
                "from RESOURCE_CLIENT_REDIRECT_URI url\n" +
                "         join RESOURCE_CLIENT_DETAILS client_detail on url.CLIENT_ID = client_detail.ID\n" +
                "where client_detail.NAME = :clientId");
        return jdbcTemplate.query(query.toString(), params, (resultSet, i) -> resultSet.getString("redirect_uri"));
    }

    /**
     * @param clientId
     * @return will return null value
     * @throws SQLException
     * @since 1.0.4-release
     */
    @Deprecated
    @Override
    public List<OauthScope> getScopesByClientId(String clientId) throws SQLException {
//        MapSqlParameterSource params = new MapSqlParameterSource();
//        params.addValue("clientId", clientId);
//        //language=OracleSqlPlus
//        StringBuilder query = new StringBuilder("select scope.ID               as scope_id,\n" +
//                "       scope.NAME             as scope_name,\n" +
//                "       scope.CREATED_BY       as created_by,\n" +
//                "       scope.CREATED_DATE     as created_date,\n" +
//                "       scope.LAST_UPDATE_BY   as last_update_by,\n" +
//                "       scope.LAST_UPDATE_DATE as last_update_date\n" +
//                "from OAUTH_CLIENT_SCOPE scope\n" +
//                "         join RESOURCE_CLIENT_SCOPES app_scope on app_scope.SCOPE_ID = scope.ID\n" +
//                "         join RESOURCE_CLIENT_DETAILS apps on apps.ID = app_scope.CLIENT_ID\n" +
//                "where apps.NAME = :clientId");
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

    @Override
    public OauthClientDetails getResourceByClientId(String clientId) throws EmptyResultDataAccessException, SQLException {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("clientId", clientId);
        //language=OracleSqlPlus
        StringBuilder query = new StringBuilder("select ID                      as id,\n" +
                "       NAME                    as name,\n" +
                "       PASSWORD                as password,\n" +
                "       IS_AUTO_APPROVE         as is_auto_approve,\n" +
                "       TOKEN_EXPIRED_IN_SECOND as token_expired_in_second,\n" +
                "       CREATED_BY              as created_by,\n" +
                "       CREATED_DATE            as created_date,\n" +
                "       LAST_UPDATE_BY          as last_update_by,\n" +
                "       LAST_UPDATE_DATE        as last_update_date\n" +
                "from RESOURCE_CLIENT_DETAILS\n" +
                "where NAME = :clientId");
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
