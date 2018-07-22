package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;

public class AddTenantCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		TenantsAPI.addTenant((TenantContext) context.get(TenantsAPI.TENANT_CONTEXT));
		return false;
	}

}
