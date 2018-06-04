package com.facilio.bmsconsole.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
			
			public static AnalyticsType getType(int val) {
				return typeMap.get(val);
			}
			
			private static final Map<Integer, AnalyticsType> typeMap = Collections.unmodifiableMap(initTypeMap());
			private static Map<Integer, AnalyticsType> initTypeMap() {
				Map<Integer, AnalyticsType> typeMap = new HashMap<>();
				
				for(AnalyticsType type : values()) {
					typeMap.put(type.getIntVal(), type);
				}
				return typeMap;
			}
			public Map<Integer, AnalyticsType> getAllTypes() {
				return typeMap;
			}
		}
}
