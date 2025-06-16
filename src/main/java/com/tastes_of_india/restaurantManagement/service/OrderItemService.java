package com.tastes_of_india.restaurantManagement.service;

import com.tastes_of_india.restaurantManagement.domain.OrderItem;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderItemStatus;
import com.tastes_of_india.restaurantManagement.service.dto.OrderItemDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;

import java.util.List;

public interface OrderItemService {

    List<OrderItemDTO> saveAllOrderItems(Long restaurantId,Long tableId,Long orderId) throws BadRequestAlertException;

    OrderItemDTO cancelOrderItem(Long orderId, Long itemId) throws BadRequestAlertException;

    void cancelOrderItems(List<OrderItem> orderItems) throws BadRequestAlertException;

    OrderItemDTO updateOrderItemStatus(Long orderItemId, OrderItemStatus orderItemStatus) throws BadRequestAlertException;

    List<OrderItemDTO> findAllOrderItems(Long restaurantId,Long orderId) throws BadRequestAlertException;

}
