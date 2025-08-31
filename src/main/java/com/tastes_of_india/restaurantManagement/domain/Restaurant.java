package com.tastes_of_india.restaurantManagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tastes_of_india.restaurantManagement.domain.enumeration.RestaurantType;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Table(name = "restaurant")
public class Restaurant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private RestaurantType type;

    @Lob
    @Column(name = "address")
    private String address;

    @Column(name = "deleted")
    private Boolean deleted=Boolean.FALSE;

    @Column(name = "logo_url")
    private String logoUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"tables","restaurants","restaurant"},allowSetters = true)
    private Employee createdBy;

    @OneToMany(mappedBy = "restaurant",fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"restaurant"},allowSetters = true)
    private List<RestaurantEmployee> restaurantEmployees;

    @OneToMany(mappedBy = "restaurant",fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"restaurant","orders","createdBy"},allowSetters = true)
    private List<Tables> tables;

    @OneToMany(mappedBy = "restaurant",fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"restaurant","menuItems"},allowSetters = true)
    private List<MenuCategory> menuCategories;


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

    public RestaurantType getType() {
        return type;
    }

    public void setType(RestaurantType type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public Employee getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Employee createdBy) {
        this.createdBy = createdBy;
    }

    public List<RestaurantEmployee> getRestaurantEmployees() {
        return restaurantEmployees;
    }

    public void setRestaurantEmployees(List<RestaurantEmployee> restaurantEmployees) {
        this.restaurantEmployees = restaurantEmployees;
    }

    public List<Tables> getTables() {
        return tables;
    }

    public void setTables(List<Tables> tables) {
        this.tables = tables;
    }

    public List<MenuCategory> getMenuCategories() {
        return menuCategories;
    }

    public void setMenuCategories(List<MenuCategory> menuCategories) {
        this.menuCategories = menuCategories;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", type=" + type +
                ", address='" + address + '\'' +
                ", deleted=" + deleted +
                '}';
    }
}
