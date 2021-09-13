package com.facilio.report.context;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;

public class ReportPivotParamsContext {
    private List<ReportPivotTableRowsContext> rows = new ArrayList<ReportPivotTableRowsContext>();
	public List<ReportPivotTableRowsContext> getRows() {
		return rows;
	}
	public void setRows(List<ReportPivotTableRowsContext> rows) {
		this.rows = rows;
	}
	
	private List<ReportPivotTableDataContext> data = new ArrayList<ReportPivotTableDataContext>();
	public List<ReportPivotTableDataContext> getData() {
		return data;
	}
	public void setData(List<ReportPivotTableDataContext> data) {
		this.data = data;
	}
	
	private JSONObject sortBy;
	public JSONObject getSortBy() {
		return sortBy;
	}
	public void setSortBy(JSONObject sortBy) {
		this.sortBy = sortBy;
	}

	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

    private Criteria criteria;
	public Criteria getCriteria() {
		return criteria;
	}
	public void setCriteria(Criteria criteria) {
		this.criteria = criteria;
	}
	
	private JSONObject templateJSON;

	public JSONObject getTemplateJSON() {
		return templateJSON;
	}
	public void setTemplateJSON(JSONObject templateJSON) {
		this.templateJSON = templateJSON;
	}
	private long dateFieldId = -1;
	public long getDateFieldId() {
		return dateFieldId;
	}
	public void setDateFieldId(long dateFieldId) {
		this.dateFieldId = dateFieldId;
	}
	
	Integer dateOperator;
	public Integer getDateOperator() {
		return dateOperator;
	}
	public void setDateOperator(Integer dateOperator) {
		this.dateOperator = dateOperator;
	}
	
	String dateValue;
	public String getDateValue() {
		return dateValue;
	}
	public void setDateValue(String dateValue) {
		this.dateValue = dateValue;
	}
	
	private Boolean showTimelineFilter;
	public Boolean getShowTimelineFilter() {
		return showTimelineFilter;
	}
	public void setShowTimelineFilter(boolean showTimelineFilter) {
		this.showTimelineFilter = showTimelineFilter;
	}
	
	private long startTime = -1;
	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	private long endTime = -1;
	public long getEndTime() {
		return endTime;
	}
	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
}