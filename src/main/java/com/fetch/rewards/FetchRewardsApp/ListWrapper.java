package com.fetch.rewards.FetchRewardsApp;

import java.util.List;

/**
 * A wrapper class to give a better JSON representation of the data for the caller. Most of the normal getter/setter methods are
 * missing intentionally to force the use of the single constructor.
 * @author Jacob Stoffregen
 *
 */
public class ListWrapper {

	private int size;
	private List<?> list;
	
	/**
	 * Public constructor.
	 * @param list the list this class will be representing
	 */
	public ListWrapper(List<?> list) {
		this.list = list;
		if (list != null) {
			this.size = list.size();
		}
	}

	/**
	 * Get the size of the list.
	 * @return the size of the list
	 */
	public int getSize() {
		return size;
	}
}