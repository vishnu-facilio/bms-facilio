package com.facilio.workflows.context;

import com.facilio.bmsconsole.modules.AggregateOperator;
import com.facilio.bmsconsole.modules.FacilioField;

public class WorkflowFieldContext{

	private AggregateOperator aggregation;
	public AggregateOperator getAggregationEnum() {
		return aggregation;
	}
	public void setAggregation(AggregateOperator aggregation) {
		this.aggregation = aggregation;
	}
	public int getAggregation() {
		if (aggregation != null) {
			return aggregation.getValue();
		}
		return -1;
	}
	public void setAggregation(int aggregation) {
		this.aggregation = AggregateOperator.getAggregateOperator(aggregation);
	}

	private long resourceId = -1;
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}

	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private long workflowId = -1;
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	private long fieldId = -1;
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
	@Override
	public boolean equals(Object o)
    {
		if(o instanceof WorkflowFieldContext) {
			WorkflowFieldContext workflowField = (WorkflowFieldContext) o;
			if(workflowField.getOrgId() == this.getOrgId() && workflowField.getModuleId() == this.getModuleId() && workflowField.getFieldId() == this.getFieldId() && workflowField.getResourceId() == this.getResourceId()) {
				return true;
			}
		}
        return false;
    }
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder()
									.append("Field ID : ").append(fieldId).append("::")
									.append("Resource ID : ").append(resourceId).append("::")
									.append("Aggr : ").append(aggregation)
									;
		return builder.toString();
	}
}
