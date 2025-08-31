package com.tastes_of_india.restaurantManagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tastes_of_india.restaurantManagement.domain.enumeration.TableStatus;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "tables")
public class Tables implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    @Enumerated(value = EnumType.STRING)
    private TableStatus status=TableStatus.AVAILABLE;

    @Column(name = "deleted")
    private boolean deleted= Boolean.FALSE;

    @Column(name = "capacity")
    private Integer capacity;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"tables","restaurants","restaurant"})
    private Employee createdBy;

    @OneToMany(mappedBy = "table" , fetch = FetchType.LAZY)
    private Set<Order> orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"createdBy","employees","tables","menuCategories"},allowSetters = true)
    private Restaurant restaurant;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TableStatus getStatus() {
        return status;
    }

    public void setStatus(TableStatus status) {
        this.status = status;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Set<Order> getOrders() {
        return orders;
    }

    public void setOrders(Set<Order> orders) {
        this.orders = orders;
    }

    public Employee getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Employee createdBy) {
        this.createdBy = createdBy;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public String toString() {
        return "Tables{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", deleted=" + deleted +
                ", capacity=" + capacity +
                ", createdDate=" + createdDate +
                ", createdById=" + createdBy +
                ", orders=" + orders +
                '}';
    }
}
