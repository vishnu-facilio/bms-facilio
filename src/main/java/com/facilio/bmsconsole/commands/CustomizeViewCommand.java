package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.List;

public class CustomizeViewCommand implements Command{

	@Override
	public boolean execute(Context context) throws Exception {
		List<FacilioView> views = (List<FacilioView>)context.get(FacilioConstants.ContextNames.VIEW_LIST);
		if(views != null) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			for(FacilioView view : views) {
				if(view.getId() == -1) {
					String viewName = view.getName();
					long viewId = ViewAPI.checkAndAddView(viewName, moduleName, null);
					view.setId(viewId);
				}
			}
			ViewAPI.customizeViews(views);
		}
		return false;
	}

}
