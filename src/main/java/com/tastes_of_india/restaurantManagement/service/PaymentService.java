package com.tastes_of_india.restaurantManagement.service;

import com.tastes_of_india.restaurantManagement.domain.enumeration.PaymentType;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;

import java.io.IOException;

public interface PaymentService {

    byte[] createPayment(Long orderId, Long tableId, Long restaurantId, PaymentType paymentType) throws BadRequestAlertException, IOException;

    byte[] getPaymentReceipt(Long paymentId) throws BadRequestAlertException, IOException;
}
