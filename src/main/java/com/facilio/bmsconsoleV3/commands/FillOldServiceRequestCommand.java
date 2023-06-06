package com.facilio.bmsconsoleV3.commands;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3ServiceRequestContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;

public class FillOldServiceRequestCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		List<V3ServiceRequestContext> serviceRequests = Constants.getRecordList((FacilioContext) context);
		
		if(serviceRequests != null && !serviceRequests.isEmpty()) {
			
			List<Long> serviceRequestIds = serviceRequests.stream().map(V3ServiceRequestContext::getId).collect(Collectors.toList());
			
			List<V3ServiceRequestContext> oldServiceRequest = V3RecordAPI.getRecordsList(FacilioConstants.ContextNames.SERVICE_REQUEST, serviceRequestIds , V3ServiceRequestContext.class);
			
			Constants.addToOldRecordMap(context,FacilioConstants.ContextNames.SERVICE_REQUEST,oldServiceRequest);
		}
		
		return false;
	}

}
