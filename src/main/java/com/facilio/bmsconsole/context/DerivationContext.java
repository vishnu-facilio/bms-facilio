package com.facilio.bmsconsole.context;

import com.facilio.time.DateRange;
import com.facilio.report.context.ReadingAnalysisContext.AnalyticsType;
import com.facilio.workflows.context.WorkflowContext;

public class DerivationContext {
	private long id;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	private long orgId;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	private long workflowId;
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	
	private WorkflowContext workflow;
	public WorkflowContext getWorkflow() {
		return workflow;
	}
	public void setWorkflow(WorkflowContext workflow) {
		this.workflow = workflow;
	}

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	private AnalyticsType analyticsType;
	public int getAnalyticsType() {
		if(analyticsType != null) {
			return analyticsType.getIntVal();
		}
		return -1;
	}
	public AnalyticsType getAnalyticsTypeEnum() {
		return analyticsType;
	}
	public void setAnalyticsType(int type) {
		this.analyticsType = AnalyticsType.getType(type);
	}
	public void setAnalyticsType(AnalyticsType analyticsType) {
		this.analyticsType = analyticsType;
	}
	
	private long formulaId;
	public long getFormulaId() {
		return formulaId;
	}
	public void setFormulaId(long formulaId) {
		this.formulaId = formulaId;
	}
	
	private FormulaFieldContext formulaField;
	public FormulaFieldContext getFormulaField() {
		return formulaField;
	}
	public void setFormulaField(FormulaFieldContext formulaField) {
		this.formulaField = formulaField;
	}
	
	private DateRange dateRange;
	public DateRange getDateRange() {
		return dateRange;
	}
	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}

}
