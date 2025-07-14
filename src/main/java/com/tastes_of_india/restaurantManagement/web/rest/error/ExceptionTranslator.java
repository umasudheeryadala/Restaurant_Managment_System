package com.tastes_of_india.restaurantManagement.web.rest.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;

@ControllerAdvice
public class ExceptionTranslator extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = BadRequestAlertException.class)
    public ResponseEntity<Object> handleBadRequestAlertException(BadRequestAlertException ex, NativeWebRequest request){
        HashMap<String,String> mp=new HashMap<>();
        mp.put("error_message",ex.getDefaultMessage());
        mp.put("entity_name",ex.getEntityName());
        mp.put("error_key",ex.getErrorKey());
        return new ResponseEntity(mp,getMappedStatusCode(ex));
    }

    @ExceptionHandler(value = InvalidSessionException.class)
    public ResponseEntity<Object> handleInvalidSessionException(InvalidSessionException ex,NativeWebRequest request){
        HashMap<String,String> mp=new HashMap<>();
        mp.put("error_message",ex.getDefaultMessage());
        mp.put("entity_name",ex.getEntityName());
        mp.put("error_key",ex.getErrorKey());
        return new ResponseEntity(mp,getMappedStatusCode(ex));
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleAnyException(Throwable ex,NativeWebRequest request){
        HashMap<String,String> mp=new HashMap<>();
        mp.put("error_message",ex.getMessage());
        mp.put("entity_name","Restaurant_Management");
        mp.put("error_key","System Error");
        return new ResponseEntity(mp,getMappedStatusCode(ex));
    }

    public HttpStatus getMappedStatusCode(Throwable ex){
        if(ex instanceof BadRequestAlertException) return HttpStatus.BAD_REQUEST;
        if(ex instanceof InsufficientAuthenticationException) return HttpStatus.UNAUTHORIZED;
        if(ex instanceof InvalidSessionException) return HttpStatus.UNAUTHORIZED;
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
