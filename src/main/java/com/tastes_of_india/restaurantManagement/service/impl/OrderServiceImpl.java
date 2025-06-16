package com.tastes_of_india.restaurantManagement.service.impl;

import com.tastes_of_india.restaurantManagement.domain.Order;
import com.tastes_of_india.restaurantManagement.domain.OrderItem;
import com.tastes_of_india.restaurantManagement.domain.Restaurant;
import com.tastes_of_india.restaurantManagement.domain.Tables;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderStatus;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderType;
import com.tastes_of_india.restaurantManagement.repository.GenericRedisRepository;
import com.tastes_of_india.restaurantManagement.repository.OrderRepository;
import com.tastes_of_india.restaurantManagement.repository.RestaurantRepository;
import com.tastes_of_india.restaurantManagement.repository.TableRepository;
import com.tastes_of_india.restaurantManagement.service.CartService;
import com.tastes_of_india.restaurantManagement.service.OrderItemService;
import com.tastes_of_india.restaurantManagement.service.OrderService;
import com.tastes_of_india.restaurantManagement.service.dto.OrderDTO;
import com.tastes_of_india.restaurantManagement.service.dto.OrderItemDTO;
import com.tastes_of_india.restaurantManagement.service.mapper.OrderMapper;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderServiceImpl implements OrderService {

    private final Logger LOG= LoggerFactory.getLogger(OrderServiceImpl.class);

    private final String ENTITY_NAME="orderServiceImpl";

    private final OrderRepository orderRepository;

    private final TableRepository tableRepository;

    private final OrderMapper orderMapper;

    private final OrderItemService orderItemService;

    private final CartService cartService;

    private final RestaurantRepository restaurantRepository;

    public OrderServiceImpl(OrderRepository orderRepository, TableRepository tableRepository, OrderMapper orderMapper, OrderItemService orderItemService, CartService cartService, RestaurantRepository restaurantRepository) {
        this.orderRepository = orderRepository;
        this.tableRepository = tableRepository;
        this.orderMapper = orderMapper;
        this.orderItemService = orderItemService;
        this.cartService = cartService;
        this.restaurantRepository = restaurantRepository;
    }


    @Override
    public OrderDTO saveOrder(Long restaurantId,Long tableId,Long orderId, OrderType orderType) throws BadRequestAlertException {
        OrderDTO orderDTO=null;
        if(orderId==null){
            orderDTO=createOrderIfNotExists(tableId,orderType);
        }else{
            Order order=orderRepository.findByIdAndTableIdAndStatusNotIn(orderId,tableId,Arrays.asList(OrderStatus.CANCELLED,OrderStatus.DELIVERED)).orElseThrow(
                    () -> new BadRequestAlertException("Order With Id Not Found",ENTITY_NAME,"orderNotFound")
            );
            orderDTO=orderMapper.toDto(order);
        }
        List<OrderItemDTO> orderItems=orderItemService.saveAllOrderItems(restaurantId,tableId,orderDTO.getId());
        orderDTO.setOrderItems(orderItems);
        cartService.deleteAllCartItems(restaurantId,tableId);
        return orderDTO;
    }

    @Override
    public OrderDTO getOrder(Long tableId,Long orderId) throws BadRequestAlertException {
        Order order=orderRepository.findByIdAndTableId(orderId,tableId).orElseThrow(
                ()-> new BadRequestAlertException("Order Not Found",ENTITY_NAME,"orderNotFound")
        );
        return orderMapper.toDto(order);
    }

    @Override
    public void cancelOrder(Long tableId,Long orderId) throws BadRequestAlertException {
        Order order=orderRepository.findByIdAndTableIdAndStatus(orderId,tableId,OrderStatus.ORDERED).orElseThrow(
                () -> new BadRequestAlertException("Order Cannot be Cancelled",ENTITY_NAME,"orderCannotBeCancelled")
        );
        if(ZonedDateTime.now().isAfter(order.getCreatedDate().plusMinutes(10L))){
            throw new BadRequestAlertException("Cancellation Time Exceeded",ENTITY_NAME,"timeExceeded ");
        }
        orderItemService.cancelOrderItems(order.getOrderItems());
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.saveAndFlush(order);
    }

    @Override
    public void updateOrderStatus(Long orderId, OrderStatus orderStatus) throws BadRequestAlertException {
        Order order=orderRepository.findByIdAndStatus(orderId,OrderStatus.IN_PROGRESS).orElseThrow(
                ()-> new BadRequestAlertException("Order Not Found",ENTITY_NAME,"orderNotFound")
        );
        order.setStatus(orderStatus);
        orderRepository.save(order);
    }

    @Override
    public Page<OrderDTO> getAllOrdersByRestaurantId(Long restaurantId,Long tableId, ZonedDateTime startDateTime, ZonedDateTime endDateTime, OrderType orderType, OrderStatus orderStatus, Pageable pageable) throws BadRequestAlertException {

        restaurantRepository.findByIdAndDeleted(restaurantId,false).orElseThrow(
                () -> new BadRequestAlertException("Restaurant Not Found",ENTITY_NAME,"restaurantNotFound")
        );

        tableRepository.findById(tableId).orElseThrow(
                () -> new BadRequestAlertException("Table Not Found",ENTITY_NAME,"tableNotFound")
        );

        Page<Order> orders=orderRepository.findAllByRestaurantIdAndTableId(restaurantId,tableId,startDateTime,endDateTime,orderType,orderStatus,pageable);

        return orders.map(orderMapper::toDto);
    }

    private OrderDTO createOrderIfNotExists(Long tableId,OrderType orderType) throws BadRequestAlertException {
        Order order = new Order();
        if(orderType.equals(OrderType.DINE_IN)) {
            Tables table = tableRepository.findByIdAndDeleted(tableId, false).orElseThrow(
                    () -> new BadRequestAlertException("Table Not Found", ENTITY_NAME, "tableNotFound")
            );
            Optional<Order> result = orderRepository.findByTableIdAndStatusNotIn(tableId, Arrays.asList(OrderStatus.CANCELLED,OrderStatus.DELIVERED));
            if (result.isEmpty()) {
                order.setTable(table);
            } else {
                return orderMapper.toDto(result.get());
            }
        }
        order.setOrderType(orderType);
        order.setCreatedDate(ZonedDateTime.now());
        order.setStatus(OrderStatus.ORDERED);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

}
