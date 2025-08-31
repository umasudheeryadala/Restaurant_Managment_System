package com.tastes_of_india.restaurantManagement.web.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.tastes_of_india.restaurantManagement.service.CartService;
import com.tastes_of_india.restaurantManagement.service.dto.CartItemDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CartResource {

    private final Logger LOG= LoggerFactory.getLogger(CartResource.class);

    private final String ENTITY_NAME="cartResource";

    @Autowired
    private CartService cartService;

    @PostMapping("/restaurant/{restaurantId}/table/{tableId}/cart/add_item")
    public ResponseEntity<String> addItemToCart(@PathVariable Long restaurantId,@PathVariable Long tableId, @RequestBody List<CartItemDTO> cartItems) throws BadRequestAlertException, JsonProcessingException {
        for (CartItemDTO cartItem:cartItems) {
            if (cartItem.getId() == null) {
                throw new BadRequestAlertException("Item id should not present", ENTITY_NAME, "itemItemNotPresent");
            }
        }
        cartService.addItemsToCart(restaurantId,tableId,cartItems);

        return ResponseEntity.ok("Item Added successfully");
    }

    @GetMapping("/restaurants/{restaurantId}/table/{tableId}/cart")
    public ResponseEntity<List<CartItemDTO>> getAllCartItems(@PathVariable Long restaurantId,@PathVariable Long tableId) throws BadRequestAlertException, JsonProcessingException {
        return ResponseEntity.ok(cartService.findAllCartItems(restaurantId,tableId));
    }

    @DeleteMapping("/restaurants/{restaurantId}/table/{tableId}/cart/{cartItemId}")
    public ResponseEntity<List<CartItemDTO>> deleteCartItem(@PathVariable Long restaurantId,@PathVariable Long tableId,@PathVariable Long cartItemId) throws BadRequestAlertException, JsonProcessingException {
        if(cartItemId==null){
            throw new BadRequestAlertException("Item Id is empty",ENTITY_NAME,"idNull");
        }

        return ResponseEntity.ok(cartService.deleteCartItem(restaurantId,tableId,cartItemId));
    }

}
