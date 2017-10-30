package com.facilio.bmsconsole.actions;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.facilio.fw.OrgInfo;
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
			this.period = "TODAY";
		}
		period = period.toUpperCase();
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
	
	private List<Map<String, Object>> getOpenWorkOrderSummary() throws Exception {

		FacilioField statusIdFld = new FacilioField();
		statusIdFld.setName("status_id");
		statusIdFld.setColumnName("ID");
		statusIdFld.setModule(ModuleFactory.getTicketStatusModule());
		statusIdFld.setDataType(FieldType.NUMBER);
		
		FacilioField statusFld = new FacilioField();
		statusFld.setName("status");
		statusFld.setColumnName("STATUS");
		statusFld.setModule(ModuleFactory.getTicketStatusModule());
		statusFld.setDataType(FieldType.STRING);
		
		FacilioField statusTypeFld = new FacilioField();
		statusTypeFld.setName("status_type");
		statusTypeFld.setColumnName("STATUS_TYPE");
		statusTypeFld.setModule(ModuleFactory.getTicketStatusModule());
		statusTypeFld.setDataType(FieldType.NUMBER);

		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(statusIdFld);
		fields.add(statusFld);
		fields.add(statusTypeFld);
		fields.add(countFld);
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("WorkOrders")
				.innerJoin("Tickets")
				.on("WorkOrders.ID = Tickets.ID")
				.innerJoin("TicketStatus")
				.on("Tickets.STATUS_ID = TicketStatus.ID")
				.groupBy("TicketStatus.ID")
				.andCustomWhere("WorkOrders.ORGID=? AND Tickets.ORGID = ? AND TicketStatus.ORGID = ? AND TicketStatus.STATUS_TYPE = ?", orgId, orgId, orgId, TicketStatusContext.StatusType.OPEN.getIntVal());

		List<Map<String, Object>> rs = builder.get();
		return rs;
	}
	
	private List<Map<String, Object>> getClosedWorkOrderSummary(String period) throws Exception {
		
		FacilioField woIdFld = new FacilioField();
		woIdFld.setName("id");
		woIdFld.setColumnName("ID");
		woIdFld.setModule(ModuleFactory.getWorkOrdersModule());
		woIdFld.setDataType(FieldType.NUMBER);
		
		FacilioField dueDateFld = new FacilioField();
		dueDateFld.setName("dueDate");
		dueDateFld.setColumnName("DUE_DATE");
		dueDateFld.setModule(ModuleFactory.getTicketsModule());
		dueDateFld.setDataType(FieldType.DATE_TIME);
		
		FacilioField actualWorkEndFld = new FacilioField();
		actualWorkEndFld.setName("actualWorkEnd");
		actualWorkEndFld.setColumnName("ACTUAL_WORK_END");
		actualWorkEndFld.setModule(ModuleFactory.getTicketsModule());
		actualWorkEndFld.setDataType(FieldType.DATE_TIME);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(woIdFld);
		fields.add(dueDateFld);
		fields.add(actualWorkEndFld);
		
		Condition closedTime = new Condition();
		closedTime.setField(actualWorkEndFld);
		closedTime.setOperator(DateOperators.valueOf(period));
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("WorkOrders")
				.innerJoin("Tickets")
				.on("WorkOrders.ID = Tickets.ID")
				.innerJoin("TicketStatus")
				.on("Tickets.STATUS_ID = TicketStatus.ID")
				.andCustomWhere("WorkOrders.ORGID=? AND Tickets.ORGID = ? AND TicketStatus.ORGID = ? AND TicketStatus.STATUS_TYPE = ?", orgId, orgId, orgId, TicketStatusContext.StatusType.CLOSED.getIntVal())
				.andCondition(closedTime);

		List<Map<String, Object>> rs = builder.get();
		
		int total = 0;
		int ontime= 0;
		int overdue = 0;
		
		if (rs != null && !rs.isEmpty()) {
			total = rs.size();
			
			for (Map<String, Object> row : rs) {
				Long dueDate = (Long) row.get("dueDate");
				Long actualWorkEnd = (Long) row.get("actualWorkEnd");
				
				if (actualWorkEnd != null) {
					if (dueDate == null || dueDate <= 0) {
						ontime++;
					}
					else {
						int daysBtwn = DateTimeUtil.getDaysBetween(dueDate, actualWorkEnd);
						if (daysBtwn <= 0) {
							ontime++;
						}
						else {
							overdue++;
						}
					}
				}
			}
		}
		
		Map<String, Object> report = new HashMap<String, Object>();
		report.put("closed", total);
		report.put("ontime", ontime);
		report.put("overdue", overdue);
		
		List<Map<String, Object>> reportData = new ArrayList<>();
		reportData.add(report);
		return reportData;
	}
	
	private List<Map<String, Object>> getWorkOrderCategorySummary() throws Exception {

		FacilioField categoryIdFld = new FacilioField();
		categoryIdFld.setName("category_id");
		categoryIdFld.setColumnName("ID");
		categoryIdFld.setModule(ModuleFactory.getTicketCategoryModule());
		categoryIdFld.setDataType(FieldType.NUMBER);
		
		FacilioField categoryFld = new FacilioField();
		categoryFld.setName("category");
		categoryFld.setColumnName("NAME");
		categoryFld.setModule(ModuleFactory.getTicketCategoryModule());
		categoryFld.setDataType(FieldType.STRING);
		
		FacilioField categoryDescFld = new FacilioField();
		categoryDescFld.setName("category_desc");
		categoryDescFld.setColumnName("DESCRIPTION");
		categoryDescFld.setModule(ModuleFactory.getTicketCategoryModule());
		categoryDescFld.setDataType(FieldType.STRING);

		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(categoryIdFld);
		fields.add(categoryFld);
		fields.add(categoryDescFld);
		fields.add(countFld);
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("WorkOrders")
				.innerJoin("Tickets")
				.on("WorkOrders.ID = Tickets.ID")
				.innerJoin("TicketStatus")
				.on("Tickets.STATUS_ID = TicketStatus.ID")
				.leftJoin("TicketCategory")
				.on("Tickets.CATEGORY_ID = TicketCategory.ID")
				.groupBy("TicketCategory.ID")
				.andCustomWhere("WorkOrders.ORGID=? AND Tickets.ORGID = ? AND TicketStatus.STATUS_TYPE = ?", orgId, orgId, TicketStatusContext.StatusType.OPEN.getIntVal());

		List<Map<String, Object>> rs = builder.get();
		return rs;
	}
	
	private List<Map<String, Object>> getWorkOrderRequestSummary() throws Exception {

		FacilioField sourceTypeFld = new FacilioField();
		sourceTypeFld.setName("source_type");
		sourceTypeFld.setColumnName("SOURCE_TYPE");
		sourceTypeFld.setModule(ModuleFactory.getTicketsModule());
		sourceTypeFld.setDataType(FieldType.NUMBER);

		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);
		
		FacilioField createdTimeFld = new FacilioField();
		createdTimeFld.setName("createdTime");
		createdTimeFld.setColumnName("CREATED_TIME");
		createdTimeFld.setModule(ModuleFactory.getWorkOrderRequestsModule());
		createdTimeFld.setDataType(FieldType.DATE_TIME);
		
		List<FacilioField> fields = new ArrayList<>();
		fields.add(sourceTypeFld);
		fields.add(countFld);
		
		Condition createdTime = new Condition();
		createdTime.setField(createdTimeFld);
		createdTime.setOperator(DateOperators.valueOf(getPeriod()));
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("WorkOrderRequests")
				.innerJoin("Tickets")
				.on("WorkOrderRequests.ID = Tickets.ID")
				.groupBy("Tickets.SOURCE_TYPE")
				.andCustomWhere("WorkOrderRequests.ORGID = ? AND Tickets.ORGID = ? AND WorkOrderRequests.STATUS = ?", orgId, orgId, WorkOrderRequestContext.RequestStatus.OPEN.getIntVal())
				.andCondition(createdTime);

		List<Map<String, Object>> rs = builder.get();
		return rs;
	}
	
	private List<Map<String, Object>> getTechnicianWorkSummary() throws Exception {

		FacilioField assignedToFld = new FacilioField();
		assignedToFld.setName("technician_id");
		assignedToFld.setColumnName("ASSIGNED_TO_ID");
		assignedToFld.setModule(ModuleFactory.getTicketsModule());
		assignedToFld.setDataType(FieldType.NUMBER);
		
		FacilioField assignedToNameFld = new FacilioField();
		assignedToNameFld.setName("technician_name");
		assignedToNameFld.setColumnName("NAME");
		assignedToNameFld.setModule(ModuleFactory.getUsersModule());
		assignedToNameFld.setDataType(FieldType.STRING);
		
		FacilioField assignedToEmailFld = new FacilioField();
		assignedToEmailFld.setName("technician_email");
		assignedToEmailFld.setColumnName("EMAIL");
		assignedToEmailFld.setModule(ModuleFactory.getUsersModule());
		assignedToEmailFld.setDataType(FieldType.STRING);
		
		FacilioField countFld = new FacilioField();
		countFld.setName("open_workorders");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(assignedToFld);
		fields.add(assignedToNameFld);
		fields.add(assignedToEmailFld);
		fields.add(countFld);
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("WorkOrders")
				.innerJoin("Tickets")
				.on("WorkOrders.ID = Tickets.ID")
				.innerJoin("TicketStatus")
				.on("Tickets.STATUS_ID = TicketStatus.ID")
				.innerJoin("ORG_Users")
				.on("Tickets.ASSIGNED_TO_ID = ORG_Users.ORG_USERID")
				.innerJoin("Users")
				.on("ORG_Users.USERID = Users.USERID")
				.groupBy("Tickets.ASSIGNED_TO_ID")
				.andCustomWhere("WorkOrders.ORGID=? AND Tickets.ORGID = ? AND TicketStatus.ORGID = ? AND ORG_Users.ORGID=? AND TicketStatus.STATUS_TYPE = ?", orgId, orgId, orgId, orgId, TicketStatusContext.StatusType.OPEN.getIntVal());

		List<Map<String, Object>> rs = builder.get();
		return rs;
	}
	
	private List<Map<String, Object>> getWorkOrderLocationSummary() throws Exception {

		FacilioField spaceIdFld = new FacilioField();
		spaceIdFld.setName("space_id");
		spaceIdFld.setColumnName("SPACE_ID");
		spaceIdFld.setModule(ModuleFactory.getTicketsModule());
		spaceIdFld.setDataType(FieldType.NUMBER);

		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(spaceIdFld);
		fields.add(countFld);
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("WorkOrders")
				.innerJoin("Tickets")
				.on("WorkOrders.ID = Tickets.ID")
				.innerJoin("TicketStatus")
				.on("Tickets.STATUS_ID = TicketStatus.ID")
				.groupBy("Tickets.SPACE_ID")
				.andCustomWhere("WorkOrders.ORGID=? AND Tickets.ORGID = ? AND TicketStatus.STATUS_TYPE = ? AND Tickets.SPACE_ID IS NOT NULL AND Tickets.SPACE_ID > 0", orgId, orgId, TicketStatusContext.StatusType.OPEN.getIntVal())
				.limit(10);

		List<Map<String, Object>> rs = builder.get();
		if (rs != null && !rs.isEmpty()) {
			for (Map<String, Object> row : rs) {
				long spaceId = (Long) row.get("space_id");
				
				String primaryLocation = "--";
				String secondaryLocation = "--";
				
				BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(spaceId);
				
				primaryLocation = baseSpace.getName();
				if(baseSpace.getSpaceTypeEnum() == BaseSpaceContext.SpaceType.SPACE && baseSpace.getBuildingId() != -1)
				{
					secondaryLocation = SpaceAPI.getBuildingSpace(baseSpace.getBuildingId()).getName();
				}
				
				row.put("primary_location", primaryLocation);
				row.put("secondary_location", secondaryLocation);
			}
		}
		return rs;
	}
}