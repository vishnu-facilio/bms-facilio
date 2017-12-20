package com.facilio.beans;

import java.util.List;

import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;

public interface ModuleCRUDBean {
	public long addWorkOrder(WorkOrderContext workorder) throws Exception;
	
	public long addWorkOrderRequest(WorkOrderRequestContext workOrderRequest) throws Exception;
	
	public long addAlarm(AlarmContext alarm) throws Exception;
	
	public int deleteAlarm(List<Long> id) throws Exception;
	
	public int updateAlarm(AlarmContext alarm, List<Long> ids) throws Exception;
	
	public int updateAlarmPriority(String priority, List<Long> ids) throws Exception;
	
	public int updateAlarmAsset(long assetId, String node) throws Exception;
	
	public long addWorkOrderFromPM(long pmId) throws Exception;
}
