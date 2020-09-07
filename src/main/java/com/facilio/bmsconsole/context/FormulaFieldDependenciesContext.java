package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.context.LoggerContext.Status;

public class FormulaFieldDependenciesContext {
	
	private long id;
	private long parentFormulaResourceId;
	private long fieldId;
	private long resourceId;
	private Long dependentFormulaResourceId;
	private Long groupId;
	private ResourceContext resourceContext;
	private long resourceCount;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	public long getParentFormulaResourceId() {
		return parentFormulaResourceId;
	}
	public void setParentFormulaResourceId(long parentFormulaResourceId) {
		this.parentFormulaResourceId = parentFormulaResourceId;
	}
	public Long getDependentFormulaResourceId() {
		return dependentFormulaResourceId;
	}
	public void setDependentFormulaResourceId(Long dependentFormulaResourceId) {
		this.dependentFormulaResourceId = dependentFormulaResourceId;
	}
	public Long getGroupId() {
		return groupId;
	}
	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}
	public ResourceContext getResourceContext() {
		return resourceContext;
	}
	public void setResourceContext(ResourceContext resourceContext) {
		this.resourceContext = resourceContext;
	}
	public long getResourceCount() {
		return resourceCount;
	}
	public void setResourceCount(long resourceCount) {
		this.resourceCount = resourceCount;
	}
}
