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
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.AssetContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.ActivityType;
import com.facilio.bmsconsole.workflow.DefaultTemplates;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.DBUtil;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;
import com.opensymphony.xwork2.ActionSupport;

public class AlarmAction extends ActionSupport {
	public String addAlarm() throws Exception {
		getAlarmFromParams(alarm);
		if(alarm != null) {
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", alarm.getOrgId());
			setAlarmId(bean.addAlarm(alarm));
		}
		return SUCCESS;
	}
	
	public String addAlarmFromEvent() throws Exception {
		
		if(alarm != null) {
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", alarm.getOrgId());
			setAlarmId(bean.addAlarm(alarm));
		}
		return SUCCESS;
	}
	
	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	private long alarmTemplateId = -1;
	public long getAlarmTemplateId() {
		return alarmTemplateId;
	}
	public void setAlarmTemplateId(long alarmTemplateId) {
		this.alarmTemplateId = alarmTemplateId;
	}
	
	public String addWorkOrderTemplate() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.ALARM, alarm);
		
		Chain addTemplate = FacilioChainFactory.getAddAlarmTemplateChain();
		addTemplate.execute(context);
		setAlarmTemplateId((long) context.get(FacilioConstants.ContextNames.RECORD_ID));
		
		return SUCCESS;
	}

	private long alarmId = -1;
	public long getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(long alarmId) {
		this.alarmId = alarmId;
	}

	private JSONObject alarmParams;
	public JSONObject getAlarmParams() {
		return alarmParams;
	}
	public void setAlarmParams(JSONObject alarmParams) {
		this.alarmParams = alarmParams;
	}
	private AlarmContext getAlarmFromParams(AlarmContext alarm) throws Exception {
		//Process alarm params
		if (alarm == null) {
			alarm = new AlarmContext();
			alarm.setType(AlarmContext.AlarmType.MAINTENANCE);

			TicketContext ticket = new TicketContext();
			ticket.setSubject("Alarm "+Math.round(Math.random()*100));
			ticket.setDescription("Alarm Testing");
		}
		alarm.setAlarmStatus(AlarmContext.AlarmStatus.ACTIVE);
		alarm.setOrgId(AccountUtil.getCurrentOrg().getOrgId());
		alarm.setSourceType(TicketContext.SourceType.ALARM);
		alarm.setIsAcknowledged(false);
		if(alarm.getAsset() != null) {
			AssetContext asset = AssetsAPI.getAssetInfo(alarm.getAsset().getId());
			BaseSpaceContext space = asset.getSpace();
			alarm.setSpace(space);
			alarm.setAsset(asset);
		}
		return alarm;
	}

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

	public String assignAlarm() throws Exception {
		FacilioContext context = new FacilioContext();
		//set Event
		context.put(FacilioConstants.ContextNames.ACTIVITY_TYPE, ActivityType.ASSIGN_TICKET);
		return updateAlarm(context);
	}

	public String updateStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		return updateAlarm(context);
	}
	
	public String updateAlarmPriority() throws Exception {
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
		rowsUpdated = bean.updateAlarmPriority(priority, id);
		return SUCCESS;
	}
	
	public String updateAlarmAsset() throws Exception {
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
		rowsUpdated = bean.updateAlarmAsset(assetId, node);
		return SUCCESS;
	}
	
	public String deleteAlarm() throws Exception {
		
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
		rowsUpdated = bean.deleteAlarm(id);
		return SUCCESS;
	}
	
	private long assetId = -1;
	public long getAssetId() {
		return assetId;
	}
	public void setAssetId(long assetId) {
		this.assetId = assetId;
	}
	
	private String node;
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}

	private String priority;
	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	public String updateAlarmFromEvent() throws Exception {
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", alarm.getOrgId());
		rowsUpdated = bean.updateAlarm(alarm, id);
		return SUCCESS;
	}

	private String updateAlarm(FacilioContext context) throws Exception {
		//		System.out.println(id);
		//		System.out.println(alarm);
		context.put(FacilioConstants.ContextNames.ALARM, alarm);
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, id);

		Chain updateAlarm = FacilioChainFactory.getUpdateAlarmChain();
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

	private int rowsUpdated;
	public int getRowsUpdated() {
		return rowsUpdated;
	}
	public void setRowsUpdated(int rowsUpdated) {
		this.rowsUpdated = rowsUpdated;
	}

	public String alarmList() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CV_NAME, getViewName());
		if(getFilters() != null)
		{	
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(getFilters());
			context.put(FacilioConstants.ContextNames.FILTERS, json);
		}
		System.out.println("View Name : "+getViewName());
		Chain alarmListChain = FacilioChainFactory.getAlarmListChain();
		alarmListChain.execute(context);

		setModuleName((String) context.get(FacilioConstants.ContextNames.MODULE_DISPLAY_NAME));
		setAlarms((List<AlarmContext>) context.get(FacilioConstants.ContextNames.ALARM_LIST));

		FacilioView cv = (FacilioView) context.get(FacilioConstants.ContextNames.CUSTOM_VIEW);
		if(cv != null) {
			setViewDisplayName(cv.getDisplayName());
		}

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
				JSONObject mailJson = DefaultTemplates.ALARM_CREATION_EMAIL.getTemplate(placeHolders);
				
				if(message != null && !message.isEmpty()) {
					String body = (String) mailJson.get("message");
					mailJson.put("message", body+"\n\n"+message);
				}
				
				mailJson.put("to", value);
				AwsUtil.sendEmail(mailJson);
			}
			else if ("mobile".equalsIgnoreCase(type)) {
				value = CommonCommandUtil.sendAlarmSMS(alarm, value, message);
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
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.connection(conn)
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
			e.printStackTrace();
			throw e;
		}
	}
}
