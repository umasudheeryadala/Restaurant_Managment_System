package com.tastes_of_india.restaurantManagement.service.mapper;

import com.tastes_of_india.restaurantManagement.domain.MenuItem;
import com.tastes_of_india.restaurantManagement.domain.Restaurant;
import com.tastes_of_india.restaurantManagement.service.dto.MenuItemDTO;
import com.tastes_of_india.restaurantManagement.service.dto.RestaurantDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {})
public interface RestaurantMapper extends EntityMapper<RestaurantDTO, Restaurant> {

    @Named("id")
    @Mapping(target = "id",source = "id")
    @BeanMapping(ignoreByDefault = true)
    RestaurantDTO toDtoId(Restaurant restaurant);
}
