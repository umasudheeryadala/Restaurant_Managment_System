package com.tastes_of_india.restaurantManagement.web.rest;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderStatus;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderType;
import com.tastes_of_india.restaurantManagement.service.OrderService;
import com.tastes_of_india.restaurantManagement.service.dto.OrderDTO;
import com.tastes_of_india.restaurantManagement.service.util.PaginationUtil;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class RestaurantOrderResource {

    private final Logger LOG= LoggerFactory.getLogger(RestaurantOrderResource.class);

    private final String ENTITY_NAME="restaurantOrderResource";

    @Autowired
    private OrderService orderService;

    @GetMapping("/restaurants/{restaurantId}/orders")
    public ResponseEntity<List<OrderDTO>> getAllOrdersByRestaurantId(@PathVariable Long restaurantId,
                                                                     @RequestParam(required = false) Long tableId,
                                                                     @RequestParam(required = false) ZonedDateTime startDateTime,
                                                                     @RequestParam(required = false) ZonedDateTime endDateTime,
                                                                     @RequestParam(required = false) OrderType orderType,
                                                                     @RequestParam(required = false) OrderStatus orderStatus,
                                                                     Pageable pageable) throws BadRequestAlertException {
        Page<OrderDTO> page=orderService.getAllOrdersByRestaurantId(restaurantId,tableId,startDateTime,endDateTime,orderType,orderStatus,pageable);

        HttpHeaders headers= PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(),page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/restaurants/{restaurantId}/tables/{tableId}/orders")
    public ResponseEntity<List<OrderDTO>> getAllOrdersByTableId(@PathVariable Long restaurantId,
                                                                 @PathVariable Long tableId,
                                                                 @RequestParam(required = false) ZonedDateTime startDateTime,
                                                                 @RequestParam(required = false) ZonedDateTime endDateTime,
                                                                 @RequestParam(required = false) OrderType orderType,
                                                                 @RequestParam(required = false) OrderStatus orderStatus,
                                                                 Pageable pageable) throws BadRequestAlertException {
        Page<OrderDTO> page=orderService.getAllOrdersByRestaurantId(restaurantId,tableId,startDateTime,endDateTime,orderType,orderStatus,pageable);

        HttpHeaders headers= PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(),page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @PostMapping("/restaurants/{restaurantId}/table/{tableId}/orders/{orderType}")
    public ResponseEntity<OrderDTO> createOrder(@PathVariable Long restaurantId, @PathVariable Long tableId, @PathVariable OrderType orderType) throws BadRequestAlertException {

        OrderDTO orderDTO=orderService.saveOrder(restaurantId,tableId,null,orderType);

        return ResponseEntity.ok(orderDTO);
    }

    @PutMapping("/restaurants/{restaurantId}/table/{tableId}/orders/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long restaurantId,@PathVariable Long tableId,@PathVariable Long orderId) throws BadRequestAlertException {

        orderService.cancelOrder(tableId, orderId);

        return ResponseEntity.ok().build();
    }


}
