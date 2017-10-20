package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.constants.FacilioConstants;

public class ValidateCampusFieldsCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		SiteContext campusContext = (SiteContext) context.get(FacilioConstants.ContextNames.SITE);
		return false;
	}

}
