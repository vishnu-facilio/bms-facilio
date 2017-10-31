package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericDeleteRecordBuilder;

public class DeleteSupportEmailCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long supportEmailId = (long) context.get(FacilioConstants.ContextNames.ID);
		if(supportEmailId != -1) {
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
														.connection(((FacilioContext)context).getConnectionWithTransaction())
														.table("SupportEmails")
														.andCustomWhere("ID = ?", supportEmailId);
			builder.delete();
			context.put(FacilioConstants.ContextNames.RESULT, "success");
		}
		
		return false;
	}

}
