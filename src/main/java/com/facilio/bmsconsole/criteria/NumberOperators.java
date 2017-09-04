package com.facilio.bmsconsole.criteria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanPredicate;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;

import com.facilio.bmsconsole.modules.FacilioField;

public enum NumberOperators implements Operator<String> {
	
	EQUALS("=") {
		@Override
		public String getWhereClause(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				if(value.contains(",")) {
					return field.getModuleTableName()+"."+field.getColumnName()+" IN ("+value+")";
				}
				else {
					return field.getModuleTableName()+"."+field.getColumnName()+" = "+value;
				}
			}
			return null;
		}

		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				return new BeanPredicate(field.getName(), computeEqualPredicate(value));
			}
			return null;
		}
	},
	NOT_EQUALS("!=") {
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
						.append(field.getColumnName());
				if(value.contains(",")) {
					builder.append(" NOT IN (")
							.append(value)
							.append(")");
				}
				else {
					builder.append(" != ")
							.append(value);
				}
				builder.append(")");
				return builder.toString();
			}
			return null;
		}

		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			// TODO Auto-generated method stub
			if(field != null && value != null && !value.isEmpty()) {
				return new BeanPredicate(field.getName(), PredicateUtils.notPredicate(computeEqualPredicate(value)));
			}
			return null;
		}
	},
	LESS_THAN("<") {
		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			if(field != null && value != null && !value.isEmpty()) {
				return new BeanPredicate(field.getName(), new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object instanceof Short) {
							return ((short) object) < Short.parseShort(value);
						}
						else if(object instanceof Integer) {
							return ((int) object) < Integer.parseInt(value);
						}
						else if(object instanceof Long) {
							return ((long) object) < Long.parseLong(value);
						}
						else if(object instanceof Float) {
							return ((float) object) < Float.parseFloat(value);
						}
						else if(object instanceof Double) {
							return ((double) object) < Double.parseDouble(value);
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	LESS_THAN_EQUAL("<=") {
		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			if(field != null && value != null && !value.isEmpty()) {
				return new BeanPredicate(field.getName(), new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object instanceof Short) {
							return ((short) object) <= Short.parseShort(value);
						}
						else if(object instanceof Integer) {
							return ((int) object) <= Integer.parseInt(value);
						}
						else if(object instanceof Long) {
							return ((long) object) <= Long.parseLong(value);
						}
						else if(object instanceof Float) {
							return ((float) object) <= Float.parseFloat(value);
						}
						else if(object instanceof Double) {
							return ((double) object) <= Double.parseDouble(value);
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	GREATER_THAN(">") {
		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			if(field != null && value != null && !value.isEmpty()) {
				return new BeanPredicate(field.getName(), new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object instanceof Short) {
							return ((short) object) > Short.parseShort(value);
						}
						else if(object instanceof Integer) {
							return ((int) object) > Integer.parseInt(value);
						}
						else if(object instanceof Long) {
							return ((long) object) > Long.parseLong(value);
						}
						else if(object instanceof Float) {
							return ((float) object) > Float.parseFloat(value);
						}
						else if(object instanceof Double) {
							return ((double) object) > Double.parseDouble(value);
						}
						return false;
					}
				});
			}
			return null;
		}
	},
	GREATER_THAN_EQUAL(">=") {
		@Override
		public Predicate getPredicate(FacilioField field, String value) {
			if(field != null && value != null && !value.isEmpty()) {
				return new BeanPredicate(field.getName(), new Predicate() {
					
					@Override
					public boolean evaluate(Object object) {
						// TODO Auto-generated method stub
						if(object instanceof Short) {
							return ((short) object) >= Short.parseShort(value);
						}
						else if(object instanceof Integer) {
							return ((int) object) >= Integer.parseInt(value);
						}
						else if(object instanceof Long) {
							return ((long) object) >= Long.parseLong(value);
						}
						else if(object instanceof Float) {
							return ((float) object) >= Float.parseFloat(value);
						}
						else if(object instanceof Double) {
							return ((double) object) >= Double.parseDouble(value);
						}
						return false;
					}
				});
			}
			return null;
		}
	}
	;
	
	@Override
	public String getWhereClause(FacilioField field, String value) {
		// TODO Auto-generated method stub
		if(field != null && value != null && !value.isEmpty()) {
			return field.getModuleTableName()+"."+field.getColumnName()+operator+value;
		}
		return null;
	};
	
	@Override
	public String getDynamicParameter() {
		return null;
	}
	
	@Override
	public List<Object> computeValues(String value) {
		return null;
	}
	
	private NumberOperators(String operator) {
		 this.operator = operator;
	}
	
	private String operator;
	@Override
	public String getOperator() {
		return operator;
	}
	
	private static Predicate computeEqualPredicate(String value) {
		if(value.contains(",")) {
			List<Predicate> equalPredicates = new ArrayList<>();
			String[] values = value.trim().split("\\s*,\\s*");
			for(String val : values) {
				equalPredicates.add(getEqualPredicate(val));
			}
			return PredicateUtils.anyPredicate(equalPredicates);
		}
		else {
			return getEqualPredicate(value);
		}
	}
	
	private static Predicate getEqualPredicate(String value) {
		return new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				// TODO Auto-generated method stub
				if(object != null) {
					if(object instanceof Short) {
						return ((short) object) == Short.parseShort(value);
					}
					else if(object instanceof Integer) {
						return ((int) object) == Integer.parseInt(value);
					}
					else if(object instanceof Long) {
						return ((long) object) == Long.parseLong(value);
					}
					else if(object instanceof Float) {
						return ((float) object) == Float.parseFloat(value);
					}
					else if(object instanceof Double) {
						return ((double) object) == Double.parseDouble(value);
					}
				}
				return false;
			}
		};
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
