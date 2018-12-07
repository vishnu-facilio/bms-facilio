package com.facilio.report.context;

import java.util.List;
import java.util.Map;

import com.facilio.report.context.ReportDataPointContext.DataPointType;
import com.facilio.report.context.ReportDataPointContext.OrderByFunction;
import com.facilio.workflows.context.WorkflowContext;

public class ReadingAnalysisContext {
	
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
	
	private String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	private List<Long> parentId;
	public List<Long> getParentId() {
		return parentId;
	}
	public void setParentId(List<Long> parentId) {
		this.parentId = parentId;
	}
	
	private DataPointType type = DataPointType.MODULE;
	public DataPointType getTypeEnum() {
		return type;
	}
	public void setType(DataPointType type) {
		this.type = type;
	}
	public int getType() {
		if (type != null) {
			return type.getValue();
		}
		return -1;
	}
	public void setType(int type) {
		this.type = DataPointType.valueOf(type);
	}

	private Map<String, String> aliases; 
	public Map<String, String> getAliases() {
		return aliases;
	}
	public void setAliases(Map<String, String> aliases) {
		this.aliases = aliases;
	}

	private ReportYAxisContext yAxis;
	public ReportYAxisContext getyAxis() {
		return yAxis;
	}
	public void setyAxis(ReportYAxisContext yAxis) {
		this.yAxis = yAxis;
	}
	
	private OrderByFunction orderByFunc;
	public OrderByFunction getOrderByFunc() {
		return orderByFunc;
	}
	public void setOrderByFunc(OrderByFunction orderByFunc) {
		this.orderByFunc = orderByFunc;
	}
	public OrderByFunction getOrderByFuncEnum() {
		return orderByFunc;
	}
	public void setOrderByFunc(int orderByFunc) {
		this.orderByFunc = OrderByFunction.valueOf(orderByFunc);
	}

	private int limit = -1;
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}

	public static enum ReportMode {
		TIMESERIES,
		SERIES,
		CONSOLIDATED
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		public static ReportMode valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
	
	public static enum AnalyticsType {
		
		PORTFOLIO("Portfolio Analysis"),
		BUILDING("Building Analysis"),
		HEAT_MAP("Heat Map Analysis"),
		REGRESSION("Regression Analysis"),
		READINGS("Readings Analysis"),
		SITE("Site Analysis")
		;
		
		private String strVal;
		
		private AnalyticsType(String strVal) {
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return ordinal() + 1;
		}
		public String getStringVal() {
			return strVal;
		}
		
		public static AnalyticsType getType(int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
		
	}
	
	public static enum XCriteriaMode {
		NONE,
		ALL_ASSET_CATEGORY,
		SPECIFIC_ASSETS_OF_CATEGORY
		;
		
		public int getValue() {
			return ordinal() + 1;
		}
		
		public static XCriteriaMode valueOf (int value) {
			if (value > 0 && value <= values().length) {
				return values() [value - 1];
			}
			return null;
		}
	}
}
