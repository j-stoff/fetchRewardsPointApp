package com.fetch.rewards.FetchRewardsApp;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This class is meant to take in parameters from the Service Layer and do the necessary computations for applying payments
 * to User accounts via payer transactions.
 * @author Jacob Stoffregen
 *
 */
public class PaymentLogic {

	private final Logger logger = LogManager.getLogger(PaymentLogic.class);

	private static PaymentLogic singletonInstance = new PaymentLogic();
	
	/**
	 * Hidden constructor to force the use of the singleton instance.
	 */
	private PaymentLogic() {
	}
	
	/**
	 * Get the singleton instance for the PaymentLogic class
	 * @return the singleton instance for PaymentLogic.
	 */
	public static PaymentLogic getInstance() {
		return singletonInstance;
	}
	
	/**
	 * Create a new user account. Throws a run time exception if the user account name already exists.
	 * @param userName the user account name to create.
	 * @return true if the account was created successfully.
	 */
	public boolean createNewUser(String userName) {
		UserAccount userAccount = UserAccountDAO.getInstance().getUserAccountByName(userName);
		if (userAccount != null) {
			String message = "User Account name already exists: " + userName;
			logger.info(message);
			throw new RuntimeException(message);
		}
		UserAccountDAO.getInstance().addNewUserAccount(userName);
		
		return true;
	}	
	
	/**
	 * Apply a payment to a User account. Throws if the payer name is not provided or if the date applied is null.
	 * New Payer names to the account must have a greater than zero payment or it will be rejected.
	 * @param payerName the name of the payer
	 * @param amount the amount being paid
	 * @param dateApplied the date the payment was applied.
	 * @param userAccountName the user account this payment was toward.
	 * @return the user account that had the payment applied.
	 */
	public UserAccount applyPaymentTransactionToUserAccount(String payerName, int amount, Date dateApplied, 
			String userAccountName) {
		UserAccount userAccount = getValidUserAccount(userAccountName);
		if (StringUtils.isBlank(payerName)) {
			String message = "Payment transaction blank for user: " + userAccount.getAccountName();
			logger.info(message);
			throw new IllegalArgumentException(message);
		}
		if (dateApplied == null) {
			String message = "Date not set for apply transaction for user: " + userAccount.getAccountName();
			logger.info(message);
			throw new IllegalArgumentException("Date not set for apply transaction for user: " + userAccount.getAccountName());
		}
		List<PaymentTransaction> transactionsForPayer = userAccount.getPaymentTransactionsByPayerName(payerName);
		
		if (CollectionUtils.isEmpty(transactionsForPayer)) {
			if (amount < 0) {
				String message = "Attempting to add a new payer " + payerName + " to account " 
						+ userAccount.getAccountName() + " with a negative amount " + amount;
				logger.info(message);
				throw new IllegalArgumentException(message);
			}
			PaymentTransaction transaction = new PaymentTransaction(payerName, amount, dateApplied);
			userAccount.addPaymentTransaction(payerName, transaction);
		} else {
			if (amount >= 0) {
				PaymentTransaction transaction = new PaymentTransaction(payerName, amount, dateApplied);
				userAccount.addPaymentTransaction(payerName, transaction);
				
			} else {
				int amountToRemove = amount;
				List<PaymentTransaction> transactionsToRemove = new ArrayList<>();
				for (PaymentTransaction payment : transactionsForPayer) {
					amountToRemove += payment.getAmount();
					if (amountToRemove <= 0) {
						transactionsToRemove.add(payment);
					} else {
						payment.setAmount(amountToRemove);
					}
				}
				userAccount.removePaymentTransactionsByPayerName(payerName, transactionsToRemove);
			}
		}
		
		return userAccount;
	}
	
	/**
	 * Apply a deduction to a user account. Requires that the amount of the deduction be lower or equal to the total
	 * amount of funds available in the account.
	 * @param amount amount to deduct
	 * @param userAccountName the user account name.
	 * @return a list of payers and the amount the deduction was applied to.
	 */
	public List<PaymentTransaction> applyDeductionToUserAccount(int amount, String userAccountName) {
		if (amount <= 0) {
			String message = "Amount given was negative for a deduction. Amount: " + amount;
			logger.info(message);
			throw new IllegalArgumentException(message);
		}
		
		UserAccount userAccount = getValidUserAccount(userAccountName);
		if (amount > userAccount.getUserAccountBalance()) {
			String message = "The deduction was over the user account limit for user: " + userAccountName;
			logger.info(message);
			throw new IllegalArgumentException(message);
		}
		
		List<PaymentTransaction> allPaymentsToUser = userAccount.getAllPaymentTransactionsSortedByDate();
		Map<String, List<PaymentTransaction>> paymentsAppliedToDeduction = new HashMap<>();
		int amountToRemove = amount;
		int index = 0;
		PaymentTransaction lastTransaction = null;
		while (amountToRemove > 0) {
			PaymentTransaction currentTransaction = allPaymentsToUser.get(index);
			if (currentTransaction.getAmount() <= amountToRemove) {
				amountToRemove -= currentTransaction.getAmount();
				addPaymentTransactionToBeRemoved(currentTransaction, paymentsAppliedToDeduction);
			} else {
				lastTransaction = new PaymentTransaction(currentTransaction.getPayerName(), amountToRemove, currentTransaction.getPaymentDate());
				amountToRemove -= currentTransaction.getAmount();
				currentTransaction.setAmount(Math.negateExact(amountToRemove));
			}
			
			index += 1;
		}
		
		for (Map.Entry<String, List<PaymentTransaction>> entry : paymentsAppliedToDeduction.entrySet()) {
			userAccount.removePaymentTransactionsByPayerName(entry.getKey(), entry.getValue());
		}
		
		addPaymentTransactionToBeRemoved(lastTransaction, paymentsAppliedToDeduction);
		
		return createListForAppliedDeduction(paymentsAppliedToDeduction);
	}
	
	/**
	 * Add a payment transaction to be removed. Used in the apply deduction logic to keep track of which payments applied to the deduction.
	 * @param transaction The transaction to remove.
	 * @param paymentTransactionsToRemove a map which a key being the payer name and the value is a list of transactions to remove.
	 */
	private void addPaymentTransactionToBeRemoved(PaymentTransaction transaction, Map<String, List<PaymentTransaction>> paymentTransactionsToRemove) {
		List<PaymentTransaction> transactions = paymentTransactionsToRemove.get(transaction.getPayerName());
		if (CollectionUtils.isEmpty(transactions)) {
			List<PaymentTransaction> newList = new ArrayList<>();
			newList.add(transaction);
			paymentTransactionsToRemove.put(transaction.getPayerName(), newList);
		} else {
			transactions.add(transaction);
		}
	}
	
	/**
	 * Create a list based on payments that were applied during a deduction. A key/value data structure is used to record
	 * all transactions that were applied for a particular payer. They are aggregated and a single transaction is made to
	 * represent the total that was applied during the deduction.
	 * @param paymentsRemoved the key/value data structure that contains the lists of payments to remove.
	 * @return a single list of payers and amounts that were applied to a deduction.
	 */
	private List<PaymentTransaction> createListForAppliedDeduction(Map<String, List<PaymentTransaction>> paymentsRemoved) {
		List<PaymentTransaction> paymentsApplied = new ArrayList<>();
		for (Map.Entry<String, List<PaymentTransaction>> entry : paymentsRemoved.entrySet()) {
			PaymentTransaction paymentTransaction = new PaymentTransaction();
			paymentTransaction.setPayerName(entry.getKey());
			paymentTransaction.setPaymentDate(new Date());
			int total = 0;
			for (PaymentTransaction transaction : entry.getValue()) {
				total += transaction.getAmount();
			}
			paymentTransaction.setAmount(Math.negateExact(total));
			paymentsApplied.add(paymentTransaction);
		}
		
		
		return paymentsApplied;
	}
	
	/**
	 * Public method to get a user account. Throws an error if the user does not exist
	 * @param userAccountName the user account to get
	 * @return the user account to get.
	 */
	public UserAccount getFullUserAccount(String userAccountName) {
		UserAccount userAccount = getValidUserAccount(userAccountName);
		return userAccount;
	}
	
	/**
	 * Hidden method to actually get a user account for internal use. Throws an IllegalArgumentException if user does not exist.
	 * @param userAccountName the user account to get.
	 * @return the User account object.
	 */
	private UserAccount getValidUserAccount(String userAccountName) {
		UserAccount userAccount = UserAccountDAO.getInstance().getUserAccountByName(userAccountName);
		if (userAccount == null) {
			String message = "User account was not found. Can not apply payment to: " + userAccountName;
			logger.info(message);
			throw new RuntimeException(message);
		}
		return userAccount;
	}
}
