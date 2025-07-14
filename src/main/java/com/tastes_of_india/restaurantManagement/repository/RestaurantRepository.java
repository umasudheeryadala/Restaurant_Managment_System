package com.tastes_of_india.restaurantManagement.repository;

import com.tastes_of_india.restaurantManagement.domain.Restaurant;
import com.tastes_of_india.restaurantManagement.domain.enumeration.Designation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<Restaurant,Long> {

    Optional<Restaurant> findByName(String name);

    Optional<Restaurant> findByIdAndDeleted(Long id,boolean deleted);

    Optional<Restaurant> findByDeletedAndTablesId(boolean deleted,Long tableId);

    @Query(value = "select restaurant from Restaurant restaurant where restaurant.createdBy.Id= ?1 and restaurant.deleted=false or restaurant.id in (select restaurantEmployee.restaurant.id from RestaurantEmployee restaurantEmployee left join Restaurant restaurant on restaurantEmployee.restaurant.id=restaurant.id where restaurantEmployee.employee.id=?1 and restaurantEmployee.designation=?2)")
    Page<Restaurant> findAllByCreatedByIdOrRestaurantEmployeeEmployeeIdIdAndDesignation(Long id, Designation designation, Pageable pageable);

}
