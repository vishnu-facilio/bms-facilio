package com.facilio.bmsconsole.context;

public class WorkorderItemContext extends ConsumableContext{
	private static final long serialVersionUID = 1L;
	
	private long parentId;
	public long getParentId() {
		return parentId;
	}
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}
	
	private double cost;
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
}
