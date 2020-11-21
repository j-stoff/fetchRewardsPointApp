package com.fetch.rewards.FetchRewardsApp;

import java.util.List;

/**
 * A model class meant to hold data that can be sent back to the caller about a User Account.
 * @author Jacob Stoffregen
 *
 */
public class UserAccountSummary {

	private String accountName;
	private int balance;
	private List<PaymentTransaction> payerList;
	
	public String getAccountName() {
		return accountName;
	}
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	public int getBalance() {
		return balance;
	}
	public void setBalance(int balance) {
		this.balance = balance;
	}
	public List<PaymentTransaction> getPayerList() {
		return payerList;
	}
	public void setPayerList(List<PaymentTransaction> payerList) {
		this.payerList = payerList;
	}
}
