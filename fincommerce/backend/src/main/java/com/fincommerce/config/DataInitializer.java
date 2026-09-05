package com.fincommerce.config;

import com.fincommerce.entity.*;
import com.fincommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (roleRepository.count() > 0) {
            return; // Data already initialized
        }

        // 1. Roles
        Role userRole = roleRepository.save(new Role("ROLE_USER"));
        Role adminRole = roleRepository.save(new Role("ROLE_ADMIN"));

        // 2. Admin User
        User admin = new User();
        admin.setFullName("FinCommerce Admin");
        admin.setEmail("admin@fincommerce.com");
        admin.setMobileNumber("9876543210");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRoles(Set.of(adminRole, userRole));
        admin.setTransactionPin("9999");
        admin.setProfilePhoto("https://images.unsplash.com/photo-1534528741775-53994a69daeb?auto=format&fit=crop&w=150&q=80");
        User savedAdmin = userRepository.save(admin);
        walletRepository.save(new Wallet(savedAdmin, new BigDecimal("100000.00")));

        // 3. Regular Customer User
        User john = new User();
        john.setFullName("John Doe");
        john.setEmail("john@example.com");
        john.setMobileNumber("9876543211");
        john.setPassword(passwordEncoder.encode("password123"));
        john.setRoles(Set.of(userRole));
        john.setTransactionPin("1234");
        john.setDateOfBirth("1995-08-15");
        john.setGender("Male");
        john.setProfilePhoto("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80");
        User savedJohn = userRepository.save(john);

        Wallet johnWallet = new Wallet(savedJohn, new BigDecimal("25450.00"));
        johnWallet.setTotalSpent(new BigDecimal("12500.00"));
        johnWallet.setMonthlySpent(new BigDecimal("4200.00"));
        walletRepository.save(johnWallet);

        // Address for John
        Address johnAddress = new Address();
        johnAddress.setUser(savedJohn);
        johnAddress.setFullName("John Doe");
        johnAddress.setMobileNumber("9876543211");
        johnAddress.setHouseFlat("Flat 402, Sunshine Heights");
        johnAddress.setStreet("MG Road, Indiranagar");
        johnAddress.setCity("Bengaluru");
        johnAddress.setState("Karnataka");
        johnAddress.setCountry("India");
        johnAddress.setPincode("560038");
        johnAddress.setIsDefault(true);
        addressRepository.save(johnAddress);

        // Bank Account for John
        BankAccount bank = new BankAccount();
        bank.setUser(savedJohn);
        bank.setBankName("HDFC Bank");
        bank.setAccountHolderName("John Doe");
        bank.setAccountNumber("50100234567890");
        bank.setMaskedAccountNumber("XXXX XXXX 7890");
        bank.setIfscCode("HDFC0001234");
        bank.setIsPrimary(true);
        bankAccountRepository.save(bank);

        // Sample Transactions
        WalletTransaction tx1 = new WalletTransaction();
        tx1.setTransactionId("TXN-ADD-8921");
        tx1.setWallet(johnWallet);
        tx1.setUser(savedJohn);
        tx1.setType("ADD_MONEY");
        tx1.setAmount(new BigDecimal("10000.00"));
        tx1.setPaymentMethod("UPI");
        tx1.setStatus("SUCCESS");
        tx1.setNote("Salary bonus deposit");
        tx1.setSenderInfo("HDFC Bank UPI");
        tx1.setReceiverInfo("John Doe Wallet");
        transactionRepository.save(tx1);

        WalletTransaction tx2 = new WalletTransaction();
        tx2.setTransactionId("TXN-UPI-4412");
        tx2.setWallet(johnWallet);
        tx2.setUser(savedJohn);
        tx2.setType("UPI_PAYMENT");
        tx2.setAmount(new BigDecimal("450.00"));
        tx2.setPaymentMethod("UPI");
        tx2.setStatus("SUCCESS");
        tx2.setNote("Starbucks Coffee");
        tx2.setSenderInfo("John Doe Wallet");
        tx2.setReceiverInfo("starbucks@upi");
        transactionRepository.save(tx2);

        // 4. Categories
        Category catElectronics = categoryRepository.save(new Category("Electronics", "electronics", "Smartphones, Laptops, Audio, Accessories", "https://images.unsplash.com/photo-1498049794561-7780e7231661?auto=format&fit=crop&w=500&q=80"));
        Category catFashion = categoryRepository.save(new Category("Fashion", "fashion", "Trendy Clothing, Footwear & Accessories", "https://images.unsplash.com/photo-1445205170230-053b83016050?auto=format&fit=crop&w=500&q=80"));
        Category catGrocery = categoryRepository.save(new Category("Grocery", "grocery", "Daily essentials, Organic produce & Beverages", "https://images.unsplash.com/photo-1542838132-92c53300491e?auto=format&fit=crop&w=500&q=80"));
        Category catBeauty = categoryRepository.save(new Category("Beauty", "beauty", "Skincare, Makeup & Fragrances", "https://images.unsplash.com/photo-1522337360788-8b13dee7a37e?auto=format&fit=crop&w=500&q=80"));
        Category catHome = categoryRepository.save(new Category("Home", "home", "Furniture, Decor & Kitchen Appliances", "https://images.unsplash.com/photo-1513694203232-719a280e022f?auto=format&fit=crop&w=500&q=80"));
        Category catSports = categoryRepository.save(new Category("Sports", "sports", "Fitness gear, Apparel & Equipment", "https://images.unsplash.com/photo-1517649763962-0c623266010b?auto=format&fit=crop&w=500&q=80"));
        Category catBooks = categoryRepository.save(new Category("Books", "books", "Bestsellers, Fiction & Educational Books", "https://images.unsplash.com/photo-1497633762265-9d179a990aa6?auto=format&fit=crop&w=500&q=80"));
        Category catAcc = categoryRepository.save(new Category("Accessories", "accessories", "Watches, Bags & Sunglasses", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=500&q=80"));

        // 5. 20+ Realistic Products
        List<Product> productsList = List.of(
            createP("UltraBook Pro 15", "TechVision", catElectronics, "74999.00", 12, 45, 4.8, 128, "Intel i7 13th Gen, 16GB RAM, 512GB SSD, 15.6 inch OLED Display", "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?auto=format&fit=crop&w=500&q=80", true),
            createP("SoundPulse Noise Cancelling Headphones", "AudioTech", catElectronics, "12999.00", 15, 80, 4.7, 95, "Active Noise Cancellation, 40-hour Battery Life, Hi-Res Audio", "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?auto=format&fit=crop&w=500&q=80", true),
            createP("SmartWatch Ultra 2", "Chronos", catElectronics, "18999.00", 10, 60, 4.6, 210, "AMOLED Display, ECG Monitoring, Dual GPS, 100+ Sports Modes", "https://images.unsplash.com/photo-1523275335684-37898b6baf30?auto=format&fit=crop&w=500&q=80", true),
            createP("Pixel Vision 4K Smart TV 55\"", "Omni", catElectronics, "42999.00", 20, 25, 4.9, 78, "Dolby Vision HDR, Google TV OS, Hands-Free Voice Control", "https://images.unsplash.com/photo-1593359677879-a4bb92f829d1?auto=format&fit=crop&w=500&q=80", false),
            
            createP("Classic Denim Jacket", "UrbanStyle", catFashion, "2999.00", 25, 120, 4.4, 54, "100% Premium Cotton Denim with Vintage Wash Finish", "https://images.unsplash.com/photo-1543076447-215ad9ba6923?auto=format&fit=crop&w=500&q=80", false),
            createP("Leather Oxford Dress Shoes", "RoyalStep", catFashion, "4599.00", 18, 50, 4.7, 42, "Handcrafted Genuine Italian Leather Sole and Cushioned Insole", "https://images.unsplash.com/photo-1614252235316-8c857d38b5f4?auto=format&fit=crop&w=500&q=80", true),
            createP("Casual Cotton Chinos", "UrbanStyle", catFashion, "1899.00", 20, 90, 4.3, 31, "Stretch Fit Breathable Fabric, Available in Khaki & Navy", "https://images.unsplash.com/photo-1473966968600-fa801b869a1a?auto=format&fit=crop&w=500&q=80", false),

            createP("Organic Almond Milk 1L (Pack of 3)", "GreenEarth", catGrocery, "599.00", 5, 200, 4.8, 150, "Unsweetened, Rich in Calcium & Vitamin D", "https://images.unsplash.com/photo-1563636619-e9143da7973b?auto=format&fit=crop&w=500&q=80", false),
            createP("Premium Roasted Arabica Coffee Beans 500g", "BeanCraft", catGrocery, "849.00", 10, 150, 4.9, 88, "Single Origin High Altitude Coffee Beans", "https://images.unsplash.com/photo-1559056199-641a0ac8b55e?auto=format&fit=crop&w=500&q=80", true),

            createP("Hydrating Botanical Facial Serum 50ml", "Lumina", catBeauty, "1499.00", 15, 110, 4.6, 64, "Hyaluronic Acid & Vitamin C for Radiant Glow", "https://images.unsplash.com/photo-1620916566398-39f1143ab7be?auto=format&fit=crop&w=500&q=80", false),
            createP("Luxurious Oud Velvet Perfume 100ml", "Elixir Paris", catBeauty, "4999.00", 20, 40, 4.8, 49, "Long-Lasting Woody Fragrance with Rose & Amber Notes", "https://images.unsplash.com/photo-1523293182086-7651a899d37f?auto=format&fit=crop&w=500&q=80", true),

            createP("Minimalist Ergonomic Desk Chair", "ModaLiving", catHome, "8999.00", 10, 30, 4.7, 72, "High-Density Mesh Back, Adjustable Lumbar Support & Armrests", "https://images.unsplash.com/photo-1580481072645-022f9a6d8310?auto=format&fit=crop&w=500&q=80", false),
            createP("Ceramic Handcrafted Coffee Mugs (Set of 4)", "CraftCraft", catHome, "1199.00", 12, 85, 4.5, 38, "Microwave and Dishwasher Safe Hand-Painted Mugs", "https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&w=500&q=80", false),

            createP("Pro Fitness Non-Slip Yoga Mat 6mm", "FitMotion", catSports, "1299.00", 15, 140, 4.6, 112, "Eco-Friendly TPE Material with Alignment Lines", "https://images.unsplash.com/photo-1601925260368-ae2f83cf8b7f?auto=format&fit=crop&w=500&q=80", false),
            createP("Adjustable Cast Iron Dumbbell Set 20kg", "FitMotion", catSports, "3499.00", 8, 45, 4.8, 83, "Heavy Duty Chrome Finish with Anti-Slip Grip", "https://images.unsplash.com/photo-1584735935682-2f2b69dff9d2?auto=format&fit=crop&w=500&q=80", true),

            createP("Atomic Habits by James Clear", "Avery Publishing", catBooks, "499.00", 10, 300, 4.9, 540, "An Easy & Proven Way to Build Good Habits & Break Bad Ones", "https://images.unsplash.com/photo-1544716278-ca5e3f4abd8c?auto=format&fit=crop&w=500&q=80", true),
            createP("The Psychology of Money", "Harriman House", catBooks, "399.00", 10, 250, 4.9, 410, "Timeless lessons on wealth, greed, and happiness", "https://images.unsplash.com/photo-1592496431122-2349e0fbc666?auto=format&fit=crop&w=500&q=80", false),

            createP("Polarized Aviator Sunglasses", "RayLook", catAcc, "3299.00", 25, 75, 4.6, 96, "UV400 Protection Metal Frame Sunglasses", "https://images.unsplash.com/photo-1511499767150-a48a237f0083?auto=format&fit=crop&w=500&q=80", false),
            createP("Canvas Travel Duffel Bag 45L", "Traveler", catAcc, "2499.00", 15, 60, 4.7, 52, "Water-Resistant Vintage Leather Trim Duffle Bag", "https://images.unsplash.com/photo-1553062407-98eeb64c6a62?auto=format&fit=crop&w=500&q=80", false)
        );

        productRepository.saveAll(productsList);

        // 6. Coupons
        couponRepository.save(new Coupon("WELCOME10", "PERCENTAGE", new BigDecimal("10.00"), BigDecimal.ZERO, new BigDecimal("1000.00"), LocalDate.now().plusMonths(6)));
        couponRepository.save(new Coupon("SAVE500", "FIXED", new BigDecimal("500.00"), new BigDecimal("5000.00"), new BigDecimal("500.00"), LocalDate.now().plusMonths(6)));
        couponRepository.save(new Coupon("FESTIVE20", "PERCENTAGE", new BigDecimal("20.00"), new BigDecimal("2000.00"), new BigDecimal("2000.00"), LocalDate.now().plusMonths(6)));

        // 7. Initial Budget
        Budget b1 = new Budget();
        b1.setUser(savedJohn);
        b1.setCategory("ALL");
        b1.setMonthlyLimit(new BigDecimal("25000.00"));
        b1.setMonthYear(LocalDate.now().toString().substring(0, 7));
        budgetRepository.save(b1);
    }

    private Product createP(String name, String brand, Category category, String price, int discount, int stock, double rating, int reviews, String desc, String img, boolean featured) {
        Product p = new Product();
        p.setName(name);
        p.setBrand(brand);
        p.setCategory(category);
        p.setPrice(new BigDecimal(price));
        p.setDiscountPercent(discount);
        p.setStockQuantity(stock);
        p.setRating(rating);
        p.setReviewsCount(reviews);
        p.setDescription(desc);
        p.setImageUrl(img);
        p.setIsFeatured(featured);
        return p;
    }
}
