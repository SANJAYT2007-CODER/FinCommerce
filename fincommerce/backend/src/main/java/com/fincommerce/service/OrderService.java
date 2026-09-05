package com.fincommerce.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fincommerce.dto.CartDtos;
import com.fincommerce.dto.CheckoutDtos;
import com.fincommerce.dto.OrderDtos;
import com.fincommerce.entity.*;
import com.fincommerce.exception.BadRequestException;
import com.fincommerce.exception.InsufficientBalanceException;
import com.fincommerce.exception.ResourceNotFoundException;
import com.fincommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductService productService;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private ReturnRefundRepository returnRefundRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private FraudDetectionService fraudDetectionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public CheckoutDtos.OrderConfirmationResponse processCheckout(Long userId, CheckoutDtos.CheckoutRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        CartDtos.CartDto cartDto = cartService.getCartDto(userId, request.getCouponCode());
        if (cartDto.getItems() == null || cartDto.getItems().isEmpty()) {
            throw new BadRequestException("Shopping cart is empty");
        }

        BigDecimal finalTotal = cartDto.getFinalTotal();
        String paymentMethod = request.getPaymentMethod();
        String orderNum = "ORD-" + System.currentTimeMillis();
        String trackingNum = "TRK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String estDelivery = LocalDateTime.now().plusDays(3).format(DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy"));

        // If paying via Wallet
        if ("WALLET".equalsIgnoreCase(paymentMethod)) {
            if (user.getTransactionPin() != null && request.getPin() != null && !user.getTransactionPin().equals(request.getPin())) {
                throw new BadRequestException("Invalid Transaction PIN");
            }

            Wallet wallet = walletRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

            if (wallet.getAvailableBalance().compareTo(finalTotal) < 0) {
                throw new InsufficientBalanceException("Insufficient Wallet balance for order checkout");
            }

            wallet.setBalance(wallet.getBalance().subtract(finalTotal));
            wallet.setAvailableBalance(wallet.getAvailableBalance().subtract(finalTotal));
            wallet.setTotalSpent(wallet.getTotalSpent().add(finalTotal));
            wallet.setMonthlySpent(wallet.getMonthlySpent().add(finalTotal));
            walletRepository.save(wallet);

            // Record Wallet Transaction
            WalletTransaction tx = new WalletTransaction();
            tx.setTransactionId("TXN-ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            tx.setWallet(wallet);
            tx.setUser(user);
            tx.setType("SHOPPING");
            tx.setAmount(finalTotal);
            tx.setPaymentMethod("WALLET");
            tx.setStatus("SUCCESS");
            tx.setNote("Order Payment for " + orderNum);
            tx.setSenderInfo(user.getFullName() + " Wallet");
            tx.setReceiverInfo("FinCommerce Merchant");
            tx.setReferenceId(orderNum);
            transactionRepository.save(tx);
        }

        // Create Order Entity
        Order order = new Order();
        order.setOrderNumber(orderNum);
        order.setUser(user);
        order.setTotalAmount(cartDto.getSubtotal());
        order.setDiscountAmount(cartDto.getDiscountAmount());
        order.setDeliveryFee(cartDto.getDeliveryFee());
        order.setTaxAmount(cartDto.getTaxAmount());
        order.setFinalTotal(finalTotal);
        order.setPaymentMethod(paymentMethod);
        order.setPaymentStatus("CASH_ON_DELIVERY".equalsIgnoreCase(paymentMethod) ? "PENDING" : "PAID");
        order.setOrderStatus("CONFIRMED");
        order.setTrackingNumber(trackingNum);
        order.setEstimatedDelivery(estDelivery);

        try {
            order.setAddressJson(objectMapper.writeValueAsString(address));
        } catch (Exception e) {
            order.setAddressJson(address.getHouseFlat() + ", " + address.getStreet() + ", " + address.getCity());
        }

        Order savedOrder = orderRepository.save(order);

        // Convert cart items to order items
        List<OrderItem> orderItems = new ArrayList<>();
        Cart cart = cartService.getOrCreateCart(userId);
        for (CartItem item : cart.getItems()) {
            OrderItem orderItem = new OrderItem(savedOrder, item.getProduct(), item.getQuantity(), item.getProduct().getPrice());
            orderItems.add(orderItem);
        }
        savedOrder.setItems(orderItems);
        orderRepository.save(savedOrder);

        // Clear cart
        cart.getItems().clear();
        cartRepository.save(cart);

        notificationService.createNotification(
                user,
                "Order Placed Successfully!",
                "Order " + orderNum + " for ₹" + finalTotal + " has been placed and confirmed.",
                "ORDER_PLACED",
                "orders.html"
        );

        return new CheckoutDtos.OrderConfirmationResponse(
                orderNum,
                finalTotal,
                savedOrder.getPaymentStatus(),
                savedOrder.getOrderStatus(),
                trackingNum,
                estDelivery,
                "Thank you for shopping with FinCommerce!"
        );
    }

    public List<OrderDtos.OrderDto> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToOrderDto)
                .collect(Collectors.toList());
    }

    public OrderDtos.OrderDto getOrderDetails(Long userId, String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        if (!order.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Order not found for user");
        }
        return mapToOrderDto(order);
    }

    @Transactional
    public OrderDtos.OrderDto updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        order.setOrderStatus(newStatus);
        Order updated = orderRepository.save(order);

        notificationService.createNotification(
                order.getUser(),
                "Order Status Updated",
                "Your order " + order.getOrderNumber() + " is now " + newStatus.replace("_", " "),
                "ORDER_SHIPPED",
                "orders.html"
        );

        return mapToOrderDto(updated);
    }

    @Transactional
    public ReturnRefund requestReturn(Long userId, OrderDtos.ReturnRequestDto request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        ReturnRefund returnObj = new ReturnRefund();
        returnObj.setUser(user);
        returnObj.setOrder(order);
        returnObj.setReason(request.getReason());
        returnObj.setImageUrl(request.getImageUrl());
        returnObj.setStatus("APPROVED"); // Auto-approved for demo flow
        returnObj.setRefundAmount(order.getFinalTotal());

        ReturnRefund saved = returnRefundRepository.save(returnObj);

        // Refund automatically to User Wallet
        Wallet wallet = walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));

        wallet.setBalance(wallet.getBalance().add(order.getFinalTotal()));
        wallet.setAvailableBalance(wallet.getAvailableBalance().add(order.getFinalTotal()));
        walletRepository.save(wallet);

        // Record refund transaction
        WalletTransaction tx = new WalletTransaction();
        tx.setTransactionId("TXN-RFD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        tx.setWallet(wallet);
        tx.setUser(user);
        tx.setType("REFUND");
        tx.setAmount(order.getFinalTotal());
        tx.setPaymentMethod("WALLET");
        tx.setStatus("SUCCESS");
        tx.setNote("Refund for Order #" + order.getOrderNumber());
        tx.setSenderInfo("FinCommerce Merchant");
        tx.setReceiverInfo(user.getFullName() + " Wallet");
        tx.setReferenceId(order.getOrderNumber());
        transactionRepository.save(tx);

        order.setOrderStatus("RETURNED");
        order.setPaymentStatus("REFUNDED");
        orderRepository.save(order);

        notificationService.createNotification(
                user,
                "Refund Processed",
                "₹" + order.getFinalTotal() + " credited back to your FinCommerce wallet for Order #" + order.getOrderNumber(),
                "REFUND_PROCESSED",
                "wallet.html"
        );

        return saved;
    }

    private OrderDtos.OrderDto mapToOrderDto(Order order) {
        OrderDtos.OrderDto dto = new OrderDtos.OrderDto();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setDeliveryFee(order.getDeliveryFee());
        dto.setTaxAmount(order.getTaxAmount());
        dto.setFinalTotal(order.getFinalTotal());
        dto.setAddressJson(order.getAddressJson());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setTrackingNumber(order.getTrackingNumber());
        dto.setEstimatedDelivery(order.getEstimatedDelivery());
        dto.setCreatedAt(order.getCreatedAt());

        if (order.getItems() != null) {
            List<OrderDtos.OrderItemDto> itemDtos = order.getItems().stream().map(item -> {
                OrderDtos.OrderItemDto itemDto = new OrderDtos.OrderItemDto();
                itemDto.setId(item.getId());
                itemDto.setProductId(item.getProduct().getId());
                itemDto.setProductName(item.getProduct().getName());
                itemDto.setProductImage(item.getProduct().getImageUrl());
                itemDto.setQuantity(item.getQuantity());
                itemDto.setPrice(item.getPrice());
                itemDto.setSubtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                return itemDto;
            }).collect(Collectors.toList());
            dto.setItems(itemDtos);
        }

        return dto;
    }
}
