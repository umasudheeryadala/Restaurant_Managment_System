package com.tastes_of_india.restaurantManagement.service.mapper;

import com.tastes_of_india.restaurantManagement.domain.OrderItem;
import com.tastes_of_india.restaurantManagement.service.dto.OrderItemDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring" ,uses = {MenuItemMapper.class})
public interface OrderItemMapper extends EntityMapper<OrderItemDTO, OrderItem> {

    @Mapping(target = "menuItem",source = "item",qualifiedByName = "id")
    OrderItemDTO toDto(OrderItem orderItem);

    @Named("idSet")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "menuItemId",source = "menuItem.id",qualifiedByName = "id")
    List<OrderItemDTO> toDto(List<OrderItem> orderItems);
}
