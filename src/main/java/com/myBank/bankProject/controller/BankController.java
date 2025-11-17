package com.myBank.bankProject.controller;

import org.springframework.web.bind.annotation.*;

import com.myBank.bankProject.service.BankService;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/bank")
public class BankController {

    BankService bank = new BankService();

    @PostMapping("/create")
    public String create(
            @RequestParam String name,
            @RequestParam String phone,
            @RequestParam String pin,
            @RequestParam double balance,
            @RequestParam String type
    ) {
        bank.createAccount(name, phone, balance, pin, type);
        return "Account Created Successfully";
    }

    @PostMapping("/login")
    public boolean login(@RequestParam String phone, @RequestParam String pin) {
        return bank.validateLogin(phone, pin);
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam String phone, @RequestParam double amount) {
        bank.deposit(phone, amount);
        return "Deposit Successful";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam String phone, @RequestParam double amount) {
        if (bank.withdraw(phone, amount))
            return "Withdraw Successful";
        return "Insufficient Balance!";
    }

    @GetMapping("/balance")
    public double balance(@RequestParam String phone) {
        return bank.getBalance(phone);
    }

    @GetMapping("/transactions")
    public List<String> transactions(@RequestParam String phone) {
        return bank.getTransactions(phone);
    }

    @PostMapping("/interest")
    public String interest(@RequestParam String phone) {
        bank.applyMonthlyInterest(phone);
        return "Interest Applied";
    }

    @PostMapping("/transfer")
    public String transfer(
            @RequestParam String sender,
            @RequestParam String receiver,
            @RequestParam double amount
    ) {
        if (bank.transferMoney(sender, receiver, amount))
            return "Transfer Successful";
        return "Transfer Failed!";
    }

    @PostMapping("/changePin")
    public String changePin(
            @RequestParam String phone,
            @RequestParam String oldPin,
            @RequestParam String newPin
    ) {
        if (bank.changePin(phone, oldPin, newPin))
            return "PIN changed successfully";
        return "Incorrect old PIN!";
    }
}
