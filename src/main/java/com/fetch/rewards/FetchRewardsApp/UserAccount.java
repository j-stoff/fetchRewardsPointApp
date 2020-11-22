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
	
	/**
	 * Get the name of the owner for this account.
	 * @return the user name for this account.
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * Set the account name for this account.
	 * @param accountName the new name for this account.
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}
	
	/**
	 * Aggregate all payment transactions and sort them by date with the earliest payment first.
	 * @return a list of PaymentTransaction objects for this user account.
	 */
	public List<PaymentTransaction> getAllPaymentTransactionsSortedByDate() {
		List<PaymentTransaction> allPayments = new ArrayList<>();
		for (Map.Entry<String, List<PaymentTransaction>> entry : paymentTransactions.entrySet()) {
			allPayments.addAll(entry.getValue());
		}
		
		allPayments.sort(PaymentTransactionComparator.getInstance());
		
		return allPayments;
	}
	
	/**
	 * Get the List of payment transactions given a payer name.
	 * @param payerName The payer name to look up.
	 * @return the list of payments that user has made to this account.
	 */
	public List<PaymentTransaction> getPaymentTransactionsByPayerName(String payerName) {
		return paymentTransactions.get(payerName);
	}
	
	/**
	 * Group the individual payments made by payers into a list with a single entry for each payer that totals up how much
	 * they have contributed to this account.
	 * @return a list of unique payers and how much they have in this account.
	 */
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
	
	/**
	 * Add a payment transaction to this user account.
	 * @param payerName The payer making the payment.
	 * @param transaction the transaction to record.
	 * @return true if the account was successfully added.
	 */
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
	
	/**
	 * Remove a list of transactions for a given payer. Throws an error if no transactions exist for that payer in this account.
	 * @param payerName the name of the payer
	 * @param transactionsToRemove the transaction objects to remove.
	 */
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
	
	/**
	 * Total up the account balance by going through the list of payment transactions.
	 * @return the total amount of points in this user account.
	 */
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
