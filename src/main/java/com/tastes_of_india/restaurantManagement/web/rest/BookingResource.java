package com.tastes_of_india.restaurantManagement.web.rest;

import com.tastes_of_india.restaurantManagement.annotation.AuthorizeApiAccess;
import com.tastes_of_india.restaurantManagement.domain.enumeration.BookingStatus;
import com.tastes_of_india.restaurantManagement.domain.enumeration.Designation;
import com.tastes_of_india.restaurantManagement.service.BookingService;
import com.tastes_of_india.restaurantManagement.service.dto.BookingDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping("/api")
public class BookingResource {

    private final Logger LOG= LoggerFactory.getLogger(BookingResource.class);

    private final String ENTITY_NAME="BookingResource";

    @Autowired
    private BookingService bookingService;


    @GetMapping("/restaurant/{restaurantId}/bookings")
    @AuthorizeApiAccess(designation = {Designation.MANAGER,Designation.RECEPTIONIST})
    public ResponseEntity<List<BookingDTO>> getAllBookings(@PathVariable Long restaurantId, @RequestParam ZonedDateTime startDate, @RequestParam ZonedDateTime endDate, @RequestParam(required = false) BookingStatus bookingStatus){

        LOG.debug("Fetch All Bookings By StartDate and EndDate");

        List<BookingDTO> bookings=bookingService.getAllBookingByStartDateAndEndDate(restaurantId,startDate,endDate,bookingStatus);

        return ResponseEntity.ok(bookings);
    }
}
