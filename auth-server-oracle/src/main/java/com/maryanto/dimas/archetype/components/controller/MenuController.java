package com.maryanto.dimas.archetype.components.controller;

import com.maryanto.dimas.archetype.components.dto.MenuDto;
import com.maryanto.dimas.archetype.components.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/component/menu")
public class MenuController {

    @Autowired
    private MenuService service;

    @GetMapping("/{id}/findByModule")
//    @PreAuthorize("permitAll()")
    public List<MenuDto> findAllMenu(Principal principal, @PathVariable("id") String moduleId) {
        return service.findAllByModuleId(principal.getName(), moduleId);
    }
}
