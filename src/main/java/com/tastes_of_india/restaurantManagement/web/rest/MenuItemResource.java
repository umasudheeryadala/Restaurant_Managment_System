package com.tastes_of_india.restaurantManagement.web.rest;

import com.tastes_of_india.restaurantManagement.annotation.AuthorizeApiAccess;
import com.tastes_of_india.restaurantManagement.domain.enumeration.Designation;
import com.tastes_of_india.restaurantManagement.service.MenuItemService;
import com.tastes_of_india.restaurantManagement.service.dto.ImageDTO;
import com.tastes_of_india.restaurantManagement.service.dto.MenuItemDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class MenuItemResource {

    private final Logger LOG= LoggerFactory.getLogger(MenuItemResource.class);

    private final String ENTITY_NAME="menuItemResource";

    @Autowired
    private MenuItemService menuItemService;

    @GetMapping("/restaurants/{restaurantId}/menu_categories/{categoryId}/menuItems")
    public ResponseEntity<List<MenuItemDTO>> getAllMenuItemsByCategory(@PathVariable Long restaurantId,
                                                                       @PathVariable Long categoryId,
                                                                       @RequestParam(required = false) Boolean disabled,
                                                                       @RequestParam(required = false) String pattern,
                                                                       @RequestParam(required = false) Boolean available,
                                                                       @RequestParam(required = false) Boolean veg,
                                                                       Pageable pageable) throws BadRequestException, BadRequestAlertException {
        Page<MenuItemDTO> menuItems=menuItemService.getAllMenuItemsByCategoryId(restaurantId,categoryId,disabled,available,veg,pattern,pageable);
        return ResponseEntity.ok(menuItems.getContent());
    }

    @AuthorizeApiAccess(designation = {Designation.COOK,Designation.MANAGER,Designation.OWNER})
    @PostMapping(value = "/restaurants/{restaurantId}/menu_categories/{categoryId}/menuItems")
    public ResponseEntity<MenuItemDTO> saveMenuItem(@PathVariable Long restaurantId,@PathVariable Long categoryId, @RequestBody MenuItemDTO menuItemDTO) throws IOException, BadRequestAlertException {
        MenuItemDTO result=menuItemService.saveMenuItem(restaurantId,menuItemDTO,categoryId);
        return ResponseEntity.ok(result);
    }

    @AuthorizeApiAccess(designation = {Designation.COOK,Designation.MANAGER,Designation.OWNER})
    @PutMapping(value = "/restaurants/{restaurantId}/menu_categories/{categoryId}/menuItems/{menuItemId}")
    public ResponseEntity<MenuItemDTO> updateMenuItem(@PathVariable Long restaurantId,@PathVariable Long categoryId,@PathVariable Long menuItemId,@RequestBody MenuItemDTO menuItemDTO) throws BadRequestAlertException, IOException {

        if(menuItemDTO.getId()==null){
            throw new BadRequestAlertException("Menu Item Id Not Found",ENTITY_NAME,"menuItemIdNotFound");
        }

        if(!Objects.equals(menuItemDTO.getId(),menuItemId)){
            throw new BadRequestAlertException("Id Mis Match",ENTITY_NAME,"idMisMatch");
        }

        MenuItemDTO result=menuItemService.saveMenuItem(restaurantId,menuItemDTO,categoryId);
        return ResponseEntity.ok(result);
    }

    @AuthorizeApiAccess(designation = {Designation.COOK,Designation.MANAGER,Designation.OWNER})
    @PostMapping(value = "/restaurants/{restaurantId}/menuItem/{itemId}/uploadImages")
    public ResponseEntity<List<ImageDTO>> uploadMenuItemImages(@PathVariable Long restaurantId, @PathVariable Long itemId, @RequestParam("file") List<MultipartFile> multipartFiles) throws BadRequestAlertException, IOException {
        return ResponseEntity.ok(menuItemService.uploadImages(itemId,multipartFiles));
    }
}
