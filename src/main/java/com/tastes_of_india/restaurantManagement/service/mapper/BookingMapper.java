package com.tastes_of_india.restaurantManagement.service.mapper;

import com.tastes_of_india.restaurantManagement.domain.Booking;
import com.tastes_of_india.restaurantManagement.service.dto.BookingDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring", uses = {TableBasicMapper.class})
public interface BookingMapper extends EntityMapper<BookingDTO, Booking> {
}
