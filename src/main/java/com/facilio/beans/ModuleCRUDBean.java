package com.facilio.beans;

import java.util.List;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;

public interface ModuleCRUDBean {
	public long addWorkOrder(WorkOrderContext workorder) throws Exception;
	
	public long addWorkOrderRequest(WorkOrderRequestContext workOrderRequest) throws Exception;
	
	public long addAlarm(AlarmContext alarm) throws Exception;
	
	public int deleteAlarm(List<Long> id) throws Exception;
	
	public int updateAlarm(AlarmContext alarm, List<Long> ids) throws Exception;
	
	public int updateAlarmPriority(String priority, List<Long> ids) throws Exception;
	
	public int updateAlarmResource(long assetId, String node) throws Exception;
	
	public WorkOrderContext addWorkOrderFromPM(PreventiveMaintenance pm) throws Exception;
	
	public WorkOrderContext addWorkOrderFromPM(PreventiveMaintenance pm, long templateId) throws Exception;
}
