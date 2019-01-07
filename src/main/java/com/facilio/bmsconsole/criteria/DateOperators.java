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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			long val = Long.parseLong(value);
			return new DateRange(val, val);
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(-1, Long.parseLong(value));
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(Long.parseLong(value), -1);
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			String values[] = value.trim().split("\\s*,\\s*");
			return new DateRange(Long.parseLong(values[0]), Long.parseLong(values[1]));
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
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
		public boolean isDynamicOperator() {
			return true;
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getDayStartTime(), DateTimeUtil.getDayStartTime(1)-1);
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return true;
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
						.append("<=")
						.append(DateTimeUtil.getCurrenTime());
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public boolean isDynamicOperator() {
			return true;
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
							return DateTimeUtil.getDayStartTime() <= currentVal && currentVal <= DateTimeUtil.getCurrenTime();
						}
						return false;
					}
				});
			}
			return null;
		}

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getDayStartTime(), DateTimeUtil.getCurrenTime());
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return true;
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
		public boolean isDynamicOperator() {
			return true;
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getDayStartTime(1), DateTimeUtil.getDayStartTime(2) - 1);
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
	 	
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
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
		public boolean isDynamicOperator() {
			return true;
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getDayStartTime(1), -1);
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return false;
		}
	 	
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
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
		public boolean isDynamicOperator() {
			return true;
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getDayStartTime(-1), DateTimeUtil.getDayStartTime() - 1);
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
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
		public boolean isDynamicOperator() {
			return true;
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(-1, DateTimeUtil.getDayStartTime() - 1);
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
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
		public boolean isDynamicOperator() {
			return true;
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getMonthStartTime(-1), DateTimeUtil.getMonthStartTime());
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
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
		public boolean isDynamicOperator() {
			return true;
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getMonthStartTime(), DateTimeUtil.getMonthStartTime(1) - 1);
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return true;
		}
	},
	CURRENT_MONTH_UPTO_NOW(48, "Current Month upto now") {
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
						.append("<=")
						.append(DateTimeUtil.getCurrenTime());
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public boolean isDynamicOperator() {
			return true;
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
							return DateTimeUtil.getMonthStartTime() <= currentVal && currentVal <= DateTimeUtil.getCurrenTime();
						}
						return false;
					}
				});
			}
			return null;
		}

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getMonthStartTime(), DateTimeUtil.getCurrenTime());
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return true;
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
		public boolean isDynamicOperator() {
			return true;
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getMonthStartTime(1), DateTimeUtil.getMonthStartTime(2) - 1);
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
		}
	},
	
	NEXT_N_MONTHS(59, "Next N Months") {
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getMonthStartTime())
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getMonthStartTime(Integer.parseInt(value) + 1));
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public boolean isDynamicOperator() {
			return true;
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						if(object != null && object instanceof Long) {
							long currentVal = (long) object;
							return DateTimeUtil.getMonthStartTime() <= currentVal && currentVal < DateTimeUtil.getMonthStartTime(Integer.parseInt(value) + 1);
						}
						return false;
					}
				});
			}
			return null;
		}

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getMonthStartTime(), DateTimeUtil.getMonthStartTime(Integer.parseInt(value) + 1));
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
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
		public boolean isDynamicOperator() {
			return true;
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getYearStartTime(), DateTimeUtil.getYearStartTime(1) - 1);
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return true;
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
		public boolean isDynamicOperator() {
			return true;
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
							return DateTimeUtil.getYearStartTime(-1) <= currentVal && currentVal < DateTimeUtil.getYearStartTime() - 1;
						}
						return false;
					}
				});
			}
			return null;
		}

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getYearStartTime(-1), DateTimeUtil.getYearStartTime() -1);
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
		}
	},
	CURRENT_YEAR_UPTO_NOW(46, "Current Year upto now") {
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
						.append("<=")
						.append(DateTimeUtil.getCurrenTime());
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public boolean isDynamicOperator() {
			return true;
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
							return DateTimeUtil.getYearStartTime() <= currentVal && currentVal <= DateTimeUtil.getCurrenTime();
						}
						return false;
					}
				});
			}
			return null;
		}

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getYearStartTime(), DateTimeUtil.getCurrenTime());
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return true;
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
		public boolean isDynamicOperator() {
			return true;
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getWeekStartTime(-1), DateTimeUtil.getWeekStartTime() - 1);
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
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
		public boolean isDynamicOperator() {
			return true;
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getWeekStartTime(), DateTimeUtil.getWeekStartTime(1) - 1);
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return true;
		}
	},
	
	CURRENT_WEEK_UPTO_NOW(47, "Current Week upto now") {
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
						.append("<=")
						.append(DateTimeUtil.getCurrenTime());
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public boolean isDynamicOperator() {
			return true;
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
							return DateTimeUtil.getWeekStartTime() <= currentVal && currentVal <= DateTimeUtil.getCurrenTime();
						}
						return false;
					}
				});
			}
			return null;
		}

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getWeekStartTime(), DateTimeUtil.getCurrenTime());
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return true;
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
		public boolean isDynamicOperator() {
			return true;
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getWeekStartTime(1), DateTimeUtil.getWeekStartTime(2) - 1);
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
		}
	},
	
	NEXT_N_WEEKS(60, "Next N Weeks") {
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getWeekStartTime())
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getWeekStartTime(Integer.parseInt(value) + 1));
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public boolean isDynamicOperator() {
			return true;
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						if(object != null && object instanceof Long) {
							long currentVal = (long) object;
							return DateTimeUtil.getWeekStartTime() <= currentVal && currentVal < DateTimeUtil.getWeekStartTime(Integer.parseInt(value) + 1);
						}
						return false;
					}
				});
			}
			return null;
		}

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getWeekStartTime(), DateTimeUtil.getWeekStartTime(Integer.parseInt(value) + 1));
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
		}
		
	},
	
	AGE_IN_DAYS(33, "Age in Days") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append("FLOOR((UNIX_TIMESTAMP(CURDATE())-(")
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
		}
	},
	
	NEXT_N_DAYS(61, "Next N Days") {
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getDayStartTime())
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getDayStartTime(Integer.parseInt(value)));
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public boolean isDynamicOperator() {
			return true;
		}
		
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			if(fieldName != null && !fieldName.isEmpty()) {
				return new FacilioModulePredicate(fieldName, new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						if(object != null && object instanceof Long) {
							long currentVal = (long) object;
							return DateTimeUtil.getDayStartTime() <= currentVal && currentVal < DateTimeUtil.getDayStartTime(Integer.parseInt(value));
						}
						return false;
					}
				});
			}
			return null;
		}

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getDayStartTime(), DateTimeUtil.getDayStartTime(Integer.parseInt(value))-1);
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
		}
	},
	
	LAST_MONTHS(39, "Last Months") {
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getMonthStartTime(-Integer.parseInt(value)), DateTimeUtil.getMonthStartTime() - 1);
		}
		
		@Override
		public boolean isDynamicOperator() {
			return true;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
		}
	},
	
	WITHIN_HOURS(40, "Within N Hours") {	// eg: If current time is 5:30 => 2:00 - 5:59
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
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

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getHourStartTime(-Integer.parseInt(value)), DateTimeUtil.getHourStartTime(1));
		}
		
		@Override
		public boolean isDynamicOperator() {
			return true;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
		}
	},
	
	NEXT_HOURS(41, "Next N Hours") {
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getHourStartTime())
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<=")
						.append(DateTimeUtil.getHourStartTime(Integer.parseInt(value)));
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
							return DateTimeUtil.getHourStartTime() <= currentVal && currentVal <= DateTimeUtil.getHourStartTime(Integer.parseInt(value));
						}
						return false;
					}
				});
			}
			return null;
		}

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange( DateTimeUtil.getHourStartTime(), DateTimeUtil.getHourStartTime(Integer.parseInt(value)));
		}
		
		@Override
		public boolean isDynamicOperator() {
			return true;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
		}
	},
	LAST_N_MINUTES(56, "Last N Minutes") {
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				
				Long endTime;
				if(value != null && value.contains(",")) {
					
					String[] values = value.split(",");
					value = values[0];
					endTime = Long.parseLong(values[1]);
				}
				else {
					endTime = DateTimeUtil.getCurrenTime();
				}
				
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getLastNMinute(endTime, Integer.valueOf(value)))
						.append("<")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<=")
						.append(endTime);
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
							
							Long endTime;
							String value1;
							if(value != null && value.contains(",")) {
								
								String[] values = value.split(",");
								value1 = values[0];
								endTime = Long.parseLong(values[1]);
							}
							else {
								endTime = DateTimeUtil.getCurrenTime();
								value1 = value;
							}
							
							long currentVal = (long) object;
							return DateTimeUtil.getLastNMinute(endTime, Integer.valueOf(value1)) < currentVal && currentVal <= endTime;
						}
						return false;
					}
				});
			}
			return null;
		}

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			
			Long endTime;
			String value1;
			if(value != null && value.contains(",")) {
				
				String[] values = value.split(",");
				value1 = values[0];
				endTime = Long.parseLong(values[1]);
			}
			else {
				endTime = DateTimeUtil.getCurrenTime();
				value1 = value;
			}
			
			return new DateRange(DateTimeUtil.getLastNHour(endTime, Integer.valueOf(value1)), endTime);
		}
		
		@Override
		public boolean isDynamicOperator() {
			return true;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return true;
		}
	},
	LAST_N_HOURS(42, "Last N Hours") {
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				
				Long endTime;
				if(value != null && value.contains(",")) {
					
					String[] values = value.split(",");
					value = values[0];
					endTime = Long.parseLong(values[1]);
				}
				else {
					endTime = DateTimeUtil.getCurrenTime();
				}
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getLastNHour(endTime, Integer.valueOf(value)))
						.append("<")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<=")
						.append(endTime);
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
							
							Long endTime;
							String value1;
							if(value != null && value.contains(",")) {
								
								String[] values = value.split(",");
								value1 = values[0];
								endTime = Long.parseLong(values[1]);
							}
							else {
								endTime = DateTimeUtil.getCurrenTime();
								value1 = value;
							}
							
							return DateTimeUtil.getLastNHour(endTime, Integer.valueOf(value1)) < currentVal && currentVal <= endTime;
						}
						return false;
					}
				});
			}
			return null;
		}

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			Long endTime;
			String value1;
			if(value != null && value.contains(",")) {
				
				String[] values = value.split(",");
				value1 = values[0];
				endTime = Long.parseLong(values[1]);
			}
			else {
				endTime = DateTimeUtil.getCurrenTime();
				value1 = value;
			}
			return new DateRange(DateTimeUtil.getLastNHour(endTime, Integer.valueOf(value1)), endTime-1);
		}
		
		@Override
		public boolean isDynamicOperator() {
			return true;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return true;
		}
	},
	
	LAST_N_DAYS(49, "Last N Days") {
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				
				builder.append(DateTimeUtil.getDayStartTime(-(Integer.valueOf(value)-1)))
						.append("<")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<=")
						.append(DateTimeUtil.getDayStartTime(1));
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
							return DateTimeUtil.getDayStartTime(-(Integer.valueOf(value)-1)) < currentVal && currentVal <= DateTimeUtil.getDayStartTime(1);
						}
						return false;
					}
				});
			}
			return null;
		}

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getDayStartTime(-(Integer.valueOf(value)-1)), DateTimeUtil.getDayStartTime(1));
		}
		
		@Override
		public boolean isDynamicOperator() {
			return true;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return true;
		}
	},
	PAST_N_DAY(57, "Past Nth day") {
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getDayStartTime(-Integer.valueOf(value)))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getDayStartTime(-Integer.valueOf(value)+1));
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
							return DateTimeUtil.getDayStartTime(-Integer.valueOf(value)) <= currentVal && currentVal < DateTimeUtil.getDayStartTime(-Integer.valueOf(value)+1);
						}
						return false;
					}
				});
			}
			return null;
		}

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getDayStartTime(-Integer.valueOf(value)), DateTimeUtil.getDayStartTime(-Integer.valueOf(value)+1));
		}
		
		@Override
		public boolean isDynamicOperator() {
			return true;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
		}
	},
	IN_N_DAY(58, "In N Day") {
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getDayStartTime(Integer.valueOf(value)))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<")
						.append(DateTimeUtil.getDayStartTime(Integer.valueOf(value)+1));
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
							return DateTimeUtil.getDayStartTime(Integer.valueOf(value)) <= currentVal && currentVal < DateTimeUtil.getDayStartTime(Integer.valueOf(value)+1);
						}
						return false;
					}
				});
			}
			return null;
		}

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getDayStartTime(Integer.valueOf(value)), DateTimeUtil.getDayStartTime(Integer.valueOf(value)+1)-1);
		}
		
		@Override
		public boolean isDynamicOperator() {
			return true;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
		}
	},
	LAST_N_WEEKS(50, "Last N Weeks") {
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getWeekStartTime(-(Integer.valueOf(value)-1)))
						.append("<")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<=")
						.append(DateTimeUtil.getWeekStartTime(1));
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
							
							return DateTimeUtil.getWeekStartTime(-(Integer.valueOf(value)-1)) < currentVal && currentVal <= DateTimeUtil.getWeekStartTime(1);
						}
						return false;
					}
				});
			}
			return null;
		}

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getWeekStartTime(-(Integer.valueOf(value)-1)), DateTimeUtil.getWeekStartTime(1));
		}
		
		@Override
		public boolean isDynamicOperator() {
			return true;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return true;
		}
	},
	CURRENT_N_DAY(62, "Current N Day"){
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getDayStartTimeOf(Long.parseLong(value)))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<=")
						.append(DateTimeUtil.getDayEndTimeOf(Long.parseLong(value)));
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
							return DateTimeUtil.getDayStartTimeOf(Long.parseLong(value)) <= currentVal &&currentVal <= DateTimeUtil.getDayEndTimeOf(Long.parseLong(value));
						}
						return false;
					}
				});
			}
			return null;
		}

	
		@Override 
		public DateRange getRange(String value) {
			return new DateRange(DateTimeUtil.getDayStartTimeOf(Long.parseLong(value)), DateTimeUtil.getDayEndTimeOf(Long.parseLong(value)));
		}
		@Override
		public boolean isDynamicOperator() {
			return true;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
		}
		
	},
	CURRENT_N_WEEK(63, "Current N Week"){
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getWeekStartTimeOf(Long.parseLong(value)))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<=")
						.append(DateTimeUtil.getWeekEndTimeOf(Long.parseLong(value)));
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
							return DateTimeUtil.getWeekStartTimeOf(Long.parseLong(value)) <= currentVal &&currentVal <= DateTimeUtil.getWeekEndTimeOf(Long.parseLong(value));
						}
						return false;
					}
				});
			}
			return null;
		}

	
		@Override 
		public DateRange getRange(String value) {
			return new DateRange(DateTimeUtil.getWeekStartTimeOf(Long.parseLong(value)), DateTimeUtil.getWeekEndTimeOf(Long.parseLong(value)));
		}
		@Override
		public boolean isDynamicOperator() {
			return true;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
		}
	},
	CURRENT_N_MONTH(64, "Current N Month"){
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getMonthStartTimeOf(Long.parseLong(value)))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<=")
						.append(DateTimeUtil.getMonthEndTimeOf(Long.parseLong(value)));
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
							return DateTimeUtil.getMonthStartTimeOf(Long.parseLong(value)) <= currentVal &&currentVal <= DateTimeUtil.getMonthEndTimeOf(Long.parseLong(value));
						}
						return false;
					}
				});
			}
			return null;
		}

	
		@Override 
		public DateRange getRange(String value) {
			return new DateRange(DateTimeUtil.getMonthStartTimeOf(Long.parseLong(value)), DateTimeUtil.getMonthEndTimeOf(Long.parseLong(value)));
		}
		@Override
		public boolean isDynamicOperator() {
			return true;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
		}
	},
	CURRENT_N_YEAR(65, "Current N Year"){
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getYearStartTimeOf(Long.parseLong(value)))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<=")
						.append(DateTimeUtil.getYearEndTimeOf(Long.parseLong(value)));
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
							return DateTimeUtil.getYearStartTimeOf(Long.parseLong(value)) <= currentVal &&currentVal <= DateTimeUtil.getYearEndTimeOf(Long.parseLong(value));
						}
						return false;
					}
				});
			}
			return null;
		}

	
		@Override 
		public DateRange getRange(String value) {
			return new DateRange(DateTimeUtil.getYearStartTimeOf(Long.parseLong(value)), DateTimeUtil.getYearEndTimeOf(Long.parseLong(value)));
		}
		@Override
		public boolean isDynamicOperator() {
			return true;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}


		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
		}
	},
	THIS_MONTH_TILL_YESTERDAY(66, "This Month Till Yesterday"){
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				if(DateTimeUtil.getDayStartTime() == DateTimeUtil.getMonthStartTime()) {
					builder.append(DateTimeUtil.getMonthStartTime(-1));
				}
				else {
					builder.append(DateTimeUtil.getMonthStartTime());
				}
						builder.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<=")
						.append(DateTimeUtil.getDayStartTime() -1 );
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
							if(DateTimeUtil.getDayStartTime() == DateTimeUtil.getMonthStartTime()) {
								return DateTimeUtil.getMonthStartTimeOf(-1) <= currentVal &&currentVal <= (DateTimeUtil.getDayStartTime(-1) - 1);
							}
							else {
								return DateTimeUtil.getMonthStartTime() <= currentVal &&currentVal <= (DateTimeUtil.getDayStartTime(-1) - 1);
							}
						}
						return false;
					}
				});
			}
			return null;
		}

	
		@Override 
		public DateRange getRange(String value) {
			if(DateTimeUtil.getDayStartTime() == DateTimeUtil.getMonthStartTime()) {
				return new DateRange(DateTimeUtil.getMonthStartTime(-1), DateTimeUtil.getDayStartTime() - 1);
			}
			else {
				return new DateRange(DateTimeUtil.getMonthStartTime(), DateTimeUtil.getDayStartTime() - 1);
			}
		}
		@Override
		public boolean isDynamicOperator() {
			return true;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
		}
	},
	CURRENT_N_QUARTER(67, "Current N Quarter"){
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getQuarterStartTimeOf(Long.parseLong(value)))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<=")
						.append(DateTimeUtil.getQuarterEndTimeOf(Long.parseLong(value)));
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
							return DateTimeUtil.getQuarterStartTimeOf(Long.parseLong(value)) <= currentVal &&currentVal <= DateTimeUtil.getQuarterEndTimeOf(Long.parseLong(value));
						}
						return false;
					}
				});
			}
			return null;
		}

	
		@Override 
		public DateRange getRange(String value) {
			return new DateRange(DateTimeUtil.getQuarterStartTimeOf(Long.parseLong(value)), DateTimeUtil.getQuarterEndTimeOf(Long.parseLong(value)));
		}
		@Override
		public boolean isDynamicOperator() {
			return true;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
		}
		
	},
	LAST_N_QUARTERS(70, "LAST N Quarters"){
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getQuarterStartTime(-(Integer.parseInt(value) - 1)))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<=")
						.append(DateTimeUtil.getQuarterEndTime(0));
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
							return DateTimeUtil.getQuarterStartTime(-(Integer.parseInt(value) - 1)) <= currentVal &&currentVal <= DateTimeUtil.getQuarterEndTime(0);
						}
						return false;
					}
				});
			}
			return null;
		}

	
		@Override 
		public DateRange getRange(String value) {
			return new DateRange(DateTimeUtil.getQuarterStartTime(-(Integer.parseInt(value) - 1)), DateTimeUtil.getQuarterEndTime(0));
		}
		@Override
		public boolean isDynamicOperator() {
			return true;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return true;
		}
		
	},
	THIS_QUARTER(68, "This Quarter"){
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getQuarterStartTime(0))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<=")
						.append(DateTimeUtil.getQuarterEndTime(0));
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
							return DateTimeUtil.getQuarterStartTime(0) <= currentVal &&currentVal <= DateTimeUtil.getQuarterEndTimeOf(0);
						}
						return false;
					}
				});
			}
			return null;
		}

	
		@Override 
		public DateRange getRange(String value) {
			return new DateRange(DateTimeUtil.getQuarterStartTime(0), DateTimeUtil.getQuarterEndTime(0));
		}
		@Override
		public boolean isDynamicOperator() {
			return true;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return true;
		}
		
	},
	LAST_QUARTER(69, "This Quarter"){
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getQuarterStartTime(-1))
						.append("<=")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<=")
						.append(DateTimeUtil.getQuarterEndTime(-1));
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
							return DateTimeUtil.getQuarterStartTime(-1) <= currentVal &&currentVal <= DateTimeUtil.getQuarterEndTimeOf(-1);
						}
						return false;
					}
				});
			}
			return null;
		}

	
		@Override 
		public DateRange getRange(String value) {
			return new DateRange(DateTimeUtil.getQuarterStartTime(-1), DateTimeUtil.getQuarterEndTime(-1));
		}
		@Override
		public boolean isDynamicOperator() {
			return true;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return false;
		}
		
	},
	LAST_N_MONTHS(51, "Last N Months") {
		@Override
		public String getWhereClause(String columnName, String value) {
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append(DateTimeUtil.getMonthStartTime(-(Integer.valueOf(value)-1)))
						.append("<")
						.append(columnName)
						.append(" AND ")
						.append(columnName)
						.append("<=")
						.append(DateTimeUtil.getMonthStartTime(1));
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
							return DateTimeUtil.getMonthStartTime(-(Integer.valueOf(value)-1)) < currentVal && currentVal < DateTimeUtil.getMonthStartTime(1);
						}
						return false;
					}
				});
			}
			return null;
		}

		@Override
		public DateRange getRange(String value) {
			// TODO Auto-generated method stub
			return new DateRange(DateTimeUtil.getMonthStartTime(-(Integer.valueOf(value)-1)), DateTimeUtil.getMonthStartTime(1));
		}
		
		@Override
		public boolean isDynamicOperator() {
			return true;
		}

		@Override
		public boolean isBaseLineSupported() {
			// TODO Auto-generated method stub
			return true;
		}
		
		@Override
		public boolean isValueNeeded() {
			// TODO Auto-generated method stub
			return true;
		}

		@Override
		public boolean isCurrentOperator() {
			// TODO Auto-generated method stub
			return true;
		}
	};
	
	private static String greaterOrLessThan(String columnName, String operator, String value) {
		if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
			StringBuilder builder = new StringBuilder();
			builder.append(columnName)
					.append(operator)
					.append(value)
					;
			
			return builder.toString();
		}
		return null;
	}
	
	private static String betweenWhereClause(String columnName, String value, boolean isNot) {
		if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
			
			String values[] = value.trim().split("\\s*,\\s*");
			
			if(values != null && values.length > 1) {
			StringBuilder builder = new StringBuilder();
				builder.append(columnName);
				if(isNot) {
					builder.append(" NOT ");
				}
				builder.append(" BETWEEN ")
						.append(values[0])
						.append(" AND ")
						.append(values[1]);
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
	public boolean isDynamicOperator() {
		return false;
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
	
	public abstract DateRange getRange(String value);
	public abstract boolean isBaseLineSupported();
	public abstract boolean isCurrentOperator(); //Like today/ last n hours
	
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
