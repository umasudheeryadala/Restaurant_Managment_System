package com.tastes_of_india.restaurantManagement.service;

import com.tastes_of_india.restaurantManagement.service.dto.CartItemDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;

import java.util.List;

public interface CartService {

    List<CartItemDTO> addItemsToCart(Long restaurantId,Long tableId, List<CartItemDTO> cartItems) throws BadRequestAlertException;

    List<CartItemDTO> findAllCartItems(Long restaurantId,Long tableId) throws BadRequestAlertException;

    List<CartItemDTO> deleteCartItem(Long restaurantId,Long tableId,Long cartItemId) throws BadRequestAlertException;

    void deleteAllCartItems(Long restaurantId,Long tableId);

}
