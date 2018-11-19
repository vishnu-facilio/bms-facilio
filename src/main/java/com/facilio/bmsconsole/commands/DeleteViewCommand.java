package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.util.BaseLineAPI;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import com.twilio.sdk.resource.preview.wireless.Command;
import java.util.List;

import org.apache.commons.chain.Context;

public class DeleteViewCommand implements org.apache.commons.chain.Command {
	public boolean execute(Context context) throws Exception {
		Long viewId =  (Long) context.get(FacilioConstants.ContextNames.VIEW_ID);
		ViewAPI.deleteView(viewId);
		return false;
	}

}
