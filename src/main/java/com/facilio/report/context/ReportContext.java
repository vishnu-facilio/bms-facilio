package com.facilio.report.context;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.modules.FieldUtil;

public class ReportContext {

	private JSONParser parser = new JSONParser();
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
	
	private String description;
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	private long reportFolderId = -1;
	public long getReportFolderId() {
		return reportFolderId;
	}
	public void setReportFolderId(long reportFolderId) {
		this.reportFolderId = reportFolderId;
	}
	
	private long workflowId = -1;
	public long getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}
	
	private String tabularState;
	public String getTabularState() {
		return tabularState;
	}
	public void setTabularState(String tabularState) {
		this.tabularState = tabularState;
	}
	
	private String chartState;
	public String getChartState() {
		return chartState;
	}
	public void setChartState(String chartState) {
		this.chartState = chartState;
	}
	
	private ReportDateFilterContext dateFilter;
	
	public ReportDateFilterContext getDateFilter() {
		return dateFilter;
	}
	public void setDateFilter(ReportDateFilterContext dateFilter) {
		this.dateFilter = dateFilter;
	}
	public void setDateFilterJson(String dateRange) throws Exception {
		
		JSONObject json = (JSONObject) parser.parse(dateRange);
		this.dateFilter = FieldUtil.getAsBeanFromJson(json, ReportDateFilterContext.class);
	}
	public String getDateFilterJson() throws Exception {
		
		if (dateFilter != null) {
			return FieldUtil.getAsJSON(dateFilter).toJSONString();
		}
		return null;
	}
	
	
	private List<ReportDataPointContext> dataPoints;
	public List<ReportDataPointContext> getDataPoints() {
		return dataPoints;
	}
	public void setDataPoints(List<ReportDataPointContext> dataPoints) {
		this.dataPoints = dataPoints;
	}
	
	public void addDataPoint(ReportDataPointContext dataPoint) {
		
		this.dataPoints = this.dataPoints == null ? new ArrayList<>() : this.dataPoints;
		this.dataPoints.add(dataPoint);
	}
	
	public void setDataPointJson(String dataPointJson) throws Exception {
		
		JSONArray jsonarray = (JSONArray) parser.parse(dataPointJson);
		
		for( Object jsonObject :jsonarray) {
			
			JSONObject json = (JSONObject) jsonObject;
			ReportDataPointContext dataPoint = FieldUtil.getAsBeanFromJson(json, ReportDataPointContext.class);
			
			addDataPoint(dataPoint);
		}
	}
	
	public String getDataPointJson() throws Exception {
		
		if (dataPoints != null) {
			return FieldUtil.getAsJSONArray(dataPoints, ReportDataPointContext.class).toJSONString();
		}
		return null;
	}
	
	private List<ReportBaseLineContext> baseLines;
	
	public List<ReportBaseLineContext> getBaseLines() {
		return baseLines;
	}
	public void setBaseLines(List<ReportBaseLineContext> baseLines) {
		this.baseLines = baseLines;
	}

	public void addBaseLines(ReportBaseLineContext baseLine) {
		
		this.baseLines = this.baseLines == null ? new ArrayList<>() : this.baseLines;
		baseLines.add(baseLine);
	}
	public void setBaselineJson(String baselineJson) throws Exception {
		
		JSONArray jsonarray = (JSONArray) parser.parse(baselineJson);
		
		for( Object jsonObject :jsonarray) {
			
			JSONObject json = (JSONObject) jsonObject;
			ReportBaseLineContext dataPoint = FieldUtil.getAsBeanFromJson(json, ReportBaseLineContext.class);
			
			addBaseLines(dataPoint);
		}
	}
	
	public String getBaselineJson() throws Exception {
		
		if (baseLines != null) {
			return FieldUtil.getAsJSONArray(baseLines, ReportBaseLineContext.class).toJSONString();
		}
		return null;
	}
	
	private ReportXCriteriaContext xCriteria;
	public ReportXCriteriaContext getxCriteria() {
		return xCriteria;
	}
	public void setxCriteria(ReportXCriteriaContext xCriteria) {
		this.xCriteria = xCriteria;
	}

	public void setXCriteriaJson(String xCriteriaJson) throws Exception {
		
		JSONObject json = (JSONObject) parser.parse(xCriteriaJson);
		this.xCriteria = FieldUtil.getAsBeanFromJson(json, ReportXCriteriaContext.class);
	}
	
	public String getXCriteriaJson() throws Exception {
		
		if (xCriteria != null) {
			return FieldUtil.getAsJSON(xCriteria).toJSONString();
		}
		return null;
	}
}
