package com.tastes_of_india.restaurantManagement.service.util;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderItemStatus;
import com.tastes_of_india.restaurantManagement.service.util.orderItemStatus.CancelledState;
import com.tastes_of_india.restaurantManagement.service.util.orderItemStatus.DeliveredState;
import com.tastes_of_india.restaurantManagement.service.util.orderItemStatus.OrderPlacedState;
import com.tastes_of_india.restaurantManagement.service.util.orderItemStatus.PreparingState;

public class OrderItemStatusFactory {

    private static final OrderItemStatusFactory instance=new OrderItemStatusFactory();
    OrderItemContext orderItemContext;

    private OrderItemStatusFactory(){
        this.orderItemContext=new OrderItemContext();
    }

    public static OrderItemStatusFactory getInstance(){
        return instance;
    }

    public OrderItemContext getOrderState(OrderItemStatus orderItemStatus){
        switch (orderItemStatus){
            case ORDERED -> {
                orderItemContext.setState(new OrderPlacedState());
            }
            case PREPARING -> {
                orderItemContext.setState(new PreparingState());
            }
            case DELIVERED -> {
                orderItemContext.setState(new DeliveredState());
            }
            case CANCELLED -> {
                orderItemContext.setState(new CancelledState());
            }
            default -> {
                return null;
            }
        }
        return  orderItemContext;
    }
}
