package com.tastes_of_india.restaurantManagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "menu_category")
public class MenuCategory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "disabled")
    private Boolean disabled=Boolean.FALSE;

    @Lob
    @Column(name = "description")
    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    private String description;

    @OneToMany(mappedBy = "category",fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"category","orderItems" , "images"},allowSetters = true)
    private Set<MenuItem> menuItems;

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

    public Boolean getDisabled() {
        return disabled;
    }

    public void setDisabled(Boolean disabled) {
        this.disabled = disabled;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<MenuItem> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(Set<MenuItem> menuItems) {
        this.menuItems = menuItems;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public String toString() {
        return "MenuCategory{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", disabled=" + disabled +
                ", description='" + description + '\'' +
                ", menuItems=" + menuItems +
                '}';
    }
}
