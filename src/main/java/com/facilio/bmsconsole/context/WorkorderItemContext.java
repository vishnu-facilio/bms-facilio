package com.facilio.bmsconsole.context;

public class WorkorderItemContext extends ItemTransactionsContext{
	private static final long serialVersionUID = 1L;
	
	private double cost = -1;
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	private long requestedLineItem;
	public long getRequestedLineItem() {
		return requestedLineItem;
	}
	public void setRequestedLineItem(long requestedLineItem) {
		this.requestedLineItem = requestedLineItem;
	}
	
	
}
