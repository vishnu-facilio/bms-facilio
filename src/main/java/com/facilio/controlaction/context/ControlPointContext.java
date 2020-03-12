package com.facilio.controlaction.context;

import com.facilio.bmsconsole.context.ReadingDataMeta;

public class ControlPointContext extends ReadingDataMeta {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	long childRDMId = -1l;
	ReadingDataMeta childRDM;
	double minValue = -1;
	double maxValue = -1;
	
	boolean valueFromCommand = false; 
	
	public boolean isValueFromCommand() {
		return valueFromCommand;
	}
	public boolean getValueFromCommand() {
		return valueFromCommand;
	}
	public void setValueFromCommand(boolean valueFromCommand) {
		this.valueFromCommand = valueFromCommand;
	}
	public ReadingDataMeta getChildRDM() {
		return childRDM;
	}
	public void setChildRDM(ReadingDataMeta childRDM) {
		this.childRDM = childRDM;
	}
	
	public long getChildRDMId() {
		return childRDMId;
	}
	public void setChildRDMId(long childRDMId) {
		this.childRDMId = childRDMId;
	}
	public double getMinValue() {
		return minValue;
	}
	public void setMinValue(double minValue) {
		this.minValue = minValue;
	}
	public double getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(double maxValue) {
		this.maxValue = maxValue;
	}
	
	
}
