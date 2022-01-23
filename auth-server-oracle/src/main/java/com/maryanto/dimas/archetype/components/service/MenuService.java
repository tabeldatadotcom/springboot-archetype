package com.maryanto.dimas.archetype.components.service;

import com.maryanto.dimas.archetype.auth.dao.AuthorityDao;
import com.maryanto.dimas.archetype.auth.dto.Authority;
import com.maryanto.dimas.archetype.components.dao.MenuDao;
import com.maryanto.dimas.archetype.components.dto.MenuDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class MenuService {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private AuthorityDao authorityDao;

    public List<MenuDto> findAllByModuleId(String username, String moduleId) {
        List<Authority> authorities = authorityDao.distinctRolesByUsername(username);
        return menuDao.getMenuByRolesAndModule(moduleId, authorities);
    }
}
