package com.facilio.bmsconsole.context;

import com.facilio.modules.ModuleBaseWithCustomFields;

public class MLAssetVariableContext extends ModuleBaseWithCustomFields 
{
	private static final long serialVersionUID = 1L;

	private long mlID;
	private long assetID;
	private String variableKey;
	private String variableValue;
	
	public void setMlID(long mlID)
	{
		this.mlID = mlID;
	}
	public long getMlID()
	{
		return mlID;
	}
	public void setAssetID(long assetID)
 	{
		this.assetID = assetID;
 	}
 	public long getAssetID()
 	{
 		return assetID;
 	}
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
