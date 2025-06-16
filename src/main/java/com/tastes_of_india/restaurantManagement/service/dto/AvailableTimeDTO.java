package com.tastes_of_india.restaurantManagement.service.dto;

import java.time.ZonedDateTime;

public class AvailableTimeDTO {

    private ZonedDateTime startDateTime;

    private ZonedDateTime endDateTime;

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

    @Override
    public String toString() {
        return "AvailableTimeDTO{" +
                "startDateTime=" + startDateTime +
                ", endDateTime=" + endDateTime +
                '}';
    }
}
