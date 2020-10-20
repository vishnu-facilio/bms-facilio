package com.facilio.bmsconsole.context;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class MLServiceContext extends ModuleBaseWithCustomFields {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * User given input variables 
	 */
	private String modelName;
	private String scenario;
	private List<String> readingVariables;
	private Map<String, Object> assetDetails;
	private Map<String, Object> mlVariables;
    private Map<String, Object> groupingMethod;
    private Map<String, Object> workflowInfo;
    private List<Map<String, Object>> filteringMethod;
    
    /**
     * Internal usage variables 
     */
	private JSONArray dataObject;
	private Map<String, Object> orgDetails;
	private long useCaseId;
	private long workflowId;
	
	private String status;
	private MLResponseContext mlResponse;

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
	
	public List<Map<String, Object>> getFilteringMethod() {
		return filteringMethod;
	}

	public void setFilteringMethod(List<Map<String, Object>> filteringMethod) {
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
	
	public JSONObject getReqJson() throws JSONException {
		JSONObject jsonRes = new JSONObject();
		jsonRes.put("modelName", 	this.modelName);
		jsonRes.put("scenario", this.scenario);
		jsonRes.put("assetDetails", this.assetDetails);
		jsonRes.put("orgDetails", this.orgDetails);
		jsonRes.put("readingVariables", this.readingVariables);
		jsonRes.put("mlVariables", this.mlVariables);
		jsonRes.put("filteringMethod", this.filteringMethod);
		jsonRes.put("groupingMethod", this.groupingMethod);
		jsonRes.put("workflowInfo", this.workflowInfo);
		return jsonRes;
	}

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
	
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
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
		setStatus(mlResponse.getMessage());
	}

	public long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	@Override
	public String toString() {
		return "MLServiceContext [modelName=" + modelName + ", scenario=" + scenario + ", readingVariables="
				+ readingVariables + ", assetDetails=" + assetDetails + ", mlVariables=" + mlVariables
				+ ", filteringMethod=" + filteringMethod + ", groupingMethod=" + groupingMethod + ", workflowInfo="
				+ workflowInfo + ", dataObject=" + dataObject + ", orgDetails=" + orgDetails + ", useCaseId="
				+ useCaseId + ", workflowId=" + workflowId + ", status=" + status + ", mlResponse=" + mlResponse + "]";
	}

}
