package com.tastes_of_india.restaurantManagement.service.util.orderItemStatus;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderItemStatus;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;

public class DeliveredState implements OrderItemState{
    @Override
    public void next(OrderItemContext context) throws BadRequestAlertException {
        throw new BadRequestAlertException("Order Successfully Delivered No Future Updates Can Be Possible","DeliveredSuccessFully","deliveredSuccessfully");
    }

    @Override
    public void cancel(OrderItemContext context) throws BadRequestAlertException {
        throw new BadRequestAlertException("Order Successfully Delivered Cannot Possible To Cancel","DeliveredSuccessFully","deliveredSuccessfully");
    }

    @Override
    public OrderItemStatus getStateName() {
        return OrderItemStatus.DELIVERED;
    }
}
