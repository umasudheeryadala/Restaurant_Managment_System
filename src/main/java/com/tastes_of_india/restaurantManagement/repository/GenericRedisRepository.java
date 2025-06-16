package com.tastes_of_india.restaurantManagement.repository;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class GenericRedisRepository {

    private final RedisTemplate<String,Object> redisTemplate;

    public GenericRedisRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String key, Object value,Long ttl, TimeUnit timeUnit){
        redisTemplate.opsForValue().set(key,value,ttl,timeUnit);
    }

    public Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public void delete(String key){
        redisTemplate.delete(key);
    }
}
