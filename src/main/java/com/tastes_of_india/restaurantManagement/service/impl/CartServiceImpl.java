package com.tastes_of_india.restaurantManagement.service.impl;

import com.tastes_of_india.restaurantManagement.config.Constants;
import com.tastes_of_india.restaurantManagement.domain.MenuItem;
import com.tastes_of_india.restaurantManagement.domain.Tables;
import com.tastes_of_india.restaurantManagement.repository.GenericRedisRepository;
import com.tastes_of_india.restaurantManagement.repository.MenuItemRepository;
import com.tastes_of_india.restaurantManagement.repository.TableRepository;
import com.tastes_of_india.restaurantManagement.service.CartService;
import com.tastes_of_india.restaurantManagement.service.dto.CartItemDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class CartServiceImpl implements CartService {

    private final Logger LOG = LoggerFactory.getLogger(CartServiceImpl.class);

    private final String ENTITY_NAME = "cartService";

    private final TableRepository tableRepository;

    private final GenericRedisRepository genericRedisRepository;

    public CartServiceImpl( TableRepository tableRepository, GenericRedisRepository genericRedisRepository) {
        this.tableRepository = tableRepository;
        this.genericRedisRepository = genericRedisRepository;
    }

    @Override
    public List<CartItemDTO> addItemsToCart(Long restaurantId,Long tableId, List<CartItemDTO> cartItemDTOs) throws BadRequestAlertException {
        LOG.debug("Adding Items To Cart: {}",cartItemDTOs);
        Optional<Tables> optionalTable = tableRepository.findByIdAndRestaurantIdAndDeleted(tableId,restaurantId,false);
        if (optionalTable.isEmpty()) {
            throw new BadRequestAlertException("Table Not Found", ENTITY_NAME, "tableNotFound");
        }

        String cartKey=Constants.CART_KEY+Constants.REDIS_SEPARATOR+restaurantId+Constants.REDIS_SEPARATOR+tableId;
        List<CartItemDTO> cartItems = (List<CartItemDTO>) genericRedisRepository.get(cartKey);

        if (cartItems == null) cartItems = new ArrayList<>();
        for (CartItemDTO cartItemDTO : cartItemDTOs) {
            boolean itemExists = false;
            for (CartItemDTO cartItem : cartItems) {
                if (cartItem.getId().equals(cartItemDTO.getId())) {
                    cartItem.setQuantity(cartItemDTO.getQuantity());
                    cartItem.setInstructions(cartItemDTO.getInstructions());
                    itemExists = true;
                }
            }
            if (!itemExists) {
                cartItems.add(cartItemDTO);
            }
        }
        try {
            genericRedisRepository.save(cartKey, cartItems, 60L, TimeUnit.MINUTES);
        } catch (Exception e) {
            throw new BadRequestAlertException("Error while adding item into cart", ENTITY_NAME, "error");
        }
        return cartItems;
    }

    @Override
    public List<CartItemDTO> findAllCartItems(Long restaurantId,Long tabledId) throws BadRequestAlertException {
        Optional<Tables> optionalTable = tableRepository.findByIdAndDeleted(tabledId,false);
        if (optionalTable.isEmpty()) {
            throw new BadRequestAlertException("Table Not Found", ENTITY_NAME, "tableNotFound");
        }
        String cartKey=Constants.CART_KEY+Constants.REDIS_SEPARATOR+restaurantId+Constants.REDIS_SEPARATOR+tabledId;

        return (List<CartItemDTO>) genericRedisRepository.get(cartKey);
    }

    @Override
    public List<CartItemDTO> deleteCartItem(Long restaurantId,Long tableId, Long cartItemId) throws BadRequestAlertException {
        Optional<Tables> optionalTable = tableRepository.findByIdAndDeleted(tableId,false);
        if (optionalTable.isEmpty()) {
            throw new BadRequestAlertException("Table Not Found", ENTITY_NAME, "tableNotFound");
        }

        String cartKey=Constants.CART_KEY+Constants.REDIS_SEPARATOR+restaurantId+Constants.REDIS_SEPARATOR+tableId;

        List<CartItemDTO> cartItems = (List<CartItemDTO>) genericRedisRepository.get(cartKey);

        if (cartItems == null) cartItems = new ArrayList<>();

        for (CartItemDTO cartItem : cartItems) {
            if (cartItem.getId().equals( cartItemId)) {
                cartItems.remove(cartItem);
                break;
            }
        }
        genericRedisRepository.save(cartKey, cartItems, 60L, TimeUnit.MINUTES);
        return cartItems;
    }

    @Override
    public void deleteAllCartItems(Long restaurantId,Long tableId) {

        String cartKey=Constants.CART_KEY+Constants.REDIS_SEPARATOR+restaurantId+Constants.REDIS_SEPARATOR+tableId;

        genericRedisRepository.delete(cartKey);
    }
}
