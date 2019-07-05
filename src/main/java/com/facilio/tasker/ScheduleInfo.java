package com.facilio.tasker;

import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.util.FacilioUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.time.*;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ScheduleInfo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int LAST_WEEK = 5;

	/**
	 * The times at which the schedule gets executed of the day of a specified time zone.
	 */
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

	public List<Integer> getYearlyDayOfWeekValues() {
		return yearlyDayOfWeekValues;
	}

	public void setYearlyDayOfWeekValues(List<Integer> yearlyDayOfWeekValues) {
		this.yearlyDayOfWeekValues = yearlyDayOfWeekValues;
	}

	public void addYearlyDayOfWeekValues(int value) {
		if (yearlyDayOfWeekValues == null) {
			yearlyDayOfWeekValues = new ArrayList<>();
		}
		yearlyDayOfWeekValues.add(value);
	}

	private List<Integer> yearlyDayOfWeekValues;

	public int getMonthValue() {
		return monthValue;
	}

	public void setMonthValue(int monthValue) {
		this.monthValue = monthValue;
	}

	private int monthValue = -1;

	private int yearlyDayValue = -1;
	public int getYearlyDayValue() {
		return yearlyDayValue;
	}
	public void setYearlyDayValue(int yearlyDayValue) {
		this.yearlyDayValue = yearlyDayValue;
	}

	private int skipEvery = -1;
	public int getSkipEvery () {
		return this.skipEvery;
	}

	public void setSkipEvery(int skipEvery) {
		this.skipEvery = skipEvery;
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

	public Pair<Long, Integer> nextExecutionTime(Pair<Long, Integer> startTimeCyclesExecuted) {
		// avoids infinite loop and rejects meaningless 'skipEvery' value
		if (this.skipEvery <= 1) {
			return Pair.of(nextExecutionTime(startTimeCyclesExecuted.getLeft()), -1);
		}

		int cycleCount = startTimeCyclesExecuted.getRight() + 1;
		if(cycleCount % this.skipEvery == 0) {
			return nextExecutionTime(Pair.of(nextExecutionTime(startTimeCyclesExecuted.getLeft()), cycleCount));
		}

		return Pair.of(nextExecutionTime(startTimeCyclesExecuted.getLeft()), cycleCount);
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
			case YEARLY_WEEK: { // tested
				// values holds the month on which schedule executes
				// weekFrequency holds the week at which the schedule executes
				// yearlyDayOfWeekValues holds the day of the week on which the schedule executes
				// times holds the times of the day at which the schedule executes

				if(values != null && !values.isEmpty() && !values.stream().allMatch(x -> x >= 1 && x <= 12)) {
					throw new IllegalArgumentException("Invalid value range of Days of Week");
				}

				if(weekFrequency < 1 || weekFrequency > LAST_WEEK) {
					throw new IllegalArgumentException("Invalid value range for Week");
				}

				if (yearlyDayOfWeekValues != null && !yearlyDayOfWeekValues.isEmpty() && !yearlyDayOfWeekValues.stream().allMatch(x -> x >= 1 & x <= 7)) {
					throw new IllegalArgumentException("Invalid value for yearlyDayOfWeekValues");
				}

				if (yearlyDayOfWeekValues == null) {
					addAndSortYearlyDayOfWeekValues(zdt.getDayOfWeek().getValue());
				}

				// Add the month of start time, in case the value array is empty
				addAndSortValue(zdt.getMonthValue());

				// 1. Move to the first day of the nearest month if the start time month doesn't fall in the rule
				if (!values.contains(zdt.getMonthValue())) {
					zdt = incrementYear(zdt, 1);
					zdt = zdt.withDayOfMonth(1);
				}

				// 2 Move to first day of the nearest week in the month, in case zdt does not fall in the rule
				int calculatedFrequency = weekFrequency;
				if (weekFrequency == LAST_WEEK) {
					calculatedFrequency = zdt.with(TemporalAdjusters.lastDayOfMonth()).get(ChronoField.ALIGNED_WEEK_OF_MONTH);
				}
				if (!checkWeekOfMonthForYearly(zdt)) {
					if (zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH) > calculatedFrequency) { // move to next year earliest feasible month and week
						zdt = zdt.with(LocalTime.of(0, 0)).with(TemporalAdjusters.firstDayOfNextYear());
						zdt = zdt.withMonth(values.get(0));
						zdt = zdt.with(ChronoField.ALIGNED_WEEK_OF_MONTH, calculatedFrequency);
					} else {
						zdt = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, 1).with(ChronoField.ALIGNED_WEEK_OF_MONTH, calculatedFrequency);
					}
				}

				// 3 Move to the particular day
				int alignedWeek = zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
				ZonedDateTime alignedStartOfWeek = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, 1).with(ChronoField.ALIGNED_WEEK_OF_MONTH, alignedWeek);
				int alignedStartActualWeekDay = alignedStartOfWeek.getDayOfWeek().getValue();
				int zdtWeekDay = zdt.getDayOfWeek().getValue();

				List<Integer> convertedWeekDays = new ArrayList<>();
				for (int val : yearlyDayOfWeekValues) {
					convertedWeekDays.add(((7 + val - alignedStartActualWeekDay) % 7) + 1);
				}
				Collections.sort(convertedWeekDays);

				int zdtAlignedWeekDay = ((7 + zdtWeekDay - alignedStartActualWeekDay) % 7) + 1;

				boolean moveToNextYear = true;
				for (int val : convertedWeekDays) {
					if (zdtAlignedWeekDay == val) {
						if (times.get(times.size() - 1).isAfter(zdt.toLocalTime())) {
							moveToNextYear = false;
							break;
						}
					} else if (zdtAlignedWeekDay < val) {
						ZonedDateTime tmp = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, val);
						if (tmp.getMonthValue() > zdt.getMonthValue()) {
							tmp = tmp.with(LocalTime.of(0, 0)).withMonth(zdt.getMonthValue()).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, val).with(ChronoField.ALIGNED_WEEK_OF_MONTH, calculatedFrequency - 1);
						}
						zdt = tmp;
						moveToNextYear = false;
						break;
					}
				}

				if (moveToNextYear) {
					zdt = zdt.with(LocalTime.of(0, 0)).with(TemporalAdjusters.firstDayOfNextYear());
					zdt = zdt.withMonth(values.get(0));
					calculatedFrequency = weekFrequency;
					if (weekFrequency == LAST_WEEK) {
						calculatedFrequency = zdt.with(TemporalAdjusters.lastDayOfMonth()).get(ChronoField.ALIGNED_WEEK_OF_MONTH);
					}
					alignedWeek = zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
					alignedStartOfWeek = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, 1).with(ChronoField.ALIGNED_WEEK_OF_MONTH, alignedWeek);
					alignedStartActualWeekDay = alignedStartOfWeek.getDayOfWeek().getValue();

					convertedWeekDays = new ArrayList<>();
					for (int val : yearlyDayOfWeekValues) {
						convertedWeekDays.add(((7 + val - alignedStartActualWeekDay) % 7) + 1);
					}
					Collections.sort(convertedWeekDays);

					ZonedDateTime tmp = zdt.with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, convertedWeekDays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, calculatedFrequency);
					if (tmp.getMonthValue() > zdt.getMonthValue()) {
						tmp = zdt.with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, convertedWeekDays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, calculatedFrequency - 1);
					}
					zdt = tmp;
				}

				// 4 Move to the nearest feasible time
				nextZdt = compareHourAndMinute(zdt);
			}
			break;
			case QUARTERLY_DAY: {
				// values hold the days of the month on which to be scheduled
				// times hold the times on which the schedule is to be executed
				// monthValue holds the month of the quarter (1, 2, 3) to be executed

				List<Integer> allowedMonths = Arrays.asList(1, 4, 7, 10);

				if (values != null && !values.isEmpty() && !values.stream().allMatch(x -> x >= 1 && x <= 31)) {
					throw new IllegalArgumentException("Invalid value range of Days of Week");
				}

				if (monthValue == -1) {
					monthValue = 1;
				}

				// finding the quarter in which the date belongs
				int quarterMonth;
				int month = zdt.getMonthValue();

				if (month >= 1 && month < 4) {
					quarterMonth = 1;
				} else if (month >= 4 && month < 7) {
					quarterMonth = 4;
				} else if (month >= 7 && month < 10) {
					quarterMonth = 7;
				} else {
					quarterMonth = 10;
				}

				addAndSortValue(zdt.getDayOfMonth());

				int dayOfMonth = zdt.getDayOfMonth();
				int offsetMonth = (month - quarterMonth) + 1;

				// 1. move zdt to the nearest allowed month.
				boolean moveToNextYear = true;
				for (Integer allowedMonth : allowedMonths) {
					if (quarterMonth == allowedMonth) {
						if (offsetMonth == monthValue) { // tested
							for (int day : values) {
								if (day == dayOfMonth) { // tested
									if (times.get(times.size() - 1).isAfter(zdt.toLocalTime())) {
										moveToNextYear = false;
										break;
									}
								} else if (day > dayOfMonth) { // tested
									zdt = zdt.with(LocalTime.of(0, 0)).with(ChronoField.DAY_OF_MONTH, day);
									moveToNextYear = false;
									break;
								}
							}

							if (!moveToNextYear) {
								break;
							}
						} else if (offsetMonth < monthValue) { // tested
							zdt = zdt.withMonth(allowedMonth + monthValue - 1).with(LocalTime.of(0, 0)).with(ChronoField.DAY_OF_MONTH, values.get(0));
							moveToNextYear = false;
							break;
						}
					} else if (allowedMonth > quarterMonth) {
						zdt = zdt.with(LocalTime.of(0, 0)).with(ChronoField.MONTH_OF_YEAR, allowedMonth + monthValue - 1).with(ChronoField.DAY_OF_MONTH, values.get(0));
						moveToNextYear = false;
						break;
					}
				}

				if (moveToNextYear) {
					zdt = zdt.with(LocalTime.of(0, 0)).with(TemporalAdjusters.firstDayOfNextYear()).with(ChronoField.MONTH_OF_YEAR, allowedMonths.get(0) + monthValue - 1).with(ChronoField.DAY_OF_MONTH, values.get(0));
				}

				// 2. Move to the nearest feasible time of the day
				nextZdt = compareHourAndMinute(zdt);
			}
			break;
			case QUARTERLY_WEEK:{
				// weekFrequency holds the week at which the schedule executes
				// times holds the times of the day at which the schedule executes
				// values holds the day of the week on which the schedule executes
				// monthValue holds the month of the quarter (1, 2, 3) to be executed

				if (monthValue == -1) {
					monthValue = 1;
				}

				if(values != null && !values.isEmpty() && !values.stream().allMatch(x -> x >= 1 && x <= 7)) {
					throw new IllegalArgumentException("Invalid value range of Days of Week");
				}

				if(weekFrequency == -1 || weekFrequency < 1 || weekFrequency > LAST_WEEK) {
					weekFrequency = zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
				}

				addAndSortValue(zdt.getDayOfWeek().getValue());

				int month = zdt.getMonthValue();
				List<Integer> allowedMonths = Arrays.asList(1, 4, 7, 10);

				// finding the quarter in which the date belongs
				int quarterMonth;


				if (month >= 1 && month < 4) {
					quarterMonth = 1;
				} else if (month >= 4 && month < 7) {
					quarterMonth = 4;
				} else if (month >= 7 && month < 10) {
					quarterMonth = 7;
				} else {
					quarterMonth = 10;
				}

				int offsetMonth = (month - quarterMonth) + 1;

				int alignedWeek = zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
				ZonedDateTime alignedStartOfWeek = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, 1).with(ChronoField.ALIGNED_WEEK_OF_MONTH, alignedWeek);
				int alignedStartActualWeekDay = alignedStartOfWeek.getDayOfWeek().getValue();
				List<Integer> convertedWeekdays = getOffsettedWeekDays(alignedStartActualWeekDay);

				int zdtWeekDay = zdt.getDayOfWeek().getValue();
				int zdtAlignedWeekDay = (((7 + zdtWeekDay - alignedStartActualWeekDay) % 7) + 1);

				// 1. Move to the nearest allowed month
				boolean moveToNextYear = true;
				for (Integer allowedMonth : allowedMonths) {
					if (quarterMonth == allowedMonth) {
						if (offsetMonth == monthValue) {
							if (checkWeekOfMonth(zdt)) {
								for (int convertedDayOfWeek: convertedWeekdays) {
									if (zdtAlignedWeekDay == convertedDayOfWeek) { // tested
										if (zdt.toLocalTime().isBefore(times.get(times.size() - 1))) {
											moveToNextYear = false;
											break;
										}
									} else if (zdtAlignedWeekDay < convertedDayOfWeek) { // tested
										zdt = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, convertedDayOfWeek);
										moveToNextYear = false;
										break;
									}
								}
								if (!moveToNextYear) {
									break;
								}
							} else if (weekFrequency == LAST_WEEK) { // tested
								int lastAlignedWeek = zdt.with(TemporalAdjusters.lastDayOfMonth()).get(ChronoField.ALIGNED_WEEK_OF_MONTH);
								if (zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH) < lastAlignedWeek) {
									ZonedDateTime temp = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, convertedWeekdays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, lastAlignedWeek);
									if (temp.getMonthValue() > zdt.getMonthValue()) {
										zdt = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, convertedWeekdays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, lastAlignedWeek - 1);
									} else {
										zdt = temp;
									}
									moveToNextYear = false;
									break;
								}
							} else if (zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH) < weekFrequency) { // tested
								zdt = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, convertedWeekdays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, weekFrequency);
								moveToNextYear = false;
								break;
							}
						} else if (offsetMonth < monthValue) { // tested
							zdt = zdt.with(LocalTime.of(0, 0)).withMonth(allowedMonth + (monthValue - 1));
							alignedWeek = zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
							alignedStartOfWeek = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, alignedWeek).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, 1);
							alignedStartActualWeekDay = alignedStartOfWeek.getDayOfWeek().getValue();
							int calculatedFrequency = weekFrequency;
							if (weekFrequency == LAST_WEEK) {
								calculatedFrequency = zdt.with(TemporalAdjusters.lastDayOfMonth()).get(ChronoField.ALIGNED_WEEK_OF_MONTH);
							}

							convertedWeekdays = getOffsettedWeekDays(alignedStartActualWeekDay);

							ZonedDateTime temp = zdt.with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, convertedWeekdays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, calculatedFrequency);
							if (temp.getMonthValue() > zdt.getMonthValue()) {
								zdt = zdt.with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, convertedWeekdays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, calculatedFrequency - 1);
							} else {
								zdt = temp;
							}
							moveToNextYear = false;
							break;
						}
					} else if (allowedMonth > quarterMonth) { // tested
						zdt = zdt.withDayOfMonth(1).with(LocalTime.of(0, 0)).withMonth(allowedMonth + (monthValue - 1));
						alignedWeek = zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
						alignedStartOfWeek = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, 1).with(ChronoField.ALIGNED_WEEK_OF_MONTH, alignedWeek);
						alignedStartActualWeekDay = alignedStartOfWeek.getDayOfWeek().getValue();
						int calculatedFrequency = weekFrequency;
						if (weekFrequency == LAST_WEEK) {
							calculatedFrequency = zdt.with(TemporalAdjusters.lastDayOfMonth()).get(ChronoField.ALIGNED_WEEK_OF_MONTH);
						}

						convertedWeekdays = getOffsettedWeekDays(alignedStartActualWeekDay);

						ZonedDateTime temp = zdt.with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH,  convertedWeekdays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, calculatedFrequency);
						if (temp.getMonthValue() > zdt.getMonthValue()) {
							zdt = zdt.with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH,  convertedWeekdays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, calculatedFrequency - 1);
						} else {
							zdt = temp;
						}
						moveToNextYear = false;
						break;
					}
				}

				if (moveToNextYear) { // tested
					zdt = zdt.with(LocalTime.of(0, 0)).with(TemporalAdjusters.firstDayOfNextYear()).withMonth(allowedMonths.get(0) + (monthValue - 1));
					alignedWeek = zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
					alignedStartOfWeek = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, 1).with(ChronoField.ALIGNED_WEEK_OF_MONTH, alignedWeek);
					alignedStartActualWeekDay = alignedStartOfWeek.getDayOfWeek().getValue();
					int calculatedFrequency = weekFrequency;
					if (weekFrequency == LAST_WEEK) {
						calculatedFrequency = zdt.with(TemporalAdjusters.lastDayOfMonth()).get(ChronoField.ALIGNED_WEEK_OF_MONTH);
					}

					convertedWeekdays = getOffsettedWeekDays(alignedStartActualWeekDay);

					ZonedDateTime temp = zdt.with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH,  convertedWeekdays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, calculatedFrequency);
					if (temp.getMonthValue() > zdt.getMonthValue()) {
						zdt = zdt.with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH,  convertedWeekdays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, calculatedFrequency - 1);
					} else {
						zdt = temp;
					}
				}

				// 2. Move to the nearest feasible time of the day
				nextZdt = compareHourAndMinute(zdt);
			}
			break;
			case HALF_YEARLY_DAY: {
				// values hold the days of the month on which to be scheduled
				// times hold the times on which the schedule is to be executed
				// monthValue holds the month of the quarter (1, 2, 3, 4, 5, 6) to be executed

				List<Integer> allowedMonths = Arrays.asList(1, 7);

				if (values != null && !values.isEmpty() && !values.stream().allMatch(x -> x >= 1 && x <= 31)) {
					throw new IllegalArgumentException("Invalid value range of Days of Week");
				}

				if (monthValue == -1) {
					monthValue = 1;
				}

				// finding the quarter in which the date belongs
				int halfYearlyMonth;
				int month = zdt.getMonthValue();

				if (month >= 1 && month < 7) {
					halfYearlyMonth = 1;
				} else {
					halfYearlyMonth = 7;
				}

				addAndSortValue(zdt.getDayOfMonth());

				int dayOfMonth = zdt.getDayOfMonth();
				int offsetMonth = (month - halfYearlyMonth) + 1;

				// 1. move zdt to the nearest allowed month.
				boolean moveToNextYear = true;
				for (Integer allowedMonth : allowedMonths) {
					if (halfYearlyMonth == allowedMonth) {
						if (offsetMonth == monthValue) { // tested
							for (int day : values) {
								if (day == dayOfMonth) { // tested
									if (times.get(times.size() - 1).isAfter(zdt.toLocalTime())) {
										moveToNextYear = false;
										break;
									}
								} else if (day > dayOfMonth) { // tested
									zdt = zdt.with(LocalTime.of(0, 0)).with(ChronoField.DAY_OF_MONTH, day);
									moveToNextYear = false;
									break;
								}
							}

							if (!moveToNextYear) {
								break;
							}
						} else if (offsetMonth < monthValue) { // tested
							zdt = zdt.withMonth(allowedMonth + monthValue - 1).with(LocalTime.of(0, 0)).with(ChronoField.DAY_OF_MONTH, values.get(0));
							moveToNextYear = false;
							break;
						}
					} else if (allowedMonth > halfYearlyMonth) {
						zdt = zdt.with(LocalTime.of(0, 0)).with(ChronoField.MONTH_OF_YEAR, allowedMonth + monthValue - 1).with(ChronoField.DAY_OF_MONTH, values.get(0));
						moveToNextYear = false;
						break;
					}
				}

				if (moveToNextYear) {
					zdt = zdt.with(LocalTime.of(0, 0)).with(TemporalAdjusters.firstDayOfNextYear()).with(ChronoField.MONTH_OF_YEAR, allowedMonths.get(0) + monthValue - 1).with(ChronoField.DAY_OF_MONTH, values.get(0));
				}

				// 2. Move to the nearest feasible time of the day
				nextZdt = compareHourAndMinute(zdt);
			}
			break;
			case HALF_YEARLY_WEEK:{
				// weekFrequency holds the week at which the schedule executes
				// times holds the times of the day at which the schedule executes
				// values holds the day of the week on which the schedule executes
				// monthValue holds the month of the week (1, 2, 3, 4, 5, 6) to be executed

				if (monthValue == -1) {
					monthValue = 1;
				}

				if(values != null && !values.isEmpty() && !values.stream().allMatch(x -> x >= 1 && x <= 7)) {
					throw new IllegalArgumentException("Invalid value range of Days of Week");
				}

				if(weekFrequency == -1 || weekFrequency < 1 || weekFrequency > LAST_WEEK) {
					weekFrequency = zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
				}

				addAndSortValue(zdt.getDayOfWeek().getValue());

				int month = zdt.getMonthValue();
				List<Integer> allowedMonths = Arrays.asList(1, 7);

				// finding the quarter in which the date belongs
				int halfYearlyMonth;

				if (month >= 1 && month < 7) {
					halfYearlyMonth = 1;
				} else {
					halfYearlyMonth = 7;
				}

				int offsetMonth = (month - halfYearlyMonth) + 1;

				int alignedWeek = zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
				ZonedDateTime alignedStartOfWeek = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, 1).with(ChronoField.ALIGNED_WEEK_OF_MONTH, alignedWeek);
				int alignedStartActualWeekDay = alignedStartOfWeek.getDayOfWeek().getValue();
				List<Integer> convertedWeekdays = getOffsettedWeekDays(alignedStartActualWeekDay);

				int zdtWeekDay = zdt.getDayOfWeek().getValue();
				int zdtAlignedWeekDay = (((7 + zdtWeekDay - alignedStartActualWeekDay) % 7) + 1);

				// 1. Move to the nearest allowed month
				boolean moveToNextYear = true;
				for (Integer allowedMonth : allowedMonths) {
					if (halfYearlyMonth == allowedMonth) {
						if (offsetMonth == monthValue) {
							if (checkWeekOfMonth(zdt)) {
								for (int convertedDayOfWeek: convertedWeekdays) {
									if (zdtAlignedWeekDay == convertedDayOfWeek) { // tested
										if (zdt.toLocalTime().isBefore(times.get(times.size() - 1))) {
											moveToNextYear = false;
											break;
										}
									} else if (zdtAlignedWeekDay < convertedDayOfWeek) { // tested
										zdt = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, convertedDayOfWeek);
										moveToNextYear = false;
										break;
									}
								}
								if (!moveToNextYear) {
									break;
								}
							} else if (weekFrequency == LAST_WEEK) { // tested
								int lastAlignedWeek = zdt.with(TemporalAdjusters.lastDayOfMonth()).get(ChronoField.ALIGNED_WEEK_OF_MONTH);
								if (zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH) < lastAlignedWeek) {
									ZonedDateTime temp = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, convertedWeekdays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, lastAlignedWeek);
									if (temp.getMonthValue() > zdt.getMonthValue()) {
										zdt = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, convertedWeekdays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, lastAlignedWeek - 1);
									} else {
										zdt = temp;
									}
									moveToNextYear = false;
									break;
								}
							} else if (zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH) < weekFrequency) { // tested
								zdt = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, convertedWeekdays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, weekFrequency);
								moveToNextYear = false;
								break;
							}
						} else if (offsetMonth < monthValue) { // tested
							zdt = zdt.with(LocalTime.of(0, 0)).withMonth(allowedMonth + (monthValue - 1));
							alignedWeek = zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
							alignedStartOfWeek = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, alignedWeek).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, 1);
							alignedStartActualWeekDay = alignedStartOfWeek.getDayOfWeek().getValue();
							int calculatedFrequency = weekFrequency;
							if (weekFrequency == LAST_WEEK) {
								calculatedFrequency = zdt.with(TemporalAdjusters.lastDayOfMonth()).get(ChronoField.ALIGNED_WEEK_OF_MONTH);
							}
							convertedWeekdays = getOffsettedWeekDays(alignedStartActualWeekDay);
							ZonedDateTime temp = zdt.with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, convertedWeekdays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, calculatedFrequency);
							if (temp.getMonthValue() > zdt.getMonthValue()) {
								zdt = zdt.with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, convertedWeekdays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, calculatedFrequency - 1);
							} else {
								zdt = temp;
							}
							moveToNextYear = false;
							break;
						}
					} else if (allowedMonth > halfYearlyMonth) { // tested
						zdt = zdt.withDayOfMonth(1).with(LocalTime.of(0, 0)).withMonth(allowedMonth + (monthValue - 1));
						alignedWeek = zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
						alignedStartOfWeek = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, 1).with(ChronoField.ALIGNED_WEEK_OF_MONTH, alignedWeek);
						alignedStartActualWeekDay = alignedStartOfWeek.getDayOfWeek().getValue();
						int calculatedFrequency = weekFrequency;
						if (weekFrequency == LAST_WEEK) {
							calculatedFrequency = zdt.with(TemporalAdjusters.lastDayOfMonth()).get(ChronoField.ALIGNED_WEEK_OF_MONTH);
						}
						convertedWeekdays = getOffsettedWeekDays(alignedStartActualWeekDay);
						ZonedDateTime temp = zdt.with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH,  convertedWeekdays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, calculatedFrequency);
						if (temp.getMonthValue() > zdt.getMonthValue()) {
							zdt = zdt.with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH,  convertedWeekdays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, calculatedFrequency - 1);
						} else {
							zdt = temp;
						}
						moveToNextYear = false;
						break;
					}
				}

				if (moveToNextYear) { // tested
					zdt = zdt.with(LocalTime.of(0, 0)).with(TemporalAdjusters.firstDayOfNextYear()).withMonth(allowedMonths.get(0) + (monthValue - 1));
					alignedWeek = zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH);
					alignedStartOfWeek = zdt.with(LocalTime.of(0, 0)).with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, 1).with(ChronoField.ALIGNED_WEEK_OF_MONTH, alignedWeek);
					alignedStartActualWeekDay = alignedStartOfWeek.getDayOfWeek().getValue();
					int calculatedFrequency = weekFrequency;
					if (weekFrequency == LAST_WEEK) {
						calculatedFrequency = zdt.with(TemporalAdjusters.lastDayOfMonth()).get(ChronoField.ALIGNED_WEEK_OF_MONTH);
					}
					convertedWeekdays = getOffsettedWeekDays(alignedStartActualWeekDay);
					ZonedDateTime temp = zdt.with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH, convertedWeekdays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, calculatedFrequency);
					if (temp.getMonthValue() > zdt.getMonthValue()) {
						zdt = zdt.with(ChronoField.ALIGNED_DAY_OF_WEEK_IN_MONTH,  convertedWeekdays.get(0)).with(ChronoField.ALIGNED_WEEK_OF_MONTH, calculatedFrequency - 1);
					} else {
						zdt = temp;
					}
				}

				// 2. Move to the nearest feasible time of the day
				nextZdt = compareHourAndMinute(zdt);
			}
		}
		return nextZdt.toEpochSecond() - 1;
	}

	private List<Integer> getOffsettedWeekDays(int alignedStartActualWeekDay) {
		List<Integer> convertedWeekdays;
		convertedWeekdays = new ArrayList<>();
		for (int dayOfWeek : values) {
			convertedWeekdays.add(((7 + dayOfWeek - alignedStartActualWeekDay) % 7) + 1);
		}
		Collections.sort(convertedWeekdays);
		return convertedWeekdays;
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
			if (zdt.get(ChronoField.ALIGNED_WEEK_OF_MONTH) == 1) { //Special handling to prevent it from going to prev month
				zdt = zdt.withDayOfMonth(1);
			}
			else {
				zdt = zdt.with(ChronoField.DAY_OF_WEEK, 1);
			}
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

	private boolean checkWeekOfMonthForYearly(ZonedDateTime zdt) {
		if(weekFrequency == LAST_WEEK) {
			for(Integer value : yearlyDayOfWeekValues) {
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
				newZdt = zdt.with(LocalTime.of(0, 0)).plusDays(dayOfWeek - currentDay);
			}
			else if(currentDay > dayOfWeek) {
				newZdt = zdt.with(LocalTime.of(0, 0)).plusDays(dayOfWeek + 7 - currentDay);
			}
			newZdt = newZdt.with(ChronoField.ALIGNED_WEEK_OF_MONTH, weekFrequency);
		}
		return newZdt;
	}

	private void addAndSortYearlyDayOfWeekValues(int val) {
		addYearlyDayOfWeekValues(val);
		Collections.sort(values);
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

	public enum FrequencyType {
		/**
		 * Schedule Executes only once; The execution time is either on the day of `startTime` and at time the time specified in {@link ScheduleInfo#times} , or the day after if the `startTime` exceeds the execution time of the day.
		 */
		DO_NOT_REPEAT,
		/**
		 * Schedule Executes everyday
		 */
		DAILY,
		WEEKLY,
		MONTHLY_DAY,
		MONTHLY_WEEK,
		YEARLY,
		YEARLY_WEEK,
		QUARTERLY_DAY,
		QUARTERLY_WEEK,
		HALF_YEARLY_DAY,
		HALF_YEARLY_WEEK;
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
						builder.append(FacilioUtil.getNumberWithSuffix(values.get(itr)));
					}
				}
				else {
					builder.append(FacilioUtil.getNumberWithSuffix(zdt.getDayOfMonth()));
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
						.append(FacilioUtil.getNumberWithSuffix(zdt.getDayOfMonth()))
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
				return FacilioUtil.getNumberWithSuffix(weekFrequency);
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

	public List<DateRange> getTimeIntervals(long startTime, long endTime) {
		List<DateRange> intervals = new ArrayList<>();
		startTime = startTime/1000;
		endTime = endTime/1000;

		long currentEndTime = nextExecutionTime(startTime);
		while (currentEndTime <= endTime) {
			intervals.add(new DateRange(startTime*1000, (currentEndTime*1000)-1));
			startTime = currentEndTime;
			currentEndTime = nextExecutionTime(startTime);
		}
		return intervals;
	}
}
