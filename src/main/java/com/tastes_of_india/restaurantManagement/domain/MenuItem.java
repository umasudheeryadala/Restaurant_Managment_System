package com.tastes_of_india.restaurantManagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Set;

@Entity
@Table(name = "menu_items")
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    @Lob
    @JdbcTypeCode(SqlTypes.LONGNVARCHAR)
    private String description;

    @Column(name = "disabled")
    private boolean disabled=Boolean.FALSE;

    @Column(name = "price")
    private Integer price;

    @Column(name = "is_available")
    private Boolean isAvailable=Boolean.TRUE;

    @Column(name = "special_text")
    private String specialText;

    @Column(name = "is_veg")
    private Boolean isVeg;

    @Column(name = "caution_text")
    private String cautionText;

    @ManyToOne(cascade = {
            CascadeType.MERGE,
            CascadeType.REFRESH
    }, fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"menuItem"})
    private MenuCategory category;

    @OneToMany(mappedBy = "item" , fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"order", "item"} ,allowSetters = true)
    private Set<OrderItem> orderItems;

    @OneToMany(mappedBy = "menuItem")
    @JsonIgnoreProperties(value = {"menuItem"},allowSetters = true)
    private Set<Image> images;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Boolean getAvailable() {
        return isAvailable;
    }

    public void setAvailable(Boolean available) {
        isAvailable = available;
    }

    public String getSpecialText() {
        return specialText;
    }

    public void setSpecialText(String specialText) {
        this.specialText = specialText;
    }

    public Boolean getVeg() {
        return isVeg;
    }

    public void setVeg(Boolean veg) {
        isVeg = veg;
    }

    public String getCautionText() {
        return cautionText;
    }

    public void setCautionText(String cautionText) {
        this.cautionText = cautionText;
    }

    public MenuCategory getMenuCategory() {
        return category;
    }

    public void setMenuCategory(MenuCategory menuCategory) {
        this.category = menuCategory;
    }

    public Set<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public Set<Image> getImages() {
        return images;
    }

    public void setImages(Set<Image> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "MenuItem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", disabled=" + disabled +
                ", price=" + price +
                ", isAvailable=" + isAvailable +
                ", specialText='" + specialText + '\'' +
                ", isVeg=" + isVeg +
                ", cautionText='" + cautionText + '\'' +
                ", menuCategory=" + category +
                '}';
    }
}
