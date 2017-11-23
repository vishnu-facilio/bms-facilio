package com.facilio.bmsconsole.actions;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.FacilioReportContext;
import com.facilio.bmsconsole.commands.ReportsChainFactory;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.TicketContext;
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
import com.facilio.fw.UserInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;
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
	private JSONArray woStats;
	public JSONArray getWoStats() {
		return this.woStats;
	}
	
	public void setWoStats(JSONArray woStats) {
		this.woStats = woStats;
	}

	public String summary() throws Exception {
		String technicianGroupBy = "{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.ASSIGNED_TO_ID+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.ASSIGNED_TO_ID+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}";
		String technicianNotNull = "\""+FacilioConstants.Ticket.ASSIGNED_TO_ID+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is not empty\"}";
		if ("open".equalsIgnoreCase(getType())) {
			setReportData(getOpenWorkOrderSummary(null));
		}
		else if ("closed".equalsIgnoreCase(getType())) {
			setReportData(getClosedWorkOrderSummary(null));
		}
		else if ("category".equalsIgnoreCase(getType())) {
			setReportData(getWorkOrderCategorySummary());
		}
		else if ("requests".equalsIgnoreCase(getType())) {
			setReportData(getWorkOrderRequestSummary());
		}
		else if ("technician".equalsIgnoreCase(getType())) {
			setReportData(getTechnicianWorkSummary());
		}
		else if("technicianEfficiency".equalsIgnoreCase(getType())) {
			setReportData(getAvgResolutionTime(technicianNotNull,technicianGroupBy));
		}
		else if("inflowTrend".equalsIgnoreCase(getType())) {
			setReportData(getWoInflowTrend());
		}
		else if("countSummary".equalsIgnoreCase(getType())) {
			setReportData(getCountSummary(null));
		}
		return SUCCESS;
	}
	public String preventiveMaintenance() throws Exception {
		String preventiveCdn ="\""+FacilioConstants.Ticket.SOURCE_TYPE+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"=\",\"value\":[\""+TicketContext.SourceType.PREVENTIVE_MAINTENANCE.getIntVal()+"_\"]}";
		String assetGroupBy = "{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.ASSET_ID+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.ASSET_ID+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}";
		String assetNotNullFilter = "\""+FacilioConstants.Ticket.ASSET_ID+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is not empty\"}";
		String spaceGroupBy = "{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.SPACE+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.SPACE+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}";
		String spaceNotNullFilter = "\""+FacilioConstants.Ticket.SPACE+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is not empty\"}";
		if ("open".equalsIgnoreCase(getType())) {
			setReportData(getOpenWorkOrderSummary(preventiveCdn));
		}
		else if ("closed".equalsIgnoreCase(getType())) {
			setReportData(getClosedWorkOrderSummary(preventiveCdn));
		}
		else if ("woVolume".equalsIgnoreCase(getType())) {
			setReportData(getWorkOrderCategorySummary());
		}
		else if ("location".equalsIgnoreCase(getType())) {
			setReportData(getWorkOrderLocationSummary());
		}
		else if ("assets".equalsIgnoreCase(getType())) {
			setReportData(getWorkOrderAssetSummary());
		}
		else if ("tasks".equalsIgnoreCase(getType())) {
			setReportData(getWorkOrderPreventiveSummary());
		}
		else if("assetEfficiency".equalsIgnoreCase(getType())) {
			setReportData(getAvgResolutionTime(assetNotNullFilter,assetGroupBy));
		}
		else if("spaceEfficiency".equalsIgnoreCase(getType())) {
			setReportData(getAvgResolutionTime(spaceNotNullFilter,spaceGroupBy));
		}
		else if("countSummary".equalsIgnoreCase(getType())) {
			setReportData(getCountSummary(preventiveCdn));
		}
		return SUCCESS;
	}
	public String technician() throws Exception {
		String userFilter ="\""+FacilioConstants.Ticket.ASSIGNED_TO_ID+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is\",\"value\":[\""+UserInfo.getCurrentUser().getOrgUserId()+"_\"]}";
		String technicianDailyGroupBy = "{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.ACTUAL_WORK_END+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.ACTUAL_WORK_END+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",,\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.DAILY+"\"},{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.ASSIGNED_TO_ID+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.ASSIGNED_TO_ID+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}";
		if ("priority".equalsIgnoreCase(getType())) {
			setReportData(getMyWOPrioritySummary(userFilter));
		}
		else if("efficiency".equalsIgnoreCase(getType())) {
			setReportData(getAvgResolutionTime(userFilter,technicianDailyGroupBy));
		}
		else if("countSummary".equalsIgnoreCase(getType())) {
			setReportData(getCountSummary(userFilter));
		}
		else if ("closed".equalsIgnoreCase(getType())) {
			setReportData(getClosedWorkOrderSummary(userFilter));
		}
		return SUCCESS;
	}
	@SuppressWarnings("unchecked")
	public String map() throws Exception {
		JSONArray woStats = new JSONArray();
		FacilioContext context = new FacilioContext();
		Chain buildChain = FacilioChainFactory.getAllBuildingChain();
		buildChain.execute(context);
		List<BuildingContext> buildings = (List<BuildingContext>) context.get(FacilioConstants.ContextNames.BUILDING_LIST);
		if(buildings != null && !buildings.isEmpty()) {
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
				for(BuildingContext building : buildings) {
					JSONObject buildingObj = new JSONObject();
					buildingObj.put("id", building.getId());
					buildingObj.put("name", building.getName());
					buildingObj.put("avatarUrl", building.getAvatarUrl());
					
					if(building.getLocation() != null) {
						JSONObject location = new JSONObject();
						location.put("lat", building.getLocation().getLat());
						location.put("lng", building.getLocation().getLng());
						buildingObj.put("location", location);
					}
					List<BaseSpaceContext> allSpaces = SpaceAPI.getBaseSpaceWithChildren(building.getId());
					buildingObj.put("stats", getOpenWorkOrderSummary(null));
					woStats.add(buildingObj);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
		setWoStats(woStats);
		return SUCCESS;
	}
	
	private List<Map<String, Object>> getMyWOPrioritySummary(String filterBy) throws Exception {
		
		FacilioReportContext repContext = new FacilioReportContext();
		JSONParser parser = new JSONParser();
		String xAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.PRIORITY+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.PRIORITY+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}]";
		String yAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Reports.ALL_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\"}]";
		String joinsJSON = "[{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.TICKET+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.INNER_JOIN+"\"}]";
		String filters = "{"+filterBy+"}";
		JSONArray xAxis = (JSONArray) parser.parse(xAxisJSON);
		repContext.setXAxis(xAxis);
		JSONArray yAxis = (JSONArray) parser.parse(yAxisJSON);
		repContext.setYAxis(yAxis);
		JSONArray joins = (JSONArray) parser.parse(joinsJSON);
		repContext.setJoins(joins);
		JSONArray groupByCols = (JSONArray) parser.parse(xAxisJSON);
		repContext.setGroupByCols(groupByCols);
		JSONObject filterObj = (JSONObject) parser.parse(filters);
		repContext.setFilters(filterObj);
		repContext.setReportType(FacilioConstants.Reports.SUMMARY_REPORT_TYPE);
		repContext.put(FacilioConstants.ContextNames.CV_NAME, "open");
		Chain summaryReportChain = ReportsChainFactory.getWorkOrderReportChain();
	 	summaryReportChain.execute(repContext);
	 	List<Map<String, Object>> rs = (List<Map<String, Object>>) repContext.get(FacilioConstants.Reports.RESULT_SET);
		
		return rs;
	}
	
	private Map<String, Object> getDueTodayCount(String filterBy) throws Exception {
		
		FacilioReportContext repContext = new FacilioReportContext();
		JSONParser parser = new JSONParser();
		String xAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Reports.ALL_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\"dueToday\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\"}]";
		String joinsJSON = "[{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.TICKET+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.INNER_JOIN+"\"}]";
		String filters = "";
		if(filterBy!=null && !filterBy.isEmpty()) {
			filters ="{"+filterBy+",\""+FacilioConstants.Ticket.DUE_DATE+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"Today\"}}";
		}
		else {
			filters ="{\""+FacilioConstants.Ticket.DUE_DATE+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"Today\"}}";
			}
		JSONArray xAxis = (JSONArray) parser.parse(xAxisJSON);
		repContext.setXAxis(xAxis);
		JSONArray joins = (JSONArray) parser.parse(joinsJSON);
		repContext.setJoins(joins);
		JSONObject filterObj = (JSONObject) parser.parse(filters);
		repContext.setFilters(filterObj);
		repContext.setReportType(FacilioConstants.Reports.NUMERIC_REPORT_TYPE);
		repContext.put(FacilioConstants.ContextNames.CV_NAME, "open");
		Chain summaryReportChain = ReportsChainFactory.getWorkOrderReportChain();
 		summaryReportChain.execute(repContext);
 		Map<String, Object> rs = (Map<String, Object>) repContext.get(FacilioConstants.Reports.RESULT_SET);
		
		return rs;
	}
	
	private Map<String, Object> getOverDueCount(String filterBy) throws Exception {
		
		FacilioReportContext repContext = new FacilioReportContext();
		JSONParser parser = new JSONParser();
		String xAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Reports.ALL_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\"Overdue\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\"}]";
		String joinsJSON = "[{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.TICKET+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.INNER_JOIN+"\"}]";
		Long nowTime = DateTimeUtil.getCurrenTime();
		String filters = "";
		if(filterBy!=null && !filterBy.isEmpty()) {
			filters ="{"+filterBy+",\""+FacilioConstants.Ticket.DUE_DATE+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is before\",\"value\":[\""+nowTime+"_\"]}}";
		}
		else {
			filters ="{\""+FacilioConstants.Ticket.DUE_DATE+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is before\",\"value\":[\""+nowTime+"_\"]}}";
		}
		JSONArray xAxis = (JSONArray) parser.parse(xAxisJSON);
		repContext.setXAxis(xAxis);
		JSONArray joins = (JSONArray) parser.parse(joinsJSON);
		repContext.setJoins(joins);
		JSONObject filterObj = (JSONObject) parser.parse(filters);
		repContext.setFilters(filterObj);
		repContext.setReportType(FacilioConstants.Reports.NUMERIC_REPORT_TYPE);
		repContext.put(FacilioConstants.ContextNames.CV_NAME, "open");
		Chain summaryReportChain = ReportsChainFactory.getWorkOrderReportChain();
 		summaryReportChain.execute(repContext);
 		Map<String, Object> rs = (Map<String, Object>) repContext.get(FacilioConstants.Reports.RESULT_SET);
		
		return rs;
	}
	
	private Map<String, Object> getUnassignedCount() throws Exception {
		
		FacilioReportContext repContext = new FacilioReportContext();
		JSONParser parser = new JSONParser();
		String xAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Reports.ALL_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\"unassigned\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\"}]";
		String joinsJSON = "[{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.TICKET+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.INNER_JOIN+"\"}]";
		Long nowTime = DateTimeUtil.getCurrenTime();
		String filters ="{\""+FacilioConstants.Ticket.ASSIGNED_TO_ID+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is empty\"}}";
		JSONArray xAxis = (JSONArray) parser.parse(xAxisJSON);
		repContext.setXAxis(xAxis);
		JSONArray joins = (JSONArray) parser.parse(joinsJSON);
		repContext.setJoins(joins);
		JSONObject filterObj = (JSONObject) parser.parse(filters);
		repContext.setFilters(filterObj);
		repContext.setReportType(FacilioConstants.Reports.NUMERIC_REPORT_TYPE);
		repContext.put(FacilioConstants.ContextNames.CV_NAME, "open");
		Chain summaryReportChain = ReportsChainFactory.getWorkOrderReportChain();
 		summaryReportChain.execute(repContext);
 		Map<String, Object> rs = (Map<String, Object>) repContext.get(FacilioConstants.Reports.RESULT_SET);
		
		return rs;
	}
	
	private List<Map<String, Object>> getCountSummary(String filterBy) throws Exception {
 		List<Map<String, Object>> rs = new ArrayList<>();
 		rs.add(getDueTodayCount(filterBy));
 		rs.add(getOverDueCount(filterBy));
 		rs.add(getUnassignedCount());
 		List<Map<String, Object>> openWoResult = getOpenWorkOrderSummary(filterBy);
 		Map<String,Object> openWO = new HashMap<>();
 		openWO.put(FacilioConstants.Reports.LABEL, "open");
 		openWO.put(FacilioConstants.Reports.VALUE, openWoResult);
 		rs.add(openWO);
		return rs;
	}
	
	private List<Map<String, Object>> getWoInflowTrend() throws Exception {
		
		FacilioReportContext repContext = new FacilioReportContext();
		JSONParser parser = new JSONParser();
		String xAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.CREATED_TIME+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.CREATED_TIME+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",,\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.DAILY+"\"}]";
		String yAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Reports.ALL_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\"}]";
		String joinsJSON = "[{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.TICKET+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.INNER_JOIN+"\"}]";
		String filters ="{\""+FacilioConstants.Ticket.CREATED_TIME+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\""+getPeriod()+"\"}}";
		JSONArray xAxis = (JSONArray) parser.parse(xAxisJSON);
		repContext.setXAxis(xAxis);
		JSONArray yAxis = (JSONArray) parser.parse(yAxisJSON);
		repContext.setYAxis(yAxis);
		JSONArray joins = (JSONArray) parser.parse(joinsJSON);
		repContext.setJoins(joins);
		JSONArray groupByCols = (JSONArray) parser.parse(xAxisJSON);
		repContext.setGroupByCols(groupByCols);
		JSONObject filterObj = (JSONObject) parser.parse(filters);
		repContext.setFilters(filterObj);
		repContext.setReportType(FacilioConstants.Reports.SUMMARY_REPORT_TYPE);
		Chain summaryReportChain = ReportsChainFactory.getWorkOrderReportChain();
 		summaryReportChain.execute(repContext);
 		List<Map<String, Object>> rs = (List<Map<String, Object>>) repContext.get(FacilioConstants.Reports.RESULT_SET);
		
		return rs;
	}
	
	private List<Map<String, Object>> getAvgResolutionTime(String filterBy, String groupBy) throws Exception {
		
		FacilioReportContext repContext = new FacilioReportContext();
		JSONParser parser = new JSONParser();
		String yAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.RESOLUTION_TIME+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.RESOLUTION_TIME+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.AVG_FUNC+"\"}]";
		String joinsJSON = "[{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.TICKET+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.INNER_JOIN+"\"}]";
		String filters = "";
		if(filterBy!=null && !filterBy.isEmpty()) {
			filters ="{"+filterBy+",\""+FacilioConstants.Ticket.ACTUAL_WORK_END+"\":[{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\""+getPeriod()+"\"},{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is not empty\"}],\""+FacilioConstants.Ticket.ACTUAL_WORK_START+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is not empty\"}}";
		}
		else {
			filters ="{\""+FacilioConstants.Ticket.ACTUAL_WORK_END+"\":[{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\""+getPeriod()+"\"},{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is not empty\"}],\""+FacilioConstants.Ticket.ACTUAL_WORK_START+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is not empty\"}}";
		}
		if(groupBy!=null && !groupBy.isEmpty()) {
			String groupByJSON = "["+ groupBy +"]";
			String xAxisJSON = "["+ groupBy +"]";
			JSONArray groupByCols = (JSONArray) parser.parse(groupByJSON);
			repContext.setGroupByCols(groupByCols);
			JSONArray xAxis = (JSONArray) parser.parse(xAxisJSON);
			repContext.setXAxis(xAxis);
		}
		JSONArray yAxis = (JSONArray) parser.parse(yAxisJSON);
		repContext.setYAxis(yAxis);
		JSONArray joins = (JSONArray) parser.parse(joinsJSON);
		repContext.setJoins(joins);
		JSONObject filterObj = (JSONObject) parser.parse(filters);
		repContext.setFilters(filterObj);
		repContext.setReportType(FacilioConstants.Reports.SUMMARY_REPORT_TYPE);
		repContext.put(FacilioConstants.ContextNames.CV_NAME, "closed");
		Chain summaryReportChain = ReportsChainFactory.getWorkOrderReportChain();
 		summaryReportChain.execute(repContext);
 		List<Map<String, Object>> rs = (List<Map<String, Object>>) repContext.get(FacilioConstants.Reports.RESULT_SET);
		
		return rs;
	}

	private List<Map<String, Object>> getOpenWorkOrderSummary(String filterBy) throws Exception {
		
		FacilioReportContext repContext = new FacilioReportContext();
		JSONParser parser = new JSONParser();
		String xAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.STATUS+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.STATUS_ID+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}]";
		String yAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Reports.ALL_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\"}]";
		String joinsJSON = "[{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.TICKET+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.INNER_JOIN+"\"}]";
		String groupByJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.STATUS+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.STATUS_ID+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}]";
		if(filterBy!=null && !filterBy.isEmpty()) {
			String filters ="{"+filterBy+"}";
			JSONObject filterObj = (JSONObject) parser.parse(filters.toString());
			repContext.setFilters(filterObj);
		}
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
	
	private List<Map<String, Object>> getClosedWorkOrderSummary(String filterBy) throws Exception {
		FacilioReportContext repContext = new FacilioReportContext();
		JSONParser parser = new JSONParser();
		String xAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.ACTUAL_WORK_END+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.ACTUAL_WORK_END+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",,\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.DAILY+"\"}]";
		String yAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Reports.ALL_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\"},{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.DUE_STATUS+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.DUE_STATUS+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}]";
		String joinsJSON = "[{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.TICKET+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.INNER_JOIN+"\"}]";
		String groupByJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.DUE_STATUS+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.DUE_STATUS+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"},{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.ACTUAL_WORK_END+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.ACTUAL_WORK_END+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",,\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.DAILY+"\"}]";
		String filters = "";
		if(filterBy!=null && !filterBy.isEmpty()) {
			filters ="{"+filterBy+",\""+FacilioConstants.Ticket.ACTUAL_WORK_END+"\":[{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\""+getPeriod()+"\"},{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is not empty\"}],\""+FacilioConstants.Ticket.DUE_DATE+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is not empty\"}}";
		}
		else {
			filters ="{\""+FacilioConstants.Ticket.ACTUAL_WORK_END+"\":[{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\""+getPeriod()+"\"},{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is not empty\"}],\""+FacilioConstants.Ticket.DUE_DATE+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is not empty\"}}";	
		}
		JSONArray xAxis = (JSONArray) parser.parse(xAxisJSON);
		repContext.setXAxis(xAxis);
		JSONArray yAxis = (JSONArray) parser.parse(yAxisJSON);
		repContext.setYAxis(yAxis);
		JSONArray joins = (JSONArray) parser.parse(joinsJSON);
		repContext.setJoins(joins);
		JSONArray groupByCols = (JSONArray) parser.parse(groupByJSON);
		repContext.setGroupByCols(groupByCols);
		JSONObject filterObj = (JSONObject) parser.parse(filters.toString());
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
		repContext.setReportType(FacilioConstants.Reports.TOP_N_TABULAR_REPORT_TYPE);
		repContext.put(FacilioConstants.ContextNames.CV_NAME, "open");
		Chain summaryReportChain = ReportsChainFactory.getWorkOrderReportChain();
 		summaryReportChain.execute(repContext);
 		List<Map<String, Object>> rs = (List<Map<String, Object>>) repContext.get(FacilioConstants.Reports.RESULT_SET);
		
		return rs;
	}
	
	private List<Map<String, Object>> getWorkOrderAssetSummary() throws Exception {
		
		FacilioReportContext repContext = new FacilioReportContext();
		JSONParser parser = new JSONParser();
		String xAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.ASSET_ID+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.ASSET_ID+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}]";
		String yAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Reports.ALL_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\"}]";
		String joinsJSON = "[{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.TICKET+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.INNER_JOIN+"\"}]";
		int topN = 5;
		String topNData = FacilioConstants.Reports.TOP_N+":"+topN+":"+FacilioConstants.Reports.COUNT_COLUMN;
		JSONArray xAxis = (JSONArray) parser.parse(xAxisJSON);
		repContext.setXAxis(xAxis);
		JSONArray yAxis = (JSONArray) parser.parse(yAxisJSON);
		repContext.setYAxis(yAxis);
		JSONArray joins = (JSONArray) parser.parse(joinsJSON);
		repContext.setJoins(joins);
		repContext.setGroupByCols(xAxis);
		repContext.put(FacilioConstants.Reports.TOP_N_DATA, topNData);
		repContext.setReportType(FacilioConstants.Reports.TOP_N_SUMMARY_REPORT_TYPE);
		repContext.put(FacilioConstants.ContextNames.CV_NAME, "open");
		Chain summaryReportChain = ReportsChainFactory.getWorkOrderReportChain();
 		summaryReportChain.execute(repContext);
 		List<Map<String, Object>> rs = (List<Map<String, Object>>) repContext.get(FacilioConstants.Reports.RESULT_SET);
		
		return rs;
	}
	
	private List<Map<String, Object>> getWorkOrderPreventiveSummary() throws Exception {
		
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
		repContext.setReportType(FacilioConstants.Reports.TOP_N_TABULAR_REPORT_TYPE);
		repContext.put(FacilioConstants.ContextNames.CV_NAME, "open");
		Chain summaryReportChain = ReportsChainFactory.getWorkOrderReportChain();
 		summaryReportChain.execute(repContext);
 		List<Map<String, Object>> rs = (List<Map<String, Object>>) repContext.get(FacilioConstants.Reports.RESULT_SET);
		
		return rs;
	}
}