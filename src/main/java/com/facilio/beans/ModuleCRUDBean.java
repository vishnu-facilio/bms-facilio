package com.facilio.beans;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.agent.AgentType;
import com.facilio.agent.fw.constants.Status;
import com.facilio.agentnew.controller.Controller;
import com.facilio.agentnew.point.Point;
import com.facilio.bmsconsole.context.*;
import com.facilio.chain.FacilioContext;
import com.facilio.events.context.EventRuleContext;
import com.facilio.modules.FacilioModule;
import com.facilio.procon.consumer.FacilioConsumer;
import com.facilio.procon.message.FacilioRecord;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface ModuleCRUDBean {
	
	public long addWorkOrderRequest(WorkOrderRequestContext workOrderRequest, List<File> attachedFiles,
			List<String> attachedFileNames, List<String> attachedFilesContentType) throws Exception;
	public long addWorkOrderFromEmail(WorkOrderContext workOrder, List<File> attachedFiles,
			List<String> attachedFileNames, List<String> attachedFilesContentType) throws Exception;
	
	public AlarmContext processAlarm(JSONObject alarmInfo) throws Exception;
	
	public Boolean isFeatureEnabled(FeatureLicense license) throws Exception;
	
	public int deleteAlarm(List<Long> id) throws Exception;
	
	public WorkOrderContext CloseAllWorkOrder() throws Exception;
	
	public List<Map<String, Object>> CopyPlannedMaintenance() throws Exception;
	
	public PreventiveMaintenance CopyWritePlannedMaintenance(List<Map<String, Object>> props) throws Exception;
	
	public int updateAlarmFromJson(JSONObject alarmInfo, List<Long> ids) throws Exception;
	
	public List<WorkOrderContext> addWorkOrderFromPM(Context context, PreventiveMaintenance pm) throws Exception;
	
	public List<WorkOrderContext> addWorkOrderFromPM(Context context, PreventiveMaintenance pm, long templateId) throws Exception;
	
	public void deleteAllData (String moduleName) throws Exception;
	
	public long processEvents(long timeStamp, JSONObject payLoad, List<EventRuleContext> eventRules, 
			Map<String, Integer> eventCountMap, long lastEventTime, String partitionKey) throws Exception ;
	
	public void processTimeSeries(long timeStamp, JSONObject payLoad, Record record, 
			IRecordProcessorCheckpointer checkpointer, boolean adjustTime) throws Exception;
	
	public void processTimeSeries(FacilioConsumer consumer, FacilioRecord record) throws Exception;
	
	public List<EventRuleContext> getActiveEventRules() throws Exception;

	public ControllerContext getController(String deviceName, String deviceId,Long agentId) throws Exception;
	
	public ControllerContext addController(ControllerContext controllerContext) throws Exception;
	
	public void acknowledgePublishedMessage (long id, String message, JSONObject payLoad) throws Exception;

	public void acknowledgeNewPublishedMessage (long id, Status status, JSONObject payLoad) throws Exception;

	public long addDeviceId (String deviceId) throws Exception;
	
	public Map<String, Long> getDeviceMap() throws Exception;

	public  List<Map<String,Object>> getAgentDataMap(String agentName, AgentType type) throws Exception;

	public Long addLog(Map<String,Object> logData) throws Exception;

	public int updateAgentMetrics(Map<String,Object> metrics, Map<String,Object> criteria) throws Exception;

	public long insertAgentMetrics(Map<String,Object> metrics) throws Exception;

	public List<Map<String, Object>> getMetrics(Long agentId, Integer publishType, Long createdTime) throws Exception;

	public Long addAgentMessage(Map<String,Object> map)throws Exception;

	public Long updateAgentMessage(Map<String,Object> map)throws Exception;

	public List<Map<String,Object>> getRows(FacilioContext context) throws Exception;

	public Integer updateTable(FacilioContext context) throws Exception;

	public Integer deleteFromDb(FacilioContext context) throws Exception;
	
	public  List<AssetCategoryContext> getCategoryList() throws Exception ;
	
	public  List<AssetContext> getAssetListOfCategory(long category) throws Exception;
	
	public List<FacilioModule> getAssetReadings(long parentCategoryId) throws Exception;

	public  void  readingTools(long orgId, long fieldId, long assetId, long startTtime, long endTtime, String email, long selectfields)throws Exception;
	
	public void updatePMJob(List<WorkOrderContext> workorders) throws Exception;

    public Long addAgentController(Controller controller) throws  Exception;

	public Long addChildController(Controller controller) throws Exception;

	public Long genericInsert(Context context) throws IllegalAccessException, InstantiationException, Exception;

	public void processNewTimeSeries(JSONObject payload,Controller controller) throws Exception;

	//public List<Map<String, Object>> getIntegration() throws Exception;

	public boolean addPoint(Point point) throws Exception;
}
