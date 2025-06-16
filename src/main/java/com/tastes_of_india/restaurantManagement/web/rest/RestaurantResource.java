package com.tastes_of_india.restaurantManagement.web.rest;

import com.tastes_of_india.restaurantManagement.annotation.AuthorizeApiAccess;
import com.tastes_of_india.restaurantManagement.domain.enumeration.Designation;
import com.tastes_of_india.restaurantManagement.service.RestaurantService;
import com.tastes_of_india.restaurantManagement.service.dto.RestaurantDTO;
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

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class RestaurantResource {

    private final Logger LOG= LoggerFactory.getLogger(RestaurantResource.class);

    private final String ENTITY_NAME="restaurantResource";

    @Autowired
    private RestaurantService restaurantService;

    @GetMapping("/restaurants")
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurants(Pageable pageable) throws BadRequestAlertException {
        Page<RestaurantDTO> restaurants=restaurantService.getAllRestaurantByEmployee(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), restaurants);
        return ResponseEntity.ok().headers(headers).body(restaurants.getContent());

    }

    @AuthorizeApiAccess(designation = Designation.All)
    @GetMapping("/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantDTO> getRestaurant(@PathVariable Long restaurantId) throws BadRequestAlertException {
        return ResponseEntity.ok(restaurantService.findByRestaurantById(restaurantId));
    }

    @PostMapping("/restaurants")
    public ResponseEntity<RestaurantDTO> saveRestaurant(@ModelAttribute RestaurantDTO restaurant) throws BadRequestAlertException, IOException {
        RestaurantDTO result=restaurantService.saveRestaurant(restaurant);
        return ResponseEntity.ok(result);
    }

    @AuthorizeApiAccess(designation = {Designation.MANAGER,Designation.OWNER})
    @PutMapping("/restaurants/{restaurantId}")
    public ResponseEntity<RestaurantDTO> updateRestaurant(@ModelAttribute RestaurantDTO restaurant,@PathVariable Long restaurantId) throws BadRequestAlertException, IOException {
        if(restaurant.getId()==null){
            throw new BadRequestAlertException("Id Not Found",ENTITY_NAME,"idNotFound");
        }
        if(!Objects.equals(restaurantId,restaurant.getId())){
            throw new BadRequestAlertException("Id misMatch",ENTITY_NAME,"idMisMatch");
        }
        RestaurantDTO result=restaurantService.saveRestaurant(restaurant);
        return ResponseEntity.ok(result);
    }

    @AuthorizeApiAccess(designation = {Designation.MANAGER,Designation.OWNER})
    @DeleteMapping("/restaurants/{restaurantId}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable Long restaurantId) throws BadRequestAlertException {
        restaurantService.deleteRestaurant(restaurantId);
        return ResponseEntity.ok().build();
    }


}
