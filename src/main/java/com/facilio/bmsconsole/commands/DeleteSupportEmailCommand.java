package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.SupportEmailAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.service.FacilioService;

public class DeleteSupportEmailCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long supportEmailId = (long) context.get(FacilioConstants.ContextNames.ID);
		if(supportEmailId != -1) {
			
			FacilioService.runAsService(FacilioConstants.Services.DEFAULT_SERVICE,() -> SupportEmailAPI.deleteSupportEmail(supportEmailId));
		}
		
		return false;
	}

}
