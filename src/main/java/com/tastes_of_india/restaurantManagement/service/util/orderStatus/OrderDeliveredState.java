package com.tastes_of_india.restaurantManagement.service.util.orderStatus;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderStatus;
import com.tastes_of_india.restaurantManagement.service.util.OrderContext;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;

public class OrderDeliveredState implements OrderState {
    @Override
    public void next(OrderContext orderContext) throws BadRequestAlertException {
        throw new BadRequestAlertException("Order is already delivered no actions can be performed","ORDER_DELIVERED","orderDeliveredNoFutureActionsCanBePerformed");
    }

    @Override
    public void cancel(OrderContext orderContext) throws BadRequestAlertException {
        throw new BadRequestAlertException("Order is already delivered not possible to cancel","ORDER_DELIVERED","orderCannotPossibleToCancel");
    }

    @Override
    public void canProcessPayment(OrderContext orderContext) throws BadRequestAlertException {
        throw new BadRequestAlertException("Order is already delivered","PAYMENT_FAILURE","paymentAlreadyProcessed");
    }

    @Override
    public OrderStatus getStateName() {
        return null;
    }
}
