package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ViewGroups;
import com.facilio.bmsconsole.util.ViewAPI;
import com.facilio.bmsconsole.view.FacilioView;
import com.facilio.bmsconsole.view.ViewFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FacilioModule;

public class CreateDefaultViewCommad extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
		
		ViewGroups viewGroup = new ViewGroups();
		viewGroup.setName("allViews");
		viewGroup.setDisplayName("All Views");
		viewGroup.setModuleId(module.getModuleId());
		long groupId = ViewAPI.addViewGroup(viewGroup, AccountUtil.getCurrentOrg().getOrgId(), module.getName());
		
		FacilioView defaultView = ViewFactory.getCustomModuleAllView(module);
		defaultView.setGroupId(groupId);
		defaultView.setModuleId(module.getModuleId());
		defaultView.setType(1);
		defaultView.setModuleName(module.getName());
		long viewId = ViewAPI.addView(defaultView, AccountUtil.getCurrentOrg().getOrgId());
		defaultView.setId(viewId);
		
		
		return false;
	}	
}
