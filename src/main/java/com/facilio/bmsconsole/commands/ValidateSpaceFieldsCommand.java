package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.SpaceContext;
import com.facilio.constants.FacilioConstants;

public class ValidateSpaceFieldsCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		SpaceContext spaceContext = (SpaceContext) context.get(FacilioConstants.ContextNames.SPACE);
		return false;
	}

}
