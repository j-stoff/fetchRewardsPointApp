package com.fetch.rewards.FetchRewardsApp;

import java.util.Comparator;

public class PaymentTransactionComparator implements Comparator<PaymentTransaction> {
	private static final PaymentTransactionComparator instance = new PaymentTransactionComparator();
	
	/**
	 * Hidden constructor
	 */
	private PaymentTransactionComparator() {	
	}
	
	
	public static PaymentTransactionComparator getInstance() {
		return instance;
	}

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
