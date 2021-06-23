package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.BaseLineContext;

public class AddBaseLineCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		BaseLineContext baseLine = (BaseLineContext) context.get(FacilioConstants.ContextNames.BASE_LINE);
		if (baseLine != null) {
			long id = BaseLineAPI.addBaseLine(baseLine);
			baseLine.setId(id);
		}
		return false;
	}

}
