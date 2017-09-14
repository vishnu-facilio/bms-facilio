package com.facilio.bmsconsole.actions;

import org.json.simple.JSONObject;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.TicketContext;
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
		ticket.setScheduledStart(System.currentTimeMillis());
		alarm.setTicket(ticket);
		
		return alarm;
	}
}
