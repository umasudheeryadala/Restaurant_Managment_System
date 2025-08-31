package com.tastes_of_india.restaurantManagement.annotation.redis.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tastes_of_india.restaurantManagement.annotation.redis.CacheWriteWithTTL;
import com.tastes_of_india.restaurantManagement.repository.GenericRedisRepository;
import org.apache.commons.lang3.SerializationUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Optional;

@Aspect
@Component
public class CacheWriteWithTTlAspect {

    private final Logger Log= LoggerFactory.getLogger(CacheWriteWithTTlAspect.class);

    private final GenericRedisRepository genericRedisRepository;


    public CacheWriteWithTTlAspect(GenericRedisRepository genericRedisRepository) {
        this.genericRedisRepository = genericRedisRepository;
    }

    @Pointcut("@annotation(com.tastes_of_india.restaurantManagement.annotation.redis.CacheWriteWithTTL)")
    public void cacheWriteWithTTLMethod(){}


    @AfterReturning(pointcut = "@annotation(com.tastes_of_india.restaurantManagement.annotation.redis.CacheWriteWithTTL)",returning = "result")
    public void cacheWriteWithTTLMethodAdvice(JoinPoint joinPoint,Object result){
        try{
            if(result==null){
                return;
            }

            MethodSignature methodSignature= (MethodSignature) joinPoint.getSignature();

            Method method=methodSignature.getMethod();

            CacheWriteWithTTL cacheWriteWithTTL=method.getAnnotation(CacheWriteWithTTL.class);

            String key= cacheWriteWithTTL.key();

            String id= cacheWriteWithTTL.id();

            String expiryPath= cacheWriteWithTTL.expiryPath();

            Long ttl= cacheWriteWithTTL.ttl();

            Long expiryAdditionInSeconds= cacheWriteWithTTL.expiryAdditionInSeconds();

            String[] excludePath= cacheWriteWithTTL.excludePath();

            Class<?> type=cacheWriteWithTTL.type();

            Object[] args=joinPoint.getArgs();

            boolean isOptional= result instanceof Optional;

            if(isOptional){
                if(((Optional<?>)result).isPresent()){
                    result=((Optional<?>)result).get();
                }else{
                    return;
                }
            }

            Object clonedResult= SerializationUtils.clone((java.io.Serializable) result);

            id= RedisAspectUtils.getValue(id,args,clonedResult).toString();


            ObjectMapper objectMapper=new ObjectMapper();

            objectMapper.registerModule(new JavaTimeModule());

            if(ttl==-1){
                ttl=RedisAspectUtils.getTTL(clonedResult,expiryPath,expiryAdditionInSeconds,args);

                if (ttl==null){
                    return;
                }
            }

            Log.debug("key: {}, id: {},ttl: {},expiryPath: {} ",key,id,ttl,expiryPath);


            String toSave=RedisAspectUtils.getValueWithExcludePathRemoved(clonedResult,excludePath,objectMapper);

            genericRedisRepository.save(key,id,toSave,ttl);

            Log.debug("Saved to Redis: key: {},id {},ttl {}",key,id,ttl);

        } catch (Exception e) {
            Log.debug("Exception: {}",e);
        }
    }
}
