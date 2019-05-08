package com.facilio.bmsconsole.commands;

import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class DeleteSupportEmailCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
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
