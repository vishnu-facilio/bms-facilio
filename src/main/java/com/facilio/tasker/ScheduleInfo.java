package com.facilio.tasker;

import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.time.DateTimeUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;

import java.io.Serializable;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ScheduleInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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
	public void addTime(String time) {
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
	
	private int yearlyDayValue = -1;
	public int getYearlyDayValue() {
		return yearlyDayValue;
	}
	public void setYearlyDayValue(int yearlyDayValue) {
		this.yearlyDayValue = yearlyDayValue;
	}
	
	public List<Long> nextExecutionTimes(long startTime, long endTime) {
		List<Long> nextExecutionTimes = new ArrayList<>();
		long flag = nextExecutionTime(startTime);
		while(flag <= endTime)
		{
			nextExecutionTimes.add(flag);
			flag = nextExecutionTime(flag);
		}
		return nextExecutionTimes;
	}
	
	public long nextExecutionTime(long startTime) {
		ZonedDateTime zdt = DateTimeUtil.getDateTime(startTime+1, true);
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
				zdt = zdt.truncatedTo(ChronoUnit.MINUTES);
				nextZdt = zdt.with(times.get(0));
				if(zdt.isAfter(nextZdt)) {
					nextZdt = nextZdt.plusDays(1);
				}
				break;
			case DAILY:
				if(values != null && !values.isEmpty() && !values.stream().allMatch(x -> x >= 1 && x <= 7)) {
					throw new IllegalArgumentException("Invalid value range of Days of Week");
				}
				
				if(zdt.toLocalTime().isAfter(times.get(times.size() - 1).plusSeconds(1))) {
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
				
				while(!values.contains(zdt.getDayOfWeek().getValue()) || zdt.toLocalTime().isAfter(times.get(times.size() - 1).plusSeconds(1))) {
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

				while(!values.contains(zdt.getDayOfMonth()) || zdt.toLocalTime().isAfter(times.get(times.size() - 1).plusSeconds(1))) {
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
				if(!values.contains(zdt.getDayOfWeek().getValue()) || !checkWeekOfMonth(zdt) || zdt.toLocalTime().isAfter(times.get(times.size() - 1).plusSeconds(1))) {
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
				if(!values.contains(zdt.getMonthValue()) || (yearlyDayValue != -1 && yearlyDayValue < zdt.getDayOfMonth()) || zdt.toLocalTime().isAfter(times.get(times.size() - 1).plusSeconds(1))) {
					zdt = incrementYear(zdt, 1);
				}
				else if (yearlyDayValue != -1 && yearlyDayValue > zdt.getDayOfMonth()) {
					zdt = zdt.withDayOfMonth(yearlyDayValue).with(times.get(0));
				}
				nextZdt = compareHourAndMinute(zdt);
				if(nextZdt == null) {
					zdt = incrementYear(zdt, frequency);
					nextZdt = compareHourAndMinute(zdt);
				}
				if (yearlyDayValue != -1) {
					nextZdt = nextZdt.withDayOfMonth(yearlyDayValue);
				}
				break;
		}
		return nextZdt.toEpochSecond() - 1;
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
				break;
			}
		}
		
		if(increaseYear) {
			newZdt = zdt.with(LocalTime.of(0, 0)).plusYears(frequency).withMonth(values.get(0));
			if (yearlyDayValue != -1) {
				newZdt = newZdt.withDayOfMonth(yearlyDayValue);
			}
		}
		return newZdt;
	}

	private ZonedDateTime compareHourAndMinute(ZonedDateTime zdt) {
		for(LocalTime time : times) {
			if(time.isAfter(zdt.toLocalTime()) || time.equals(zdt.toLocalTime())) {
				return zdt.with(time).plusSeconds(1);
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
	
	public String getDescription(long startTime) {
		ZonedDateTime zdt = DateTimeUtil.getDateTime(startTime);
		StringBuilder builder = new StringBuilder();
		switch(frequencyType) {
			case DO_NOT_REPEAT:
				return "Only Once";
			case DAILY:
				if(frequency == 1 && (values == null || values.isEmpty())) {
					return "Daily";
				}
				else {
					if(frequency == 1) {
						builder.append("Everyday");
					}
					else {
						builder.append("Every ")
								.append(frequency)
								.append(" days");
					}
					updateWeeklyDesc(builder, values, " only if it is ");
					return builder.toString();
				}
			case WEEKLY:
				if(frequency == 1 && (values == null || values.isEmpty())) {
					return "Every "+zdt.getDayOfWeek().name();
				}
				else {
					if(frequency == 1) {
						builder.append("Everyweek");
					}
					else {
						builder.append("Every ")
								.append(frequency)
								.append(" weeks");
					}
					if(values != null && !values.isEmpty()) {
						updateWeeklyDesc(builder, values, " on ");
					}
					else {
						updateWeeklyDesc(builder, Collections.singletonList(zdt.getDayOfWeek().getValue()), " on ");
					}
					return builder.toString();
				}
			case MONTHLY_DAY:
				if(frequency == 1) {
					builder.append("Every month");
				}
				else {
					builder.append("Every ")
							.append(frequency)
							.append(" months");
				}
				builder.append(" on ");
				if(values != null && !values.isEmpty()) {
					for(int itr = 0; itr<values.size(); itr++) {
						if(itr != 0) {
							if(itr == values.size() - 1) {
								builder.append(" or ");
							}
							else {
								builder.append(", ");
							}
						}
						builder.append(CommonCommandUtil.getNumberWithSuffix(values.get(itr)));
					}
				}
				else {
					builder.append(CommonCommandUtil.getNumberWithSuffix(zdt.getDayOfMonth()));
				}
				return builder.toString();
			case MONTHLY_WEEK:
				if(frequency == 1) {
					builder.append("Every month");
				}
				else {
					builder.append("Every ")
							.append(frequency)
							.append(" months");
				}
				if(values != null && !values.isEmpty()) {
					updateWeeklyDesc(builder, values, " on "+getWeekFrequencyInWords()+" ");
				}
				else {
					updateWeeklyDesc(builder, Collections.singletonList(zdt.getDayOfWeek().getValue()), " on "+getWeekFrequencyInWords()+" ");
				}
				return builder.toString();
			case YEARLY:
				if(frequency == 1) {
					builder.append("Every year");
				}
				else {
					builder.append("Every ")
							.append(frequency)
							.append(" years");
				}
				builder.append(" on ")
						.append(CommonCommandUtil.getNumberWithSuffix(zdt.getDayOfMonth()))
						.append(" of ");
				if(values != null && !values.isEmpty()) {
					for(int itr = 0; itr<values.size(); itr++) {
						if(itr != 0) {
							if(itr == values.size() - 1) {
								builder.append(" or ");
							}
							else {
								builder.append(", ");
							}
						}
						builder.append(Month.of(values.get(itr)).name());
					}
				}
				else {
					builder.append(zdt.getMonth().name());
				}
				return builder.toString();
		}
		return null;
	}
	
	private String getWeekFrequencyInWords() {
		switch(weekFrequency) {
			case 1:
			case 2:
			case 3:
			case 4:
				return CommonCommandUtil.getNumberWithSuffix(weekFrequency);
			case 5:
				return "Last";
		}
		return null;
	}
	
	private void updateWeeklyDesc(StringBuilder builder, List<Integer> values, String msg) {
		if(values != null && !values.isEmpty()) {
			builder.append(msg);
			for(int itr = 0; itr<values.size(); itr++) {
				if(itr != 0) {
					if(itr == values.size() - 1) {
						builder.append(" or ");
					}
					else {
						builder.append(", ");
					}
				}
				builder.append(DayOfWeek.of(values.get(itr)).name());
			}
		}
	}
}
