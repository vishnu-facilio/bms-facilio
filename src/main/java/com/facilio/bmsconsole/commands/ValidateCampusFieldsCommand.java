package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.CampusContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.constants.FacilioConstants;

public class ValidateCampusFieldsCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		CampusContext campusContext = (CampusContext) context.get(FacilioConstants.ContextNames.CAMPUS);
		return false;
	}

}
