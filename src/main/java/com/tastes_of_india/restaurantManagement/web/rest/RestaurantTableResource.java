package com.tastes_of_india.restaurantManagement.web.rest;

import com.tastes_of_india.restaurantManagement.annotation.AuthorizeApiAccess;
import com.tastes_of_india.restaurantManagement.domain.enumeration.Designation;
import com.tastes_of_india.restaurantManagement.domain.enumeration.TableStatus;
import com.tastes_of_india.restaurantManagement.service.TableService;
import com.tastes_of_india.restaurantManagement.service.dto.TableBasicDTO;
import com.tastes_of_india.restaurantManagement.service.dto.TableDTO;
import com.tastes_of_india.restaurantManagement.service.util.PaginationUtil;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.support.PageableUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.HandlerAdapter;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class RestaurantTableResource {

    private final Logger LOG = LoggerFactory.getLogger(RestaurantTableResource.class);

    private final String ENTITY_NAME = "restaurantTableResource";

    @Autowired
    private TableService tableService;

    @GetMapping("/restaurants/{restaurantId}/tables")
    public ResponseEntity<List<TableBasicDTO>> getAllTables(@PathVariable Long restaurantId, @RequestParam(required = false) TableStatus tableStatus, Pageable pageable) {
        Page<TableBasicDTO> page = tableService.getAllTables(restaurantId,tableStatus, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }


    @AuthorizeApiAccess(designation = {Designation.OWNER,Designation.MANAGER})
    @PostMapping("/restaurants/{restaurantId}/tables")
    public ResponseEntity<TableBasicDTO> saveRestaurantTable(@PathVariable Long restaurantId, @RequestBody TableBasicDTO table) throws BadRequestAlertException {
        TableBasicDTO result = tableService.saveTable(table, restaurantId);
        return ResponseEntity.ok(result);
    }

    @AuthorizeApiAccess(designation = {Designation.MANAGER,Designation.OWNER})
    @PutMapping("restaurants/{restaurantId}/tables/{tableId}")
    public ResponseEntity<TableBasicDTO> updateRestaurantTable(@PathVariable Long restaurantId,@PathVariable Long tableId, @RequestBody TableBasicDTO tableBasicDTO) throws BadRequestAlertException {
        if (tableBasicDTO.getId() == null) {
            throw new BadRequestAlertException("Id Not Found", ENTITY_NAME, "idNotFound");
        }

        if (!Objects.equals(tableId, tableBasicDTO.getId())) {
            throw new BadRequestAlertException("InValid Table Id", ENTITY_NAME, "inValidId");
        }

        TableBasicDTO result = tableService.saveTable(tableBasicDTO, restaurantId);
        return ResponseEntity.ok(result);
    }

    @AuthorizeApiAccess(designation = {Designation.MANAGER,Designation.RECEPTIONIST,Designation.OWNER})
    @PutMapping("/restaurants/{restaurantId}/tables/{tableId}/update_status/{tableStatus}")
    public ResponseEntity<Void> updateTableStatus(@PathVariable Long restaurantId,@PathVariable Long tableId, @PathVariable TableStatus tableStatus) throws BadRequestAlertException {
        tableService.updateTableStatus(restaurantId,tableId,tableStatus);
        return ResponseEntity.ok().build();
    }

    @AuthorizeApiAccess(designation = {Designation.MANAGER,Designation.OWNER})
    @DeleteMapping("/restaurants/{restaurantId}/tables/{tableId}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long restaurantId,@PathVariable Long tableId) throws BadRequestAlertException {
        tableService.deleteTable(restaurantId,tableId);
        return ResponseEntity.ok().build();
    }

}
