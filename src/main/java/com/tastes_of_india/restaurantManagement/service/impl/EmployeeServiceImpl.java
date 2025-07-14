package com.tastes_of_india.restaurantManagement.service.impl;

import com.tastes_of_india.restaurantManagement.config.ApplicationProperties;
import com.tastes_of_india.restaurantManagement.domain.Employee;
import com.tastes_of_india.restaurantManagement.domain.Restaurant;
import com.tastes_of_india.restaurantManagement.domain.RestaurantEmployee;
import com.tastes_of_india.restaurantManagement.domain.enumeration.Designation;
import com.tastes_of_india.restaurantManagement.repository.EmployeeRepository;
import com.tastes_of_india.restaurantManagement.repository.RestaurantEmployeeRepository;
import com.tastes_of_india.restaurantManagement.repository.RestaurantRepository;
import com.tastes_of_india.restaurantManagement.service.EmployeeService;
import com.tastes_of_india.restaurantManagement.service.dto.EmployeeBasicDTO;
import com.tastes_of_india.restaurantManagement.service.dto.RestaurantEmployeeDTO;
import com.tastes_of_india.restaurantManagement.service.mapper.EmployeeMapper;
import com.tastes_of_india.restaurantManagement.service.mapper.RestaurantEmployeeMapper;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
import java.util.*;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final Logger LOG = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    private final String ENTITY_NAME = "EmployeeService";

    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;

    private final RestTemplate restTemplate;

    private final ApplicationProperties applicationProperties;

    private final RestaurantEmployeeRepository restaurantEmployeeRepository;

    private final RestaurantRepository restaurantRepository;

    private final RestaurantEmployeeMapper restaurantEmployeeMapper;

    public EmployeeServiceImpl(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper, RestTemplate restTemplate, ApplicationProperties applicationProperties, RestaurantEmployeeRepository restaurantEmployeeRepository, RestaurantRepository restaurantRepository, RestaurantEmployeeMapper restaurantEmployeeMapper) {
        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.restTemplate = restTemplate;
        this.applicationProperties = applicationProperties;
        this.restaurantEmployeeRepository = restaurantEmployeeRepository;
        this.restaurantRepository = restaurantRepository;
        this.restaurantEmployeeMapper = restaurantEmployeeMapper;
    }

    @Override
    public EmployeeBasicDTO createEmployeeFromAuthentication(AbstractAuthenticationToken authToken) {

        Map<String, Object> details = null;
        if (authToken instanceof OAuth2AuthenticationToken token) {
            details = token.getPrincipal().getAttributes();
        } else if (authToken instanceof JwtAuthenticationToken token) {
            details = token.getTokenAttributes();
        } else {
            LOG.debug("user not found");
        }
        return employeeMapper.toDto(getEmployee(details));
    }

    @Override
    public void updateEmployeePassword(EmployeeBasicDTO employeeBasicDTO) throws BadRequestAlertException {
        if (employeeBasicDTO.getPassword() == null) {
            throw new BadRequestAlertException("password can't be null", ENTITY_NAME, "passwordCannotBeNUll");
        }
        Employee employee = getCurrentlyLoggedInUser();
        String userId = employee.getLoginId();
        String passwordUri = new String(applicationProperties.getKeycloak_password_update_url());
        passwordUri = passwordUri.replace("{userId}", userId);

        Map<String, Object> map = getAccessToken();

        String access_token = map.get("access_token").toString();

        LOG.debug("access token {}", access_token);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(access_token);


        Map<String, Object> body = new HashMap<>();
        body.put("type", applicationProperties.getGrant_type());
        body.put("temporary", false);
        body.put("value", employeeBasicDTO.getPassword());

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        restTemplate.exchange(passwordUri, HttpMethod.PUT, request, Void.class);

    }

    @Override
    public Employee getCurrentlyLoggedInUser() throws BadRequestAlertException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loginId = null;
        if (authentication instanceof JwtAuthenticationToken token) {
            loginId = token.getToken().getClaims().get("sub").toString();

        }

        if(authentication instanceof OAuth2AuthenticationToken token){
            loginId=token.getPrincipal().getName();
        }
        if (loginId != null) {
            Optional<Employee> employee = employeeRepository.findByLoginId(loginId);
            if (employee.isPresent()) return employee.get();
        }
        LOG.debug("authentication context {} {}",authentication,loginId);

        throw new BadRequestAlertException("user not found", ENTITY_NAME, "userNotFound");
    }


    @Override
    public EmployeeBasicDTO updateEmployeeDetails(EmployeeBasicDTO employee) throws BadRequestException {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employee.getId());
        if (optionalEmployee.isEmpty()) {
            throw new BadRequestException("Employee with Id Not found");
        }
        Employee employee1 = optionalEmployee.get();
        employee1.setDesignation(employee.getDesignation());
        employee1.setGender(employee.getGender());
        employee1.setPhoneNumber(employee.getPhoneNumber());
        employee1.setFirstName(employee.getFirstName());
        employee1.setLastName(employee.getLastName());
        employee1.setLastModifiedDate(ZonedDateTime.now());

        employeeRepository.save(employee1);
        return employeeMapper.toDto(employee1);
    }

    @Override
    public Page<RestaurantEmployeeDTO> getAllEmployees(Long restaurantId, Designation designation, String pattern, Pageable pageable) throws BadRequestAlertException {
        restaurantRepository.findByIdAndDeleted(restaurantId, false).orElseThrow(
                () -> new BadRequestAlertException("Restaurant Not Found", ENTITY_NAME, "restaurantNotFound")
        );
        Page<RestaurantEmployee> restaurantEmployees = null;
        if (pattern != null) pattern = pattern.toLowerCase();
        if (designation != null) {
            restaurantEmployees = restaurantEmployeeRepository.findAllByRestaurantIdAndDesignation(restaurantId, designation, pattern, pageable);
        } else {
            restaurantEmployees = restaurantEmployeeRepository.findAllByRestaurantId(restaurantId, pattern, pageable);
        }
        return restaurantEmployees.map(restaurantEmployeeMapper::toDto);
    }

    @Override
    public RestaurantEmployeeDTO saveRestaurantEmployee(Long restaurantId, RestaurantEmployeeDTO employeeDTO) throws BadRequestAlertException {

        Restaurant restaurant=restaurantRepository.findByIdAndDeleted(restaurantId, false).orElseThrow(
                () -> new BadRequestAlertException("Restaurant Not Found", ENTITY_NAME, "restaurantNotFound")
        );

        Employee employee=null;

        if(employeeDTO.getEmployeeId()!=null){
            employee=employeeRepository.findById(employeeDTO.getEmployeeId()).orElseThrow(
                    ()-> new BadRequestAlertException("Employee Not Found",ENTITY_NAME,"employeeNotFound")
            );
            RestaurantEmployee restaurantEmployee=restaurantEmployeeRepository.findByRestaurantIdAndEmployeeId(restaurant.getId(),employee.getId()).orElseThrow(
                    () -> new BadRequestAlertException("Employee Not Found in Restaurant",ENTITY_NAME,"employeeNotFound")
            );

            restaurantEmployee.setDesignation(employeeDTO.getDesignation());
            return restaurantEmployeeMapper.toDto(restaurantEmployeeRepository.save(restaurantEmployee));
        }

        Optional<RestaurantEmployee> optionalRestaurantEmployee=restaurantEmployeeRepository.findByRestaurantIdAndEmployeeEmail(restaurantId,employeeDTO.getEmail());

        if(optionalRestaurantEmployee.isPresent()){
            throw new BadRequestAlertException("Employee Already Exists in Restaurant",ENTITY_NAME,"alreadyExists");
        }



        employee=employeeRepository.findByEmail(employeeDTO.getEmail()).orElseThrow(
                ()-> new BadRequestAlertException("Employee Not Found",ENTITY_NAME,"employeeNotFound")
        );

        RestaurantEmployee restaurantEmployee=new RestaurantEmployee();

        restaurantEmployee.setEmployee(employee);
        restaurantEmployee.setRestaurant(restaurant);
        restaurantEmployee.setDesignation(employeeDTO.getDesignation());

        restaurantEmployeeRepository.save(restaurantEmployee);
        return restaurantEmployeeMapper.toDto(restaurantEmployee);
    }

    @Override
    public void deleteRestaurantEmployee(Long restaurantId, Long employeeId) {
        restaurantEmployeeRepository.deleteByRestaurantIdAndEmployeeId(restaurantId,employeeId);
    }

    @Override
    public boolean checkPermission(Designation[] designations,Long restaurantId) throws BadRequestAlertException {
        Employee employee=getCurrentlyLoggedInUser();
        LOG.debug("employeeId: {}",employee.getId());
        RestaurantEmployee restaurantEmployee=restaurantEmployeeRepository.findByRestaurantIdAndEmployeeId(restaurantId,employee.getId()).orElseThrow(
                () ->new BadRequestAlertException("Employee Doesn't Belong to This Restaurant",ENTITY_NAME,"employeeDoesNotBelongToRestaurant")
        );

        if(designations[0].equals(Designation.All)){
            return true;
        }
        for(Designation designation:designations){
            if(restaurantEmployee.getDesignation().equals(designation)){
                return true;
            }
        }
        return false;
    }

    private Employee getEmployee(Map<String, Object> details) {
        Employee employee = new Employee();
        String sub = String.valueOf(details.get("sub"));
        String userName = null;
        if (details.get("preferred_username") != null) {
            userName = String.valueOf(details.get("preferred_username")).toLowerCase();
        }

        if (details.get("uid") != null) {
            employee.setLoginId((String) details.get("uid"));
            employee.setUserName(sub);
        } else {
            employee.setLoginId(sub);
        }

        if (userName != null) {
            employee.setUserName(userName);
        } else if (employee.getUserName() == null) {
            employee.setUserName(employee.getLoginId());
        }

        if (details.get("given_name") != null) {
            employee.setFirstName((String) details.get("given_name"));
        } else if (details.get("name") != null) {
            employee.setFirstName((String) details.get("name"));
        }

        if (details.get("family_name") != null) {
            employee.setLastName((String) details.get("family_name"));
        }

        if (details.get("email") != null) {
            employee.setEmail((String) details.get("email"));
        }
        employee.setCreatedDate(ZonedDateTime.now());

        return createIfNotExists(employee);
    }

    private Employee createIfNotExists(Employee employee) {
        Optional<Employee> result = employeeRepository.findByLoginId(employee.getLoginId());
        if (result.isPresent()) return result.get();
        return employeeRepository.save(employee);
    }

    private Map<String, Object> getAccessToken() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", applicationProperties.getKeycloak_client_id());
        body.add("username", applicationProperties.getKeycloak_admin_user_name());
        body.add("password", applicationProperties.getKeycloak_admin_password());
        body.add("grant_type", applicationProperties.getGrant_type());
        body.add("client-secret", applicationProperties.getKeycloak_client_secret());
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, httpHeaders);
        ResponseEntity<Map> response = restTemplate.exchange(
                applicationProperties.getKeycloak_access_token_url(),
                HttpMethod.POST,
                requestEntity,
                Map.class
        );
        return response.getBody();
    }
}
