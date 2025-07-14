package com.tastes_of_india.restaurantManagement.service.util.orderItemStatus;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderItemStatus;
import com.tastes_of_india.restaurantManagement.service.util.OrderItemContext;

public class OrderPlacedState implements OrderItemState {
    @Override
    public void next(OrderItemContext context) {
        context.setState(new PreparingState());
    }

    @Override
    public void cancel(OrderItemContext context) {
        context.setState(new CancelledState());
    }

    @Override
    public OrderItemStatus getStateName() {
        return OrderItemStatus.ORDERED;
    }
}
