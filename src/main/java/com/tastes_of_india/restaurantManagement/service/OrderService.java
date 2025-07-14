package com.tastes_of_india.restaurantManagement.service;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderStatus;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderType;
import com.tastes_of_india.restaurantManagement.service.dto.OrderDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.ZonedDateTime;

public interface OrderService {

    OrderDTO saveOrder(Long restaurantId,Long tableId,Long orderId,OrderType orderType) throws BadRequestAlertException;

    OrderDTO getOrder(Long tableId,Long orderId) throws BadRequestAlertException;

    void cancelOrder(Long tableId,Long orderId) throws BadRequestAlertException;

    void updateOrderStatus(Long orderId) throws BadRequestAlertException;

    void canProcessedForPayment(Long orderId) throws BadRequestAlertException;

    Page<OrderDTO> getAllOrdersByRestaurantId(Long restaurantId,Long tableId, ZonedDateTime startDateTime, ZonedDateTime endDateTime, OrderType orderType, OrderStatus orderStatus, Pageable pageable) throws BadRequestAlertException;

}
