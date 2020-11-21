package com.fetch.rewards.FetchRewardsApp;

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
	
}
