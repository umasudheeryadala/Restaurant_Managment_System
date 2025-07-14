package com.tastes_of_india.restaurantManagement.service.util;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderItemStatus;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderStatus;
import com.tastes_of_india.restaurantManagement.service.util.orderItemStatus.CancelledState;
import com.tastes_of_india.restaurantManagement.service.util.orderItemStatus.DeliveredState;
import com.tastes_of_india.restaurantManagement.service.util.orderItemStatus.PreparingState;
import com.tastes_of_india.restaurantManagement.service.util.orderStatus.OrderCancelledState;
import com.tastes_of_india.restaurantManagement.service.util.orderStatus.OrderDeliveredState;
import com.tastes_of_india.restaurantManagement.service.util.orderStatus.OrderPlacedState;
import com.tastes_of_india.restaurantManagement.service.util.orderStatus.Order_IN_ProgressState;

public class OrderStatusFactory {

    private static final OrderStatusFactory instance=new OrderStatusFactory();
    OrderContext orderContext;

    private OrderStatusFactory(){
        this.orderContext=new OrderContext();
    }

    public static OrderStatusFactory getInstance(){
        return instance;
    }

    public OrderContext getOrderState(OrderStatus orderItemStatus){
        switch (orderItemStatus){
            case ORDERED -> {
                orderContext.setState(new OrderPlacedState());
            }
            case IN_PROGRESS -> {
                orderContext.setState(new Order_IN_ProgressState());
            }
            case DELIVERED -> {
                orderContext.setState(new OrderDeliveredState());
            }
            case CANCELLED -> {
                orderContext.setState(new OrderCancelledState());
            }
            default -> {
                return null;
            }
        }
        return  orderContext;
    }
}
