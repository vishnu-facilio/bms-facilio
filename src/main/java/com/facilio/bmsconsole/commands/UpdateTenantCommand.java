package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class UpdateTenantCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		int rowsUpdated = TenantsAPI.updateTenant((TenantContext) context.get(TenantsAPI.TENANT_CONTEXT));
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED,rowsUpdated);
		return false;
	}

}
