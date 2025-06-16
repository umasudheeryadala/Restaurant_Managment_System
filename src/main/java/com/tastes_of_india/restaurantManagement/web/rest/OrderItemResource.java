package com.tastes_of_india.restaurantManagement.web.rest;

import com.tastes_of_india.restaurantManagement.annotation.AuthorizeApiAccess;
import com.tastes_of_india.restaurantManagement.domain.Order;
import com.tastes_of_india.restaurantManagement.domain.enumeration.Designation;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderItemStatus;
import com.tastes_of_india.restaurantManagement.service.OrderItemService;
import com.tastes_of_india.restaurantManagement.service.dto.OrderItemDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class OrderItemResource {

    private final Logger LOG= LoggerFactory.getLogger(OrderItemResource.class);

    private final String ENTITY_NAME="orderItemResource";

    @Autowired
    private OrderItemService orderItemService;


    @AuthorizeApiAccess(designation = {Designation.All})
    @GetMapping("/restaurants/{restaurantId}/order/{orderId}/order_items")
    public ResponseEntity<List<OrderItemDTO>> getAllOrderItems(@PathVariable Long restaurantId,@PathVariable Long orderId) throws BadRequestAlertException {
        List<OrderItemDTO> orderItems=orderItemService.findAllOrderItems(restaurantId, orderId);

        return ResponseEntity.ok(orderItems);

    }

    @AuthorizeApiAccess(designation = {Designation.WAITER,Designation.COOK})
    @PutMapping("/restaurants/{restaurantId}/order_items/{itemId}/status/{status}")
    public ResponseEntity<OrderItemDTO> updateOrderItemStatus(@PathVariable Long restaurantId,@PathVariable Long itemId, @PathVariable OrderItemStatus status) throws BadRequestAlertException {
        return ResponseEntity.ok(orderItemService.updateOrderItemStatus(itemId,status));
    }

    @AuthorizeApiAccess(designation = {Designation.MANAGER,Designation.CUSTOMER_SERVICE_MANAGER,Designation.WAITER})
    @PutMapping("/restaurants/{restaurantId}/orders/{orderId}/order_items/{itemId}/cancelItem")
    public ResponseEntity<OrderItemDTO> cancelOrderItem(@PathVariable Long restaurantId,@PathVariable Long orderId,@PathVariable Long itemId) throws BadRequestAlertException {

        return ResponseEntity.ok(orderItemService.cancelOrderItem(orderId,itemId));

    }

}
