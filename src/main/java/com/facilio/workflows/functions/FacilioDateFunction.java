package com.facilio.workflows.functions;

import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.modules.BaseLineContext;
import com.facilio.modules.BaseLineContext.AdjustType;
import com.facilio.scriptengine.annotation.ScriptNameSpace;
import com.facilio.scriptengine.exceptions.FunctionParamException;
import com.facilio.scriptengine.systemfunctions.FacilioNameSpaceConstants;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.util.FacilioUtil;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

//@ScriptNameSpace(nameSpace = FacilioNameSpaceConstants.DATE_FUNCTION)
/*date function is moved to script engine*/
public class FacilioDateFunction {
	public Object minsToHour(Map<String, Object> globalParam, Object... objects) throws Exception {

         checkParam(objects);

         if(objects[0] == null) {
            return false;
         }
		Double mins = Double.parseDouble(objects[0].toString());
		Double hours = UnitsUtil.convert(mins, Unit.MIN, Unit.HOUR);
		return hours;
	};

	public Object secToHour(Map<String, Object> globalParam, Object... objects) throws Exception {

         checkParam(objects);

         if(objects[0] == null) {
            return false;
         }
		Double secs = Double.parseDouble(objects[0].toString());
		Double hours = UnitsUtil.convert(secs, Unit.SEC, Unit.HOUR);
		return hours;
	};

	public Object hourToDay(Map<String, Object> globalParam, Object... objects) throws Exception {

          checkParam(objects);

          if(objects[0] == null) {
             return false;
          }
		Double hours = Double.parseDouble(objects[0].toString());
		Double day = UnitsUtil.convert(hours, Unit.HOUR, Unit.DAY);
		return day;
	};

	public Object getCurrentMonthDays(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);

		int days = 0;
		if(objects == null || objects.length == 0) {

			ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(DateTimeUtil.getMonthStartTime());
			YearMonth yearMonthObject = YearMonth.of(zdt.getYear(), zdt.getMonthValue());
			days = yearMonthObject.lengthOfMonth();
		}
		else {
			Long startTime = (long) Double.parseDouble(objects[0].toString());
			ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(startTime);
			YearMonth yearMonthObject = YearMonth.of(zdt.getYear(), zdt.getMonthValue());
			days = yearMonthObject.lengthOfMonth();
		}

		return days;
	};

	public Object getLastMonthDays(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);

		int days = DateTimeUtil.getDaysBetween(DateTimeUtil.getMonthStartTime(-1), DateTimeUtil.getMonthStartTime()-1);

		return days;
	};

	public Object getDaysBetween(Map<String, Object> globalParam, Object... objects) throws Exception {

         checkParam(objects);

         if(objects[0] == null || objects[1] == null) {
            return null;
         }

		Long startTime = Long.parseLong(objects[0].toString());
		Long endTime = Long.parseLong(objects[1].toString());
		int days = DateTimeUtil.getDaysBetween(startTime, endTime);

		return days;
	};

	public Object getCurrentHour(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);

		int hour = 0;
		if(objects == null || objects.length == 0) {

			ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(DateTimeUtil.getCurrenTime());
			hour = zdt.getHour();
		}
		else {
			Long startTime = Long.parseLong( objects[0].toString());
			ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(startTime);
			hour = zdt.getHour();
		}

		return hour;
	};

	public Object getCurrentDate(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);

		int date = 0;
		if(objects == null || objects.length == 0) {

			ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(DateTimeUtil.getCurrenTime());
			date = zdt.getDayOfMonth();
		}
		else {
			Long startTime = Long.parseLong( objects[0].toString());
			ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(startTime);
			date = zdt.getDayOfMonth();
		}

		return date;
	};

	public Object getCurrentDay(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);

		int day = 0;
		if(objects == null || objects.length == 0) {

			ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(DateTimeUtil.getCurrenTime());
			day = zdt.getDayOfWeek().getValue();
		}
		else {
			Long startTime = Long.parseLong( objects[0].toString());
			ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(startTime);
			day = zdt.getDayOfWeek().getValue();
		}

		return day;
	};

	public Object getCurrentMonth(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);

		int month = 0;
		if(objects == null || objects.length == 0) {

			ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(DateTimeUtil.getCurrenTime());
			month = zdt.getMonth().getValue();
		}
		else {
			Long startTime = (long) Double.parseDouble(objects[0].toString());
			ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(startTime);
			month = zdt.getMonth().getValue();
		}

		return month;
	};

	public Object addYears(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);

		Long time = Long.parseLong( objects[0].toString());
		int yearCount = Integer.parseInt(objects[1].toString());

		return DateTimeUtil.addYears(time, yearCount);
	};

	public Object addMonths(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);

		Long time = (long) Double.parseDouble(objects[0].toString());
		int monthCount = (int) Double.parseDouble(objects[1].toString());

		return DateTimeUtil.addMonths(time, monthCount);
	};

	public Object addDays(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);

		Long time = (long) Double.parseDouble(objects[0].toString());
		int dateCount = (int) Double.parseDouble(objects[1].toString());

		return DateTimeUtil.addDays(time, dateCount);
	};

	public Object addHours(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);

		Long time = Long.parseLong( objects[0].toString());
		int yearCount = Integer.parseInt(objects[1].toString());

		return DateTimeUtil.addHours(time, yearCount);
	};

	public Object addMinutes(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);

		Long time = Long.parseLong( objects[0].toString());
		int yearCount = Integer.parseInt(objects[1].toString());

		return DateTimeUtil.addMinutes(time, yearCount);
	};

	public Object addSeconds(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);

		Long time = Long.parseLong( objects[0].toString());
		int yearCount = Integer.parseInt(objects[1].toString());

		return DateTimeUtil.addSeconds(time, yearCount);
	};

	public Object addWeeks(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);

		Long time = Long.parseLong( objects[0].toString());
		int yearCount = Integer.parseInt(objects[1].toString());

		return DateTimeUtil.addWeeks(time, yearCount);
	};

	public Object getPreviousMonthStartDate(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);

		return DateTimeUtil.getMonthStartTime(-1,false);


	};

	public Object getPreviousMonthEndDate(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);
		Long previousStart = DateTimeUtil.getMonthStartTime(-1,false);
		return DateTimeUtil.getMonthEndTimeOf(previousStart,false);
	};

	public Object getPreviousMonthName(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);

		String month = "";int year;
		if(objects == null || objects.length == 0) {

			ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(DateTimeUtil.getMonthStartTime(-1,false));
			month = zdt.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
			year = zdt.getYear();
		}
		else {
			Long startTime = Long.parseLong( objects[0].toString());
			ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(startTime);
			month = zdt.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
			year = zdt.getYear();
		}

		return month+" "+year;
	};

	public Object getPreviousLastMonthName(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);

		String month = "";int year;
		if(objects == null || objects.length == 0) {

			ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(DateTimeUtil.getMonthStartTime(-2,false));
			month = zdt.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
			year = zdt.getYear();
		}
		else {
			Long startTime = Long.parseLong( objects[0].toString());
			ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(startTime);
			month = zdt.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
			year = zdt.getYear();
		}

		return month+" "+year;
	};

	public Object now(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);
		if(objects == null || objects.length == 0) {
			return DateTimeUtil.getCurrenTime(false);
		}
		else {
			return Long.parseLong( objects[0].toString());
		}
	}

	public Object getDateRange(Map<String, Object> globalParam, Object... objects) throws Exception {

		int pointer = 0;
		String name = objects[pointer++].toString();
		DateOperators operator = null;
		if(FacilioUtil.isNumeric(name)) {
			operator = (DateOperators) Operator.getOperator(Integer.parseInt(name));
		}
		else {
			Map<String, Operator> operators = DateOperators.getAllOperators();
			operator = (DateOperators) operators.get(name);
		}


		if(operator == null) {
			throw new RuntimeException("No Such Date Operator -- "+name);
		}
		String operatorParam = null;
		String baselineName = null;
		String baselineAdjustmentType = null;


		if(objects.length > pointer && objects[pointer] != null) {
			String value1 = objects[pointer++].toString();
			if(FacilioUtil.isNumeric(value1)) {
				operatorParam = ""+(int) Double.parseDouble(value1);
				if(objects.length > pointer && objects[pointer] != null) {
					String value2 = objects[pointer++].toString();
					if(FacilioUtil.isNumeric(value2)) {
						Long operatorParam2 = Long.parseLong(value2);
						operatorParam = operatorParam + ","+operatorParam2;
					}
					else {

						baselineName = value2;
						if(objects.length > pointer && objects[pointer] != null) {
							String value3 = objects[pointer++].toString();
							baselineAdjustmentType = value3;
						}
					}
				}
			}
			else {
				baselineName = value1;
				if(objects.length > pointer && objects[pointer] != null) {
					String value2 = objects[pointer++].toString();
					baselineAdjustmentType = value2;
				}
			}

		}

		if(objects.length > pointer && objects[pointer] != null) {
			String value1 = objects[pointer++].toString();
			baselineName = value1;
			if(objects.length > pointer && objects[pointer] != null) {
				String value2 = objects[pointer++].toString();
				baselineAdjustmentType = value2;
			}
		}

		DateRange dataRange = operator.getRange(operatorParam);
		if(baselineName != null) {
			BaseLineContext baseline = BaseLineAPI.getBaseLine(baselineName);
			if(baseline != null) {
				if(baselineAdjustmentType != null) {
					baseline.setAdjustType(BaseLineContext.AdjustType.getAllAdjustments().get(baselineAdjustmentType));
				}
				else {
					baseline.setAdjustType(AdjustType.WEEK);
				}
				dataRange = baseline.calculateBaseLineRange(dataRange, baseline.getAdjustTypeEnum());
			}

		}
		return dataRange;
	}

	public Object getDayStartTime(Map<String, Object> globalParam, Object... objects) throws Exception {


		if(objects == null || objects.length == 0) {
			return DateTimeUtil.getDayStartTime();
		}
		else {
			long time = (long) Double.parseDouble(objects[0].toString());
			return DateTimeUtil.getDayStartTimeOf(time);
		}

	}

	public Object getWeekOfTheYear(Map<String, Object> globalParam, Object... objects) throws Exception {

		if (objects != null && objects.length > 0 && objects[0] instanceof Long) {
			return DateTimeUtil.getWeekOfWeekBasedYear(DateTimeUtil.getZonedDateTime((Long) objects[0]));
		}
		return null;
	}

	public Object getMonthRange(Map<String, Object> globalParam, Object... objects) throws Exception {


		if(objects == null || objects.length == 0) {
			long startTime = DateTimeUtil.getMonthStartTime();
			long endTime = DateTimeUtil.getMonthStartTime(1)-1;

			return new DateRange(startTime, endTime);
		}
		else {
			long time = (long) Double.parseDouble(objects[0].toString());
			long startTime = DateTimeUtil.getMonthStartTimeOf(time);
			long endTime = DateTimeUtil.getMonthEndTimeOf(time);
			return new DateRange(startTime, endTime);
		}
	}

	public Object dateRange(Map<String, Object> globalParam, Object... objects) throws Exception {

		String startTimeString = objects[0].toString();
		String endTimeString = objects[1].toString();

		long startTime = (long) Double.parseDouble(startTimeString);
		long endTime = (long) Double.parseDouble(endTimeString);

		DateRange dataRange = new DateRange(startTime, endTime);
		return dataRange;
	}

	public Object getPreviousQuarterStartDate(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);

		return DateTimeUtil.getMonthStartTime(-3,false);


	};

	public Object getPreviousQuarterEndDate(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);
		Long previousStart = DateTimeUtil.getMonthStartTime(-1,false);
		return DateTimeUtil.getMonthEndTimeOf(previousStart,false);
	};

	public Object getPreviousQuarterName(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);
		ZonedDateTime zdt = null;
		if(objects == null || objects.length == 0) {
			zdt = DateTimeUtil.getZonedDateTime(DateTimeUtil.getMonthStartTime(-3,false));
		}
		else {
			Long startTime = Long.parseLong( objects[0].toString());
			zdt = DateTimeUtil.getZonedDateTime(startTime);
		}
		int quarter = (zdt.getMonth().getValue() / 3) + 1;
		return "Q"+quarter+" "+zdt.getYear();


	};

	public Object getPreviousLastQuarterName(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);
		ZonedDateTime zdt = null;
		if(objects == null || objects.length == 0) {
			zdt = DateTimeUtil.getZonedDateTime(DateTimeUtil.getMonthStartTime(-6,false));
		}
		else {
			Long startTime = Long.parseLong( objects[0].toString());
			zdt = DateTimeUtil.getZonedDateTime(startTime);
		}
		int quarter = (zdt.getMonth().getValue() / 3) + 1;
		return "Q"+quarter+" "+zdt.getYear();


	};

	public Object getFormattedTime(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);

		Long ttime = (long) Double.parseDouble(objects[0].toString());

		if(objects == null || objects.length > 1) {
			String formatter = objects[1].toString();
			return DateTimeUtil.getFormattedTime(ttime,formatter);
		}
		else {
			return DateTimeUtil.getFormattedTime(ttime);
		}

	};

	public Object getDateOperator(Map<String, Object> globalParam, Object... objects) throws Exception {

		String name = objects[0].toString();
		DateOperators operator = null;
		if(FacilioUtil.isNumeric(name)) {
			operator = (DateOperators) Operator.getOperator(Integer.parseInt(name));
			return operator.getOperator();
		}
		else {
			Map<String, Operator> operators = DateOperators.getAllOperators();
			operator = (DateOperators) operators.get(name);
			return operator.getOperatorId();
		}
	};

	public Object getRFC3339FormatedDateString(Map<String, Object> globalParam, Object... objects) throws Exception {

		long time = (long) Double.parseDouble(objects[0].toString());


		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date(time));

	};

	public Object getMilliSecondFromRFC3339FormatedDateString(Map<String, Object> globalParam, Object... objects) throws Exception {

		String timeString = objects[0].toString();

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
		Date date = sdf.parse(timeString);
		long millis = date.getTime();

		return millis;

	};

	public Object getMilliSecondFromFormatedDateString(Map<String, Object> globalParam, Object... objects) throws Exception {

		String timeString = objects[0].toString();
		String formatString = objects[1].toString();

		return DateTimeUtil.getTime(timeString, formatString);

	};

	public Object getMonthStartTime(Map<String, Object> globalParam, Object... objects) throws Exception {

		if(objects == null || objects.length == 0) {
			return DateTimeUtil.getMonthStartTimeOf(DateTimeUtil.getCurrenTime());
		}
		else {
			long time = (long) Double.parseDouble(objects[0].toString());
			return DateTimeUtil.getMonthStartTimeOf(time);
		}

	};

	public Object getMonthEndTime(Map<String, Object> globalParam, Object... objects) throws Exception {

		if(objects == null || objects.length == 0) {
			return DateTimeUtil.getMonthEndTimeOf(DateTimeUtil.getCurrenTime());
		}
		else {
			long time = (long) Double.parseDouble(objects[0].toString());
			return DateTimeUtil.getMonthEndTimeOf(time);
		}

	};

	public Object getCurrentYear(Map<String, Object> globalParam, Object... objects) throws Exception {

		//checkParam(objects);

		int month = 0;
		if(objects == null || objects.length == 0) {

			ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(DateTimeUtil.getCurrenTime());
			month = zdt.getYear();
		}
		else {
			Long startTime = (long) Double.parseDouble(objects[0].toString());
			ZonedDateTime zdt = DateTimeUtil.getZonedDateTime(startTime);
			month = zdt.getYear();
		}

		return month;
	};

	public Object getDayEndTime(Map<String, Object> globalParam, Object... objects) throws Exception {

		if(objects == null || objects.length == 0) {
			return DateTimeUtil.getDayEndTimeOf(DateTimeUtil.getCurrenTime());
		}
		else {
			long time = (long) Double.parseDouble(objects[0].toString());
			return DateTimeUtil.getDayEndTimeOf(time);
		}
	}

	public Object getMilliSeconds(Map<String, Object> globalParam, Object... objects) throws Exception {

		String timeString = objects[0].toString();
		String formatString = objects[1].toString();

		return DateTimeUtil.getTimeInstant(formatString, timeString).toEpochMilli();

	};

	public void checkParam(Object... objects) throws Exception {
		if(objects.length <= 0) {
			throw new FunctionParamException("Required Object is null");
		}
	}

}
