package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;

public class DeleteSupportEmailCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long supportEmailId = (long) context.get(FacilioConstants.ContextNames.ID);
		if(supportEmailId != -1) {
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
														.table("SupportEmails")
														.andCustomWhere("ID = ?", supportEmailId);
			builder.delete();
			context.put(FacilioConstants.ContextNames.RESULT, "success");
		}
		
		return false;
	}

}
