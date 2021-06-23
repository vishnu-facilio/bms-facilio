package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;

public class UpdateTenantCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		TenantContext tenant = (TenantContext) context.get(TenantsAPI.TENANT_CONTEXT);
//		List<Long> spaceIds = (ArrayList<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		List<Long> spaceIds = new ArrayList<>();
		
		if(tenant.getStatusEnum() != null && tenant.getStatusEnum() != TenantContext.Status.ACTIVE) {
			throw new IllegalArgumentException("Cannot update an inactive tenant");
		}
		int rowsUpdated = TenantsAPI.updateTenant(tenant, spaceIds);
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED,rowsUpdated);
		return false;
	}

}
