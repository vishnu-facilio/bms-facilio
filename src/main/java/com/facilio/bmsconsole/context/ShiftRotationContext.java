package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class ShiftRotationContext extends ModuleBaseWithCustomFields{
	private static final long serialVersionUID = 1L;
	
	private String schedularName;
	public String getSchedularName() {
		return schedularName;
	}
	public void setSchedularName(String schedularName) {
		this.schedularName = schedularName;
	}
	
	private SchedularFrequency schedularFrequency;
	public SchedularFrequency getSchedularFrequencyEnum() {
		return schedularFrequency;
	}
	
	public void setSchedularFrequency(SchedularFrequency schedularFrequency) {
		this.schedularFrequency = schedularFrequency;
	}
	
	public int getSchedularFrequency() {
		if (schedularFrequency != null) {
			return schedularFrequency.getValue();
		}
		return -1;
	}
	
	public void setSchedularFrequency(int schedularFrequency) {
		this.schedularFrequency = SchedularFrequency.valueOf(schedularFrequency);
	}
	
	public enum SchedularFrequency {
		DAILY,
		WEEKLY,
		MONTHLY;
		
		public int getValue() {
			return ordinal()+1;
		}

		public static SchedularFrequency valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}
	
	private DayOfWeek schedularDay;
	public DayOfWeek getSchedularDayEnum() {
		return schedularDay;
	}
	
	public void setSchedularDay(DayOfWeek schedularDay) {
		this.schedularDay = schedularDay;
	}
	
	public int getSchedularDay() {
		if (schedularDay != null) {
			return schedularDay.getValue();
		}
		return -1;
	}
	
	public void setSchedularDay(int schedularDay) {
		this.schedularDay = DayOfWeek.valueOf(schedularDay);
	}
	
	private DayOfWeek shiftSpanFrom;
	public DayOfWeek getShiftSpanFromEnum() {
		return shiftSpanFrom;
	}
	
	public void setShiftSpanFrom(DayOfWeek shiftSpanFrom) {
		this.shiftSpanFrom = shiftSpanFrom;
	}
	
	public int getShiftSpanFrom() {
		if (shiftSpanFrom != null) {
			return shiftSpanFrom.getValue();
		}
		return -1;
	}
	
	public void setShiftSpanFrom(int shiftSpanFrom) {
		this.shiftSpanFrom = DayOfWeek.valueOf(shiftSpanFrom);
	}
	
	private DayOfWeek shiftSpanTo;
	public DayOfWeek getShiftSpanToEnum() {
		return shiftSpanTo;
	}
	
	public void setShiftSpanTo(DayOfWeek shiftSpanTo) {
		this.shiftSpanTo = shiftSpanTo;
	}
	
	public int getShiftSpanTo() {
		if (shiftSpanTo != null) {
			return shiftSpanTo.getValue();
		}
		return -1;
	}
	
	public void setShiftSpanTo(int shiftSpanTo) {
		this.shiftSpanTo = DayOfWeek.valueOf(shiftSpanTo);
	}
	
	public enum DayOfWeek {
		SUNDAY,
		MONDAY,
		TUESDAY,
		WEDNESDAY,
		THURSDAY,
		FRIDAY,
		SATURDAY;
		
		public int getValue() {
			return ordinal()+1;
		}
		
		public static DayOfWeek valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}

}
