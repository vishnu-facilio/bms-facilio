package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;


public class AnalyticsAnamolyContext extends ModuleBaseWithCustomFields {
	private long ttime;
	private double energyDelta;
	private long meterId;
	
	public long getMeterId() {
		return meterId;
	}
	public void setMeterId(long meterId) {
		this.meterId = meterId;
	}

	
	public long getTtime() {
		return ttime;
	}
	public void setTtime(long ttime) {
		this.ttime = ttime;
	}
	
	public double getEnergyDelta() {
		return energyDelta;
	}
	public void setEnergyDelta(double energyDelta) {
		this.energyDelta = energyDelta;
	}
}
