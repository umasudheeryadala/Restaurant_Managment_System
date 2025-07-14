package com.tastes_of_india.restaurantManagement.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.google.gson.Gson;
import com.tastes_of_india.restaurantManagement.config.ApplicationProperties;
import com.tastes_of_india.restaurantManagement.domain.enumeration.TableStatus;
import com.tastes_of_india.restaurantManagement.service.TableService;
import com.tastes_of_india.restaurantManagement.service.ValidateService;
import com.tastes_of_india.restaurantManagement.service.dto.AuthTokenDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import com.tastes_of_india.restaurantManagement.web.rest.error.InvalidSessionException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import java.util.Optional;

@Service
public class ValidateServiceImpl implements ValidateService {

    private final Logger LOG = LoggerFactory.getLogger(ValidateServiceImpl.class);

    private final String ENTITY_NAME = "validateServiceImpl";

    private final Gson gson;

    private final ApplicationProperties applicationProperties;

    private final TableService tableService;

    public ValidateServiceImpl(Gson gson, ApplicationProperties applicationProperties, TableService tableService) {
        this.gson = gson;
        this.applicationProperties = applicationProperties;
        this.tableService = tableService;
    }


    @Override
    public ResponseCookie generateJWTToken(AuthTokenDTO authTokenDTO) {
        LOG.debug("Generation JWT token with Id {}: ", authTokenDTO);
        String token = JWT.create()
                .withSubject(String.valueOf(gson.toJson(authTokenDTO)))
                .withExpiresAt(new Date(System.currentTimeMillis() + 60000L * 120))
                .sign(Algorithm.HMAC512(applicationProperties.getClientSecret().getBytes()));

        return ResponseCookie.from("auth_token", token).maxAge(Duration.of(120, ChronoUnit.MINUTES)).path("/").httpOnly(true).build();
    }

    @Override
    public AuthTokenDTO isSessionActive(HttpServletRequest servletRequest) throws  InvalidSessionException {
        Optional<String> tokenValue = getCookieByName(servletRequest, "auth_token");
        if (tokenValue.isEmpty()) {
            throw new InvalidSessionException("Session is Not Active",ENTITY_NAME,"INVALID_SESSION");
        }
        String value = JWT
                .require(Algorithm.HMAC512(applicationProperties.getClientSecret().getBytes()))
                .build()
                .verify(tokenValue.get())
                .getSubject();
        if (value == null) {
            throw new InvalidSessionException("Session is Not Active",ENTITY_NAME,"INVALID_SESSION");
        }
        return gson.fromJson(value,AuthTokenDTO.class);
    }

    @Override
    public ResponseCookie inValidateSession(HttpServletRequest servletRequest) throws BadRequestAlertException, InvalidSessionException {
        AuthTokenDTO tokenDTO=isSessionActive(servletRequest);
        tableService.updateTableStatus(tokenDTO.getRestaurantId(), tokenDTO.getTableId(), TableStatus.AVAILABLE);
        return ResponseCookie.from("auth_token",null).maxAge(0).path("/").build();
    }

    private Optional<String> getCookieByName(HttpServletRequest request, String cookieName) {
        return Arrays.stream(request.getCookies()).sequential().filter(cookie -> cookie.getName().equals(cookieName)).map(Cookie::getValue).findAny();

    }
}
