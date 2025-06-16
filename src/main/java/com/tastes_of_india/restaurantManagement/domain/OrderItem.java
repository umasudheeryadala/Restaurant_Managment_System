package com.tastes_of_india.restaurantManagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderItemStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.ZonedDateTime;

@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE ,generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderItemStatus status;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @Lob
    @Column(name = "instructions")
    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    private String instructions;

    @ManyToOne(cascade = {
            CascadeType.MERGE,
            CascadeType.REFRESH
    },fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"table","orderItems","payments"},allowSetters = true)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"category","orderItems","images"},allowSetters = true)
    private MenuItem item;

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

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public MenuItem getItem() {
        return item;
    }

    public void setItem(MenuItem item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", quantity=" + quantity +
                ", status=" + status +
                ", createdDate=" + createdDate +
                ", instructions='" + instructions + '\'' +
                '}';
    }
}
