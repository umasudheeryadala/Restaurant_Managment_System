package com.tastes_of_india.restaurantManagement.service.util.orderStatus;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderStatus;
import com.tastes_of_india.restaurantManagement.service.util.OrderContext;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;

public class Order_IN_ProgressState implements OrderState{
    @Override
    public void next(OrderContext orderContext) {
        orderContext.setState(new OrderDeliveredState());
    }

    @Override
    public void cancel(OrderContext orderContext) throws BadRequestAlertException {
        throw new BadRequestAlertException("Order already started preparing cannot possible to cancel","Order_CANCELLATION","orderCannotPossibleToCancel");
    }

    @Override
    public void canProcessPayment(OrderContext orderContext) {}

    @Override
    public OrderStatus getStateName() {
        return OrderStatus.IN_PROGRESS;
    }
}
