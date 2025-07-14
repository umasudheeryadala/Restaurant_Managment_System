package com.tastes_of_india.restaurantManagement.service.payment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentProcessor {

    private final Logger LOG= LoggerFactory.getLogger(PaymentProcessor.class);
    private PaymentStrategy paymentStrategy;
    public PaymentProcessor(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }

    public void processPayment(double amount) {
        paymentStrategy.processPayment(amount);
        LOG.debug("Payment Successful with amount: {} ",amount);
    }

    public void setPaymentStrategy(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
    }
}
