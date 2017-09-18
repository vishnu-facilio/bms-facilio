package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.ViewLayout;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.workflow.EventContext.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.opensymphony.xwork2.ActionSupport;

public class AlarmAction extends ActionSupport {
	public String addAlarm() throws Exception {
		AlarmContext alarm = getAlarmFromParams();
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
	
	private AlarmContext getAlarmFromParams() {
		//Process alarm params
		AlarmContext alarm = new AlarmContext();
		alarm.setStatus(AlarmContext.AlarmStatus.ACTIVE);
		alarm.setType(AlarmContext.AlarmType.MAINTENANCE);
		alarm.setOrgId(1);
		TicketContext ticket = new TicketContext();
		ticket.setSubject("Alarm "+Math.round(Math.random()*100));
		ticket.setDescription("ddd");
		ticket.setSourceType(TicketContext.SourceType.ALARM);
//		ticket.setScheduledStart(System.currentTimeMillis());
		alarm.setTicket(ticket);
		
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
	
	private String updateAlarm(FacilioContext context) throws Exception {
//		System.out.println(id);
//		System.out.println(alarm);
		alarm.setTicket(ticket);
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
}
