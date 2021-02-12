package com.facilio.bmsconsoleV3.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3TenantUnitSpaceContext;
import com.facilio.constants.FacilioConstants;

public class SetSpaceRecordForRollUpFieldCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		context.put(FacilioConstants.ContextNames.RECORD,(V3TenantUnitSpaceContext) context.get(FacilioConstants.ContextNames.SPACE));
		return false;
	}

}
