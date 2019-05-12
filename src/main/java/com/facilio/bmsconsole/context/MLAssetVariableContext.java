package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class MLAssetVariableContext extends ModuleBaseWithCustomFields 
{
	private static final long serialVersionUID = 1L;

	private String variableKey;
	private String variableValue;
	
	public void setVariableKey(String variableKey)
	{
		this.variableKey = variableKey;
	}
	public String getVariableKey()
	{
		return variableKey;
	}
	
	public void setVariableValue(String variableValue)
	{
		this.variableValue = variableValue;
	}
	public String getVariableValue()
	{
		return variableValue;
	}

}
