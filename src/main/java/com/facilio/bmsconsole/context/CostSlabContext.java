package com.facilio.bmsconsole.context;

public class CostSlabContext {
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}
	
	private long costId = -1;
	public long getCostId() {
		return costId;
	}
	public void setCostId(long costId) {
		this.costId = costId;
	}

	private double cost = -1;
	public double getCost() {
		return cost;
	}
	public void setCost(double cost) {
		this.cost = cost;
	}
	
	private double startRange = -1;
	public double getStartRange() {
		return startRange;
	}
	public void setStartRange(double startRange) {
		this.startRange = startRange;
	}
	
	private double endRange = -1;
	public double getEndRange() {
		return endRange;
	}
	public void setEndRange(double endRange) {
		this.endRange = endRange;
	}
	
	private double maxUnit = -1;
	public double getMaxUnit() {
		return maxUnit;
	}
	public void setMaxUnit(double maxUnit) {
		this.maxUnit = maxUnit;
	}
}
