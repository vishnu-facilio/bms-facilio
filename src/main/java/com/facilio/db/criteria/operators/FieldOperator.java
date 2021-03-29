package com.facilio.db.criteria.operators;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Predicate;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.db.criteria.FacilioModulePredicate;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.fields.FacilioField;
import com.google.common.base.Objects;

public enum FieldOperator implements Operator<Object> {

	EQUAL(74, " = ") {
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, Object valueField) {
			return new FacilioModulePredicate(fieldName, new Predicate() {
				@Override
				public boolean evaluate(Object object) {
					if ((object == null || (object instanceof Number && ((Number) object).intValue() == -1)) 
							&& ((valueField == null || (valueField instanceof Number && ((Number) object).intValue() == -1)))) {
						return true;
					}
					return Objects.equal(object, valueField);
				}
			});
		}
	},
	NOT_EQUAL(75, " != ") {
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, Object valueField) {
			return new FacilioModulePredicate(fieldName, new Predicate() {
				@Override
				public boolean evaluate(Object object) {
					if ((object == null || (object instanceof Number && ((Number) object).intValue() == -1)) 
							&& ((valueField == null || (valueField instanceof Number && ((Number) object).intValue() == -1)))) {
						return true;
					}
					return !Objects.equal(object, valueField);
				}
			});
		}
	},
	LESS_THAN(76, " < ") {
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, Object valueField) {
			return new FacilioModulePredicate(fieldName, new Predicate() {
				@Override
				public boolean evaluate(Object object) {
					if ((object == null || (object instanceof Number && ((Number) object).intValue() == -1)) 
							&& ((valueField == null || (valueField instanceof Number && ((Number) object).intValue() == -1)))) {
						return true;
					}
					if (object instanceof Number && valueField instanceof Number) {
						return ((Number) object).doubleValue() < ((Number) valueField).doubleValue();
					}
					return false;
				}
			});
		}
	},
	LESS_THAN_EQUAL(77, " <= ") {
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, Object valueField) {
			return new FacilioModulePredicate(fieldName, new Predicate() {
				@Override
				public boolean evaluate(Object object) {
					if ((object == null || (object instanceof Number && ((Number) object).intValue() == -1)) 
							&& ((valueField == null || (valueField instanceof Number && ((Number) object).intValue() == -1)))) {
						return true;
					}
					if (object instanceof Number && valueField instanceof Number) {
						return ((Number) object).doubleValue() <= ((Number) valueField).doubleValue();
					}
					return false;
				}
			});
		}
	},
	GREATER_THAN(78, " > ") {
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, Object valueField) {
			return new FacilioModulePredicate(fieldName, new Predicate() {
				@Override
				public boolean evaluate(Object object) {
					if ((object == null || (object instanceof Number && ((Number) object).intValue() == -1)) 
							&& ((valueField == null || (valueField instanceof Number && ((Number) object).intValue() == -1)))) {
						return true;
					}
					if (object instanceof Number && valueField instanceof Number) {
						return ((Number) object).doubleValue() > ((Number) valueField).doubleValue();
					}
					return false;
				}
			});
		}
	},
	GREATER_THAN_EQUAL(79, " >= ") {
		@Override
		public FacilioModulePredicate getPredicate(String fieldName, Object valueField) {
			return new FacilioModulePredicate(fieldName, new Predicate() {
				@Override
				public boolean evaluate(Object object) {
					if ((object == null || (object instanceof Number && ((Number) object).intValue() == -1)) 
							&& ((valueField == null || (valueField instanceof Number && ((Number) object).intValue() == -1)))) {
						return true;
					}
					if (object instanceof Number && valueField instanceof Number) {
						return ((Number) object).doubleValue() >= ((Number) valueField).doubleValue();
					}
					return false;
				}
			});
		}
	},
	;
	
	private static Logger log = LogManager.getLogger(FieldOperator.class.getName());

	private FieldOperator(int operatorId, String operator) {
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
	public String getWhereClause(String fieldName, Object valueFieldName) {
		try {
			if(StringUtils.isNotEmpty(fieldName) && valueFieldName != null && StringUtils.isNotEmpty((String) valueFieldName)) {
				String[] fieldData = fieldName.split("\\.");
				String[] valueData = valueFieldName.toString().split("\\.");
				if(fieldData.length > 1 && valueData.length > 1) {
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					
					FacilioField field = modBean.getField(fieldData[1], fieldData[0]);
					FacilioField value = modBean.getField(valueData[1], valueData[0]);
					
					if (field != null && value != null) {
						StringBuilder builder = new StringBuilder();
						builder.append(field.getCompleteColumnName());
						builder.append(getOperator());
						builder.append(value.getCompleteColumnName());
						return builder.toString();
					}
				}
			}
		} catch (Exception e) {
			log.info("Exception occurred ", e);
		}
		return null;
	}

	@Override
	public boolean isPlaceHoldersMandatory() {
		return true;
	}

	@Override
	public boolean isDynamicOperator() {
		return false;
	}

	@Override
	public boolean isValueNeeded() {
		return true;
	}

	@Override
	public List<Object> computeValues(Object value) {
		return null;
	}
	
	private static final Map<String, Operator> operatorMap = Collections.unmodifiableMap(initOperatorMap());
	private static Map<String, Operator> initOperatorMap() {
		Map<String, Operator> operatorMap = new HashMap<>();
		for(Operator operator : values()) {
			operatorMap.put(operator.getOperator().trim(), operator);
		}
		return operatorMap;
	}
	public static Map<String, Operator> getAllOperators() {
		return operatorMap;
	}
}
