package com.tastes_of_india.restaurantManagement.service.payment;

public class UPIPaymentStrategy implements PaymentStrategy{
    @Override
    public String processPayment(double amount) {
        return "Payment process successfully with UPI";
    }
}
