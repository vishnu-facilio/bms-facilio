package com.facilio.bmsconsole.context;

import com.facilio.time.DateTimeUtil;
import org.apache.kafka.common.utils.CircularIterator;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;

public class BusinessHoursList  extends ArrayList<BusinessHourContext>{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public long getNextPossibleTime(long time, int additional) {
		if (additional <= 0) {
			return time;
		}

		ZonedDateTime zonedDateTime = DateTimeUtil.getZonedDateTime(time);
		DayOfWeek dayOfWeek = zonedDateTime.getDayOfWeek();
		int hour = zonedDateTime.getHour();
		int minute = zonedDateTime.getMinute();
		int second = zonedDateTime.getSecond();
		LocalTime localTime = LocalTime.of(hour, minute, second);

		boolean setStartTimeAsLocalTime = false;
		List<BusinessHourContext> list = new ArrayList<>();
		int index = 0;
		boolean matchFound = false;
		boolean skip = false;

		int count = 0;
		for (BusinessHourContext businessHour : this) {
			LocalTime startTime = businessHour.getStartTimeOrDefault();
			LocalTime endTime = businessHour.getEndTimeOrDefault();

			if (skip) {
				count = index;
				index = 0;
				matchFound = true;
			}
			else {
				if (!matchFound &&
						businessHour.getDayOfWeekEnum().getValue() >= dayOfWeek.getValue()) {
					if (startTime.isBefore(endTime)) {
						if (endTime.isAfter(localTime)) {
							count = index;
							index = 0;
							matchFound = true;
						} else {
							setStartTimeAsLocalTime = true;
							skip = true;
						}
					}
					else {
						// night shift case
						count = index;
						index = 0;
						matchFound = true;
					}
				}
			}
			list.add(index++, businessHour);
		}
		CircularIterator<BusinessHourContext> iterator = new CircularIterator(list);

		BusinessHourContext businessHour = null;
		LocalTime newTime = null;
		ZonedDateTime newTimeDate = null;
		int cycle = 0;
		while (iterator.hasNext()) {
			businessHour = iterator.next();

			LocalTime startTime = businessHour.getStartTimeAsLocalTime();
			if (startTime == null) {
				startTime = LocalTime.MIN;
			}
			if (setStartTimeAsLocalTime) {
				localTime = LocalTime.ofSecondOfDay(startTime.toSecondOfDay());
			}
			else if (localTime.isBefore(startTime)) {
				localTime = LocalTime.ofSecondOfDay(startTime.toSecondOfDay());
			}

			LocalTime endTime = businessHour.getEndTimeAsLocalTime();
			if (endTime == null) {
				endTime = LocalTime.MAX;
			}

			ZonedDateTime localTimeDate = getTimeStamp(businessHour.getDayOfWeek(), localTime, cycle, time);
			ZonedDateTime startTimeDate = getTimeStamp(businessHour.getDayOfWeek(), startTime, cycle, time);
			if (localTimeDate.isBefore(startTimeDate)) {
				localTimeDate = startTimeDate;
			}
			ZonedDateTime endTimeDate = getTimeStamp(businessHour.getDayOfWeek(), endTime, cycle, time);
			if (startTime.isAfter(endTime)) {
				// night shift use case
				endTimeDate = endTimeDate.plusDays(1);
			}

			if (++count == size()) {
				cycle++;
				count = 0;
			}

			if (localTimeDate.toEpochSecond() < (int) (time / 1000)) {
				continue;
			}

			newTimeDate = DateTimeUtil.getDateTime(localTimeDate.toInstant().toEpochMilli())
					.plusSeconds(additional);
			if (!setStartTimeAsLocalTime && localTimeDate.isAfter(endTimeDate)) {
				setStartTimeAsLocalTime = true;
				continue;
			}
			additional = (int) (newTimeDate.toInstant().toEpochMilli() / 1000) - (int) (endTimeDate.toInstant().toEpochMilli() / 1000);
			if (additional <= 0) {
				break;
			}
			setStartTimeAsLocalTime = true;
		}

		return newTimeDate.toInstant().toEpochMilli();
	}

	private ZonedDateTime getTimeStamp(int dayOfWeek, LocalTime localTime, int addWeeks, long time) {
		ZonedDateTime instance = DateTimeUtil.getDateTime(time)
				.with(WeekFields.of(DayOfWeek.MONDAY, 1).dayOfWeek(), dayOfWeek)
				.plusWeeks(addWeeks)
				.withHour(localTime.getHour())
				.withMinute(localTime.getMinute())
				.withSecond(localTime.getSecond());
		return instance;
	}
}
