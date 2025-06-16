package com.tastes_of_india.restaurantManagement.web.rest;

import com.tastes_of_india.restaurantManagement.annotation.AuthorizeApiAccess;
import com.tastes_of_india.restaurantManagement.domain.enumeration.Designation;
import com.tastes_of_india.restaurantManagement.service.MenuCategoryService;
import com.tastes_of_india.restaurantManagement.service.dto.MenuCategoryDTO;
import com.tastes_of_india.restaurantManagement.service.util.PaginationUtil;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class RestaurantMenuCategoryResource {

    private final Logger LOG= LoggerFactory.getLogger(RestaurantMenuCategoryResource.class);

    private final String ENTITY_NAME="restaurantMenuCategory";

    @Autowired
    private MenuCategoryService menuCategoryService;


    @GetMapping("/restaurants/{restaurantId}/menu_categories")
    public ResponseEntity<List<MenuCategoryDTO>> getAllMenuCategories(@PathVariable Long restaurantId,@RequestParam(required = false) Boolean disabled, Pageable pageable) throws BadRequestAlertException {
        Page<MenuCategoryDTO> page=menuCategoryService.findAllMenuCategories(restaurantId,disabled,pageable);

        HttpHeaders headers= PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(),page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @AuthorizeApiAccess(designation = {Designation.KITCHEN_MANAGER,Designation.MANAGER})
    @PostMapping("/restaurants/{restaurantId}/menu_categories")
    public ResponseEntity<MenuCategoryDTO> saveMenuCategory(@PathVariable Long restaurantId,@RequestBody MenuCategoryDTO menuCategoryDTO) throws BadRequestAlertException {
        MenuCategoryDTO result=menuCategoryService.saveMenuCategory(restaurantId,menuCategoryDTO);
        return ResponseEntity.ok(result);
    }

    @AuthorizeApiAccess(designation = {Designation.KITCHEN_MANAGER,Designation.MANAGER})
    @PutMapping("/restaurants/{restaurantId}/menu_categories/{categoryId}")
    public ResponseEntity<MenuCategoryDTO> updateMenuCategory(@PathVariable Long restaurantId,@PathVariable Long categoryId,@RequestBody MenuCategoryDTO menuCategoryDTO) throws BadRequestAlertException {

        if(menuCategoryDTO.getId()==null){
            throw new BadRequestAlertException("Id Not Found",ENTITY_NAME,"idNotFound");
        }

        if(!Objects.equals(menuCategoryDTO.getId(),categoryId)){
            throw new BadRequestAlertException("Id Mis Match",ENTITY_NAME,"idMisMatch");
        }
        MenuCategoryDTO result=menuCategoryService.saveMenuCategory(restaurantId,menuCategoryDTO);
        return ResponseEntity.ok(result);
    }

}
