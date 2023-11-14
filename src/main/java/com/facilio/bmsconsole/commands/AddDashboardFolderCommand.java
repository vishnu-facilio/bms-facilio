package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.DashboardFolderContext;
import com.facilio.bmsconsole.util.DashboardUtil;
import com.facilio.constants.FacilioConstants;

import java.util.List;
import java.util.Map;

public class AddDashboardFolderCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		DashboardFolderContext folders = (DashboardFolderContext) context.get(FacilioConstants.ContextNames.DASHBOARD_FOLDER);
		if(folders != null) {
			getDashboardFolderLinkName(folders);
			DashboardUtil.addDashboardFolder(folders);
		}
		return false;
	}
	private void getDashboardFolderLinkName(DashboardFolderContext folder) throws Exception {
		Map<String, FacilioField> dashboardFolderFields = FieldFactory.getAsMap(FieldFactory.getDashboardFolderFields());
		FacilioField folderLinkName = dashboardFolderFields.get(FacilioConstants.ContextNames.LINK_NAME);
		FacilioModule module = ModuleFactory.getDashboardFolderModule();
		List<String> linkNames = DashboardUtil.getExistingLinkNames(module.getTableName(),folderLinkName);
		if(folder.getLinkName() == null){
			String name = folder.getName().replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
			String linkName = DashboardUtil.getLinkName(name,linkNames);
			folder.setLinkName(linkName);
		}
	}

}
