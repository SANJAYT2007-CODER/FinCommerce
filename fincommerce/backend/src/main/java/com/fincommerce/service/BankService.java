package com.fincommerce.service;

import com.fincommerce.dto.FinancialDtos;
import com.fincommerce.entity.BankAccount;
import com.fincommerce.entity.User;
import com.fincommerce.exception.ResourceNotFoundException;
import com.fincommerce.repository.BankAccountRepository;
import com.fincommerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BankService {

    @Autowired
    private BankAccountRepository bankAccountRepository;

    @Autowired
    private UserRepository userRepository;

    public List<BankAccount> getBankAccounts(Long userId) {
        return bankAccountRepository.findByUserId(userId);
    }

    @Transactional
    public BankAccount addBankAccount(Long userId, FinancialDtos.AddBankAccountRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<BankAccount> existing = bankAccountRepository.findByUserId(userId);

        BankAccount account = new BankAccount();
        account.setUser(user);
        account.setBankName(request.getBankName());
        account.setAccountHolderName(request.getAccountHolderName());
        account.setAccountNumber(request.getAccountNumber());
        
        // Mask account number
        String accNum = request.getAccountNumber().trim();
        String masked = accNum.length() > 4 ? "XXXX XXXX " + accNum.substring(accNum.length() - 4) : accNum;
        account.setMaskedAccountNumber(masked);
        account.setIfscCode(request.getIfscCode());

        if (existing.isEmpty() || Boolean.TRUE.equals(request.getIsPrimary())) {
            existing.forEach(a -> a.setIsPrimary(false));
            bankAccountRepository.saveAll(existing);
            account.setIsPrimary(true);
        } else {
            account.setIsPrimary(false);
        }

        return bankAccountRepository.save(account);
    }

    @Transactional
    public void deleteBankAccount(Long userId, Long bankAccountId) {
        BankAccount account = bankAccountRepository.findById(bankAccountId)
                .orElseThrow(() -> new ResourceNotFoundException("Bank Account not found"));

        if (!account.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Bank Account not found for user");
        }

        bankAccountRepository.delete(account);
    }
}
