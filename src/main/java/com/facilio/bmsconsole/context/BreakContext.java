package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class BreakContext extends ModuleBaseWithCustomFields {
	private static final long serialVersionUID = 1L;

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private long breakTime;
	public long getBreakTime() {
		return breakTime;
	}
	public void setBreakTime(long breakTime) {
		this.breakTime = breakTime;
	}
	
	private BreakType breakType;
	public BreakType getBreakTypeEnum() {
		return breakType;
	}
	public void setBreakType(BreakType breakType) {
		this.breakType = breakType;
	}
	public int getBreakType() {
		if (breakType != null) {
			return breakType.getValue();
		}
		return -1;
	}
	public void setBreakType(int breakType) {
		this.breakType = BreakType.valueOf(breakType);
	}
	
	private List<ShiftContext> shifts;
	public List<ShiftContext> getShifts() {
		return shifts;
	}
	public void setShifts(List<ShiftContext> shifts) {
		this.shifts = shifts;
	}
	
	public static enum BreakType {
		PAID,
		UNPAID;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static BreakType valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
}
