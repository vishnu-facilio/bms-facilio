package com.facilio.bmsconsoleV3.commands.servicerequest;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsoleV3.context.V3ServiceRequestContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;

public class SetIsNewForServiceRequestCommandV3 extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		List<V3ServiceRequestContext> requests = Constants.getRecordList((FacilioContext) context);
		
		requests.stream().forEach(request -> request.setEmailConversationIsNewRecord(Boolean.TRUE));

		return false;
	}

}
