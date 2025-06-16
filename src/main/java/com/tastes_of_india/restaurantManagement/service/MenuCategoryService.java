package com.tastes_of_india.restaurantManagement.service;

import com.tastes_of_india.restaurantManagement.service.dto.MenuCategoryDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MenuCategoryService {

    MenuCategoryDTO saveMenuCategory(Long restaurantId,MenuCategoryDTO menuCategoryDTO) throws  BadRequestAlertException;

    Page findAllMenuCategories(Pageable pageable,String pattern);

    Page<MenuCategoryDTO> findAllMenuCategories(Long restaurantId,Boolean disabled,Pageable pageable) throws BadRequestAlertException;

}
