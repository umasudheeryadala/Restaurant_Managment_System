package com.tastes_of_india.restaurantManagement.service.dto;

import com.tastes_of_india.restaurantManagement.domain.enumeration.PaymentStatus;
import com.tastes_of_india.restaurantManagement.domain.enumeration.PaymentType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public class PaymentDTO {

    private Long id;

    private String billUrl;

    private BigDecimal totalAmount;

    private PaymentType paymentType;

    private ZonedDateTime paymentTime;

    private PaymentStatus paymentStatus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBillUrl() {
        return billUrl;
    }

    public void setBillUrl(String billUrl) {
        this.billUrl = billUrl;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentTYpe) {
        this.paymentType = paymentTYpe;
    }

    public ZonedDateTime getPaymentTime() {
        return paymentTime;
    }

    public void setPaymentTime(ZonedDateTime paymentTime) {
        this.paymentTime = paymentTime;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
        return "PaymentDTO{" +
                "id=" + id +
                ", billUrl='" + billUrl + '\'' +
                ", totalAmount=" + totalAmount +
                ", paymentTYpe=" + paymentType +
                ", paymentTime=" + paymentTime +
                ", paymentStatus=" + paymentStatus +
                '}';
    }
}
