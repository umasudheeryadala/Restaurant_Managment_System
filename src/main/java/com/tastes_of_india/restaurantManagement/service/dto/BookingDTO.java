package com.tastes_of_india.restaurantManagement.service.dto;

import com.tastes_of_india.restaurantManagement.domain.enumeration.BookingStatus;

import java.time.ZonedDateTime;
import java.util.Map;

public class BookingDTO {

    private Long id;

    private ZonedDateTime startDateTime;

    private ZonedDateTime endDateTime;

    private BookingStatus status;

    private ZonedDateTime createdDate;

    private Map<String,Object> bookingDetails;

    private TableBasicDTO table;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(ZonedDateTime startDateTime) {
        this.startDateTime = startDateTime;
    }

    public ZonedDateTime getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(ZonedDateTime endDateTime) {
        this.endDateTime = endDateTime;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public ZonedDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(ZonedDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public Map<String, Object> getBookingDetails() {
        return bookingDetails;
    }

    public void setBookingDetails(Map<String, Object> bookingDetails) {
        this.bookingDetails = bookingDetails;
    }

    public TableBasicDTO getTable() {
        return table;
    }

    public void setTable(TableBasicDTO table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return "BookingDTO{" +
                "id=" + id +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", status=" + status +
                ", createdDate=" + createdDate +
                ", bookingDetails=" + bookingDetails +
                '}';
    }
}
