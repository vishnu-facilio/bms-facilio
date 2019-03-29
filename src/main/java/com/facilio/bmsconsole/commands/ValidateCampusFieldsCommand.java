package com.facilio.bmsconsole.commands;

import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class ValidateCampusFieldsCommand implements Command {
	
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		context.get(FacilioConstants.ContextNames.SITE);
		return false;
	}

}
