package com.tastes_of_india.restaurantManagement.web.rest;

import com.tastes_of_india.restaurantManagement.service.PaymentService;
import com.tastes_of_india.restaurantManagement.service.dto.PaymentDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PaymentResource {

    private final Logger LOG= LoggerFactory.getLogger(PaymentResource.class);

    private final String ENTITY_NAME="paymentResource";

    @Autowired
    private PaymentService paymentService;

    @GetMapping("/restaurants/{restaurantId}/orders/{orderId}/payments")
    public ResponseEntity<List<PaymentDTO>> getAllPaymentsByOrderId(@PathVariable Long restaurantId,@PathVariable Long orderId) throws BadRequestAlertException {
        List<PaymentDTO> payments=paymentService.findAllByOrderId(restaurantId,orderId);
        return ResponseEntity.ok(payments);
    }
}
