package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class MLModelVariableContext extends ModuleBaseWithCustomFields
{
	private static final long serialVersionUID = 1L;
	
	private String variableKey;
	private String variableValue;
	
	public String getVariableKey() 
	{
		return variableKey;
	}
	public void setVariableKey(String variableKey) 
	{
		this.variableKey = variableKey;
	}
	
	public String getVariableValue() 
	{
		return variableValue;
	}
	public void setVariableValue(String variableValue) 
	{
		this.variableValue = variableValue;
	}
	

}
