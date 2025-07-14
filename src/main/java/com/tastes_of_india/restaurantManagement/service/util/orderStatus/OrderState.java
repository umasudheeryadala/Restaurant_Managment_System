package com.tastes_of_india.restaurantManagement.service.util.orderStatus;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderStatus;
import com.tastes_of_india.restaurantManagement.service.util.OrderContext;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;

public interface OrderState {

    void next(OrderContext orderContext) throws BadRequestAlertException;

    void cancel(OrderContext orderContext) throws BadRequestAlertException;

    void canProcessPayment(OrderContext orderContext) throws BadRequestAlertException;

    OrderStatus getStateName();
}
