package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;

public class AddTenantLogoCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		TenantContext tenant = (TenantContext)context.get(FacilioConstants.ContextNames.TENANT);
		if(tenant != null) {
			long fileId = TenantsAPI.addTenantLogo(tenant);
			context.put(FacilioConstants.ContextNames.FILE_ID, fileId);
		}
		return false;
	}

}
