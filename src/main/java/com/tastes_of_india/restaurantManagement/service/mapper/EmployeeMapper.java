package com.tastes_of_india.restaurantManagement.service.mapper;

import com.tastes_of_india.restaurantManagement.domain.Employee;
import com.tastes_of_india.restaurantManagement.service.dto.EmployeeBasicDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring" , uses = {})
public interface EmployeeMapper extends EntityMapper<EmployeeBasicDTO, Employee> {

}
