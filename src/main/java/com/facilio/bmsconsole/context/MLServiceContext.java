package com.facilio.bmsconsole.context;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class MLServiceContext extends ModuleBaseWithCustomFields {
	
	private static final long serialVersionUID = 1L;
	
	/**
	 * User given input variables 
	 */
	private String modelName;
	private String scenario;
	private JSONArray readingVariables;
	private JSONObject assetDetails;
	private JSONObject mlVariables;
    private JSONObject groupingMethod;
    private JSONObject workflowInfo;
    private JSONArray filteringMethod;
    
    /**
     * Internal usage variables 
     */
	private JSONArray dataObject;
	private JSONObject orgDetails;
	private long useCaseId;
	private long workflowId;
	
	private String status;
	private MLResponseContext mlResponse;

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

	public JSONArray getReadingVariables() {
		return readingVariables;
	}
	
	public List<String> getReadingList() {
		List<String> readingList = new ArrayList<String>();
		if(this.readingVariables!=null) {
			readingList.addAll(this.readingVariables);
		}
		return readingList;
	}

	public void setReadingVariables(JSONArray readingVariables) {
		this.readingVariables = readingVariables;
	}
	
	public JSONObject getAssetDetails() {
		return assetDetails;
	}

	public void setAssetDetails(JSONObject assetDetails) {
		this.assetDetails = assetDetails;
	}
	
	public JSONObject getReqJson() throws JSONException {
		JSONObject jsonRes = new JSONObject();
		jsonRes.put("modelName", this.modelName);
		jsonRes.put("scenario", this.scenario);
		jsonRes.put("assetDetails", this.assetDetails);
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
				+ workflowInfo + ", orgDetails=" + orgDetails + ", useCaseId="
				+ useCaseId + ", workflowId=" + workflowId + ", status=" + status + "]"; //excluding dataObject and MLResponse
	}

}
