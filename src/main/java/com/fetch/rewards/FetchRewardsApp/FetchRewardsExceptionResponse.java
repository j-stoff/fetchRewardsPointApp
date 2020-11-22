package com.fetch.rewards.FetchRewardsApp;

/**
 * This class is meant to provide an abstraction from the internals of the application. It provides a simple message and the name of 
 * the exception that was thrown.
 * @author Jacob Stoffregen
 *
 */
public class FetchRewardsExceptionResponse {
	
	private String exceptionMessage;
	private String exceptionName;
	
	/**
	 * Get the exception message.
	 * @return the exception message.
	 */
	public String getExceptionMessage() {
		return exceptionMessage;
	}
	
	/**
	 * Set the exception message.
	 * @param exceptionMessage the exception message.
	 */
	public void setExceptionMessage(String exceptionMessage) {
		this.exceptionMessage = exceptionMessage;
	}
	
	/**
	 * Get the exception name.
	 * @return the exception name.
	 */
	public String getExceptionName() {
		return exceptionName;
	}
	
	/**
	 * Set the exception name
	 * @param exceptionName the name of the exception.
	 */
	public void setExceptionName(String exceptionName) {
		this.exceptionName = exceptionName;
	}
}