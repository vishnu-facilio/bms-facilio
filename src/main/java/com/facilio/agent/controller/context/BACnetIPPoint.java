package com.facilio.agent.controller.context;



public class BACnetIPPoint extends Point
{
	private static final long serialVersionUID = 1L;
	
	private long instanceNumber=-1;
	

	public long getInstanceNumber() {
		return instanceNumber;
	}

	public void setInstanceNumber(long instanceNumber) {
		this.instanceNumber = instanceNumber;
	}

	private int instanceType=-1;

	public int getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(int instanceType) {
		this.instanceType = instanceType;
	}

	
}
