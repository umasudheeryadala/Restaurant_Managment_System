package com.tastes_of_india.restaurantManagement.service;

import com.tastes_of_india.restaurantManagement.domain.enumeration.TableStatus;
import com.tastes_of_india.restaurantManagement.service.dto.TableBasicDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TableService {

    void updateTableStatus(Long restaurantId,Long tableId, TableStatus tableStatus) throws BadRequestAlertException;

    void deleteTable(Long restaurantId,Long tableId) throws BadRequestAlertException;

    TableBasicDTO saveTable(TableBasicDTO tableBasicDTO,Long restaurantId) throws BadRequestAlertException;

    Page<TableBasicDTO> getAllTables(Long restaurantId,TableStatus tableStatus, Pageable pageable);
}
