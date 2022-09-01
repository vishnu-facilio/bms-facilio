package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;

public class CustomizeViewCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long appId = (long) context.get(FacilioConstants.ContextNames.APP_ID);
		List<FacilioView> views = (List<FacilioView>)context.get(FacilioConstants.ContextNames.VIEW_LIST);
		if(views != null) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			for(FacilioView view : views) {
				if(view.getId() == -1) {
					String viewName = view.getName();
					long viewId = ViewAPI.checkAndAddView(viewName, moduleName, null, null, appId);
					view.setId(viewId);
				}
			}
			ViewAPI.customizeViews(views);
		}
		return false;
	}

}
