package com.tastes_of_india.restaurantManagement.service.dto;

public class AuthTokenDTO {

    private Long tableId;

    private Long orderId;

    private Long restaurantId;

    public Long getTableId() {
        return tableId;
    }

    public void setTableId(Long tableId) {
        this.tableId = tableId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }

    @Override
    public String toString() {
        return "AuthTokenDTO{" +
                "tableId=" + tableId +
                ", orderId=" + orderId +
                ", restaurantId=" + restaurantId +
                '}';
    }
}
