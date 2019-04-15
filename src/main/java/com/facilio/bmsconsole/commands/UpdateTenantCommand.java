package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class UpdateTenantCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		TenantContext tenant = (TenantContext) context.get(TenantsAPI.TENANT_CONTEXT);
		List<Long> spaceIds = (ArrayList<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		
		if(tenant.getStatusEnum() != TenantContext.Status.ACTIVE) {
			throw new IllegalArgumentException("Cannot update an inactive tenant");
		}
		int rowsUpdated = TenantsAPI.updateTenant(tenant, spaceIds);
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED,rowsUpdated);
		return false;
	}

}
