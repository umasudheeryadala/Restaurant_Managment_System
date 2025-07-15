package com.tastes_of_india.restaurantManagement.service.impl;

import com.tastes_of_india.restaurantManagement.domain.Order;
import com.tastes_of_india.restaurantManagement.domain.Tables;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderStatus;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderType;
import com.tastes_of_india.restaurantManagement.repository.OrderItemRepository;
import com.tastes_of_india.restaurantManagement.repository.OrderRepository;
import com.tastes_of_india.restaurantManagement.repository.RestaurantRepository;
import com.tastes_of_india.restaurantManagement.repository.TableRepository;
import com.tastes_of_india.restaurantManagement.service.CartService;
import com.tastes_of_india.restaurantManagement.service.OrderItemService;
import com.tastes_of_india.restaurantManagement.service.OrderService;
import com.tastes_of_india.restaurantManagement.service.dto.OrderDTO;
import com.tastes_of_india.restaurantManagement.service.mapper.OrderMapper;
import com.tastes_of_india.restaurantManagement.service.util.orderStatus.OrderContext;
import com.tastes_of_india.restaurantManagement.service.util.orderStatus.OrderStatusFactory;
import com.tastes_of_india.restaurantManagement.web.rest.StreamResource;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Arrays;

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

    private final OrderItemRepository orderItemRepository;

    private final StreamResource streamResource;

    public OrderServiceImpl(OrderRepository orderRepository, TableRepository tableRepository, OrderMapper orderMapper, OrderItemService orderItemService, CartService cartService, RestaurantRepository restaurantRepository, OrderItemRepository orderItemRepository, StreamResource streamResource) {
        this.orderRepository = orderRepository;
        this.tableRepository = tableRepository;
        this.orderMapper = orderMapper;
        this.orderItemService = orderItemService;
        this.cartService = cartService;
        this.restaurantRepository = restaurantRepository;
        this.orderItemRepository = orderItemRepository;
        this.streamResource = streamResource;
    }


    @Override
    public OrderDTO saveOrder(Long restaurantId,Long tableId,Long orderId, OrderType orderType) throws BadRequestAlertException {
        OrderDTO orderDTO=null;
        if(orderId==null){
            orderDTO=createOrder(tableId,orderType);
        }else{
            Order order=orderRepository.findByIdAndTableIdAndStatusNotIn(orderId,tableId,Arrays.asList(OrderStatus.CANCELLED,OrderStatus.DELIVERED)).orElseThrow(
                    () -> new BadRequestAlertException("Order With Id Not Found",ENTITY_NAME,"orderNotFound")
            );
            orderDTO=orderMapper.toDto(order);
        }
        orderItemService.saveAllOrderItems(restaurantId,tableId,orderDTO.getId());
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
        Order order=orderRepository.findByIdAndTableId(orderId,tableId).orElseThrow(
                () -> new BadRequestAlertException("Order Cannot be Cancelled",ENTITY_NAME,"orderCannotBeCancelled")
        );
        OrderContext orderStatus=OrderStatusFactory.getInstance().getOrderState(order.getStatus());
        orderStatus.cancel();
        orderItemService.cancelOrderItems(orderItemRepository.findByOrderId(orderId));
        order.setStatus(orderStatus.getOrderState());
        orderRepository.saveAndFlush(order);
    }

    @Override
    public void updateOrderStatus(Long orderId) throws BadRequestAlertException {
        LOG.debug("updating order status: {}",orderId);
        Order order=orderRepository.findById(orderId).orElseThrow(
                ()-> new BadRequestAlertException("Order Not Found",ENTITY_NAME,"orderNotFound")
        );
        OrderContext orderContext= OrderStatusFactory.getInstance().getOrderState(order.getStatus());
        orderContext.next();
        order.setStatus(orderContext.getOrderState());
        orderRepository.save(order);
    }

    @Override
    public void canProcessedForPayment(Long orderId) throws BadRequestAlertException {
        Order order=orderRepository.findById(orderId).orElseThrow(
                () -> new BadRequestAlertException("Order Not Found",ENTITY_NAME,"orderNotFound")
        );

        OrderContext orderContext=OrderStatusFactory.getInstance().getOrderState(order.getStatus());

        LOG.debug("canProcessedForPayment {}",orderContext.getOrderState());

        orderContext.processPayment();
    }

    @Override
    public Page<OrderDTO> getAllOrdersByRestaurantId(Long restaurantId,Long tableId, ZonedDateTime startDateTime, ZonedDateTime endDateTime, OrderType orderType, OrderStatus orderStatus, Pageable pageable) throws BadRequestAlertException {

        restaurantRepository.findByIdAndDeleted(restaurantId,false).orElseThrow(
                () -> new BadRequestAlertException("Restaurant Not Found",ENTITY_NAME,"restaurantNotFound")
        );

        tableRepository.findById(tableId).orElseThrow(
                () -> new BadRequestAlertException("Table Not Found",ENTITY_NAME,"tableNotFound")
        );

        Page<Order> orders=orderRepository.findAllByRestaurantIdAndTableId(restaurantId,tableId,orderType,orderStatus,pageable);

        return orders.map(orderMapper::toDto);
    }

    private OrderDTO createOrder(Long tableId,OrderType orderType) throws BadRequestAlertException {
        Order order = new Order();
        if(orderType.equals(OrderType.DINE_IN)) {
            Tables table = tableRepository.findByIdAndDeleted(tableId, false).orElseThrow(
                    () -> new BadRequestAlertException("Table Not Found", ENTITY_NAME, "tableNotFound")
            );
            order.setTable(table);
        }
        order.setOrderType(orderType);
        order.setCreatedDate(ZonedDateTime.now());
        order.setStatus(OrderStatus.ORDERED);
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

}
