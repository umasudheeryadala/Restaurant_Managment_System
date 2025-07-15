package com.tastes_of_india.restaurantManagement.service.util.orderItemStatus;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderItemStatus;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;

public class CancelledState implements OrderItemState{
    @Override
    public void next(OrderItemContext context) throws BadRequestAlertException {
        throw new BadRequestAlertException("Order Successfully Cancelled No Future Updates Can Be Possible","OrderCancelled","OrderCancelled");
    }

    @Override
    public void cancel(OrderItemContext context) throws BadRequestAlertException {
        throw new BadRequestAlertException("Order already Cancelled","OrderCancelled","OrderCancelled");
    }

    @Override
    public OrderItemStatus getStateName() {
        return OrderItemStatus.CANCELLED;
    }
}
