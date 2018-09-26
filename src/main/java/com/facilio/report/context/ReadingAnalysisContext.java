package com.facilio.report.context;

import java.util.List;
import java.util.Map;

import com.facilio.report.context.ReportDataPointContext.DataPointType;
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

	private ReportAxisContext yAxis;
	public ReportAxisContext getyAxis() {
		return yAxis;
	}
	public void setyAxis(ReportAxisContext yAxis) {
		this.yAxis = yAxis;
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
		
		PORTFOLIO(1, "Portfolio Analysis"),
		BUILDING(2, "Building Analysis"),
		HEAT_MAP(3, "Heat Map Analysis"),
		REGRESSION(4, "Regression Analysis"),
		READINGS(5, "Readings Analysis")
		;
		
		private int intVal;
		private String strVal;
		
		private AnalyticsType(int intVal, String strVal) {
			this.intVal = intVal;
			this.strVal = strVal;
		}
		
		public int getIntVal() {
			return intVal;
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
}
