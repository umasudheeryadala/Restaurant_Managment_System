package com.tastes_of_india.restaurantManagement.service.impl;

import com.tastes_of_india.restaurantManagement.domain.Booking;
import com.tastes_of_india.restaurantManagement.domain.Tables;
import com.tastes_of_india.restaurantManagement.domain.enumeration.BookingStatus;
import com.tastes_of_india.restaurantManagement.repository.BookingRepository;
import com.tastes_of_india.restaurantManagement.repository.TableRepository;
import com.tastes_of_india.restaurantManagement.service.BookingService;
import com.tastes_of_india.restaurantManagement.service.dto.BookingDTO;
import com.tastes_of_india.restaurantManagement.service.mapper.BookingMapper;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final Logger LOG = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final String ENTITY_NAME = "BookingServiceImpl";

    private final BookingRepository bookingRepository;

    private final BookingMapper bookingMapper;

    private final TableRepository tableRepository;

    private final ZoneId zoneId=ZoneId.of("Asia/Kolkata");

    public BookingServiceImpl(BookingRepository bookingRepository, BookingMapper bookingMapper, TableRepository tableRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
        this.tableRepository = tableRepository;
    }

    @Override
    public List<BookingDTO> getAllBookingByStartDateAndEndDate(Long restaurantId,ZonedDateTime startDate, ZonedDateTime endDate, BookingStatus bookingStatus) {
        if (bookingStatus == null) {
            return bookingRepository.findAllByTableRestaurantIdAndStartDateTimeAfterAndEndDateTimeBefore(restaurantId,startDate, endDate).stream().map(bookingMapper::toDto).toList();
        } else {
            return bookingRepository.findAllByTableRestaurantIdAndStartDateTimeAfterAndEndDateTimeBeforeAndStatus(restaurantId,startDate, endDate, bookingStatus).stream().map(bookingMapper::toDto).toList();
        }
    }

    @Override
    public void getAllAvailableTimeSlots(Integer capacity) {
        List<Tables> tables=tableRepository.findAllByDeletedAndCapacity(false,capacity);
        List<Booking> bookings=new ArrayList<>();
        ZonedDateTime.of(LocalDate.now(zoneId), LocalTime.of(9,0),zoneId);
        ZonedDateTime.of(LocalDate.now(zoneId),LocalTime.of(22,0),zoneId);
        for(Tables table:tables){
            bookings.addAll(bookingRepository.findAllByTableId(table.getId()));
        }
    }


}
