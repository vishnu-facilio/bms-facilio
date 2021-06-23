package com.facilio.bmsconsole.commands;

import java.util.Collections;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;

public class DeleteTenantSpaceRelationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		TenantContext tenant = (TenantContext)context.get(FacilioConstants.ContextNames.TENANT);
		if (tenant == null) {
			tenant = (TenantContext) context.get(TenantsAPI.TENANT_CONTEXT);
		}
		Boolean spacesUpdate = (Boolean) context.get(FacilioConstants.ContextNames.SPACE_UPDATE);
		Long id = tenant.getId();
		if ((spacesUpdate != null && spacesUpdate) || (tenant.getSpaces() != null && tenant.getSpaces().size() > 0)) {
			if (id != null && id > 0) {
	            TenantsAPI.deleteTenantSpace(Collections.singletonList(id));
	        }
		}
		
		return false;
	}
}
