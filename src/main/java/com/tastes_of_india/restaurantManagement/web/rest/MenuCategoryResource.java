package com.tastes_of_india.restaurantManagement.web.rest;

import com.tastes_of_india.restaurantManagement.service.MenuCategoryService;
import com.tastes_of_india.restaurantManagement.service.dto.MenuCategoryDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class MenuCategoryResource {

    @Autowired
    private MenuCategoryService menuCategoryService;

    @GetMapping("menu_categories/{categoryId}")
    public ResponseEntity<MenuCategoryDTO> getMenuCategoryById(@PathVariable Long categoryId) throws BadRequestAlertException {
        MenuCategoryDTO menuCategoryDTO=menuCategoryService.findById(categoryId);
        return  ResponseEntity.ok(menuCategoryDTO);
    }
}
