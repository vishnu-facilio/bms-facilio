package com.facilio.bmsconsole.context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class MLServiceContext extends ModuleBaseWithCustomFields {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String modelName;
	private List<String> readingVariables;
	private Map<String, Object> assetDetails;
	private Map<String, Object> mlVariables;
    private Map<String, Object> filteringMethod;
    private Map<String, Object> groupingMethod;
    private Map<String, Object> workflowInfo;
    private String scenario;
    
    public Map<String, Object> getWorkflowInfo() {
		return workflowInfo;
	}

	public void setWorkflowInfo(Map<String, Object> workflowInfo) {
		this.workflowInfo = workflowInfo;
	}
    
	public String getModelName() {
		return modelName;
	}
	
	public void setModelName(String modelName) {
		this.modelName = modelName;
	}
	
	public Map<String, Object> getMlVariables() {
		return mlVariables;
	}
	
	public void setMlVariables(Map<String, Object> mlVariables) {
		this.mlVariables = mlVariables;
	}
	
	public Map<String, Object> getGroupingMethod() {
		return groupingMethod;
	}
	
	public void setGroupingMethod(Map<String, Object> groupingMethod) {
		this.groupingMethod = groupingMethod;
	}
	
	public Map<String, Object> getFilteringMethod() {
		return filteringMethod;
	}

	public void setFilteringMethod(Map<String, Object> filteringMethod) {
		this.filteringMethod = filteringMethod;
	}

	public List<String> getReadingVariables() {
		return readingVariables;
	}

	public void setReadingVariables(List<String> readingVariables) {
		this.readingVariables = readingVariables;
	}
	
	public Map<String, Object> getAssetDetails() {
		return assetDetails;
	}

	public void setAssetDetails(Map<String, Object> assetDetails) {
		this.assetDetails = assetDetails;
	}
	
	public JSONObject getReqJson() {
		JSONObject jsonRes = new JSONObject();
		jsonRes.put("modelName", this.modelName);
		jsonRes.put("assetDetails", this.assetDetails);
		jsonRes.put("readingVariables", this.readingVariables);
		jsonRes.put("mlVariables", this.mlVariables);
		jsonRes.put("filteringMethod", this.filteringMethod);
		jsonRes.put("groupingMethod", this.groupingMethod);
		jsonRes.put("scenario", this.scenario);
		return jsonRes;
	}

	private JSONArray dataObject;
	private Map<String, Object> orgDetails;
	private long useCaseId;
	

	public JSONArray getDataObject() {
		return dataObject;
	}

	public void setDataObject(JSONArray dataObject) {
		this.dataObject = dataObject;
	}

	public Map<String, Object> getOrgDetails() {
		return orgDetails;
	}

	public void setOrgDetails(Map<String, Object> orgDetails) {
		this.orgDetails = orgDetails;
	}

	public long getUseCaseId() {
		return useCaseId;
	}

	public void setUseCaseId(long useCaseId) {
		this.useCaseId = useCaseId;
	}
	
	private String status;
	private JSONObject apiResponse;
	private MLResponseContext mlResponse;
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public JSONObject getApiResponse() {
		return apiResponse;
	}

	public void setApiResponse(JSONObject apiResponse) {
		this.apiResponse = apiResponse;
		setMlResponse(FieldUtil.getAsBeanFromMap(apiResponse.toMap(), MLResponseContext.class));
	}

	public String getScenario() {
		return scenario;
	}

	public void setScenario(String scenario) {
		this.scenario = scenario;
	}

	public MLResponseContext getMlResponse() {
		return mlResponse;
	}

	public void setMlResponse(MLResponseContext mlResponse) {
		this.mlResponse = mlResponse;
	}

	private Map<String, MLVariableContext> mlVariableMap;
	
	public Map<String, MLVariableContext> getMlVariableMap() {
		return mlVariableMap;
	}

	public void setMlVariableMap(Map<String, MLVariableContext> mlVariableMap) {
		this.mlVariableMap = mlVariableMap;
	}

	@Override
	public String toString() {
		return "MLServiceContext [modelName=" + modelName + ", readingVariables=" + readingVariables + ", assetDetails="
				+ assetDetails + ", mlVariables=" + mlVariables + ", filteringMethod=" + filteringMethod
				+ ", groupingMethod=" + groupingMethod + ", workflowInfo=" + workflowInfo + ", scenario=" + scenario
				+ ", dataObject=" + dataObject + ", orgDetails=" + orgDetails + ", useCaseId=" + useCaseId + ", status="
				+ status + ", apiResponse=" + apiResponse + ", mlResponse=" + mlResponse + ", mlVariable=" + mlVariable
				+ "]";
	}

	
}
