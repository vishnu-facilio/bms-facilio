package com.facilio.bmsconsole.context;

import java.io.Serializable;

import com.facilio.db.criteria.operators.Operator;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ScopingConfigContext implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long id;
	private long orgId;
	private long scopingId;
	private long moduleId;
	private String fieldName;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	public long getScopingId() {
		return scopingId;
	}
	public void setScopingId(long scopingId) {
		this.scopingId = scopingId;
	}
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	
	private String value;
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
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
	
	private int operatorId;
	public int getOperatorId() {
		if(operator != null) {
			return operator.getOperatorId();
		}
		return operatorId;
	}
	public void setOperatorId(int operatorId) {
		this.operatorId = operatorId;
		this.setOperator(Operator.getOperator(operatorId));
	}
	
	private String fieldValueGenerator;
	public String getFieldValueGenerator() {
		return fieldValueGenerator;
	}
	public void setFieldValueGenerator(String fieldValueGenerator) {
		this.fieldValueGenerator = fieldValueGenerator;
	}
	
	private Boolean isMandatory;

	public Boolean getIsMandatory() {
		return isMandatory;
	}

	public void setIsMandatory(Boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	public boolean isMandatory() {
		if (isMandatory != null) {
			return isMandatory.booleanValue();
		}
		return false;
	} 
	
}
