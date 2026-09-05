package com.fincommerce.controller;

import com.fincommerce.dto.ApiResponse;
import com.fincommerce.entity.*;
import com.fincommerce.repository.*;
import com.fincommerce.service.FraudDetectionService;
import com.fincommerce.service.OrderService;
import com.fincommerce.service.SupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private FraudDetectionService fraudDetectionService;

    @Autowired
    private SupportService supportService;

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long totalUsers = userRepository.count();
        long totalProducts = productRepository.count();
        long totalOrders = orderRepository.count();

        List<Order> orders = orderRepository.findAll();
        BigDecimal totalRevenue = orders.stream()
                .filter(o -> !"CANCELLED".equalsIgnoreCase(o.getOrderStatus()) && !"REFUNDED".equalsIgnoreCase(o.getOrderStatus()))
                .map(Order::getFinalTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long pendingOrders = orders.stream()
                .filter(o -> "CONFIRMED".equalsIgnoreCase(o.getOrderStatus()) || "PACKED".equalsIgnoreCase(o.getOrderStatus()) || "SHIPPED".equalsIgnoreCase(o.getOrderStatus()))
                .count();

        long fraudAlerts = fraudDetectionService.getAllAlerts().stream()
                .filter(a -> !Boolean.TRUE.equals(a.getIsReviewed()))
                .count();

        stats.put("totalUsers", totalUsers);
        stats.put("totalProducts", totalProducts);
        stats.put("totalOrders", totalOrders);
        stats.put("totalRevenue", totalRevenue);
        stats.put("pendingOrders", pendingOrders);
        stats.put("fraudAlerts", fraudAlerts);
        stats.put("totalTransactions", transactionRepository.count());

        return ResponseEntity.ok(ApiResponse.success("Admin dashboard metrics retrieved", stats));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success("Users list", userRepository.findAll()));
    }

    @GetMapping("/products")
    public ResponseEntity<ApiResponse<List<Product>>> getAllProducts() {
        return ResponseEntity.ok(ApiResponse.success("Products catalog", productRepository.findAll()));
    }

    @PostMapping("/products")
    public ResponseEntity<ApiResponse<Product>> addProduct(@RequestBody Product product) {
        if (product.getCategory() != null && product.getCategory().getId() != null) {
            Category category = categoryRepository.findById(product.getCategory().getId()).orElse(null);
            product.setCategory(category);
        }
        Product saved = productRepository.save(product);
        return ResponseEntity.ok(ApiResponse.success("Product created", saved));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id, @RequestBody Product details) {
        Product product = productRepository.findById(id).orElseThrow();
        if (details.getName() != null) product.setName(details.getName());
        if (details.getPrice() != null) product.setPrice(details.getPrice());
        if (details.getStockQuantity() != null) product.setStockQuantity(details.getStockQuantity());
        if (details.getDescription() != null) product.setDescription(details.getDescription());
        if (details.getImageUrl() != null) product.setImageUrl(details.getImageUrl());
        return ResponseEntity.ok(ApiResponse.success("Product updated", productRepository.save(product)));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id) {
        productRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Product deleted"));
    }

    @GetMapping("/orders")
    public ResponseEntity<ApiResponse<List<Order>>> getAllOrders() {
        return ResponseEntity.ok(ApiResponse.success("Orders list", orderRepository.findAllByOrderByCreatedAtDesc()));
    }

    @PutMapping("/orders/{id}/status")
    public ResponseEntity<ApiResponse<Object>> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        return ResponseEntity.ok(ApiResponse.success("Order status updated", orderService.updateOrderStatus(id, status)));
    }

    @GetMapping("/coupons")
    public ResponseEntity<ApiResponse<List<Coupon>>> getAllCoupons() {
        return ResponseEntity.ok(ApiResponse.success("Coupons list", couponRepository.findAll()));
    }

    @PostMapping("/coupons")
    public ResponseEntity<ApiResponse<Coupon>> createCoupon(@RequestBody Coupon coupon) {
        return ResponseEntity.ok(ApiResponse.success("Coupon created", couponRepository.save(coupon)));
    }

    @DeleteMapping("/coupons/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCoupon(@PathVariable Long id) {
        couponRepository.deleteById(id);
        return ResponseEntity.ok(ApiResponse.success("Coupon deleted"));
    }

    @GetMapping("/alerts")
    public ResponseEntity<ApiResponse<List<SecurityAlert>>> getAllAlerts() {
        return ResponseEntity.ok(ApiResponse.success("Security alerts", fraudDetectionService.getAllAlerts()));
    }

    @PutMapping("/alerts/{id}/review")
    public ResponseEntity<ApiResponse<String>> reviewAlert(@PathVariable Long id) {
        fraudDetectionService.markAlertAsReviewed(id);
        return ResponseEntity.ok(ApiResponse.success("Alert reviewed"));
    }

    @GetMapping("/tickets")
    public ResponseEntity<ApiResponse<List<SupportTicket>>> getAllTickets() {
        return ResponseEntity.ok(ApiResponse.success("Support tickets", supportService.getAllTickets()));
    }

    @PutMapping("/tickets/{id}/status")
    public ResponseEntity<ApiResponse<SupportTicket>> updateTicketStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String status = body.get("status");
        String notes = body.get("notes");
        return ResponseEntity.ok(ApiResponse.success("Ticket updated", supportService.updateTicketStatus(id, status, notes)));
    }
}
