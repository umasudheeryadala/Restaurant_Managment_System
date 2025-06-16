package com.tastes_of_india.restaurantManagement.repository;

import com.tastes_of_india.restaurantManagement.domain.RestaurantEmployee;
import com.tastes_of_india.restaurantManagement.domain.RestaurantEmployeeId;
import com.tastes_of_india.restaurantManagement.domain.enumeration.Designation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface RestaurantEmployeeRepository extends JpaRepository<RestaurantEmployee, RestaurantEmployeeId> {

    @Query(value = "select restaurantEmployee from RestaurantEmployee restaurantEmployee where restaurantEmployee.restaurant.id=?1 and (?2 is null or LOWER(restaurantEmployee.employee.firstName) Like %?2%)")
    Page<RestaurantEmployee> findAllByRestaurantId(Long restaurantId, String pattern, Pageable pageable);

    @Query(value = "select restaurantEmployee from RestaurantEmployee restaurantEmployee where restaurantEmployee.restaurant.id=?1 and restaurantEmployee.designation=?2 and (?3 is null or LOWER(restaurantEmployee.employee.firstName) Like %?3%)")
    Page<RestaurantEmployee> findAllByRestaurantIdAndDesignation(Long restaurantId, Designation designation, String pattern, Pageable pageable);

    Optional<RestaurantEmployee> findByRestaurantIdAndEmployeeEmail(Long restaurantId,String email);

    Optional<RestaurantEmployee> findByRestaurantIdAndEmployeeId(Long restaurantId,Long employeeId);

    void deleteByRestaurantIdAndEmployeeId(Long restaurantId,Long employeeId);

}
