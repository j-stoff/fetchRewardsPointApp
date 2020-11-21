package com.fetch.rewards.FetchRewardsApp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.collections.CollectionUtils;
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
	
	public List<PaymentTransaction> getAllPaymentTransactionsSortedByDate() {
		List<PaymentTransaction> allPayments = new ArrayList<>();
		for (Map.Entry<String, List<PaymentTransaction>> entry : paymentTransactions.entrySet()) {
			allPayments.addAll(entry.getValue());
		}
		
		allPayments.sort(PaymentTransactionComparator.getInstance());
		
		return allPayments;
	}
	
	public List<PaymentTransaction> getPaymentTransactionsByPayerName(String payerName) {
		return paymentTransactions.get(payerName);
	}
	
	public List<PaymentTransaction> groupAllPaymentTransactionsByPayerName() {
		List<PaymentTransaction> groupedTransactions = new ArrayList<>();
		PaymentTransaction transaction;
		for (Map.Entry<String, List<PaymentTransaction>> entry : paymentTransactions.entrySet()) {
			transaction = new PaymentTransaction();
			transaction.setPayerName(entry.getKey());
			int total = 0;
			for (PaymentTransaction currentTransaction : entry.getValue()) {
				total += currentTransaction.getAmount();
			}
			transaction.setAmount(total);
			groupedTransactions.add(transaction);
		}
		
		return groupedTransactions;
	}
	
	public boolean addPaymentTransaction(String payerName, PaymentTransaction transaction) {
		List<PaymentTransaction> transactions = paymentTransactions.get(payerName);
		if (CollectionUtils.isEmpty(transactions)) {
			transactions = new ArrayList<>();
			transactions.add(transaction);
			paymentTransactions.put(payerName, transactions);
		} else {
			transactions.add(transaction);
			transactions.sort(PaymentTransactionComparator.getInstance());
		}
		
		return true;
	}
	
	public void removePaymentTransactionsByPayerName(String payerName, List<PaymentTransaction> transactionsToRemove) {
		List<PaymentTransaction> transactions = paymentTransactions.get(payerName);
		if (CollectionUtils.isEmpty(transactions)) {
			String message = "Attempting to remove payments when payer does not exist. Payer: " + payerName + ", user: " 
					+ accountName;
			logger.info(message);
			throw new IllegalArgumentException(message);
		}
		transactions.removeAll(transactionsToRemove);
	}
	
	public int getUserAccountBalance() {
		int total = 0;
		for (Map.Entry<String, List<PaymentTransaction>> paymentList : paymentTransactions.entrySet()) {
			for (PaymentTransaction transaction : paymentList.getValue()) {
				total += transaction.getAmount();
			}
		}
		
		return total;
	}
}
