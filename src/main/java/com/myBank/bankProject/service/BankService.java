package com.myBank.bankProject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

import com.myBank.bankProject.model.Account;
import com.myBank.bankProject.model.CurrentAccount;
import com.myBank.bankProject.model.SavingAccount;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class BankService {

    @Autowired
    private DataSource dataSource;   // Spring Boot DB connection

    // Create Account
    public void createAccount(String name, String phone, double bal, String pin, String type) {
        String q = "INSERT INTO accounts(name, phone, pin, balance, type) VALUES (?,?,?,?,?)";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(q)) {

            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, pin);
            ps.setDouble(4, bal);
            ps.setString(5, type);

            ps.executeUpdate();

            addTransaction(phone, "DEPOSIT", bal);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Validate Login
    public boolean validateLogin(String phone, String pin) {
        String q = "SELECT 1 FROM accounts WHERE phone=? AND pin=?";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(q)) {

            ps.setString(1, phone);
            ps.setString(2, pin);

            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // Get Balance
    public double getBalance(String phone) {
        String q = "SELECT balance FROM accounts WHERE phone=?";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(q)) {

            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) return rs.getDouble("balance");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Update Balance
    public void updateBalance(String phone, double newBal) {
        String q = "UPDATE accounts SET balance=? WHERE phone=?";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(q)) {

            ps.setDouble(1, newBal);
            ps.setString(2, phone);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Deposit
    public void deposit(String phone, double amt) {
        double bal = getBalance(phone);
        bal += amt;

        updateBalance(phone, bal);
        addTransaction(phone, "DEPOSIT", amt);
    }

    // Withdraw
    public boolean withdraw(String phone, double amt) {
        double bal = getBalance(phone);

        if (bal < amt) return false;

        bal -= amt;
        updateBalance(phone, bal);
        addTransaction(phone, "WITHDRAW", amt);

        return true;
    }

    // Add Transaction
    public void addTransaction(String phone, String type, double amt) {
        String q = "INSERT INTO transactions(phone, type, amount) VALUES (?,?,?)";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(q)) {

            ps.setString(1, phone);
            ps.setString(2, type);
            ps.setDouble(3, amt);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get transactions
    public List<String> getTransactions(String phone) {
        List<String> list = new ArrayList<>();
        String q = "SELECT * FROM transactions WHERE phone=? ORDER BY date DESC";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(q)) {

            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                list.add(
                        rs.getTimestamp("date") + " | " +
                        rs.getString("type") + " | " +
                        rs.getDouble("amount")
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // Get full account
    public Account getAccount(String phone) {
        String q = "SELECT * FROM accounts WHERE phone=?";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(q)) {

            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String type = rs.getString("type");

                if (type.equalsIgnoreCase("SAVING")) {
                    return new SavingAccount(
                            rs.getString("name"),
                            rs.getString("phone"),
                            rs.getDouble("balance"),
                            rs.getString("pin")
                    );
                } else {
                    return new CurrentAccount(
                            rs.getString("name"),
                            rs.getString("phone"),
                            rs.getDouble("balance"),
                            rs.getString("pin")
                    );
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Apply Monthly Interest
    public void applyMonthlyInterest(String phone) {
        String q = "SELECT balance, type FROM accounts WHERE phone=?";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(q)) {

            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return;

            double bal = rs.getDouble("balance");
            String type = rs.getString("type");

            if (!type.equalsIgnoreCase("SAVING")) return;

            double interest = bal * (0.04 / 12);
            double newBal = bal + interest;

            updateBalance(phone, newBal);
            addTransaction(phone, "INTEREST", interest);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Transfer Money
    public boolean transferMoney(String senderPhone, String receiverPhone, double amount) {

        String debit = "UPDATE accounts SET balance = balance - ? WHERE phone = ?";
        String credit = "UPDATE accounts SET balance = balance + ? WHERE phone = ?";
        String tx = "INSERT INTO transactions(phone, type, amount) VALUES (?,?,?)";

        try (Connection con = dataSource.getConnection()) {

            double senderBal = getBalance(senderPhone);
            if (senderBal < amount) return false;

            con.setAutoCommit(false);

            PreparedStatement ps1 = con.prepareStatement(debit);
            ps1.setDouble(1, amount);
            ps1.setString(2, senderPhone);
            ps1.executeUpdate();

            PreparedStatement ps2 = con.prepareStatement(credit);
            ps2.setDouble(1, amount);
            ps2.setString(2, receiverPhone);
            ps2.executeUpdate();

            PreparedStatement ps3 = con.prepareStatement(tx);
            ps3.setString(1, senderPhone);
            ps3.setString(2, "TRANSFER OUT");
            ps3.setDouble(3, amount);
            ps3.executeUpdate();

            PreparedStatement ps4 = con.prepareStatement(tx);
            ps4.setString(1, receiverPhone);
            ps4.setString(2, "TRANSFER IN");
            ps4.setDouble(3, amount);
            ps4.executeUpdate();

            con.commit();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Change PIN
    public boolean changePin(String phone, String oldPin, String newPin) {
        String q = "SELECT pin FROM accounts WHERE phone=? AND pin=?";

        try (Connection con = dataSource.getConnection();
             PreparedStatement ps = con.prepareStatement(q)) {

            ps.setString(1, phone);
            ps.setString(2, oldPin);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return false;

            PreparedStatement ps2 = con.prepareStatement(
                    "UPDATE accounts SET pin=? WHERE phone=?"
            );

            ps2.setString(1, newPin);
            ps2.setString(2, phone);
            ps2.executeUpdate();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
