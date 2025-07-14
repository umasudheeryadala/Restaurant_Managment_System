package com.tastes_of_india.restaurantManagement.service.dto;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderStatus;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderType;

import java.time.ZonedDateTime;
import java.util.List;

public class OrderDTO {

    private Long id;

    private OrderStatus status;

    private OrderType orderType;

    private ZonedDateTime createdType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }

    public ZonedDateTime getCreatedType() {
        return createdType;
    }

    public void setCreatedType(ZonedDateTime createdType) {
        this.createdType = createdType;
    }


    @Override
    public String toString() {
        return "OrderDTO{" +
                "id=" + id +
                ", status=" + status +
                ", orderType=" + orderType +
                ", createdType=" + createdType +
                '}';
    }
}
