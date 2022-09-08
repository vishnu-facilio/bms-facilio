package com.facilio.bmsconsoleV3.commands.servicerequest;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.bmsconsole.activity.CommonActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.bmsconsoleV3.context.V3ServiceRequestContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;

public class AddActivityForServiceRequestCommandV3 extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(AddActivityForServiceRequestCommandV3.class.getName());

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		List<V3ServiceRequestContext> serviceRequests = Constants.getRecordList((FacilioContext) context);

		for(V3ServiceRequestContext serviceRequest :serviceRequests) {
			CommonCommandUtil.addActivityToContext(serviceRequest.getId(), -1, CommonActivityType.ADD_RECORD, new JSONObject(), (FacilioContext) context);
		}
		
		return false;
	}

}
