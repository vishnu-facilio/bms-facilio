
package com.facilio.bmsconsole.criteria;

import java.util.List;

import org.apache.commons.collections.Predicate;

import com.facilio.bmsconsole.modules.FacilioField;

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
 	
	private long fieldId = -1;
	public long getFieldId() {
		if(field != null) {
			return field.getFieldId();
		}
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	
	private FacilioField field;
	public FacilioField getField() {
		return field;
	}
	public void setField(FacilioField field) {
		this.field = field;
	}
	
	private String operatorStr;
	public String getOperatorStr() {
		if(operator != null) {
			return operator.getOperator();
		}
		return operatorStr;
	}
	public void setOperatorStr(String operatorStr) {
		this.operatorStr = operatorStr;
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
		if(computedWhereClause == null && operator != null) {
			if(operator == LookupOperator.LOOKUP) {
				computedWhereClause = operator.getWhereClause(field, criteriaValue);
			}
			else {
				computedWhereClause = operator.getWhereClause(field, value);
			}
		}
		return computedWhereClause;
	}
	public void setComputedWhereClause(String computedWhereClause) {
		this.computedWhereClause = computedWhereClause;
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
	
	public Predicate getPredicate() {
		if(operator != null) {
			if(operator == LookupOperator.LOOKUP) {
				return operator.getPredicate(field, criteriaValue);
			}
			else {
				return operator.getPredicate(field, value);
			}
		}
		return null;
	}
}
