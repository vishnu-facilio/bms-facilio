package com.facilio.bmsconsole.context;

import java.util.List;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class ShiftRotationContext extends ModuleBaseWithCustomFields {
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
		DAILY, WEEKLY, MONTHLY;

		public int getValue() {
			return ordinal() + 1;
		}

		public static SchedularFrequency valueOf(int value) {
			if (value > 0 && value <= values().length) {
				return values()[value - 1];
			}
			return null;
		}
	}

	private int schedularDay;

	public int getSchedularDay() {
		return schedularDay;
	}

	public void setSchedularDay(int schedularDay) {
		this.schedularDay = schedularDay;
	}

	private int shiftSpanFrom;

	public int getShiftSpanFrom() {
		return shiftSpanFrom;
	}

	public void setShiftSpanFrom(int shiftSpanFrom) {
		this.shiftSpanFrom = shiftSpanFrom;
	}

	private int shiftSpanTo;

	public int getShiftSpanTo() {
		return shiftSpanTo;
	}

	public void setShiftSpanTo(int shiftSpanTo) {
		this.shiftSpanTo = shiftSpanTo;
	}

	private List<ShiftRotationDetailsContext> shiftRotations;

	public List<ShiftRotationDetailsContext> getShiftRotations() {
		return shiftRotations;
	}

	public void setShiftRotations(List<ShiftRotationDetailsContext> shiftRotationDetails) {
		this.shiftRotations = shiftRotationDetails;
	}

	private List<ShiftRotationApplicableForContext> applicableFor;

	public List<ShiftRotationApplicableForContext> getApplicableFor() {
		return applicableFor;
	}

	public void setApplicableFor(List<ShiftRotationApplicableForContext> applicableFor) {
		this.applicableFor = applicableFor;
	}
	
	private long timeOfSchedule = -1;
	public long getTimeOfSchedule() {
		return timeOfSchedule;
	}
	public void setTimeOfSchedule(long timeOfSchedule) {
		this.timeOfSchedule = timeOfSchedule;
	}

}
