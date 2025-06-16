package com.tastes_of_india.restaurantManagement.service.impl;

import com.tastes_of_india.restaurantManagement.config.Constants;
import com.tastes_of_india.restaurantManagement.service.util.FileUtil;
import com.tastes_of_india.restaurantManagement.domain.*;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderItemStatus;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderStatus;
import com.tastes_of_india.restaurantManagement.domain.enumeration.PaymentStatus;
import com.tastes_of_india.restaurantManagement.domain.enumeration.PaymentType;
import com.tastes_of_india.restaurantManagement.repository.OrderRepository;
import com.tastes_of_india.restaurantManagement.repository.PaymentRepository;
import com.tastes_of_india.restaurantManagement.repository.RestaurantRepository;
import com.tastes_of_india.restaurantManagement.service.OrderService;
import com.tastes_of_india.restaurantManagement.service.PaymentService;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.Objects;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final Logger LOG= LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final String ENTITY_NAME="paymentServiceImpl";

    private final String pdfFileName="payment_receipt.pdf";

    private final RestaurantRepository restaurantRepository;

    private final OrderRepository orderRepository;

    private final PaymentRepository paymentRepository;

    private final OrderService orderService;


    public PaymentServiceImpl(RestaurantRepository restaurantRepository, OrderRepository orderRepository, PaymentRepository paymentRepository, OrderService orderService) {
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
    }

    @Override
    public byte[] createPayment(Long orderId, Long tableId, Long restaurantId, PaymentType paymentType) throws BadRequestAlertException, IOException {
        Restaurant restaurant=restaurantRepository.findByIdAndDeleted(restaurantId,false).orElseThrow(
                () -> new BadRequestAlertException("Restaurant Not Found",ENTITY_NAME,"restaurantNotFound")
        );
        Order order=orderRepository.findByIdAndStatus(orderId, OrderStatus.IN_PROGRESS).orElseThrow(
                () -> new BadRequestAlertException("Order unable to process for payment",ENTITY_NAME,"unableToProcess")
        );

        for(OrderItem orderItem:order.getOrderItems()){
            if(Arrays.asList(OrderItemStatus.ORDERED,OrderItemStatus.PREPARING,OrderItemStatus.CANCELLED).contains(orderItem.getStatus())){
                throw new BadRequestAlertException("Some of the Order Items are Not Delivered",ENTITY_NAME,"itemsNotDelivered");
            }
        }
        Payment payment=new Payment();
        payment.setPaymentStatus(PaymentStatus.CREATED);
        payment.setPaymentTYpe(paymentType);
        payment.setOrder(order);
        payment.setPaymentTime(ZonedDateTime.now());
        paymentRepository.save(payment);
        byte[] result=generateBill(restaurant,order,payment);
        orderService.updateOrderStatus(orderId,OrderStatus.DELIVERED);
        return  result;
    }

    @Override
    public byte[] getPaymentReceipt(Long paymentId) throws BadRequestAlertException, IOException {
        Payment payment=paymentRepository.findById(paymentId).orElseThrow(
                () -> new BadRequestAlertException("Payment With Id Not Found",ENTITY_NAME,"paymentNotFound")
        );
        byte[] result= FileUtil.getFile(payment.getBillUrl());
        return result;
    }

    private byte[] generateBill(Restaurant restaurant,Order order,Payment payment) throws IOException, BadRequestAlertException {
        String htmlTemplate = new String(
                Objects.requireNonNull(getClass().getResourceAsStream("/templates/order-receipt.html")).readAllBytes(),
                StandardCharsets.UTF_8);

        StringBuilder itemRows = new StringBuilder();
        int totalAmount=0;
        for (OrderItem item : order.getOrderItems()) {
            if(item.getStatus().equals(OrderItemStatus.CANCELLED)){
                continue;
            }
            MenuItem menuItem=item.getItem();
            itemRows.append("<tr>")
                    .append("<td>").append(order.getId()).append("</td>")
                    .append("<td>").append(menuItem.getName()).append("</td>")
                    .append("<td>").append(item.getQuantity()).append("</td>")
                    .append("<td>").append("₹").append(menuItem.getPrice()).append("</td>")
                    .append("<td>").append("₹").append(item.getQuantity() * menuItem.getPrice()).append("</td>")
                    .append("</tr>");
            totalAmount+=(item.getQuantity()*menuItem.getPrice());
        }

        String image= Base64.getEncoder().encodeToString(FileUtil.getFile(restaurant.getLogoUrl()));
        // Replace placeholders
        String filledHtml = htmlTemplate
                .replace("${image}",image)
                .replace("${restaurantName}", restaurant.getName())
                .replace("${restaurantAddress}",restaurant.getAddress())
                .replace("${inVoiceNo}",String.valueOf(payment.getId()))
                .replace("${orderId}", String.valueOf(order.getId()))
                .replace("${orderDate}", order.getCreatedDate().toLocalDate().toString())
                .replace("${tableName}", String.valueOf(order.getTable().getName()))
                .replace("${itemRows}", itemRows.toString())
                .replace("${totalAmount}", String.valueOf(totalAmount));

        LOG.debug("byte data: {} ",filledHtml);

        // Convert HTML to PDF
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ITextRenderer renderer=new ITextRenderer();
        renderer.setDocumentFromString(filledHtml);
        renderer.layout();
        renderer.createPDF(baos);
        renderer.finishPDF();
        String fileName= FileUtil.saveFile(baos.toByteArray(), payment.getId()+ Constants.FILE_SEPARATOR +pdfFileName);
        LOG.debug("PDF file name: {}",fileName);
        payment.setBillUrl(fileName);
        payment.setTotalAmount(BigDecimal.valueOf(totalAmount));
        paymentRepository.saveAndFlush(payment);
        return baos.toByteArray();
    }
}
