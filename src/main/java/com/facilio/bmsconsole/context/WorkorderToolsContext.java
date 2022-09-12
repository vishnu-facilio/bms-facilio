package com.facilio.bmsconsole.context;

public class WorkorderToolsContext extends ToolTransactionContext {
	private static final long serialVersionUID = 1L;

	private double cost = -1;

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
		this.setTransactionCost(cost);
	}

	private long issueTime = -1;

	public long getIssueTime() {
		return issueTime;
	}

	public void setIssueTime(long issueTime) {
		this.issueTime = issueTime;
	}

	private long returnTime = -1;

	public long getReturnTime() {
		return returnTime;
	}

	public void setReturnTime(long returnTime) {
		this.returnTime = returnTime;
	}

	private double duration = -1;

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public double getDuration() {
		return duration;
	}

	private Double rate;

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}
}
