package com.facilio.workflows.context;

import java.util.Arrays;
import java.util.List;

public class WorkflowFunctionContext {

	String nameSpace;
	String FunctionName;
	String params;
	
	public List<String> getParamList() {
		if(params != null && !params.equals("")) {
			params.split(",");
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
		return FunctionName;
	}
	public void setFunctionName(String functionName) {
		FunctionName = functionName;
	}
	public String getParams() {
		return params;
	}
	public void setParams(String params) {
		this.params = params;
	}
}
