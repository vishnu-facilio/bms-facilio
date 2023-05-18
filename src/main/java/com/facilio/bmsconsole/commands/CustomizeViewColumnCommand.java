package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.constants.FacilioConstants;

public class CustomizeViewColumnCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		long appId = (long) context.getOrDefault(FacilioConstants.ContextNames.APP_ID, -1L);
		List<ViewField> columns = (List<ViewField>)context.get(FacilioConstants.ContextNames.VIEWCOLUMNS);
		if(columns != null && !columns.isEmpty()) {
			Long viewId = (Long)context.get(FacilioConstants.ContextNames.VIEWID);
			if(viewId == null || viewId == -1) {
				String viewName = (String)context.get(FacilioConstants.ContextNames.CV_NAME);
				String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
				if (viewName != null && !viewName.isEmpty()) {
					viewId = ViewAPI.checkAndAddView(viewName, moduleName, columns, (Long) null, appId);
				} else {
					throw new IllegalArgumentException("viewId or viewName,moduleName is required");
				}
			}
			else {
				ViewAPI.customizeViewColumns(viewId, columns);
			}
			
			List<ViewField> savedColumns = ViewAPI.getViewColumns(viewId);
			FacilioView dbView = ViewAPI.getView(viewId);
			context.put(FacilioConstants.ContextNames.EXISTING_CV, dbView);
			context.put(FacilioConstants.ContextNames.VIEWCOLUMNS, savedColumns);
		}
		
		return false;
	}

}
