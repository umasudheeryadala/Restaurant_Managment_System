package com.tastes_of_india.restaurantManagement.repository;

import com.tastes_of_india.restaurantManagement.domain.Booking;
import com.tastes_of_india.restaurantManagement.domain.enumeration.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.ZonedDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking,Long> {

    List<Booking> findAllByTableRestaurantIdAndStartDateTimeAfterAndEndDateTimeBefore(Long restaurantId,ZonedDateTime startDate,ZonedDateTime endDate);

    List<Booking> findAllByTableRestaurantIdAndStartDateTimeAfterAndEndDateTimeBeforeAndStatus(Long restaurantId,ZonedDateTime startDate, ZonedDateTime endDate, BookingStatus status);

    List<Booking> findAllByTableId(Long tableId);


}
