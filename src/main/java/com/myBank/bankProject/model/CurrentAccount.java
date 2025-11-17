package com.myBank.bankProject.model;

public class CurrentAccount extends Account {

    public CurrentAccount(String name, String phone, double balance, String pin) {
        super(name, phone, balance, pin, "CURRENT");
    }
}