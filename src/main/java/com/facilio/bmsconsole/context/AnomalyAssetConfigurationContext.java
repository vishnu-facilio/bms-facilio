package com.facilio.bmsconsole.context;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
			
public class AnomalyAssetConfigurationContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;
	private long meterId;
	private long orgId;
	
	private String dimension1Buckets;
	private String dimension1Value;
	
	private int historyDays;
	private String startDate;
	private boolean startDateMode;
	private int isactive;
	private int meterInterval;
	private double tableValue; 
	private double adjustmentPercentage;
	private int orderRange;

	public long getMeterId() {
		return meterId;
	}
	public void setMeterId(long meterId) {
		this.meterId = meterId;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public String getDimension1Buckets() {
		return dimension1Buckets;
	}
	public void setDimension1Buckets(String dimension1Buckets) {
		this.dimension1Buckets = dimension1Buckets;
	}
	public String getDimension1Value() {
		return dimension1Value;
	}
	public void setDimension1Value(String dimension1Value) {
		this.dimension1Value = dimension1Value;
	}
	public int getHistoryDays() {
		return historyDays;
	}
	public void setHistoryDays(int historyDays) {
		this.historyDays = historyDays;
	}
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	public boolean isStartDateMode() {
		return startDateMode;
	}
	public void setStartDateMode(boolean startDateMode) {
		this.startDateMode = startDateMode;
	}
	public int getIsactive() {
		return isactive;
	}
	public void setIsactive(int isactive) {
		this.isactive = isactive;
	}
	public int getMeterInterval() {
		return meterInterval;
	}
	public void setMeterInterval(int meterInterval) {
		this.meterInterval = meterInterval;
	}
	public double getTableValue() {
		return tableValue;
	}
	public void setTableValue(double tableValue) {
		this.tableValue = tableValue;
	}
	public double getAdjustmentPercentage() {
		return adjustmentPercentage;
	}
	public void setAdjustmentPercentage(double adjustmentPercentage) {
		this.adjustmentPercentage = adjustmentPercentage;
	}
	public int getOrderRange() {
		return orderRange;
	}
	public void setOrderRange(int orderRange) {
		this.orderRange = orderRange;
	}
}