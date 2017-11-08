package com.facilio.bmsconsole.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioReportContext;
import com.facilio.bmsconsole.commands.ReportsChainFactory;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.opensymphony.xwork2.ActionSupport;

public class WorkOrderReportAction extends ActionSupport {

	private String type;
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	private String period;
	public String getPeriod() {
		if (this.period == null) {
			this.period = "Today";
		}
		return this.period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
	
	private List<Map<String, Object>> reportData;
	public List<Map<String, Object>> getReportData() {
		return this.reportData;
	}
	
	public void setReportData(List<Map<String, Object>> reportData) {
		this.reportData = reportData;
	}

	public String summary() throws Exception {

		if ("open".equalsIgnoreCase(getType())) {
			setReportData(getOpenWorkOrderSummary());
		}
		else if ("closed".equalsIgnoreCase(getType())) {
			setReportData(getClosedWorkOrderSummary(getPeriod()));
		}
		else if ("category".equalsIgnoreCase(getType())) {
			setReportData(getWorkOrderCategorySummary());
		}
		else if ("requests".equalsIgnoreCase(getType())) {
			setReportData(getWorkOrderRequestSummary());
		}
		else if ("location".equalsIgnoreCase(getType())) {
			setReportData(getWorkOrderLocationSummary());
		}
		else if ("technician".equalsIgnoreCase(getType())) {
			setReportData(getTechnicianWorkSummary());
		}
		
		return SUCCESS;
	}
	private List<Map<String, Object>> getTechnicianAvgResponseTime() throws Exception {
		
		FacilioReportContext repContext = new FacilioReportContext();
		JSONParser parser = new JSONParser();
		String xAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.SPACE+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.SPACE+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}]";
		String yAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Reports.ALL_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\"}]";
		String joinsJSON = "[{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.TICKET+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.INNER_JOIN+"\"}]";
		String groupByJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.SPACE+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.SPACE+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}]";
		int topN = 5;
		String topNData = FacilioConstants.Reports.TOP_N+":"+topN+":"+FacilioConstants.Reports.COUNT_COLUMN;
		JSONArray xAxis = (JSONArray) parser.parse(xAxisJSON);
		repContext.setXAxis(xAxis);
		JSONArray yAxis = (JSONArray) parser.parse(yAxisJSON);
		repContext.setYAxis(yAxis);
		JSONArray joins = (JSONArray) parser.parse(joinsJSON);
		repContext.setJoins(joins);
		JSONArray groupByCols = (JSONArray) parser.parse(groupByJSON);
		repContext.setGroupByCols(groupByCols);
		repContext.put(FacilioConstants.Reports.TOP_N_DATA, topNData);
		repContext.setReportType(FacilioConstants.Reports.TOP_N_SUMMARY_REPORT_TYPE);
		repContext.put(FacilioConstants.ContextNames.CV_NAME, "open");
		Chain summaryReportChain = ReportsChainFactory.getWorkOrderReportChain();
 		summaryReportChain.execute(repContext);
 		List<Map<String, Object>> rs = (List<Map<String, Object>>) repContext.get(FacilioConstants.Reports.RESULT_SET);
		
		return rs;
	}

	private List<Map<String, Object>> getOpenWorkOrderSummary() throws Exception {
		
		FacilioReportContext repContext = new FacilioReportContext();
		JSONParser parser = new JSONParser();
		String xAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.STATUS+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.STATUS_ID+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}]";
		String yAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Reports.ALL_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\"}]";
		String joinsJSON = "[{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.TICKET+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.INNER_JOIN+"\"}]";
		String groupByJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.STATUS+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.STATUS_ID+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}]";
		JSONArray xAxis = (JSONArray) parser.parse(xAxisJSON);
		repContext.setXAxis(xAxis);
		JSONArray yAxis = (JSONArray) parser.parse(yAxisJSON);
		repContext.setYAxis(yAxis);
		JSONArray joins = (JSONArray) parser.parse(joinsJSON);
		repContext.setJoins(joins);
		JSONArray groupByCols = (JSONArray) parser.parse(groupByJSON);
		repContext.setGroupByCols(groupByCols);
		repContext.setReportType(FacilioConstants.Reports.SUMMARY_REPORT_TYPE);
		repContext.put(FacilioConstants.ContextNames.CV_NAME, "open");
		Chain summaryReportChain = ReportsChainFactory.getWorkOrderReportChain();
	 	summaryReportChain.execute(repContext);
	 	List<Map<String, Object>> rs = (List<Map<String, Object>>) repContext.get(FacilioConstants.Reports.RESULT_SET);
		
		return rs;
	}
	
	private List<Map<String, Object>> getClosedWorkOrderSummary(String period) throws Exception {
		
		FacilioReportContext repContext = new FacilioReportContext();
		JSONParser parser = new JSONParser();
		String xAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.DUE_STATUS+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.DUE_STATUS+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}]";
		String yAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Reports.ALL_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\"}]";
		String joinsJSON = "[{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.TICKET+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.INNER_JOIN+"\"}]";
		String groupByJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.DUE_STATUS+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.DUE_STATUS+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}]";
		String filters ="{\""+FacilioConstants.Ticket.ACTUAL_WORK_END+"\":[{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\""+getPeriod()+"\"},{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is not empty\"}],\""+FacilioConstants.Ticket.DUE_DATE+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is not empty\"}}";
		JSONArray xAxis = (JSONArray) parser.parse(xAxisJSON);
		repContext.setXAxis(xAxis);
		JSONArray yAxis = (JSONArray) parser.parse(yAxisJSON);
		repContext.setYAxis(yAxis);
		JSONArray joins = (JSONArray) parser.parse(joinsJSON);
		repContext.setJoins(joins);
		JSONArray groupByCols = (JSONArray) parser.parse(groupByJSON);
		repContext.setGroupByCols(groupByCols);
		JSONObject filterObj = (JSONObject) parser.parse(filters);
		repContext.setFilters(filterObj);
		repContext.setReportType(FacilioConstants.Reports.SUMMARY_REPORT_TYPE);
		repContext.put(FacilioConstants.ContextNames.CV_NAME, "closed");
		Chain summaryReportChain = ReportsChainFactory.getWorkOrderReportChain();
 		summaryReportChain.execute(repContext);
 		List<Map<String, Object>> result = (List<Map<String, Object>>) repContext.get(FacilioConstants.Reports.RESULT_SET);
 		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private List<Map<String, Object>> getWorkOrderCategorySummary() throws Exception {

			
			FacilioReportContext repContext = new FacilioReportContext();
			JSONParser parser = new JSONParser();
			String xAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.ContextNames.ID+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\"category_id\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.TICKET_CATEGORY+"\"}]";
			String yAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Reports.ALL_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\"}]";
			String joinsJSON = "[{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.TICKET+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.INNER_JOIN+"\"},{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.TICKET_CATEGORY+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.LEFT_JOIN+"\"}]";
			String groupByJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.ContextNames.ID+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\"category_id\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.TICKET_CATEGORY+"\"}]";
			JSONArray xAxis = (JSONArray) parser.parse(xAxisJSON);
			repContext.setXAxis(xAxis);
			JSONArray yAxis = (JSONArray) parser.parse(yAxisJSON);
			repContext.setYAxis(yAxis);
			JSONArray joins = (JSONArray) parser.parse(joinsJSON);
			repContext.setJoins(joins);
			JSONArray groupByCols = (JSONArray) parser.parse(groupByJSON);
			repContext.setGroupByCols(groupByCols);
			repContext.setReportType(FacilioConstants.Reports.SUMMARY_REPORT_TYPE);
			repContext.put(FacilioConstants.ContextNames.CV_NAME, "open");
			Chain summaryReportChain = ReportsChainFactory.getWorkOrderReportChain();
 	 		summaryReportChain.execute(repContext);
 	 		List<Map<String, Object>> rs = (List<Map<String, Object>>) repContext.get(FacilioConstants.Reports.RESULT_SET);
 	 		
			return rs;
	}
	
	private List<Map<String, Object>> getWorkOrderRequestSummary() throws Exception {

		FacilioReportContext repContext = new FacilioReportContext();
		JSONParser parser = new JSONParser();
		String xAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.WorkOrderRquest.SOURCE_TYPE+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.WorkOrderRquest.SOURCE_TYPE+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER_REQUEST+"\"}]";
		String yAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Reports.ALL_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER_REQUEST+"\",\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\"}]";
		String joinsJSON = "[{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.TICKET+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.INNER_JOIN+"\"}]";
		String groupByJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.WorkOrderRquest.SOURCE_TYPE+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.WorkOrderRquest.SOURCE_TYPE+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER_REQUEST+"\"}]";
		String filters ="{\""+FacilioConstants.WorkOrderRquest.CREATED_TIME+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER_REQUEST+"\",\"operator\":\""+getPeriod()+"\"}}";
		JSONArray xAxis = (JSONArray) parser.parse(xAxisJSON);
		repContext.setXAxis(xAxis);
		JSONArray yAxis = (JSONArray) parser.parse(yAxisJSON);
		repContext.setYAxis(yAxis);
		JSONArray joins = (JSONArray) parser.parse(joinsJSON);
		repContext.setJoins(joins);
		JSONArray groupByCols = (JSONArray) parser.parse(groupByJSON);
		repContext.setGroupByCols(groupByCols);
		JSONObject filterObj = (JSONObject) parser.parse(filters);
		repContext.setFilters(filterObj);
		repContext.setReportType(FacilioConstants.Reports.SUMMARY_REPORT_TYPE);
		repContext.put(FacilioConstants.ContextNames.CV_NAME, "open");
		Chain summaryReportChain = ReportsChainFactory.getWorkOrderRequestReportChain();
 		summaryReportChain.execute(repContext);
 		List<Map<String, Object>> rs = (List<Map<String, Object>>) repContext.get(FacilioConstants.Reports.RESULT_SET);
	 		
		return rs;
	}
	
	private List<Map<String, Object>> getTechnicianWorkSummary() throws Exception {

		
		FacilioReportContext repContext = new FacilioReportContext();
		JSONParser parser = new JSONParser();
		String xAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.ASSIGNED_TO_ID+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\"technician_id\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}]";
		String yAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Reports.ALL_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\"open_workorders\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\"}]";
		String joinsJSON = "[{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.TICKET+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.INNER_JOIN+"\"}]";
		String groupByJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.ASSIGNED_TO_ID+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\"technician_id\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}]";
		JSONArray xAxis = (JSONArray) parser.parse(xAxisJSON);
		repContext.setXAxis(xAxis);
		JSONArray yAxis = (JSONArray) parser.parse(yAxisJSON);
		repContext.setYAxis(yAxis);
		JSONArray joins = (JSONArray) parser.parse(joinsJSON);
		repContext.setJoins(joins);
		JSONArray groupByCols = (JSONArray) parser.parse(groupByJSON);
		repContext.setGroupByCols(groupByCols);
		repContext.setReportType(FacilioConstants.Reports.SUMMARY_REPORT_TYPE);
		repContext.put(FacilioConstants.ContextNames.CV_NAME, "open");
		Chain summaryReportChain = ReportsChainFactory.getWorkOrderReportChain();
	 	summaryReportChain.execute(repContext);
	 	List<Map<String, Object>> rs = (List<Map<String, Object>>) repContext.get(FacilioConstants.Reports.RESULT_SET);
		
		return rs;
	}
	
	private List<Map<String, Object>> getWorkOrderLocationSummary() throws Exception {
		
		FacilioReportContext repContext = new FacilioReportContext();
		JSONParser parser = new JSONParser();
		String xAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.SPACE+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.SPACE+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}]";
		String yAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Reports.ALL_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\"}]";
		String joinsJSON = "[{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.TICKET+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.INNER_JOIN+"\"}]";
		String groupByJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.SPACE+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.SPACE+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}]";
		int topN = 5;
		String topNData = FacilioConstants.Reports.TOP_N+":"+topN+":"+FacilioConstants.Reports.COUNT_COLUMN;
		JSONArray xAxis = (JSONArray) parser.parse(xAxisJSON);
		repContext.setXAxis(xAxis);
		JSONArray yAxis = (JSONArray) parser.parse(yAxisJSON);
		repContext.setYAxis(yAxis);
		JSONArray joins = (JSONArray) parser.parse(joinsJSON);
		repContext.setJoins(joins);
		JSONArray groupByCols = (JSONArray) parser.parse(groupByJSON);
		repContext.setGroupByCols(groupByCols);
		repContext.put(FacilioConstants.Reports.TOP_N_DATA, topNData);
		repContext.setReportType(FacilioConstants.Reports.TOP_N_SUMMARY_REPORT_TYPE);
		repContext.put(FacilioConstants.ContextNames.CV_NAME, "open");
		Chain summaryReportChain = ReportsChainFactory.getWorkOrderReportChain();
 		summaryReportChain.execute(repContext);
 		List<Map<String, Object>> rs = (List<Map<String, Object>>) repContext.get(FacilioConstants.Reports.RESULT_SET);
		
		return rs;
	}
}