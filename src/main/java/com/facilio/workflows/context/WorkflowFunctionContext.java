package com.facilio.workflows.context;

import java.util.Arrays;
import java.util.List;

public class WorkflowFunctionContext {

	private String nameSpace;
	private String functionName;
	private String params;
	
	public List<String> getParamList() {
		if(params != null && !params.equals("")) {
			params.split("\\s*,\\s*");
			List<String> paramList = Arrays.asList(params);  
			return paramList;
		}
		return null;
	}
	public String getNameSpace() {
		return nameSpace;
	}
	public void setNameSpace(String nameSpace) {
		this.nameSpace = nameSpace;
	}
	public String getFunctionName() {
		return functionName;
	}
	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
}
