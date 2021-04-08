package com.facilio.bmsconsoleV3.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.MailMessageUtil;
import com.facilio.constants.FacilioConstants;

public class AddSuplimentsForEmailBaseMessageCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
	    context.put(FacilioConstants.ContextNames.FETCH_SUPPLEMENTS, MailMessageUtil.getMailMessageSupliments());
		
		return false;
	}

}
