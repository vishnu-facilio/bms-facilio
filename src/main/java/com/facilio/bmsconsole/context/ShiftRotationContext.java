package com.facilio.bmsconsole.context;

import java.time.ZonedDateTime;
import java.util.List;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.time.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;

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

	private int schedularDay = -1;

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

	@JsonIgnore
	@JSON(serialize = false)
	public long[] getStartAndEndTime() {
		long[] time = new long[2];
		switch (schedularFrequency) {
		case DAILY:
			time[0] = DateTimeUtil.getDayStartTime();
			time[1] = DateTimeUtil.getDayStartTime(1) - 1;
			break;
			
		case WEEKLY:
			time[0] = DateTimeUtil.getWeekStartTime();
			time[1] = DateTimeUtil.getDayStartTime(1) - 1;
			break;
			
		case MONTHLY:
			time[0] = DateTimeUtil.getMonthStartTime();
			time[1] = DateTimeUtil.getMonthStartTime(1) - 1;
			break;

		default:
			break;
		}
		return time;
	}

}
