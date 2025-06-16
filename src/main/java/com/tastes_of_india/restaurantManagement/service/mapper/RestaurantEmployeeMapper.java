package com.tastes_of_india.restaurantManagement.service.mapper;

import com.tastes_of_india.restaurantManagement.domain.RestaurantEmployee;
import com.tastes_of_india.restaurantManagement.service.dto.RestaurantEmployeeDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring" , uses = {EmployeeMapper.class})
public interface RestaurantEmployeeMapper extends EntityMapper<RestaurantEmployeeDTO,RestaurantEmployee>{

    @Mapping(target = "employeeId",source = "employee.id")
    @Mapping(target = "firstName",source = "employee.firstName")
    @Mapping(target = "lastName", source = "employee.lastName")
    @Mapping(target = "email",source = "employee.email")
    RestaurantEmployeeDTO toDto(RestaurantEmployee restaurantEmployee);

}
