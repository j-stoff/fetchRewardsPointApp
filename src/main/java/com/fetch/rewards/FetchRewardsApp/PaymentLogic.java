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
 * to User accounts via payer transactions
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
			// attempt to add
			if (amount < 0) {
				String message = "Attempting to add a new payer " + payerName + " to account " 
						+ userAccount.getAccountName() + " with a negative amount " + amount;
				logger.info(message);
				throw new IllegalArgumentException(message);
			}
			PaymentTransaction transaction = new PaymentTransaction(payerName, amount, dateApplied);
			userAccount.addPaymentTransaction(payerName, transaction);
		} else {
			// add payment to payer
			if (amount >= 0) {
				// amount is positive
				PaymentTransaction transaction = new PaymentTransaction(payerName, amount, dateApplied);
				userAccount.addPaymentTransaction(payerName, transaction);
				
			} else {
				// amount is negative, take away from the oldest transactions
				// we are essentially not recording the negative transaction since it will take away from current payments
				int amountToRemove = amount;
				List<PaymentTransaction> transactionsToRemove = new ArrayList<>();
				for (PaymentTransaction payment : transactionsForPayer) {
					amountToRemove += payment.getAmount();
					if (amountToRemove <= 0) {
						// There is still move to remove, this entire transaction's amount has been used
						transactionsToRemove.add(payment);
					} else {
						// we went over, the amountToRemove is the final balance of the last transaction
						payment.setAmount(amountToRemove);
					}
				}
				userAccount.removePaymentTransactionsByPayerName(payerName, transactionsToRemove);
				
			}
		}
		
		return userAccount;
	}
	
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
		// 1. get all payment transactions and sort the list using the date comparator
		// 2. Loop through all payments
		// 		a. Apply deduction amount until it is 0
		//		b. Keep track of which payments are being applied to which accounts
		//      c. Keep track of which payers the deduction is being applied to (return value)
		
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
		
		// The last transaction is a partial transaction that could remove all in the account but likely will not
		addPaymentTransactionToBeRemoved(lastTransaction, paymentsAppliedToDeduction);
		
		return createListForAppliedDeduction(paymentsAppliedToDeduction);
	}
	
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
	
	public UserAccount getFullUserAccount(String userAccountName) {
		UserAccount userAccount = getValidUserAccount(userAccountName);
		return userAccount;
	}
	
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
