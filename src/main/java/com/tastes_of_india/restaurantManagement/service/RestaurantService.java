package com.tastes_of_india.restaurantManagement.service;

import com.tastes_of_india.restaurantManagement.service.dto.RestaurantDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import io.minio.errors.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface RestaurantService {

    RestaurantDTO saveRestaurant(RestaurantDTO restaurantDTO) throws BadRequestAlertException, IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    Page<RestaurantDTO> getAllRestaurantByEmployee(Pageable pageable) throws BadRequestAlertException;

    RestaurantDTO findByRestaurantById(Long restaurantId) throws BadRequestAlertException;

    void deleteRestaurant(Long restaurantId) throws BadRequestAlertException;
}
