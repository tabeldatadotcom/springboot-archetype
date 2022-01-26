package com.maryanto.dimas.archetype.auth.dao;

import com.maryanto.dimas.archetype.auth.dto.Authority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.io.Serializable;
import java.util.List;

@Repository
@Slf4j
public class AuthorityDaoPostgreSQL implements AuthorityDao, Serializable {

    @Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;

    public List<Authority> distinctRolesByUsername(String username) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("userId", username);
        StringBuilder query = new StringBuilder("select distinct role.id as role_id, role.name as role_name, role.description as role_description\n" +
                "from auth.users u\n" +
                "       join auth.user_privileges granted on u.id = granted.user_id\n" +
                "       join auth.privileges privilege on granted.privilege_id = privilege.id\n" +
                "       join auth.authorities authority on authority.privilege_id = privilege.id\n" +
                "       join auth.roles role on authority.role_id = role.id\n" +
                "where u.username = :userId");
        return this.jdbcTemplate.query(
                query.toString(), params,
                (resultSet, i) -> new Authority(
                        resultSet.getInt("role_id"),
                        resultSet.getString("role_name"),
                        resultSet.getString("role_description"))
        );
    }
}
