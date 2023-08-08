package com.facilio.report.context;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONObject;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;

public class ReportPivotParamsContext {
	private List<PivotRowColumnContext> rows = new ArrayList<PivotRowColumnContext>();

	public List<PivotRowColumnContext> getRows() {
		return rows;
	}

	public void setRows(List<PivotRowColumnContext> rows) {
		this.rows = rows;
	}

	private List<PivotDataColumnContext> data = new ArrayList<PivotDataColumnContext>();

	public List<PivotDataColumnContext> getData() {
		return data;
	}

	public void setData(List<PivotDataColumnContext> data) {
		this.data = data;
	}

	private List<PivotFormulaColumnContext> formula = new ArrayList<>();

	public List<PivotFormulaColumnContext> getFormula() {
		return formula;
	}

	public void setFormula(List<PivotFormulaColumnContext> formula) {
		this.formula = formula;
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

	public void setShowTimelineFilter(Boolean showTimelineFilter) {
		this.showTimelineFilter = showTimelineFilter;
	}

	private long drillDown = 0;

	public long getDrillDown() {
		return drillDown;
	}

	public void setDrillDown(long drillDown) {
		this.drillDown = drillDown;
	}

	public boolean isBuilderV2() {
		return isBuilderV2;
	}

	public void setBuilderV2(boolean builderV2) {
		isBuilderV2 = builderV2;
	}

	private boolean isBuilderV2 = false;

	public List<PivotValueColumnContext> getValues() {
		return values;
	}

	public void setValues(List<PivotValueColumnContext> values) {
		this.values = values;
	}

	private List<PivotValueColumnContext> values = new ArrayList<>();

	public Integer getDateOffset() {
		return dateOffset;
	}

	public void setDateOffset(Integer dateOffset) {
		this.dateOffset = dateOffset;
	}

	public  Integer dateOffset;
}