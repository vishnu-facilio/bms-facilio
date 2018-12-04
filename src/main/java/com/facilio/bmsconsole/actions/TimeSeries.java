package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.constants.FacilioConstants;
import com.facilio.timeseries.TimeSeriesAPI;

public class TimeSeries extends FacilioAction {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = LogManager.getLogger(TimeSeries.class.getName());

	
	public String publish() throws Exception
	{
		JSONParser parser = new JSONParser();
		try {
			JSONObject payLoad = (JSONObject) parser.parse(getDeviceData());
			TimeSeriesAPI.processPayLoad(getTimestamp(), payLoad);	
			return SUCCESS;
		}
		catch(Exception e) {
			LOGGER.info("Error while processing data: ", e);
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
		
		Chain mappingChain = TransactionChainFactory.getInstanceAssetMappingChain();
		mappingChain.execute(context);
		
		if (doMigration) {
			String deviceName = (String) instanceAssetMap.get("deviceName");
			setDeviceList(Collections.singletonList(deviceName));
			migrateData();
		}
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
		List<Map<String, Object>> instances = TimeSeriesAPI.getUnmodeledInstancesForController(controllerId, configured, getFetchMapped());
		setResult("instances", instances);
		return SUCCESS;
	}
	
	public String configureInstances () throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, ids);
		context.put(FacilioConstants.ContextNames.DEVICE_DATA , changedDevices);
		context.put(FacilioConstants.ContextNames.CONFIGURE , configured);
		
		Chain markChain = TransactionChainFactory.getMarkUnmodeledInstanceChain();
		markChain.execute(context);

		setResult("result", "success");
		return SUCCESS;
	}
	
	private long controllerId;
	public long getControllerId() {
		return controllerId;
	}
	public void setControllerId(long controllerId) {
		this.controllerId = controllerId;
	}
	
	private Boolean configured;
	public Boolean getConfigured() {
		return configured;
	}
	public void setConfigured(Boolean configured) {
		this.configured = configured;
	}
	
	private Boolean fetchMapped;
	public Boolean getFetchMapped() {
		if (fetchMapped == null) {
			return false;
		}
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

	private long timestamp;
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
	
	
}