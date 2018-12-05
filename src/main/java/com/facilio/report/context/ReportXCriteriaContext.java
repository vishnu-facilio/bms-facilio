package com.facilio.report.context;

import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.workflows.context.WorkflowContext;

public class ReportXCriteriaContext {

	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	private long xFieldId = -1;
	public long getxFieldId() {
		return xFieldId;
	}
	public void setxFieldId(long xFieldId) {
		this.xFieldId = xFieldId;
	}
	
	private String xFieldName;
	public String getxFieldName() {
		return xFieldName;
	}
	public void setxFieldName(String xFieldName) {
		this.xFieldName = xFieldName;
	}
	
	private FacilioField xField;
	public FacilioField getxField() {
		return xField;
	}
	public void setxField(FacilioField xField) {
		this.xField = xField;
		
		this.xFieldId = xField.getId();
		this.xFieldName = xField.getName();
		
		if (xField.getModule() != null) {
			this.moduleName = xField.getModule().getName();
		}
	}

	private long criteriaId = -1;
	public long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(long criteriaId) {
		this.criteriaId = criteriaId;
	}
	
	private Criteria criteria;
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}

	private WorkflowContext transformWorkflow;
	public WorkflowContext getTransformWorkflow() {
		return transformWorkflow;
	}
	public void setTransformWorkflow(WorkflowContext transformWorkflow) {
		this.transformWorkflow = transformWorkflow;
	}
}
