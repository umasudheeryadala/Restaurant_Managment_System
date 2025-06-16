package com.tastes_of_india.restaurantManagement.service.mapper;

import com.tastes_of_india.restaurantManagement.domain.MenuCategory;
import com.tastes_of_india.restaurantManagement.service.dto.MenuCategoryDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring" ,uses = {})
public interface MenuCategoryMapper extends EntityMapper<MenuCategoryDTO, MenuCategory> {
}
