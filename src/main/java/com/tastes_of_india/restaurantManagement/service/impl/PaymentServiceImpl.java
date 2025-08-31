package com.tastes_of_india.restaurantManagement.service.impl;

import com.tastes_of_india.restaurantManagement.config.Constants;
import com.tastes_of_india.restaurantManagement.service.FileService;
import com.tastes_of_india.restaurantManagement.service.OrderItemService;
import com.tastes_of_india.restaurantManagement.service.dto.MenuItemDTO;
import com.tastes_of_india.restaurantManagement.service.dto.OrderItemDTO;
import com.tastes_of_india.restaurantManagement.service.dto.PaymentDTO;
import com.tastes_of_india.restaurantManagement.service.mapper.PaymentMapper;
import com.tastes_of_india.restaurantManagement.service.payment.PaymentFactory;
import com.tastes_of_india.restaurantManagement.service.payment.PaymentProcessor;
import com.tastes_of_india.restaurantManagement.service.util.storage.LocalStorage;
import com.tastes_of_india.restaurantManagement.domain.*;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderItemStatus;
import com.tastes_of_india.restaurantManagement.domain.enumeration.PaymentStatus;
import com.tastes_of_india.restaurantManagement.domain.enumeration.PaymentType;
import com.tastes_of_india.restaurantManagement.repository.OrderRepository;
import com.tastes_of_india.restaurantManagement.repository.PaymentRepository;
import com.tastes_of_india.restaurantManagement.repository.RestaurantRepository;
import com.tastes_of_india.restaurantManagement.service.OrderService;
import com.tastes_of_india.restaurantManagement.service.PaymentService;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import io.minio.errors.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.List;
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

    private final PaymentMapper paymentMapper;

    private final OrderItemService orderItemService;

    private final FileService fileService;


    public PaymentServiceImpl(RestaurantRepository restaurantRepository, OrderRepository orderRepository, PaymentRepository paymentRepository, OrderService orderService, PaymentMapper paymentMapper, OrderItemService orderItemService, FileService fileService) {
        this.restaurantRepository = restaurantRepository;
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.paymentMapper = paymentMapper;
        this.orderItemService = orderItemService;
        this.fileService = fileService;
    }

    @Override
    public PaymentDTO pay(Long orderId, Long tableId, Long restaurantId, PaymentType paymentType) throws BadRequestAlertException, IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Restaurant restaurant=restaurantRepository.findByIdAndDeleted(restaurantId,false).orElseThrow(
                () -> new BadRequestAlertException("Restaurant Not Found",ENTITY_NAME,"restaurantNotFound")
        );

        Order order=orderRepository.findById(orderId).orElseThrow(
                ()-> new BadRequestAlertException("Order not Found",ENTITY_NAME,"orderNotFound")
        );
        orderService.canProcessedForPayment(orderId);
        orderItemService.checkStatusOfOrderItems(orderId);
        double amount=getTotalAmount(orderItemService.findAllOrderItemsByOrderId(orderId));
        Payment payment=processPayment(order,amount,paymentType);
        orderService.updateOrderStatus(orderId);
        generateBill(restaurant,order,payment);
        return  paymentMapper.toDto(payment);
    }

    @Override
    public void checkOut(Long orderId, Long tableId, Long restaurantId) throws BadRequestAlertException {
        restaurantRepository.findByIdAndDeleted(restaurantId,false).orElseThrow(
                () -> new BadRequestAlertException("Restaurant Not Found",ENTITY_NAME,"restaurantNotFound")
        );
        orderService.canProcessedForPayment(orderId);
        orderItemService.checkStatusOfOrderItems(orderId);
    }

    @Override
    public byte[] getPaymentReceipt(Long paymentId) throws BadRequestAlertException, IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        Payment payment=paymentRepository.findById(paymentId).orElseThrow(
                () -> new BadRequestAlertException("Payment With Id Not Found",ENTITY_NAME,"paymentNotFound")
        );
        byte[] result= fileService.getFile(payment.getBillUrl());
        return result;
    }

    @Override
    public List<PaymentDTO> findAllByOrderId(Long restaurantId, Long orderId) throws BadRequestAlertException {

        orderRepository.findByIdAndTableRestaurantId(orderId,restaurantId).orElseThrow(
                () ->new BadRequestAlertException("Order Nor Found",ENTITY_NAME,"orderNotFound")
        );

        List<Payment> payments=paymentRepository.findAllByOrderId(orderId);

        return payments.stream().map(paymentMapper::toDto).toList();
    }

    private Payment processPayment(Order order,double amount,PaymentType paymentType) throws BadRequestAlertException {
        PaymentProcessor paymentProcessor= PaymentFactory.getInstance().getPaymentProcessor(paymentType);
        Payment payment=new Payment();
        payment.setTotalAmount(BigDecimal.valueOf(amount));
        payment.setOrder(order);
        payment.setPaymentType(paymentType);
        payment.setPaymentTime(ZonedDateTime.now());
        try {
            paymentProcessor.processPayment(amount);
            payment.setPaymentStatus(PaymentStatus.SUCCESS);
        }catch (Exception e){
            payment.setPaymentStatus(PaymentStatus.FAILURE);
            paymentRepository.saveAndFlush(payment);
            throw new BadRequestAlertException("Payment Failed",ENTITY_NAME,"paymentFailed");
        }
        paymentRepository.save(payment);
        return payment;
    }

    private byte[] generateBill(Restaurant restaurant,Order order,Payment payment) throws IOException, BadRequestAlertException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        String htmlTemplate = new String(
                Objects.requireNonNull(getClass().getResourceAsStream("/templates/order-receipt.html")).readAllBytes(),
                StandardCharsets.UTF_8);

        StringBuilder itemRows = new StringBuilder();
        List<OrderItemDTO> orderItemDTOS=orderItemService.findAllOrderItemsByOrderId(order.getId());
        double totalAmount=getTotalAmount(orderItemDTOS);
        for (OrderItemDTO item : orderItemDTOS) {
            if(item.getStatus().equals(OrderItemStatus.CANCELLED)){
                continue;
            }
            MenuItemDTO menuItem=item.getMenuItem();
            itemRows.append("<tr>")
                    .append("<td>").append(order.getId()).append("</td>")
                    .append("<td>").append(menuItem.getName()).append("</td>")
                    .append("<td>").append(item.getQuantity()).append("</td>")
                    .append("<td>").append("₹").append(menuItem.getPrice()).append("</td>")
                    .append("<td>").append("₹").append(item.getQuantity() * menuItem.getPrice()).append("</td>")
                    .append("</tr>");
        }

        String image= Base64.getEncoder().encodeToString(fileService.getFile(restaurant.getLogoUrl()));
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
        String fileName= fileService.saveFile(baos.toByteArray(), payment.getId()+ Constants.FILE_SEPARATOR +pdfFileName);
        LOG.debug("PDF file name: {}",fileName);
        payment.setBillUrl(fileName);
        payment.setTotalAmount(BigDecimal.valueOf(totalAmount));
        paymentRepository.saveAndFlush(payment);
        return baos.toByteArray();
    }

    private double getTotalAmount(List<OrderItemDTO> orderItems){
        double totalAmount=0;
        for(OrderItemDTO orderItem:orderItems){
            if(orderItem.getStatus()!=OrderItemStatus.CANCELLED)
                totalAmount+=orderItem.getQuantity()*orderItem.getMenuItem().getPrice();
        }
        return totalAmount;
    }
}
