package com.tastes_of_india.restaurantManagement.service.util.orderItemStatus;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderItemStatus;
import com.tastes_of_india.restaurantManagement.service.util.OrderItemContext;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;

public interface OrderItemState {
    void next(OrderItemContext context) throws BadRequestAlertException;
    void cancel(OrderItemContext context) throws BadRequestAlertException;
    OrderItemStatus getStateName();
}
