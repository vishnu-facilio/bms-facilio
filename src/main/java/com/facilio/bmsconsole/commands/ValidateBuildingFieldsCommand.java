package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BuildingContext;
import com.facilio.constants.FacilioConstants;

public class ValidateBuildingFieldsCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		BuildingContext campusContext = (BuildingContext) context.get(FacilioConstants.ContextNames.BUILDING);
		return false;
	}

}
