package com.tastes_of_india.restaurantManagement.service.dto;

import com.tastes_of_india.restaurantManagement.domain.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public class MenuItemDTO {
    private Long id;

    private String name;

    private String description;

    private boolean disabled=Boolean.FALSE;

    private Integer price;

    private Boolean isAvailable;

    private String specialText;

    private Boolean isVeg;

    private String cautionText;

    private Set<ImageDTO> images;


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

    public Set<ImageDTO> getImages() {
        return images;
    }

    public void setImages(Set<ImageDTO> images) {
        this.images = images;
    }

    @Override
    public String toString() {
        return "MenuItemDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", disabled=" + disabled +
                ", price=" + price +
                ", isAvailable=" + isAvailable +
                ", specialText='" + specialText + '\'' +
                ", isVeg=" + isVeg +
                ", cautionText='" + cautionText + '\'' +
                '}';
    }
}
