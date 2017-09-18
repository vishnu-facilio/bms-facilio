package com.facilio.bmsconsole.actions;

import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.context.ReportContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.Reports.Energy;
import com.opensymphony.xwork2.ActionSupport;

public class ReportActions extends ActionSupport {
	
	public String reportdata() throws Exception 
	{
		String strDurat = String.valueOf(report.get("duration"));
		int duration = Integer.valueOf(strDurat);
		String strEngery = String.valueOf(report.get("energydata"));
		int energyData =Integer.valueOf(strEngery);
		//int deviceId =Integer.valueOf(strDevice);
		//System.out.println(duration);
		//System.out.println(data);
		JSONObject reportDatas=ReportsUtil.getEnergyData(energyData,duration);
		setReportData(reportDatas);
		return SUCCESS;
	}
	private JSONObject reportData = null;
	
	public JSONObject getReportData() {
		return reportData;
	}
	public void setReportData(JSONObject reportData) {
		this.reportData = reportData;		
	}
	public void setConstantEnergy() {
		
	}
	private JSONObject report;
	public  JSONObject getParamsdata() {
		return report;
	}
	public void setParamsdata(JSONObject report) {
		System.out.println("setParamsdata"+report);
		this.report = report;
	}
	private String viewName = null;
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	private int queryReportfilter;
	public int getQueryReportfilter() {
		return queryReportfilter;
	}
	public void setQueryReportfilter(int filterId) {
		this.queryReportfilter = filterId;
	}
	private int queryDatafilter;
	public int getQueryDatafilter() {
		return queryDatafilter;
	}
	public void setQueryDatafilter(int dataId) {
		this.queryDatafilter = dataId;
	}
}