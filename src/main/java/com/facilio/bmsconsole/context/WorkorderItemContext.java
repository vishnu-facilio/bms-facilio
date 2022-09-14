package com.facilio.bmsconsole.context;

public class WorkorderItemContext extends ItemTransactionsContext{
	private static final long serialVersionUID = 1L;
	
	private double cost = -1;
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
		this.setTransactionCost(cost);
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	private Double unitPrice;

	
}
