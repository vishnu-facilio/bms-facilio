package com.facilio.bmsconsole.context;

public class WorkorderItemContext extends InventoryTransactionsContext{
	private static final long serialVersionUID = 1L;
	
	private double cost = -1;
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
}
