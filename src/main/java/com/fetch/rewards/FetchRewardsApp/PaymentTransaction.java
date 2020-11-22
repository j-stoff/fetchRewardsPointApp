package com.fetch.rewards.FetchRewardsApp;

import java.util.Date;

/**
 * This is a model class to hold information regarding a payment transaction.
 * @author Jacob Stoffregen
 *
 */
public class PaymentTransaction {

	private String payerName;
	private int amount;
	private Date paymentDate;
	
	/**
	 * Default no arg constructor
	 */
	public PaymentTransaction() {
	}
	
	/**
	 * Convenience constructor to set all instance fields.
	 */
	public PaymentTransaction(String payerName, int amount, Date paymentDate) {
		this.payerName = payerName;
		this.amount = amount;
		this.paymentDate = paymentDate;
	}

	/**
	 * Get the name of the payer.
	 * @return the name of the payer.
	 */
	public String getPayerName() {
		return payerName;
	}
	
	/**
	 * Set the name of the payer.
	 * @param payerName the name of the payer for this transaction.
	 */
	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}
	
	/**
	 * Get the amount for this transaction.
	 * @return the amount for this transaction.
	 */
	public int getAmount() {
		return amount;
	}
	
	/**
	 * Set the amount for this transaction.
	 * @param amount the amount for this transaction.
	 */
	public void setAmount(int amount) {
		this.amount = amount;
	}
	
	/**
	 * Get the date the payment occurred.
	 * @return the date the payment occurred.
	 */
	public Date getPaymentDate() {
		return paymentDate;
	}
	
	/**
	 * Set the date the payment occurred.
	 * @param paymentDate the date the payment occurred.
	 */
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
}