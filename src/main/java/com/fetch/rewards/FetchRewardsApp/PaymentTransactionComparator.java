package com.fetch.rewards.FetchRewardsApp;

import java.util.Comparator;

public class PaymentTransactionComparator implements Comparator<PaymentTransaction> {
	private static final PaymentTransactionComparator instance = new PaymentTransactionComparator();
	
	/**
	 * Hidden constructor
	 */
	private PaymentTransactionComparator() {	
	}
	
	/**
	 * Singleton reference method to get an instance of the class.
	 * @return the single instance of this class.
	 */
	public static PaymentTransactionComparator getInstance() {
		return instance;
	}
	
	/**
	 * Compare two payment transactions. Arbitrarily calls the first argument 'first' and second argument 'second'. 
	 * If both are null, they are equal. Zero is returned.
	 * If first is not null, but second is then first is greater. A positive result is returned.
	 * If first is null, but second is not then second is greater. A negative result is returned.
	 * If both are not null then a compareTo method is called using the built in Date comparison.
	 */
	@Override
	public int compare(PaymentTransaction first, PaymentTransaction second) {
		if (first == null) {
			if (second == null) {
				return 0;
			} else {
				return -1;
			}
		} else {
			if (second == null) {
				return 1;
			} else {
				return first.getPaymentDate().compareTo(second.getPaymentDate());
			}
		}
	}

}
