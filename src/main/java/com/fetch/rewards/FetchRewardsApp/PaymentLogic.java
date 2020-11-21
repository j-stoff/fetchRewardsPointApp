package com.fetch.rewards.FetchRewardsApp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
