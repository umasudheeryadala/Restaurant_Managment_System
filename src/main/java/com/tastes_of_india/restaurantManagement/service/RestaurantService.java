package com.tastes_of_india.restaurantManagement.service;

import com.tastes_of_india.restaurantManagement.service.dto.RestaurantDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;

public interface RestaurantService {

    RestaurantDTO saveRestaurant(RestaurantDTO restaurantDTO) throws BadRequestAlertException, IOException;

    Page<RestaurantDTO> getAllRestaurantByEmployee(Pageable pageable) throws BadRequestAlertException;

    RestaurantDTO findByRestaurantById(Long restaurantId) throws BadRequestAlertException;

    void deleteRestaurant(Long restaurantId) throws BadRequestAlertException;
}
