package com.facilio.bmsconsole.context;

import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;

public class MLAssetContext extends ModuleBaseWithCustomFields
{
	
	private static final long serialVersionUID = 1L;
	
	private long assetID;

	public long getAssetID() 
	{
		return assetID;
	}

	public void setAssetID(long assetID) 
	{
		this.assetID = assetID;
	}

}
