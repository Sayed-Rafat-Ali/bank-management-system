📌 Bank Management System
Full Stack Project — Java | Spring Boot | MySQL | JDBC | HTML/CSS/JS

A complete banking application that supports account creation, login, deposit, withdraw, money transfer, balance check, interest calculation, and transaction history, built using a clean layered architecture.

🚀 Features
🔐 Authentication

Login using Phone Number + PIN

Change PIN anytime

🧾 Account Management

Create Account (Saving / Current)

Monthly Interest Auto Application (Saving)

Fetch Complete Account Details

💸 Banking Operations

Deposit Money

Withdraw Money

Money Transfer (with transaction locks)

Check Balance

Complete Transaction History

💽 Database

MySQL + JDBC

Accounts table

Transactions table

PreparedStatements for security

🌐 REST API (Spring Boot)

Clean Controller + Service + Model layers

Proper GET/POST usage

JSON/text responses

🖥️ Frontend UI

Pure HTML + CSS + JavaScript (No frameworks)

Responsive & clean UI

🛠️ Tech Stack
Backend

Java 17

Spring Boot

REST APIs

JDBC

MySQL

Frontend

HTML

CSS

JavaScript (Fetch API)

Tools

Maven

Git & GitHub

 Eclipse

Postman

🧠 System Architecture

Frontend (HTML/CSS/JS)
       ↓  Fetch API (HTTP)
Spring Boot Controller
       ↓
Service Layer (Business Logic)
       ↓
JDBC → MySQL Database

🔌 REST API Endpoints

| Method | Endpoint         | Description             |
| ------ | ---------------- | ----------------------- |
| POST   | /api/bank/create | Create new bank account |
| POST   | /api/bank/login  | Login with phone & pin  |

Banking Ops

| Method | Endpoint               | Description            |
| ------ | ---------------------- | ---------------------- |
| POST   | /api/bank/deposit      | Deposit money          |
| POST   | /api/bank/withdraw     | Withdraw money         |
| POST   | /api/bank/transfer     | Transfer money         |
| GET    | /api/bank/balance      | Check balance          |
| GET    | /api/bank/transactions | Get history            |
| POST   | /api/bank/changePin    | Change PIN             |
| POST   | /api/bank/interest     | Apply Monthly Interest |

🛠️ How to Run Locally

1️⃣ Clone the Repository

git clone https://github.com/YOUR-USERNAME/bank-management-system.git
cd bank-management-system

2️⃣ Create Database

CREATE DATABASE bankdb;

3️⃣ Update MySQL Credentials

src/main/resources/application.properties

4️⃣ Run Application

mvn spring-boot:run

5️⃣ Open Frontend

http://localhost:8080/index.html

🎉 Now your banking system is ready!
