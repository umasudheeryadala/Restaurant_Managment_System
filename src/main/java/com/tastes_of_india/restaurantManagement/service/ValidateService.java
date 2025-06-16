package com.tastes_of_india.restaurantManagement.service;

import com.tastes_of_india.restaurantManagement.service.dto.AuthTokenDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseCookie;

public interface ValidateService {

    ResponseCookie generateJWTToken(AuthTokenDTO authTokenDTO);

    AuthTokenDTO isSessionActive(HttpServletRequest servletRequest) throws BadRequestAlertException;

    ResponseCookie inValidateSession(HttpServletRequest servletRequest) throws BadRequestAlertException;
}
