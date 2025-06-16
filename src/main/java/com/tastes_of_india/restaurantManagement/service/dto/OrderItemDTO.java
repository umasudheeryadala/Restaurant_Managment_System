package com.tastes_of_india.restaurantManagement.service.dto;

import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderItemStatus;

public class OrderItemDTO {

    private Long id;

    private Integer quantity;

    private OrderItemStatus status;

    private String instructions;

    private MenuItemDTO menuItem;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public OrderItemStatus getStatus() {
        return status;
    }

    public void setStatus(OrderItemStatus status) {
        this.status = status;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public MenuItemDTO getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItemDTO menuItem) {
        this.menuItem = menuItem;
    }

    @Override
    public String toString() {
        return "OrderItemDTO{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", status=" + status +
                ", instructions='" + instructions + '\'' +
                ", menuItem=" + menuItem +
                '}';
    }
}
