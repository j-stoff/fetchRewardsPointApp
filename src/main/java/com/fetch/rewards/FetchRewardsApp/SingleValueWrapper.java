package com.fetch.rewards.FetchRewardsApp;

/**
 * A wrapper class to give a better JSON representation of the data for the caller. Overloads most constructors to assign the value.
 * @author Jacob Stoffregen
 *
 */
public class SingleValueWrapper {
	private Object value;

	/**
	 * Object constructor to assign the argument to the value.
	 * @param obj the new value as an object
	 */
	public SingleValueWrapper(Object obj) {
		value = obj;
	}
	
	/**
	 * Boolean constructor to assign the value as a Boolean.
	 * @param b the new value as a boolean
	 */
	public SingleValueWrapper(boolean b) {
		value = Boolean.valueOf(b);
	}
	
	/**
	 * Integer constructor to assign the value as an Integer.
	 * @param i the new value as an integer
	 */
	public SingleValueWrapper(int i) {
		value = Integer.valueOf(i);
	}
	
	/**
	 * Character constructor to assign the value as a Character.
	 * @param c the new value as a character.
	 */
	public SingleValueWrapper(char c) {
		value = Character.valueOf(c);
	}
	
	/**
	 * Long constructor to assign the value as a Long.
	 * @param l the new value as a long.
	 */
	public SingleValueWrapper(long l) {
		value = Long.valueOf(l);
	}
	
	/**
	 * Short constructor to assign the value as Short.
	 * @param s the new value as a short.
	 */
	public SingleValueWrapper(short s) {
		value = Short.valueOf(s);
	}
	
	/**
	 * Byte constructor to assign the value as a Byte.
	 * @param b the new value as a byte.
	 */
	public SingleValueWrapper(byte b) {
		value = Byte.valueOf(b);
	}
	
	/**
	 * Float constructor to assign the value as a Float.
	 * @param f the new value as a float.
	 */
	public SingleValueWrapper(float f) {
		value = Float.valueOf(f);
	}
	
	/**
	 * Double constructor to assign the value as a Double.
	 * @param d the new value as a double.
	 */
	public SingleValueWrapper(double d) {
		value = Double.valueOf(d);
	}
	
	/**
	 * Get the value.
	 * @return the value.
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Set the value.
	 * @param value the new value. 
	 */
	public void setValue(Object value) {
		this.value = value;
	}
}
