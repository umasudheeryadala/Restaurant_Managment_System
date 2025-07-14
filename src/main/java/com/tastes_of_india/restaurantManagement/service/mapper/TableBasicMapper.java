package com.tastes_of_india.restaurantManagement.service.mapper;


import com.tastes_of_india.restaurantManagement.domain.Tables;
import com.tastes_of_india.restaurantManagement.service.dto.TableBasicDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring" , uses = {OrderMapper.class, EmployeeMapper.class})
public interface TableBasicMapper extends EntityMapper<TableBasicDTO, Tables>{

    TableBasicDTO toDto(Tables tables);

    @Named("id")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TableBasicDTO toDtoId(Tables tables);

}
