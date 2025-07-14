package com.tastes_of_india.restaurantManagement.service.util.orderStatus;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderStatus;
import com.tastes_of_india.restaurantManagement.service.util.OrderContext;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;

public class OrderCancelledState implements OrderState{
    @Override
    public void next(OrderContext orderContext) throws BadRequestAlertException {
        throw new BadRequestAlertException("Order is cancelled no actions can be performed","ORDER_CANCELLED","orderCancelledNoFutureActionsCanBePerformed");
    }

    @Override
    public void cancel(OrderContext orderContext) throws BadRequestAlertException {
        throw new BadRequestAlertException("Order is already cancelled","ORDER_CANCELLED","orderAlreadyCancelled");
    }

    @Override
    public void canProcessPayment(OrderContext orderContext) throws BadRequestAlertException {
        throw new BadRequestAlertException("Order is cancelled cannot process for Payment","PAYMENT_FAILURE","paymentCannotBeProcessed");
    }

    @Override
    public OrderStatus getStateName() {
        return null;
    }
}
