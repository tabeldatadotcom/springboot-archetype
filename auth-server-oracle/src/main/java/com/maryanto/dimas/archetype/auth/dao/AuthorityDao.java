package com.maryanto.dimas.archetype.auth.dao;

import com.maryanto.dimas.archetype.auth.dto.Authority;

import java.util.List;

public interface AuthorityDao {

    List<Authority> distinctRolesByUsername(String username);
}
