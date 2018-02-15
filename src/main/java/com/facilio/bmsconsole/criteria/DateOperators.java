package com.facilio.bmsconsole.criteria;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

import com.facilio.bmsconsole.util.DateTimeUtil;

public enum DateOperators implements Operator<String> {
	
	IS(16, "is") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				return columnName+" = "+value;
			}
			return null;
		}

		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				ZonedDateTime val = DateTimeUtil.getDateTime(Long.parseLong(value)).truncatedTo(ChronoUnit.MINUTES);
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							ZonedDateTime currentVal = DateTimeUtil.getDateTime((long) object).truncatedTo(ChronoUnit.MINUTES);
							return currentVal.equals(val); 
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	ISN_T(17, "isn't") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append("(")
						.append(columnName)
						.append(" IS NULL OR ")
						.append(columnName)
						.append(" != ")
						.append(value.trim())
						.append(")");
				return builder.toString();
			}
			return null;
		}

		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				ZonedDateTime val = DateTimeUtil.getDateTime(Long.parseLong(value)).truncatedTo(ChronoUnit.MINUTES);
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							ZonedDateTime currentVal = DateTimeUtil.getDateTime((long) object).truncatedTo(ChronoUnit.MINUTES);
							return !currentVal.equals(val); 
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	IS_BEFORE(18, "is before") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			return greaterOrLessThan(columnName, "<", value);
		}

		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				ZonedDateTime val = DateTimeUtil.getDateTime(Long.parseLong(value)).truncatedTo(ChronoUnit.MINUTES);
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							ZonedDateTime currentVal = DateTimeUtil.getDateTime((long) object).truncatedTo(ChronoUnit.MINUTES);
							return currentVal.isBefore(val); 
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	IS_AFTER(19, "is after") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			return greaterOrLessThan(columnName, ">", value);
		}

		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				ZonedDateTime val = DateTimeUtil.getDateTime(Long.parseLong(value)).truncatedTo(ChronoUnit.MINUTES);
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							ZonedDateTime currentVal = DateTimeUtil.getDateTime((long) object).truncatedTo(ChronoUnit.MINUTES);
							return currentVal.isAfter(val);
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	BETWEEN(20, "between") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			return betweenWhereClause(columnName, value, false);
		}

		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				Predicate betweenPredicate = getBetweenPredicate(value);
				if(betweenPredicate != null) {
					return new FacilioModulePredicate(fieldName, betweenPredicate);
				}
			}
			return null;
		}
	},
	
	NOT_BETWEEN(21, "not between") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			return betweenWhereClause(columnName, value, true);
		}

		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				Predicate betweenPredicate = getBetweenPredicate(value);
				if(betweenPredicate != null) {
					return new FacilioModulePredicate(fieldName, PredicateUtils.notPredicate(betweenPredicate));
				}
			}
			return null;
		}
	},
	
	TODAY(22, "Today") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getDayStartTime())
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getDayStartTime(1));
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${TODAY}";
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getDayStartTime() <= currentVal && currentVal < DateTimeUtil.getDayStartTime(1);
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	TODAY_UPTO_NOW(43, "today upto now") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getDayStartTime())
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getCurrenTime());
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${TODAY_NOW}";
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getDayStartTime() <= currentVal && currentVal < DateTimeUtil.getCurrenTime();
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	TOMORROW(23, "Tomorrow") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getDayStartTime(1))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getDayStartTime(2));
				return builder.toString();
			}
			return null;	
		}
		
		@Override
		public String getDynamicParameter() {
			return "${TOMORROW}";
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getDayStartTime(1) <= currentVal && currentVal < DateTimeUtil.getDayStartTime(2);
						}
						return false;
					}
				});
			}
			return null;
		}
	 	
	},
	
	STARTING_TOMORROW(24, "Starting Tomorrow") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(columnName)
						.append(">=")
						.append(DateTimeUtil.getDayStartTime(1));
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${TOMORROWPLUS}";
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return currentVal >= DateTimeUtil.getDayStartTime(1);
						}
						return false;
					}
				});
			}
			return null;
		}
	 	
	},
	
	YESTERDAY(25, "Yesterday") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getDayStartTime(-1))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getDayStartTime());
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${YESTERDAY}";
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getDayStartTime(-1) <= currentVal && currentVal < DateTimeUtil.getDayStartTime();
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	TILL_YESTERDAY(26, "Till Yesterday") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(columnName)
						.append("<")
						.append(DateTimeUtil.getDayStartTime());
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${YESTERDAYMINUS}";
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return currentVal < DateTimeUtil.getDayStartTime();
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	LAST_MONTH(27, "Last Month") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getMonthStartTime(-1))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getMonthStartTime());
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${LASTMONTH}";
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getMonthStartTime(-1) <= currentVal && currentVal < DateTimeUtil.getMonthStartTime();
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	CURRENT_MONTH(28, "Current Month") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getMonthStartTime())
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getMonthStartTime(1));
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${THISMONTH}";
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getMonthStartTime() <= currentVal && currentVal < DateTimeUtil.getMonthStartTime(1);
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	NEXT_MONTH(29, "Next Month") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getMonthStartTime(1))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getMonthStartTime(2));
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${NEXTMONTH}";
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getMonthStartTime(1) <= currentVal && currentVal < DateTimeUtil.getMonthStartTime(2);
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	CURRENT_YEAR(44, "Current Year") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getYearStartTime())
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getYearStartTime(1));
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${THISYEAR}";
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getYearStartTime() <= currentVal && currentVal < DateTimeUtil.getYearStartTime(1);
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	LAST_YEAR(45, "Last Year") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getYearStartTime(-1))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getYearStartTime());
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${LASTYEAR}";
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							long currentVal = (long) object;
							return DateTimeUtil.getMonthStartTime(-1) <= currentVal && currentVal < DateTimeUtil.getMonthStartTime();
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	LAST_WEEK(30, "Last Week") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getWeekStartTime(-1))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getWeekStartTime());
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${LASTWEEK}";
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getWeekStartTime(-1) <= currentVal && currentVal < DateTimeUtil.getWeekStartTime();
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	CURRENT_WEEK(31, "Current Week") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getWeekStartTime())
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getWeekStartTime(1));
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${THISWEEK}";
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getWeekStartTime() <= currentVal && currentVal < DateTimeUtil.getWeekStartTime(1);
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	NEXT_WEEK(32, "Next Week") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getWeekStartTime(1))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getWeekStartTime(2));
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${NEXTWEEK}";
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getWeekStartTime(1) <= currentVal && currentVal < DateTimeUtil.getWeekStartTime(2);
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	AGE_IN_DAYS(33, "Age in Days") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append("CEIL((UNIX_TIMESTAMP(CURDATE())-(")
						.append(columnName)
						.append("/1000))/(24*3600)) = ")
						.append(value);
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				int age = Integer.parseInt(value);
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getAge(currentVal) == age;
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	DUE_IN_DAYS(34, "Due in Days") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append("FLOOR(((")
						.append(columnName)
						.append("/1000)-UNIX_TIMESTAMP(CURDATE()))/(24*3600)) = ")
						.append(value);
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				int due = Integer.parseInt(value);
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getDue(currentVal) == due;
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	LAST_MONTHS(39, "Last Months") {
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getMonthStartTime(-Integer.parseInt(value)))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getMonthStartTime());
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						if(object != null && object instanceof Long) {
							long currentVal = (long) object;
							return DateTimeUtil.getMonthStartTime(-Integer.parseInt(value)) <= currentVal && currentVal < DateTimeUtil.getMonthStartTime();
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	WITHIN_HOURS(40, "Within Hours") {
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getHourStartTime(-Integer.parseInt(value)))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getHourStartTime(1));
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						if(object != null && object instanceof Long) {
							long currentVal = (long) object;
							return DateTimeUtil.getHourStartTime(-Integer.parseInt(value)) <= currentVal && currentVal < DateTimeUtil.getHourStartTime(1);
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	NEXT_HOURS(41, "Next Hours") {
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getHourStartTime())
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getHourStartTime(Integer.parseInt(value)+1));
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						if(object != null && object instanceof Long) {
							long currentVal = (long) object;
							return DateTimeUtil.getHourStartTime() <= currentVal && currentVal < DateTimeUtil.getHourStartTime(Integer.parseInt(value)+1);
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	LAST_N_HOURS(42, "Last N Hours") {
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty()) {
				Long currentTime = DateTimeUtil.getCurrenTime();
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getLastNHour(currentTime, Integer.valueOf(value)))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(currentTime);
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						if(object != null && object instanceof Long) {
							Long currentTime = DateTimeUtil.getCurrenTime();
							long currentVal = (long) object;
							return DateTimeUtil.getLastNHour(currentTime, Integer.valueOf(value)) <= currentVal && currentVal < currentTime;
						}
						return false;
					}
				});
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
			
			if(values != null && values.length > 1) {
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
	public abstract String getWhereClause(String columnName, String value);

	@Override
	public abstract FacilioModulePredicate getPredicate(String fieldName, String value);
	
	@Override
	public String getDynamicParameter() {
		return null;
	}
 	
	@Override
	public List<Object> computeValues(String value) {
		return null;
	}
	
	private DateOperators(int operatorId, String operator) {
		this.operatorId = operatorId;
		this.operator = operator;
	}
	
	private int operatorId;
	@Override
	public int getOperatorId() {
		return operatorId;
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
	
	private static Predicate getBetweenPredicate(String value) {
		String values[] = value.trim().split("\\s*,\\s*");
		
		if(values != null && values.length > 1) {
			ZonedDateTime startVal = DateTimeUtil.getDateTime(Long.parseLong(values[0])).truncatedTo(ChronoUnit.MINUTES);
			ZonedDateTime endVal = DateTimeUtil.getDateTime(Long.parseLong(values[1])).truncatedTo(ChronoUnit.MINUTES);
			return new Predicate() {
				
				@Override
				public boolean evaluate(Object object) {
					// TODO Auto-generated method stub
					if(object != null && object instanceof Long) {
						ZonedDateTime currentVal = DateTimeUtil.getDateTime((long) object).truncatedTo(ChronoUnit.MINUTES);
						return currentVal.equals(startVal) || (currentVal.isAfter(startVal) && currentVal.isBefore(endVal)) || currentVal.equals(endVal);
					}
					return false;
				}
			};
		}
		return null;
	}
}
