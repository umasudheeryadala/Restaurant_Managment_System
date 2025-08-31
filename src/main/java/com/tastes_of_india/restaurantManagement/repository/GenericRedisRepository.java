package com.tastes_of_india.restaurantManagement.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class GenericRedisRepository {

    private final RedisTemplate<String,String> redisTemplate;


    public GenericRedisRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String key, String value,Long ttl, TimeUnit timeUnit) throws JsonProcessingException {
        redisTemplate.opsForValue().set(key,value,ttl,timeUnit);
    }

    public void save(String key,String id, String value,Long ttl){
        redisTemplate.opsForValue().set(key+":"+id,value,ttl,TimeUnit.SECONDS);
    }

    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public String get(String key,String id){
        return redisTemplate.opsForValue().get(key+":"+id);
    }

    public void delete(String key){
        redisTemplate.delete(key);
    }
}
