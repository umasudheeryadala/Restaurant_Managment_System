package com.tastes_of_india.restaurantManagement.service.util.orderStatus;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderStatus;
import com.tastes_of_india.restaurantManagement.service.util.OrderContext;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;

public class OrderPlacedState implements OrderState{
    @Override
    public void next(OrderContext orderContext) {
        orderContext.setState(new Order_IN_ProgressState());
    }

    @Override
    public void cancel(OrderContext orderContext) {
        orderContext.setState(new OrderCancelledState());
    }

    @Override
    public void canProcessPayment(OrderContext orderContext) throws BadRequestAlertException {
        throw new BadRequestAlertException("Order is not delivered once delivered try again","PAYMENT_FAILURE","paymentCannotBeProcessed");
    }

    @Override
    public OrderStatus getStateName() {
        return OrderStatus.ORDERED;
    }
}
