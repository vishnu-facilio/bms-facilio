package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class PurchasedToolContext extends ModuleBaseWithCustomFields{
	private static final long serialVersionUID = 1L;

	private ToolContext tool;

	public ToolContext getTool() {
		return tool;
	}

	public void setTool(ToolContext tool) {
		this.tool = tool;
	}

	private double rate = -1;

	public double getRate() {
		return rate;
	}

	public void setRate(double rate) {
		this.rate = rate;
	}

	private long costDate;

	public long getCostDate() {
		return costDate;
	}

	public void setCostDate(long costDate) {
		this.costDate = costDate;
	}

	public Boolean isUsed;

	public Boolean getIsUsed() {
		return isUsed;
	}

	public void setIsUsed(Boolean individualTracking) {
		this.isUsed = individualTracking;
	}

	public boolean isIndividualTracking() {
		if (isUsed != null) {
			return isUsed.booleanValue();
		}
		return false;
	}

	private String serialNumber;

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

}
