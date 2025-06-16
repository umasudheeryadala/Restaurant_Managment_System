package com.tastes_of_india.restaurantManagement.service.impl;

import com.tastes_of_india.restaurantManagement.domain.MenuItem;
import com.tastes_of_india.restaurantManagement.domain.Order;
import com.tastes_of_india.restaurantManagement.domain.OrderItem;
import com.tastes_of_india.restaurantManagement.domain.Restaurant;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderItemStatus;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderStatus;
import com.tastes_of_india.restaurantManagement.repository.MenuItemRepository;
import com.tastes_of_india.restaurantManagement.repository.OrderItemRepository;
import com.tastes_of_india.restaurantManagement.repository.OrderRepository;
import com.tastes_of_india.restaurantManagement.repository.RestaurantRepository;
import com.tastes_of_india.restaurantManagement.service.CartService;
import com.tastes_of_india.restaurantManagement.service.OrderItemService;
import com.tastes_of_india.restaurantManagement.service.dto.CartItemDTO;
import com.tastes_of_india.restaurantManagement.service.dto.OrderItemDTO;
import com.tastes_of_india.restaurantManagement.service.mapper.OrderItemMapper;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderItemServiceImpl implements OrderItemService {

    private final Logger LOG= LoggerFactory.getLogger(OrderItemService.class);

    private final String ENTITY_NAME="orderItemServiceImpl";

    private final OrderItemRepository orderItemRepository;

    private final OrderRepository orderRepository;

    private final MenuItemRepository menuItemRepository;

    private final OrderItemMapper orderItemMapper;

    private final CartService cartService;

    private final RestaurantRepository restaurantRepository;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, OrderRepository orderRepository, MenuItemRepository menuItemRepository, OrderItemMapper orderItemMapper, CartService cartService, RestaurantRepository restaurantRepository) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderItemMapper = orderItemMapper;
        this.cartService = cartService;
        this.restaurantRepository = restaurantRepository;
    }


    @Override
    public List<OrderItemDTO> saveAllOrderItems(Long restaurantId,Long tableId,Long orderId) throws BadRequestAlertException {
         Order order=orderRepository.findByIdAndTableIdAndStatusNotIn(orderId,tableId, Arrays.asList(OrderStatus.CANCELLED,OrderStatus.DELIVERED)).orElseThrow(
                 () -> new BadRequestAlertException("Order Not Found",ENTITY_NAME,"orderNotFound")
         );
         List<CartItemDTO> cartItems=null;
         try {
             cartItems=cartService.findAllCartItems(restaurantId,tableId);
         }catch (Exception e){
             throw new BadRequestAlertException(e.getMessage(),ENTITY_NAME,"exception");
         }
         if(cartItems==null){
             throw new BadRequestAlertException("Empty Cart Can't be processed",ENTITY_NAME,"emptyCart");
         }
         Map<Long,CartItemDTO> itemIds=cartItems.stream().collect(Collectors.toMap(CartItemDTO::getId, cartItemDTO -> cartItemDTO));
         List<MenuItem> menuItems=menuItemRepository.findAllByIdInAndCategoryRestaurantIdAndDisabled(itemIds.keySet(),restaurantId,false);
         if(!Objects.equals(menuItems.size(),itemIds.size())){
             for(MenuItem menuItem:menuItems){
                 itemIds.remove(menuItem.getId());
             }
             throw new BadRequestAlertException(itemIds.toString(),ENTITY_NAME,"CUSTOM_ERROR");
         }else{
             List<OrderItem> orderItems=new ArrayList<>();
             for(MenuItem menuItem:menuItems){
                 OrderItem orderItem=new OrderItem();
                 orderItem.setItem(menuItem);
                 orderItem.setQuantity(itemIds.get(menuItem.getId()).getQuantity());
                 orderItem.setOrder(order);
                 orderItem.setInstructions(itemIds.get(menuItem.getId()).getInstructions());
                 orderItem.setStatus(OrderItemStatus.ORDERED);
                 orderItem.setCreatedDate(ZonedDateTime.now());
                 orderItems.add(orderItem);
             }
             return orderItemRepository.saveAllAndFlush(orderItems).stream().map(orderItemMapper::toDto).toList();
         }
    }

    @Override
    public OrderItemDTO cancelOrderItem(Long orderId, Long itemId) throws BadRequestAlertException {
        OrderItem orderItem=orderItemRepository.findByIdAndOrderIdAndStatus(itemId,orderId,OrderItemStatus.ORDERED).orElseThrow(
                () -> new BadRequestAlertException("Order Item Can't be Cancelled",ENTITY_NAME,"orderItemCannotBeCancelled")
        );

        if(ZonedDateTime.now().isAfter(orderItem.getCreatedDate().plusMinutes(10))){
            throw new BadRequestAlertException("Cancellation Time Exceeded",ENTITY_NAME,"timeExceeded");
        }
        orderItem.setStatus(OrderItemStatus.CANCELLED);
        orderItemRepository.saveAndFlush(orderItem);
        return orderItemMapper.toDto(orderItem);
    }

    @Override
    public void cancelOrderItems(List<OrderItem> orderItems) throws BadRequestAlertException {
        for(OrderItem orderItem:orderItems){
            if(orderItem.getStatus()!=OrderItemStatus.ORDERED){
                throw new BadRequestAlertException("Order Item In Process Cannot Able to Cancel",ENTITY_NAME,"orderCannotBeCanceled");
            }
        }
        orderItems.stream().forEach(orderItem -> orderItem.setStatus(OrderItemStatus.CANCELLED));
        orderItemRepository.saveAllAndFlush(orderItems);
    }

    @Override
    public OrderItemDTO updateOrderItemStatus(Long orderItemId, OrderItemStatus orderItemStatus) throws BadRequestAlertException {

        OrderItem orderItem=orderItemRepository.findByIdAndStatusNotIn(orderItemId, Arrays.asList(OrderItemStatus.DELIVERED,OrderItemStatus.CANCELLED)).orElseThrow(
                () -> new BadRequestAlertException("Order May be Delivered Or Cancelled",ENTITY_NAME,"orderNotFound")
        );

        Order order=orderItem.getOrder();
        if(order.getStatus()==OrderStatus.CANCELLED){
            throw new BadRequestAlertException("Order Cancelled",ENTITY_NAME,"orderCancelled");
        }
        if(order.getStatus()==OrderStatus.DELIVERED){
            throw new BadRequestAlertException("Order Delivered",ENTITY_NAME,"orderDelivered");
        }
        order.setStatus(OrderStatus.IN_PROGRESS);
        orderRepository.saveAndFlush(order);
        orderItem.setStatus(orderItemStatus);
        return orderItemMapper.toDto(orderItemRepository.saveAndFlush(orderItem));
    }

    @Override
    public List<OrderItemDTO> findAllOrderItems(Long restaurantId, Long orderId) throws BadRequestAlertException {

        Restaurant restaurant=restaurantRepository.findByIdAndDeleted(restaurantId,false).orElseThrow(
                () -> new BadRequestAlertException("Restaurant Not Found",ENTITY_NAME,"restaurantNotFound")
        );

        List<OrderItem> orderItems=orderItemRepository.findAllByOrderIdAndOrderTableRestaurantId(orderId,restaurantId);

        return orderItems.stream().map(orderItemMapper::toDto).toList();
    }
}
