package com.tastes_of_india.restaurantManagement.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tastes_of_india.restaurantManagement.service.dto.CartItemDTO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class CartRepository extends GenericRedisRepository{
    public CartRepository(RedisTemplate<String, String> redisTemplate) {
        super(redisTemplate);
    }

    public List<CartItemDTO> getCartItems(String key) throws JsonProcessingException {
        String valueFromRedis=get(key);
        if(valueFromRedis==null){
            return null;
        }
        ObjectMapper objectMapper=new ObjectMapper();
        return objectMapper.readValue(valueFromRedis,objectMapper.getTypeFactory().constructCollectionType(ArrayList.class,CartItemDTO.class));
    }

    public void saveCartItems(String cartKey, List<CartItemDTO> cartItems, Long time, TimeUnit timeUnit) throws JsonProcessingException {
        ObjectMapper objectMapper=new ObjectMapper();
        String valueToSave=objectMapper.writeValueAsString(cartItems);
        save(cartKey,valueToSave,time,timeUnit);
    }

    public void deleteCart(String cartKey){
        delete(cartKey);
    }


}
