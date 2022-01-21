package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.util.MLServiceAPI;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class MLServiceContext extends ModuleBaseWithCustomFields {

	private static final long serialVersionUID = 1L;

	/**
	 * User given input variables 
	 */
	private String modelName;
	private String scenario;
	private String serviceType = "default" ;
	private String assetIds;
	private List<Long> assetList; 
	private List<String> readingVariables;
	private List<List<Map<String, Object>>> models;
	private JSONObject mlVariables;
	private JSONObject groupingMethod;
	private JSONObject workflowInfo;
	private JSONArray filteringMethod;
	private String startTimeString;
	private String endTimeString;

	/**
	 * For development purpose
	 */
	private long mlID;
	private long startTime;
	private long endTime;
	private long executeTime;

	/**
	 * Internal usage variables 
	 */
	private long trainingSamplingPeriod = MLServiceAPI.TRAINING_SAMPLING_PERIOD;
	private JSONObject samplingJson;
	private boolean isPastData;

	private List<JSONArray> dataObjectList;

	private JSONObject orgDetails;
	private long useCaseId;
	private long workflowId;

	private String status;
	private List<MLResponseContext> mlResponseList;
	private List<Long> parentMlIdList;
	private Long childMlId;
	
	public JSONObject getWorkflowInfo() {
		return workflowInfo;
	}

	public void setWorkflowInfo(JSONObject workflowInfo) {
		this.workflowInfo = workflowInfo;
	}

	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public JSONObject getMlVariables() {
		return mlVariables;
	}

	public void setMlVariables(JSONObject mlVariables) {
		this.mlVariables = mlVariables;
	}
	
	public JSONObject getSamplingJson() {
		return samplingJson;
	}
	
	public void updateSamplingJson() {
//		"mlVariables" : {"totalEnergyConsumptionDelta" : {"maxSamplingPeriod" : 777600000 }}}
		this.samplingJson = new JSONObject();
		for(String reading : this.readingVariables) {
			JSONObject readingJson = new JSONObject();
			readingJson.put("maxSamplingPeriod", this.trainingSamplingPeriod);
			samplingJson.put(reading, readingJson);
		}
	}

	public JSONObject getGroupingMethod() {
		return groupingMethod;
	}

	public void setGroupingMethod(JSONObject groupingMethod) {
		this.groupingMethod = groupingMethod;
	}

	public JSONArray getFilteringMethod() {
		return filteringMethod;
	}

	public void setFilteringMethod(JSONArray filteringMethod) {
		this.filteringMethod = filteringMethod;
	}

	public List<String> getReadingVariables() {
		return readingVariables;
	}

	public void setReadingVariables(List<String> readingVariables) {
		this.readingVariables = readingVariables;
	}
	
	public void updateReadingVariables() throws Exception {
		this.readingVariables = MLServiceAPI.getModelReadings(this.models).get(0);
	}

	public List<Long> getAssetList() {
		return assetList;
	}

	public void setAssetList(List<Long> assetList) {
		this.assetList = assetList;
	}

	public JSONObject getRequestMeta() throws JSONException {
		JSONObject requestJson = new JSONObject();
		requestJson.putAll(getRequestMetaMap());
		return requestJson;
	}
	
	public Map<String, Object> getRequestMetaMap() {
		Map<String, Object> requestMap = new LinkedHashMap<>();
		addParams(requestMap, "modelName", this.modelName);
		addParams(requestMap, "scenario", this.scenario);
		addParams(requestMap, "assetIds", this.assetIds);
		addParams(requestMap, "serviceType", this.serviceType);
		addParams(requestMap, "readingVariables", this.readingVariables); //constructed variable
		addParams(requestMap, "models", this.models);
		addParams(requestMap, "mlVariables", this.mlVariables);
		addParams(requestMap, "filteringMethod", this.filteringMethod);
		addParams(requestMap, "groupingMethod", this.groupingMethod);
		addParams(requestMap, "workflowInfo", this.workflowInfo);
		addParams(requestMap, "startTime", this.startTime);
		addParams(requestMap, "endTime", this.endTime);
		addParams(requestMap, "executeTime", this.executeTime);
		addParams(requestMap, "isPastData", this.isPastData);
		addParams(requestMap, "parentMlIdList", this.parentMlIdList);
		addParams(requestMap, "childMlId", this.childMlId);
		addParams(requestMap, "trainingSamplingPeriod", this.trainingSamplingPeriod);
		return requestMap;
	}
	
	private void addParams(Map<String, Object> map, String key, Object value) {
		if(value!=null) {
			map.put(key, value);
		}
	}
	
	private void addParams(Map<String, Object> map, String key, long value) {
		if(value!=0) {
			map.put(key, value);
		}
	}

	public List<JSONArray> getDataObjectList() {
		return dataObjectList;
	}

	public void setDataObjectList(List<JSONArray> dataObjectList) {
		this.dataObjectList = dataObjectList;
	}

	public JSONObject getOrgDetails() {
		return orgDetails;
	}

	public void setOrgDetails(JSONObject orgDetails) {
		this.orgDetails = orgDetails;
	}

	public long getUseCaseId() {
		return useCaseId;
	}

	public void setUseCaseId(long useCaseId) {
		this.useCaseId = useCaseId;
	}

	public String getStatus() {
		return status;
	}

	public void updateStatus(String status) throws Exception {
		this.setStatus(status);
		MLServiceAPI.updateMLServiceStatus(this.getUseCaseId(), status);
	}

	public void setStatus(String status) {
		this.status = status;
	}

	//	public String getAssetType() {
	//		return assetType;
	//	}
	//	
	//	public void setAssetType(String assetType) {
	//		this.assetType = assetType;
	//	}

	public String getAssetIds() { 
		return assetIds;
	}

	public void setAssetIds(String assetIds) {
		this.assetIds = assetIds;
		if(assetIds!=null) {
			List<Long> assetList = new ArrayList<>();
			for(String assetId : assetIds.split(",")) {
				assetList.add(Long.valueOf(assetId));
			}
			this.setAssetList(assetList);
		}
	}

	public String getScenario() {
		return scenario;
	}

	public void setScenario(String scenario) {
		this.scenario = scenario;
	}

	public List<MLResponseContext> getMlResponseList() {
		return mlResponseList;
	}

	public void setMlResponseList(List<MLResponseContext> mlResponseList) {
		this.mlResponseList = mlResponseList;
	}

	public long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	public long getStartTime() {
		return startTime;
	}
	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
	public long getExecuteTime() {
		return executeTime;
	}

	public void setExecuteTime(long executeTime) {
		this.executeTime = executeTime;
	}

	public List<List<Map<String, Object>>> getModels() {
		return models;
	}

	public void setModels(List<List<Map<String, Object>>> models) {
		this.models = models;
	}

	public String getServiceType() {
		return serviceType;
	}

	public void setServiceType(String serviceType) {
		this.serviceType = serviceType;
	}

	public long getMlID() {
		return mlID;
	}

	public void setMlID(long mlID) {
		this.mlID = mlID;
	}
	
	public void updateMlID(long mlID) throws Exception {
		this.setMlID(mlID);
		MLServiceAPI.updateMLID(this.getUseCaseId(), mlID);
	}

	public List<Long> getParentMlIdList() {
		return parentMlIdList;
	}

	public void setParentMlIdList(List<Long> parentMlIdList) {
		this.parentMlIdList = parentMlIdList;
	}

	public Long getChildMlId() {
		return childMlId;
	}

	public void setChildMlId(Long childMlId) {
		this.childMlId = childMlId;
	}

	public void setStartTimeString(String startTimeString) {
		this.startTimeString = startTimeString;
		this.startTime = MLServiceAPI.convertDatetoTTimeZone(startTimeString);
	}

	public String getStartTimeString() {
		return startTimeString;
	}
	
	public void setEndTimeString(String endTimeString) {
		this.endTimeString = endTimeString;
		this.endTime = MLServiceAPI.convertDatetoTTimeZone(endTimeString);

	}
	
	public String getEndTimeString() {
		return endTimeString;
	}
	
	public long getTrainingSamplingPeriod() {
		return trainingSamplingPeriod;
	}

	public void setTrainingSamplingPeriod(long trainingSamplingPeriod) {
		this.trainingSamplingPeriod = trainingSamplingPeriod;
	}

	public boolean isPastData() {
		return isPastData;
	}

	public void setPastData(boolean isPastData) {
		this.isPastData = isPastData;
	}
	
	@Override
	public String toString() {
		return "MLServiceContext [modelName=" + modelName + ", scenario=" + scenario + ", serviceType=" + serviceType
				+ ", assetIds=" + assetIds + ", assetList=" + assetList + ", readingVariables=" + readingVariables
				+ ", models=" + models + ", mlVariables=" + mlVariables + ", groupingMethod=" + groupingMethod
				+ ", workflowInfo=" + workflowInfo + ", filteringMethod=" + filteringMethod + ", startTimeString="
				+ startTimeString + ", endTimeString=" + endTimeString + ", mlID=" + mlID + ", startTime=" + startTime
				+ ", endTime=" + endTime + ", executeTime=" + executeTime + ", trainingSamplingPeriod="
				+ trainingSamplingPeriod + ", samplingJson=" + samplingJson + ", isPastData=" + isPastData
				+ ", orgDetails=" + orgDetails + ", useCaseId=" + useCaseId
				+ ", workflowId=" + workflowId + ", status=" + status
				+ ", parentMlIdList=" + parentMlIdList + ", childMlId=" + childMlId + "]"; //excluding dataObjectList, mlResponseList
	}

}
