package com.facilio.bmsconsole.actions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.device.Device;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.bmsconsole.util.SpaceAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.DefaultTemplates;
import com.facilio.bmsconsole.workflow.EventContext.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.fw.UserInfo;
import com.facilio.sql.DBUtil;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;
import com.opensymphony.xwork2.ActionSupport;
import com.twilio.sdk.Twilio;

public class AlarmAction extends ActionSupport {
	public String addAlarm() throws Exception {
		getAlarmFromParams(alarm);
		if(alarm != null) {
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", alarm.getOrgId());
			setAlarmId(bean.addAlarm(alarm));
		}
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
		alarm.setStatus(AlarmContext.AlarmStatus.ACTIVE);
		alarm.setOrgId(OrgInfo.getCurrentOrgInfo().getOrgid());
		alarm.getTicket().setSourceType(TicketContext.SourceType.ALARM);
		alarm.setIsAcknowledged(false);

		if(alarm.getDeviceId() == -1)
		{
			Long deviceId = DeviceAPI.addDevice(alarm.getTicket().getSpace().getName(), alarm.getTicket().getSpace().getId());
			alarm.setDeviceId(deviceId);
		}
		else
		{
			Long spaceId = DeviceAPI.getDevice(alarm.getDeviceId()).getSpaceId();
			
			BaseSpaceContext space = new BaseSpaceContext();
			space.setId(spaceId);
			alarm.getTicket().setSpace(space);
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

	private TicketContext ticket;
	public TicketContext getTicket() {
		return ticket;
	}
	public void setTicket(TicketContext ticket) {
		this.ticket = ticket;
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
		context.put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.ASSIGN_TICKET);
		return updateAlarm(context);
	}

	public String updateStatus() throws Exception {
		FacilioContext context = new FacilioContext();
		return updateAlarm(context);
	}

	private String updateAlarm(FacilioContext context) throws Exception {
		//		System.out.println(id);
		//		System.out.println(alarm);
		if (ticket != null) {
			alarm.setTicket(ticket);
		}
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
		String alarmSubject = getAlarm().getTicket().getSubject();
		String alarmType = getAlarm().getTypeVal();

		List<HashMap<String, Object>> followers = new ArrayList<>();
		
		for (HashMap<String, Object> to : toList) {
			String type = (String) to.get("type");
			String value = (String) to.get("value");
			Device device = DeviceAPI.getDevice(alarm.getDeviceId());
			BaseSpaceContext space = getSpace(device.getSpaceId());
			if ("email".equalsIgnoreCase(type)) {
				Map<String, Object> placeHolders = new HashMap<>();
				CommonCommandUtil.appendModuleNameInKey(FacilioConstants.ContextNames.ALARM, FacilioConstants.ContextNames.ALARM, FieldUtil.getAsProperties(alarm), placeHolders);
				CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(OrgInfo.getCurrentOrgInfo()), placeHolders);
				CommonCommandUtil.appendModuleNameInKey(null, "user", FieldUtil.getAsProperties(UserInfo.getCurrentUser()), placeHolders);
				
				placeHolders.put("follower.email", value);
				JSONObject mailJson = DefaultTemplates.ALARM_CREATION_EMAIL.getTemplate(placeHolders);
				
				if(message != null && !message.isEmpty()) {
					String body = (String) mailJson.get("message");
					mailJson.put("message", body+"\n\n"+message);
				}
				
				AwsUtil.sendEmail(mailJson);
			}
			else if ("mobile".equalsIgnoreCase(type)) {
				String sms = null;
				if(message != null && !message.isEmpty()) {
					sms = MessageFormat.format("[ALARM] [{0}] {1} in {2} - {3}", alarmType, alarmSubject, space.getName(), message);
				}
				else {
					sms = MessageFormat.format("[ALARM] [{0}] {1} in {2}", alarmType, alarmSubject, space.getName());
				}
				value = sendSMS(value, sms);
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

	private BaseSpaceContext getSpace(long id) throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			BaseSpaceContext space = SpaceAPI.getBaseSpace(id, OrgInfo.getCurrentOrgInfo().getOrgid(), conn);
			return space;
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private static final String ACCOUNTS_ID = "AC49fd18185d9f484739aa73b648ba2090"; // Your Account SID from www.twilio.com/user/account
	private static final String AUTH_TOKEN = "3683aa0033af81877501961dc886a52b"; // Your Auth Token from www.twilio.com/user/account
	public String sendSMS(String to, String message) {

		try {

			Twilio.init(ACCOUNTS_ID, AUTH_TOKEN);

			com.twilio.sdk.resource.api.v2010.account.Message tmessage = com.twilio.sdk.resource.api.v2010.account.Message.create(ACCOUNTS_ID,
					new com.twilio.sdk.type.PhoneNumber(to),  // To number
					new com.twilio.sdk.type.PhoneNumber("+16106248741"),  // From number
					message                    // SMS body
					).execute();


			//com.twilio.sdk.resource.lookups.v1.PhoneNumber
			//	com.twilio.sdk.resource.api.v2010.account.Message.create(accountSid, to, from, mediaUrl)
			System.out.println(tmessage.getSid());
			System.out.println(tmessage.getTo());

			return tmessage.getTo();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
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
		idFld.setModuleTableName("WorkOrders");
		idFld.setDataType(FieldType.NUMBER);

		List<FacilioField> fields = new ArrayList<>();
		fields.add(idFld);
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
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
