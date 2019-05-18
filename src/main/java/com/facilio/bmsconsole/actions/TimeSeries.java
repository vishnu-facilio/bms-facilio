package com.facilio.bmsconsole.actions;

import java.io.BufferedReader;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.PublishData;
import com.facilio.bmsconsole.context.PublishMessage;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.instant.jobs.PublishedDataCheckerJob;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.bmsconsole.util.IoTMessageAPI.IotCommandType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.timeseries.TimeSeriesAPI;
import com.facilio.wms.message.Message;
import com.facilio.wms.message.MessageType;
import com.facilio.wms.message.WmsPublishResponse;
import com.facilio.wms.util.WmsApi;

public class TimeSeries extends FacilioAction {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(TimeSeries.class.getName());
	
	public String testNotification() throws Exception {
		if (type == 1) {
			Message message = new Message(MessageType.BROADCAST);
			message.setNamespace("system");
			message.setAction("reload");
			//message.setEventType(WmsEvent.WmsEventType.RECORD_UPDATE);
			message.addData("time", System.currentTimeMillis());
			message.addData("sound", false);
			WmsApi.broadCastMessage(message);
		}
		else if (type == 2) {
			Message message = new WmsPublishResponse();
			message.setAction("publish");
			message.addData("time", System.currentTimeMillis());
			message.addData("sound", false);
			WmsApi.sendPubSubMessage(Collections.singletonList(userId), message);
		}
		else if (type == 3) {
			
			Message message = new Message();
			if (instanceAssetMap.containsKey("messageType")) {
				message.setMessageType((String) instanceAssetMap.get("messageType"));
			}
			message.setNamespace((String) instanceAssetMap.get("namespace"));
			message.setAction((String) instanceAssetMap.get("action"));
			message.addData("time", System.currentTimeMillis());
			message.addData("sound", false);
			WmsApi.broadCastMessage(message);
		}
		else {
			JSONArray instanceArray = new JSONArray();
			JSONObject obj = new JSONObject();
			obj.put("instance", "instance1");
			obj.put("device", "device1");
			instanceArray.add(obj);
			PublishMessage pubmsg = new PublishMessage();
			JSONObject aa = new JSONObject();
			aa.put("message", "message1");
			pubmsg.setData(aa);
			PublishData data = new PublishData();
			data.setMessages(Collections.singletonList(pubmsg));
			PublishedDataCheckerJob.sendNotification(data, userId);
		}
		return SUCCESS;
	}
	
	private long userId = -1;
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	private int type = -1;
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	public String publish() throws Exception
	{
		if ( getDeviceData() == null) { // added this for altayer emsol also check authentication util
			HttpServletRequest request = ServletActionContext.getRequest();
			BufferedReader reader = request.getReader();
			StringBuilder builder = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line);
			}
			deviceData = builder.toString();
		}

		JSONParser parser = new JSONParser();
		try {
			JSONObject payLoad = (JSONObject) parser.parse(getDeviceData());
			TimeSeriesAPI.processPayLoad(getTimestamp(), payLoad, getMacAddr());
			return SUCCESS;
		}
		catch(Exception e) {
			LOGGER.info("Error while processing data: " + getDeviceData(), e);
		}
		return ERROR;
	}
	
	public String migrateData() throws Exception{
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.DEVICE_LIST , deviceList);
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.KINESIS);
		Chain processDataChain = TransactionChainFactory.getProcessHistoricalDataChain();
		processDataChain.execute(context);
		
		return SUCCESS;
	}
	
	public String markedReadings() throws Exception{
		
		List<Map<String,Object>> markedReadings=TimeSeriesAPI.getMarkedReadings(getCriteria());
		setMarkedData(markedReadings);
		return SUCCESS;
	}
	
	private List<Map<String, Object>> markedData;
	public List<Map<String, Object>> getMarkedData() {
		return this.markedData;
	}
	
	public void setMarkedData(List<Map<String, Object>> markedData) {
		this.markedData = markedData;
	}
	
	public String deviceList() throws Exception{
		setAllDevices(TimeSeriesAPI.getAllDevices());
		return SUCCESS;
	}
	
	@SuppressWarnings("unchecked")
	public String mapInstanceAsset() throws Exception
	{
		Map<String,Object> object = getInstanceAssetMap();
		String deviceName = (String) object.get("deviceName");
		long assetId = (long) object.get("assetId");
		Long controllerId=(Long)object.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		Map<String,Long> instanceFieldMap = (Map<String, Long>) object.get("instanceFieldMap");
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.DEVICE_DATA , deviceName);
		context.put(FacilioConstants.ContextNames.ASSET_ID, assetId);
		context.put(FacilioConstants.ContextNames.RECORD_MAP, instanceFieldMap);
		
		if(controllerId!=null) {
			context.put(FacilioConstants.ContextNames.CONTROLLER_ID, controllerId);
		}
		
		Chain mappingChain = TransactionChainFactory.getInstanceAssetMappingChain();
		mappingChain.execute(context);
		
		if (doMigration) {
			setDeviceList(Collections.singletonList(deviceName));
			migrateData();
		}
		
		return SUCCESS;
	}
	
	public String mapInstance() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.INSTANCE_INFO, instanceAssetMap);
		context.put(FacilioConstants.ContextNames.CONTROLLER_ID, controllerId);
		context.put(FacilioConstants.ContextNames.UNIT_POINTS, unit);
		Chain mappingChain = TransactionChainFactory.getInstanceAssetMappingChain();
		mappingChain.execute(context);
		
		setResult("result", "success");
		return SUCCESS;
	}
	
	public String migrateUnmodelledData() throws Exception {
		TimeSeriesAPI.migrateUnmodelledData(controllerId, instances);
		setResult("result", "success");
		return SUCCESS;
	}

	public String getDefaultFieldMap() throws Exception {
		setFieldMap(TimeSeriesAPI.getDefaultInstanceFieldMap());
		return SUCCESS;
	}
	
	public String getMappedInstances() throws Exception {
		setResult("mappedValues", TimeSeriesAPI.getMappedInstances(controllerId));
		return SUCCESS;
	}
	
	public String getInstancesForController () throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.CONTROLLER_ID, controllerId);
		context.put(FacilioConstants.ContextNames.CONFIGURE, configured);
		context.put(FacilioConstants.ContextNames.SUBSCRIBE, subscribed);
		context.put(FacilioConstants.ContextNames.FETCH_MAPPED, fetchMapped);
		context.put(FacilioConstants.ContextNames.PAGINATION, getPagination());
		context.put(FacilioConstants.ContextNames.COUNT, getShowInstanceCount());
		
		if (!getShowInstanceCount()) {
			context.put(FacilioConstants.ContextNames.FETCH_READING_INPUT_VALUES, true);
		}
		if (getSearch() != null) {
			context.put(FacilioConstants.ContextNames.SEARCH, getSearch());
		}
		Chain chain = ReadOnlyChainFactory.getUnmodelledInstancesForController();
		chain.execute(context);
		
		setResult("instances", context.get(FacilioConstants.ContextNames.INSTANCE_INFO));
		setResult("readings", context.get(FacilioConstants.ContextNames.READING_DATA_META_LIST));
		
		return SUCCESS;
	}
	
	public String configureInstances () throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
		context.put(FacilioConstants.ContextNames.DEVICE_DATA , changedDevices);
		context.put(FacilioConstants.ContextNames.CONFIGURE , configured);
		context.put(FacilioConstants.ContextNames.CONTROLLER_ID , controllerId);
		
		Chain markChain = TransactionChainFactory.getMarkUnmodeledInstanceChain();
		markChain.execute(context);

		setResult("result", "success");
		return SUCCESS;
	}
	
	public String subscribeInstances () throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.INSTANCE_INFO, instances);
		context.put(FacilioConstants.ContextNames.CONTROLLER_ID , controllerId);
		
		Chain markChain = TransactionChainFactory.getSubscribeInstanceChain();
		markChain.execute(context);

		setResult("result", "success");
		return SUCCESS;
	}
	
	public String unsubscribeInstances () throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.UNSUBSCRIBE_IDS, ids);
		context.put(FacilioConstants.ContextNames.CONTROLLER_ID , controllerId);
		
		Chain markChain = TransactionChainFactory.getUnSubscribeInstanceChain();
		markChain.execute(context);

		setResult("result", "success");
		return SUCCESS;
	}
	
	public String discoverInstances () throws Exception {
		
		PublishData data = IoTMessageAPI.publishIotMessage(controllerId, IotCommandType.DISCOVER);

		setResult("data", data);
		return SUCCESS;
	}
	private Integer unit;

	public Integer getUnit() {
		return unit;
	}

	public void setUnit(Integer unit) {
		this.unit = unit;
	}

	private long controllerId;
	public long getControllerId() {
		return controllerId;
	}
	public void setControllerId(long controllerId) {
		this.controllerId = controllerId;
	}
	
	String search;

	public void setSearch(String search) {
		this.search = search;
	}

	public String getSearch() {
		return this.search;
	}
	
	private String macAddr;
	public String getMacAddr() {
		if (macAddr == null && AccountUtil.getCurrentOrg() != null) {
            setMacAddr(AccountUtil.getCurrentOrg().getDomain());
		}
		return macAddr;
	}
	public void setMacAddr(String macAddr) {
		this.macAddr = macAddr;
	}
	
	private Boolean configured;
	public Boolean getConfigured() {
		return configured;
	}
	public void setConfigured(Boolean configured) {
		this.configured = configured;
	}
	
	private Boolean subscribed;
	public Boolean getSubscribed() {
		return subscribed;
	}
	public void setSubscribed(Boolean subscribed) {
		this.subscribed = subscribed;
	}
	
	private Boolean fetchMapped;
	public Boolean getFetchMapped() {
		return fetchMapped;
	}
	public void setFetchMapped(Boolean fetchMapped) {
		this.fetchMapped = fetchMapped;
	}
	
	private List<Long> ids; 
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}
	
	private Map<Long, String> changedDevices;
	public Map<Long, String> getChangedDevices() {
		return changedDevices;
	}
	public void setChangedDevices(Map<Long, String> changedDevices) {
		this.changedDevices = changedDevices;
	}

	private long timestamp=-1;
	public void setTimestamp(long ttime) {
		this.timestamp=ttime;
	}
	public long getTimestamp() {
		return timestamp;
	}
	
	private String deviceData = null;
	public void setDeviceData(String deviceData) {
		this.deviceData = deviceData;
	}
	public String getDeviceData() {
		return deviceData;
	}
	
	private List<String> deviceList = null;
	public void setDeviceList(List<String> deviceList) {
		this.deviceList = deviceList;
	}
	public List<String> getDeviceList() {
		return deviceList;
	}
	
	
	private Map<String,Object> instanceAssetMap = null;
	public Map<String,Object> getInstanceAssetMap() {
		return instanceAssetMap;
	}
	public void setInstanceAssetMap(Map<String,Object> instanceAssetMap) {
		this.instanceAssetMap = instanceAssetMap;		
	}
	
	private List<Map<String,Object>> instances = null;
	public List<Map<String,Object>> getInstances() {
		return instances;
	}
	public void setInstances(List<Map<String,Object>> instances) {
		this.instances = instances;		
	}
	
	private Map<String, List<String>> allDevices = null;
	public Map<String, List<String>> getAllDevices() {
		return allDevices;
	}
	public void setAllDevices(Map<String, List<String>> allDevices) {
		this.allDevices = allDevices;		
	}
	
	private Map<String, Long> fieldMap = null;
	public Map<String, Long> getFieldMap() {
		return fieldMap;
	}
	public void setFieldMap(Map<String, Long> fieldMap) {
		this.fieldMap = fieldMap;		
	}
	
	private boolean doMigration;
	public boolean getDoMigration() {
		return doMigration;
	}
	public void setDoMigration(boolean doMigration) {
		this.doMigration = doMigration;
	}
	
	private List<Long> timeRange;
	public void setTimeRange(List<Long> timeRange) {
		this.timeRange=timeRange;
	}
	public List<Long> getTimeRange(){
		return timeRange;
	}
	
	private List<Long> moduleList;
	public void setModuleList(List<Long> moduleList) {
		this.moduleList=moduleList;
	}
	public List<Long> getModuleList(){
		return moduleList;
	}
	
	private List<Long> resourceList;
	public void setResourceList(List<Long> resourceList) {
		this.resourceList=resourceList;
	}
	public List<Long> getResourceList(){
		return resourceList;
	}
	
	private List<Long> fieldList;
	public void setFieldList(List<Long> fieldList) {
		this.fieldList=fieldList;
	}
	public List<Long> getFieldList(){
		return fieldList;
	}
	
	
	private List<Integer> markTypeList;
	public void setMarkTypeList(List<Integer> markTypeList) {
		this.markTypeList=markTypeList;
	}
	public List<Integer> getMarkTypeList(){
		return markTypeList;
	}
	
	private Criteria getCriteria() {
		return TimeSeriesAPI.getCriteria(timeRange, resourceList, moduleList, fieldList, markTypeList);
		
	}
	private Boolean showInstanceCount;
	public Boolean getShowInstanceCount() {
		if (showInstanceCount == null) {
			showInstanceCount = false;
		}
		return showInstanceCount;
	}

	public void setShowInstanceCount(Boolean showInstanceCount) {
		this.showInstanceCount = showInstanceCount;
	}

	
}