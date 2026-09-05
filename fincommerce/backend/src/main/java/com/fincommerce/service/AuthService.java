package com.fincommerce.service;

import com.fincommerce.dto.AuthDtos;
import com.fincommerce.entity.Role;
import com.fincommerce.entity.User;
import com.fincommerce.entity.Wallet;
import com.fincommerce.exception.BadRequestException;
import com.fincommerce.exception.ResourceNotFoundException;
import com.fincommerce.repository.RoleRepository;
import com.fincommerce.repository.UserRepository;
import com.fincommerce.repository.WalletRepository;
import com.fincommerce.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private com.fincommerce.repository.SecurityAlertRepository securityAlertRepository;

    private final java.util.Map<String, OtpEntry> otpCache = new java.util.concurrent.ConcurrentHashMap<>();

    private static class OtpEntry {
        String code;
        java.time.LocalDateTime expiresAt;
        OtpEntry(String code, java.time.LocalDateTime expiresAt) {
            this.code = code;
            this.expiresAt = expiresAt;
        }
    }


    @Transactional
    public AuthDtos.AuthResponse registerUser(AuthDtos.RegisterRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("Passwords do not match");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email address is already registered");
        }

        if (userRepository.existsByMobileNumber(request.getMobileNumber())) {
            throw new BadRequestException("Mobile number is already registered");
        }

        User user = new User(
                request.getFullName(),
                request.getEmail(),
                request.getMobileNumber(),
                passwordEncoder.encode(request.getPassword())
        );

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
        user.setRoles(Collections.singleton(userRole));

        // Default PIN: 1234
        user.setTransactionPin("1234");
        user.setProfilePhoto("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?auto=format&fit=crop&w=150&q=80");

        User savedUser = userRepository.save(user);

        // Create Digital Wallet automatically with starting demo balance
        Wallet wallet = new Wallet(savedUser, new BigDecimal("25450.00"));
        walletRepository.save(wallet);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String jwt = tokenProvider.generateToken(authentication);

        return new AuthDtos.AuthResponse(
                jwt,
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getMobileNumber(),
                "ROLE_USER"
        );
    }

    public AuthDtos.AuthResponse loginUser(AuthDtos.LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        String jwt = tokenProvider.generateToken(authentication);
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String primaryRole = user.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse("ROLE_USER");

        return new AuthDtos.AuthResponse(
                jwt,
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getMobileNumber(),
                primaryRole
        );
    }

    @Transactional
    public void changePassword(Long userId, AuthDtos.ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Incorrect current password");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public void setTransactionPin(Long userId, String newPin) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setTransactionPin(newPin);
        userRepository.save(user);
    }

    public AuthDtos.OtpResponse sendOtp(AuthDtos.SendOtpRequest request) {
        String identifier = request.getIdentifier().trim();
        User user = userRepository.findByMobileNumber(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new ResourceNotFoundException("No account registered with " + identifier));

        // Generate 6-digit OTP (default demo fallback 123456 or random)
        String otpCode = "123456"; 
        otpCache.put(identifier, new OtpEntry(otpCode, java.time.LocalDateTime.now().plusMinutes(5)));

        String maskedMobile = user.getMobileNumber().replaceAll("\\d(?=\\d{4})", "X");

        return new AuthDtos.OtpResponse(
                "OTP sent to +91 " + maskedMobile,
                user.getMobileNumber(),
                otpCode
        );
    }

    @Transactional
    public AuthDtos.AuthResponse verifyOtp(AuthDtos.VerifyOtpRequest request) {
        String identifier = request.getIdentifier().trim();
        String otp = request.getOtp().trim();

        OtpEntry entry = otpCache.get(identifier);

        // Allow 123456 for demo or validate entry
        if (!"123456".equals(otp)) {
            if (entry == null || entry.expiresAt.isBefore(java.time.LocalDateTime.now())) {
                throw new BadRequestException("OTP expired or invalid. Please request a new OTP.");
            }
            if (!entry.code.equals(otp)) {
                throw new BadRequestException("Invalid OTP. Please try again.");
            }
        }

        if (entry != null) {
            otpCache.remove(identifier);
        }

        User user = userRepository.findByMobileNumber(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = tokenProvider.generateTokenFromUser(user);

        String primaryRole = user.getRoles().stream()
                .map(Role::getName)
                .findFirst()
                .orElse("ROLE_USER");

        String deviceInfo = request.getDeviceInfo() != null ? request.getDeviceInfo() : "Chrome / Windows";
        java.time.LocalDateTime now = java.time.LocalDateTime.now();

        // 1. Create Security Notification
        notificationService.createNotification(
                user,
                "New login detected.",
                "Login successful | Device: " + deviceInfo + " | Date: " + now.toLocalDate() + " | Time: " + now.toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a")) + " | Location: Mumbai, India (Demo)",
                "LOGIN_ALERT",
                "settings.html"
        );

        // 2. Create Security Alert Entity
        com.fincommerce.entity.SecurityAlert alert = new com.fincommerce.entity.SecurityAlert();
        alert.setUser(user);
        alert.setAlertMessage("We detected a login to your FinCommerce account from " + deviceInfo + " on " + now.toLocalDate() + " at " + now.toLocalTime().format(java.time.format.DateTimeFormatter.ofPattern("hh:mm a")) + ".");
        alert.setSeverity("MEDIUM");
        alert.setTransactionId("LOGIN-" + System.currentTimeMillis());
        alert.setStatus("DETECTED");
        alert.setIsReviewed(false);
        securityAlertRepository.save(alert);

        return new AuthDtos.AuthResponse(
                token,
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getMobileNumber(),
                primaryRole
        );
    }
}

