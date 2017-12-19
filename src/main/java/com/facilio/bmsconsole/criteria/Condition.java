
package com.facilio.bmsconsole.criteria;

import java.util.ArrayList;
import java.util.Collections;
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
	
	private FacilioField field;
	public void setField(FacilioField field) {
		this.field = field;
		if(field != null && field.getExtendedModule() != null && field.getName() != null && field.getColumnName() != null) {
			this.columnName = field.getExtendedModule().getTableName()+"."+field.getColumnName();
			this.fieldName = field.getName();
		}
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
		if(operator != null && (computedWhereClause == null || DYNAMIC_OPERATORS.contains(operator))) {
			if(operator == LookupOperator.LOOKUP) {
				updateFieldNameWithModule();
				computedWhereClause = operator.getWhereClause(fieldName, criteriaValue);
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
				updateFieldNameWithModule();
				return operator.getPredicate(fieldName, criteriaValue);
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
	
	private static List<Operator> DYNAMIC_OPERATORS = Collections.unmodifiableList(getDynamicOperators());
	private static List<Operator> getDynamicOperators() {
		List<Operator> dynamicOperators = new ArrayList<>();
		dynamicOperators.add(PickListOperators.IS);
		dynamicOperators.add(PickListOperators.ISN_T);
		dynamicOperators.add(DateOperators.TODAY);
		dynamicOperators.add(DateOperators.TOMORROW);
		dynamicOperators.add(DateOperators.STARTING_TOMORROW);
		dynamicOperators.add(DateOperators.YESTERDAY);
		dynamicOperators.add(DateOperators.TILL_YESTERDAY);
		dynamicOperators.add(DateOperators.LAST_MONTH);
		dynamicOperators.add(DateOperators.CURRENT_MONTH);
		dynamicOperators.add(DateOperators.NEXT_MONTH);
		dynamicOperators.add(DateOperators.LAST_WEEK);
		dynamicOperators.add(DateOperators.CURRENT_WEEK);
		dynamicOperators.add(DateOperators.NEXT_WEEK);
		return dynamicOperators;
	}
}
