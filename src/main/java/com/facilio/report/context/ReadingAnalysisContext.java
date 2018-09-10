package com.facilio.report.context;

import java.util.List;

import com.facilio.bmsconsole.context.FormulaContext.AggregateOperator;

public class ReadingAnalysisContext {
	private List<Long> parentId;
	public List<Long> getParentId() {
		return parentId;
	}
	public void setParentId(List<Long> parentId) {
		this.parentId = parentId;
	}

	private long fieldId = -1;
	public long getFieldId() {
		return fieldId;
	}
	public void setFieldId(long fieldId) {
		this.fieldId = fieldId;
	}
	
	private AggregateOperator yAggr;
	public AggregateOperator getyAggrEnum() {
		return yAggr;
	}
	public void setyAggr(AggregateOperator yAggr) {
		this.yAggr = yAggr;
	}
	public int getyAggr() {
		if (yAggr != null) {
			return yAggr.getValue();
		}
		return -1;
	}
	public void setyAggr(int yAggr) {
		this.yAggr = AggregateOperator.getAggregateOperator(yAggr);
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
