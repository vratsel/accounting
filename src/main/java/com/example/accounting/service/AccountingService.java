package com.example.accounting.service;

import com.example.accounting.db.Account;
import com.example.accounting.repo.AccountRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AccountingService {

    private AccountRepository accountRepository;

    public List<String> getAvailableAccountNumbers() {
        var accounts = accountRepository.findAll();
        if (accounts != null)
            return accounts.stream().map(Account::getNumber).collect(Collectors.toList());
        return null;
    }

    @Transactional(readOnly = true)
    public Account getAccountDetails(String number) {
        return accountRepository.findAccountByNumber(number).orElseThrow(IllegalArgumentException::new);
    }

    @Transactional
    public Account updateAccount(String number, Long amount) {
        return updateAccount(number, amount, false);
    }

    @Transactional
    public Account updateAccount(String number, Long amount, boolean ignoreOverdraw) {
        var account = accountRepository.findAccountByNumber(number).orElseThrow(IllegalArgumentException::new);
        Long newValue = account.getBalance() + amount;
        if (!ignoreOverdraw && newValue < 0)
            throw new IllegalStateException("Resulting amount is less than 0");
        account.setBalance(newValue);
        return accountRepository.save(account);
    }

    @Transactional
    public Account createNewAccount(String number) {
        if (accountRepository.findAccountByNumber(number).isPresent())
            throw new IllegalStateException("Account already exists");
        return accountRepository.save(new Account(null, number, 0L));
    }
}
