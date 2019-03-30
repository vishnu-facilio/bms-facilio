package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.BaseLineContext;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class AddBaseLineCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		BaseLineContext baseLine = (BaseLineContext) context.get(FacilioConstants.ContextNames.BASE_LINE);
		if (baseLine != null) {
			long id = BaseLineAPI.addBaseLine(baseLine);
			baseLine.setId(id);
		}
		return false;
	}

}
