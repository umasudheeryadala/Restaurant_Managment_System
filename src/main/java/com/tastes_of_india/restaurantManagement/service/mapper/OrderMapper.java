package com.tastes_of_india.restaurantManagement.service.mapper;

import com.tastes_of_india.restaurantManagement.domain.Order;
import com.tastes_of_india.restaurantManagement.service.dto.OrderDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring" ,uses = {OrderItemMapper.class})
public interface OrderMapper extends EntityMapper<OrderDTO, Order> {

    @Mapping(target = "orderItems",source = "orderItems",qualifiedByName = "idSet")
    OrderDTO toDto(Order order);

}
