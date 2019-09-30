package com.facilio.workflows.functions;

import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.db.criteria.operators.DateOperators;
import com.facilio.db.criteria.operators.Operator;
import com.facilio.modules.BaseLineContext;
import com.facilio.modules.BaseLineContext.AdjustType;
import com.facilio.time.DateRange;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.exceptions.FunctionParamException;
import com.facilio.workflows.util.WorkflowUtil;

public enum FacilioDateFunction implements FacilioWorkflowFunctionInterface {

	MINS_TO_HOUR(1,"minsToHour") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return false;
			}
			Double mins = Double.parseDouble(objects[0].toString());
			Double hours = UnitsUtil.convert(mins, Unit.MIN, Unit.HOUR);
			return hours;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	
	SEC_TO_HOUR(2,"secToHour") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return false;
			}
			Double secs = Double.parseDouble(objects[0].toString());
			Double hours = UnitsUtil.convert(secs, Unit.SEC, Unit.HOUR);
			return hours;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	
	HOUR_TO_DAY(3,"hourToDay") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null) {
				return false;
			}
			Double hours = Double.parseDouble(objects[0].toString());
			Double day = UnitsUtil.convert(hours, Unit.HOUR, Unit.DAY);
			return day;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	
	CURRENT_MONTH_DAYS(4,"getCurrentMonthDays") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
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
		
		public void checkParam(Object... objects) throws Exception {
			
		}
	},
	LAST_MONTH_DAYS(5,"getLastMonthDays") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			int days = DateTimeUtil.getDaysBetween(DateTimeUtil.getMonthStartTime(-1), DateTimeUtil.getMonthStartTime()-1);
			
			return days;
		};
		
		public void checkParam(Object... objects) throws Exception {
			
		}
	},
	DAYS_BETWEEN(6,"getDaysBetween") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			if(objects[0] == null || objects[1] == null) {
				return null;
			}
			
			Long startTime = Long.parseLong(objects[0].toString());
			Long endTime = Long.parseLong(objects[1].toString());
			int days = DateTimeUtil.getDaysBetween(startTime, endTime);
			
			return days;
		};
		
		public void checkParam(Object... objects) throws Exception {
			if(objects.length <= 0) {
				throw new FunctionParamException("Required Object is null");
			}
		}
	},
	CURRENT_HOUR(7,"getCurrentHour") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
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
		public void checkParam(Object... objects) throws Exception {

		}
	},
	CURRENT_DATE(8,"getCurrentDate") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
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
		public void checkParam(Object... objects) throws Exception {

		}
	},
	CURRENT_DAY(9,"getCurrentDay") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
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
		public void checkParam(Object... objects) throws Exception {

		}
	},
	CURRENT_MONTH(10,"getCurrentMonth") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
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
		public void checkParam(Object... objects) throws Exception {

		}
	},
	
	ADD_YEAR(11,"addYears") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			Long time = Long.parseLong( objects[0].toString());
			int yearCount = Integer.parseInt(objects[1].toString());
			
			return DateTimeUtil.addYears(time, yearCount);
		};
		public void checkParam(Object... objects) throws Exception {
			
		}
	},
	ADD_MONTHS(12,"addMonths") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			Long time = (long) Double.parseDouble(objects[0].toString());
			int monthCount = (int) Double.parseDouble(objects[1].toString());
			
			return DateTimeUtil.addMonths(time, monthCount);
		};
		public void checkParam(Object... objects) throws Exception {
			
		}
	},
	ADD_DAYS(13,"addDays") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			Long time = (long) Double.parseDouble(objects[0].toString());
			int dateCount = (int) Double.parseDouble(objects[1].toString());
			
			return DateTimeUtil.addDays(time, dateCount);
		};
		public void checkParam(Object... objects) throws Exception {
			
		}
	},
	ADD_HOURS(14,"addHours") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			Long time = Long.parseLong( objects[0].toString());
			int yearCount = Integer.parseInt(objects[1].toString());
			
			return DateTimeUtil.addHours(time, yearCount);
		};
		public void checkParam(Object... objects) throws Exception {
			
		}
	},
	ADD_MINUTES(15,"addMinutes") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			Long time = Long.parseLong( objects[0].toString());
			int yearCount = Integer.parseInt(objects[1].toString());
			
			return DateTimeUtil.addMinutes(time, yearCount);
		};
		public void checkParam(Object... objects) throws Exception {
			
		}
	},
	ADD_SECONDS(16,"addSeconds") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			Long time = Long.parseLong( objects[0].toString());
			int yearCount = Integer.parseInt(objects[1].toString());
			
			return DateTimeUtil.addSeconds(time, yearCount);
		};
		public void checkParam(Object... objects) throws Exception {
			
		}
	},
	ADD_WEEKS(17,"addWeeks") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			Long time = Long.parseLong( objects[0].toString());
			int yearCount = Integer.parseInt(objects[1].toString());
			
			return DateTimeUtil.addWeeks(time, yearCount);
		};
		public void checkParam(Object... objects) throws Exception {
			
		}
	},
	GET_PREVIOUS_MONTH_START_DATE(18,"getPreviousMonthStartDate") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			return DateTimeUtil.getMonthStartTime(-1,false);
			
			
		};
		public void checkParam(Object... objects) throws Exception {
			
		}
	},
	GET_PREVIOUS_MONTH_END_DATE(19,"getPreviousMonthEndDate") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			 Long previousStart = DateTimeUtil.getMonthStartTime(-1,false);
			 return DateTimeUtil.getMonthEndTimeOf(previousStart,false);
		};
		public void checkParam(Object... objects) throws Exception {
			
		}
	},
	PREVIOUS_MONTH_NAME(20,"getPreviousMonthName") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
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
		public void checkParam(Object... objects) throws Exception {

		}
	},
	PREVIOUS_LAST_MONTH_NAME(21,"getPreviousLastMonthName") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
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
		public void checkParam(Object... objects) throws Exception {

		}
	},
	NOW(22, "now") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			if(objects == null || objects.length == 0) {
				return DateTimeUtil.getCurrenTime(false);
			}
			else {
				return Long.parseLong( objects[0].toString());
			}
		}
		public void checkParam(Object... objects) throws Exception {

		}
	},
	GET_DATE_RANGE(23, "getDateRange") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			String name = objects[0].toString();
			Map<String, Operator> operators = DateOperators.getAllOperators();
			DateOperators operator = (DateOperators) operators.get(name);
			
			if(operator == null) {
				throw new RuntimeException("No Such Date Operator -- "+name);
			}
			String operatorParam = null; 
			String baselineName = null;
			String baselineAdjustmentType = null;
			if(objects.length > 1 && objects[1] != null) {
				String secondParam = objects[1].toString();
				if(FacilioUtil.isNumeric(secondParam)) {
					operatorParam = ""+(int) Double.parseDouble(secondParam);
					if(objects.length > 2 && objects[2] != null) {
						Double operatorParam2 = (Double)objects[2];
						operatorParam = operatorParam + ","+WorkflowUtil.getStringValueFromDouble(operatorParam2);
					}
				}
				else {
					baselineName = secondParam;
					if(objects.length > 2 && objects[2] != null) {
						baselineAdjustmentType = objects[2].toString();
					}
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
		public void checkParam(Object... objects) throws Exception {

		}
	},
	GET_TODAY_START_TIME(24, "getDayStartTime") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			
			if(objects == null || objects.length == 0) {
				return DateTimeUtil.getDayStartTime();
			}
			else {
				long time = (long) Double.parseDouble(objects[0].toString());
				return DateTimeUtil.getDayStartTimeOf(time);
			}
		}
		public void checkParam(Object... objects) throws Exception {

		}
	},
	GET_MONTH_RANGE(25, "getMonthRange") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			
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
		public void checkParam(Object... objects) throws Exception {

		}
	},
	GET_DATE_RANGE_WITH_START_AND_END(26, "dateRange") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			String startTimeString = objects[0].toString();
			String endTimeString = objects[1].toString();
			
			long startTime = (long) Double.parseDouble(startTimeString);
			long endTime = (long) Double.parseDouble(endTimeString);
			
			DateRange dataRange = new DateRange(startTime, endTime);
			return dataRange;
		}
		public void checkParam(Object... objects) throws Exception {

		}
	},
	GET_PREVIOUS_QUARTER_START_DATE(27,"getPreviousQuarterStartDate") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			
			return DateTimeUtil.getMonthStartTime(-3,false);
			
			
		};
		public void checkParam(Object... objects) throws Exception {
			
		}
	},
	GET_PREVIOUS_QUARTER_END_DATE(28,"getPreviousQuarterEndDate") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			 Long previousStart = DateTimeUtil.getMonthStartTime(-1,false);
			 return DateTimeUtil.getMonthEndTimeOf(previousStart,false);
		};
		public void checkParam(Object... objects) throws Exception {
			
		}
	},
	PREVIOUS_QUARTER_NAME(29,"getPreviousQuarterName") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
			ZonedDateTime zdt = null;
			if(objects == null || objects.length == 0) {
			     zdt = DateTimeUtil.getZonedDateTime(DateTimeUtil.getMonthStartTime(-1,false));
			}
			else {
				Long startTime = Long.parseLong( objects[0].toString());
				zdt = DateTimeUtil.getZonedDateTime(startTime);
			}
			int quarter = (zdt.getMonth().getValue() / 3) + 1;
			return "Q"+quarter+" "+zdt.getYear();
		
			
		};
		public void checkParam(Object... objects) throws Exception {

		}
	},
	PREVIOUS_LAST_QUARTER_NAME(30,"getPreviousLastQuarterName") {
		@Override
		public Object execute(Object... objects) throws Exception {
			
			checkParam(objects);
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
		public void checkParam(Object... objects) throws Exception {

		}
	},
	;
	private Integer value;
	private String functionName;
	private String namespace = "date";
	private String params;
	private FacilioSystemFunctionNameSpace nameSpaceEnum = FacilioSystemFunctionNameSpace.DATE;
	
	public Integer getValue() {
		return value;
	}
	public void setValue(Integer value) {
		this.value = value;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getNamespace() {
		return namespace;
	}
	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
	FacilioDateFunction(Integer value,String functionName) {
		this.value = value;
		this.functionName = functionName;
	}
	public static Map<String, FacilioDateFunction> getAllFunctions() {
		return DATE_FUNCTIONS;
	}
	public static FacilioDateFunction getFacilioDateFunction(String functionName) {
		return DATE_FUNCTIONS.get(functionName);
	}
	static final Map<String, FacilioDateFunction> DATE_FUNCTIONS = Collections.unmodifiableMap(initTypeMap());
	static Map<String, FacilioDateFunction> initTypeMap() {
		Map<String, FacilioDateFunction> typeMap = new HashMap<>();
		for(FacilioDateFunction type : FacilioDateFunction.values()) {
			typeMap.put(type.getFunctionName(), type);
		}
		return typeMap;
	}
}
