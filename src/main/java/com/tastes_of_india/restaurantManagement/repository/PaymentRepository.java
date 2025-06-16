package com.tastes_of_india.restaurantManagement.repository;

import com.tastes_of_india.restaurantManagement.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment,Long> {
}
