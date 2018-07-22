package com.facilio.report.context;

import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.unitconversion.Unit;
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
	
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	private long moduleId = -1;
	public long getModuleId() {
		return moduleId;
	}
	public void setModuleId(long moduleId) {
		this.moduleId = moduleId;
	}
	
	private long xAxisFieldId = -1;
	public long getxAxisFieldId() {
		return xAxisFieldId;
	}
	public void setxAxisFieldId(long xAxisFieldId) {
		this.xAxisFieldId = xAxisFieldId;
	}
	
	private String xAxisFieldName;
	public String getxAxisFieldName() {
		return xAxisFieldName;
	}
	public void setxAxisFieldName(String xAxisFieldName) {
		this.xAxisFieldName = xAxisFieldName;
	}
	
	private FacilioField xAxisField;
	public FacilioField getxAxisField() {
		return xAxisField;
	}
	public void setxAxisField(FacilioField xAxisField) {
		this.xAxisField = xAxisField;
	}
	
	private String xAxisLabel;
	public String getxAxisLabel() {
		return xAxisLabel;
	}
	public void setxAxisLabel(String xAxisLabel) {
		this.xAxisLabel = xAxisLabel;
	}

	private AggregateOperator xAxisAggr;
	public AggregateOperator getxAxisAggrEnum() {
		return xAxisAggr;
	}
	public void setxAxisAggr(AggregateOperator xAxisAggr) {
		this.xAxisAggr = xAxisAggr;
	}
	public int getxAxisAggr() {
		if (xAxisAggr != null) {
			return xAxisAggr.getValue();
		}
		return -1;
	}
	public void setxAxisAggr(int xAxisAggr) {
		this.xAxisAggr = AggregateOperator.getAggregateOperator(xAxisAggr);
	}
	
	private Unit xAxisUnit;
	public Unit getxAxisUnitEnum() {
		return xAxisUnit;
	}
	public void setxAxisUnit(Unit xAxisUnit) {
		this.xAxisUnit = xAxisUnit;
	}
	public int getxAxisUnit() {
		if (xAxisUnit != null) {
			return xAxisUnit.getUnitId();
		}
		return -1;
	}
	public void setxAxisUnit(int xAxisUnit) {
		this.xAxisUnit = Unit.valueOf(xAxisUnit);
	}

	private long yAxisFieldId = -1;
	public long getyAxisFieldId() {
		return yAxisFieldId;
	}
	public void setyAxisFieldId(long yAxisFieldId) {
		this.yAxisFieldId = yAxisFieldId;
	}
	
	private String yAxisFieldName;
	public String getyAxisFieldName() {
		return yAxisFieldName;
	}
	public void setyAxisFieldName(String yAxisFieldName) {
		this.yAxisFieldName = yAxisFieldName;
	}
	
	private FacilioField yAxisField;
	public FacilioField getyAxisField() {
		return yAxisField;
	}
	public void setyAxisField(FacilioField yAxisField) {
		this.yAxisField = yAxisField;
	}
	
	private String yAxisLabel;
	public String getyAxisLabel() {
		return yAxisLabel;
	}
	public void setyAxisLabel(String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
	}
	
	private AggregateOperator yAxisAggr;
	public AggregateOperator getyAxisAggrEnum() {
		return yAxisAggr;
	}
	public void setyAxisAggr(AggregateOperator yAxisAggr) {
		this.yAxisAggr = yAxisAggr;
	}
	public int getyAxisAggr() {
		if (yAxisAggr != null) {
			return yAxisAggr.getValue();
		}
		return -1;
	}
	public void setyAxisAggr(int yAxisAggr) {
		this.yAxisAggr = AggregateOperator.getAggregateOperator(yAxisAggr);
	}
	
	private Unit yAxisUnit;
	public Unit getyAxisUnitEnum() {
		return yAxisUnit;
	}
	public void setyAxisUnit(Unit yAxisUnit) {
		this.yAxisUnit = yAxisUnit;
	}
	public int getyAxisUnit() {
		if (yAxisUnit != null) {
			return yAxisUnit.getUnitId();
		}
		return -1;
	}
	public void setyAxisUnit(int yAxisUnit) {
		this.yAxisUnit = Unit.valueOf(yAxisUnit);
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

	public enum OrderByFunction {
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
