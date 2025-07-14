package com.tastes_of_india.restaurantManagement.service.mapper;

import com.tastes_of_india.restaurantManagement.domain.MenuItem;
import com.tastes_of_india.restaurantManagement.service.dto.MenuItemDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring" , uses = {ImageMapper.class})
public interface MenuItemMapper extends EntityMapper<MenuItemDTO, MenuItem>{

    @Named("idNamePrice")
    @Mapping(target = "id",source = "id")
    @Mapping(target = "name",source = "name")
    @Mapping(target = "price",source = "price")
    @BeanMapping(ignoreByDefault = true)
    MenuItemDTO toDtoId(MenuItem menuItem);

}
