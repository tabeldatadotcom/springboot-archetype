package com.maryanto.dimas.archetype.oauth.repository;

import com.maryanto.dimas.archetype.oauth.models.OauthAccessTokenExtended;
import com.maryanto.dimas.archetype.oauth.models.OauthAccessTokenHistory;
import com.maryanto.dimas.archetype.utils.QueryComparator;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.DataTablesRequest;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.OrderingByColumns;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.common.util.SerializationUtils;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.util.Assert;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Slf4j
@Configuration
public class JdbcTokenStoreCustomPostgreSQL extends JdbcTokenStore implements JdbcTokenStoreCustom {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    //language=PostgreSQL
    private String insertAccessTokenSql = "insert into oauth.access_token (token_id, token, auth_id, user_name, client_id, authentication, refresh_token, ip_address)\n" +
            "values (?, ?, ?, ?, ?, ?, ?, ?)";
    //language=PostgreSQL
    private String selectAccessTokenSql = "select token_id, token\n" +
            "from oauth.access_token\n" +
            "where token_id = ?";
    //language=PostgreSQL
    private String selectAccessTokenAuthenticationSql = "select token_id, authentication\n" +
            "from oauth.access_token\n" +
            "where token_id = ?";
    //language=PostgreSQL
    private String selectAccessTokenFromAuthenticationSql = "select token_id, token\n" +
            "from oauth.access_token\n" +
            "where auth_id = ?";
    //language=PostgreSQL
    private String selectAccessTokensFromUserNameAndClientIdSql = "select token_id, token\n" +
            "from oauth.access_token\n" +
            "where user_name = ?\n" +
            "  and client_id = ?";
    //language=PostgreSQL
    private String selectAccessTokensFromUserNameSql = "select token_id, token\n" +
            "from oauth.access_token\n" +
            "where user_name = ?";
    //language=PostgreSQL
    private String selectAccessTokensFromClientIdSql = "select token_id, token\n" +
            "from oauth.access_token\n" +
            "where client_id = ?";
    //language=PostgreSQL
    private String deleteAccessTokenSql = "delete\n" +
            "from oauth.access_token\n" +
            "where token_id = ?";
    //language=PostgreSQL
    private String insertRefreshTokenSql = "insert into oauth.refresh_token (token_id, token, authentication)\n" +
            "values (?, ?, ?)";
    //language=PostgreSQL
    private String selectRefreshTokenSql = "select token_id, token\n" +
            "from oauth.refresh_token\n" +
            "where token_id = ?";
    //language=PostgreSQL
    private String selectRefreshTokenAuthenticationSql = "select token_id, authentication\n" +
            "from oauth.refresh_token\n" +
            "where token_id = ?";
    //language=PostgreSQL
    private String deleteRefreshTokenSql = "delete\n" +
            "from oauth.refresh_token\n" +
            "where token_id = ?";
    //language=PostgreSQL
    private String deleteAccessTokenFromRefreshTokenSql = "delete\n" +
            "from oauth.access_token\n" +
            "where refresh_token = ?";
    //language=PostgreSQL
    private String insertHistoryAccessTokenSql = "insert into oauth.history_access_token (id, access_id, client_id, token, ip_address, user_name, login_at, is_logout, logout_at, logout_by)\n" +
            "VALUES (uuid_generate_v4(), ?, ?, ?, ?, ?, now(), false, null, null)";
    //language=PostgreSQL
    private String updateHistoryAccessTokenSql = "update oauth.history_access_token\n" +
            "set is_logout = true,\n" +
            "    logout_at = now(),\n" +
            "    logout_by = ?\n" +
            "where access_id = ?\n" +
            "  and is_logout = false";
    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    public JdbcTokenStoreCustomPostgreSQL(DataSource dataSource) {
        super(dataSource);
        Assert.notNull(dataSource, "DataSource required");
        setAuthenticationKeyGenerator(authenticationKeyGenerator);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.namedJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    private String getRemoteAddress() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        WebAuthenticationDetails details = (WebAuthenticationDetails) auth.getDetails();
        String ipAddress = details.getRemoteAddress();
        return ipAddress;
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        OAuth2AccessToken accessToken = null;
        String key = this.authenticationKeyGenerator.extractKey(authentication);

        try {
            accessToken = this.jdbcTemplate.queryForObject(
                    this.selectAccessTokenFromAuthenticationSql,
                    (rs, rowNum) -> JdbcTokenStoreCustomPostgreSQL.this.deserializeAccessToken(rs.getBytes(2)),
                    key);
        } catch (EmptyResultDataAccessException var5) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to find access token for authentication " + authentication);
            }
        } catch (IllegalArgumentException var6) {
            log.error("Could not extract access token for authentication " + authentication, var6);
        }

        if (accessToken != null && !key.equals(this.authenticationKeyGenerator.extractKey(this.readAuthentication(accessToken.getValue())))) {
            this.removeAccessToken(accessToken.getValue());
            this.storeAccessToken(accessToken, authentication);
        }

        return accessToken;
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        String refreshToken = null;
        if (token.getRefreshToken() != null) {
            refreshToken = token.getRefreshToken().getValue();
        }

        if (this.readAccessToken(token.getValue()) != null) {
            this.removeAccessToken(token.getValue());
        }

        String authId = this.authenticationKeyGenerator.extractKey(authentication);
        String tokenId = this.extractTokenKey(token.getValue());
        String username = authentication.isClientOnly() ? null : authentication.getName();
        String clientId = authentication.getOAuth2Request().getClientId();

        SqlLobValue tokenLobValue = new SqlLobValue(this.serializeAccessToken(token));
        this.jdbcTemplate.update(
                this.insertAccessTokenSql,
                new Object[]{
                        tokenId,
                        tokenLobValue,
                        authId,
                        username,
                        clientId,
                        new SqlLobValue(this.serializeAuthentication(authentication)),
                        this.extractTokenKey(refreshToken),
                        getRemoteAddress()
                }, new int[]{Types.VARCHAR, Types.BLOB, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BLOB, Types.VARCHAR, Types.VARCHAR});

        this.jdbcTemplate.update(
                this.insertHistoryAccessTokenSql,
                new Object[]{
                        tokenId,
                        clientId,
                        tokenLobValue,
                        getRemoteAddress(),
                        username},
                new int[]{Types.VARCHAR, Types.VARCHAR, Types.BLOB, Types.VARCHAR, Types.VARCHAR});
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        OAuth2AccessToken accessToken = null;

        try {
            accessToken = this.jdbcTemplate.queryForObject(
                    this.selectAccessTokenSql,
                    (rs, rowNum) -> JdbcTokenStoreCustomPostgreSQL.this.deserializeAccessToken(rs.getBytes(2)),
                    this.extractTokenKey(tokenValue));
        } catch (EmptyResultDataAccessException var4) {
            if (log.isInfoEnabled()) {
                log.info("Failed to find access token for token " + tokenValue);
            }
        } catch (IllegalArgumentException var5) {
            log.warn("Failed to deserialize access token for " + tokenValue, var5);
            this.removeAccessToken(tokenValue);
        }

        return accessToken;
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        this.removeAccessToken(token.getValue());
    }

    public void removeAccessToken(OAuth2AccessToken token, String username) {
        this.removeAccessToken(token.getValue(), username);
    }

    @Override
    public void removeAccessToken(String tokenValue) {
        String tokenId = this.extractTokenKey(tokenValue);

        this.jdbcTemplate.update(
                this.deleteAccessTokenSql,
                tokenId
        );

        this.jdbcTemplate.update(
                this.updateHistoryAccessTokenSql,
                "timeout", tokenId);
    }

    public void removeAccessToken(String tokenValue, String username) {
        String tokenId = this.extractTokenKey(tokenValue);

        this.jdbcTemplate.update(
                this.deleteAccessTokenSql,
                tokenId
        );

        this.jdbcTemplate.update(
                this.updateHistoryAccessTokenSql,
                username, tokenId);
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return this.readAuthentication(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String token) {
        OAuth2Authentication authentication = null;

        try {
            authentication = this.jdbcTemplate.queryForObject(
                    this.selectAccessTokenAuthenticationSql,
                    (rs, rowNum) -> JdbcTokenStoreCustomPostgreSQL.this.deserializeAuthentication(rs.getBytes(2)),
                    this.extractTokenKey(token));
        } catch (EmptyResultDataAccessException var4) {
            if (log.isInfoEnabled()) {
                log.info("Failed to find access token for token " + token);
            }
        } catch (IllegalArgumentException var5) {
            log.warn("Failed to deserialize authentication for " + token, var5);
            this.removeAccessToken(token);
        }

        return authentication;
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        this.jdbcTemplate.update(
                this.insertRefreshTokenSql,
                new Object[]{
                        this.extractTokenKey(refreshToken.getValue()),
                        new SqlLobValue(this.serializeRefreshToken(refreshToken)),
                        new SqlLobValue(this.serializeAuthentication(authentication))},
                new int[]{12, 2004, 2004});
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String token) {
        OAuth2RefreshToken refreshToken = null;

        try {
            refreshToken = this.jdbcTemplate.queryForObject(
                    this.selectRefreshTokenSql,
                    (rs, rowNum) -> JdbcTokenStoreCustomPostgreSQL.this.deserializeRefreshToken(rs.getBytes(2)),
                    this.extractTokenKey(token));
        } catch (EmptyResultDataAccessException var4) {
            if (log.isInfoEnabled()) {
                log.info("Failed to find refresh token for token " + token);
            }
        } catch (IllegalArgumentException var5) {
            log.warn("Failed to deserialize refresh token for token " + token, var5);
            this.removeRefreshToken(token);
        }

        return refreshToken;
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        this.removeRefreshToken(token.getValue());
    }

    @Override
    public void removeRefreshToken(String token) {
        this.jdbcTemplate.update(this.deleteRefreshTokenSql, this.extractTokenKey(token));
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return this.readAuthenticationForRefreshToken(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(String value) {
        OAuth2Authentication authentication = null;

        try {
            authentication = this.jdbcTemplate.queryForObject(
                    this.selectRefreshTokenAuthenticationSql,
                    (rs, rowNum) -> JdbcTokenStoreCustomPostgreSQL.this.deserializeAuthentication(rs.getBytes(2)),
                    this.extractTokenKey(value));
        } catch (EmptyResultDataAccessException var4) {
            if (log.isInfoEnabled()) {
                log.info("Failed to find access token for token " + value);
            }
        } catch (IllegalArgumentException var5) {
            log.warn("Failed to deserialize access token for " + value, var5);
            this.removeRefreshToken(value);
        }

        return authentication;
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        this.removeAccessTokenUsingRefreshToken(refreshToken.getValue());
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(String refreshToken) {
        this.jdbcTemplate.update(this.deleteAccessTokenFromRefreshTokenSql, new Object[]{this.extractTokenKey(refreshToken)}, new int[]{12});
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        List accessTokens = new ArrayList();

        try {
            accessTokens = this.jdbcTemplate.query(
                    this.selectAccessTokensFromClientIdSql,
                    new JdbcTokenStoreCustomPostgreSQL.SafeAccessTokenRowMapper(),
                    clientId);
        } catch (EmptyResultDataAccessException var4) {
            if (log.isInfoEnabled()) {
                log.info("Failed to find access token for clientId " + clientId);
            }
        }

        accessTokens = this.removeNulls(accessTokens);
        return accessTokens;
    }

    public Collection<OAuth2AccessToken> findTokensByUserName(String userName) {
        List<OAuth2AccessToken> accessTokens = new ArrayList<>();

        try {
            accessTokens = this.jdbcTemplate.query(
                    this.selectAccessTokensFromUserNameSql,
                    new JdbcTokenStoreCustomPostgreSQL.SafeAccessTokenRowMapper(),
                    userName);
        } catch (EmptyResultDataAccessException var4) {
            if (log.isInfoEnabled()) {
                log.info("Failed to find access token for userName " + userName);
            }
        }

        accessTokens = this.removeNulls(accessTokens);
        return accessTokens;
    }

    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        List<OAuth2AccessToken> accessTokens = new ArrayList<>();

        try {
            accessTokens = this.jdbcTemplate.query(
                    this.selectAccessTokensFromUserNameAndClientIdSql,
                    new JdbcTokenStoreCustomPostgreSQL.SafeAccessTokenRowMapper(),
                    userName, clientId);
        } catch (EmptyResultDataAccessException var5) {
            if (log.isInfoEnabled()) {
                log.info("Failed to find access token for clientId " + clientId + " and userName " + userName);
            }
        }

        accessTokens = this.removeNulls(accessTokens);
        return accessTokens;
    }

    private List<OAuth2AccessToken> removeNulls(List<OAuth2AccessToken> accessTokens) {
        List<OAuth2AccessToken> tokens = new ArrayList<>();
        Iterator var3 = accessTokens.iterator();

        while (var3.hasNext()) {
            OAuth2AccessToken token = (OAuth2AccessToken) var3.next();
            if (token != null) {
                tokens.add(token);
            }
        }

        return tokens;
    }

    protected String extractTokenKey(String value) {
        if (value == null) {
            return null;
        } else {
            MessageDigest digest;
            try {
                digest = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException var5) {
                throw new IllegalStateException("MD5 algorithm not available.  Fatal (should be in the JDK).");
            }

            try {
                byte[] bytes = digest.digest(value.getBytes("UTF-8"));
                return String.format("%032x", new BigInteger(1, bytes));
            } catch (UnsupportedEncodingException var4) {
                throw new IllegalStateException("UTF-8 encoding not available.  Fatal (should be in the JDK).");
            }
        }
    }

    protected byte[] serializeAccessToken(OAuth2AccessToken token) {
        return SerializationUtils.serialize(token);
    }

    protected byte[] serializeRefreshToken(OAuth2RefreshToken token) {
        return SerializationUtils.serialize(token);
    }

    protected byte[] serializeAuthentication(OAuth2Authentication authentication) {
        return SerializationUtils.serialize(authentication);
    }

    protected OAuth2AccessToken deserializeAccessToken(byte[] token) {
        return (OAuth2AccessToken) SerializationUtils.deserialize(token);
    }

    protected OAuth2RefreshToken deserializeRefreshToken(byte[] token) {
        return (OAuth2RefreshToken) SerializationUtils.deserialize(token);
    }

    protected OAuth2Authentication deserializeAuthentication(byte[] authentication) {
        return (OAuth2Authentication) SerializationUtils.deserialize(authentication);
    }

    /**
     * private String username;
     * private String clientId;
     * private String ipAddress;
     * private String accessToken;
     * private Timestamp loginAt;
     * private Timestamp expiredAt;
     *
     * @param params
     * @return
     */
    @Override
    public List<OauthAccessTokenExtended> datatables(DataTablesRequest<OauthAccessTokenExtended> params) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        //language=PostgreSQL
        String baseQuery = "select auth_id    as authentication_id,\n" +
                "       token_id   as token_id,\n" +
                "       token      as access_token,\n" +
                "       user_name   as username,\n" +
                "       client_id  as client_id,\n" +
                "       ip_address as ip_address,\n" +
                "       login_at   as login_time\n" +
                "from oauth.access_token where 1=1 ";
        OauthAccessTokenExtendedQueryComparator queryComparator = new OauthAccessTokenExtendedQueryComparator(baseQuery, map);
        StringBuilder query = queryComparator.getQuery(params.getValue());
        map = queryComparator.getParameters();

        OrderingByColumns serviceColumn = new OrderingByColumns("user_name", "client_id", "ip_address", "login_time");
        query.append(serviceColumn.orderBy(params.getColDir(), params.getColOrder()));


        query.append(" limit :limit ").append(" offset :offset ");
        map.addValue("limit", params.getLength());
        map.addValue("offset", params.getStart());

        if (log.isDebugEnabled())
            log.debug("query : {}", query.toString());

        List<OauthAccessTokenExtended> list = this.namedJdbcTemplate.query(query.toString(), map, (resultSet, i) -> {
            try {
                OAuth2AccessToken oauth2AccessToken = JdbcTokenStoreCustomPostgreSQL.
                        this.deserializeAccessToken(resultSet.getBytes("access_token"));
                return new OauthAccessTokenExtended(
                        resultSet.getString("username"),
                        resultSet.getString("client_id"),
                        resultSet.getString("ip_address"),
                        oauth2AccessToken.getValue(),
                        resultSet.getTimestamp("login_time"),
                        new Timestamp(oauth2AccessToken.getExpiration().getTime())
                );
            } catch (IllegalArgumentException var5) {
                String token = resultSet.getString("token_id");
                JdbcTokenStoreCustomPostgreSQL.this.jdbcTemplate.update(JdbcTokenStoreCustomPostgreSQL.this.deleteAccessTokenSql, token);
                return null;
            }
        });
        return list;
    }

    @Override
    public Long datatables(OauthAccessTokenExtended value) {
        //language=PostgreSQL
        String baseQuery = "select count(*) as rows from oauth.access_token where 1=1 ";
        MapSqlParameterSource map = new MapSqlParameterSource();

        OauthAccessTokenExtendedQueryComparator queryComparator = new OauthAccessTokenExtendedQueryComparator(baseQuery, map);
        StringBuilder query = queryComparator.getQuery(value);
        map = queryComparator.getParameters();

        return this.namedJdbcTemplate.queryForObject(
                query.toString(), map,
                (resultSet, i) -> resultSet.getLong("rows")
        );
    }

    /**
     * private String username;
     * private String clientId;
     * private String ipAddress;
     * private String accessToken;
     * private Timestamp loginAt;
     * private Timestamp expiredAt;
     * private boolean logout;
     * private Timestamp logoutAt;
     * private String logoutBy;
     *
     * @param username
     * @param clientId
     * @param params
     * @return
     */
    public List<OauthAccessTokenHistory> historyByUserAndClientIdDatatables(
            String username,
            String clientId,
            DataTablesRequest<OauthAccessTokenHistory> params) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        //language=PostgreSQL
        String baseQuery = "select access_id,\n" +
                "       token,\n" +
                "       client_id,\n" +
                "       ip_address,\n" +
                "       user_name,\n" +
                "       login_at,\n" +
                "       is_logout,\n" +
                "       logout_at,\n" +
                "       logout_by\n" +
                "from oauth.history_access_token\n" +
                "where 1 = 1\n" +
                "  and client_id = :clientId\n" +
                "  and user_name = :userName ";
        map.addValue("clientId", clientId);
        map.addValue("userName", username);

        OauthAccessTokenHistoryByUserAndClientId queryComparator = new OauthAccessTokenHistoryByUserAndClientId(baseQuery, map);
        StringBuilder queryBuilder = queryComparator.getQuery(params.getValue());
        map = queryComparator.getParameters();

        OrderingByColumns serviceOrder = new OrderingByColumns("user_name", "client_id", "ip_address", "login_at", "is_logout", "logout_at", "logout_by", "logout_by", "");
        queryBuilder.append(serviceOrder.orderBy(params.getColDir(), params.getColOrder()));

        queryBuilder.append(" limit :limit ").append(" offset :offset ");
        map.addValue("limit", params.getLength());
        map.addValue("offset", params.getStart());

        if (log.isDebugEnabled())
            log.debug("query : {}", queryBuilder.toString());

        List<OauthAccessTokenHistory> list = this.namedJdbcTemplate.query(queryBuilder.toString(), map, (resultSet, i) -> {
            try {
                OAuth2AccessToken oauth2AccessToken = JdbcTokenStoreCustomPostgreSQL.this.deserializeAccessToken(resultSet.getBytes("token"));
                return new OauthAccessTokenHistory(
                        resultSet.getString("user_name"),
                        resultSet.getString("client_id"),
                        resultSet.getString("ip_address"),
                        oauth2AccessToken.getValue(),
                        resultSet.getTimestamp("login_at"),
                        new Timestamp(oauth2AccessToken.getExpiration().getTime()),
                        resultSet.getBoolean("is_logout"),
                        resultSet.getTimestamp("logout_at"),
                        resultSet.getString("logout_by")
                );
            } catch (IllegalArgumentException var5) {
                String token = resultSet.getString("access_id");
                JdbcTokenStoreCustomPostgreSQL.this.jdbcTemplate.update(JdbcTokenStoreCustomPostgreSQL.this.deleteAccessTokenSql, token);
                return null;
            }
        });
        return list;

    }

    public Long historyByUsernameAndClientIdDatatables(String username, String clientId, OauthAccessTokenHistory param) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        //language=PostgreSQL
        String baseQuery = "select count(*) as rows\n" +
                "from oauth.history_access_token\n" +
                "where 1 = 1 \n" +
                "  and client_id = :clientId\n " +
                "  and user_name = :userName ";
        map.addValue("clientId", clientId);
        map.addValue("userName", username);

        OauthAccessTokenHistoryByUserAndClientId queryComparator = new OauthAccessTokenHistoryByUserAndClientId(baseQuery, map);
        StringBuilder queryBuilder = queryComparator.getQuery(param);
        map = queryComparator.getParameters();

        return this.namedJdbcTemplate.queryForObject(queryBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("rows"));
    }

    /**
     * private String username;
     * private String clientId;
     * private String ipAddress;
     * private String accessToken;
     * private Timestamp loginAt;
     * private Timestamp expiredAt;
     * private boolean logout;
     * private Timestamp logoutAt;
     * private String logoutBy;
     *
     * @param username
     * @param params
     * @return
     */
    public List<OauthAccessTokenHistory> historyByUserDatatables(
            String username,
            DataTablesRequest<OauthAccessTokenHistory> params) {
        MapSqlParameterSource map = new MapSqlParameterSource();

        String baseQuery = "select access_id,\n" +
                "       token,\n" +
                "       client_id,\n" +
                "       ip_address,\n" +
                "       user_name,\n" +
                "       login_at,\n" +
                "       is_logout,\n" +
                "       logout_at,\n" +
                "       logout_by\n" +
                "from oauth.history_access_token\n" +
                "where 1 = 1\n" +
                "  and user_name = :userName ";
        map.addValue("userName", username);

        OauthAccessTokenHistoryByUser queryComparator = new OauthAccessTokenHistoryByUser(baseQuery, map);
        StringBuilder queryBuilder = queryComparator.getQuery(params.getValue());
        map = queryComparator.getParameters();

        OrderingByColumns serviceColumns = new OrderingByColumns("user_name", "client_id", "ip_address", "login_at", "is_logout", "logout_at", "logout_by");
        queryBuilder.append(serviceColumns.orderBy(params.getColDir(), params.getColOrder()));

        queryBuilder.append(" limit :limit ").append(" offset :offset ");
        map.addValue("limit", params.getLength());
        map.addValue("offset", params.getStart());

        if (log.isDebugEnabled())
            log.debug("query : {}", queryBuilder.toString());

        List<OauthAccessTokenHistory> list = this.namedJdbcTemplate.query(queryBuilder.toString(), map, (resultSet, i) -> {
            try {
                OAuth2AccessToken oauth2AccessToken = JdbcTokenStoreCustomPostgreSQL.this.deserializeAccessToken(resultSet.getBytes("token"));
                return new OauthAccessTokenHistory(
                        resultSet.getString("user_name"),
                        resultSet.getString("client_id"),
                        resultSet.getString("ip_address"),
                        oauth2AccessToken.getValue(),
                        resultSet.getTimestamp("login_at"),
                        new Timestamp(oauth2AccessToken.getExpiration().getTime()),
                        resultSet.getBoolean("is_logout"),
                        resultSet.getTimestamp("logout_at"),
                        resultSet.getString("logout_by")
                );
            } catch (IllegalArgumentException var5) {
                String token = resultSet.getString("access_id");
                JdbcTokenStoreCustomPostgreSQL.this.jdbcTemplate.update(JdbcTokenStoreCustomPostgreSQL.this.deleteAccessTokenSql, token);
                return null;
            }
        });
        return list;

    }

    public Long historyByUsernameDatatables(String username, OauthAccessTokenHistory param) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        //language=PostgreSQL
        String baseQuery = "select count(*) as rows\n" +
                "from oauth.history_access_token\n" +
                "where 1 = 1 \n" +
                "  and user_name = :userName ";
        map.addValue("userName", username);

        OauthAccessTokenHistoryByUser queryComparator = new OauthAccessTokenHistoryByUser(baseQuery, map);
        StringBuilder queryBuilder = queryComparator.getQuery(param);
        map = queryComparator.getParameters();

        return this.namedJdbcTemplate.queryForObject(
                queryBuilder.toString(), map,
                (resultSet, i) -> resultSet.getLong("rows"));
    }

    /**
     * private String username;
     * private String clientId;
     * private String ipAddress;
     * private String accessToken;
     * private Timestamp loginAt;
     * private Timestamp expiredAt;
     * private boolean logout;
     * private Timestamp logoutAt;
     * private String logoutBy;
     *
     * @param clientId
     * @param params
     * @return
     */
    public List<OauthAccessTokenHistory> historyByClientIdDatatables(
            String clientId,
            DataTablesRequest<OauthAccessTokenHistory> params) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        String baseQuery = "select access_id,\n" +
                "       token,\n" +
                "       client_id,\n" +
                "       ip_address,\n" +
                "       user_name,\n" +
                "       login_at,\n" +
                "       is_logout,\n" +
                "       logout_at,\n" +
                "       logout_by\n" +
                "from oauth.history_access_token\n" +
                "where 1 = 1\n" +
                "  and client_id = :clientId ";
        map.addValue("clientId", clientId);

        OauthAccessTokenHistoryByClientId queryComparator = new OauthAccessTokenHistoryByClientId(baseQuery, map);
        StringBuilder queryBuilder = queryComparator.getQuery(params.getValue());
        map = queryComparator.getParameters();

        OrderingByColumns serviceColumns = new OrderingByColumns("user_name", "client_id", "ip_address", "login_at", "is_logout", "is_logout", "logout_at", "logout_by");
        queryBuilder.append(serviceColumns.orderBy(params.getColDir(), params.getColOrder()));

        queryBuilder.append(" limit :limit ").append(" offset :offset ");
        map.addValue("limit", params.getLength());
        map.addValue("offset", params.getStart());

        if (log.isDebugEnabled())
            log.debug("query : {}", queryBuilder.toString());

        List<OauthAccessTokenHistory> list = this.namedJdbcTemplate.query(queryBuilder.toString(), map, (resultSet, i) -> {
            try {
                OAuth2AccessToken oauth2AccessToken = JdbcTokenStoreCustomPostgreSQL.this.deserializeAccessToken(resultSet.getBytes("token"));
                return new OauthAccessTokenHistory(
                        resultSet.getString("user_name"),
                        resultSet.getString("client_id"),
                        resultSet.getString("ip_address"),
                        oauth2AccessToken.getValue(),
                        resultSet.getTimestamp("login_at"),
                        new Timestamp(oauth2AccessToken.getExpiration().getTime()),
                        resultSet.getBoolean("is_logout"),
                        resultSet.getTimestamp("logout_at"),
                        resultSet.getString("logout_by")
                );
            } catch (IllegalArgumentException var5) {
                String token = resultSet.getString("access_id");
                JdbcTokenStoreCustomPostgreSQL.this.jdbcTemplate.update(JdbcTokenStoreCustomPostgreSQL.this.deleteAccessTokenSql, token);
                return null;
            }
        });
        return list;

    }

    public Long historyByClientIdDatatables(String clientId, OauthAccessTokenHistory param) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        String baseQuery = "select count(*) as rows\n" +
                "from oauth.history_access_token\n" +
                "where 1 = 1 \n" +
                "  and client_id = :clientId ";
        map.addValue("clientId", clientId);
        OauthAccessTokenHistoryByClientId queryComparator = new OauthAccessTokenHistoryByClientId(baseQuery, map);
        StringBuilder queryBuilder = queryComparator.getQuery(param);
        map = queryComparator.getParameters();

        return this.namedJdbcTemplate.queryForObject(queryBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("rows"));
    }

    private class OauthAccessTokenExtendedQueryComparator implements QueryComparator<OauthAccessTokenExtended> {

        private StringBuilder builder;
        private MapSqlParameterSource parameterSource;

        public OauthAccessTokenExtendedQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.parameterSource = parameterSource;
        }

        @Override
        public StringBuilder getQuery(OauthAccessTokenExtended value) {
            if (StringUtils.isNotBlank(value.getClientId())) {
                builder.append(" and client_id like :clientId ");
                parameterSource.addValue("clientId", new StringBuilder("%").append(value.getClientId()).append("%").toString());
            }

            if (StringUtils.isNotBlank(value.getUsername())) {
                builder.append(" and user_name like :userName ");
                parameterSource.addValue("userName", new StringBuilder("%").append(value.getUsername()).append("%").toString());
            }

            if (StringUtils.isNotBlank(value.getIpAddress())) {
                builder.append(" and ip_address = :ipAddress ");
                parameterSource.addValue("ipAddress", value.getIpAddress());
            }

            return this.builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return this.parameterSource;
        }
    }

    private class OauthAccessTokenHistoryByUserAndClientId implements QueryComparator<OauthAccessTokenHistory> {

        private StringBuilder builder;
        private MapSqlParameterSource parameterSource;

        public OauthAccessTokenHistoryByUserAndClientId(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.parameterSource = parameterSource;
        }

        @Override
        public StringBuilder getQuery(OauthAccessTokenHistory value) {
            if (value.getLogout() != null) {
                builder.append(" and is_logout = :isLogout ");
                parameterSource.addValue("isLogout", value.getLogout());
            }

            if (StringUtils.isNotBlank(value.getIpAddress())) {
                builder.append(" and ip_address like :ipAddress ");
                parameterSource.addValue("ipAddress", new StringBuilder("%").append(value.getIpAddress()).append("%").toString());
            }
            return this.builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return this.parameterSource;
        }
    }

    private class OauthAccessTokenHistoryByUser implements QueryComparator<OauthAccessTokenHistory> {

        private StringBuilder builder;
        private MapSqlParameterSource parameterSource;

        public OauthAccessTokenHistoryByUser(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.parameterSource = parameterSource;
        }

        @Override
        public StringBuilder getQuery(OauthAccessTokenHistory param) {
            if (param.getLogout() != null) {
                builder.append(" and is_logout = :isLogout ");
                parameterSource.addValue("isLogout", param.getLogout());
            }

            if (StringUtils.isNotBlank(param.getClientId())) {
                builder.append(" and client_id like :clintId ");
                parameterSource.addValue("clintId", new StringBuilder("%").append(param.getClientId()).append("%").toString());
            }

            if (StringUtils.isNotBlank(param.getIpAddress())) {
                builder.append(" and ip_address like :ipAddress ");
                parameterSource.addValue("ipAddress", new StringBuilder("%").append(param.getIpAddress()).append("%").toString());
            }
            return this.builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return this.parameterSource;
        }
    }

    private class OauthAccessTokenHistoryByClientId implements QueryComparator<OauthAccessTokenHistory> {

        private StringBuilder builder;
        private MapSqlParameterSource parameterSource;

        public OauthAccessTokenHistoryByClientId(String baseQuery, MapSqlParameterSource parameterSource) {
            this.builder = new StringBuilder(baseQuery);
            this.parameterSource = parameterSource;
        }

        @Override
        public StringBuilder getQuery(OauthAccessTokenHistory param) {
            if (param.getLogout() != null) {
                builder.append(" and is_logout = :isLogout ");
                parameterSource.addValue("isLogout", param.getLogout());
            }

            if (StringUtils.isNotBlank(param.getUsername())) {
                builder.append(" and user_name like :userName ");
                parameterSource.addValue("userName", new StringBuilder("%").append(param.getUsername()).append("%").toString());
            }

            if (StringUtils.isNotBlank(param.getIpAddress())) {
                builder.append(" and ip_address like :ipAddress ");
                parameterSource.addValue("ipAddress", new StringBuilder("%").append(param.getIpAddress()).append("%").toString());
            }
            return this.builder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return this.parameterSource;
        }
    }


    private final class SafeAccessTokenRowMapper implements RowMapper<OAuth2AccessToken> {
        private SafeAccessTokenRowMapper() {
        }

        public OAuth2AccessToken mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                return JdbcTokenStoreCustomPostgreSQL.this.deserializeAccessToken(rs.getBytes(2));
            } catch (IllegalArgumentException var5) {
                String token = rs.getString(1);
                JdbcTokenStoreCustomPostgreSQL.this.jdbcTemplate.update(JdbcTokenStoreCustomPostgreSQL.this.deleteAccessTokenSql, token);
                return null;
            }
        }
    }


}
