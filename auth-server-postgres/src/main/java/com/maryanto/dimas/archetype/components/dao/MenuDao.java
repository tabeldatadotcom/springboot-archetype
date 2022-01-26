package com.maryanto.dimas.archetype.components.dao;

import com.maryanto.dimas.archetype.auth.dto.Authority;
import com.maryanto.dimas.archetype.components.dto.MenuDto;

import java.util.List;

public interface MenuDao {

    List<MenuDto> getMenuByRolesAndModule(String moduleId, List<Authority> roles);

}
