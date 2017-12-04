package com.facilio.bmsconsole.actions;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.AlarmContext.AlarmStatus;
import com.facilio.bmsconsole.context.AlarmContext.AlarmType;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
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
			this.period = "CURRENT_WEEK";
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
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Alarms")
				.andCustomWhere("Alarms.ORGID=? AND Alarms.STATUS=? AND (Alarms.IS_ACKNOWLEDGED IS NULL OR Alarms.IS_ACKNOWLEDGED = ?)",
						orgId, AlarmStatus.ACTIVE.getIntVal(), false);
		
		List<Map<String, Object>> rs = builder.get();
		return rs;
	}
	
	private List<Map<String, Object>> getUnAssigned() throws Exception {

		FacilioField countFld = new FacilioField();
		countFld.setName("unassigned");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Alarms")
				.innerJoin("Tickets")
				.on("Alarms.ID = Tickets.ID")
				.andCustomWhere("Alarms.ORGID=? AND Alarms.STATUS=? AND Tickets.ORGID=? AND Tickets.ASSIGNED_TO_ID IS NULL", orgId, AlarmStatus.ACTIVE.getIntVal(), orgId);
		List<Map<String, Object>> rs = builder.get();
		return rs;
	}
	
	@SuppressWarnings("unchecked")
	public String map() throws Exception {
		alarmTypeStats = new JSONArray();
		List<BuildingContext> buildings = getBuildings();
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
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Alarms")
				.innerJoin("Tickets")
				.on("Alarms.ID = Tickets.ID")
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
		
		return statsObj;
	}
	
	private List<BuildingContext> getBuildings() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> buildingFields = modBean.getAllFields(FacilioConstants.ContextNames.BUILDING);
		
		SelectRecordsBuilder<BuildingContext> builder = new SelectRecordsBuilder<BuildingContext>()
				.table("Building")
				.moduleName(FacilioConstants.ContextNames.BUILDING)
				.beanClass(BuildingContext.class)
				.select(buildingFields)
				.andCustomWhere("Building.ORGID = ?", AccountUtil.getCurrentOrg().getOrgId())
				.orderBy("ID");
		return builder.get();
	}
	
	@SuppressWarnings("unchecked")
	public String spaceResponse() throws Exception {
		String requestType=getType();
		if(requestType==null)
		{
			requestType="spaceResponse";
		}
		
		alarmResponseStats = new JSONObject();
		List<BuildingContext> buildings = getBuildings();
		if(buildings != null && !buildings.isEmpty()) {
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
				JSONArray buildingArray = new JSONArray();
				for(BuildingContext building : buildings) {
					JSONObject buildingObj = new JSONObject();
					buildingObj.put("id", building.getId());
					buildingObj.put("name", building.getName());
					
					List<BaseSpaceContext> allSpaces = SpaceAPI.getBaseSpaceWithChildren(building.getId());
					if(requestType.equals("spaceResponse"))
					{
						buildingObj.put("stats", getBuildingAlarmResolutionStats(allSpaces));
					}
					else
					{
						buildingObj.put("stats", getBuildingAlarmResponseStats(allSpaces));
					}
					
					buildingArray.add(buildingObj);
				}
				alarmResponseStats.put("buildings", buildingArray);
				
				JSONObject total  = new JSONObject();
				total.put("buildings", buildings.size());
				if(requestType.equals("spaceResponse"))
				{
					total.put("stats", getBuildingAlarmResolutionStats(null));
				}
				else
				{
					total.put("stats", getBuildingAlarmResponseStats(null));
				}
				
				alarmResponseStats.put("total", total);
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
		createdTimeFld.setModule(ModuleFactory.getAlarmsModule());
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
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Alarms")
				.innerJoin("Tickets")
				.on("Alarms.ID = Tickets.ID")
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
		
		return statsObj;
	}
	
	private JSONObject getBuildingAlarmResponseStats(List<BaseSpaceContext> spaces) throws Exception {
		JSONObject statsObj = new JSONObject();
		
		FacilioField countFld = new FacilioField();
		countFld.setName("avg");
		countFld.setColumnName("AVG(CLEARED_TIME-CREATED_TIME)");
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
		createdTimeFld.setModule(ModuleFactory.getAlarmsModule());
		createdTimeFld.setDataType(FieldType.DATE_TIME);
		
		Condition createdTime = new Condition();
		createdTime.setField(createdTimeFld);
		createdTime.setOperator(DateOperators.valueOf(getPeriod()));
		
		StringBuilder where = new StringBuilder();
		where.append("Alarms.ORGID = ? AND Alarms.STATUS = ? AND Tickets.ASSIGNED_TO_ID IS NOT NULL");
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
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Alarms")
				.innerJoin("Tickets")
				.on("Alarms.ID = Tickets.ID")
				.groupBy("ALARM_TYPE")
				.andCustomWhere(where.toString(), orgId,AlarmStatus.CLEAR.getIntVal())
				.andCondition(createdTime);
		List<Map<String, Object>> stats = builder.get();
		
		if(stats != null && !stats.isEmpty()) {
			for(Map<String, Object> stat : stats) {
				int type = (int) stat.get("type");
				double count = ((BigDecimal) stat.get("avg")).doubleValue();
				
				statsObj.put(AlarmType.getType(type).getStringVal(), count);
			}
		}
		return statsObj;
	}
	
	private JSONObject alarmResponseStats;
	public JSONObject getAlarmResponseStats() {
		return alarmResponseStats;
	}
	public void setAlarmResponseStats(JSONObject alarmResponseStats) {
		this.alarmResponseStats = alarmResponseStats;
	}
	
	
	@SuppressWarnings("unchecked")
	public String technicianResponse() throws Exception {
		
		String requestType=getType();
		if(requestType==null)
		{
			requestType="techResponse";
		}
		alarmResponseStats = new JSONObject();
		if(requestType.equalsIgnoreCase("techResponse"))
		{
			alarmResponseStats.put("technicians", getResponseStats(true));
			alarmResponseStats.put("total", getResponseStats(false));
			return SUCCESS;
		}
		alarmResponseStats.put("technicians", getResolutionStats(true));
		alarmResponseStats.put("total", getResolutionStats(false));
		return SUCCESS;
	}
	
	
	
	@SuppressWarnings("unchecked")
	private JSONArray getResponseStats(boolean groupBy) throws Exception {
		
		JSONArray jsonArray = new JSONArray();
		FacilioField countFld = new FacilioField();
		countFld.setName("total");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);
		
		String average="AVG(ACKNOWLEDGED_TIME-CREATED_TIME)";
		FacilioField avgFld = new FacilioField();
		avgFld.setName("average");
		avgFld.setColumnName(average);
		avgFld.setDataType(FieldType.DECIMAL);
		

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		fields.add(avgFld);
		
		if(groupBy)
		{
			FacilioField technicianField = new FacilioField();
			technicianField.setName("acknowledged");
			technicianField.setColumnName("ACKNOWLEDGED_BY");
			technicianField.setDataType(FieldType.NUMBER);
			fields.add(technicianField);
		}
		
		FacilioField createdTimeFld = new FacilioField();
		createdTimeFld.setName("createdTime");
		createdTimeFld.setColumnName("CREATED_TIME");
		createdTimeFld.setModule(ModuleFactory.getAlarmsModule());
		createdTimeFld.setDataType(FieldType.DATE_TIME);
		
		Condition createdTime = new Condition();
		createdTime.setField(createdTimeFld);
		createdTime.setOperator(DateOperators.valueOf(getPeriod()));
		
		StringBuilder where = new StringBuilder();
		where.append("Alarms.ORGID = ? AND Alarms.IS_ACKNOWLEDGED = true");
		
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Alarms");
				if(groupBy)
				{
					builder.groupBy("ACKNOWLEDGED_BY");
				}
				 builder.andCustomWhere(where.toString(), orgId)
				.andCondition(createdTime)
				.orderBy(average);
		List<Map<String, Object>> stats = builder.get();
		if(stats != null && !stats.isEmpty()) {
			for(Map<String, Object> stat : stats) {
				
				JSONObject statsObj = new JSONObject();
				long total = (long) stat.get("total");
				if(total==0)continue;
				if(groupBy){
					long acknowledger = (long) stat.get("acknowledged");
					statsObj.put("technician", acknowledger);
				}
				double avg = ((BigDecimal) stat.get("average")).doubleValue();
				statsObj.put("average", avg);
				statsObj.put("total", total);
				jsonArray.add(statsObj);
			}
		
		}
		
		return jsonArray;
	}
	
	
	@SuppressWarnings("unchecked")
	private JSONArray getResolutionStats(boolean groupBy) throws Exception {
		
		JSONArray jsonArray = new JSONArray();
		FacilioField countFld = new FacilioField();
		countFld.setName("total");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);
		String average="AVG(CLEARED_TIME-IFNULL(ACKNOWLEDGED_TIME,CREATED_TIME))";
		FacilioField avgFld = new FacilioField();
		avgFld.setName("average");
		avgFld.setColumnName(average);
		avgFld.setDataType(FieldType.DECIMAL);
		

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		fields.add(avgFld);
		
		if(groupBy)
		{	
			FacilioField assignedToFld = new FacilioField();
			assignedToFld.setName("technician");
			assignedToFld.setColumnName("ASSIGNED_TO_ID");
			assignedToFld.setModule(ModuleFactory.getTicketsModule());
			assignedToFld.setDataType(FieldType.NUMBER);
			fields.add(assignedToFld);
		}
		
		FacilioField createdTimeFld = new FacilioField();
		createdTimeFld.setName("createdTime");
		createdTimeFld.setColumnName("CREATED_TIME");
		createdTimeFld.setModule(ModuleFactory.getAlarmsModule());
		createdTimeFld.setDataType(FieldType.DATE_TIME);
		
		Condition createdTime = new Condition();
		createdTime.setField(createdTimeFld);
		createdTime.setOperator(DateOperators.valueOf(getPeriod()));
		
		StringBuilder where = new StringBuilder();
		where.append("Alarms.ORGID = ? AND Alarms.STATUS = ? AND Tickets.ASSIGNED_TO_ID IS NOT NULL");
		
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Alarms")
				.innerJoin("Tickets")
				.on("Alarms.ID = Tickets.ID");
				if(groupBy)
				{
					builder.groupBy("Tickets.ASSIGNED_TO_ID");
				}
				 builder.andCustomWhere(where.toString(), orgId, AlarmStatus.CLEAR.getIntVal())
				.andCondition(createdTime)
				.orderBy(average);
		List<Map<String, Object>> stats = builder.get();
		if(stats != null && !stats.isEmpty()) {
			for(Map<String, Object> stat : stats) {
				
				JSONObject statsObj = new JSONObject();
				long total = (long) stat.get("total");
				if(total==0)continue;
				if(groupBy){
					long technician = (long) stat.get("technician");
					statsObj.put("technician", technician);
				}
				double avg = stat.get("average") != null ? ((BigDecimal) stat.get("average")).doubleValue() : 0d;
				statsObj.put("average", avg);
				statsObj.put("total", total);
				jsonArray.add(statsObj);
			}
		}
		
		return jsonArray;
	}
}