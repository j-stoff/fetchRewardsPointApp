package com.fetch.rewards.FetchRewardsApp;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserAccount {

	private final Logger logger = LogManager.getLogger(UserAccount.class);
	
	private String accountName;
	private Map<String, List<PaymentTransaction>> paymentTransactions;
	
	/**
	 * Default constructor
	 */
	public UserAccount() {
		paymentTransactions = new TreeMap<>();
	}
	
	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
}
