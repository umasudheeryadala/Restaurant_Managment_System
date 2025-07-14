package com.tastes_of_india.restaurantManagement.web.rest;

import com.tastes_of_india.restaurantManagement.service.util.FileUtil;
import com.tastes_of_india.restaurantManagement.domain.enumeration.OrderType;
import com.tastes_of_india.restaurantManagement.domain.enumeration.PaymentType;
import com.tastes_of_india.restaurantManagement.domain.enumeration.TableStatus;
import com.tastes_of_india.restaurantManagement.service.*;
import com.tastes_of_india.restaurantManagement.service.dto.*;
import com.tastes_of_india.restaurantManagement.service.util.PaginationUtil;
import com.tastes_of_india.restaurantManagement.web.rest.error.BadRequestAlertException;
import com.tastes_of_india.restaurantManagement.web.rest.error.InvalidSessionException;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/public")
public class PublicResource {

    private final Logger LOG = LoggerFactory.getLogger(PublicResource.class);

    private final String ENTITY_NAME = "publicResource";

    @Autowired
    private MenuItemService menuItemService;

    @Autowired
    private MenuCategoryService menuCategoryService;

    @Autowired
    private ValidateService validateService;

    @Autowired
    private CartService cartService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private TableService tableService;

    @Autowired
    private RestaurantService restaurantService;


    @GetMapping("restaurants/{restaurantId}")
    public ResponseEntity<RestaurantDTO> getRestaurantDetails(@PathVariable Long restaurantId) throws BadRequestAlertException {
        return ResponseEntity.ok(restaurantService.findByRestaurantById(restaurantId));
    }


    //Generating Auth Token Which Acts As A Session Token
    @GetMapping("/restaurants/{restaurantId}/tables/{tableId}/welcome")
    public ResponseEntity<String> getAuthenticationToken(@PathVariable Long restaurantId,@PathVariable Long tableId,@RequestParam(required = false) Boolean session, HttpServletRequest servletRequest) throws BadRequestAlertException {
        LOG.debug("session {}",session);
        try {
            AuthTokenDTO authTokenDTO=validateService.isSessionActive(servletRequest);
            if(authTokenDTO.getTableId().equals(tableId) && authTokenDTO.getRestaurantId().equals(restaurantId)) {
                return ResponseEntity.ok().body("/public/restaurants/"+authTokenDTO.getRestaurantId()+"/tables/+"+authTokenDTO.getTableId()+"/welcome");
            }else {
                if(session==null) {
                    throw new BadRequestAlertException("Invalid Session", ENTITY_NAME, "INVALID_SESSION");
                }
                if(!session){
                    return ResponseEntity.ok("/public/restaurants/"+authTokenDTO.getRestaurantId()+"/tables/+"+authTokenDTO.getTableId()+"/welcome");
                }
            }
        }catch (BadRequestAlertException badRequestAlertException){
            throw badRequestAlertException;
        }
        catch (Exception e){
            LOG.debug("Exception : {}",e.getMessage());
        }
        AuthTokenDTO authTokenDTO = new AuthTokenDTO();
        authTokenDTO.setTableId(tableId);
        authTokenDTO.setRestaurantId(restaurantId);
        tableService.updateTableStatus(restaurantId,tableId, TableStatus.OCCUPIED);
        ResponseCookie responseCookie = validateService.generateJWTToken(authTokenDTO);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body("/public/restaurants/"+authTokenDTO.getRestaurantId()+"/tables/+"+authTokenDTO.getTableId()+"/welcome");
    }

    // Get All Menu Items Based On Category I'd

    @GetMapping("/menu_categories/{categoryId}/menuItems")
    public ResponseEntity<List<MenuItemDTO>> getAllMenuItemsByCategory(@PathVariable Long categoryId,
                                                                       @RequestParam(required = false) String pattern,
                                                                       @RequestParam(required = false) Boolean veg,
                                                                       Pageable pageable,
                                                                       HttpServletRequest servletRequest) throws BadRequestException, BadRequestAlertException, InvalidSessionException {
        AuthTokenDTO authTokenDTO=validateService.isSessionActive(servletRequest);

        Page<MenuItemDTO> menuItems = menuItemService.getAllMenuItemsByCategoryId(authTokenDTO.getRestaurantId(), categoryId, false, true, veg, pattern, pageable);
        return ResponseEntity.ok(menuItems.getContent());
    }

    // Get All Menu Categories Based On RestaurantId

    @GetMapping("/menu_categories")
    public ResponseEntity<List<MenuCategoryDTO>> getAllMenuCategories(Pageable pageable,HttpServletRequest servletRequest) throws BadRequestAlertException, InvalidSessionException {

        AuthTokenDTO authTokenDTO=validateService.isSessionActive(servletRequest);

        Page<MenuCategoryDTO> page = menuCategoryService.findAllMenuCategories(authTokenDTO.getRestaurantId(),false, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);

        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    // Using Redis To Maintain Temporary Cart

    @PostMapping("/cart/addItems")
    public ResponseEntity<List<CartItemDTO>> addItemToCart(@RequestBody List<CartItemDTO> cartItems, HttpServletRequest servletRequest) throws BadRequestAlertException, InvalidSessionException {
        for (CartItemDTO cartItem : cartItems) {
            if (cartItem.getId() == null) {
                throw new BadRequestAlertException("Item id should not present", ENTITY_NAME, "itemItemNotPresent");
            }
        }
        AuthTokenDTO authTokenDTO=validateService.isSessionActive(servletRequest);

        Long tableId = authTokenDTO.getTableId();

        Long restaurantId= authTokenDTO.getRestaurantId();


        List<CartItemDTO> result = cartService.addItemsToCart(restaurantId,tableId, cartItems);

        return ResponseEntity.ok(result);
    }


    // Get All Cart Items

    @GetMapping("/cart")
    public ResponseEntity<List<CartItemDTO>> getAllCartItems(HttpServletRequest servletRequest) throws BadRequestAlertException, InvalidSessionException {
        AuthTokenDTO authTokenDTO=validateService.isSessionActive(servletRequest);

        Long tableId = authTokenDTO.getTableId();

        Long restaurantId= authTokenDTO.getRestaurantId();

        return ResponseEntity.ok(cartService.findAllCartItems(restaurantId,tableId));
    }

    // Delete Cart Item
    @DeleteMapping("/cart/{cartItemId}")
    public ResponseEntity<List<CartItemDTO>> deleteCartItem(@PathVariable Long cartItemId, HttpServletRequest servletRequest) throws BadRequestAlertException, InvalidSessionException {
        if (cartItemId == null) {
            throw new BadRequestAlertException("Item Id is empty", ENTITY_NAME, "idNull");
        }
        AuthTokenDTO authTokenDTO=validateService.isSessionActive(servletRequest);

        Long tableId = authTokenDTO.getTableId();

        Long restaurantId= authTokenDTO.getRestaurantId();

        return ResponseEntity.ok(cartService.deleteCartItem(restaurantId,tableId, cartItemId));
    }

    // Proceed Cart Items Create New Order or Add Items To Order

    @PostMapping("/orders/proceed/{orderType}")
    public ResponseEntity<OrderDTO> createOrder(@PathVariable OrderType orderType, HttpServletRequest servletRequest) throws BadRequestAlertException, InvalidSessionException {
        AuthTokenDTO authToken = validateService.isSessionActive(servletRequest);

        Long tableId= authToken.getTableId();

        Long restaurantId= authToken.getRestaurantId();

        Long orderId=authToken.getOrderId();

        OrderDTO  result = orderService.saveOrder(restaurantId,tableId,orderId, orderType);

        authToken.setOrderId(result.getId());
        ResponseCookie responseCookie = validateService.generateJWTToken(authToken);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(result);
    }


    // Cancel The ordered Item If the item placed with in 15 min and status is Ordered

    @PutMapping("/order_items/{itemId}/cancel")
    public ResponseEntity<OrderItemDTO> cancelOrderItem(@PathVariable Long itemId,HttpServletRequest servletRequest) throws BadRequestAlertException, InvalidSessionException {
        AuthTokenDTO authTokenDTO=validateService.isSessionActive(servletRequest);

        if(authTokenDTO.getOrderId()==null){
            throw new BadRequestAlertException("Order Details Not Found",ENTITY_NAME,"orderDetailsNotFound");
        }

        OrderItemDTO orderItem=orderItemService.cancelOrderItem(itemId);

        return ResponseEntity.ok(orderItem);
    }

    // Get Order Details
    @GetMapping("/orders")
    public ResponseEntity<OrderDTO> getOrderDetails(HttpServletRequest servletRequest) throws BadRequestAlertException, InvalidSessionException {

        AuthTokenDTO authTokenDTO=validateService.isSessionActive(servletRequest);

        if(authTokenDTO.getOrderId()==null){
            throw new BadRequestAlertException("Order Details Not Found",ENTITY_NAME,"orderDetailsNotFound");
        }

        return ResponseEntity.ok(orderService.getOrder(authTokenDTO.getTableId(),authTokenDTO.getOrderId()));

    }

    @GetMapping("/orders/{orderId}/order-items")
    public ResponseEntity<List<OrderItemDTO>> getOrderItems(@PathVariable Long orderId,HttpServletRequest servletRequest) throws BadRequestAlertException, InvalidSessionException {
        AuthTokenDTO authTokenDTO=validateService.isSessionActive(servletRequest);

        Long existingOrderId= authTokenDTO.getOrderId();

        if(!existingOrderId.equals(orderId)){
            throw new BadRequestAlertException("Order Not Found",ENTITY_NAME,"orderNotFound");
        }

        return ResponseEntity.ok(orderItemService.findAllOrderItemsByOrderId(orderId));
    }


    // Cancel the order with in 15 min and none of the order started preparing
    @PutMapping("/orders/cancelOrder")
    public ResponseEntity<String> cancelOrder(HttpServletRequest servletRequest) throws BadRequestAlertException, InvalidSessionException {
        AuthTokenDTO authTokenDTO=validateService.isSessionActive(servletRequest);

        if(authTokenDTO.getOrderId()==null){
            throw new BadRequestAlertException("Order Details Not Found",ENTITY_NAME,"orderDetailsNotFound");
        }

        orderService.cancelOrder(authTokenDTO.getTableId(),authTokenDTO.getOrderId());

        ResponseCookie responseCookie=validateService.inValidateSession(servletRequest);

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,responseCookie.toString()).body("Order Cancelled Successfully");
    }

    // Process the Order for Payment
    @PostMapping("/payments/{paymentType}")
    public ResponseEntity<PaymentDTO> createPayment(@PathVariable PaymentType paymentType, HttpServletRequest httpServletRequest) throws BadRequestAlertException, IOException, InvalidSessionException {
        AuthTokenDTO authTokenDTO=validateService.isSessionActive(httpServletRequest);
        if(authTokenDTO.getOrderId()!=null && authTokenDTO.getRestaurantId()!=null && authTokenDTO.getTableId()!=null){
            ResponseCookie responseCookie=validateService.inValidateSession(httpServletRequest);
            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,responseCookie.toString()).body(paymentService.pay(authTokenDTO.getOrderId(),authTokenDTO.getTableId(), authTokenDTO.getRestaurantId(), paymentType));
        }else{
            throw new BadRequestAlertException("Payment Cannot be Processed",ENTITY_NAME,"paymentUnSuccessFull");
        }
    }

    @GetMapping("/proceed/payment")
    public ResponseEntity<Void> proceedForPayment(HttpServletRequest httpServletRequest) throws BadRequestAlertException, InvalidSessionException {
        AuthTokenDTO authTokenDTO=validateService.isSessionActive(httpServletRequest);
        if(authTokenDTO.getOrderId()!=null && authTokenDTO.getRestaurantId()!=null && authTokenDTO.getTableId()!=null){
            paymentService.checkOut(authTokenDTO.getOrderId(),authTokenDTO.getTableId(), authTokenDTO.getRestaurantId());
            return ResponseEntity.ok().build();
        }else{
            throw new BadRequestAlertException("Payment Cannot be Processed",ENTITY_NAME,"paymentUnSuccessFull");
        }
    }

    // Based On the paymentId get the payment receipt
    @GetMapping("/payments/{paymentId}/receipt")
    public ResponseEntity<byte[]> getPaymentReceipt(@PathVariable Long paymentId) throws BadRequestAlertException, IOException {
        byte[] result=paymentService.getPaymentReceipt(paymentId);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(result);
    }


    // To fetch menu Item Images
    @GetMapping("/images/{fileName}")
    public ResponseEntity<byte[]> getImage(@PathVariable String fileName) throws BadRequestAlertException, IOException {
        byte[] result= FileUtil.getFile(fileName);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(result);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void>  logoutSession(HttpServletRequest servletRequest) throws BadRequestAlertException, InvalidSessionException {
        ResponseCookie responseCookie=validateService.inValidateSession(servletRequest);
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE,responseCookie.toString()).build();
    }

}
