package com.tastes_of_india.restaurantManagement.annotation.redis;


import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
public @interface CacheableWithTTL {

    String key();

    String id();

    long ttl() default -1;

    String expiryPath() default "";

    Class<?> type() default Object.class;

    String[] excludePath() default {};

    long expiryAdditionInSeconds() default 0L;

}
