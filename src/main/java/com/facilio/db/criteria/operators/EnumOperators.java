package com.facilio.db.criteria.operators;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.EnumField;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections.PredicateUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.*;

public enum EnumOperators implements Operator<String> {
	VALUE_IS(52, "value is") {
		@Override
		public String getWhereClause(String fieldName, String value) {
			// TODO Auto-generated method stub
			return computeIsIsntWhereClause(fieldName, value, false);
		}

		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				return new FacilioModulePredicate(fieldName, computeIsIsntPredicate(fieldName, value));
			}
			return null;
		}
	},
	VALUE_ISN_T(53, "value isn't") {
		@Override
		public String getWhereClause(String fieldName, String value) {
			// TODO Auto-generated method stub
			return computeIsIsntWhereClause(fieldName, value, true);
		}

		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				return new FacilioModulePredicate(fieldName, PredicateUtils.notPredicate(computeIsIsntPredicate(fieldName, value)));
			}
			return null;
		}
	},
	IS(54, "is") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				return columnName+" IN ("+value+")";
			}
			return null;
		}

		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				return new FacilioModulePredicate(fieldName, computeValueIsPredicate(value));
			}
			return null;
		}
	},
	ISN_T(55, "isn't") {
		@Override
		public String getWhereClause(String columnName, String value) {
			// TODO Auto-generated method stub
			if(columnName != null && !columnName.isEmpty() && value != null && !value.isEmpty()) {
				StringBuilder builder = new StringBuilder();
				builder.append("(")
						.append(columnName)
						.append(" IS NULL OR ")
						.append(columnName)
						.append(" NOT IN (")
						.append(value)
						.append("))");
				return builder.toString();
			}
			return null;
		}

		@Override
		public FacilioModulePredicate getPredicate(String fieldName, String value) {
			// TODO Auto-generated method stub
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				return new FacilioModulePredicate(fieldName, PredicateUtils.notPredicate(computeValueIsPredicate(value)));
			}
			return null;
		}
	}
	;
	private static Logger log = LogManager.getLogger(EnumOperators.class.getName());

	private static Predicate computeIsIsntPredicate(String fieldName, String value) {
		// TODO Auto-generated method stub
		try {
			String[] module = fieldName.split("\\.");
			if(module.length > 1) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				EnumField enumField = (EnumField) modBean.getField(module[1], module[0]);
				if(value.contains(",")) {
					List<Predicate> equalPredicates = new ArrayList<>();
					String[] values = value.trim().split("\\s*,\\s*");
					for(String val : values) {
						equalPredicates.add(computeSingleIsIsntPredicate(enumField, val));
					}
					return PredicateUtils.anyPredicate(equalPredicates);
				}
				else {
					return computeSingleIsIsntPredicate(enumField, value);
				}
			}
		}
		catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		return null;
	}
	
	private static Predicate computeSingleIsIsntPredicate(EnumField field, String value) {
		int index = field.getIndex(value);
		if (index == -1) {
			return null;
		}
		return new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				// TODO Auto-generated method stub
				if(object != null) {
					return value.equals(object);
				}
				return false;
			}
		};
	}
	
	private static String computeIsIsntWhereClause(String fieldName, String value, boolean isNot) {
		try {
			if(fieldName != null && !fieldName.isEmpty() && value != null && !value.isEmpty()) {
				String[] module = fieldName.split("\\.");
				if(module.length > 1) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					EnumField enumField = (EnumField) modBean.getField(module[1], module[0]);
					String values = splitAndReturnValues(enumField, value);
					if(values != null && !values.isEmpty()) {
						StringBuilder builder = new StringBuilder();
						builder.append(enumField.getTableName())
								.append(".")
								.append(enumField.getColumnName());
								if (isNot) {
									builder.append(" NOT");
								}
								builder.append(" IN (")
								.append(values)
								.append(")");
						return builder.toString();
					}
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e);
		}
		return null;
	}
	
	private static Predicate computeValueIsPredicate(String value) {
		if(value.contains(",")) {
			List<Predicate> equalPredicates = new ArrayList<>();
			String[] values = value.trim().split("\\s*,\\s*");
			for(String val : values) {
				equalPredicates.add(computeSingleValueIsPredicate(val));
			}
			return PredicateUtils.anyPredicate(equalPredicates);
		}
		else {
			return computeSingleValueIsPredicate(value);
		}
	}
	
	private static Predicate computeSingleValueIsPredicate(String value) {
		return new Predicate() {
			@Override
			public boolean evaluate(Object object) {
				// TODO Auto-generated method stub
				if(object != null) {
					double doubleVal = Double.parseDouble(value);
					double currentVal = Double.parseDouble(object.toString());
					return currentVal == doubleVal;
				}
				return false;
			}
		};
	}
	
	private static String splitAndReturnValues (EnumField field, String value) {
		String[] values = value.trim().split("\\s*,\\s*");
		StringJoiner joiner = new StringJoiner(",");
		for(int i=0; i<values.length; i++) {
			int index = field.getIndex(values[i]);
			if (index == -1) {
				return null;
			}
			joiner.add(String.valueOf(index));
		}
		return joiner.toString();
	}
	
	private EnumOperators(int operatorId, String operator) {
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
	
	@Override
	public abstract String getWhereClause(String fieldName, String value);
	
	@Override
	public abstract FacilioModulePredicate getPredicate(String fieldName, String value);
	
	@Override
	public boolean isDynamicOperator() {
		return false;
	}
	
	@Override
	public boolean isValueNeeded() {
		// TODO Auto-generated method stub
		return true;
	}
	
	@Override
	public List<Object> computeValues(String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateFieldNameWithModule() {
		return true;
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
