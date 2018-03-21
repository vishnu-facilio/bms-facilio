
package com.facilio.bmsconsole.criteria;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Predicate;

import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.util.FacilioExpressionWrapper;

public class Condition {
	
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
		this.columnName = field.getExtendedModule().getTableName()+"."+field.getColumnName();
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
	public Operator getOperator() {
		return operator;
	}
	public void setOperator(Operator operator) {
		this.operator = operator;
	}
	
	private String value;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
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
	
	private String computedWhereClause;
	public String getComputedWhereClause() {
		return getComputedWhereClause(null);
	}
	
	public String getComputedWhereClause(Map<String, Object> placeHolders) {
		if(operator != null && (computedWhereClause == null || operator.isDynamicOperator() || isExpressionValue())) {
			if(operator == LookupOperator.LOOKUP) {
				updateFieldNameWithModule();
				computedWhereClause = operator.getWhereClause(fieldName, criteriaValue);
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
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return fieldName+"::"+operator.getOperator()+"::"+value;
	}
}
