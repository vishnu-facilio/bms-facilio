package com.facilio.beans;

import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.bmsconsole.context.AlarmContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.events.context.EventRuleContext;

public interface ModuleCRUDBean {
	public long addWorkOrder(WorkOrderContext workorder) throws Exception;
	
	public long addWorkOrderRequest(WorkOrderRequestContext workOrderRequest) throws Exception;
	
	public AlarmContext processAlarm(JSONObject alarmInfo) throws Exception;
	
	public int deleteAlarm(List<Long> id) throws Exception;
	
	public WorkOrderContext CloseAllWorkOrder() throws Exception;
	
	public List<Map<String, Object>> CopyPlannedMaintenance() throws Exception;
	
	public PreventiveMaintenance CopyWritePlannedMaintenance(List<Map<String, Object>> props) throws Exception;
	
	public int updateAlarm(AlarmContext alarm, List<Long> ids) throws Exception;
	
	public int updateAlarmFromJson(JSONObject alarmInfo, List<Long> ids) throws Exception;
	
	public int updateAlarmPriority(String priority, List<Long> ids) throws Exception;
	
	public int updateAlarmResource(long assetId, String source) throws Exception;
	
	public WorkOrderContext addWorkOrderFromPM(PreventiveMaintenance pm) throws Exception;
	
	public WorkOrderContext addWorkOrderFromPM(PreventiveMaintenance pm, long templateId) throws Exception;
	
	public void deleteAllData (String moduleName) throws Exception;
	
	public long processEvents(long timeStamp, JSONObject payLoad, List<EventRuleContext> eventRules, 
			Map<String, Integer> eventCountMap, long lastEventTime) throws Exception ;
	
	public void processTimeSeries(long timeStamp, JSONObject payLoad, Record record, 
			IRecordProcessorCheckpointer checkpointer) throws Exception;
	
}
