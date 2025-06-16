package com.tastes_of_india.restaurantManagement.annotation;

import com.tastes_of_india.restaurantManagement.domain.enumeration.Designation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthorizeApiAccess {

    Designation[] designation() ;
}
