package com.facilio.bmsconsole.context;

import com.facilio.annotations.AnnotationEnums;
import com.facilio.annotations.ImmutableChildClass;
import com.facilio.db.criteria.Criteria;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@ImmutableChildClass(className = "ScopingConfigCacheContext",constructorPolicy = AnnotationEnums.ConstructorPolicy.REQUIRE_COPY_CONSTRUCTOR)
public class ScopingConfigContext implements Serializable{
	/**
	 * 
	 */

	public ScopingConfigContext(ScopingConfigContext object) {
		this.id = object.id;
		this.orgId = object.orgId;
		this.scopingId = object.scopingId;
		this.moduleId = object.moduleId;
		this.criteriaId = object.criteriaId;
		this.criteria = object.criteria;
		this.fieldName = object.fieldName;
		this.value = object.value;
		this.operatorId = object.operatorId;
		this.valueGenerator = object.valueGenerator;
	}

	private static final long serialVersionUID = 1L;
	
	private long id;
	private long orgId;
	private long scopingId;
	private long moduleId;
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
	private long criteriaId;
	private Criteria criteria;

	public long getCriteriaId() {
		return criteriaId;
	}

	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}

	public Criteria getCriteria() {
		return criteria;
	}

	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	private String fieldName;
	private String value;
	private long operatorId;
	private String valueGenerator;

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public long getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(long operatorId) {
		this.operatorId = operatorId;
	}

	public String getValueGenerator() {
		return valueGenerator;
	}

	public void setValueGenerator(String valueGenerator) {
		this.valueGenerator = valueGenerator;
	}
}
