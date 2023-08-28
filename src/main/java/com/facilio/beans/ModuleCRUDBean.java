package com.facilio.beans;

import java.io.File;
import java.io.InputStream;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.context.*;
import com.facilio.ims.handler.AuditLogHandler;
import com.facilio.flowLog.moduleFlowLog.context.FlowExecutionLogContext;
import org.apache.commons.chain.Context;
import org.apache.commons.mail.util.MimeMessageParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.agent.AgentType;
import com.facilio.agent.fw.constants.Status;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.metrics.AgentMetrics;
import com.facilio.agentv2.point.Point;
import com.facilio.chain.FacilioContext;
import com.facilio.db.criteria.Criteria;
import com.facilio.events.context.EventRuleContext;
import com.facilio.fs.FileInfo;
import com.facilio.modules.FacilioModule;
import com.facilio.plannedmaintenance.PlannedMaintenanceAPI;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.workflows.context.WorkflowContext;

import javax.mail.internet.MimeMessage;

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
	
	public void processEvents(long timeStamp, JSONArray payLoad, List<EventRuleContext> eventRules) throws Exception ;
	
	public void processTimeSeries(long timeStamp, JSONObject payLoad, FacilioRecord record,
			 boolean adjustTime) throws Exception;

	public void processTimeSeries(FacilioRecord record) throws Exception;

    /*public void processTimeSeries(long timeStamp, JSONObject payLoad, Record record,
                                  IRecordProcessorCheckpointer checkpointer, boolean adjustTime) throws Exception;

    public void processTimeSeries(FacilioConsumer consumer, FacilioRecord record) throws Exception;*/

	public List<EventRuleContext> getActiveEventRules() throws Exception;

	public ControllerContext getController(String deviceName, String deviceId, Long agentId) throws Exception;

	public ControllerContext addController(ControllerContext controllerContext) throws Exception;

	public void acknowledgePublishedMessage(long id, String message, JSONObject payLoad) throws Exception;

	public void acknowledgeNewPublishedMessage(long id, Status status) throws Exception;

	public long addDeviceId(String deviceId) throws Exception;

	public Map<String, Long> getDeviceMap() throws Exception;

	public List<Map<String, Object>> getAgentDataMap(String agentName, AgentType type) throws Exception;

	public Long addLog(Map<String, Object> logData) throws Exception;

	public int updateAgentMetrics(Map<String, Object> metrics, Criteria criteria) throws Exception;

	public long insertAgentMetrics(Map<String, Object> metrics) throws Exception;

	public List<Map<String, Object>> getMetrics(Long agentId, Integer publishType, Long createdTime) throws Exception;

	public Long addAgentMessage(Map<String, Object> map) throws Exception;

	public Long updateAgentMessage(Map<String, Object> map) throws Exception;

	public List<Map<String, Object>> getRows(FacilioContext context) throws Exception;

	public Integer updateTable(FacilioContext context) throws Exception;

	public Integer deleteFromDb(FacilioContext context) throws Exception;

	public List<AssetCategoryContext> getCategoryList() throws Exception;

	public List<AssetContext> getAssetListOfCategory(long category) throws Exception;

	public List<FacilioModule> getAssetReadings(long parentCategoryId) throws Exception;

	public  void  readingTools(long orgId, long fieldId, long assetId, long startTtime, long endTtime, String email, long selectfields)throws Exception;

	public void deleteReadings(long orgId, long fieldId, long assetId, long startTtime, long endTtime, long categoryId, long moduleId) throws Exception;

	public void moveReadings(long orgId, long fieldId, long assetId, long startTtime, long endTtime, long categoryId, long duration, long type) throws Exception;

	public void readingFieldsMigration(long orgId,long sourceFieldId,long assetId, long assetCategoryId,long targetFieldId) throws Exception;

	public void updatePMJob(List<WorkOrderContext> workorders) throws Exception;

    public Long addAgentController(Controller controller) throws  Exception;

	public Long addChildController(Controller controller) throws Exception;

	public Long genericInsert(Context context) throws IllegalAccessException, InstantiationException, Exception;

	public void processNewTimeSeries(JSONObject payload,Controller controller) throws Exception;

	//public List<Map<String, Object>> getIntegration() throws Exception;

	public boolean addPoint(Point point) throws Exception;
	
	public FileInfo getFile(long fileId) throws Exception;
	
	public void demoOneTimeJob(long orgId, ZonedDateTime currentZdt) throws Exception;

	public boolean addMetrics(AgentMetrics toInsertMap) throws Exception;

	public boolean updateMetrics(Map<String, Object> toUpdate, long metricsId) throws Exception;

	public List<Map<String,Object>> getOrgSpecificAgentList() throws Exception;
	
	public void disableOrEnableAgent(long agentId, boolean disable) throws Exception;
	
	public void addOrUpdateWeatherData(Map<Long, List<Long>> siteAndStationMap, Map<Long, Map<String, Object>> dataMap)throws Exception;
	
	public void addOrUpdateDailyWeatherData(Map<Long, List<Long>> map, Map<Long, Map<String, Object>> dataMap)throws Exception;

    public long getPendingDataCount() throws Exception;

	public List<Map<String,Object>> getMissingData() throws Exception;

	public InputStream getDownloadStream(String nameSpace, long fileId) throws Exception;

	public FileInfo getFileInfo(String nameSpace,long fileId) throws Exception;

	public long addRequestFromEmail(MimeMessage emailMsg, MimeMessageParser parser, SupportEmailContext supportEmail,Long workOrderRequestEmailId) throws Exception;
	
	public Object executeWorkflow(WorkflowContext workflowContext) throws Exception;

	void addAuditLog(AuditLogHandler.AuditLogContext auditLog) throws Exception;
	
	public void initMLService(Map<String,Object> requestJson) throws Exception;

	ApplicationContext getApplicationForLinkName(String appName) throws Exception;

	void schedulePM(long plannerId,PlannedMaintenanceAPI.ScheduleOperation operation) throws Exception;

	//void extendPlanner(long plannerId, Duration duration) throws Exception;
	void addWorkflowRuleLog(WorkflowRuleLogContext workflowRuleLog, List<WorkflowRuleActionLogContext> workflowRuleActionLogList) throws Exception;
	void addFlowExecutionLog(FlowExecutionLogContext flowExecutionLog) throws Exception;

}
