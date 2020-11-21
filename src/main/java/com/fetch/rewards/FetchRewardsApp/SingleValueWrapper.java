package com.fetch.rewards.FetchRewardsApp;

public class SingleValueWrapper {
	private Object value;

	public SingleValueWrapper(Object obj) {
		value = obj;
	}
	
	public SingleValueWrapper(boolean b) {
		value = Boolean.valueOf(b);
	}
	
	public SingleValueWrapper(int i) {
		value = Integer.valueOf(i);
	}
	
	public SingleValueWrapper(char c) {
		value = Character.valueOf(c);
	}
	
	public SingleValueWrapper(long l) {
		value = Long.valueOf(l);
	}
	
	public SingleValueWrapper(short s) {
		value = Short.valueOf(s);
	}
	
	public SingleValueWrapper(byte b) {
		value = Byte.valueOf(b);
	}
	
	public SingleValueWrapper(float f) {
		value = Float.valueOf(f);
	}
	
	public SingleValueWrapper(double d) {
		value = Double.valueOf(d);
	}
	
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
