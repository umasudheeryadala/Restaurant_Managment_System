package com.tastes_of_india.restaurantManagement.web.rest;

import com.tastes_of_india.restaurantManagement.service.TableService;
import com.tastes_of_india.restaurantManagement.service.dto.TableDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TableResource {

    private final Logger LOG= LoggerFactory.getLogger(TableResource.class);

    private final String ENTITY_NAME="tableResource";

    @Autowired
    private TableService tableService;

    @GetMapping("/tables/{tableId}")
    public ResponseEntity<TableDTO> getTable(@PathVariable Long tableId) throws BadRequestAlertException {
        TableDTO table=tableService.findTableById(tableId);
        return ResponseEntity.ok(table);
    }
}
