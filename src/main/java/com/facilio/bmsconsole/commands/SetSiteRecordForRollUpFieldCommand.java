package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.constants.FacilioConstants;

public class SetSiteRecordForRollUpFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
	
		context.put(FacilioConstants.ContextNames.RECORD,(SiteContext) context.get(FacilioConstants.ContextNames.SITE));
		return false;
	}
}
