package com.myBank.bankProject.model;

public class Account {

    private String name;
    private String phone;
    private String pin;
    private double balance;
    private String type;

    public Account() {}

    public Account(String name, String phone, double balance, String pin, String type) {
        this.name = name;
        this.phone = phone;
        this.balance = balance;
        this.pin = pin;
        this.type = type;
    }

    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getPin() { return pin; }
    public double getBalance() { return balance; }
    public String getType() { return type; }

    public void setName(String name) { this.name = name; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setPin(String pin) { this.pin = pin; }
    public void setBalance(double balance) { this.balance = balance; }
    public void setType(String type) { this.type = type; }
}