
package com.facilio.bmsconsole.criteria;

import java.util.List;

import com.facilio.bmsconsole.modules.FacilioField;

public class Condition {
	
	private long conditionId;
	public long getConditionId() {
		return conditionId;
	}
	public void setConditionId(long conditionId) {
		this.conditionId = conditionId;
	}
	
	private long criteriaId;
	public long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	
	private int sequence;
	public int getSequence() {
		return sequence;
	}
	public void setSequence(int sequence) {
		this.sequence = sequence;
	}
 	
	private long fieldId;
	public long getFieldId() {
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
	
	private String computedWhereClause;
	public String getComputedWhereClause() {
		if(computedWhereClause == null) {
			computedWhereClause = operator.getWhereClause(field.getColumnName(), value);
		}
		return computedWhereClause;
	}
	public void setComputedWhereClause(String computedWhereClause) {
		this.computedWhereClause = computedWhereClause;
	}
	
	public List<Object> getComputedValues() {
		return operator.computeValues(value);
	}
}
