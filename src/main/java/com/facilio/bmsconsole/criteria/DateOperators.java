package com.facilio.bmsconsole.criteria;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanPredicate;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.DateTimeUtil;

public enum DateOperators implements Operator<String> {
	
	IS("is") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				return field.getModuleTableName()+"."+field.getColumnName()+" = "+value;
			}
			return null;
		}

		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				Instant val = Instant.ofEpochMilli(Long.parseLong(value)).truncatedTo(ChronoUnit.MINUTES);
				return new BeanPredicate(field.getName(), new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							return currentVal.equals(val); 
						}
						return false;
					}
				});
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
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append(" IS NULL OR ")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append(" != ")
						.append(value.trim())
						.append(")");
				return builder.toString();
			}
			return null;
		}

		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				Instant val = Instant.ofEpochMilli(Long.parseLong(value)).truncatedTo(ChronoUnit.MINUTES);
				return new BeanPredicate(field.getName(), new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							return !currentVal.equals(val); 
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	IS_BEFORE("is before") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			return greaterOrLessThan(field.getModuleTableName()+"."+field.getColumnName(), "<", value);
		}

		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				Instant val = Instant.ofEpochMilli(Long.parseLong(value)).truncatedTo(ChronoUnit.MINUTES);
				return new BeanPredicate(field.getName(), new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							return currentVal.isBefore(val); 
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	IS_AFTER("is after") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			return greaterOrLessThan(field.getModuleTableName()+"."+field.getColumnName(), ">", value);
		}

		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				Instant val = Instant.ofEpochMilli(Long.parseLong(value)).truncatedTo(ChronoUnit.MINUTES);
				return new BeanPredicate(field.getName(), new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							return currentVal.isAfter(val);
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	BETWEEN("between") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			return betweenWhereClause(field.getModuleTableName()+"."+field.getColumnName(), value, false);
		}

		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				Predicate betweenPredicate = getBetweenPredicate(value);
				if(betweenPredicate != null) {
					return new BeanPredicate(field.getName(), betweenPredicate);
				}
			}
			return null;
		}
	},
	
	NOT_BETWEEN("not between") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			return betweenWhereClause(field.getModuleTableName()+"."+field.getColumnName(), value, true);
		}

		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				Predicate betweenPredicate = getBetweenPredicate(value);
				if(betweenPredicate != null) {
					return new BeanPredicate(field.getName(), PredicateUtils.notPredicate(betweenPredicate));
				}
			}
			return null;
		}
	},
	
	TODAY("Today") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				StringBuilder builder = new StringBuilder();
				builder.append("(CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) >= UNIX_TIMESTAMP(CURDATE()) AND CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) < UNIX_TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY)))");
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${TODAY}";
		}
		
		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			if(field != null) {
				return new BeanPredicate(field.getName(), new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getDayStartTime() <= currentVal && currentVal < DateTimeUtil.getDayStartTime(-1);
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	TOMORROW("Tomorrow") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				StringBuilder builder = new StringBuilder();
				builder.append("(CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) >= UNIX_TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY)) AND CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) < UNIX_TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 2 DAY)))");
				return builder.toString();
			}
			return null;	
		}
		
		@Override
		public String getDynamicParameter() {
			return "${TOMORROW}";
		}
		
		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			if(field != null) {
				return new BeanPredicate(field.getName(), new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getDayStartTime(-1) <= currentVal && currentVal < DateTimeUtil.getDayStartTime(-2);
						}
						return false;
					}
				});
			}
			return null;
		}
	 	
	},
	
	STARTING_TOMORROW("Starting Tomorrow") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				StringBuilder builder = new StringBuilder();
				builder.append("CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) >= UNIX_TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL 1 DAY))");
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${TOMORROWPLUS}";
		}
		
		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			if(field != null) {
				return new BeanPredicate(field.getName(), new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return currentVal >= DateTimeUtil.getDayStartTime(-1);
						}
						return false;
					}
				});
			}
			return null;
		}
	 	
	},
	
	YESTERDAY("Yesterday") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				StringBuilder builder = new StringBuilder();
				builder.append("(CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) >= UNIX_TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL 1 DAY)) AND CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) < UNIX_TIMESTAMP(CURDATE()))");
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${YESTERDAY}";
		}
		
		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			if(field != null) {
				return new BeanPredicate(field.getName(), new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getDayStartTime(1) <= currentVal && currentVal < DateTimeUtil.getDayStartTime();
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	TILL_YESTERDAY("Till Yesterday") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				StringBuilder builder = new StringBuilder();
				builder.append("CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) < UNIX_TIMESTAMP(CURDATE())");
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${YESTERDAYMINUS}";
		}
		
		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			if(field != null) {
				return new BeanPredicate(field.getName(), new Predicate() {
					
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
	
	LAST_MONTH("Last Month") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				StringBuilder builder = new StringBuilder();
				builder.append("(CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) >= UNIX_TIMESTAMP(DATE_ADD(LAST_DAY(DATE_SUB(CURDATE(), INTERVAL 2 MONTH)), INTERVAL 1 DAY)) AND CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) < UNIX_TIMESTAMP(DATE_ADD(LAST_DAY(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)), INTERVAL 1 DAY)))");
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${LASTMONTH}";
		}
		
		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			if(field != null) {
				return new BeanPredicate(field.getName(), new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getMonthStartTime(1) <= currentVal && currentVal < DateTimeUtil.getMonthStartTime();
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	CURRENT_MONTH("Current Month") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				StringBuilder builder = new StringBuilder();
				builder.append("(CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) >= UNIX_TIMESTAMP(DATE_ADD(LAST_DAY(DATE_SUB(CURDATE(), INTERVAL 1 MONTH)), INTERVAL 1 DAY)) AND CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) < UNIX_TIMESTAMP(DATE_ADD(LAST_DAY(CURDATE()), INTERVAL 1 DAY)))");
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${THISMONTH}";
		}
		
		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			if(field != null) {
				return new BeanPredicate(field.getName(), new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getMonthStartTime() <= currentVal && currentVal < DateTimeUtil.getMonthStartTime(-1);
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	NEXT_MONTH("Next Month") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				StringBuilder builder = new StringBuilder();
				builder.append("(CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) >= UNIX_TIMESTAMP(DATE_ADD(LAST_DAY(CURDATE()), INTERVAL 1 DAY)) AND CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) < UNIX_TIMESTAMP(DATE_ADD(LAST_DAY(DATE_ADD(CURDATE(), INTERVAL 1 MONTH)), INTERVAL 1 DAY)))");
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${NEXTMONTH}";
		}
		
		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			if(field != null) {
				return new BeanPredicate(field.getName(), new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getMonthStartTime(-1) <= currentVal && currentVal < DateTimeUtil.getMonthStartTime(-2);
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	LAST_WEEK("Last Week") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				StringBuilder builder = new StringBuilder();
				builder.append("(CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) >= UNIX_TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL (DAYOFWEEK(CURDATE()) - 1)+7 DAY)) AND CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) < UNIX_TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL (DAYOFWEEK(CURDATE()) - 1) DAY)))");
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${LASTWEEK}";
		}
		
		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			if(field != null) {
				return new BeanPredicate(field.getName(), new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getWeekStartTime(1) <= currentVal && currentVal < DateTimeUtil.getWeekStartTime();
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	CURRENT_WEEK("Current Week") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				StringBuilder builder = new StringBuilder();
				//Condition is >= first day of this week and < first day of next week
				builder.append("(CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) >= UNIX_TIMESTAMP(DATE_SUB(CURDATE(), INTERVAL (DAYOFWEEK(CURDATE()) - 1) DAY)) AND CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) < UNIX_TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL (7 - DAYOFWEEK(CURDATE()))+1 DAY)))");
				return builder.toString(); 
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${THISWEEK}";
		}
		
		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			if(field != null) {
				return new BeanPredicate(field.getName(), new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getWeekStartTime() <= currentVal && currentVal < DateTimeUtil.getWeekStartTime(-1);
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	NEXT_WEEK("Next Week") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null) {
				StringBuilder builder = new StringBuilder();
				builder.append("(CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) >= UNIX_TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL (7 - DAYOFWEEK(CURDATE()))+1 DAY)) AND CEIL(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000) < UNIX_TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL (7 - DAYOFWEEK(CURDATE()))+8 DAY)))");
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public String getDynamicParameter() {
			return "${NEXTWEEK}";
		}
		
		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			if(field != null) {
				return new BeanPredicate(field.getName(), new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object != null && object instanceof Long) {
							//Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
							long currentVal = (long) object;
							return DateTimeUtil.getWeekStartTime(-1) <= currentVal && currentVal < DateTimeUtil.getWeekStartTime(-2);
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	
	AGE_IN_DAYS("Age in Days") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append("CEIL((UNIX_TIMESTAMP(CURDATE())-(")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000))/(24*3600)) = ")
						.append(value);
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			if(field != null && value != null && !value.isEmpty()) {
				int age = Integer.parseInt(value);
				return new BeanPredicate(field.getName(), new Predicate() {
					
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
	
	DUE_IN_DAYS("Due in Days") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append("FLOOR(((")
						.append(field.getModuleTableName())
						.append(".")
						.append(field.getColumnName())
						.append("/1000)-UNIX_TIMESTAMP(CURDATE()))/(24*3600)) = ")
						.append(value);
				return builder.toString();
			}
			return null;
		}
		
		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			if(field != null && value != null && !value.isEmpty()) {
				int due = Integer.parseInt(value);
				return new BeanPredicate(field.getName(), new Predicate() {
					
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
	
	private static Predicate getBetweenPredicate(String value) {
		String values[] = value.trim().split("\\s*,\\s*");
		
		if(values != null && values.length > 1) {
			Instant startVal = Instant.ofEpochMilli(Long.parseLong(values[0])).truncatedTo(ChronoUnit.MINUTES);
			Instant endVal = Instant.ofEpochMilli(Long.parseLong(values[1])).truncatedTo(ChronoUnit.MINUTES);
			return new Predicate() {
				
				@Override
				public boolean evaluate(Object object) {
					// TODO Auto-generated method stub
					if(object != null && object instanceof Long) {
						Instant currentVal = Instant.ofEpochMilli((long) object).truncatedTo(ChronoUnit.MINUTES);
						return currentVal.equals(startVal) || (currentVal.isAfter(startVal) && currentVal.isBefore(endVal)) || currentVal.equals(endVal);
					}
					return false;
				}
			};
		}
		return null;
	}
}
