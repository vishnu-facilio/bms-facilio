package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.constants.FacilioConstants;

public class SetBaseSpaceRecordForRollUpFieldCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.BASE_SPACE);
		context.put(FacilioConstants.ContextNames.RECORD,(BaseSpaceContext) context.get(FacilioConstants.ContextNames.BASE_SPACE));
		return false;
	}


}
