package com.facilio.workflows.context;

import java.io.Serializable;

public class WorkflowFunctionContext implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nameSpace;
	private String functionName;
	private String params;
	
	public String[] getParamList() {
		if(params != null && !params.equals("")) {
			String[] paramArray = params.split("\\s*,\\s*");
			return paramArray;
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
