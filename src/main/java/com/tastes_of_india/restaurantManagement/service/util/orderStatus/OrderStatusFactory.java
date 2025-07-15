package com.tastes_of_india.restaurantManagement.service.util.orderStatus;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderStatus;

public class OrderStatusFactory {

    OrderContext orderContext;

    private OrderStatusFactory(){
        this.orderContext=new OrderContext();
    }

    public static OrderStatusFactory getInstance(){
        return new OrderStatusFactory();
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
