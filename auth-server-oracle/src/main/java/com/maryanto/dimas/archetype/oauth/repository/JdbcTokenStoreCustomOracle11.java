package com.maryanto.dimas.archetype.oauth.repository;

import com.maryanto.dimas.archetype.oauth.models.OauthAccessTokenExtended;
import com.maryanto.dimas.archetype.oauth.models.OauthAccessTokenHistory;
import com.maryanto.dimas.archetype.utils.PageableLimitOffset;
import com.maryanto.dimas.archetype.utils.QueryComparator;
import com.maryanto.dimas.archetype.utils.impl.oracle.Oracle11LimitOffset;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.DataTablesRequest;
import com.maryanto.dimas.plugins.web.commons.ui.datatables.OrderingByColumns;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import javax.validation.constraints.NotNull;
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
public class JdbcTokenStoreCustomOracle11 extends JdbcTokenStore implements JdbcTokenStoreCustom {

    private final static Logger console = LoggerFactory.getLogger(JdbcTokenStoreCustomOracle11.class);
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    //language=OracleSqlPlus
    private String insertAccessTokenSql = "insert into oauth_access_token (token_id, token, auth_id, USERNAME, client_id, authentication, refresh_token, ip_address)\n" +
            "values (?, ?, ?, ?, ?, ?, ?, ?)";
    //language=OracleSqlPlus
    private String selectAccessTokenSql = "select token_id, token from oauth_access_token where token_id = ?";
    //language=OracleSqlPlus
    private String selectAccessTokenAuthenticationSql = "select token_id, authentication\n" +
            "from oauth_access_token\n" +
            "where token_id = ?";
    //language=OracleSqlPlus
    private String selectAccessTokenFromAuthenticationSql = "select token_id, token\n" +
            "from oauth_access_token\n" +
            "where auth_id = ?";
    //language=OracleSqlPlus
    private String selectAccessTokensFromUserNameAndClientIdSql = "select token_id, token from oauth_access_token where USERNAME = ? and client_id = ?";
    //language=OracleSqlPlus
    private String selectAccessTokensFromUserNameSql = "select token_id, token from oauth_access_token where USERNAME = ?";
    //language=OracleSqlPlus
    private String selectAccessTokensFromClientIdSql = "select token_id, token from oauth_access_token where client_id = ?";
    //language=OracleSqlPlus
    private String deleteAccessTokenSql = "delete from oauth_access_token where token_id = ?";
    //language=OracleSqlPlus
    private String insertRefreshTokenSql = "insert into oauth_refresh_token (token_id, token, authentication)\n" +
            "values (?, ?, ?)";
    //language=OracleSqlPlus
    private String selectRefreshTokenSql = "select token_id, token\n" +
            "from oauth_refresh_token\n" +
            "where token_id = ?";
    //language=OracleSqlPlus
    private String selectRefreshTokenAuthenticationSql = "select token_id, authentication\n" +
            "from oauth_refresh_token\n" +
            "where token_id = ?";
    //language=OracleSqlPlus
    private String deleteRefreshTokenSql = "delete\n" +
            "from oauth_refresh_token\n" +
            "where token_id = ?";
    //language=OracleSqlPlus
    private String deleteAccessTokenFromRefreshTokenSql = "delete\n" +
            "from oauth_access_token\n" +
            "where refresh_token = ?";
    //language=OracleSqlPlus
    private String insertHistoryAccessTokenSql = "insert into oauth_history_access_token (id, access_id, client_id, token, ip_address, user_name, login_at, is_logout,\n" +
            "                                        logout_at, logout_by)\n" +
            "VALUES (sys_guid(), ?, ?, ?, ?, ?, current_timestamp, 0, null, null)";
    //language=OracleSqlPlus
    private String updateHistoryAccessTokenSql = "update oauth_history_access_token\n" +
            "set is_logout = 0,\n" +
            "    logout_at = current_timestamp,\n" +
            "    logout_by = ?\n" +
            "where access_id = ?\n" +
            "  and is_logout = 0";
    private AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    public JdbcTokenStoreCustomOracle11(DataSource dataSource) {
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
                    (rs, rowNum) -> JdbcTokenStoreCustomOracle11.this.deserializeAccessToken(rs.getBytes(2)),
                    key);
        } catch (EmptyResultDataAccessException var5) {
            if (console.isDebugEnabled()) {
                console.debug("Failed to find access token for authentication " + authentication);
            }
        } catch (IllegalArgumentException var6) {
            console.error("Could not extract access token for authentication " + authentication, var6);
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
                },
                new int[]{Types.VARCHAR, Types.BLOB, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.BLOB, Types.VARCHAR, Types.VARCHAR});

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
                    (rs, rowNum) -> JdbcTokenStoreCustomOracle11.this.deserializeAccessToken(rs.getBytes(2)),
                    this.extractTokenKey(tokenValue));
        } catch (EmptyResultDataAccessException var4) {
            if (console.isInfoEnabled()) {
                console.info("Failed to find access token for token " + tokenValue);
            }
        } catch (IllegalArgumentException var5) {
            console.warn("Failed to deserialize access token for " + tokenValue, var5);
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
                    (rs, rowNum) -> JdbcTokenStoreCustomOracle11.this.deserializeAuthentication(rs.getBytes(2)),
                    this.extractTokenKey(token));
        } catch (EmptyResultDataAccessException var4) {
            if (console.isInfoEnabled()) {
                console.info("Failed to find access token for token " + token);
            }
        } catch (IllegalArgumentException var5) {
            console.warn("Failed to deserialize authentication for " + token, var5);
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
                    (rs, rowNum) -> JdbcTokenStoreCustomOracle11.this.deserializeRefreshToken(rs.getBytes(2)),
                    this.extractTokenKey(token));
        } catch (EmptyResultDataAccessException var4) {
            if (console.isInfoEnabled()) {
                console.info("Failed to find refresh token for token " + token);
            }
        } catch (IllegalArgumentException var5) {
            console.warn("Failed to deserialize refresh token for token " + token, var5);
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
                    (rs, rowNum) -> JdbcTokenStoreCustomOracle11.this.deserializeAuthentication(rs.getBytes(2)),
                    this.extractTokenKey(value));
        } catch (EmptyResultDataAccessException var4) {
            if (console.isInfoEnabled()) {
                console.info("Failed to find access token for token " + value);
            }
        } catch (IllegalArgumentException var5) {
            console.warn("Failed to deserialize access token for " + value, var5);
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
                    new JdbcTokenStoreCustomOracle11.SafeAccessTokenRowMapper(),
                    clientId);
        } catch (EmptyResultDataAccessException var4) {
            if (console.isInfoEnabled()) {
                console.info("Failed to find access token for clientId " + clientId);
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
                    new JdbcTokenStoreCustomOracle11.SafeAccessTokenRowMapper(),
                    userName);
        } catch (EmptyResultDataAccessException var4) {
            if (console.isInfoEnabled()) {
                console.info("Failed to find access token for userName " + userName);
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
                    new JdbcTokenStoreCustomOracle11.SafeAccessTokenRowMapper(),
                    userName, clientId);
        } catch (EmptyResultDataAccessException var5) {
            if (console.isInfoEnabled()) {
                console.info("Failed to find access token for clientId " + clientId + " and userName " + userName);
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
        //language=Oracle
        String baseQuery = "select ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                "             AUTH_ID                             as authentication_id,\n" +
                "             TOKEN_ID                            as token_id,\n" +
                "             TOKEN                               as access_token,\n" +
                "             USERNAME                            as username,\n" +
                "             CLIENT_ID                           as client_id,\n" +
                "             IP_ADDRESS                          as ip_address,\n" +
                "             LOGIN_AT                            as login_time\n" +
                "      from OAUTH_ACCESS_TOKEN oauth\n" +
                "      where 1 = 1";

        MapSqlParameterSource map = new MapSqlParameterSource();
        OauthAccessTokenExtendedQueryComparator queryComparator = new OauthAccessTokenExtendedQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuery(params.getValue());
        map = queryComparator.getParameters();

        OrderingByColumns columns = new OrderingByColumns("username", "client_id", "ip_address", "login_time");
        stringBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(stringBuilder.toString(), "no");

        if (log.isDebugEnabled())
            log.debug("query : {}", finalQuery);

        List<OauthAccessTokenExtended> list = this.namedJdbcTemplate.query(finalQuery, map, (resultSet, i) -> {
            try {
                OAuth2AccessToken oauth2AccessToken = JdbcTokenStoreCustomOracle11.this.deserializeAccessToken(resultSet.getBytes("access_token"));
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
                JdbcTokenStoreCustomOracle11.this.jdbcTemplate.update(JdbcTokenStoreCustomOracle11.this.deleteAccessTokenSql, token);
                return null;
            }
        });
        return list;
    }

    @Override
    public Long datatables(OauthAccessTokenExtended value) {
        //language=Oracle
        String baseQuery = "select count(*) as value_row from oauth_access_token where 1=1";
        MapSqlParameterSource map = new MapSqlParameterSource();
        OauthAccessTokenExtendedQueryComparator queryComparator = new OauthAccessTokenExtendedQueryComparator(baseQuery, map);
        StringBuilder stringBuilder = queryComparator.getQuery(value);
        map = queryComparator.getParameters();

        Long row = this.namedJdbcTemplate.queryForObject(stringBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("value_row"));
        return row;
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
            @NotNull String username,
            @NotNull String clientId,
            DataTablesRequest<OauthAccessTokenHistory> params) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        //language=OracleSqlPlus
        String baseQuery = "select ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                "       access_id,\n" +
                "       token,\n" +
                "       client_id,\n" +
                "       ip_address,\n" +
                "       user_name,\n" +
                "       login_at,\n" +
                "       is_logout,\n" +
                "       logout_at,\n" +
                "       logout_by\n" +
                "from oauth_history_access_token\n" +
                "where 1 = 1\n" +
                "  and client_id = :clientId\n" +
                "  and user_name = :userName ";
        map.addValue("clientId", clientId);
        map.addValue("userName", username);
        OauthAccessTokenHistoryQueryCompare queryCompare = new OauthAccessTokenHistoryQueryCompare(baseQuery, map);
        StringBuilder queryBuilder = queryCompare.getQuery(params.getValue());
        map = queryCompare.getParameters();

        OrderingByColumns columns = new OrderingByColumns("user_name", "client_id", "ip_address", "login_at", "is_logout", "logout_at", "logout_by");
        queryBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(queryBuilder.toString(), "no");

        if (log.isDebugEnabled())
            log.debug("query : {}", finalQuery);

        List<OauthAccessTokenHistory> list = this.namedJdbcTemplate.query(finalQuery, map, (resultSet, i) -> {
            try {
                OAuth2AccessToken oauth2AccessToken = JdbcTokenStoreCustomOracle11.this.deserializeAccessToken(resultSet.getBytes("token"));
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
                JdbcTokenStoreCustomOracle11.this.jdbcTemplate.update(JdbcTokenStoreCustomOracle11.this.deleteAccessTokenSql, token);
                return null;
            }
        });
        return list;

    }

    public Long historyByUsernameAndClientIdDatatables(String username, String clientId, OauthAccessTokenHistory param) {
        //language=OracleSqlPlus
        String baseQuery = "select count(*) as rows_value\n" +
                "from oauth_history_access_token\n" +
                "where 1 = 1 \n" +
                "  and client_id = :clientId\n " +
                "  and user_name = :userName ";
        MapSqlParameterSource map = new MapSqlParameterSource();
        map.addValue("clientId", clientId);
        map.addValue("userName", username);


        OauthAccessTokenHistoryQueryCompare queryCompare = new OauthAccessTokenHistoryQueryCompare(baseQuery, map);
        StringBuilder queryBuilder = queryCompare.getQuery(param);
        map = queryCompare.getParameters();

        Long row = this.namedJdbcTemplate.queryForObject(queryBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("rows_value"));
        return row;
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
        //language=Oracle
        String baseQuery = "select ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                "       access_id,\n" +
                "       token,\n" +
                "       client_id,\n" +
                "       ip_address,\n" +
                "       user_name,\n" +
                "       login_at,\n" +
                "       is_logout,\n" +
                "       logout_at,\n" +
                "       logout_by\n" +
                "from oauth_history_access_token\n" +
                "where 1 = 1\n" +
                "  and user_name = :userName ";
        map.addValue("userName", username);

        OauthAccessTokenHistoryQueryComparatorByUsername queryComparator = new OauthAccessTokenHistoryQueryComparatorByUsername(baseQuery, map);
        StringBuilder queryBuilder = queryComparator.getQuery(params.getValue());
        map = queryComparator.getParameters();

        OrderingByColumns columns = new OrderingByColumns("user_name", "client_id", "ip_address", "login_at", "is_logout", "is_logout", "logout_at", "logout_by");
        queryBuilder.append(columns.orderBy(params.getColDir(), params.getColOrder()));

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        map = limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(queryBuilder.toString(), "no");

        if (log.isDebugEnabled())
            log.debug("query : {}", finalQuery);

        List<OauthAccessTokenHistory> list = this.namedJdbcTemplate.query(finalQuery, map, (resultSet, i) -> {
            try {
                OAuth2AccessToken oauth2AccessToken = JdbcTokenStoreCustomOracle11.this.deserializeAccessToken(resultSet.getBytes("token"));
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
                JdbcTokenStoreCustomOracle11.this.jdbcTemplate.update(JdbcTokenStoreCustomOracle11.this.deleteAccessTokenSql, token);
                return null;
            }
        });
        return list;

    }

    public Long historyByUsernameDatatables(String username, OauthAccessTokenHistory param) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        //language=Oracle
        String baseQuery = "select count(*) as rows_value\n" +
                "from oauth_history_access_token\n" +
                "where 1 = 1\n" +
                "  and user_name = :userName ";
        map.addValue("userName", username);
        OauthAccessTokenHistoryQueryComparatorByUsername queryComparator = new OauthAccessTokenHistoryQueryComparatorByUsername(baseQuery, map);
        StringBuilder queryBuilder = queryComparator.getQuery(param);
        map = queryComparator.getParameters();

        Long row = this.namedJdbcTemplate.queryForObject(queryBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("rows_value"));
        return row;
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
        //language=Oracle
        String baseQuery = "select ROW_NUMBER() over (order by ROWNUM) as no,\n" +
                "       access_id,\n" +
                "       token,\n" +
                "       client_id,\n" +
                "       ip_address,\n" +
                "       user_name,\n" +
                "       login_at,\n" +
                "       is_logout,\n" +
                "       logout_at,\n" +
                "       logout_by\n" +
                "from oauth_history_access_token\n" +
                "where 1 = 1\n" +
                "  and client_id = :clientId ";
        map.addValue("clientId", clientId);

        OauthAccessTokenHistoryQueryComparatorByClientId queryComparator = new OauthAccessTokenHistoryQueryComparatorByClientId(baseQuery, map);
        StringBuilder queryBuilder = queryComparator.getQuery(params.getValue());
        map = queryComparator.getParameters();

        OrderingByColumns serviceColumns = new OrderingByColumns("user_name", "client_id", "ip_address", "login_at", "login_at", "is_logout", "is_logout", "logout_at", "logout_by");
        queryBuilder.append(serviceColumns.orderBy(params.getColDir(), params.getColOrder()));

        PageableLimitOffset limitOffset = new Oracle11LimitOffset(map);
        limitOffset.parameter(params.getStart(), params.getLength());
        String finalQuery = limitOffset.query(queryBuilder.toString(), "no");

        if (log.isDebugEnabled())
            log.debug("query : {}", finalQuery);

        List<OauthAccessTokenHistory> list = this.namedJdbcTemplate.query(finalQuery, map, (resultSet, i) -> {
            try {
                OAuth2AccessToken oauth2AccessToken = JdbcTokenStoreCustomOracle11.this.deserializeAccessToken(resultSet.getBytes("token"));
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
                JdbcTokenStoreCustomOracle11.this.jdbcTemplate.update(JdbcTokenStoreCustomOracle11.this.deleteAccessTokenSql, token);
                return null;
            }
        });
        return list;

    }

    public Long historyByClientIdDatatables(String clientId, OauthAccessTokenHistory param) {
        MapSqlParameterSource map = new MapSqlParameterSource();
        //language=OracleSqlPlus
        String baseQuery = "select count(*) as rows_value\n" +
                "from oauth_history_access_token\n" +
                "where 1 = 1 \n" +
                "  and client_id = :clientId ";
        map.addValue("clientId", clientId);

        OauthAccessTokenHistoryQueryComparatorByClientId queryComparator = new OauthAccessTokenHistoryQueryComparatorByClientId(baseQuery, map);
        StringBuilder queryBuilder = queryComparator.getQuery(param);
        map = queryComparator.getParameters();

        Long row = this.namedJdbcTemplate.queryForObject(queryBuilder.toString(), map, (resultSet, i) -> resultSet.getLong("rows_value"));
        return row;
    }

    private class OauthAccessTokenExtendedQueryComparator implements QueryComparator<OauthAccessTokenExtended> {

        private StringBuilder stringBuilder;
        private MapSqlParameterSource parameterSource;

        public OauthAccessTokenExtendedQueryComparator(String baseQuery, MapSqlParameterSource parameterSource) {
            this.stringBuilder = new StringBuilder(baseQuery);
            this.parameterSource = parameterSource;
        }

        @Override
        public StringBuilder getQuery(OauthAccessTokenExtended value) {
            if (StringUtils.isNotBlank(value.getClientId())) {
                stringBuilder.append(" and client_id like :clientId ");
                parameterSource.addValue("clientId", new StringBuilder("%").append(value.getClientId()).append("%").toString());
            }

            if (StringUtils.isNotBlank(value.getUsername())) {
                stringBuilder.append(" and username like :userName ");
                parameterSource.addValue("userName", new StringBuilder("%").append(value.getUsername()).append("%").toString());
            }

            if (StringUtils.isNotBlank(value.getIpAddress())) {
                stringBuilder.append(" and ip_address = :ipAddress ");
                parameterSource.addValue("ipAddress", value.getIpAddress());
            }
            return this.stringBuilder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return this.parameterSource;
        }
    }

    private class OauthAccessTokenHistoryQueryCompare implements QueryComparator<OauthAccessTokenHistory> {

        private StringBuilder queryBuilder;
        private MapSqlParameterSource parameterSource;

        public OauthAccessTokenHistoryQueryCompare(String baseQuery, MapSqlParameterSource parameterSource) {
            this.queryBuilder = new StringBuilder(baseQuery);
            this.parameterSource = parameterSource;
        }

        @Override
        public StringBuilder getQuery(OauthAccessTokenHistory params) {
            if (params.getLogout() != null) {
                queryBuilder.append(" and is_logout = :isLogout ");
                this.parameterSource.addValue("isLogout", params.getLogout());
            }

            if (StringUtils.isNotBlank(params.getIpAddress())) {
                queryBuilder.append(" and ip_address like :ipAddress ");
                this.parameterSource.addValue("ipAddress", new StringBuilder("%").append(params.getIpAddress()).append("%").toString());
            }

            return this.queryBuilder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return this.parameterSource;
        }
    }

    public class OauthAccessTokenHistoryQueryComparatorByUsername implements QueryComparator<OauthAccessTokenHistory> {

        private StringBuilder queryBuilder;
        private MapSqlParameterSource parameterSource;

        public OauthAccessTokenHistoryQueryComparatorByUsername(String baseQuery, MapSqlParameterSource parameterSource) {
            this.queryBuilder = new StringBuilder(baseQuery);
            this.parameterSource = parameterSource;
        }

        @Override
        public StringBuilder getQuery(OauthAccessTokenHistory param) {

            if (param.getLogout() != null) {
                queryBuilder.append(" and is_logout = :isLogout ");
                parameterSource.addValue("isLogout", param.getLogout());
            }

            if (StringUtils.isNotBlank(param.getClientId())) {
                queryBuilder.append(" and client_id like :clintId ");
                parameterSource.addValue("clintId", new StringBuilder("%").append(param.getClientId()).append("%").toString());
            }

            if (StringUtils.isNotBlank(param.getIpAddress())) {
                queryBuilder.append(" and ip_address like :ipAddress ");
                parameterSource.addValue("ipAddress", new StringBuilder("%").append(param.getIpAddress()).append("%").toString());
            }
            return this.queryBuilder;
        }

        @Override
        public MapSqlParameterSource getParameters() {
            return this.parameterSource;
        }
    }

    public class OauthAccessTokenHistoryQueryComparatorByClientId implements QueryComparator<OauthAccessTokenHistory> {

        private StringBuilder queryBuilder;
        private MapSqlParameterSource parameterSource;

        public OauthAccessTokenHistoryQueryComparatorByClientId(String baseQuery, MapSqlParameterSource parameterSource) {
            this.queryBuilder = new StringBuilder(baseQuery);
            this.parameterSource = parameterSource;
        }

        @Override
        public StringBuilder getQuery(OauthAccessTokenHistory param) {
            if (param.getLogout() != null) {
                queryBuilder.append(" and is_logout = :isLogout ");
                parameterSource.addValue("isLogout", param.getLogout());
            }

            if (StringUtils.isNotBlank(param.getUsername())) {
                queryBuilder.append(" and user_name like :userName ");
                parameterSource.addValue("userName", new StringBuilder("%").append(param.getUsername()).append("%").toString());
            }

            if (StringUtils.isNotBlank(param.getIpAddress())) {
                queryBuilder.append(" and ip_address like :ipAddress ");
                parameterSource.addValue("ipAddress", new StringBuilder("%").append(param.getIpAddress()).append("%").toString());
            }
            return this.queryBuilder;
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
                return JdbcTokenStoreCustomOracle11.this.deserializeAccessToken(rs.getBytes(2));
            } catch (IllegalArgumentException var5) {
                String token = rs.getString(1);
                JdbcTokenStoreCustomOracle11.this.jdbcTemplate.update(JdbcTokenStoreCustomOracle11.this.deleteAccessTokenSql, token);
                return null;
            }
        }
    }
}
