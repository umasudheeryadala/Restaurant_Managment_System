package com.tastes_of_india.restaurantManagement.service;

import com.tastes_of_india.restaurantManagement.domain.enumeration.BookingStatus;
import com.tastes_of_india.restaurantManagement.service.dto.BookingDTO;

import java.time.ZonedDateTime;
import java.util.List;

public interface BookingService {

    List<BookingDTO> getAllBookingByStartDateAndEndDate(Long restaurantId,ZonedDateTime startDate, ZonedDateTime endDate, BookingStatus bookingStatus);


    void getAllAvailableTimeSlots(Integer capacity);
}
