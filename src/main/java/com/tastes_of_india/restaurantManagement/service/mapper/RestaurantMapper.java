package com.tastes_of_india.restaurantManagement.service.mapper;

import com.tastes_of_india.restaurantManagement.domain.Restaurant;
import com.tastes_of_india.restaurantManagement.service.dto.RestaurantDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {})
public interface RestaurantMapper extends EntityMapper<RestaurantDTO, Restaurant> {
}
