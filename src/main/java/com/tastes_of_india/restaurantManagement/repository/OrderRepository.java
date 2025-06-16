package com.tastes_of_india.restaurantManagement.repository;

import com.tastes_of_india.restaurantManagement.domain.Order;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderStatus;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order,Long> {

    Optional<Order> findByTableIdAndStatusNotIn(Long tableId, List<OrderStatus> orderStatuses);

    Optional<Order> findByIdAndTableId(Long orderId,Long tableId);

    Optional<Order> findByIdAndTableIdAndStatus(Long orderId,Long tableId,OrderStatus orderStatus);

    Optional<Order> findByIdAndTableIdAndStatusNotIn(Long orderId,Long tableId,List<OrderStatus> orderStatuses);

    Optional<Order> findByIdAndStatus(Long orderId,OrderStatus orderStatus);

    @Query(value = "Select order from Order order where (?2 is null or order.tableId=?2) and (?3 is null or order.createdDate>= ?3) and (?4 is null or order.createdDate<=?4) and (?5 is null or order.orderType=?5) and (?6 is null or order.status=?6) and order.id in " +
            "(select o.id from Tables table left join Order o on table.id=o.table.id where table.restaurant.id=?1 )")
    Page<Order> findAllByRestaurantIdAndTableId(Long restaurantId,Long tableId, ZonedDateTime startDateTime, ZonedDateTime endDateTime, OrderType orderType, OrderStatus orderStatus, Pageable pageable);

}
