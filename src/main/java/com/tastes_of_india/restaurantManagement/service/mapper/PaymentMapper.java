package com.tastes_of_india.restaurantManagement.service.mapper;

import com.tastes_of_india.restaurantManagement.domain.Payment;
import com.tastes_of_india.restaurantManagement.service.dto.PaymentDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",uses = {})
public interface PaymentMapper extends EntityMapper<PaymentDTO, Payment> {

}
