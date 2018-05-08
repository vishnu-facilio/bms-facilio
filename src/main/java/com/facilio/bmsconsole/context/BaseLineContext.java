package com.facilio.bmsconsole.context;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.WeekFields;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.DateRange;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.DateTimeUtil;

public class BaseLineContext {
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private long spaceId = -1;
	public long getSpaceId() {
		return spaceId;
	}
	public void setSpaceId(long spaceId) {
		this.spaceId = spaceId;
	}

	private RangeType rangeType;
	public RangeType getRangeTypeEnum() {
		return rangeType;
	}
	public void setRangeType(RangeType rangeType) {
		this.rangeType = rangeType;
	}
	public int getRangeType() {
		if (rangeType != null) {
			return rangeType.getVal();
		}
		return -1;
	}
	public void setRangeType(int rangeType) {
		this.rangeType = RangeType.valueOf(rangeType);
	}
	
	private long startTime = -1;
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	private long endTime = -1;
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	private long reportId = -1;
	public long getReportId() {
		return reportId;
	}
	public void setReportId(long reportId) {
		this.reportId = reportId;
	}
	
	private Boolean isAdjust;
	public Boolean getIsAdjust() {
		return isAdjust;
	}
	public void setIsAdjust(boolean isAdjust) {
		this.isAdjust = isAdjust;
	}
	public boolean isAdjust() {
		if (isAdjust != null) {
			return isAdjust.booleanValue();
		}
		return true;
	}
	
	public Condition getBaseLineCondition(FacilioField field, DateRange range) {
		String blRange = calculateRange(range.getStartTime(), range.getEndTime(), isAdjust());
		if (blRange != null && !blRange.isEmpty()) {
			return CriteriaAPI.getCondition(field, blRange, DateOperators.BETWEEN);
		}
		return null;
	}
	
	private RangeType getDynamicPreviousType(ZonedDateTime dataStartZdt, ZonedDateTime dataEndZdt) {
		if (DateTimeUtil.isSameHour(dataStartZdt, dataEndZdt)) {
			return RangeType.PREVIOUS_HOUR;
		}
		else if (DateTimeUtil.isSameDay(dataStartZdt, dataEndZdt)) {
			return RangeType.PREVIOUS_DAY;
		}
		else if (DateTimeUtil.isSameWeek(dataStartZdt, dataEndZdt)) {
			return RangeType.PREVIOUS_WEEK;
		}
		else if (DateTimeUtil.isSameMonth(dataStartZdt, dataEndZdt)) {
			return RangeType.PREVIOUS_MONTH;
		}
		else if (DateTimeUtil.isSameYear(dataStartZdt, dataEndZdt)) {
			return RangeType.PREVIOUS_YEAR;
		}
		else {
			return null;
		}
	}
	
	private Pair<ZonedDateTime, ZonedDateTime> populateStartAndEndTimes(RangeType type, ZonedDateTime dataStartZdt, ZonedDateTime dataEndZdt) {
		ZonedDateTime blStartZdt = null, blEndZdt = null;
		switch (type) {
			case PREVIOUS:
				throw new RuntimeException("Shouldn't be here!!");
			case PREVIOUS_HOUR:
				blStartZdt = dataStartZdt.minusHours(1).withMinute(0);
				blEndZdt = dataStartZdt.withMinute(59);
				break;
			case PREVIOUS_DAY:
				blStartZdt = dataStartZdt.minusDays(1);
				blEndZdt = DateTimeUtil.getDayEndTimeOf(blStartZdt);
				break;
			case PREVIOUS_WEEK:
				blStartZdt = dataStartZdt.minusWeeks(1);
				blEndZdt = DateTimeUtil.getWeekEndTimeOf(blStartZdt);
				break;
			case PREVIOUS_MONTH:
				blStartZdt = DateTimeUtil.getMonthStartTimeOf(dataStartZdt.minusMonths(1));
				blEndZdt = DateTimeUtil.getMonthEndTimeOf(blStartZdt);
				break;
			case PREVIOUS_YEAR:
				blStartZdt = DateTimeUtil.getYearStartTimeOf(dataStartZdt.minusYears(1));
				blEndZdt = DateTimeUtil.getYearEndTimeOf(blStartZdt);
				break;
			default:
				blStartZdt = DateTimeUtil.getDateTime(startTime);
				blEndZdt = DateTimeUtil.getDateTime(endTime);
		}
		return new ImmutablePair<ZonedDateTime, ZonedDateTime>(blStartZdt, blEndZdt);
	}
	
	private String calculateRange(long dataStartTime, long dataEndTime, boolean adjust) {
		ZonedDateTime dataStartZdt = DateTimeUtil.getDateTime(dataStartTime);
		ZonedDateTime dataEndZdt = DateTimeUtil.getDateTime(dataEndTime);
		
		System.out.println(dataStartZdt+" - "+dataStartZdt.getDayOfWeek());
		System.out.println(dataEndZdt+" - "+dataEndZdt.getDayOfWeek());
		
		System.out.println("#####");
		
		Duration dataDuration = Duration.between(dataStartZdt, dataEndZdt);
		RangeType type = rangeType;
		
		if (type == RangeType.PREVIOUS) {
			type = getDynamicPreviousType(dataStartZdt, dataEndZdt);
		}
		if (type != null) {
			Pair<ZonedDateTime, ZonedDateTime> zdtPair = populateStartAndEndTimes(type, dataStartZdt, dataEndZdt);
			ZonedDateTime blStartZdt = zdtPair.getLeft(), blEndZdt = zdtPair.getRight();
			
			if(adjust) {
				WeekFields weekFields = DateTimeUtil.getWeekFields();
				switch (type) {
					case PREVIOUS_HOUR:
					{
						blStartZdt = adjustStartMinute(dataStartZdt, blStartZdt);
						blEndZdt = blStartZdt.plus(dataDuration);
					}break;
					case ANY_HOUR:
					{
						blStartZdt = adjustStartMinute(dataStartZdt, blStartZdt);
						blEndZdt = adjustEndMinute(dataEndZdt, blEndZdt);
					}break;
					case PREVIOUS_DAY:
					{
						blStartZdt = adjustStartTime(dataStartZdt, blStartZdt);
						blEndZdt = blStartZdt.plus(dataDuration);
					}break;
					case ANY_DAY:
					{
						blStartZdt = adjustStartTime(dataStartZdt, blStartZdt);
						blEndZdt = adjustEndTime(dataEndZdt, blEndZdt);
					}break;
					case PREVIOUS_WEEK:
					{
						blStartZdt = adjustStartOfWeek(weekFields, dataStartZdt, blStartZdt);
						blEndZdt = blStartZdt.plus(dataDuration);
					}break;
					case ANY_WEEK:
					{
						blStartZdt = adjustStartOfWeek(weekFields, dataStartZdt, blStartZdt);
						if (DateTimeUtil.isSameWeek(dataStartZdt, dataEndZdt)) {
							blEndZdt = adjustEndOfWeek(weekFields, dataEndZdt, blEndZdt);
						}
					}break;
					case PREVIOUS_MONTH:
					{
						blStartZdt = adjustStartOfMonth(weekFields, dataStartZdt, blStartZdt);
						blEndZdt = blStartZdt.plus(dataDuration);
					}break;
					case ANY_MONTH:
					{
						blStartZdt = adjustStartOfMonth(weekFields, dataStartZdt, blStartZdt);
						if(DateTimeUtil.isSameMonth(dataStartZdt, dataEndZdt)) {
							blEndZdt = adjustEndOfMonth(weekFields, dataEndZdt, blEndZdt);
						}
					}break;
					case PREVIOUS_YEAR:
					{
						blStartZdt = adjustStartOfYear(weekFields, dataStartZdt, blStartZdt);
						blEndZdt = blStartZdt.plus(dataDuration);
					}break;
					case ANY_YEAR:
					{
						blStartZdt = adjustStartOfYear(weekFields, dataStartZdt, blStartZdt);
						if (DateTimeUtil.isSameYear(dataStartZdt, dataEndZdt)) {
							blEndZdt = adjustEndOfYear(weekFields, dataEndZdt, blEndZdt);
						}
					}break;
					case CUSTOM:
					{
						Duration blDuration = Duration.between(blStartZdt, blEndZdt);
						blStartZdt = adjustStartOfWeek(weekFields, dataStartZdt, blStartZdt);
						if (dataDuration.compareTo(blDuration) < 0) {
							blEndZdt = blStartZdt.plus(dataDuration);
						}
					}break;
					case PREVIOUS:
						throw new RuntimeException("Cannot be here!!");
				}
			}
			else {
				Duration blDuration = Duration.between(blStartZdt, blEndZdt);
				blStartZdt = adjustStartTime(dataStartZdt, blStartZdt);
				if (dataDuration.compareTo(blDuration) < 0) {
					blEndZdt = blStartZdt.plus(dataDuration);
				}
			}
			System.out.println(blStartZdt+" - "+blStartZdt.getDayOfWeek());
			System.out.println(blEndZdt+" - "+blEndZdt.getDayOfWeek());
			return blStartZdt.toInstant().toEpochMilli()+", "+blEndZdt.toInstant().toEpochMilli();
		}
		return null;
	}
	
	private ZonedDateTime adjustStartOfYear(WeekFields weekFields, ZonedDateTime dataStartZdt, ZonedDateTime blStartZdt) {
		int startWeekOfYear = dataStartZdt.get(weekFields.weekOfYear());
		blStartZdt = blStartZdt.with(weekFields.weekOfYear(), startWeekOfYear);
		return adjustStartOfWeek(weekFields, dataStartZdt, blStartZdt);
	}
	private ZonedDateTime adjustEndOfYear(WeekFields weekFields, ZonedDateTime dataEndZdt, ZonedDateTime blEndZdt) {
		int endWeekOfYear = dataEndZdt.get(weekFields.weekOfYear());
		blEndZdt = blEndZdt.with(weekFields.weekOfYear(), endWeekOfYear);
		return adjustEndOfWeek(weekFields, dataEndZdt, blEndZdt);
	}
	
	private ZonedDateTime adjustStartOfMonth(WeekFields weekFields, ZonedDateTime dataStartZdt, ZonedDateTime blStartZdt) {
		int startWeekOfMonth = dataStartZdt.get(weekFields.weekOfMonth());
		blStartZdt = blStartZdt.with(weekFields.weekOfMonth(), startWeekOfMonth);
		return adjustStartOfWeek(weekFields, dataStartZdt, blStartZdt);
	}
	private ZonedDateTime adjustEndOfMonth(WeekFields weekFields, ZonedDateTime dataEndZdt, ZonedDateTime blEndZdt) {
		int endWeekOfMonth = dataEndZdt.get(weekFields.weekOfMonth());
		blEndZdt = blEndZdt.with(weekFields.weekOfMonth(), endWeekOfMonth);
		return adjustEndOfWeek(weekFields, dataEndZdt, blEndZdt);
	}
	
	private ZonedDateTime adjustStartOfWeek(WeekFields weekFields, ZonedDateTime dataStartZdt, ZonedDateTime blStartZdt) {
		int startDayOfWeek = dataStartZdt.get(weekFields.dayOfWeek()); 
		blStartZdt = blStartZdt.with(weekFields.dayOfWeek(), startDayOfWeek);
		return adjustStartTime(dataStartZdt, blStartZdt);
	}
	private ZonedDateTime adjustEndOfWeek(WeekFields weekFields, ZonedDateTime dataEndZdt, ZonedDateTime blEndZdt) {
		int endDayOfWeek = dataEndZdt.get(weekFields.dayOfWeek());
		blEndZdt = blEndZdt.with(weekFields.dayOfWeek(), endDayOfWeek);
		return adjustEndTime(dataEndZdt, blEndZdt);
	}
	
	private ZonedDateTime adjustStartTime(ZonedDateTime dataStartZdt, ZonedDateTime blStartZdt) {
		if(dataStartZdt.toLocalTime().isAfter(blStartZdt.toLocalTime())) {
			return blStartZdt.with(dataStartZdt.toLocalTime());
		}
		return blStartZdt; 
	}
	private ZonedDateTime adjustEndTime(ZonedDateTime dataEndZdt, ZonedDateTime blEndZdt) {
		if(dataEndZdt.toLocalTime().isBefore(blEndZdt.toLocalTime())) {
			return blEndZdt.with(dataEndZdt.toLocalTime());
		} 
		return blEndZdt;
	}
	
	private ZonedDateTime adjustStartMinute(ZonedDateTime dataStartZdt, ZonedDateTime blStartZdt) {
		if(dataStartZdt.getMinute() > blStartZdt.getMinute()) {
			return blStartZdt.withMinute(dataStartZdt.getMinute());
		}
		return blStartZdt; 
	}
	private ZonedDateTime adjustEndMinute(ZonedDateTime dataEndZdt, ZonedDateTime blEndZdt) {
		if(dataEndZdt.toLocalTime().isBefore(blEndZdt.toLocalTime())) {
			return blEndZdt.withMinute(dataEndZdt.getMinute());
		} 
		return blEndZdt;
	}
	
	public static enum RangeType {
		PREVIOUS,
		PREVIOUS_HOUR,
		PREVIOUS_DAY,
		PREVIOUS_WEEK,
		PREVIOUS_MONTH,
		PREVIOUS_YEAR,
		ANY_HOUR,
		ANY_DAY,
		ANY_WEEK,
		ANY_MONTH,
		ANY_YEAR,
		CUSTOM
		;
		
		public int getVal() {
			return ordinal()+1;
		}
		
		public static RangeType valueOf (int val) {
			if(val > 0 && val <= values().length) {
				return values()[val - 1];
			}
			return null;
		}
	}

}
