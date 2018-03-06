package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;

import com.facilio.tasker.ScheduleInfo;
import com.facilio.tasker.ScheduleInfo.FrequencyType;

public enum FacilioFrequency {
	DAILY {
		@Override
		public ScheduleInfo getScheduleInfo() {
			// TODO Auto-generated method stub
			ScheduleInfo schedule = new ScheduleInfo();
			schedule.addTime("00:00");
			schedule.setFrequencyType(FrequencyType.DAILY);
			return schedule;
		}
	},
	WEEKLY {
		@Override
		public ScheduleInfo getScheduleInfo() {
			// TODO Auto-generated method stub
			ScheduleInfo schedule = new ScheduleInfo();
			schedule.addTime("00:00");
			schedule.setFrequencyType(FrequencyType.WEEKLY);
			List<Integer> values = new ArrayList<>();
			values.add(DateTimeUtil.getWeekFields().getFirstDayOfWeek().getValue());
			schedule.setValues(values);
			return schedule;
		}
	},
	MONTHLY {
		@Override
		public ScheduleInfo getScheduleInfo() {
			// TODO Auto-generated method stub
			ScheduleInfo schedule = new ScheduleInfo();
			schedule.addTime("00:00");
			schedule.setFrequencyType(FrequencyType.MONTHLY_DAY);
			List<Integer> values = new ArrayList<>();
			values.add(1);
			schedule.setValues(values);
			return schedule;
		}
	},
	QUARTERTLY {
		@Override
		public ScheduleInfo getScheduleInfo() {
			// TODO Auto-generated method stub
			ScheduleInfo schedule = new ScheduleInfo();
			schedule.addTime("00:00");
			schedule.setFrequencyType(FrequencyType.YEARLY);
			schedule.setYearlyDayValue(1);
			List<Integer> values = new ArrayList<>();
			values.add(1);
			values.add(4);
			values.add(7);
			values.add(10);
			schedule.setValues(values);
			return schedule;
		}
	},
	HALF_YEARLY {
		@Override
		public ScheduleInfo getScheduleInfo() {
			// TODO Auto-generated method stub
			ScheduleInfo schedule = new ScheduleInfo();
			schedule.addTime("00:00");
			schedule.setFrequencyType(FrequencyType.YEARLY);
			schedule.setYearlyDayValue(1);
			List<Integer> values = new ArrayList<>();
			values.add(1);
			values.add(7);
			schedule.setValues(values);
			return schedule;
		}
	},
	ANNUALLY {
		@Override
		public ScheduleInfo getScheduleInfo() {
			// TODO Auto-generated method stub
			ScheduleInfo schedule = new ScheduleInfo();
			schedule.addTime("00:00");
			schedule.setFrequencyType(FrequencyType.YEARLY);
			schedule.setYearlyDayValue(1);
			List<Integer> values = new ArrayList<>();
			values.add(1);
			schedule.setValues(values);
			return schedule;
		}
	},
	CUSTOM {
		@Override
		public ScheduleInfo getScheduleInfo() {
			// TODO Auto-generated method stub
			return null;
		}
	} 
	;
	
	public abstract ScheduleInfo getScheduleInfo();
	
	public int getValue() {
		return ordinal()+1;
	}
	
	public static FacilioFrequency valueOf(int value) {
		if (value > 0 && value <= values().length) {
			return values()[value - 1];
		}
		return null;
	}
}
