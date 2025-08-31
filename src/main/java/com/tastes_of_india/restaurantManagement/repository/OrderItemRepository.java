package com.tastes_of_india.restaurantManagement.repository;

import com.tastes_of_india.restaurantManagement.domain.OrderItem;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderItemStatus;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem,Long> {

    List<OrderItem> findByOrderId(Long orderId);

    List<OrderItem> findByOrderTableRestaurantId(Long restaurantId);


    Optional<OrderItem> findByIdAndOrderIdAndStatus(Long itemId, Long orderId, OrderItemStatus orderItemStatus);

    Optional<OrderItem> findByIdAndStatusNotIn(Long itemId, List<OrderItemStatus> orderItemStatuses);

    List<OrderItem> findAllByOrderIdAndOrderTableRestaurantId(Long orderId,Long restaurantId);


}
