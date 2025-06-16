package com.tastes_of_india.restaurantManagement.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tastes_of_india.restaurantManagement.domain.enumeration.BookingStatus;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.Map;

@Entity
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "start_date_time")
    private ZonedDateTime startDateTime;

    @Column(name = "end_date_time")
    private ZonedDateTime endDateTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(name = "createdDate")
    private ZonedDateTime createdDate;

    @Column(name = "booking_details")
    @Convert(converter = MapToStringConverter.class)
    private Map<String,Object> bookingDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"createdBy","orders"})
    private Tables table;

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

    public Tables getTable() {
        return table;
    }

    public void setTable(Tables table) {
        this.table = table;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                ", status=" + status +
                ", createdDate=" + createdDate +
                ", bookingDetails=" + bookingDetails +
                '}';
    }
}
