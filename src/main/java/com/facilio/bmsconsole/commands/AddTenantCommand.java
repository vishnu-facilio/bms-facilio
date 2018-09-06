package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;

public class AddTenantCommand implements Command {

//	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		long tenant = TenantsAPI.addTenant((TenantContext) context.get(TenantsAPI.TENANT_CONTEXT));
		TenantContext tenantContext = TenantsAPI.getTenant(tenant, true);
		context.put(FacilioConstants.ContextNames.USER, tenantContext.getContactInfo());
		context.put(TenantsAPI.TENANT_CONTEXT, tenantContext);
		return false;
	}

}






































