package com.tastes_of_india.restaurantManagement.service.mapper;

import com.tastes_of_india.restaurantManagement.domain.Image;
import com.tastes_of_india.restaurantManagement.service.dto.ImageDTO;
import jakarta.persistence.Entity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring" ,uses = {})
public interface ImageMapper extends EntityMapper<ImageDTO,Image> {


}
