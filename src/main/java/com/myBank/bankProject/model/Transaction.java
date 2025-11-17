package com.myBank.bankProject.model;

import java.sql.Timestamp;

public class Transaction {

    private int id;
    private String phone;
    private String type;
    private double amount;
    private Timestamp date;

    public Transaction() {}

    public Transaction(int id, String phone, String type, double amount, Timestamp date) {
        this.id = id;
        this.phone = phone;
        this.type = type;
        this.amount = amount;
        this.date = date;
    }

    public int getId() { return id; }
    public String getPhone() { return phone; }
    public String getType() { return type; }
    public double getAmount() { return amount; }
    public Timestamp getDate() { return date; }
}

