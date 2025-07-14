package com.tastes_of_india.restaurantManagement.repository;

import com.tastes_of_india.restaurantManagement.domain.Tables;
import com.tastes_of_india.restaurantManagement.domain.enumeration.TableStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TableRepository extends JpaRepository<Tables,Long> {

    Tables save(Tables table);

    Optional<Tables> findByIdAndRestaurantIdAndDeleted(Long tableId,Long restaurantId, boolean deleted);

    Optional<Tables> findByIdAndDeleted(Long tableId,boolean deleted);

    Optional<Tables> findByIdAndRestaurantIdAndDeletedAndStatus(Long tableId,Long restaurantId,Boolean deleted,TableStatus status);

    List<Tables> findAllByDeletedAndCapacity(boolean deleted,Integer capacity);

    Optional<Tables> findByRestaurantIdAndNameAndDeleted(Long restaurantId,String name,boolean deleted);

    Page<Tables> findAllByRestaurantIdAndDeleted(Long id,boolean deleted, Pageable pageable);

    Page<Tables> findAllByRestaurantIdAndDeletedAndStatus(Long id,boolean deleted,TableStatus tableStatus,Pageable pageable);

}
