package com.tastes_of_india.restaurantManagement.web.rest;

import com.tastes_of_india.restaurantManagement.domain.enumeration.Designation;
import com.tastes_of_india.restaurantManagement.service.EmployeeService;
import com.tastes_of_india.restaurantManagement.service.dto.RestaurantEmployeeDTO;
import com.tastes_of_india.restaurantManagement.service.util.PaginationUtil;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class RestaurantEmployeeResource {

    private final Logger LOG= LoggerFactory.getLogger(RestaurantEmployeeResource.class);

    private final String ENTITY_NAME="restaurantEmployeeResource";

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/restaurants/{restaurantId}/employees")
    public ResponseEntity<List<RestaurantEmployeeDTO>> getAllEmployees(@PathVariable Long restaurantId, @RequestParam(required = false) Designation designation, @RequestParam(required = false) String pattern, Pageable pageable) throws BadRequestAlertException {
        Page<RestaurantEmployeeDTO> result=employeeService.getAllEmployees(restaurantId,designation,pattern,pageable);
        HttpHeaders headers= PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(),result);
        return ResponseEntity.ok().headers(headers).body(result.getContent());
    }

    @PostMapping("/restaurants/{restaurantId}/employee")
    public ResponseEntity<RestaurantEmployeeDTO> saveRestaurantEmployee(@PathVariable Long restaurantId, @RequestBody RestaurantEmployeeDTO employeeDTO) throws BadRequestAlertException {
        if(employeeDTO.getEmail()==null){
            throw new BadRequestAlertException("Email Not Found",ENTITY_NAME,"emailNotFound");
        }
        return ResponseEntity.ok(employeeService.saveRestaurantEmployee(restaurantId,employeeDTO));
    }

    @PutMapping("/restaurants/{restaurantId}/employee/{employeeId}")
    public ResponseEntity<RestaurantEmployeeDTO> updateRestaurantEmployee(@PathVariable Long restaurantId,@PathVariable Long employeeId, @RequestBody RestaurantEmployeeDTO employeeDTO) throws BadRequestAlertException {

        if(employeeDTO.getEmployeeId()==null){
            throw new BadRequestAlertException("Employee Id Not Found",ENTITY_NAME,"idNotFound");
        }

        if(!Objects.equals(employeeId,employeeDTO.getEmployeeId())){
            throw new BadRequestAlertException("Id MisMatch",ENTITY_NAME,"idMisMatch");
        }
        return ResponseEntity.ok(employeeService.saveRestaurantEmployee(restaurantId,employeeDTO));
    }

    @DeleteMapping("/restaurants/{restaurantId}/employee/{employeeId}")
    public ResponseEntity<Void> deleteRestaurantEmployee(@PathVariable Long restaurantId,@PathVariable Long employeeId){
        employeeService.deleteRestaurantEmployee(restaurantId,employeeId);
        return ResponseEntity.ok().build();
    }
}
