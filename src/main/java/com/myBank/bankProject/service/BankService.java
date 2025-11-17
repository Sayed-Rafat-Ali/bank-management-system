package com.myBank.bankProject.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.myBank.bankProject.config.DB;
import com.myBank.bankProject.model.Account;
import com.myBank.bankProject.model.CurrentAccount;
import com.myBank.bankProject.model.SavingAccount;

@Service
public class BankService {

    // Create Account
    public void createAccount(String name, String phone, double bal, String pin, String type) {
        try (Connection con = DB.getConnection()) {

            String q = "INSERT INTO accounts(name, phone, pin, balance, type) VALUES (?,?,?,?,?)";
            PreparedStatement ps = con.prepareStatement(q);

            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, pin);
            ps.setDouble(4, bal);
            ps.setString(5, type);

            ps.executeUpdate();

            addTransaction(phone, "DEPOSIT", bal);

            System.out.println("Account Created Successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Validate Login
    public boolean validateLogin(String phone, String pin) {
        try (Connection con = DB.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT 1 FROM accounts WHERE phone=? AND pin=?"
            );

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
        try (Connection con = DB.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT balance FROM accounts WHERE phone=?"
            );
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
        try (Connection con = DB.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                    "UPDATE accounts SET balance=? WHERE phone=?"
            );
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

        System.out.println("Amount Deposited!");
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
        try (Connection con = DB.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO transactions(phone, type, amount) VALUES (?,?,?)"
            );

            ps.setString(1, phone);
            ps.setString(2, type);
            ps.setDouble(3, amt);

            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Get Transaction History
    public List<String> getTransactions(String phone) {
        List<String> list = new ArrayList<>();

        try (Connection con = DB.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM transactions WHERE phone=? ORDER BY date DESC"
            );
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

    // Fetch Full Account
    public Account getAccount(String phone) {
        try (Connection con = DB.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT * FROM accounts WHERE phone=?"
            );
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
        try (Connection con = DB.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT balance, type FROM accounts WHERE phone=?"
            );
            ps.setString(1, phone);

            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("Account not found!");
                return;
            }

            double balance = rs.getDouble("balance");
            String type = rs.getString("type");

            if (!type.equalsIgnoreCase("SAVING")) {
                System.out.println("Interest applies only to Saving Accounts!");
                return;
            }

            double rate = 0.04;
            double monthlyInterest = balance * (rate / 12);

            double newBalance = balance + monthlyInterest;

            PreparedStatement ps2 = con.prepareStatement(
                    "UPDATE accounts SET balance=? WHERE phone=?"
            );

            ps2.setDouble(1, newBalance);
            ps2.setString(2, phone);

            ps2.executeUpdate();

            addTransaction(phone, "INTEREST", monthlyInterest);

            System.out.println("Interest Applied Successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Transfer Money
    public boolean transferMoney(String senderPhone, String receiverPhone, double amount) {

        String debitQuery = "UPDATE accounts SET balance = balance - ? WHERE phone = ?";
        String creditQuery = "UPDATE accounts SET balance = balance + ? WHERE phone = ?";
        String txQuery = "INSERT INTO transactions(phone, type, amount) VALUES (?,?,?)";

        try (Connection con = DB.getConnection()) {

            double senderBal = getBalance(senderPhone);

            if (senderBal < amount) {
                System.out.println("Insufficient Balance!");
                return false;
            }

            con.setAutoCommit(false); // start transaction

            PreparedStatement ps1 = con.prepareStatement(debitQuery);
            ps1.setDouble(1, amount);
            ps1.setString(2, senderPhone);
            ps1.executeUpdate();

            PreparedStatement ps2 = con.prepareStatement(creditQuery);
            ps2.setDouble(1, amount);
            ps2.setString(2, receiverPhone);
            ps2.executeUpdate();

            PreparedStatement ps3 = con.prepareStatement(txQuery);
            ps3.setString(1, senderPhone);
            ps3.setString(2, "TRANSFER OUT");
            ps3.setDouble(3, amount);
            ps3.executeUpdate();

            PreparedStatement ps4 = con.prepareStatement(txQuery);
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

        try (Connection con = DB.getConnection()) {

            PreparedStatement ps = con.prepareStatement(
                    "SELECT pin FROM accounts WHERE phone=? AND pin=?"
            );
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
