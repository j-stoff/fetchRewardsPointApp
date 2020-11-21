package com.fetch.rewards.FetchRewardsApp;

import java.util.Date;

/**
 * This is a model class to hold information regarding a payment transaction
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
	 * 
	 */
	public PaymentTransaction(String payerName, int amount, Date paymentDate) {
		this.payerName = payerName;
		this.amount = amount;
		this.paymentDate = paymentDate;
	}
	
	public String getPayerName() {
		return payerName;
	}
	public void setPayerName(String payerName) {
		this.payerName = payerName;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public Date getPaymentDate() {
		return paymentDate;
	}
	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}
}