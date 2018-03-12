package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.timeseries.TimeSeriesAPI;
import com.opensymphony.xwork2.ActionSupport;

public class TimeSeries extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	
	public String publish() throws Exception
	{
		JSONParser parser = new JSONParser();
		JSONObject payLoad = (JSONObject) parser.parse(getDeviceData());
		TimeSeriesAPI.processPayLoad(getTimestamp(), payLoad);
		return SUCCESS;
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
	
	private List<Long> fieldList;
	public void setFieldList(List<Long> fieldList) {
		this.fieldList=fieldList;
	}
	public List<Long> getFieldList(){
		return fieldList;
	}
	
	
	private List<Long> markTypeList;
	public void setMarkTypeList(List<Long> markTypeList) {
		this.markTypeList=markTypeList;
	}
	public List<Long> getMarkTypeList(){
		return markTypeList;
	}
	
	private Criteria getCriteria() {
		Criteria criteria = new Criteria(); 
		if(timeRange!=null && !timeRange.isEmpty()) {
			criteria.addAndCondition(CriteriaAPI.getCondition("TTIME","TTIME", 
					StringUtils.join(timeRange, ","),DateOperators.BETWEEN));
		}
		if(deviceList!=null && !deviceList.isEmpty()) {
			criteria.addAndCondition(CriteriaAPI.getCondition("RESOURCE_ID","RESOURCE_ID", 
					StringUtils.join(deviceList,","), NumberOperators.EQUALS));
		}
		if(moduleList!=null && !moduleList.isEmpty()) {
		criteria.addAndCondition(CriteriaAPI.getCondition("MODULEID","MODULEID", 
				StringUtils.join(moduleList,","), NumberOperators.EQUALS));
		}
		if(fieldList!=null && !fieldList.isEmpty()) {
		criteria.addAndCondition(CriteriaAPI.getCondition("FIELD_ID","FIELD_ID", 
				StringUtils.join(fieldList,","), NumberOperators.EQUALS));
		}
		if(markTypeList!=null && !markTypeList.isEmpty()) {
		criteria.addAndCondition(CriteriaAPI.getCondition("MARK_TYPE","MARK_TYPE", 
				StringUtils.join(markTypeList,","), NumberOperators.EQUALS));
		}
		
		return criteria;
	}
}