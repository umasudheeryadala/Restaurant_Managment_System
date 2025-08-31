package com.tastes_of_india.restaurantManagement.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tastes_of_india.restaurantManagement.service.dto.CartItemDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;

import java.util.List;

public interface CartService {

    List<CartItemDTO> addItemsToCart(Long restaurantId,Long tableId, List<CartItemDTO> cartItems) throws BadRequestAlertException, JsonProcessingException;

    List<CartItemDTO> findAllCartItems(Long restaurantId,Long tableId) throws BadRequestAlertException, JsonProcessingException;

    List<CartItemDTO> deleteCartItem(Long restaurantId,Long tableId,Long cartItemId) throws BadRequestAlertException, JsonProcessingException;

    void deleteAllCartItems(Long restaurantId,Long tableId);

}
