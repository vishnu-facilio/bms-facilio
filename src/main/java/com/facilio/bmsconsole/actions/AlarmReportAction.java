package com.facilio.bmsconsole.actions;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.context.AlarmContext.AlarmType;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.db.criteria.BuildingOperator;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.DateOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AlarmReportAction extends ActionSupport {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LogManager.getLogger(AlarmReportAction.class.getName());
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
	
	private long buildingId=-1;
	public long getBuildingId()
	{
		return this.buildingId;
	}
	
	public void setBuildingId(long buildingId)
	{
		this.buildingId=buildingId;
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
	
	@SuppressWarnings("unchecked")
	public String getAllBuildings() throws Exception
	{
		List<BuildingContext> buildings=SpaceAPI.getAllBuildings();
		JSONArray buildingArray= new JSONArray ();
		for(BuildingContext building:buildings) {
			JSONObject buildingData=ReportsUtil.getBuildingData(building);
			long spaceId=building.getId();
			buildingData.put("activeCount", getActiveAlarms(spaceId));
			//buildingData.put("totalCount", getTotalAlarms(spaceId));
			buildingArray.add(buildingData);
		}
		setAlarmTypeStats(buildingArray); 
        return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	public String getBuildingMap() throws Exception
	{
		List<BuildingContext> buildings=SpaceAPI.getAllBuildings();
		JSONObject result = new JSONObject();
		for(BuildingContext building:buildings) {
			long spaceId=building.getId();
			String name=building.getName();
			result.put(spaceId,name);
		}
		setAlarmResponseStats(result); 
        return SUCCESS;
	}
	
	private Long getActiveAlarms(long spaceId) throws Exception
	{
		
		FacilioField countFld = new FacilioField();
		countFld.setName("active");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		
		FacilioField resourceIdFld = new FacilioField();
		resourceIdFld.setName("resourceId");
		resourceIdFld.setColumnName("RESOURCE_ID");
		resourceIdFld.setModule(ModuleFactory.getTicketsModule());
		resourceIdFld.setDataType(FieldType.NUMBER);

		Condition spaceCond = new Condition();
		spaceCond.setField(resourceIdFld);
		spaceCond.setOperator(BuildingOperator.BUILDING_IS);
		spaceCond.setValue(spaceId+"");
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Alarms")
				.innerJoin("Tickets")
				.on("Alarms.ID = Tickets.ID")
				.innerJoin("Alarm_Severity")
				.on("Alarms.SEVERITY=Alarm_Severity.ID")
				.andCustomWhere("Alarms.ORGID=?",orgId)
				.andCustomWhere("Alarm_Severity.SEVERITY != ?",FacilioConstants.Alarm.CLEAR_SEVERITY)
				//.andCondition(getSpaceCondition(spaceId));
				.andCondition(spaceCond);
		List<Map<String, Object>> rs = builder.get();
		if(rs.isEmpty()) {
			return 0L;
		}
		
		return ((Number) rs.get(0).get("active")).longValue();
		
	}
	
	private Condition getSpaceCondition(long spaceId)
	{
		return 	CriteriaAPI.getCondition("SPACE_ID", "SPACE_ID", ""+spaceId, BuildingOperator.BUILDING_IS);
	}
	private Long getTotalAlarms(long spaceId) throws Exception
	{
		long startTime=DateTimeUtil.getWeekStartTime();
		
		FacilioField countFld = new FacilioField();
		countFld.setName("total");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		
		FacilioField resourceIdFld = new FacilioField();
		resourceIdFld.setName("resourceId");
		resourceIdFld.setColumnName("RESOURCE_ID");
		resourceIdFld.setModule(ModuleFactory.getTicketsModule());
		resourceIdFld.setDataType(FieldType.NUMBER);

		Condition spaceCond = new Condition();
		spaceCond.setField(resourceIdFld);
		spaceCond.setOperator(BuildingOperator.BUILDING_IS);
		spaceCond.setValue(spaceId+"");
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Alarms")
				.innerJoin("Tickets")
				.on("Alarms.ID = Tickets.ID")
				.andCustomWhere("Alarms.ORGID=?",orgId)
				.andCustomWhere("Alarms.MODIFIED_TIME > ?", startTime)
				//.andCondition(getSpaceCondition(spaceId));
				.andCondition(spaceCond);
		List<Map<String, Object>> rs = builder.get();
		if(rs.isEmpty()) {
			return 0L;
		}
		return (Long)rs.get(0).get("total");
	}
	private List<Map<String, Object>> getUnAcknowledged() throws Exception {

		FacilioField countFld = new FacilioField();
		countFld.setName("unacknowledged");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);
		

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		
		Condition spaceCond = null;
		if(buildingId!=-1) {
			FacilioField resourceIdFld = new FacilioField();
			resourceIdFld.setName("resourceId");
			resourceIdFld.setColumnName("RESOURCE_ID");
			resourceIdFld.setModule(ModuleFactory.getTicketsModule());
			resourceIdFld.setDataType(FieldType.NUMBER);
	
			spaceCond = new Condition();
			spaceCond.setField(resourceIdFld);
			spaceCond.setOperator(BuildingOperator.BUILDING_IS);
			spaceCond.setValue(buildingId+"");
		}
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Alarms")
				.innerJoin("Alarm_Severity")
				.on("Alarms.SEVERITY=Alarm_Severity.ID")
				.andCustomWhere("Alarms.ORGID=?",orgId)
				.andCustomWhere("Alarm_Severity.SEVERITY != ?",FacilioConstants.Alarm.CLEAR_SEVERITY)
				.andCustomWhere("(Alarms.IS_ACKNOWLEDGED IS NULL OR Alarms.IS_ACKNOWLEDGED = ?)",false);
//		if(buildingId!=-1) {
//			builder.innerJoin("Tickets")
//			.on("Alarms.ID = Tickets.ID")
//			.andCondition(getSpaceCondition(buildingId));
//		}
		if(spaceCond != null)
		{
			builder.innerJoin("Tickets")
			.on("Alarms.ID = Tickets.ID")
			.andCondition(spaceCond);
		}
		List<Map<String, Object>> rs = builder.get();
		return rs;
	}
	
	private List<Map<String, Object>> getUnAssigned() throws Exception {

		FacilioField countFld = new FacilioField();
		countFld.setName("active");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		
		Condition spaceCond = null;
		if(buildingId!=-1) {
			FacilioField resourceIdFld = new FacilioField();
			resourceIdFld.setName("resourceId");
			resourceIdFld.setColumnName("RESOURCE_ID");
			resourceIdFld.setModule(ModuleFactory.getTicketsModule());
			resourceIdFld.setDataType(FieldType.NUMBER);
	
			spaceCond = new Condition();
			spaceCond.setField(resourceIdFld);
			spaceCond.setOperator(BuildingOperator.BUILDING_IS);
			spaceCond.setValue(buildingId+"");
		}
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Alarms")
				.innerJoin("Tickets")
				.on("Alarms.ID = Tickets.ID")
				.innerJoin("Alarm_Severity")
				.on("Alarms.SEVERITY=Alarm_Severity.ID")
				.andCustomWhere("Alarms.ORGID=? ",orgId)
				.andCustomWhere("Alarm_Severity.SEVERITY!=?",FacilioConstants.Alarm.CLEAR_SEVERITY)
				.andCustomWhere("Tickets.ORGID=?",orgId);
//		if(buildingId!=-1) {
//			builder.andCondition(getSpaceCondition(buildingId));
//		}
		if(spaceCond!=null) {
			builder.andCondition(spaceCond);
		}
		List<Map<String, Object>> rs = builder.get();
		return rs;
	}
	
	@SuppressWarnings("unchecked")
	public String map() throws Exception {
		alarmTypeStats = new JSONArray();
		List<BuildingContext> buildings = SpaceAPI.getAllBuildings();
		if(buildings != null && !buildings.isEmpty()) {
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
				for(BuildingContext building : buildings) {
					
					JSONObject buildingObj = ReportsUtil.getBuildingData(building);
					
					buildingObj.put("stats", getBuildingAlarmTypeStats(building.getId()));
					
					alarmTypeStats.add(buildingObj);
				}
			}
			catch(Exception e) {
				log.info("Exception occurred ", e);
				throw e;
			}
		}
		return SUCCESS;
	}
	
	public String siteMap() throws Exception {
		alarmTypeStats = new JSONArray();
		List<SiteContext> sites = SpaceAPI.getAllSites();
		if(sites != null && !sites.isEmpty()) {
			try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
				for(SiteContext site : sites) {
					
					if(site.getLocation() != null && site.getLocation().getId() > 0) {
						FacilioContext context = new FacilioContext();
						context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.LOCATION);
						context.put(FacilioConstants.ContextNames.ID, site.getLocation().getId());
						Chain getLocationChain = FacilioChainFactory.getLocationChain();
						getLocationChain.execute(context);
						
						if(context.get(FacilioConstants.ContextNames.LOCATION) != null ) {
							site.setLocation((LocationContext)context.get(FacilioConstants.ContextNames.LOCATION));
						}
					}
					JSONObject siteObj = new JSONObject();
					
					siteObj.put("siteMeta", site);
					siteObj.put("stats", getBuildingAlarmTypeStats(site.getId()));
					
					alarmTypeStats.add(siteObj);
				}
			}
			catch(Exception e) {
				log.info("Exception occurred ", e);
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

	private JSONObject getBuildingAlarmTypeStats(long buildingId) throws Exception {
		JSONObject statsObj = new JSONObject();
		
		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);
		
		FacilioField typeField = new FacilioField();
		typeField.setName("severity");
		typeField.setColumnName("Alarm_Severity.SEVERITY");
		typeField.setDataType(FieldType.STRING);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		fields.add(typeField);
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Alarms")
				.innerJoin("Tickets")
				.on("Alarms.ID = Tickets.ID")
				.innerJoin("Alarm_Severity")
				.on("Alarms.SEVERITY=Alarm_Severity.ID")
				.groupBy("Alarm_Severity.SEVERITY")
				.andCustomWhere("Alarms.ORGID = ? AND Alarm_Severity.SEVERITY != ?", orgId, FacilioConstants.Alarm.CLEAR_SEVERITY);
		
		FacilioField resourceIdFld = new FacilioField();
		resourceIdFld.setName("resourceId");
		resourceIdFld.setColumnName("RESOURCE_ID");
		resourceIdFld.setModule(ModuleFactory.getTicketsModule());
		resourceIdFld.setDataType(FieldType.NUMBER);

		Condition spaceCond = new Condition();
		spaceCond.setField(resourceIdFld);
		spaceCond.setOperator(BuildingOperator.BUILDING_IS);
		spaceCond.setValue(buildingId+"");
		
		builder.andCondition(spaceCond);
		
		List<Map<String, Object>> stats = builder.get();
		
		if(stats != null && !stats.isEmpty()) {
			int total = 0;
			for(Map<String, Object> stat : stats) {
				String severity = (String) stat.get("severity");
				long count = (long) stat.get("count");
				
				statsObj.put(severity, count);
				total += count;
			}
			statsObj.put("Total", total);
		}
		
		return statsObj;
	}
	
	
	
	@SuppressWarnings("unchecked")
	public String spaceResponse() throws Exception {
		

		alarmResponseStats = new JSONObject();
		List<BuildingContext> buildings = SpaceAPI.getAllBuildings();
		if(buildings != null && !buildings.isEmpty()) {
			JSONArray buildingArray = new JSONArray();
			for(BuildingContext building : buildings) {
				JSONObject buildingObj = new JSONObject();
				buildingObj.put("id", building.getId());
				buildingObj.put("name", building.getName());

				List<BaseSpaceContext> allSpaces = SpaceAPI.getBaseSpaceWithChildren(building.getId());
				
					buildingObj.put("stats", getBuildingAlarmResponseStats(allSpaces));

				buildingArray.add(buildingObj);
			}
			alarmResponseStats.put("buildings", buildingArray);

//			JSONObject total  = new JSONObject();
//			total.put("buildings", buildings.size());
//			if(requestType.equals("spaceResponse"))
//			{
//				total.put("stats", getBuildingAlarmResolutionStats(null));
//			}
//			else
//			{
//				total.put("stats", getBuildingAlarmResponseStats(null));
//			}
//
//			alarmResponseStats.put("total", total);
		}
		return SUCCESS;
		}
		
	@SuppressWarnings("unchecked")
	public String topLocations() throws Exception {

		alarmResponseStats = new JSONObject();
		List<BuildingContext> buildings = SpaceAPI.getAllBuildings();
		if(buildings != null && !buildings.isEmpty()) {
			JSONArray buildingArray = new JSONArray();
			for(BuildingContext building : buildings) {
				JSONObject buildingObj = new JSONObject();
				buildingObj.put("id", building.getId());
				buildingObj.put("name", building.getName());
				List<BaseSpaceContext> allSpaces = SpaceAPI.getBaseSpaceWithChildren(building.getId());
				buildingObj.put("stats", getBuildingAlarmStats(allSpaces));
				buildingArray.add(buildingObj);
			}
			alarmResponseStats.put("buildings", buildingArray);

		}
		return SUCCESS;
	}


	
	
	private JSONObject getBuildingAlarmStats(List<BaseSpaceContext> spaces) throws Exception {
		JSONObject statsObj = new JSONObject();
		
		FacilioField countFld = new FacilioField();
		countFld.setName("count");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);
		
		FacilioField typeField = new FacilioField();
		typeField.setName("severity");
		typeField.setColumnName("Alarm_Severity.SEVERITY");
		typeField.setDataType(FieldType.STRING);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		fields.add(typeField);
		
		
		
		Condition createdTime = new Condition();
		createdTime.setFieldName("MODIFIED_TIME");
		createdTime.setColumnName("MODIFIED_TIME");
		createdTime.setOperator(DateOperators.valueOf(getPeriod()));
		
		StringBuilder where = new StringBuilder();
		where.append("Alarms.ORGID = ? ");
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
				.innerJoin("Alarm_Severity")
				.on("Alarms.SEVERITY=Alarm_Severity.ID")
				.groupBy("Alarm_Severity.SEVERITY")
				.andCustomWhere(where.toString(), orgId)
				.andCondition(createdTime);
		List<Map<String, Object>> stats = builder.get();
		
		if(stats != null && !stats.isEmpty()) {
			for(Map<String, Object> stat : stats) {
				String type= (String)stat.get("severity");
				if(type==null) {
					continue;
				}
				long count = (long) stat.get("count");
				
				statsObj.put(type, count);
			}
		}
		
		return statsObj;
	}
	
	private JSONObject getBuildingAlarmResolutionStats(List<BaseSpaceContext> spaces) throws Exception {
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
		where.append("Alarms.ORGID = ? AND Alarms.SEVERITY = ? AND Tickets.ASSIGNED_TO_ID IS NOT NULL");
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
				.andCustomWhere(where.toString(), orgId,FacilioConstants.Alarm.CLEAR_SEVERITY)
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
			where.append(" AND Tickets.RESOURCE_ID IN (");
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
				Object type= stat.get("type");
				if(type==null) {
					continue;
				}
				double count = ((BigDecimal) stat.get("avg")).doubleValue();
				
				statsObj.put(AlarmType.getType((int)type).getStringVal(), count);
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
		alarmResponseStats = new JSONObject();
		if(requestType==null)
		{
			alarmResponseStats.put("technicians", getResponseStats());
			return SUCCESS;
		}
		alarmResponseStats.put("technicians", getResolutionStats());
		return SUCCESS;
	}
	
	public String severityStats() throws Exception {
		
		FacilioField countFld = new FacilioField();
		countFld.setName("total");
		countFld.setColumnName("COUNT(*)");
		countFld.setDataType(FieldType.NUMBER);
		
		FacilioField typeField = new FacilioField();
		typeField.setName("severity");
		typeField.setColumnName("Alarm_Severity.SEVERITY");
		typeField.setDataType(FieldType.STRING);
		
		Condition modTime = new Condition();
		modTime.setFieldName("MODIFIED_TIME");
		modTime.setColumnName("MODIFIED_TIME");
		modTime.setOperator(DateOperators.valueOf(getPeriod()));

		List<FacilioField> fields = new ArrayList<>();
		fields.add(countFld);
		fields.add(typeField);
		
		Condition spaceCond = null;
		if(buildingId!=-1) {
			FacilioField resourceIdFld = new FacilioField();
			resourceIdFld.setName("resourceId");
			resourceIdFld.setColumnName("RESOURCE_ID");
			resourceIdFld.setModule(ModuleFactory.getTicketsModule());
			resourceIdFld.setDataType(FieldType.NUMBER);
	
			spaceCond = new Condition();
			spaceCond.setField(resourceIdFld);
			spaceCond.setOperator(BuildingOperator.BUILDING_IS);
			spaceCond.setValue(buildingId+"");
		}
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Alarms")
				.innerJoin("Alarm_Severity")
				.on("Alarms.SEVERITY=Alarm_Severity.ID");
//				 if(buildingId!=-1) {
//					 builder.innerJoin("Tickets")
//						.on("Alarms.ID = Tickets.ID")
//						.andCondition(getSpaceCondition(buildingId));
//					}
				if(spaceCond != null) 
				{
					builder.innerJoin("Tickets")
					.on("Alarms.ID = Tickets.ID")
					.andCondition(spaceCond);
				}
				 builder.andCustomWhere("Alarms.ORGID = ?", orgId)
				.andCondition(modTime)
				.groupBy("Alarm_Severity.SEVERITY");
				List<Map<String, Object>> result = builder.get();
		setReportData(result);
		return SUCCESS;
	}
	
	
	
	@SuppressWarnings("unchecked")
	private JSONArray getResponseStats() throws Exception {
		
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
		

		FacilioField technicianField = new FacilioField();
		technicianField.setName("acknowledged");
		technicianField.setColumnName("ACKNOWLEDGED_BY");
		technicianField.setDataType(FieldType.NUMBER);
		fields.add(technicianField);
		
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
		
		Condition spaceCond = null;
		if(buildingId!=-1) {
			FacilioField resourceIdFld = new FacilioField();
			resourceIdFld.setName("resourceId");
			resourceIdFld.setColumnName("RESOURCE_ID");
			resourceIdFld.setModule(ModuleFactory.getTicketsModule());
			resourceIdFld.setDataType(FieldType.NUMBER);
	
			spaceCond = new Condition();
			spaceCond.setField(resourceIdFld);
			spaceCond.setOperator(BuildingOperator.BUILDING_IS);
			spaceCond.setValue(buildingId+"");
		}
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Alarms");
//				 if(buildingId!=-1) {
//					 builder.innerJoin("Tickets")
//						.on("Alarms.ID = Tickets.ID")
//						.andCondition(getSpaceCondition(buildingId));
//					}
		if(spaceCond != null) {
			 builder.innerJoin("Tickets")
				.on("Alarms.ID = Tickets.ID")
				.andCondition(spaceCond);
			}
				builder.andCustomWhere(where.toString(), orgId)
				.andCondition(createdTime)
				.groupBy("ACKNOWLEDGED_BY WITH ROLLUP");
		List<Map<String, Object>> stats = builder.get();
		double totalAvg=0;
		int totalCount=0;
		if(stats!=null && !stats.isEmpty()) {
			totalCount=stats.size()-1;
			Map<String,Object> avgTotal=stats.remove(totalCount);
			totalAvg = avgTotal.get("average") != null ? ((BigDecimal) avgTotal.get("average")).doubleValue() : 0d;
			totalAvg *=totalCount;
		}
			for(Map<String, Object> stat : stats) {
				
				JSONObject statsObj = new JSONObject();
				long total = (long) stat.get("total");
				if(total==0)continue;
				long acknowledger = (long) stat.get("acknowledged");
				statsObj.put("technician", acknowledger);
				double avg = ((BigDecimal) stat.get("average")).doubleValue();
				statsObj.put("average", avg);
				statsObj.put("total", total);
				double percent=ReportsUtil.getPercentage(avg, totalAvg);
				statsObj.put("percentage", roundToMinLevel(percent));
				jsonArray.add(statsObj);
			}
			
		return jsonArray;
	}
	
	
	@SuppressWarnings("unchecked")
	private JSONArray getResolutionStats() throws Exception {
		
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
		

		FacilioField assignedToFld = new FacilioField();
		assignedToFld.setName("technician");
		assignedToFld.setColumnName("ASSIGNED_TO_ID");
		assignedToFld.setModule(ModuleFactory.getTicketsModule());
		assignedToFld.setDataType(FieldType.NUMBER);
		fields.add(assignedToFld);

		FacilioField createdTimeFld = new FacilioField();
		createdTimeFld.setName("createdTime");
		createdTimeFld.setColumnName("CREATED_TIME");
		createdTimeFld.setModule(ModuleFactory.getAlarmsModule());
		createdTimeFld.setDataType(FieldType.DATE_TIME);
		
		Condition createdTime = new Condition();
		createdTime.setField(createdTimeFld);
		createdTime.setOperator(DateOperators.valueOf(getPeriod()));
		
		StringBuilder where = new StringBuilder();
		where.append("Alarms.ORGID = ? AND Alarm_Severity.SEVERITY = ? AND Tickets.ASSIGNED_TO_ID IS NOT NULL");
		
		Condition spaceCond = null;
		if(buildingId!=-1) {
			FacilioField resourceIdFld = new FacilioField();
			resourceIdFld.setName("resourceId");
			resourceIdFld.setColumnName("RESOURCE_ID");
			resourceIdFld.setModule(ModuleFactory.getTicketsModule());
			resourceIdFld.setDataType(FieldType.NUMBER);
	
			spaceCond = new Condition();
			spaceCond.setField(resourceIdFld);
			spaceCond.setOperator(BuildingOperator.BUILDING_IS);
			spaceCond.setValue(buildingId+"");
		}
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Alarms")
				.innerJoin("Tickets")
				.on("Alarms.ID = Tickets.ID")
				.innerJoin("Alarm_Severity")
				.on("Alarms.SEVERITY=Alarm_Severity.ID")
				.andCustomWhere(where.toString(), orgId, FacilioConstants.Alarm.CLEAR_SEVERITY)
				.andCondition(createdTime);
//				 if(buildingId!=-1) {
//						builder.andCondition(getSpaceCondition(buildingId));
//					}
			if(spaceCond != null)
			{
				builder.andCondition(spaceCond);
			}
				builder.groupBy("Tickets.ASSIGNED_TO_ID WITH ROLLUP");
				
		List<Map<String, Object>> stats = builder.get();
		double totalAvg=0;
		int totalCount=0;
		if(stats!=null && !stats.isEmpty()) {
			totalCount=stats.size()-1;
			Map<String,Object> avgTotal=stats.remove(totalCount);
			totalAvg = avgTotal.get("average") != null ? ((BigDecimal) avgTotal.get("average")).doubleValue() : 0d;
			totalAvg *=totalCount;
		}
		for(Map<String, Object> stat : stats) {

			JSONObject statsObj = new JSONObject();
			long total = (long) stat.get("total");
			if(total==0)continue;
			long technician = (long) stat.get("technician");
			statsObj.put("technician", technician);
			double avg = stat.get("average") != null ? ((BigDecimal) stat.get("average")).doubleValue() : 0d;
			statsObj.put("average", avg);
			statsObj.put("total", total);
			double percent=ReportsUtil.getPercentage(avg, totalAvg);
			statsObj.put("percentage", roundToMinLevel(percent));
			jsonArray.add(statsObj);
		}
		
		return jsonArray;
	}
	
	private double roundToMinLevel(double percent)
	{
		int minVal=3;
		return (percent<minVal)?minVal:percent;
	}
}