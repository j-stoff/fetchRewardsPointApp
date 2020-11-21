package com.fetch.rewards.FetchRewardsApp;

import java.util.List;

public class ListWrapper {

	private int size;
	private List<?> list;
	
	public ListWrapper(List<?> list) {
		this.list = list;
		if (list != null) {
			this.size = list.size();
		}
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}
}