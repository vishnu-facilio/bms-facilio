package com.facilio.report.context;

import java.util.List;

import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.workflows.context.WorkflowContext;

public class ReportDataPointContext {

	private long id = -1; 
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
	
	private long siteId = -1;
	public long getSiteId() {
		return siteId;
	}
	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	private long reportId = -1;
	public long getReportId() {
		return reportId;
	}
	public void setReportId(long reportId) {
		this.reportId = reportId;
	}
	
	private ReportAxisContext xAxis;
	public ReportAxisContext getxAxis() {
		return xAxis;
	}
	public void setxAxis(ReportAxisContext xAxis) {
		this.xAxis = xAxis;
	}

	private ReportAxisContext yAxis;
	public ReportAxisContext getyAxis() {
		return yAxis;
	}
	public void setyAxis(ReportAxisContext yAxis) {
		this.yAxis = yAxis;
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

	private long transformCriteriaId = -1;
	public long getTransformCriteriaId() {
		return transformCriteriaId;
	}
	public void setTransformCriteriaId(long transformCriteriaId) {
		this.transformCriteriaId = transformCriteriaId;
	}
	
	private Criteria transformCriteria;
	public Criteria getTransformCriteria() {
		return transformCriteria;
	}
	public void setTransformCriteria(Criteria transformCriteria) {
		this.transformCriteria = transformCriteria;
	}
	
	private long transformWorkflowId = -1;
	public long getTransformWorkflowId() {
		return transformWorkflowId;
	}
	public void setTransformWorkflowId(long transformWorkflowId) {
		this.transformWorkflowId = transformWorkflowId;
	}
	
	private WorkflowContext transformWorkflow;
	public WorkflowContext getTransformWorkflow() {
		return transformWorkflow;
	}
	public void setTransformWorkflow(WorkflowContext transformWorkflow) {
		this.transformWorkflow = transformWorkflow;
	}
	
	private int limit = -1;
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	
	private String orderBy;
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
	private OrderByFunction orderByFunc;
	public OrderByFunction getOrderByFuncEnum() {
		return orderByFunc;
	}
	public void setOrderByFunc(OrderByFunction orderByFunc) {
		this.orderByFunc = orderByFunc;
	}
	public int getOrderByFunc() {
		if (orderByFunc != null) {
			return orderByFunc.getValue();
		}
		return -1;
	}
	public void setOrderByFunc(int orderByFunc) {
		this.orderByFunc = OrderByFunction.valueOf(orderByFunc);
	}
	
	private List<ReportGroupByField> groupByFields;
	public List<ReportGroupByField> getGroupByFields() {
		return groupByFields;
	}
	public void setGroupByFields(List<ReportGroupByField> groupByFields) {
		this.groupByFields = groupByFields;
	}

	private long dateFieldId = -1;
	public long getDateFieldId() {
		return dateFieldId;
	}
	public void setDateFieldId(long dateFieldId) {
		this.dateFieldId = dateFieldId;
	}

	private FacilioField dateField;
	public FacilioField getDateField() {
		return dateField;
	}
	public void setDateField(FacilioField dateField) {
		this.dateField = dateField;
	}

	public static enum OrderByFunction {
		ACCENDING ("ASC"),
		DESCENDING ("DESC");
		
		OrderByFunction(String stringValue) {
			this.stringValue = stringValue;
		}
		
		private String stringValue;
		public String getStringValue() {
			return stringValue;
		}
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static OrderByFunction valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
		
	}
}
