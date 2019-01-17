package com.facilio.bmsconsole.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.ReadingAlarmContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.AlarmAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.rule.ActivityType;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.DBUtil;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class AlarmAction extends FacilioAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LogManager.getLogger(AlarmAction.class.getName());
	private AlarmContext alarm;
	public AlarmContext getAlarm() {
		return alarm;
	}
	public void setAlarm(AlarmContext alarm) {
		this.alarm = alarm;
	}

	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}

	public String createWorkorder() throws Exception {
		FacilioContext woContext = new FacilioContext();
		woContext.put(FacilioConstants.ContextNames.RECORD_ID, id.get(0));
		woContext.put(FacilioConstants.ContextNames.RECORD, alarm);

		Chain addWorkOrder = TransactionChainFactory.getAddWoFromAlarmChain();
		addWorkOrder.execute(woContext);
		
		rowsUpdated = (int) woContext.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		WorkOrderContext wo = (WorkOrderContext) woContext.get(FacilioConstants.ContextNames.WORK_ORDER);
		setWoId(wo.getId());
		
		return SUCCESS;
	}

	public String updateStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		return updateAlarm(context);
	}
	
	private String updateAlarm(FacilioContext context) throws Exception {
		//		System.out.println(id);
		//		System.out.println(alarm);
		context.put(FacilioConstants.ContextNames.ALARM, alarm);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);

		Chain updateAlarm = TransactionChainFactory.getUpdateAlarmChain();
		updateAlarm.execute(context);
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);

		return SUCCESS;
	}

	private List<Long> id;
	public List<Long> getId() {
		return id;
	}
	public void setId(List<Long> id) {
		this.id = id;
	}
	
	private long woId = -1;
	public long getWoId() {
		return woId;
	}
	public void setWoId(long woId) {
		this.woId = woId;
	}
	public String deleteAlarm() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.DELETE);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);
		
		Chain deleteAlarm = FacilioChainFactory.getDeleteAlarmChain();
		deleteAlarm.execute(context);
		rowsUpdated = (int) context.get(FacilioConstants.ContextNames.ROWS_UPDATED);
		
		return SUCCESS;
	}

	private int rowsUpdated;
	public int getRowsUpdated() {
		return rowsUpdated;
	}
	public void setRowsUpdated(int rowsUpdated) {
		this.rowsUpdated = rowsUpdated;
	}
	private String isCount;
	
	
	public String getIsCount() {
		return isCount;
	}
	public void setIsCount(String isCount) {
		this.isCount = isCount;
	}
	private long count ;
	public long getCount() {
		return count;
	}
	public void setCount(long count) {
		this.count = count;
	}
	private FacilioContext constructChainContext() throws Exception {
		FacilioContext context = new FacilioContext();
 		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
 		if(getFilters() != null)
 		{	
	 		JSONParser parser = new JSONParser();
	 		JSONObject json = (JSONObject) parser.parse(getFilters());
	 		context.put(FacilioConstants.ContextNames.FILTERS, json);
	 		context.put(FacilioConstants.ContextNames.INCLUDE_PARENT_CRITERIA, getIncludeParentFilter());
 		}
 		if (getSearch() != null) {
 			JSONObject searchObj = new JSONObject();
 			searchObj.put("fields", "alarm.subject,alarm.description");
 			searchObj.put("query", getSearch());
	 		context.put(FacilioConstants.ContextNames.SEARCH, searchObj);
 		}
 		context.put(FacilioConstants.ContextNames.CRITERIA_IDS, getCriteriaIds());
 		if (getIsCount() != null) {
 			context.put(FacilioConstants.ContextNames.COUNT, getIsCount());
 		}
 		JSONObject sorting = new JSONObject();
 		if (getOrderBy() != null) {
 			sorting.put("orderBy", getOrderBy());
 			sorting.put("orderType", getOrderType());
 		}
 		else {
 			sorting.put("orderBy", "modifiedTime");
 			sorting.put("orderType", "desc");
 		}
 		context.put(FacilioConstants.ContextNames.SORTING, sorting);
 		
 		JSONObject pagination = new JSONObject();
 		pagination.put("page", getPage());
 		pagination.put("perPage", getPerPage());
 		context.put(FacilioConstants.ContextNames.PAGINATION, pagination);
 		System.out.println("PAGINATION ####### "+ pagination);
 		
 		return context;
	}
	
	public String v2alarmCount () throws Exception {
		alarmList();
		setResult(FacilioConstants.ContextNames.COUNT, getCount());
		return SUCCESS;
	}

	public String alarmList() throws Exception {
 		FacilioContext context = constructChainContext();
 		context.put(FacilioConstants.ContextNames.ALARM_ENTITY_ID, entityId);
 		
 		System.out.println("View Name : "+getViewName());
 		Chain alarmListChain = ReadOnlyChainFactory.getAlarmListChain();
		alarmListChain.execute(context);
 		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		if (getIsCount() != null) {
			setCount((long) context.get(FacilioConstants.ContextNames.COUNT));
			System.out.println("data" + getCount());
		}
		else {
			setAlarms((List<AlarmContext>) context.get(FacilioConstants.ContextNames.ALARM_LIST));
		}
		
		FacilioView cv = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		if(cv != null) {
			setViewDisplayName(cv.getDisplayName());
		}
		
		return SUCCESS;
	}
	
	
	private long entityId = -1;
	public long getEntityId() {
		return entityId;
	}
	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}
	
	public String viewAlarm() throws Exception {
		
		FacilioContext context = new FacilioContext();
		if (getViewName() != null) {
			context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		}
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, getId());
 		
 		Chain alarmChain = ReadOnlyChainFactory.getAlarmDetailsChain();
 		alarmChain.execute(context);
 		
		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setAlarms((List<AlarmContext>) context.get(FacilioConstants.ContextNames.ALARM_LIST));
		
		return SUCCESS;
	}
	
	private List<AlarmContext> alarms;
	public List<AlarmContext> getAlarms() {
		return alarms;
	}
	public void setAlarms(List<AlarmContext> alarms) {
		this.alarms = alarms;
	}

	private String viewName = null;
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getModuleLinkName()
	{
		return FacilioConstants.ContextNames.ALARM;
	}

	public ViewLayout getViewlayout()
	{
		return ViewLayout.getViewWorkOrderLayout();
	}

	private String displayName = "All Alarms";
	public String getViewDisplayName() {
		return displayName;
	}
	public void setViewDisplayName(String displayName) {
		this.displayName = displayName;
	}

	String filters;
	public void setFilters(String filters)
	{
		this.filters = filters;
	}
	public String getFilters()
	{
		return this.filters;
	}
	
	private boolean includeParentFilter;
	public boolean getIncludeParentFilter() {
		return includeParentFilter;
	}
	public void setIncludeParentFilter(boolean includeParentFilter) {
		this.includeParentFilter = includeParentFilter;
	}
	
	String orderBy;
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}
	
	public String getOrderBy() {
		return this.orderBy;
	}
	
	String orderType;
	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}
	
	public String getOrderType() {
		return this.orderType;
	}
	
	String search;
	public void setSearch(String search) {
		this.search = search;
	}
	
	public String getSearch() {
		return this.search;
	}
	
	private int page;
	public void setPage(int page) {
		this.page = page;
	}
	
	public int getPage() {
		return this.page;
	}
	
	private int perPage = 20;
	public void setPerPage(int perPage) {
		this.perPage = perPage;
	}
	
	public int getPerPage() {
		return this.perPage;
	}
	
	private String criteriaIds;
	public void setCriteriaIds(String criteriaIds)
	{
		this.criteriaIds = criteriaIds;
	}
	
	public String getCriteriaIds()
	{
		return this.criteriaIds;
	}

	private HashMap<String, Object> notification;

	public HashMap<String, Object> getNotification() {
		return notification;
	}
	public void setNotification(HashMap<String, Object> notification) {
		this.notification = notification;
	}

	private List<HashMap<String, Object>> followers;
	public void setFollowers(List<HashMap<String, Object>> followers) {
		this.followers = followers;
	}
	
	public List<HashMap<String, Object>> getFollowers() {
		return this.followers;
	}
	
	public String notifyUser() throws Exception {

		HashMap<String, Object> notificationObj = getNotification();

		List<HashMap<String, Object>> toList = (List<HashMap<String, Object>>) notificationObj.get("to");
		String message = (String) notificationObj.get("message");
		
		long alarmId = getAlarm().getId();

		List<HashMap<String, Object>> followers = new ArrayList<>();
		
		for (HashMap<String, Object> to : toList) {
			String type = (String) to.get("type");
			String value = (String) to.get("value");
			if ("email".equalsIgnoreCase(type)) {
				Map<String, Object> placeHolders = new HashMap<>();
				CommonCommandUtil.appendModuleNameInKey(FacilioConstants.ContextNames.ALARM, FacilioConstants.ContextNames.ALARM, FieldUtil.getAsProperties(alarm), placeHolders);
				CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
				CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(AccountUtil.getCurrentUser()), placeHolders);
				
				placeHolders.put("follower.email", value);
				JSONObject mailJson = TemplateAPI.getDefaultTemplate(5).getTemplate(placeHolders); //Default template id of ALARM_CREATION_EMAIL is 5
				
				if(message != null && !message.isEmpty()) {
					String body = (String) mailJson.get("message");
					mailJson.put("message", body+"\n\n"+message);
				}
				
				mailJson.put("to", value);
				AwsUtil.sendEmail(mailJson);
			}
			else if ("mobile".equalsIgnoreCase(type)) {
				value = AlarmAPI.sendAlarmSMS(alarm, value, message);
				to.put("value", value);
			}
			
			if (value != null) {
				HashMap<String, Object> follower = getAlarmFollower(alarmId, value); 
				if (follower == null) {
					follower = addAlarmFollower(alarmId, to);
				}
				followers.add(follower);
			}
		}
		setFollowers(followers);

		return SUCCESS;
	}

	public HashMap<String, Object> getAlarmFollower(long alarmId, String follower) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT * FROM AlarmFollowers WHERE ALARM_ID=? AND FOLLOWER=?");
			pstmt.setLong(1, alarmId);
			pstmt.setString(2, follower);
			
			rs = pstmt.executeQuery();
			if (rs.next()) {
				HashMap<String, Object> hm = new HashMap<String, Object>();
				hm.put("id", rs.getLong("ID"));
				hm.put("type", rs.getString("FOLLOWER_TYPE"));
				hm.put("value", rs.getString("FOLLOWER"));
				return hm;
			}
			return null;
		}
		catch(SQLException | RuntimeException e) 
		{
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public HashMap<String, Object> addAlarmFollower(long alarmId, HashMap<String, Object> follower) throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try
		{
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("INSERT INTO AlarmFollowers SET ALARM_ID=?, FOLLOWER_TYPE=?, FOLLOWER=?", Statement.RETURN_GENERATED_KEYS);
			pstmt.setLong(1, alarmId);
			pstmt.setString(2, follower.get("type").toString());
			pstmt.setString(3, follower.get("value").toString());
			
			pstmt.executeUpdate();
			rs = pstmt.getGeneratedKeys();
			rs.next();
			long id = rs.getLong(1);
			follower.put("id", id);
			return follower;
		}
		catch(SQLException | RuntimeException e) 
		{
			throw e;
		}
		finally 
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	private long workorderId;
	public void setWorkorderId(long workorderId) {
		this.workorderId = workorderId;
	}
	
	public long getWorkorderId() {
		return this.workorderId;
	}
	
	public String viewWorkorder() throws Exception {
		
		setWorkorderId(getAlarmWorkOrderId(this.id.get(0)));
		return SUCCESS;
	}
	
	private long getAlarmWorkOrderId(long ticketId) throws Exception {
		
		FacilioField idFld = new FacilioField();
		idFld.setName("ID");
		idFld.setColumnName("ID");
		idFld.setModule(ModuleFactory.getWorkOrdersModule());
		idFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(idFld);
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		try {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.select(fields)
					.table("WorkOrders")
					.innerJoin("Tickets")
					.on("WorkOrders.TICKET_ID = Tickets.ID")
					.andCustomWhere("WorkOrders.ORGID=? AND Tickets.ORGID = ? AND Tickets.ID=?", orgId, orgId, ticketId);

			List<Map<String, Object>> rs = builder.get();
			if (rs != null && !rs.isEmpty()) {
				return (Long) rs.get(0).get("ID");
			}
			return -1;
		}
		catch(Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
	}
	
	private List<ReadingAlarmContext> readingAlarms;
	public List<ReadingAlarmContext> getReadingAlarms() {
		return readingAlarms;
	}
	public void setReadingAlarms(List<ReadingAlarmContext> readingAlarms) {
		this.readingAlarms = readingAlarms;
	}
	
	public String fetchReadingAlarms() throws Exception {
		FacilioContext context = constructChainContext();
		Chain getReadingAlarmsChain = FacilioChainFactory.getReadingAlarmsChain();
		getReadingAlarmsChain.execute(context);
		readingAlarms = (List<ReadingAlarmContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		return SUCCESS;
	}
	
	/******************      V2 Api    ******************/
	public String v2alarmList() throws Exception{
		alarmList();
		setResult(FacilioConstants.ContextNames.ALARM_LIST, getAlarm());
		setResult(FacilioConstants.ContextNames.COUNT, getCount());
		return SUCCESS;
		
	}
	
	public String v2viewAlarm() throws Exception{
		viewAlarm();
		setResult(FacilioConstants.ContextNames.ALARM, alarms.get(0));
		return SUCCESS;
	}
	
	public String v2updateStatus() throws Exception{
		updateStatus();
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		return SUCCESS;
		
	}
	
	public String v2deleteAlarm() throws Exception{
		deleteAlarm();
		setResult(FacilioConstants.ContextNames.ROWS_UPDATED, rowsUpdated);
		return SUCCESS;
	}
	
	public String v2createWorkorder() throws Exception{
		List<Long> ids = new ArrayList<>();
		ids.add(alarm.getId());
		setId(ids);
		
		createWorkorder();
		
		setResult("workorderId", woId);
		return SUCCESS;
	}
}
