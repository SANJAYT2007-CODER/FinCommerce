package com.fincommerce.service;

import com.fincommerce.entity.Address;
import com.fincommerce.entity.User;
import com.fincommerce.exception.ResourceNotFoundException;
import com.fincommerce.repository.AddressRepository;
import com.fincommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AddressRepository addressRepository;

    public User getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Transactional
    public User updateUserProfile(Long userId, User updatedDetails) {
        User user = getUserProfile(userId);
        if (updatedDetails.getFullName() != null) user.setFullName(updatedDetails.getFullName());
        if (updatedDetails.getMobileNumber() != null) user.setMobileNumber(updatedDetails.getMobileNumber());
        if (updatedDetails.getDateOfBirth() != null) user.setDateOfBirth(updatedDetails.getDateOfBirth());
        if (updatedDetails.getGender() != null) user.setGender(updatedDetails.getGender());
        if (updatedDetails.getProfilePhoto() != null) user.setProfilePhoto(updatedDetails.getProfilePhoto());
        return userRepository.save(user);
    }

    @Transactional
    public User toggleBiometric(Long userId, boolean enabled) {
        User user = getUserProfile(userId);
        user.setBiometricEnabled(enabled);
        return userRepository.save(user);
    }

    public List<Address> getUserAddresses(Long userId) {
        return addressRepository.findByUserId(userId);
    }

    @Transactional
    public Address addAddress(Long userId, Address address) {
        User user = getUserProfile(userId);
        address.setUser(user);
        
        List<Address> existing = addressRepository.findByUserId(userId);
        if (existing.isEmpty() || Boolean.TRUE.equals(address.getIsDefault())) {
            existing.forEach(a -> a.setIsDefault(false));
            address.setIsDefault(true);
        }
        return addressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(Long userId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
        if (!address.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Address not found for user");
        }
        addressRepository.delete(address);
    }

    @Transactional
    public User updatePreferences(Long userId, User updatedPrefs) {
        User user = getUserProfile(userId);
        if (updatedPrefs.getDailySummaryTime() != null) user.setDailySummaryTime(updatedPrefs.getDailySummaryTime());
        if (updatedPrefs.getDailySummaryEnabled() != null) user.setDailySummaryEnabled(updatedPrefs.getDailySummaryEnabled());
        if (updatedPrefs.getSavingsReminderTime() != null) user.setSavingsReminderTime(updatedPrefs.getSavingsReminderTime());
        if (updatedPrefs.getSavingsReminderEnabled() != null) user.setSavingsReminderEnabled(updatedPrefs.getSavingsReminderEnabled());
        if (updatedPrefs.getBudgetAlertPercentage() != null) user.setBudgetAlertPercentage(updatedPrefs.getBudgetAlertPercentage());
        if (updatedPrefs.getNotificationPreferences() != null) user.setNotificationPreferences(updatedPrefs.getNotificationPreferences());
        return userRepository.save(user);
    }
}

