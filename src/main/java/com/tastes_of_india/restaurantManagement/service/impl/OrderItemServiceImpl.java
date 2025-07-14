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
import com.tastes_of_india.restaurantManagement.service.util.OrderContext;
import com.tastes_of_india.restaurantManagement.service.util.OrderItemContext;
import com.tastes_of_india.restaurantManagement.service.util.OrderItemStatusFactory;
import com.tastes_of_india.restaurantManagement.service.util.OrderStatusFactory;
import com.tastes_of_india.restaurantManagement.web.rest.StreamResource;
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

    private final StreamResource streamResource;

    public OrderItemServiceImpl(OrderItemRepository orderItemRepository, OrderRepository orderRepository, MenuItemRepository menuItemRepository, OrderItemMapper orderItemMapper, CartService cartService, RestaurantRepository restaurantRepository, StreamResource streamResource) {
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderItemMapper = orderItemMapper;
        this.cartService = cartService;
        this.restaurantRepository = restaurantRepository;
        this.streamResource = streamResource;
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
                 orderItem.setStatus(new OrderItemContext().getStateName());
                 orderItem.setCreatedDate(ZonedDateTime.now());
                 orderItems.add(orderItem);
             }
             List<OrderItemDTO> orderItemDTOS= orderItemRepository.saveAllAndFlush(orderItems).stream().map(orderItemMapper::toDto).toList();
             sendNotification(restaurantId,orderItemDTOS.stream().map(OrderItemDTO::getId).toList());
             return  orderItemDTOS;
         }
    }

    @Override
    public OrderItemDTO cancelOrderItem(Long itemId) throws BadRequestAlertException {
        OrderItem orderItem=orderItemRepository.findById(itemId).orElseThrow(
                () -> new BadRequestAlertException("Order Item Not Found",ENTITY_NAME,"orderItemNotFound")
        );
        OrderItemContext itemState=OrderItemStatusFactory.getInstance().getOrderState(orderItem.getStatus());
        itemState.cancel();
        orderItem.setStatus(itemState.getStateName());
        orderItemRepository.saveAndFlush(orderItem);
        return orderItemMapper.toDto(orderItem);
    }

    @Override
    public void cancelOrderItems(List<OrderItem> orderItems) throws BadRequestAlertException {
        OrderItemStatusFactory statusFactory=OrderItemStatusFactory.getInstance();
        for(OrderItem orderItem:orderItems){
            OrderItemContext orderItemContext=statusFactory.getOrderState(orderItem.getStatus());
            orderItemContext.cancel();
        }
        orderItems.stream().forEach(orderItem -> orderItem.setStatus(OrderItemStatus.CANCELLED));
        orderItemRepository.saveAllAndFlush(orderItems);
    }

    @Override
    public OrderItemDTO updateOrderItemStatus(Long orderItemId) throws BadRequestAlertException {

        OrderItem orderItem=orderItemRepository.findById(orderItemId).orElseThrow(
                () -> new BadRequestAlertException("Order May be Delivered Or Cancelled",ENTITY_NAME,"orderNotFound")
        );

        Order order=orderItem.getOrder();

        OrderContext orderContext= OrderStatusFactory.getInstance().getOrderState(order.getStatus());
        orderContext.canPerformUpdate();
        if(order.getStatus()==OrderStatus.ORDERED){
            orderContext.next();
            order.setStatus(orderContext.getOrderState());
            orderRepository.save(order);
        }
        OrderItemContext itemState=OrderItemStatusFactory.getInstance().getOrderState(orderItem.getStatus());
        itemState.next();
        orderItem.setStatus(itemState.getStateName());
        sendNotification(order.getTable().getId(),orderItemId,itemState.getStateName());
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

    @Override
    public List<OrderItemDTO> findAllOrderItemsByOrderId(Long orderId) {

        List<OrderItem> orderItems=orderItemRepository.findByOrderId(orderId);

        return orderItems.stream().map(orderItemMapper::toDto).toList();
    }

    @Override
    public List<OrderItemDTO> findAllOrderItemsByRestaurantId(Long restaurantId) {
        List<OrderItem> orderItems=orderItemRepository.findByOrderTableRestaurantId(restaurantId);

        return orderItems.stream().map(orderItemMapper::toDto).toList();
    }

    @Override
    public void checkStatusOfOrderItems(Long orderId) throws BadRequestAlertException {
        List<OrderItem> orderItems=orderItemRepository.findByOrderId(orderId);
        for(OrderItem orderItem:orderItems){
            OrderItemContext context=OrderItemStatusFactory.getInstance().getOrderState(orderItem.getStatus());
            if(!context.canProcessPayment()) {
                throw new BadRequestAlertException("Some of the Order Items are Not Delivered", ENTITY_NAME, "itemsNotDelivered");
            }
        }
    }

    private void sendNotification(Long orderId,Long orderItemId,OrderItemStatus orderItemStatus){
        if(orderItemStatus.equals(OrderItemStatus.PREPARING)){
            streamResource.notifyOrderUpdate(orderId,"Item with Id: "+orderItemId+" started preparing");
        } else if (orderItemStatus.equals(OrderItemStatus.CANCELLED)) {
            streamResource.notifyOrderUpdate(orderId,"Item with Id: "+orderItemId+" cancelled");
        }else if(orderItemStatus.equals(OrderItemStatus.DELIVERED)){
            streamResource.notifyOrderUpdate(orderId,"Item with Id: "+orderItemId+" delivered successfully");
        }
    }

    private void sendNotification(Long id, List<Long> orderItemIds){
        for (Long orderItemId:orderItemIds) {
            streamResource.notifyOrderUpdate(id, "Order Received with id : " + orderItemId);
        }
    }
}
