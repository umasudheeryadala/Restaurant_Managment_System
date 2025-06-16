package com.tastes_of_india.restaurantManagement.repository;

import com.tastes_of_india.restaurantManagement.domain.MenuItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface MenuItemRepository extends JpaRepository<MenuItem,Long> {

    @Query(value = "select menuItem from MenuItem menuItem where menuItem.category.id =?1 and (?2 is null or menuItem.disabled=?2) and (?3 is null or menuItem.isAvailable=?3) and (?4 is null or menuItem.isVeg=?4) and (?5 is null or LOWER(menuItem.name) LIKE %?5%)")
    Page<MenuItem> findAllByCategoryIdAndDisabledAndPattern(Long categoryId, Boolean disabled,Boolean available,Boolean veg, String pattern, Pageable pageable);

    Optional<MenuItem> findByNameAndCategoryId(String name,Long id);

    List<MenuItem> findAllByIdInAndCategoryRestaurantIdAndDisabled(Set<Long> itemIds, Long restaurantId, Boolean disabled);

    Optional<MenuItem> findByIdAndCategoryId(Long id, Long menuCategoryId);
}
