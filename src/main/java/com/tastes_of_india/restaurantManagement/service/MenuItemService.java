package com.tastes_of_india.restaurantManagement.service;

import com.tastes_of_india.restaurantManagement.service.dto.ImageDTO;
import com.tastes_of_india.restaurantManagement.service.dto.MenuItemDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MenuItemService {

    MenuItemDTO saveMenuItem(Long restaurantId,MenuItemDTO menuItem, Long categoryId) throws IOException, BadRequestAlertException;

    Page<MenuItemDTO> getAllMenuItemsByCategoryId(Long restaurantId,Long categoryId, Boolean disabled,Boolean available,Boolean veg, String pattern, Pageable pageable) throws BadRequestException, BadRequestAlertException;

    List<ImageDTO>  uploadImages(Long itemId, List<MultipartFile> multipartFiles) throws IOException, BadRequestAlertException;
}
