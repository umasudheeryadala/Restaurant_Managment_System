package com.tastes_of_india.restaurantManagement.web.rest;

import com.tastes_of_india.restaurantManagement.annotation.AuthorizeApiAccess;
import com.tastes_of_india.restaurantManagement.domain.enumeration.Designation;
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
    @GetMapping("/restaurants/{restaurantId}/orders/{orderId}/order-items")
    public ResponseEntity<List<OrderItemDTO>> getAllOrderItems(@PathVariable Long restaurantId,@PathVariable Long orderId) throws BadRequestAlertException {
        List<OrderItemDTO> orderItems=orderItemService.findAllOrderItems(restaurantId, orderId);

        return ResponseEntity.ok(orderItems);

    }

    @GetMapping("/restaurants/{restaurantId}/order-items")
    public ResponseEntity<List<OrderItemDTO>> getAllOrderItemsByRestaurantId(@PathVariable Long restaurantId){
        List<OrderItemDTO> orderItemDTOS=orderItemService.findAllOrderItemsByRestaurantId(restaurantId);
        return ResponseEntity.ok(orderItemDTOS);
    }

    @AuthorizeApiAccess(designation = {Designation.WAITER,Designation.COOK,Designation.OWNER})
    @PutMapping("/restaurants/{restaurantId}/order_items/{itemId}/update_status")
    public ResponseEntity<OrderItemDTO> updateOrderItemStatus(@PathVariable Long restaurantId,@PathVariable Long itemId) throws BadRequestAlertException {
        return ResponseEntity.ok(orderItemService.updateOrderItemStatus(itemId));
    }

    @AuthorizeApiAccess(designation = {Designation.MANAGER,Designation.CUSTOMER_SERVICE_MANAGER,Designation.OWNER})
    @PutMapping("/restaurants/{restaurantId}/order_items/{itemId}/cancelItem")
    public ResponseEntity<OrderItemDTO> cancelOrderItem(@PathVariable Long restaurantId,@PathVariable Long itemId) throws BadRequestAlertException {

        return ResponseEntity.ok(orderItemService.cancelOrderItem(itemId));

    }

}
