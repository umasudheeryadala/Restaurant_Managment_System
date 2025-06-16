package com.tastes_of_india.restaurantManagement.annotation;

import com.tastes_of_india.restaurantManagement.domain.Employee;
import com.tastes_of_india.restaurantManagement.domain.enumeration.Designation;
import com.tastes_of_india.restaurantManagement.service.EmployeeService;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect
public class AuthorizeApiAccessAspect {

    private final Logger LOG= LoggerFactory.getLogger(AuthorizeApiAccessAspect.class);

    private final String ENTITY_NAME="authorizeApiAccess";

    private final EmployeeService employeeService;

    public AuthorizeApiAccessAspect(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Around("@annotation(com.tastes_of_india.restaurantManagement.annotation.AuthorizeApiAccess)")
    public Object authorizeApi(ProceedingJoinPoint pointCut) throws Throwable {
        LOG.debug("Entering Authorize Api");

        MethodSignature signature=(MethodSignature) pointCut.getSignature();

        Long restaurantId=(Long) pointCut.getArgs()[0];

        Method method =signature.getMethod();

        AuthorizeApiAccess authorizeApiAccess=method.getAnnotation(AuthorizeApiAccess.class);

        Designation[] designations=authorizeApiAccess.designation();

        boolean allowed=employeeService.checkPermission(designations,restaurantId);


        if(!allowed){
            throw new BadRequestAlertException("You are not authorized to perform this operation",ENTITY_NAME,"authorizationFailed");
        }

        return pointCut.proceed();
    }
}
