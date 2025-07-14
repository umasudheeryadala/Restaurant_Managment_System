package com.tastes_of_india.restaurantManagement.service.payment;

import com.tastes_of_india.restaurantManagement.domain.enumeration.PaymentType;

public class PaymentFactory {

    public  static final PaymentFactory instance=new PaymentFactory();

    private PaymentProcessor paymentProcessor;

    private PaymentFactory(){
        paymentProcessor=new PaymentProcessor(new CashPaymentStrategy());
    }

    public PaymentProcessor getPaymentProcessor(PaymentType paymentType){
        switch (paymentType){
            case UPI -> {
                paymentProcessor.setPaymentStrategy(new UPIPaymentStrategy());
            }
            case CARD -> {
                paymentProcessor.setPaymentStrategy(new CardPaymentStrategy());
            }
            default -> {
                paymentProcessor.setPaymentStrategy(new CashPaymentStrategy());
            }
        }
        return paymentProcessor;
    }

}
