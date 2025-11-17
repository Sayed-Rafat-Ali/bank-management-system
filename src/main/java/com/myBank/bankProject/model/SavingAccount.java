package com.myBank.bankProject.model;

public class SavingAccount extends Account{

	  public SavingAccount(String name, String phone, double balance, String pin) {
	        super(name, phone, balance, pin, "SAVING");
	    }
}
