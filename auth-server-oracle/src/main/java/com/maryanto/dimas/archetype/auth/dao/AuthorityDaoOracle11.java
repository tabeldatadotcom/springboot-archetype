package com.maryanto.dimas.archetype.auth.dao;

import com.maryanto.dimas.archetype.auth.dto.Authority;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Slf4j
public class AuthorityDaoOracle11 implements AuthorityDao {

    @Override
    public List<Authority> distinctRolesByUsername(String username) {
        return null;
    }
}
