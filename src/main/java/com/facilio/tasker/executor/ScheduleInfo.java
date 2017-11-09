package com.facilio.tasker.executor;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.struts2.json.annotations.JSON;

import com.facilio.bmsconsole.util.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

public class ScheduleInfo {
	
	public static final int LAST_WEEK = 5;
	
	private List<LocalTime> times = null;
	
	@JsonIgnore
	public List<LocalTime> getTimeObjects() {
		return times;
	}
	@JsonIgnore
	public void setTimeObjects(List<LocalTime> times) {
		this.times = times;
	}
	public void addTime(LocalTime time) {
		if(times == null) {
			times = new ArrayList<LocalTime>();
		}
		times.add(time);
	}
	public void addTim1e(String time) {
		addTime(LocalTime.parse(time));
	}
	
	public List<String> getTimes() {
		if(times != null) {
			List<String> timesStr = new ArrayList<>();
			for(LocalTime time : times) {
				timesStr.add(time.toString());
			}
			return timesStr;
		}
		return null;
	}
	
	@JsonSetter("times")
	public void setTimes(List<String> timesStr) {
		if(timesStr != null) {
			times = new ArrayList<>();
			for(String time : timesStr) {
				times.add(LocalTime.parse(time));
			}
		}
	}

	private FrequencyType frequencyType;
	public int getFrequencyType() {
		if(frequencyType != null) {
			return frequencyType.ordinal();
		}
		return -1;
	}
	public void setFrequencyType(int frequencyType) {
		if(frequencyType < 0 || frequencyType > TYPES.length-1) {
			throw new IllegalArgumentException("Invalid value for frequency type");
		}
		this.frequencyType = TYPES[frequencyType];
	}
	public void setFrequencyType(FrequencyType frequencyType) {
		this.frequencyType = frequencyType;
	}
	public FrequencyType getFrequencyTypeEnum() {
		return frequencyType;
	}
	
	private int frequency = 1;
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	
	private int weekFrequency = -1;
	public int getWeekFrequency() {
		return weekFrequency;
	}
	public void setWeekFrequency(int weekFrequency) {
		this.weekFrequency = weekFrequency;
	}

	private List<Integer> values;
	public List<Integer> getValues() {
		return values;
	}
	public void setValues(List<Integer> values) {
		this.values = values;
	}
	public void addValue(int value) {
		if(values == null) {
			values = new ArrayList<Integer>();
		}
		values.add(value);
	}
	
	public long nextExecutionTime(long startTime) {
		ZonedDateTime zdt = DateTimeUtil.getDateTime(startTime, true);
		
//		zdt = zdt.truncatedTo(ChronoUnit.MINUTES);
		if(times == null || times.isEmpty()) {
			addTime(zdt.toLocalTime().truncatedTo(ChronoUnit.MINUTES));
		}
		else {
			Collections.sort(times);
		}
		
		ZonedDateTime nextZdt = null;
		switch(frequencyType) {	
			case DO_NOT_REPEAT:
				nextZdt = zdt;
				break;
			case DAILY:
				if(values != null && !values.isEmpty() && !values.stream().allMatch(x -> x >= 1 && x <= 7)) {
					throw new IllegalArgumentException("Invalid value range of Days of Week");
				}
				
				if(zdt.toLocalTime().isAfter(times.get(times.size() - 1))) {
					zdt = zdt.with(LocalTime.of(0, 0)).plusDays(1);
				}
				
				nextZdt = compareHourAndMinute(zdt);
				if(nextZdt == null) {
					zdt = zdt.with(LocalTime.of(0, 0)).plusDays(frequency);
					nextZdt = compareHourAndMinute(zdt);
				}
				while((values != null && !values.isEmpty() && !values.contains(nextZdt.getDayOfWeek().getValue()))) {
					zdt = zdt.with(LocalTime.of(0, 0)).plusDays(1);
					nextZdt = compareHourAndMinute(zdt);
				}
				break;
			case WEEKLY:
				if(values != null && !values.isEmpty() && !values.stream().allMatch(x -> x >= 1 && x <= 7)) {
					throw new IllegalArgumentException("Invalid value range of Days of Week");
				}
				addAndSortValue(zdt.getDayOfWeek().getValue());
				
				while(!values.contains(zdt.getDayOfWeek().getValue()) || zdt.toLocalTime().isAfter(times.get(times.size() - 1))) {
					zdt = zdt.with(LocalTime.of(0, 0)).plusDays(1);
				}
				nextZdt = compareHourAndMinute(zdt);
				if(nextZdt == null) {
					int currentDay = zdt.getDayOfWeek().getValue();
					boolean incrementWeek = true;
					for(int value : values) {
						if(currentDay < value) {
							zdt = zdt.with(LocalTime.of(0, 0)).plusDays(value-currentDay);
							incrementWeek = false;
							break;
						}
					}
					if(incrementWeek) {
						zdt = zdt.with(LocalTime.of(0, 0)).minusDays(currentDay - values.get(0)).plusWeeks(frequency);
					}
					nextZdt = compareHourAndMinute(zdt);
				}
				break;
			case MONTHLY_DAY:
				if(values != null && !values.isEmpty() && !values.stream().allMatch(x -> x >= 1 && x <= 31)) {
					throw new IllegalArgumentException("Invalid value range of Days of Week");
				}
				addAndSortValue(zdt.getDayOfMonth());

				while(!values.contains(zdt.getDayOfMonth()) || zdt.toLocalTime().isAfter(times.get(times.size() - 1))) {
					zdt = zdt.with(LocalTime.of(0, 0)).plusDays(1);
				}
				nextZdt = compareHourAndMinute(zdt);
				if(nextZdt == null) {
					int currentDay = zdt.getDayOfMonth();
					boolean incrementMonth = true;
					for(int value : values) {
						if(currentDay < value) {
							try {
								zdt = zdt.with(LocalTime.of(0, 0)).withDayOfMonth(value);
								incrementMonth = false;
								break;
							}
							catch(DateTimeException e) {
								
							}
						}
					}
					if(incrementMonth) {
						try {
							zdt = zdt.with(LocalTime.of(0, 0)).plusMonths(frequency).withDayOfMonth(values.get(0));
						}
						catch(DateTimeException e) {
							zdt = zdt.with(LocalTime.of(0, 0)).plusMonths(frequency);
						}
					}
					nextZdt = compareHourAndMinute(zdt);
				}
				while(nextZdt == null || !values.contains(nextZdt.getDayOfMonth())) {
					try {
						zdt = zdt.with(LocalTime.of(0, 0)).plusMonths(1).withDayOfMonth(values.get(0));
					}
					catch(DateTimeException e) {
						zdt = zdt.with(LocalTime.of(0, 0)).plusMonths(1);
					}
					nextZdt = compareHourAndMinute(zdt);
				}
				break;
			case MONTHLY_WEEK:
				if(values != null && !values.isEmpty() && !values.stream().allMatch(x -> x >= 1 && x <= 7)) {
					throw new IllegalArgumentException("Invalid value range of Days of Week");
				}
				
				if(weekFrequency == -1 || weekFrequency < 1 || weekFrequency > LAST_WEEK) {
					weekFrequency = zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
				}
				
				addAndSortValue(zdt.getDayOfWeek().getValue());
				if(!values.contains(zdt.getDayOfWeek().getValue()) || !checkWeekOfMonth(zdt) || zdt.toLocalTime().isAfter(times.get(times.size() - 1))) {
					zdt = firstMonthlyWeek(zdt, 1);
				}
				nextZdt = compareHourAndMinute(zdt);
				if(nextZdt == null) {
					zdt = incrementMonthlyWeek(zdt, frequency);
					nextZdt = compareHourAndMinute(zdt);
				}
				break;
			case YEARLY:
				if(values != null && !values.isEmpty() && !values.stream().allMatch(x -> x >= 1 && x <= 12)) {
					throw new IllegalArgumentException("Invalid value range of Months");
				}
				addAndSortValue(zdt.getMonthValue());
				if(!values.contains(zdt.getMonthValue()) || zdt.toLocalTime().isAfter(times.get(times.size() - 1))) {
					zdt = incrementYear(zdt, 1);
				}
				nextZdt = compareHourAndMinute(zdt);
				if(nextZdt == null) {
					zdt = incrementYear(zdt, frequency);
					nextZdt = compareHourAndMinute(zdt);
				}
				break;
		}
		return nextZdt.toEpochSecond();
	}
	
	private ZonedDateTime incrementMonthlyWeek(ZonedDateTime zdt, int frequency) {
		boolean incrementMonth = true;
		ZonedDateTime newZdt = null;
		for(int value : values) {
			newZdt = shiftToDayOfWeek(zdt, value);
			if(zdt.getMonth() == newZdt.getMonth() && zdt.isBefore(newZdt)) {
				incrementMonth = false;
				break;
			}
		}
		if(incrementMonth) {
			zdt = zdt.with(LocalTime.of(0, 0)).plusMonths(frequency);
			newZdt = getMinWeek(zdt);
		}
		return newZdt;
	}
	
	private ZonedDateTime firstMonthlyWeek(ZonedDateTime zdt, int frequency) {
		ZonedDateTime newZdt =getMinWeek(zdt);
		if(!zdt.isBefore(newZdt) ) {
			zdt = zdt.with(LocalTime.of(0, 0)).plusMonths(frequency);
			newZdt = getMinWeek(zdt);
		}
		return newZdt;
	}
	
	private ZonedDateTime getMinWeek(ZonedDateTime zdt) {
		ZonedDateTime minZdt = null;
		ZonedDateTime newZdt = null;
		for(int value : values) {
			newZdt = shiftToDayOfWeek(zdt, value);
			if(minZdt == null || newZdt.isBefore(minZdt)) {
				minZdt = newZdt;
			}
		}
		return minZdt;
	}
	
	private boolean checkWeekOfMonth(ZonedDateTime zdt) {
		if(weekFrequency == LAST_WEEK) {
			for(Integer value : values) {
				int lhs = zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
				int rhs = shiftToDayOfWeek(zdt, value).get(ChronoField.ALIGNED_WEEK_OF_MONTH);
				if(lhs == rhs) {
					return true;
				}
			}
			return false;
		}
		else {
			return zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH) == weekFrequency;
		}
	}
	
	private ZonedDateTime shiftToDayOfWeek(ZonedDateTime zdt, int dayOfWeek) {
		int currentDay = zdt.getDayOfWeek().getValue();
		ZonedDateTime newZdt = zdt;
		if(weekFrequency == LAST_WEEK) {
			newZdt = zdt.with(LocalTime.of(0, 0)).with(TemporalAdjusters.lastInMonth(DayOfWeek.of(dayOfWeek)));
		}
		else {
			if(dayOfWeek > currentDay) {
				newZdt = zdt.with(LocalTime.of(0, 0)).plusDays(dayOfWeek - currentDay).with(ChronoField.ALIGNED_WEEK_OF_MONTH, weekFrequency);
			}
			else if(currentDay > dayOfWeek) {
				newZdt = zdt.with(LocalTime.of(0, 0)).plusDays(dayOfWeek + 7 - currentDay).with(ChronoField.ALIGNED_WEEK_OF_MONTH, weekFrequency);
			}
		}
		return newZdt;
	}
	
	private void addAndSortValue(int value) {
		if(values == null || values.isEmpty()) {
			addValue(value);
		}
		else {
			Collections.sort(values);
		}
	}
	
	private ZonedDateTime incrementYear(ZonedDateTime zdt, int frequency) {
		ZonedDateTime newZdt = null;
		int currentMonth = zdt.getMonthValue();
		boolean increaseYear = true;
		for(int value : values) {
			if(currentMonth < value) {
				newZdt = zdt.with(LocalTime.of(0, 0)).withMonth(value);
				increaseYear = false;
			}
		}
		
		if(increaseYear) {
			newZdt = zdt.with(LocalTime.of(0, 0)).plusYears(frequency).withMonth(values.get(0));
		}
		return newZdt;
	}

	private ZonedDateTime compareHourAndMinute(ZonedDateTime zdt) {
		for(LocalTime time : times) {
			if(time.isAfter(zdt.toLocalTime())) {
				return zdt.with(time);
			}
		}
		return null;
	}
	
	private static final FrequencyType[] TYPES = FrequencyType.values();
	public static enum FrequencyType {
		DO_NOT_REPEAT,
		DAILY,
		WEEKLY,
		MONTHLY_DAY,
		MONTHLY_WEEK,
		YEARLY;
	}
}
