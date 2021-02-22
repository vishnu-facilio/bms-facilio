package com.facilio.report.context;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

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
}