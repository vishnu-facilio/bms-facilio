package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class TemperatureContext extends ModuleBaseWithCustomFields {
	private long ttime;
	private double temperature;
	
	public long getTtime() {
		return ttime;
	}
	public void setTtime(long ttime) {
		this.ttime = ttime;
	}
	
	public double getTemperature() {
		return temperature;
	}
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(new Long(getId()).toString());
		buf.append(",");
		buf.append(new Long(ttime).toString());
		buf.append(",");
		buf.append(new Double(temperature).toString());
		return buf.toString();
	}
}