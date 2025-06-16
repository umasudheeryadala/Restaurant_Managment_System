package com.tastes_of_india.restaurantManagement.domain;

import com.tastes_of_india.restaurantManagement.domain.enumeration.PaymentStatus;
import com.tastes_of_india.restaurantManagement.domain.enumeration.PaymentType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Arrays;

@Entity
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "bill_url")
    private String billUrl;

    @Column(name = "total_amount",precision =10 ,scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentType paymentTYpe;

    @Column(name = "payment_time")
    private ZonedDateTime paymentTime;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    @ManyToOne
    private Order order;

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

    public PaymentType getPaymentTYpe() {
        return paymentTYpe;
    }

    public void setPaymentTYpe(PaymentType paymentTYpe) {
        this.paymentTYpe = paymentTYpe;
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

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "Payments{" +
                "id=" + id +
                ", billImage=" + billUrl +
                ", totalAmount=" + totalAmount +
                ", paymentTYpe=" + paymentTYpe +
                ", paymentTime=" + paymentTime +
                ", paymentStatus=" + paymentStatus +
                ", order=" + order +
                '}';
    }
}
