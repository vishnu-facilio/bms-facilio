package com.facilio.report.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.facilio.bmsconsole.context.ReportContext.OrderByFunction;

public class ReportDataPointContext {

	Long id,moduleId,reportId,xAxisField,yAxisField,criteriaId,transformCriteriaId,transformWorkflowId;
	Integer xAxisAggr,xAxisUnit,yAxisAggr,yAxisUnit,limit,orderByFunc;
	String name,moduleName,xAxisFieldName,yAxisFieldName,yAxisLabel,orderBy;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Long getModuleId() {
		return moduleId;
	}
	public void setModuleId(Long moduleId) {
		this.moduleId = moduleId;
	}
	public Long getReportId() {
		return reportId;
	}
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	public Long getxAxisField() {
		return xAxisField;
	}
	public void setxAxisField(Long xAxisField) {
		this.xAxisField = xAxisField;
	}
	public Long getyAxisField() {
		return yAxisField;
	}
	public void setyAxisField(Long yAxisField) {
		this.yAxisField = yAxisField;
	}
	public Long getCriteriaId() {
		return criteriaId;
	}
	public void setCriteriaId(Long criteriaId) {
		this.criteriaId = criteriaId;
	}
	public Long getTransformCriteriaId() {
		return transformCriteriaId;
	}
	public void setTransformCriteriaId(Long transformCriteriaId) {
		this.transformCriteriaId = transformCriteriaId;
	}
	public Long getTransformWorkflowId() {
		return transformWorkflowId;
	}
	public void setTransformWorkflowId(Long transformWorkflowId) {
		this.transformWorkflowId = transformWorkflowId;
	}
	public Integer getxAxisAggr() {
		return xAxisAggr;
	}
	public void setxAxisAggr(Integer xAxisAggr) {
		this.xAxisAggr = xAxisAggr;
	}
	public Integer getxAxisUnit() {
		return xAxisUnit;
	}
	public void setxAxisUnit(Integer xAxisUnit) {
		this.xAxisUnit = xAxisUnit;
	}
	public Integer getyAxisAggr() {
		return yAxisAggr;
	}
	public void setyAxisAggr(Integer yAxisAggr) {
		this.yAxisAggr = yAxisAggr;
	}
	public Integer getyAxisUnit() {
		return yAxisUnit;
	}
	public void setyAxisUnit(Integer yAxisUnit) {
		this.yAxisUnit = yAxisUnit;
	}
	public Integer getLimit() {
		return limit;
	}
	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	public Integer getOrderByFunc() {
		return orderByFunc;
	}
	public void setOrderByFunc(Integer orderByFunc) {
		this.orderByFunc = orderByFunc;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public String getxAxisFieldName() {
		return xAxisFieldName;
	}
	public void setxAxisFieldName(String xAxisFieldName) {
		this.xAxisFieldName = xAxisFieldName;
	}
	public String getyAxisFieldName() {
		return yAxisFieldName;
	}
	public void setyAxisFieldName(String yAxisFieldName) {
		this.yAxisFieldName = yAxisFieldName;
	}
	public String getyAxisLabel() {
		return yAxisLabel;
	}
	public void setyAxisLabel(String yAxisLabel) {
		this.yAxisLabel = yAxisLabel;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
	
	public enum OrderByFunction {
		ACCENDING (1,"ASC"),
		DESCENDING (2,"DESC");
		
		public int getValue() {
			return value;
		}

		public void setValue(int value) {
			this.value = value;
		}

		public String getStringValue() {
			return stringValue;
		}

		public void setStringValue(String stringValue) {
			this.stringValue = stringValue;
		}

		private int value;
		private String stringValue;
		
		OrderByFunction(int value,String stringValue) {
			
			this.value = value;
			this.stringValue = stringValue;
		}
		
		public static OrderByFunction getOrderByFunction(int value) {
			return ORDER_BY_FUNCTION_MAP.get(value);
		}

		private static final Map<Integer, OrderByFunction> ORDER_BY_FUNCTION_MAP = Collections.unmodifiableMap(initTypeMap());
		private static Map<Integer, OrderByFunction> initTypeMap() {
			Map<Integer, OrderByFunction> typeMap = new HashMap<>();
			for(OrderByFunction type : values()) {
				typeMap.put(type.getValue(), type);
			}
			return typeMap;
		}
	}
}
