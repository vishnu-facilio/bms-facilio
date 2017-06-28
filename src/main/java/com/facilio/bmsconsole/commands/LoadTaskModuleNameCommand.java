package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.customfields.CFUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.OrgInfo;

public class LoadTaskModuleNameCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		long orgId = OrgInfo.getCurrentOrgInfo().getOrgid();
		String moduleName = CFUtil.getModuleName("Tasks_Objects", orgId);
		context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
		
		return false;
	}

}
