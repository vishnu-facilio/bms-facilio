package com.facilio.bmsconsole.commands;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class FacilioReportContext extends FacilioContext{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String reportType;
	private String moduleName;
	private JSONArray joins;
	private JSONArray xAxis;
	private JSONArray yAxis;
	private JSONArray groupByCols;
	private String groupBy;
	private JSONArray orderByCols;
	private String orderBy;
	private String orderType;
	private int limit = 0;
	private JSONObject filters;
	public String getReportType() {
		return reportType;
	}
	public void setReportType(String repType) {
		this.reportType = repType;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String modName) {
		this.moduleName = modName;
	}
	public JSONArray getJoins() {
		return joins;
	}
	public void setJoins(JSONArray joins) {
		this.joins = joins;
	}
	public JSONArray getXAxis() {
		return xAxis;
	}
	public void setXAxis(JSONArray xAxis) {
		this.xAxis = xAxis;
	}
	public JSONArray getYAxis() {
		return yAxis;
	}
	public void setYAxis(JSONArray yAxis) {
		this.yAxis = yAxis;
	}
	public JSONArray getGroupByCols() {
		return groupByCols;
	}
	public void setGroupByCols(JSONArray groupByCols) {
		this.groupByCols = groupByCols;
	}
	public String getGroupBy() {
		return groupBy;
	}
	public void setGroupBy(String groupBy) {
		this.groupBy = groupBy;
	}
	public JSONArray getOrderByCols() {
		return orderByCols;
	}
	public void setOrderByCols(JSONArray orderByCols) {
		this.orderByCols = orderByCols;
	}
	public String getOrderBy() {
		return orderBy;
	}
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	public String getOrderType() {
		return orderType;
	}
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public JSONObject getFilters() {
		return filters;
	}
	public void setFilters(JSONObject filters) {
		this.filters = filters;
	}
}
