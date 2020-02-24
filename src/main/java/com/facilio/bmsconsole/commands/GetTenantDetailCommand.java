package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;


public class GetTenantDetailCommand extends FacilioCommand{
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			TenantContext tenant = TenantsAPI.getTenant((Long)context.get(FacilioConstants.ContextNames.ID));
			context.put(FacilioConstants.ContextNames.TENANT, tenant);
		}
		return false;
	}
}
