package com.apigee.sdk.data.client;

public class CounterIncrement {

	private String counterName;
	private long counterIncrementValue;
	
	
	public CounterIncrement() {
		this.counterIncrementValue = 1;
	}
	
	public CounterIncrement(String counterName, long counterIncrementValue) {
		this.counterName = counterName;
		this.counterIncrementValue = counterIncrementValue;
	}
	
	public String getCounterName() {
		return this.counterName;
	}
	
	public void setCounterName(String counterName) {
		this.counterName = counterName;
	}
	
	public long getCounterIncrementValue() {
		return this.counterIncrementValue;
	}
	
	public void setCounterIncrementValue(long counterIncrementValue) {
		this.counterIncrementValue = counterIncrementValue;
	}
}
