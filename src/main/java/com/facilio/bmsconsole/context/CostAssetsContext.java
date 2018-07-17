package com.facilio.bmsconsole.context;

public class CostAssetsContext {
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
	
	private long assetId = -1;
	public long getAssetId() {
		return assetId;
	}
	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}
	
	private int billStartDay = -1;
	public int getBillStartDay() {
		return billStartDay;
	}
	public void setBillStartDay(int billStartDay) {
		this.billStartDay = billStartDay;
	}
	
	private int noOfBillMonths = -1; 
	public int getNoOfBillMonths() {
		return noOfBillMonths;
	}
	public void setNoOfBillMonths(int noOfBillMonths) {
		this.noOfBillMonths = noOfBillMonths;
	}
	
	private long firstBillTime = -1;
	public long getFirstBillTime() {
		return firstBillTime;
	}
	public void setFirstBillTime(long firstBillTime) {
		this.firstBillTime = firstBillTime;
	}
	
}
