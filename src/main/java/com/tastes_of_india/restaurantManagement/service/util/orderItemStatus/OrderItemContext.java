package com.tastes_of_india.restaurantManagement.service.util.orderItemStatus;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderItemStatus;
import com.tastes_of_india.restaurantManagement.service.util.orderItemStatus.OrderItemState;
import com.tastes_of_india.restaurantManagement.service.util.orderItemStatus.OrderPlacedState;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;

public class  OrderItemContext {

    OrderItemState orderItemState;

    public OrderItemContext(){
        this.orderItemState=new OrderPlacedState();
    }

    public void setState(OrderItemState orderItemState){
        this.orderItemState=orderItemState;
    }

    public void next() throws BadRequestAlertException {
        orderItemState.next(this);
    }

    public void cancel() throws BadRequestAlertException {
        orderItemState.cancel(this);
    }

    public boolean canProcessPayment(){

        return getStateName()==OrderItemStatus.DELIVERED || getStateName()==OrderItemStatus.CANCELLED;
    }

    public OrderItemStatus getStateName(){
        return orderItemState.getStateName();
    }
}
