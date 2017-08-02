package com.facilio.bmsconsole.criteria;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.modules.FacilioField;

public enum DateOperators implements Operator<String> {
	
	IS("is") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				return field.getColumnName()+" = "+value;
			}
			return null;
		}
	},
	
	ISN_T("isn't") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append("(")
						.append(field.getColumnName())
						.append(" IS NULL OR ")
						.append(field.getColumnName())
						.append(" != ")
						.append(value.trim())
						.append(")");
				return builder.toString();
			}
			return null;
		}
	},
	
	IS_BEFORE("is before") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			return greaterOrLessThan(field.getColumnName(), "<", value);
		}
	},
	
	IS_AFTER("is after") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			return greaterOrLessThan(field.getColumnName(), ">", value);
		}
	},
	
	BETWEEN("between") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			return betweenWhereClause(field.getColumnName(), value, false);
		}
	},
	
	NOT_BETWEEN("not between") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			return betweenWhereClause(field.getColumnName(), value, true);
		}
	},
	
	TODAY("Today") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				return "(CEIL("+field.getColumnName()+"/1000) >= UNIX_TIMESTAMP(CURDATE()) AND CEIL("+field.getColumnName()+"/1000) < UNIX_TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY)))";
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${TODAY}";
		}
	},
	
	TOMORROW("Tomorrow") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				return "(CEIL("+field.getColumnName()+"/1000) >= UNIX_TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY)) AND CEIL("+field.getColumnName()+"/1000) < UNIX_TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 2 DAY)))";
			}
			return null;	
		}
		
		@Override
		public String getDynamicParameter() {
			return "${TOMORROW}";
		}
	 	
	},
	
	STARTING_TOMORROW("Starting Tomorrow") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				return "CEIL("+field.getColumnName()+"/1000) >= UNIX_TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY))";
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${TOMORROWPLUS}";
		}
	 	
	},
	
	YESTERDAY("Yesterday") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				return "(CEIL("+field.getColumnName()+"/1000) >= UNIX_TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 1 DAY)) AND CEIL("+field.getColumnName()+"/1000) < UNIX_TIMESTAMP(CURDATE()))";
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${YESTERDAY}";
		}
	},
	
	TILL_YESTERDAY("Till Yesterday") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				return "CEIL("+field.getColumnName()+"/1000) < UNIX_TIMESTAMP(CURDATE())";
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${YESTERDAYMINUS}";
		}
	},
	
	LAST_MONTH("Last Month") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				return "(CEIL("+field.getColumnName()+"/1000) >= UNIX_TIMESTAMP(DATE_ADD(LAST_DAY(DATE_SUB(CURDATE(), INTERVAL 2 MONTH)), INTERVAL 1 DAY)) AND CEIL("+field.getColumnName()+"/1000) < UNIX_TIMESTAMP(DATE_ADD(LAST_DAY(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)), INTERVAL 1 DAY)))";
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${LASTMONTH}";
		}
	},
	
	CURRENT_MONTH("Current Month") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				return "(CEIL("+field.getColumnName()+"/1000) >= UNIX_TIMESTAMP(DATE_ADD(LAST_DAY(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)), INTERVAL 1 DAY)) AND CEIL("+field.getColumnName()+"/1000) < UNIX_TIMESTAMP(DATE_ADD(LAST_DAY(CURDATE()), INTERVAL 1 DAY)))";
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${THISMONTH}";
		}
	},
	
	NEXT_MONTH("Next Month") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				return "(CEIL("+field.getColumnName()+"/1000) >= UNIX_TIMESTAMP(DATE_ADD(LAST_DAY(CURDATE()), INTERVAL 1 DAY)) AND CEIL("+field.getColumnName()+"/1000) < UNIX_TIMESTAMP(DATE_ADD(LAST_DAY(DATE_ADD(CURDATE(), INTERVAL 1 MONTH)), INTERVAL 1 DAY)))";
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${NEXTMONTH}";
		}
	},
	
	LAST_WEEK("Last Week") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				return "(CEIL("+field.getColumnName()+"/1000) >= UNIX_TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL (DAYOFWEEK(CURDATE()) - 1)+7 DAY)) AND CEIL("+field.getColumnName()+"/1000) < UNIX_TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL (DAYOFWEEK(CURDATE()) - 1) DAY)))";
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${LASTWEEK}";
		}
	},
	
	CURRENT_WEEK("Current Week") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				return "(CEIL("+field.getColumnName()+"/1000) >= UNIX_TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL (DAYOFWEEK(CURDATE()) - 1) DAY)) AND CEIL("+field.getColumnName()+"/1000) < UNIX_TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL (7 - DAYOFWEEK(CURDATE()))+1 DAY)))"; //Condition is >= first day of this week and < first day of next week
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${THISWEEK}";
		}
	},
	
	NEXT_WEEK("Next Week") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				return "(CEIL("+field.getColumnName()+"/1000) >= UNIX_TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL (7 - DAYOFWEEK(CURDATE()))+1 DAY)) AND CEIL("+field.getColumnName()+"/1000) < UNIX_TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL (7 - DAYOFWEEK(CURDATE()))+8 DAY)))";
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${NEXTWEEK}";
		}
	},
	
	AGE_IN_DAYS("Age in Days") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				return "CEIL((UNIX_TIMESTAMP(CURDATE())-("+field.getColumnName()+"/1000))/(24*3600)) "+value;
			}
			return null;
		}
	},
	
	DUE_IN_DAYS("Due in Days") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				return "FLOOR((("+field.getColumnName()+"/1000)-UNIX_TIMESTAMP(CURDATE()))/(24*3600)) "+value;
			}
			return null;
		}
	}
	;
	
	private static String greaterOrLessThan(String columnName, String operator, String value) {
		if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			builder.append("CEIL(")
					.append(columnName)
					.append("/1000)")
					.append(operator)
					.append("CEIL(")
					.append(value)
					.append("/1000)");
			
			return builder.toString();
		}
		return null;
	}
	
	private static String betweenWhereClause(String columnName, String value, boolean isNot) {
		if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
			
			String values[] = value.trim().split("\\s*,\\s*");
			
			if(values != null && values.length > 0) {
			StringBuilder builder = new StringBuilder();
				builder.append("CEIL(")
						.append(columnName)
						.append("/1000)");
				if(isNot) {
					builder.append(" NOT ");
				}
				builder.append(" BETWEEN ")
						.append("CEIL(")
						.append(values[0])
						.append("/1000)")
						.append(" AND ")
						.append("CEIL(")
						.append(values[1])
						.append("/1000)");
				return builder.toString();
			}
		}
		return null;
	}
	
	@Override
	public abstract String getWhereClause(FacilioField field, String value);

	@Override
	public String getDynamicParameter() {
		return null;
	}
 	
	@Override
	public List<Object> computeValues(String value) {
		return null;
	}
	
	private DateOperators(String operator) {
		 this.operator = operator;
	}
	
	private String operator;
	@Override
	public String getOperator() {
		return operator;
	}
	
	private static final Map<String, Operator> operatorMap = Collections.unmodifiableMap(initOperatorMap());
	private static Map<String, Operator> initOperatorMap() {
		Map<String, Operator> operatorMap = new HashMap<>();
		for(Operator operator : values()) {
			operatorMap.put(operator.getOperator(), operator);
		}
		operatorMap.putAll(CommonOperators.getAllOperators());
		return operatorMap;
	}
	public static Map<String, Operator> getAllOperators() {
		return operatorMap;
	}
}
