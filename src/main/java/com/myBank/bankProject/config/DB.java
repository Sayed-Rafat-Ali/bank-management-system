package com.myBank.bankProject.config;

import java.sql.Connection;
import java.sql.DriverManager;

public class DB {
	


	    private static final String URL = "jdbc:mysql://localhost:3306/bankdb";
	    private static final String USER = "root";
	    private static final String PASS = "#Rafat1410";

	    public static Connection getConnection() {
	        try {
	            Class.forName("com.mysql.cj.jdbc.Driver");
	            return DriverManager.getConnection(URL, USER, PASS);
	        } 
	        catch (Exception e) {
	            e.printStackTrace();
	            return null;
	        }
	    }
	}
