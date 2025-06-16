package com.tastes_of_india.restaurantManagement.service.dto;

import com.tastes_of_india.restaurantManagement.domain.MenuItem;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Set;

public class MenuCategoryDTO {

    private Long id;

    private String name;

    private Boolean disabled;

    private String description;

    private Set<MenuItemDTO> menuItems;

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

    public Set<MenuItemDTO> getMenuItems() {
        return menuItems;
    }

    public void setMenuItems(Set<MenuItemDTO> menuItems) {
        this.menuItems = menuItems;
    }

    @Override
    public String toString() {
        return "MenuCategoryDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", disabled=" + disabled +
                ", description='" + description + '\'' +
                ", menuItems=" + menuItems +
                '}';
    }
}
