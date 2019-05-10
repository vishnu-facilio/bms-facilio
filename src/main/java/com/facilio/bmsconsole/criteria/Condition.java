
package com.facilio.bmsconsole.criteria;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.util.FacilioExpressionWrapper;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.collections.Predicate;
import org.apache.commons.collections4.MapUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Condition implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long conditionId = -1;
	public long getConditionId() {
		return conditionId;
	}
	public void setConditionId(long conditionId) {
		this.conditionId = conditionId;
	}
	
	private long parentCriteriaId = -1;
	public long getParentCriteriaId() {
		return parentCriteriaId;
	}
	public void setParentCriteriaId(long parentCriteriaId) {
		this.parentCriteriaId = parentCriteriaId;
	}
	
	private int sequence = -1;
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
	
	private FacilioField field;
	public void setField(FacilioField field) {
		this.field = field;
		this.columnName = field.getCompleteColumnName();
		this.fieldName = field.getName();
	}
	
	private String columnName;
	public String getColumnName() {
		return columnName;
	}
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	private String fieldName;
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	private int operatorId;
	public int getOperatorId() {
		if(operator != null) {
			return operator.getOperatorId();
		}
		return operatorId;
	}
	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
		this.setOperator(Operator.OPERATOR_MAP.get(operatorId));
	}

	private Operator operator;
	
	@JsonIgnore
	public Operator getOperator() {
		return operator;
	}
	public void setOperator(Operator operator) {
		this.operatorId = operator.getOperatorId();
		this.operator = operator;
	}
	
	private String value;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	private FacilioField valueField;
	public void setValueField(FacilioField valueField) {
		this.valueField = valueField;
		this.value = valueField.getModule().getName() + "." + valueField.getName();
	}
	
	private long criteriaValueId = -1;
	public long getCriteriaValueId() {
		return criteriaValueId;
	}
	public void setCriteriaValueId(long criteriaValueId) {
		this.criteriaValueId = criteriaValueId;
	}
	
	private Criteria criteriaValue;
	public Criteria getCriteriaValue() {
		return criteriaValue;
	}
	public void setCriteriaValue(Criteria criteriaVal) {
		this.criteriaValue = criteriaVal;
	}
	
	public String computeAndGetWhereClause() {
		return computeAndGetWhereClause(null);
	}
	
	public String computeAndGetWhereClause(Map<String, Object> placeHolders) {
		if(computedWhereClause == null || operator.isDynamicOperator() || isExpressionValue()) {
			if(operator == LookupOperator.LOOKUP) {
				updateFieldNameWithModule();
				computedWhereClause = operator.getWhereClause(fieldName, criteriaValue);
			} 
			else if (operator instanceof FieldOperator) {
				computedWhereClause = ((FieldOperator) operator).getWhereClause(getFieldNameWithModule(), getFieldValueWithModule());
			}
			else if (operator == EnumOperators.VALUE_IS || operator == EnumOperators.VALUE_ISN_T) {
				updateFieldNameWithModule();
				computedWhereClause = operator.getWhereClause(fieldName, value);
			}
			else if (placeHolders != null && !placeHolders.isEmpty() && isExpressionValue() && Arrays.asList(NumberOperators.values()).contains(operator)) {
				computedWhereClause = operator.getWhereClause(columnName, evaluateExpression(placeHolders));									
			}
			else {
				computedWhereClause = operator.getWhereClause(columnName, value);
			}
		}
		return computedWhereClause;
	}
	
	private String computedWhereClause;
	public String getComputedWhereClause() {
		return computedWhereClause;
	}
	public void setComputedWhereClause(String computedWhereClause) {
		this.computedWhereClause = computedWhereClause;
	}
	
	private String evaluateExpression(Map<String, Object> placeHolders) {
		FacilioExpressionWrapper exp = new FacilioExpressionWrapper(value)
											.setVariablesInObject(placeHolders);
		BigDecimal val = exp.evaluate();
		return String.valueOf(val);
	}
	
	private Boolean isExpressionValue;
	public Boolean getIsExpressionValue() {
		return isExpressionValue;
	}
	public void setIsExpressionValue(Boolean isExpressionValue) {
		this.isExpressionValue = isExpressionValue;
	}
	public boolean isExpressionValue() {
		if(isExpressionValue != null) {
			return isExpressionValue.booleanValue();
		}
		return false;
	}
	
	@JsonIgnore
	public List<Object> getComputedValues() {
		if(operator != null) {
			if(operator == LookupOperator.LOOKUP) {
				return operator.computeValues(criteriaValue);
			}
			else {
				return operator.computeValues(value);
			}
		}
		return null;
	}
	
	public Predicate computePredicate() {
		return computePredicate(null);
	}
	public Predicate computePredicate(Map<String, Object> placeHolders) {
		if(operator != null) {
			if(operator == LookupOperator.LOOKUP) {
				updateFieldNameWithModule();
				return operator.getPredicate(fieldName, criteriaValue);
			}
			else if (operator instanceof FieldOperator) {
				if (MapUtils.isEmpty(placeHolders)) {
					return null;
				}
				return operator.getPredicate(getFieldName(), placeHolders.get(getValue()));
			}
			else if (operator == EnumOperators.VALUE_IS || operator == EnumOperators.VALUE_ISN_T) {
				updateFieldNameWithModule();
				return operator.getPredicate(fieldName, value);
			}
			else if (placeHolders != null && !placeHolders.isEmpty() && isExpressionValue() && Arrays.asList(NumberOperators.values()).contains(operator)) {
				return operator.getPredicate(fieldName, evaluateExpression(placeHolders));
			}
			else {
				return operator.getPredicate(fieldName, value);
			}
		}
		return null;
	}
	
	private void updateFieldNameWithModule() {
		if(field != null) {
			fieldName = field.getModule().getName()+"."+fieldName;
			field = null;
		}
	}
	
	private String getFieldNameWithModule() {
		String fieldName = this.fieldName;
		if(field != null) {
			fieldName = field.getModule().getName()+"."+field.getName();
		}
		return fieldName;
	}
	
	private String getFieldValueWithModule() {
		String fieldName = this.value;
		if(valueField != null) {
			fieldName = valueField.getModule().getName()+"."+valueField.getName();
		}
		return fieldName;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return fieldName+"::"+operator.getOperator()+"::"+value;
	}
	
	@Override
	public boolean equals(Object other) {
		// TODO Auto-generated method stub
		if (this == other) {
            return true;
        }
		if (other != null && other instanceof Condition ) {
			Condition otherCondition = (Condition) other;
			if (Objects.equals(operator, operator) &&
					Objects.equals(this.columnName, otherCondition.columnName) &&
					Objects.equals(this.fieldName, otherCondition.fieldName) &&
					Objects.equals(this.value, otherCondition.value) &&
					Objects.equals(this.criteriaValue, otherCondition.criteriaValue) ) {
						return true;
			}
		}
		return false;
	}
}
