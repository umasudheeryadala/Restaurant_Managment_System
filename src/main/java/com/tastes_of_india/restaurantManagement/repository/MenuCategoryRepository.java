package com.tastes_of_india.restaurantManagement.repository;

import com.tastes_of_india.restaurantManagement.domain.MenuCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MenuCategoryRepository extends JpaRepository<MenuCategory,Long> {

    Optional<MenuCategory> findByNameAndRestaurantId(String name,Long restaurantId);

    @Query(value = "SELECT menu_category from MenuCategory menu_category where (?1 is null or LOWER(menu_category.name) Like %?1%)")
    Page<MenuCategory> findAllByPattern(String pattern, Pageable pageable);

    Page<MenuCategory> findByRestaurantIdAndDisabled(Long restaurantId,Boolean disabled,Pageable pageable);

    Page<MenuCategory> findByRestaurantId(Long restaurantId,Pageable pageable);

    Optional<MenuCategory> findByIdAndRestaurantId(Long id,Long restaurantId);


}
