package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class GetTenantDetailCommand implements Command{
	@Override
	public boolean execute(Context context) throws Exception {
		
		if (context.get(FacilioConstants.ContextNames.ID) != null) {
			TenantContext tenant = TenantsAPI.fetchTenant((Long)context.get(FacilioConstants.ContextNames.ID));
			context.put(FacilioConstants.ContextNames.TENANT, tenant);
		}
		return false;
	}
}
