package com.tastes_of_india.restaurantManagement.web.rest;

import com.tastes_of_india.restaurantManagement.service.EmployeeService;
import com.tastes_of_india.restaurantManagement.service.dto.EmployeeBasicDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class EmployeeResource {

    private final String ENTITY_NAME="employeeResource";

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/account")
    public ResponseEntity<EmployeeBasicDTO> getAccount(Principal principal) {
        EmployeeBasicDTO employee = null;
        if (principal instanceof AbstractAuthenticationToken token) {
            employee = employeeService.createEmployeeFromAuthentication(token);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(employee);
    }

    @PutMapping("/account/update_password")
    public ResponseEntity<Void> updateEmployeePassword(@RequestBody EmployeeBasicDTO employee) throws  BadRequestAlertException {
        employeeService.updateEmployeePassword(employee);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/employees/{employeeId}/update")
    public ResponseEntity<EmployeeBasicDTO> updateEmployeeDetails(@PathVariable Long employeeId, @RequestBody EmployeeBasicDTO employee) throws BadRequestException, BadRequestAlertException {
        if (employee.getId() == null) {
            throw new BadRequestAlertException("Employee with id not found",ENTITY_NAME,"idNotFound");
        }

        if (!Objects.equals(employeeId, employee.getId())) {
            throw new BadRequestAlertException("Id mismatch",ENTITY_NAME,"idMisMatch");
        }
        EmployeeBasicDTO result = employeeService.updateEmployeeDetails(employee);
        return ResponseEntity.ok(result);
    }


}
