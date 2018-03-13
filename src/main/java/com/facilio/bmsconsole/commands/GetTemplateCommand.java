package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.constants.FacilioConstants;

public class GetTemplateCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long id = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if (id != -1) {
			context.put(FacilioConstants.ContextNames.TEMPLATE, TemplateAPI.getTemplate(id));
		}
		return false;
	}

}
