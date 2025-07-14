package com.tastes_of_india.restaurantManagement.service.mapper;

import com.tastes_of_india.restaurantManagement.domain.Tables;
import com.tastes_of_india.restaurantManagement.service.dto.TableDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring" ,uses = {RestaurantMapper.class})
public interface TableMapper extends EntityMapper<TableDTO, Tables> {

    @Mapping(source = "restaurant" ,target = "restaurant", qualifiedByName = "id")
    TableDTO toDto(Tables entity);
}
