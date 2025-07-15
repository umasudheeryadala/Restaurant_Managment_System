package com.tastes_of_india.restaurantManagement.service.util.orderStatus;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderStatus;
import com.tastes_of_india.restaurantManagement.service.util.orderStatus.OrderState;
import com.tastes_of_india.restaurantManagement.service.util.orderStatus.OrderPlacedState;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;

public class OrderContext {

    private OrderState orderState;

    public OrderContext(){
        this.orderState=new OrderPlacedState();
    }

    public void setState(OrderState orderState){
        this.orderState=orderState;
    }

    public void next() throws BadRequestAlertException {
        orderState.next(this);
    }

    public void cancel() throws BadRequestAlertException {
        orderState.cancel(this);
    }

    public void processPayment() throws BadRequestAlertException {
        orderState.canProcessPayment(this);
    }

    public void canPerformUpdate() throws BadRequestAlertException {
        if(getOrderState()==OrderStatus.CANCELLED || getOrderState()==OrderStatus.DELIVERED){
            throw new BadRequestAlertException("Update cannot possible","OrderUpdate","updateNotPossible");
        }
    }

    public OrderStatus getOrderState(){
        return orderState.getStateName();
    }
}
