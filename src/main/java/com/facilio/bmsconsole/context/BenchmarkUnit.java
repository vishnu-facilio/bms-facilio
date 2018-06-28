package com.facilio.bmsconsole.context;

import com.facilio.unitconversion.Unit;

public class BenchmarkUnit {
	private Unit fromUnit;
	public int getFromUnit() {
		if (fromUnit != null) { 
			return fromUnit.getUnitId();
		}
		return -1;
	}
	public void setFromUnit(int fromUnit) {
		this.fromUnit = Unit.valueOf(fromUnit);
	}
	public Unit getFromUnitEnum() {
		return fromUnit;
	}
	public void setFromUnit(Unit fromUnit) {
		this.fromUnit = fromUnit;
	}
	
	private Unit toUnit;
	public int getToUnit() {
		if (toUnit != null) {
			return toUnit.getUnitId();
		}
		return -1;
	}
	public void setToUnit(int toUnit) {
		this.toUnit = Unit.valueOf(toUnit);
	}
	public Unit getToUnitEnum() {
		return toUnit;
	}
	public void setToUnit(Unit toUnit) {
		this.toUnit = toUnit;
	}
	
	private double val = 1;
	public double getVal() {
		return val;
	}
	public void setVal(double val) {
		this.val = val;
	}
}
