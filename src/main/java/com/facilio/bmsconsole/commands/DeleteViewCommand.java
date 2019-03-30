package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

public class DeleteViewCommand implements org.apache.commons.chain.Command {
	public boolean execute(Context context) throws Exception {
		Long viewId =  (Long) context.get(FacilioConstants.ContextNames.VIEWID);
		ViewAPI.deleteView(viewId);
		return false;
	}

}
