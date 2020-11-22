package com.fetch.rewards.FetchRewardsApp;

import java.util.List;

/**
 * A model class meant to hold data. Meant to be sent back to the caller in JSON format.
 * @author Jacob Stoffregen
 *
 */
public class UserAccountSummary {

	private String accountName;
	private int balance;
	private List<PaymentTransaction> payerList;
	
	/**
	 * Get the account name.
	 * @return the name on this account.
	 */
	public String getAccountName() {
		return accountName;
	}
	
	/**
	 * Set the account name
	 * @param accountName the name of the one who owns this account.
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	/**
	 * Get the balance on this account.
	 * @return the numeric balance of the account.
	 */
	public int getBalance() {
		return balance;
	}
	
	/**
	 * Set the balance on this account.
	 * @param balance the new balance for this account.
	 */
	public void setBalance(int balance) {
		this.balance = balance;
	}
	
	/**
	 * Get the list of payers for this account.
	 * @return the list of payers on this user account.
	 */
	public List<PaymentTransaction> getPayerList() {
		return payerList;
	}
	
	/**
	 * Set the list of payers on this account.
	 * @param payerList the new list of payers for this user account.
	 */
	public void setPayerList(List<PaymentTransaction> payerList) {
		this.payerList = payerList;
	}
}
