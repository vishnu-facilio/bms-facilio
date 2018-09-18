package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.timeseries.TimeSeriesAPI;
import com.opensymphony.xwork2.ActionSupport;

public class TimeSeries extends ActionSupport {
	
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
		
		TimeSeriesAPI.processHistoricalData(deviceList);
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
		Map<String,Long> instanceFieldMap = (Map<String, Long>) object.get("instanceFieldMap");
		TimeSeriesAPI.insertInstanceAssetMapping(deviceName, assetId, instanceFieldMap);
		if (doMigration) {
			setDeviceList(Collections.singletonList(deviceName));
			migrateData();
		}
		return SUCCESS;
	}
	


	public String getDefaultFieldMap() throws Exception {
		setFieldMap(TimeSeriesAPI.getDefaultInstanceFieldMap());
		return SUCCESS;
	}
	
	long timestamp;
	public void setTimestamp(long ttime) {
		this.timestamp=ttime;
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	private String deviceData = null;
	public void setDeviceData(String deviceData) 
	{
		this.deviceData = deviceData;
	}
	
	public String getDeviceData() {
		
		return deviceData;
	}
	
	private List<String> deviceList = null;
	public void setDeviceList(List<String> deviceList) 
	{
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