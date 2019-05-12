package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;


public class AnalyticsAnomalyContext extends ModuleBaseWithCustomFields {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long ttime;
	private double totalEnergyConsumptionDelta;
	private long parentId;
	private double outlierDistance;

	public long getTtime() {
		return ttime;
	}

	public void setTtime(long ttime) {
		this.ttime = ttime;
	}

	public double getTotalEnergyConsumptionDelta() {
		return totalEnergyConsumptionDelta;
	}

	public void setTotalEnergyConsumptionDelta(double totalEnergyConsumptionDelta) {
		this.totalEnergyConsumptionDelta = totalEnergyConsumptionDelta;
	}

	public long getParentId() {
		return parentId;
	}

	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	public double getOutlierDistance() {
		return outlierDistance;
	}

	public void setOutlierDistance(double outlierDistance) {
		this.outlierDistance = outlierDistance;
	}
	
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(new Long(getId()).toString());
		buf.append(",");
		buf.append(new Long(ttime).toString());
		buf.append(",");
		buf.append(new Double(totalEnergyConsumptionDelta).toString());
		return buf.toString();
	}
}
