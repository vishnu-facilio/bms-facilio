package com.facilio.bmsconsole.actions;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext.AlarmType;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;
import com.opensymphony.xwork2.ActionSupport;

public class AlarmReportAction extends ActionSupport {

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

		if ("unackassign".equalsIgnoreCase(getType())) {
			List<Map<String, Object>> result= getUnAcknowledged();
			Map<String,Object> unAck= result.get(0);
			Map<String, Object> unAssign= getUnAssigned().get(0);
			unAck.putAll(unAssign);
		 	setReportData(result);	
		}
		return SUCCESS;
	}
	
	private List<Map<String, Object>> getUnAcknowledged() throws Exception {

		FacilioField countFld = new FacilioField();
		countFld.setName("unacknowledged");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.connection(conn)
					.select(fields)
					.table("Alarms")
					.andCustomWhere("Alarms.ORGID=? AND Alarms.STATUS=1 AND (Alarms.IS_ACKNOWLEDGED IS NULL OR Alarms.IS_ACKNOWLEDGED = ?)", orgId, false);
			List<Map<String, Object>> rs = builder.get();
			return rs;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private List<Map<String, Object>> getUnAssigned() throws Exception {

		FacilioField countFld = new FacilioField();
		countFld.setName("unassigned");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.connection(conn)
					.select(fields)
					.table("Alarms")
					.innerJoin("Tickets")
					.on("Alarms.TICKET_ID = Tickets.ID")
					.andCustomWhere("Alarms.ORGID=? AND Alarms.STATUS=1 AND Tickets.ORGID=? AND Tickets.ASSIGNED_TO_ID IS NULL", orgId, orgId);
			List<Map<String, Object>> rs = builder.get();
			return rs;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public String map() throws Exception {
		alarmTypeStats = new JSONArray();
		List<BuildingContext> buildings = getBuildings();
		if(buildings != null && !buildings.isEmpty()) {
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
				for(BuildingContext building : buildings) {
					JSONObject buildingObj = new JSONObject();
					buildingObj.put("id", building.getBaseSpaceId());
					buildingObj.put("name", building.getName());
					
					if(building.getLocation() != null) {
						JSONObject location = new JSONObject();
						location.put("lat", building.getLocation().getLat());
						location.put("lng", building.getLocation().getLng());
						buildingObj.put("location", location);
					}
					List<BaseSpaceContext> allSpaces = SpaceAPI.getBaseSpaceWithChildren(OrgInfo.getCurrentOrgInfo().getOrgid(), building.getBaseSpaceId(), conn);
					buildingObj.put("stats", getBuildingAlarmTypeStats(allSpaces));
					alarmTypeStats.add(buildingObj);
				}
			}
			catch(Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
		return SUCCESS;
	}
	
	private JSONArray alarmTypeStats;
	public JSONArray getAlarmTypeStats() {
		return alarmTypeStats;
	}
	public void setAlarmTypeStats(JSONArray alarmTypeStats) {
		this.alarmTypeStats = alarmTypeStats;
	}

	private JSONObject getBuildingAlarmTypeStats(List<BaseSpaceContext> spaces) throws Exception {
		JSONObject statsObj = new JSONObject();
		
		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);
		
		FacilioField typeField = new FacilioField();
		typeField.setName("type");
		typeField.setColumnName("ALARM_TYPE");
		typeField.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		fields.add(typeField);
		
		StringBuilder where = new StringBuilder();
		where.append("Alarms.ORGID = ? AND Alarms.STATUS = 1 AND Tickets.SPACE_ID IN (");
		
		boolean isFirst = true;
		for(BaseSpaceContext space : spaces) {
			if(isFirst) {
				isFirst = false;
			}
			else {
				where.append(", ");
			}
			where.append(space.getId());
		}
		where.append(")");
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.connection(conn)
					.select(fields)
					.table("Alarms")
					.innerJoin("Tickets")
					.on("Alarms.TICKET_ID = Tickets.ID")
					.groupBy("ALARM_TYPE")
					.andCustomWhere(where.toString(), orgId);
			List<Map<String, Object>> stats = builder.get();
			
			if(stats != null && !stats.isEmpty()) {
				int total = 0;
				for(Map<String, Object> stat : stats) {
					int type = (int) stat.get("type");
					long count = (long) stat.get("count");
					
					statsObj.put(AlarmType.getType(type).getStringVal(), count);
					total += count;
				}
				statsObj.put("Total", total);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return statsObj;
	}
	
	private List<BuildingContext> getBuildings() throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			List<FacilioField> buildingFields = modBean.getAllFields(FacilioConstants.ContextNames.BUILDING);
			
			SelectRecordsBuilder<BuildingContext> builder = new SelectRecordsBuilder<BuildingContext>()
					.connection(conn)
					.table("Building")
					.moduleName(FacilioConstants.ContextNames.BUILDING)
					.beanClass(BuildingContext.class)
					.select(buildingFields)
					.andCustomWhere("ORGID = ?", OrgInfo.getCurrentOrgInfo().getOrgid())
					.orderBy("ID");
			return builder.get();
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	@SuppressWarnings("unchecked")
	public String resolution() throws Exception {
		alarmResolutionStats = new JSONObject();
		List<BuildingContext> buildings = getBuildings();
		if(buildings != null && !buildings.isEmpty()) {
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
				JSONArray buildingArray = new JSONArray();
				for(BuildingContext building : buildings) {
					JSONObject buildingObj = new JSONObject();
					buildingObj.put("id", building.getBaseSpaceId());
					buildingObj.put("name", building.getName());
					
					List<BaseSpaceContext> allSpaces = SpaceAPI.getBaseSpaceWithChildren(OrgInfo.getCurrentOrgInfo().getOrgid(), building.getBaseSpaceId(), conn);
					buildingObj.put("stats", getBuildingAlarmResolutionStats(allSpaces));
					
					buildingArray.add(buildingObj);
				}
				alarmResolutionStats.put("buildings", buildingArray);
				
				JSONObject total  = new JSONObject();
				total.put("buildings", buildings.size());
				total.put("stats", getBuildingAlarmResolutionStats(null));
				
				alarmResolutionStats.put("total", total);
			}
			catch(Exception e) {
				e.printStackTrace();
				throw e;
			}
		}
		return SUCCESS;
	}
	
	private JSONObject getBuildingAlarmResolutionStats(List<BaseSpaceContext> spaces) throws Exception {
		JSONObject statsObj = new JSONObject();
		
		FacilioField countFld = new FacilioField();
		countFld.setName("avg");
		countFld.setColumnName("AVG(ACKNOWLEDGED_TIME-CREATED_TIME)");
		countFld.setDataType(FieldType.DECIMAL);
		
		FacilioField typeField = new FacilioField();
		typeField.setName("type");
		typeField.setColumnName("ALARM_TYPE");
		typeField.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		fields.add(typeField);
		
		FacilioField createdTimeFld = new FacilioField();
		createdTimeFld.setName("createdTime");
		createdTimeFld.setColumnName("CREATED_TIME");
		createdTimeFld.setModuleTableName("Alarms");
		createdTimeFld.setDataType(FieldType.DATE_TIME);
		
		Condition createdTime = new Condition();
		createdTime.setField(createdTimeFld);
		createdTime.setOperator(DateOperators.valueOf(getPeriod()));
		
		StringBuilder where = new StringBuilder();
		where.append("Alarms.ORGID = ? AND Alarms.IS_ACKNOWLEDGED = true");
		if(spaces != null && !spaces.isEmpty()) {
			where.append(" AND Tickets.SPACE_ID IN (");
			boolean isFirst = true;
			for(BaseSpaceContext space : spaces) {
				if(isFirst) {
					isFirst = false;
				}
				else {
					where.append(", ");
				}
				where.append(space.getId());
			}
			where.append(")");
		}
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.connection(conn)
					.select(fields)
					.table("Alarms")
					.innerJoin("Tickets")
					.on("Alarms.TICKET_ID = Tickets.ID")
					.groupBy("ALARM_TYPE")
					.andCustomWhere(where.toString(), orgId)
					.andCondition(createdTime);
			List<Map<String, Object>> stats = builder.get();
			
			if(stats != null && !stats.isEmpty()) {
				for(Map<String, Object> stat : stats) {
					int type = (int) stat.get("type");
					double count = ((BigDecimal) stat.get("avg")).doubleValue();
					
					statsObj.put(AlarmType.getType(type).getStringVal(), count);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return statsObj;
	}
	
	private JSONObject alarmResolutionStats;
	public JSONObject getAlarmResolutionStats() {
		return alarmResolutionStats;
	}
	public void setAlarmResolutionStats(JSONObject alarmResolutionStats) {
		this.alarmResolutionStats = alarmResolutionStats;
	}
	
	/*
	private List<Map<String, Object>> getClosedWorkOrderSummary(String period) throws Exception {
		
		FacilioField woIdFld = new FacilioField();
		woIdFld.setName("id");
		woIdFld.setColumnName("ID");
		woIdFld.setModuleTableName("WorkOrders");
		woIdFld.setDataType(FieldType.NUMBER);
		
		FacilioField dueDateFld = new FacilioField();
		dueDateFld.setName("dueDate");
		dueDateFld.setColumnName("DUE_DATE");
		dueDateFld.setModuleTableName("Tickets");
		dueDateFld.setDataType(FieldType.DATE_TIME);
		
		FacilioField actualWorkEndFld = new FacilioField();
		actualWorkEndFld.setName("actualWorkEnd");
		actualWorkEndFld.setColumnName("ACTUAL_WORK_END");
		actualWorkEndFld.setModuleTableName("Tickets");
		actualWorkEndFld.setDataType(FieldType.DATE_TIME);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(woIdFld);
		fields.add(dueDateFld);
		fields.add(actualWorkEndFld);
		
		Condition closedTime = new Condition();
		closedTime.setField(actualWorkEndFld);
		closedTime.setOperator(DateOperators.valueOf(period));
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.connection(conn)
					.select(fields)
					.table("WorkOrders")
					.innerJoin("Tickets")
					.on("WorkOrders.TICKET_ID = Tickets.ID")
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
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private List<Map<String, Object>> getWorkOrderCategorySummary() throws Exception {

		FacilioField categoryIdFld = new FacilioField();
		categoryIdFld.setName("category_id");
		categoryIdFld.setColumnName("ID");
		categoryIdFld.setModuleTableName("TicketCategory");
		categoryIdFld.setDataType(FieldType.NUMBER);
		
		FacilioField categoryFld = new FacilioField();
		categoryFld.setName("category");
		categoryFld.setColumnName("NAME");
		categoryFld.setModuleTableName("TicketCategory");
		categoryFld.setDataType(FieldType.STRING);
		
		FacilioField categoryDescFld = new FacilioField();
		categoryDescFld.setName("category_desc");
		categoryDescFld.setColumnName("DESCRIPTION");
		categoryDescFld.setModuleTableName("TicketCategory");
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
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.connection(conn)
					.select(fields)
					.table("WorkOrders")
					.innerJoin("Tickets")
					.on("WorkOrders.TICKET_ID = Tickets.ID")
					.innerJoin("TicketStatus")
					.on("Tickets.STATUS_ID = TicketStatus.ID")
					.leftJoin("TicketCategory")
					.on("Tickets.CATEGORY_ID = TicketCategory.ID")
					.groupBy("TicketCategory.ID")
					.andCustomWhere("WorkOrders.ORGID=? AND Tickets.ORGID = ? AND TicketStatus.STATUS_TYPE = ?", orgId, orgId, TicketStatusContext.StatusType.OPEN.getIntVal());

			List<Map<String, Object>> rs = builder.get();
			return rs;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private List<Map<String, Object>> getWorkOrderRequestSummary() throws Exception {

		FacilioField sourceTypeFld = new FacilioField();
		sourceTypeFld.setName("source_type");
		sourceTypeFld.setColumnName("SOURCE_TYPE");
		sourceTypeFld.setModuleTableName("Tickets");
		sourceTypeFld.setDataType(FieldType.NUMBER);

		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(sourceTypeFld);
		fields.add(countFld);
		
		FacilioField createdTimeFld = new FacilioField();
		createdTimeFld.setName("createdTime");
		createdTimeFld.setColumnName("CREATED_TIME");
		createdTimeFld.setModuleTableName("WorkOrderRequests");
		createdTimeFld.setDataType(FieldType.DATE_TIME);
		
		Condition createdTime = new Condition();
		createdTime.setField(createdTimeFld);
		createdTime.setOperator(DateOperators.valueOf(getPeriod()));
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.connection(conn)
					.select(fields)
					.table("WorkOrderRequests")
					.innerJoin("Tickets")
					.on("WorkOrderRequests.TICKET_ID = Tickets.ID")
					.groupBy("Tickets.SOURCE_TYPE")
					.andCustomWhere("WorkOrderRequests.ORGID = ? AND Tickets.ORGID = ? AND WorkOrderRequests.STATUS = ?", orgId, orgId, WorkOrderRequestContext.RequestStatus.OPEN.getIntVal())
					.andCondition(createdTime);

			List<Map<String, Object>> rs = builder.get();
			return rs;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private List<Map<String, Object>> getTechnicianWorkSummary() throws Exception {

		FacilioField assignedToFld = new FacilioField();
		assignedToFld.setName("technician_id");
		assignedToFld.setColumnName("ASSIGNED_TO_ID");
		assignedToFld.setModuleTableName("Tickets");
		assignedToFld.setDataType(FieldType.NUMBER);
		
		FacilioField assignedToNameFld = new FacilioField();
		assignedToNameFld.setName("technician_name");
		assignedToNameFld.setColumnName("NAME");
		assignedToNameFld.setModuleTableName("Users");
		assignedToNameFld.setDataType(FieldType.STRING);
		
		FacilioField assignedToEmailFld = new FacilioField();
		assignedToEmailFld.setName("technician_email");
		assignedToEmailFld.setColumnName("EMAIL");
		assignedToEmailFld.setModuleTableName("Users");
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
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.connection(conn)
					.select(fields)
					.table("WorkOrders")
					.innerJoin("Tickets")
					.on("WorkOrders.TICKET_ID = Tickets.ID")
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
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private List<Map<String, Object>> getWorkOrderLocationSummary() throws Exception {

		FacilioField spaceIdFld = new FacilioField();
		spaceIdFld.setName("space_id");
		spaceIdFld.setColumnName("SPACE_ID");
		spaceIdFld.setModuleTableName("Tickets");
		spaceIdFld.setDataType(FieldType.NUMBER);

		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(spaceIdFld);
		fields.add(countFld);
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.connection(conn)
					.select(fields)
					.table("WorkOrders")
					.innerJoin("Tickets")
					.on("WorkOrders.TICKET_ID = Tickets.ID")
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
					
					BaseSpaceContext baseSpace = SpaceAPI.getBaseSpace(spaceId, orgId, conn);
					
					primaryLocation = baseSpace.getName();
					
					row.put("primary_location", primaryLocation);
					row.put("secondary_location", secondaryLocation);
				}
			}
			return rs;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	*/
}