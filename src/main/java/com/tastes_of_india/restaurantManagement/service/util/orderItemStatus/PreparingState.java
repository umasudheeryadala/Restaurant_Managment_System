package com.tastes_of_india.restaurantManagement.service.util.orderItemStatus;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderItemStatus;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;

public class PreparingState implements OrderItemState {
    @Override
    public void next(OrderItemContext context) {
        context.setState(new DeliveredState());
    }

    @Override
    public void cancel(OrderItemContext context) throws BadRequestAlertException {
        throw new BadRequestAlertException("Order is preparing cannot possible to cancel","OrderPreparingState","OrderCannotBeCanceeled");
    }

    @Override
    public OrderItemStatus getStateName() {
        return OrderItemStatus.PREPARING;
    }
}
