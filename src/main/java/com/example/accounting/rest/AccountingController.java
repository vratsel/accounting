package com.example.accounting.rest;

import com.example.accounting.db.Account;
import com.example.accounting.service.AccountingService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/accounting")
@AllArgsConstructor
public class AccountingController {

    private AccountingService accountingService;

    @GetMapping
    public List<String> getAvailableAccountNumbers() {
        return accountingService.getAvailableAccountNumbers();
    }

    @ApiOperation(value = "Get the account details",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "Account not found")})
    @GetMapping("/{number}")
    public Account getAccountDetails(@PathVariable String number) {
        return accountingService.getAccountDetails(number);
    }

    @ApiOperation(value = "Create a new account",
            notes = "Initial balance is 0",
            produces = "application/json")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Account already exists")})
    @PutMapping("/{number}")
    public Account createNewAccount(@PathVariable String number) {
        return accountingService.createNewAccount(number);
    }

    @ApiOperation(value = "Update account by some amount",
            produces = "application/json",
            notes = "Positive value of amount adds the amount to the account balance, negative one withdraws the account by the amount.In case there is not enough balance to withdraw, error is thrown")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Account overdrawn")})
    @PostMapping("/{number}/{amount}")
    public Account updateAccount(@PathVariable String number, @PathVariable Long amount) {
        return accountingService.updateAccount(number, amount);
    }
}
