package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.*;
import com.facilio.bmsconsole.context.*;
import com.facilio.modules.FacilioStatus;
import com.facilio.modules.FacilioStatus.StatusType;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.Operator;
import com.facilio.bmsconsole.modules.*;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.modules.*;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.chain.Chain;
import org.apache.commons.chain.impl.ChainBase;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.sql.Connection;
import java.util.*;

public class WorkOrderReportAction extends ActionSupport {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static Logger log = LogManager.getLogger(WorkOrderReportAction.class.getName());

    private String type;
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	private Boolean exportData;
	public boolean getExportData() {
		return this.exportData;
	}

	public void setExportData(boolean exportData) {
		this.exportData = exportData;
	}
	
	private String exportType;
	public String getExportType() {
		return this.exportType;
	}

	public void setExportType(String exportType) {
		this.exportType = exportType;
	}
	
	private String exportUrl;
	public String getExportUrl() {
		return this.exportUrl;
	}

	public void setExportUrl(String exportUrl) {
		this.exportUrl = exportUrl;
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
	private List<PreventiveMaintenance> pms;
	public List<PreventiveMaintenance> getPms() {
		return pms;
	}
	public void setPms(List<PreventiveMaintenance> pms) {
		this.pms = pms;
	}
	private JSONArray allWorkOrderJsonReports;
	
	public JSONArray getAllWorkOrderJsonReports() {
		return allWorkOrderJsonReports;
	}

	public void setAllWorkOrderJsonReports(JSONArray allWorkOrderJsonReports) {
		this.allWorkOrderJsonReports = allWorkOrderJsonReports;
	}

	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	
	public String getAllWorkOrderReports() throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		
		// calling this method to create default report folder if not already exists
		DashboardUtil.getDefaultReportFolder(moduleName);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getReportFolderFields())
				.table(ModuleFactory.getReportFolder().getTableName())
//				.innerJoin(ModuleFactory.getReport().getTableName())
//				.on(ModuleFactory.getReportFolder().getTableName()+".ID = "+ModuleFactory.getReport().getTableName()+".REPORT_FOLDER_ID")
				.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.andCustomWhere("MODULEID = ?", module.getModuleId());
			
		
		List<Map<String, Object>> props = selectBuilder.get();
		
		List<ReportFolderContext> reportFolders = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop:props) {
				ReportFolderContext reportFolder = FieldUtil.getAsBeanFromMap(prop, ReportFolderContext.class);
				List<ReportContext> reports = DashboardUtil.getReportsFormReportFolderId(reportFolder.getId());
				reportFolder.setReports(reports);
				reportFolders.add(reportFolder);
			}
		}
		
		setAllWorkOrderJsonReports(DashboardUtil.getReportResponseJson(reportFolders));
	
		return SUCCESS;
	}
	
	public String summary() throws Exception {
		String technicianGroupBy = "{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.ASSIGNED_TO_ID+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.ASSIGNED_TO_ID+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}";
		String technicianNotNull = "\""+FacilioConstants.Ticket.ASSIGNED_TO_ID+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is not empty\"}";
		if ("open".equalsIgnoreCase(getType())) {
			if(getExportData())
			{
				getOpenWorkOrderSummary(null);
				FacilioModule module = new FacilioModule();
				module.setName("openworkorders");
				module.setDisplayName("Open Workorders");
				
				FacilioField field1 = new FacilioField();
				field1.setName("label");
				field1.setDisplayName("Status");
				
				FacilioField field2 = new FacilioField();
				field2.setName("value");
				field2.setDisplayName("Count");
				
				List<FacilioField> fields = new ArrayList<>();
				fields.add(field1);
				fields.add(field2);
				
				/*if("csv".equals(getExportType()))
				{
					setExportUrl(ExportUtil.exportDataAsCSV(module, fields, records));
				}
				else if("xls".equals(getExportType()))
				{
					setExportUrl(ExportUtil.exportDataAsXLS(module, fields, records));
				}*/
			}
			else
			{
				setReportData(getOpenWorkOrderSummary(null));
			}
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
			setReportData(getCountSummary(null,false));
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
			setReportData(getWorkOrderVolumeSummary());
		}
		else if ("location".equalsIgnoreCase(getType())) {
			setReportData(getWorkOrderLocationSummary());
		}
		else if ("topSpaces".equalsIgnoreCase(getType())) {
			setReportData(getWorkOrderSpaceSummary());
		}
		else if ("assets".equalsIgnoreCase(getType())) {
			setReportData(getWorkOrderAssetSummary());
		}
		else if ("tasks".equalsIgnoreCase(getType())) {
			setPms(getWorkOrderPreventiveSummary());
		}
		else if("assetEfficiency".equalsIgnoreCase(getType())) {
			setReportData(getAvgResolutionTime(assetNotNullFilter,assetGroupBy));
		}
		else if("spaceEfficiency".equalsIgnoreCase(getType())) {
			setReportData(getAvgResolutionTime(spaceNotNullFilter,spaceGroupBy));
		}
		else if("countSummary".equalsIgnoreCase(getType())) {
			setReportData(getCountSummary(preventiveCdn,false));
		}
		return SUCCESS;
	}
	public String technician() throws Exception {
		String userFilter ="\""+FacilioConstants.Ticket.ASSIGNED_TO_ID+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is\",\"value\":[\""+AccountUtil.getCurrentUser().getOuid()+"_\"]}";
		String technicianDailyGroupBy = "{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.ACTUAL_WORK_END+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.ACTUAL_WORK_END+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",,\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.DAILY+"\"},{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.ASSIGNED_TO_ID+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.ASSIGNED_TO_ID+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}";
		if ("priority".equalsIgnoreCase(getType())) {
			setReportData(getMyWOPrioritySummary(userFilter));
		}
		else if("efficiency".equalsIgnoreCase(getType())) {
			setReportData(getAvgResolutionTime(userFilter,technicianDailyGroupBy));
		}
		else if("countSummary".equalsIgnoreCase(getType())) {
			setReportData(getCountSummary(userFilter,true));
		}
		else if ("closed".equalsIgnoreCase(getType())) {
			setReportData(getClosedWorkOrderSummary(userFilter));
		}
		else if ("open".equalsIgnoreCase(getType())) {
			setReportData(getOpenWorkOrderSummary(userFilter));
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

					LocationContext locationCtxt=building.getLocation();
					if(locationCtxt!=null)
					{
						locationCtxt=SpaceAPI.getLocationSpace(building.getLocation().getId());
						JSONObject location = new JSONObject();
						location.put("city", locationCtxt.getCity());
						location.put("street",locationCtxt.getStreet());
						if(locationCtxt.getLat()!=-1l) {
							location.put("lat",locationCtxt.getLat());
						}
						if(locationCtxt.getLng()!=-1) {
							location.put("lng",locationCtxt.getLng());
						}
						buildingObj.put("location", location);
					}
					// List<BaseSpaceContext> allSpaces = SpaceAPI.getBaseSpaceWithChildren(building.getId());
					String buildingCnd ="\""+FacilioConstants.Ticket.SPACE+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"building_is\",\"value\":[\""+building.getId()+"_\"]}";
					buildingObj.put("stats", getOpenWorkOrderSummary(buildingCnd));
					woStats.add(buildingObj);
				}
			}
			catch(Exception e) {
				log.info("Exception occurred ", e);
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
		
		if (getCriteria() != null) {	
	 		repContext.put(FacilioConstants.ContextNames.FILTER_CRITERIA, getCriteria());
 		}
		
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
		DateTimeUtil.getCurrenTime();
		String filters = "";
		if(filterBy!=null && !filterBy.isEmpty()) {
			filters ="{"+filterBy+",\""+FacilioConstants.Ticket.DUE_DATE+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"Till Yesterday\"}}";
		}
		else {
			filters ="{\""+FacilioConstants.Ticket.DUE_DATE+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"Till Yesterday\"}}";
		}
		JSONArray xAxis = (JSONArray) parser.parse(xAxisJSON);
		repContext.setXAxis(xAxis);
		JSONArray joins = (JSONArray) parser.parse(joinsJSON);
		repContext.setJoins(joins);
		JSONObject filterObj = (JSONObject) parser.parse(filters);
		repContext.setFilters(filterObj);
		repContext.setReportType(FacilioConstants.Reports.NUMERIC_REPORT_TYPE);
		repContext.put(FacilioConstants.ContextNames.CV_NAME, "open");
		
		if (getCriteria() != null) {	
	 		repContext.put(FacilioConstants.ContextNames.FILTER_CRITERIA, getCriteria());
 		}
		
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
		DateTimeUtil.getCurrenTime();
		String filters ="{\""+FacilioConstants.Ticket.ASSIGNED_TO_ID+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is empty\"}}";
		JSONArray xAxis = (JSONArray) parser.parse(xAxisJSON);
		repContext.setXAxis(xAxis);
		JSONArray joins = (JSONArray) parser.parse(joinsJSON);
		repContext.setJoins(joins);
		JSONObject filterObj = (JSONObject) parser.parse(filters);
		repContext.setFilters(filterObj);
		repContext.setReportType(FacilioConstants.Reports.NUMERIC_REPORT_TYPE);
		repContext.put(FacilioConstants.ContextNames.CV_NAME, "open");
		
		if (getCriteria() != null) {	
	 		repContext.put(FacilioConstants.ContextNames.FILTER_CRITERIA, getCriteria());
 		}
		
		Chain summaryReportChain = ReportsChainFactory.getWorkOrderReportChain();
 		summaryReportChain.execute(repContext);
 		Map<String, Object> rs = (Map<String, Object>) repContext.get(FacilioConstants.Reports.RESULT_SET);
		
		return rs;
	}
	
	private List<Map<String, Object>> getCountSummary(String filterBy,boolean isFromTechnician) throws Exception {
 		List<Map<String, Object>> rs = new ArrayList<>();
 		rs.add(getDueTodayCount(filterBy));
 		rs.add(getOverDueCount(filterBy));
 		if(isFromTechnician) {
 			rs.add(getOpenHighPriority(filterBy));
 		}
 		else {
 			rs.add(getUnassignedCount());
 		}
 		List<Map<String, Object>> openWoResult = getOpenWorkOrderSummary(filterBy);
 		Map<String,Object> openWO = new HashMap<>();
 		openWO.put(FacilioConstants.Reports.LABEL, "open");
 		openWO.put(FacilioConstants.Reports.VALUE, openWoResult);
 		rs.add(openWO);
		return rs;
	}
	private Map<String,Object> getOpenHighPriority(String filterBy) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//		SelectRecordsBuilder<FacilioStatus> builder = new SelectRecordsBuilder<FacilioStatus>()
//				.table("TicketStatus")
//				.moduleName("ticketstatus")
//				.beanClass(FacilioStatus.class)
//				.select(modBean.getAllFields("ticketstatus"))
//				.andCustomWhere("STATUS_TYPE=2");
//		List<FacilioStatus> statuses = builder.get();
		List<FacilioStatus> statuses = TicketAPI.getStatusOfStatusType(StatusType.CLOSED);

		Long closedStatusId = statuses.get(0).getId();
		
		SelectRecordsBuilder<TicketPriorityContext> builder1 = new SelectRecordsBuilder<TicketPriorityContext>()
				.table("TicketPriority")
				.moduleName("ticketpriority")
				.beanClass(TicketPriorityContext.class)
				.select(modBean.getAllFields("ticketpriority"))
				.andCustomWhere("PRIORITY=?", "High");
		List<TicketPriorityContext> priorities = builder1.get();
		
		Long highPriorityId = priorities.get(0).getId();
		
		String filters = "";
		if(filterBy!=null && !filterBy.isEmpty()) {
			filters ="{"+filterBy+",\""+FacilioConstants.Ticket.STATUS+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"isn't\",\"value\":[\""+closedStatusId+"_\"]}, \""+FacilioConstants.Ticket.PRIORITY+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is\",\"value\":[\""+highPriorityId+"_\"]}}";
		}
		else {
			filters ="\""+FacilioConstants.Ticket.STATUS+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"isn't\",\"value\":[\""+closedStatusId+"_\"]}}";
		}
		System.out.println("filters ---- "+filters);
		FacilioReportContext repContext = new FacilioReportContext();
		JSONParser parser = new JSONParser();
		String xAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Reports.ALL_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\"openHighPriority\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\"}]";
		String joinsJSON = "[{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.TICKET+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.INNER_JOIN+"\"}]";

		JSONArray xAxis = (JSONArray) parser.parse(xAxisJSON);
		repContext.setXAxis(xAxis);
		JSONArray joins = (JSONArray) parser.parse(joinsJSON);
		repContext.setJoins(joins);
		JSONObject filterObj = (JSONObject) parser.parse(filters);
		repContext.setFilters(filterObj);
		repContext.setReportType(FacilioConstants.Reports.NUMERIC_REPORT_TYPE);
		repContext.put(FacilioConstants.ContextNames.CV_NAME, "open");
		
		if (getCriteria() != null) {	
	 		repContext.put(FacilioConstants.ContextNames.FILTER_CRITERIA, getCriteria());
 		}
		
		Chain summaryReportChain = ReportsChainFactory.getWorkOrderReportChain();
 		summaryReportChain.execute(repContext);
 		Map<String, Object> rs = (Map<String, Object>) repContext.get(FacilioConstants.Reports.RESULT_SET);
		
		return rs;
	}
	
	private List<Map<String, Object>> getWoInflowTrend() throws Exception {
		FacilioReportContext repContext = new FacilioReportContext();
		JSONParser parser = new JSONParser();
		String xAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.CREATED_TIME+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.CREATED_TIME+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.DAILY+"\"}]";
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

	private String filters;
	
	public void setFilters(String filters) {
		this.filters = filters;
	}

	public String getFilters() {
		return this.filters;
	}
	
	private void setConditions(String moduleName, String fieldName, JSONObject fieldJson,List<Condition> conditionList) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioField field = modBean.getField(fieldName, moduleName);
		JSONArray value = (JSONArray) fieldJson.get("value");
		int operatorId;
		String operatorName;
		if (fieldJson.containsKey("operatorId")) {
			operatorId = (int) (long) fieldJson.get("operatorId");
			operatorName = Operator.OPERATOR_MAP.get(operatorId).getOperator();
		} else {
			operatorName = (String) fieldJson.get("operator");
			operatorId = field.getDataTypeEnum().getOperator(operatorName).getOperatorId();
		}
		
		if((value!=null && value.size() > 0) || (operatorName != null && !(operatorName.equals("is")) ) ) {
			
			Condition condition = new Condition();
			condition.setField(field);
			condition.setOperatorId(operatorId);
			
			if(value!=null && value.size()>0) {
				StringBuilder values = new StringBuilder();
				boolean isFirst = true;
				Iterator<String> iterator = value.iterator();
				while(iterator.hasNext())
				{
					String obj = iterator.next();
					if(!isFirst) {
						values.append(",");
					}
					else {
						isFirst = false;
					}
					if (obj.indexOf("_") != -1) {
						try {
							String filterValue = obj.split("_")[0];
							values.append(filterValue);
						}
						catch (Exception e) {
							values.append(obj);
						}
					}
					else {
						values.append(obj);
					}
				}
				condition.setValue(values.toString());
			}
			conditionList.add(condition);
		}
	}
	
	private Criteria getCriteria() throws Exception {
		if (getFilters() != null) {
			JSONParser parser = new JSONParser();
	 		JSONObject filterJson = (JSONObject) parser.parse(getFilters());
	 		
	 		if (filterJson.size() > 0) {
	 			Iterator<String> filterIterator = filterJson.keySet().iterator();
				String moduleName = "workorder";
				Criteria criteria = new Criteria();
				while(filterIterator.hasNext()) {
					String fieldName = filterIterator.next();
					Object fieldJson = filterJson.get(fieldName);
					List<Condition> conditionList = new ArrayList<>();
					if(fieldJson!=null && fieldJson instanceof JSONArray) {
						JSONArray fieldJsonArr = (JSONArray) fieldJson;
						for(int i=0;i<fieldJsonArr.size();i++) {
							JSONObject fieldJsonObj = (JSONObject) fieldJsonArr.get(i);
							setConditions(moduleName, fieldName, fieldJsonObj, conditionList);
						}
					}
					else if(fieldJson!=null && fieldJson instanceof JSONObject) {
						JSONObject fieldJsonObj = (JSONObject) fieldJson;
						setConditions(moduleName, fieldName, fieldJsonObj, conditionList);
					}
					criteria.groupOrConditions(conditionList);
				}
				
				return criteria;
	 		}
		}
		return null;
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
		
		if (getCriteria() != null) {	
	 		repContext.put(FacilioConstants.ContextNames.FILTER_CRITERIA, getCriteria());
 		}
		
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
	private List<Map<String, Object>> getWorkOrderVolumeSummary() throws Exception {
		
		FacilioReportContext repContext = new FacilioReportContext();
		JSONParser parser = new JSONParser();
		String xAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Ticket.SPACE+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Ticket.SPACE+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\"}]";
		String yAxisJSON = "[{\""+FacilioConstants.Reports.REPORT_FIELD+"\":\""+FacilioConstants.Reports.ALL_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_ALIAS+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\",\""+FacilioConstants.Reports.FIELD_MODULE+"\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\""+FacilioConstants.Reports.AGG_FUNC+"\":\""+FacilioConstants.Reports.COUNT_COLUMN+"\"}]";
		String joinsJSON = "[{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.TICKET+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.INNER_JOIN+"\"},{\""+FacilioConstants.Reports.JOIN_TABLE+"\":\""+FacilioConstants.ContextNames.BUILDING+"\",\""+FacilioConstants.Reports.JOIN_TYPE+"\":\""+FacilioConstants.Reports.INNER_JOIN+"\"}]";
		String filters ="{\""+FacilioConstants.Ticket.CREATED_TIME+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\""+getPeriod()+"\"},\""+FacilioConstants.Ticket.SPACE+"\":{\"module\":\""+FacilioConstants.ContextNames.WORK_ORDER+"\",\"operator\":\"is not empty\"}}";
		int topN = 7;
		String topNData = FacilioConstants.Reports.TOP_N+":"+topN+":"+FacilioConstants.Reports.COUNT_COLUMN;
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
		repContext.put(FacilioConstants.Reports.TOP_N_DATA, topNData);
		repContext.setReportType(FacilioConstants.Reports.TOP_N_SUMMARY_REPORT_TYPE);
		Chain summaryReportChain = ReportsChainFactory.getWorkOrderReportChain();
 		summaryReportChain.execute(repContext);
 		List<Map<String, Object>> rs = (List<Map<String, Object>>) repContext.get(FacilioConstants.Reports.RESULT_SET);
		
		return rs;
	}
	
	private List<PreventiveMaintenance> getWorkOrderPreventiveSummary() throws Exception {
		
		long startTime = System.currentTimeMillis() / 1000;
		int topN = 5;
		String topNData = FacilioConstants.Reports.BOTTOM_N+":"+topN+":"+FacilioConstants.Job.TABLE_NAME+"."+FacilioConstants.Job.NEXT_EXECUTION_TIME;
		FacilioReportContext context = new FacilioReportContext();
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_STARTTIME, startTime);
		context.put(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_ENDTIME, startTime + (7*24*60*60));
		context.put(FacilioConstants.Reports.TOP_N_DATA, topNData);
		

		Chain getPmchain = new ChainBase();
		getPmchain.addCommand(new SetTopNReportCommand());
		getPmchain.addCommand(new SetOrderByCommand());
		getPmchain.addCommand(new GetUpcomingPreventiveMaintenanceCommand());
		getPmchain.execute(context);
		
		List<PreventiveMaintenance> pms = (List<PreventiveMaintenance>) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE_LIST);
		return pms;
	}
	
	private List<Map<String, Object>> getWorkOrderSpaceSummary() throws Exception {
		
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