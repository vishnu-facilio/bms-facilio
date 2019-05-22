package com.facilio.modules;

import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.time.DateRange;
import com.facilio.modules.fields.FacilioField;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;

public class BaseLineContext {
	private static final Logger LOGGER = LogManager.getLogger(BaseLineContext.class.getName());
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
	
	private AdjustType adjustType;
	public AdjustType getAdjustTypeEnum() {
		return adjustType;
	}
	public void setAdjustType(AdjustType adjustType) {
		this.adjustType = adjustType;
	}
	public int getAdjustType() {
		if (adjustType != null) {
			return adjustType.getValue();
		}
		return -1;
	}
	public void setAdjustType(int adjustType) {
		this.adjustType = AdjustType.valueOf(adjustType);
	}
	
	public Condition getBaseLineCondition(FacilioField field, DateRange range) {
		DateRange blRange = calculateBaseLineRange(range, adjustType);
		if (blRange != null) {
			return CriteriaAPI.getCondition(field, blRange.toString(), DateOperators.BETWEEN);
		}
		return null;
	}
	
	public Condition getBaseLineCondition(FacilioField field, DateRange range, AdjustType type) {
		DateRange blRange = calculateBaseLineRange(range, type);
		if (blRange != null) {
			return CriteriaAPI.getCondition(field, blRange.toString(), DateOperators.BETWEEN);
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
		else if (DateTimeUtil.isSameQuarter(dataStartZdt, dataEndZdt)) {
			return RangeType.PREVIOUS_QUARTER;
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
			case PREVIOUS_QUARTER:
				blStartZdt = DateTimeUtil.getQuarterStartTimeOf(dataStartZdt).minusMonths(3);
				blEndZdt = DateTimeUtil.getQuarterEndTimeOf(blStartZdt);
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
	
	public DateRange calculateBaseLineRange(DateRange dataRange, AdjustType adjustType) {
		long dataStartTime = dataRange.getStartTime(), dataEndTime = dataRange.getEndTime();
		
		ZonedDateTime dataStartZdt = DateTimeUtil.getDateTime(dataStartTime);
		ZonedDateTime dataEndZdt = DateTimeUtil.getDateTime(dataEndTime);
		
		LOGGER.debug("Data Range : ");;
		LOGGER.debug(dataStartZdt+" - "+dataStartZdt.getDayOfWeek());
		LOGGER.debug(dataEndZdt+" - "+dataEndZdt.getDayOfWeek());
		LOGGER.debug("Range Type : "+rangeType);
		LOGGER.debug("Adjust Type : "+adjustType);
		
		Duration dataDuration = Duration.between(dataStartZdt, dataEndZdt);
		RangeType type = rangeType;
		
		if (type == RangeType.PREVIOUS) {
			type = getDynamicPreviousType(dataStartZdt, dataEndZdt);
		}
		if (type != null) {
			Pair<ZonedDateTime, ZonedDateTime> zdtPair = populateStartAndEndTimes(type, dataStartZdt, dataEndZdt);
			ZonedDateTime blStartZdt = zdtPair.getLeft(), blEndZdt = zdtPair.getRight();
			
			if(adjustType != null && adjustType != AdjustType.NONE) {
				Pair<ZonedDateTime, ZonedDateTime> baseLineRange = null;
				switch (adjustType) {
					case WEEK:
						baseLineRange = weeklyAdjustment(type, blStartZdt, blEndZdt, dataStartZdt, dataEndZdt, dataDuration);
						break;
					case DATE:
						baseLineRange = dateAdjustment(type, blStartZdt, blEndZdt, dataStartZdt, dataEndZdt, dataDuration, false);
						break;
					case MONTH_AND_DATE:
						baseLineRange = monthAndDateAdjustment(type, blStartZdt, blEndZdt, dataStartZdt, dataEndZdt, dataDuration);
						break;
					case FULL_MONTH_DATE:
						baseLineRange = dateAdjustment(type, blStartZdt, blEndZdt, dataStartZdt, dataEndZdt, dataDuration, true);
						break;
					default:
						break;
				}
				blStartZdt = baseLineRange.getLeft();
				blEndZdt = baseLineRange.getRight();
			}
			else {
				Duration blDuration = Duration.between(blStartZdt, blEndZdt);
				
				if (type != RangeType.PREVIOUS_HOUR && type != RangeType.ANY_HOUR) {
					blStartZdt = adjustStartTime(dataStartZdt, blStartZdt);
				}
				if (dataDuration.compareTo(blDuration) < 0) {
					blEndZdt = blStartZdt.plus(dataDuration);
				}
			}
			LOGGER.debug("Base Line Range : ");
			LOGGER.debug(blStartZdt+" - "+blStartZdt.getDayOfWeek());
			LOGGER.debug(blEndZdt+" - "+blEndZdt.getDayOfWeek());
			return new DateRange(blStartZdt.toInstant().toEpochMilli(), blEndZdt.toInstant().toEpochMilli());
		}
		return null;
	}
	
	private Pair<ZonedDateTime, ZonedDateTime> monthAndDateAdjustment(RangeType type, ZonedDateTime blStartZdt, ZonedDateTime blEndZdt, ZonedDateTime dataStartZdt, ZonedDateTime dataEndZdt, Duration dataDuration) {
		switch (type) {
			case PREVIOUS:
				throw new RuntimeException("Cannot be here!!");
			case PREVIOUS_YEAR:
				blStartZdt = blStartZdt.withMonth(dataStartZdt.getMonthValue());
				blStartZdt = withDate(blStartZdt, dataStartZdt,false);
				blStartZdt = adjustStartTime(dataStartZdt, blStartZdt);
				blEndZdt = blStartZdt.plusYears(dataEndZdt.getYear() - dataStartZdt.getYear()).with(blEndZdt.toLocalTime());
				blEndZdt = blEndZdt.withMonth(dataEndZdt.getMonthValue());
				blEndZdt = withDate(blEndZdt, dataEndZdt, blStartZdt.equals(blStartZdt.with(TemporalAdjusters.firstDayOfMonth())));
				blEndZdt = adjustEndTime(dataEndZdt, blEndZdt);
			case ANY_YEAR:
				blStartZdt = blStartZdt.withMonth(dataStartZdt.getMonthValue());
				blStartZdt = withDate(blStartZdt, dataStartZdt,false);
				blStartZdt = adjustStartTime(dataStartZdt, blStartZdt);
				if (DateTimeUtil.isSameYear(dataStartZdt, dataEndZdt)) {
					blEndZdt = blEndZdt.withMonth(dataEndZdt.getMonthValue());
					blEndZdt = withDate(blEndZdt, dataEndZdt, blStartZdt.equals(blStartZdt.with(TemporalAdjusters.firstDayOfMonth())));
				}
				blEndZdt = adjustEndTime(dataEndZdt, blEndZdt);
				break;
			default:
				throw new IllegalArgumentException("Unsupported adjust type for base line : "+id);
		}
		return Pair.of(blStartZdt, blEndZdt);
	}
	
	private Pair<ZonedDateTime, ZonedDateTime> dateAdjustment(RangeType type, ZonedDateTime blStartZdt, ZonedDateTime blEndZdt, ZonedDateTime dataStartZdt, ZonedDateTime dataEndZdt, Duration dataDuration, boolean lastDayCheck) {
		switch (type) {
			case PREVIOUS:
				throw new RuntimeException("Cannot be here!!");
			case PREVIOUS_MONTH:
				blStartZdt = withDate(blStartZdt, dataStartZdt,false);
				blStartZdt = adjustStartTime(dataStartZdt, blStartZdt);
				blEndZdt = blStartZdt.plusMonths(dataEndZdt.getMonthValue() - dataStartZdt.getMonthValue()).with(blEndZdt.toLocalTime());
				blEndZdt = withDate(blEndZdt, dataEndZdt,lastDayCheck && blStartZdt.equals(blStartZdt.with(TemporalAdjusters.firstDayOfMonth())));
				blEndZdt = adjustEndTime(dataEndZdt, blEndZdt);
				break;
			case ANY_MONTH:
				blStartZdt = withDate(blStartZdt, dataStartZdt,false);
				blStartZdt = adjustStartTime(dataStartZdt, blStartZdt);
				if (DateTimeUtil.isSameMonth(dataStartZdt, dataEndZdt)) {
					blEndZdt = withDate(blEndZdt, dataEndZdt,lastDayCheck && blStartZdt.equals(blStartZdt.with(TemporalAdjusters.firstDayOfMonth())));
				}
				blEndZdt = adjustEndTime(dataEndZdt, blEndZdt);
				break;
			case PREVIOUS_QUARTER:
				blStartZdt = blStartZdt.plusMonths(dataStartZdt.getMonthValue() - DateTimeUtil.getQuarterStartTimeOf(dataStartZdt).getMonthValue());
				blStartZdt = withDate(blStartZdt, dataStartZdt,false);
				blStartZdt = adjustStartTime(dataStartZdt, blStartZdt);
				blEndZdt = blStartZdt.plusMonths(dataEndZdt.getMonthValue() - dataStartZdt.getMonthValue()).with(blEndZdt.toLocalTime());
				blEndZdt = withDate(blEndZdt, dataEndZdt,lastDayCheck && blStartZdt.equals(DateTimeUtil.getQuarterStartTimeOf(blStartZdt).with(blStartZdt.toLocalTime())));
				blEndZdt = adjustEndTime(dataEndZdt, blEndZdt);
				break;
			case ANY_QUARTER:
				blStartZdt = blStartZdt.plusMonths(dataStartZdt.getMonthValue() - DateTimeUtil.getQuarterStartTimeOf(dataStartZdt).getMonthValue());
				blStartZdt = withDate(blStartZdt, dataStartZdt,false);
				blStartZdt = adjustStartTime(dataStartZdt, blStartZdt);
				if (DateTimeUtil.isSameQuarter(dataStartZdt, dataEndZdt)) {
					blEndZdt = withDate(blEndZdt, dataEndZdt,lastDayCheck && blStartZdt.equals(DateTimeUtil.getQuarterStartTimeOf(blStartZdt).with(blStartZdt.toLocalTime())));
				}
				blEndZdt = adjustEndTime(dataEndZdt, blEndZdt);
				break;
			default:
				throw new IllegalArgumentException("Unsupported adjust type for base line : "+id);
		}
		return Pair.of(blStartZdt, blEndZdt);
	}
	
	private Pair<ZonedDateTime, ZonedDateTime> weeklyAdjustment(RangeType type, ZonedDateTime blStartZdt, ZonedDateTime blEndZdt, ZonedDateTime dataStartZdt, ZonedDateTime dataEndZdt, Duration dataDuration) {
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
				blEndZdt = adjustEndMinute(dataEndZdt, blStartZdt, blEndZdt, dataDuration);
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
			case PREVIOUS_QUARTER:
			{
				blStartZdt = adjustStartOfQuarter(weekFields, dataStartZdt, blStartZdt);
				blEndZdt = blStartZdt.plus(dataDuration);
			}break;
			case ANY_QUARTER:
			{
				blStartZdt = adjustStartOfQuarter(weekFields, dataStartZdt, blStartZdt);
				if(DateTimeUtil.isSameQuarter(dataStartZdt, dataEndZdt)) {
					blEndZdt = adjustEndOfQuarter(weekFields, dataEndZdt, blEndZdt);
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
		return Pair.of(blStartZdt, blEndZdt);
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
	private ZonedDateTime adjustStartOfQuarter(WeekFields weekFields, ZonedDateTime dataStartZdt, ZonedDateTime blStartZdt) {
		ZonedDateTime quarterStartTime = DateTimeUtil.getQuarterStartTimeOf(dataStartZdt);
		long numberOfWeeksFromQuarterStart = dataStartZdt.get(weekFields.weekOfYear()) - quarterStartTime.get(weekFields.weekOfYear());
		blStartZdt = blStartZdt.plusWeeks(numberOfWeeksFromQuarterStart);
		return adjustStartOfWeek(weekFields, dataStartZdt, blStartZdt);
	}
	private ZonedDateTime adjustEndOfQuarter(WeekFields weekFields, ZonedDateTime dataEndZdt, ZonedDateTime blEndZdt) {
		ZonedDateTime quarterEndTime = DateTimeUtil.getQuarterEndTimeOf(dataEndZdt);
		long numberOfWeeksToQuarterEnd = quarterEndTime.get(weekFields.weekOfYear()) - dataEndZdt.get(weekFields.weekOfYear());
		blEndZdt = blEndZdt.minusWeeks(numberOfWeeksToQuarterEnd);
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
	private ZonedDateTime adjustEndMinute(ZonedDateTime dataEndZdt,ZonedDateTime blStartZdt, ZonedDateTime blEndZdt, Duration dataDuration) {
		Duration blDuration = Duration.between(blStartZdt, blEndZdt);
		if (dataDuration.compareTo(blDuration) < 0) {
			return blEndZdt.withMinute(dataEndZdt.getMinute());
		} 
		return blEndZdt;
	}
	
	private ZonedDateTime withDate(ZonedDateTime blZdt, ZonedDateTime dataZdt, boolean lastDayCheck) {
		ZonedDateTime blLastDayZdt = blZdt.with(TemporalAdjusters.lastDayOfMonth());
		if (lastDayCheck) {
			ZonedDateTime dataLastDayZdt = dataZdt.with(TemporalAdjusters.lastDayOfMonth());
			if (dataZdt.getDayOfMonth() == dataLastDayZdt.getDayOfMonth()) {
				return blLastDayZdt;
			}
		}
		if (dataZdt.getDayOfMonth() > blLastDayZdt.getDayOfMonth()) {
			return blLastDayZdt;
		}
		return blZdt.withDayOfMonth(dataZdt.getDayOfMonth());
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
		CUSTOM,
		
		PREVIOUS_QUARTER,
		ANY_QUARTER
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
	
	public static enum AdjustType {
		NONE,
		WEEK,
		DATE, //Monthly
		MONTH_AND_DATE, //Yearly
		FULL_MONTH_DATE //Monthly & Quarterly
		;
		
		public int getValue() {
			return ordinal();
		}
		
		public static AdjustType valueOf (int value) {
			if(value >= 0 && value < values().length) {
				return values()[value];
			}
			return null;
		}
	}
}
