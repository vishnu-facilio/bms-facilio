package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class MLModelVariableContext extends ModuleBaseWithCustomFields
{
	private static final long serialVersionUID = 1L;
	
	private long mlId;
	private String variableKey;
	private String variableValue;
	
	public void setMlID(long mlId)
	{
		this.mlId = mlId;
 	}
	public long getMlID()
	{
		return mlId;
 	}
	
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
