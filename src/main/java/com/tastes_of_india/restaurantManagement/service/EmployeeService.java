package com.tastes_of_india.restaurantManagement.service;

import com.tastes_of_india.restaurantManagement.domain.Employee;
import com.tastes_of_india.restaurantManagement.domain.enumeration.Designation;
import com.tastes_of_india.restaurantManagement.service.dto.EmployeeBasicDTO;
import com.tastes_of_india.restaurantManagement.service.dto.RestaurantEmployeeDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AbstractAuthenticationToken;

public interface EmployeeService {

    EmployeeBasicDTO createEmployeeFromAuthentication(AbstractAuthenticationToken authToken);

    void updateEmployeePassword(EmployeeBasicDTO employeeBasicDTO) throws  BadRequestAlertException;

    Employee getCurrentlyLoggedInUser() throws  BadRequestAlertException;


    EmployeeBasicDTO updateEmployeeDetails(EmployeeBasicDTO employee) throws BadRequestException;

    Page<RestaurantEmployeeDTO> getAllEmployees(Long restaurantId, Designation designation, String pattern, Pageable pageable) throws BadRequestAlertException;

    RestaurantEmployeeDTO saveRestaurantEmployee(Long restaurantId, RestaurantEmployeeDTO employeeDTO) throws BadRequestAlertException;

    void deleteRestaurantEmployee(Long restaurantId,Long employeeId);

    boolean checkPermission(Designation[] designations,Long restaurantId) throws BadRequestAlertException;

}
