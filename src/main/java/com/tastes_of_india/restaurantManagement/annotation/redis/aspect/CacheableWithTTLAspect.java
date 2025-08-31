package com.tastes_of_india.restaurantManagement.annotation.redis.aspect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tastes_of_india.restaurantManagement.annotation.redis.CacheableWithTTL;
import com.tastes_of_india.restaurantManagement.repository.GenericRedisRepository;
import org.apache.commons.lang3.SerializationUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
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
public class CacheableWithTTLAspect {

    private final Logger Log= LoggerFactory.getLogger(CacheableWithTTLAspect.class);

    private final GenericRedisRepository genericRedisRepository;

    public CacheableWithTTLAspect(GenericRedisRepository genericRedisRepository) {
        this.genericRedisRepository = genericRedisRepository;
    }

    @Pointcut("@annotation(com.tastes_of_india.restaurantManagement.annotation.redis.CacheableWithTTL)")
    public void cacheableWithTTLMethod(){}

    @Around("@annotation(com.tastes_of_india.restaurantManagement.annotation.redis.CacheableWithTTL)")
    public Object cacheableWithTTLMethodAspect(ProceedingJoinPoint joinPoint) throws Throwable {

        Log.debug("checkPoint");

        try {
            MethodSignature methodSignature= (MethodSignature) joinPoint.getSignature();

            Method method=methodSignature.getMethod();

            CacheableWithTTL cacheableWithTTL=method.getAnnotation(CacheableWithTTL.class);

            String key=cacheableWithTTL.key();

            String id= cacheableWithTTL.id();

            Long ttl= cacheableWithTTL.ttl();

            String expiryPath= cacheableWithTTL.expiryPath();

            Long expiryAdditionInSeconds= cacheableWithTTL.expiryAdditionInSeconds();

            String[] excludePath= cacheableWithTTL.excludePath();

            Class<?> type=cacheableWithTTL.type();

            Object[] args=joinPoint.getArgs();

            id= RedisAspectUtils.getValue(id,args,null).toString();

            Object read=null;

            ObjectMapper objectMapper=new ObjectMapper().registerModule(new JavaTimeModule());

            try {
                read=RedisAspectUtils.read(genericRedisRepository,key,id,type,method.getReturnType(),objectMapper);
            }catch (Exception e){
                Log.debug("value not found in redis");
            }

            Log.debug("value from redis: {}",read);

            if(read!=null){
                return read;
            }

            Object result=joinPoint.proceed();

            boolean isOptional= result instanceof Optional;

            if(isOptional){
                if(((Optional<?>) result).isPresent()){
                    result=((Optional<?>) result).get();
                }else {
                    return result;
                }
            }

            Object clonedResult= SerializationUtils.clone((java.io.Serializable) result);

            id=RedisAspectUtils.getValue(id,args,clonedResult).toString();

            if(ttl==-1){
                ttl=RedisAspectUtils.getTTL(clonedResult,expiryPath,expiryAdditionInSeconds,args);
                if(ttl==null){
                    if(isOptional){
                        return Optional.of(result);
                    }
                    return result;
                }
            }

            String toSave=RedisAspectUtils.getValueWithExcludePathRemoved(clonedResult,excludePath,objectMapper);

            genericRedisRepository.save(key,id,toSave,ttl);

            Log.debug("value to save in redis: {}",toSave);

            if(isOptional){
                return Optional.of(result);
            }
            return result;

        }catch (Exception e){
            Log.debug("Exception in CacheableWithTTL: {}",e);

            return joinPoint.proceed();
        }
    }
}
