package com.tastes_of_india.restaurantManagement.service;

import com.tastes_of_india.restaurantManagement.domain.enumeration.PaymentType;
import com.tastes_of_india.restaurantManagement.service.dto.PaymentDTO;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import io.minio.errors.*;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public interface PaymentService {

    PaymentDTO pay(Long orderId, Long tableId, Long restaurantId, PaymentType paymentType) throws BadRequestAlertException, IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    void checkOut(Long orderId, Long tableId, Long restaurantId) throws BadRequestAlertException;

    byte[] getPaymentReceipt(Long paymentId) throws BadRequestAlertException, IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;

    List<PaymentDTO> findAllByOrderId(Long restaurantId, Long orderId) throws BadRequestAlertException;
}
