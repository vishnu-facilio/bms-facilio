package com.facilio.bmsconsole.actions;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.fw.BeanFactory;
import com.opensymphony.xwork2.ActionSupport;

public class InternalActions extends ActionSupport {
	//All actions in this should be done via a bean
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String addAlarmFromEvent() throws Exception {
		
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", (Long) alarmInfo.get("orgId"));
		alarm = bean.processAlarm(alarmInfo);
		alarmId = alarm.getId();
		
		return SUCCESS;
	}
	
	public static String CloseAllWorkOrder(long orgId) throws Exception {
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
	    bean.CloseAllWorkOrder();
		return SUCCESS;
	}
	
	public static String CopyWritePlannedMaintenance(long newOrgId, List<Map<String, Object>> props) throws Exception {
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", newOrgId);
	    bean.CopyWritePlannedMaintenance(props);
	    
		return SUCCESS;
	}
	public static String CopyPlannedMaintenance(long orgId, long newOrgId) throws Exception {
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
		List<Map<String, Object>> props =  bean.CopyPlannedMaintenance();
	    CopyWritePlannedMaintenance(newOrgId, props);
		
	    return SUCCESS;
	}
	
	public String deleteAlarm() throws Exception {
		
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
		rowsUpdated = bean.deleteAlarm(id);
		return SUCCESS;
	}
	
	public String updateAlarmFromEvent() throws Exception {
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", (Long) alarmInfo.get("orgId"));
		rowsUpdated = bean.updateAlarmFromJson(alarmInfo, id);
		return SUCCESS;
	}
	
	
	
	private List<Long> id;
	public List<Long> getId() {
		return id;
	}
	public void setId(List<Long> id) {
		this.id = id;
	}
	
	private String priority;
	public String getPriority() {
		return priority;
	}

	public void setPriority(String priority) {
		this.priority = priority;
	}
	
	private String source;
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

	private long resourceId = -1;
	public long getResourceId() {
		return resourceId;
	}
	public void setResourceId(long resourceId) {
		this.resourceId = resourceId;
	}
	
	private int rowsUpdated;
	public int getRowsUpdated() {
		return rowsUpdated;
	}
	public void setRowsUpdated(int rowsUpdated) {
		this.rowsUpdated = rowsUpdated;
	}
	
	private AlarmContext alarm;
	public AlarmContext getAlarm() {
		return alarm;
	}
	public void setAlarm(AlarmContext alarm) {
		this.alarm = alarm;
	}
	
	private long alarmId = -1;
	public long getAlarmId() {
		return alarmId;
	}
	public void setAlarmId(long alarmId) {
		this.alarmId = alarmId;
	}
	
	private JSONObject alarmInfo;
	public JSONObject getAlarmInfo() {
		return alarmInfo;
	}
	public void setAlarmInfo(JSONObject alarmInfo) {
		this.alarmInfo = alarmInfo;
	}

	private long orgId = -1;
	public long getOrgId() {
		return orgId;
	}
	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}
}
