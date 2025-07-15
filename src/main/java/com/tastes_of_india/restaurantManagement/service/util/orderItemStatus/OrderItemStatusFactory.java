package com.tastes_of_india.restaurantManagement.service.util.orderItemStatus;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderItemStatus;

public class OrderItemStatusFactory {

    OrderItemContext orderItemContext;

    public OrderItemStatusFactory(){
        this.orderItemContext=new OrderItemContext();
    }

    public static OrderItemStatusFactory getInstance(){
        return new OrderItemStatusFactory();
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
