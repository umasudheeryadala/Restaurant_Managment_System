package com.tastes_of_india.restaurantManagement.service.impl;

import com.tastes_of_india.restaurantManagement.domain.Employee;
import com.tastes_of_india.restaurantManagement.domain.Restaurant;
import com.tastes_of_india.restaurantManagement.domain.Tables;
import com.tastes_of_india.restaurantManagement.domain.enumeration.TableStatus;
import com.tastes_of_india.restaurantManagement.repository.RestaurantRepository;
import com.tastes_of_india.restaurantManagement.repository.TableRepository;
import com.tastes_of_india.restaurantManagement.service.EmployeeService;
import com.tastes_of_india.restaurantManagement.service.TableService;
import com.tastes_of_india.restaurantManagement.service.dto.TableBasicDTO;
import com.tastes_of_india.restaurantManagement.service.dto.TableDTO;
import com.tastes_of_india.restaurantManagement.service.mapper.TableBasicMapper;
import com.tastes_of_india.restaurantManagement.service.mapper.TableMapper;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@Transactional
public class TableServiceImpl implements TableService {

    private final Logger LOG = LoggerFactory.getLogger(TableServiceImpl.class);

    private final String ENTITY_NAME = "TableService";

    private final TableRepository tableRepository;

    private final TableBasicMapper tableBasicMapper;

    private final EmployeeService employeeService;

    private final RestaurantRepository restaurantRepository;

    private final TableMapper tableMapper;


    public TableServiceImpl(TableRepository tableRepository, TableBasicMapper tableMapper, EmployeeService employeeService, RestaurantRepository restaurantRepository, TableMapper tableMapper1) {
        this.tableRepository = tableRepository;
        this.tableBasicMapper = tableMapper;
        this.employeeService = employeeService;
        this.restaurantRepository = restaurantRepository;
        this.tableMapper = tableMapper1;
    }

    @Override
    public void updateTableStatus(Long restaurantId,Long tableId, TableStatus tableStatus) throws BadRequestAlertException {
        Tables table;
        if (tableStatus.equals(TableStatus.OCCUPIED)) {
            table = tableRepository.findByIdAndRestaurantIdAndDeletedAndStatus(tableId,restaurantId ,false, TableStatus.AVAILABLE).orElseThrow(
                    () -> new BadRequestAlertException("Table Not Available", ENTITY_NAME, "tableNotAvailable")
            );
        } else if (tableStatus.equals(TableStatus.AVAILABLE)) {
            table = tableRepository.findByIdAndRestaurantIdAndDeleted(tableId,restaurantId, false).orElseThrow(
                    () -> new BadRequestAlertException("Table Not Available", ENTITY_NAME, "tableNotAvailable")
            );
        } else {
            throw new BadRequestAlertException("Table status misMatch", ENTITY_NAME, "tableStatusMisMatch");
        }
        table.setStatus(tableStatus);
        tableRepository.save(table);
    }

    @Override
    public void deleteTable(Long restaurantId,Long tableId) throws BadRequestAlertException {
        Optional<Tables> optionalTable = tableRepository.findByIdAndRestaurantIdAndDeletedAndStatus(tableId,restaurantId,false,TableStatus.AVAILABLE);
        if (optionalTable.isEmpty()) {
            throw new BadRequestAlertException("Table not Found", ENTITY_NAME, "tableNotFound");
        }

        Tables table = optionalTable.get();

        table.setDeleted(true);
        tableRepository.saveAndFlush(table);
    }

    @Override
    public TableBasicDTO saveTable(TableBasicDTO tableBasicDTO, Long restaurantId) throws BadRequestAlertException {
        Restaurant restaurant = restaurantRepository.findByIdAndDeleted(restaurantId, false).orElseThrow(
                () -> new BadRequestAlertException("Restaurant Id Not Found", ENTITY_NAME, "idNotFound")
        );
        if (tableBasicDTO.getId() != null) {
            Tables table = tableRepository.findByIdAndDeleted(tableBasicDTO.getId(), false).orElseThrow(
                    () -> new BadRequestAlertException("Table with Id Not Found", ENTITY_NAME, "tableNotFound")
            );
            if (!table.getName().equals(tableBasicDTO.getName())) {
                Optional<Tables> optionalTable = tableRepository.findByRestaurantIdAndNameAndDeleted(restaurantId,tableBasicDTO.getName(), false);
                if (optionalTable.isPresent()) {
                    throw new BadRequestAlertException("Table With Updated Name Already Exists", ENTITY_NAME, "tableAlreadyExists");
                }
            }
            tableBasicMapper.partialUpdate(table, tableBasicDTO);
            return tableBasicMapper.toDto(tableRepository.saveAndFlush(table));

        }
        Optional<Tables> optionalTable = tableRepository.findByRestaurantIdAndNameAndDeleted(restaurantId,tableBasicDTO.getName(), false);

        if (optionalTable.isPresent()) {
            throw new BadRequestAlertException("Table With Name Already Exists", ENTITY_NAME, "tableAlreadyExists");
        }

        Employee employee = employeeService.getCurrentlyLoggedInUser();
        Tables table = new Tables();

        tableBasicMapper.partialUpdate(table, tableBasicDTO);
        table.setRestaurant(restaurant);
        table.setCreatedBy(employee);
        table.setCreatedDate(ZonedDateTime.now());
        tableRepository.save(table);
        return tableBasicMapper.toDto(table);
    }

    @Override
    public Page<TableBasicDTO> getAllTables(Long restaurantId, TableStatus tableStatus, Pageable pageable) {
        Page<Tables> tables = null;
        if (tableStatus == null) {
            tables = tableRepository.findAllByRestaurantIdAndDeleted(restaurantId, false, pageable);
        } else {
            tables = tableRepository.findAllByRestaurantIdAndDeletedAndStatus(restaurantId, false, tableStatus, pageable);
        }
        return tables.map(tableBasicMapper::toDto);
    }

    @Override
    public TableDTO findTableById(Long tableId) throws BadRequestAlertException {
        Tables table=tableRepository.findByIdAndDeleted(tableId,false).orElseThrow(
                () -> new BadRequestAlertException("Table Not Found",ENTITY_NAME,"tableNotFound")
        );

        return tableMapper.toDto(table);
    }
}
