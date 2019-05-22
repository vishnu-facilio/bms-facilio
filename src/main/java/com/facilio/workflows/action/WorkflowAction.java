package com.facilio.workflows.action;

import com.facilio.bmsconsole.actions.FacilioAction;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.functions.FacilioSystemFunctionNameSpace;
import com.facilio.workflows.functions.FacilioWorkflowFunctionInterface;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.log4j.LogManager;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkflowAction extends FacilioAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static org.apache.log4j.Logger log = LogManager.getLogger(WorkflowUtil.class.getName());
	
	List<FacilioSystemFunctionNameSpace> namespaces;
	
	public List<FacilioSystemFunctionNameSpace> getNamespaces() {
		return namespaces;
	}

	public void setNamespaces(List<FacilioSystemFunctionNameSpace> namespaces) {
		this.namespaces = namespaces;
	}
	
	public int nameSpaceValue;
	
	public int getNameSpaceValue() {
		return nameSpaceValue;
	}

	public void setNameSpaceValue(int nameSpaceValue) {
		this.nameSpaceValue = nameSpaceValue;
	}
	
	List<FacilioWorkflowFunctionInterface> functions;
	
	public List<FacilioWorkflowFunctionInterface> getFunctions() {
		return functions;
	}

	public void setFunctions(List<FacilioWorkflowFunctionInterface> functions) {
		this.functions = functions;
	}
	

	public String getAllFunctions() {
		
		try {

			functions = WorkflowUtil.getFacilioFunctions(FacilioSystemFunctionNameSpace.getFacilioDefaultFunction(nameSpaceValue).getName());
			
			return SUCCESS;
		}
		catch (Exception e) {
			log.error(e);
			return ERROR;
		}
	}

	public String getAllNameSpace() {
		
		try {
			namespaces = new ArrayList(FacilioSystemFunctionNameSpace.getNamespaceMap().values());
			
			return SUCCESS;
		}
		catch (Exception e) {
			log.error(e);
			return ERROR;
		}
	}
	
	private WorkflowContext workflow;
	public WorkflowContext getWorkflow() {
		return workflow;
	}
	public void setWorkflow(WorkflowContext workflow) {
		this.workflow = workflow;
	}
	
	public String getWorkflowXmlFromObject() throws Exception {
		setResult("workflowString", WorkflowUtil.getXmlStringFromWorkflow(workflow));
		return SUCCESS;
	}
	
	Long workflowId;
	JSONObject params;
	String paramString;
	public String getParamString() {
		return paramString;
	}

	public void setParamString(String paramString) {
		this.paramString = paramString;
	}

	String workflowString;
	
	int workflowUIMode;
	
	public int getWorkflowUIMode() {
		return workflowUIMode;
	}

	public void setWorkflowUIMode(int workflowUIMode) {
		this.workflowUIMode = workflowUIMode;
	}

	public Long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(Long workflowId) {
		this.workflowId = workflowId;
	}

	public JSONObject getParams() {
		return params;
	}

	public void setParams(JSONObject params) {
		this.params = params;
	}

	public String getWorkflowString() {
		return workflowString;
	}

	public void setWorkflowString(String workflowString) {
		this.workflowString = workflowString;
	}
	Map<String, Object> resultMap;

	public Map<String, Object> getResultMap() {
		return resultMap;
	}

	public void setResultMap(Map<String, Object> resultMap) {
		this.resultMap = resultMap;
	}
	
	String logResult;

	public String getLogResult() {
		return logResult;
	}

	public void setLogResult(String logResult) {
		this.logResult = logResult;
	}

	public String runWorkflow() throws Exception {
		if(params == null && paramString != null) {
    		JSONParser parser = new JSONParser();
    		params = (JSONObject) parser.parse(paramString);
    	}
	    if(workflowId != null) {
	    	resultMap = WorkflowUtil.getExpressionResultMap(workflowId, params);
	    }
	    else if(workflowString != null) {
	    	WorkflowContext wfContext = new WorkflowContext();
	    	wfContext.setWorkflowString(workflowString);
	    	wfContext.setWorkflowUIMode(workflowUIMode);
	    	wfContext.setDebugMode(true);
	    	logResult = WorkflowUtil.getWorkflowExpressionResult(wfContext, params).toString();
	    	System.out.println(logResult);
	    }
		return SUCCESS;
	}
	
}
