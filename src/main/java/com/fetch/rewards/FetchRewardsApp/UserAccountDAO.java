package com.fetch.rewards.FetchRewardsApp;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

/**
 * This class is meant to interface with the User Account data. Since the User Accounts will ultimately be held in
 * memory, this class equates to holding a list of unique accounts. Contains methods for simple CRUD functionality; however, the update
 * would be done by the 'Read' or get operation in this case.
 * @author Jacob Stoffregen
 * 
 */
public class UserAccountDAO {
	
	private Map<String, UserAccount> userAccounts;
	private static UserAccountDAO singletonInstance = new UserAccountDAO();
	
	/**
	 * Private constructor to force the singleton instance to be used.
	 */
	private UserAccountDAO() {
		userAccounts = new HashMap<>();
	}
	
	/**
	 * Get the singleton instance for the UserAccountDAO
	 * @return the singleton instance of the UserAccountDAO.
	 */
	public static UserAccountDAO getInstance() {
		return singletonInstance;
	}

	/**
	 * Get the user account by a given name. Can return null if not found.
	 * @param userAccountName the user account to get.
	 * @return the user account, or null if not found.
	 */
	public UserAccount getUserAccountByName(String userAccountName) {
		return userAccounts.get(userAccountName);
	}
	
	/**
	 * Add a user account to the list of active user accounts. Will not add duplicate users based on their account name.
	 * @param userAccount the user account to add.
	 * @return true if the account was added successfully, false if the account was already in the list.
	 */
	public boolean addUserAccount(UserAccount userAccount) {
		if (userAccount == null 
				|| StringUtils.isBlank(userAccount.getAccountName())) {
			return false;
		}
		
		userAccounts.putIfAbsent(userAccount.getAccountName(), userAccount);
		return true;
	}
	
	/**
	 * Add a new user to the list. Uses the same logic addUserAccount() does to not add duplicates.
	 * @param userAccountName the new user account name.
	 * @return true if the user account was created, false if it was not.
	 */
	public boolean addNewUserAccount(String userAccountName) {
		UserAccount newUser = new UserAccount();
		newUser.setAccountName(userAccountName);
		return addUserAccount(newUser);
	}
	
	/**
	 * Remove and return the object if it exists in the list.
	 * @param userAccount the user account to remove.
	 * @return the UserAccount object that was removed or null if the list did not have the input user account.
	 */
	public UserAccount removeUserAccount(UserAccount userAccount) {
		if (userAccount == null) {
			return null;
		}
		
		return removeUserAccountByName(userAccount.getAccountName());
	}
	
	/**
	 * Remove the user account given a specific account name.
	 * @param userAccountName the user account to remove
	 * @return the UserAccount object or null if it was not found.
	 */
	public UserAccount removeUserAccountByName(String userAccountName) {
		if (StringUtils.isBlank(userAccountName)) {
			return null;
		}
		
		return userAccounts.remove(userAccountName);
	}
	
}