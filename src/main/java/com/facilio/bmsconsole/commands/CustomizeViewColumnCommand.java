package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ViewField;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.bmsconsole.view.FacilioView.ViewType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class CustomizeViewColumnCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		List<ViewField> columns = (List<ViewField>)context.get(FacilioConstants.ContextNames.VIEWCOLUMNS);
		if(columns != null && !columns.isEmpty()) {
			Long viewId = (Long)context.get(FacilioConstants.ContextNames.VIEWID);
			if(viewId == null || viewId == -1) {
				String viewName = (String)context.get(FacilioConstants.ContextNames.CV_NAME);
				String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
				if (viewName != null && !viewName.isEmpty()) {
					viewId = ViewAPI.checkAndAddView(viewName, moduleName);
				} else {
					throw new IllegalArgumentException("viewId or viewName,moduleName is required");
				}
			}
			
			if(viewId != -1) {
				ViewAPI.customizeViewColumns(viewId, columns);
				List<ViewField> savedColumns = ViewAPI.getViewColumns(viewId);
				context.put(FacilioConstants.ContextNames.VIEWCOLUMNS, savedColumns);
			}
		}
		
		return false;
	}

}
