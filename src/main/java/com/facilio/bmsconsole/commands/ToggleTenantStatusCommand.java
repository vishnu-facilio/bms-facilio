package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class ToggleTenantStatusCommand implements Command {
	
	
	public boolean execute(Context context) throws Exception {
		//TenantContext tenant = (TenantContext) context.get(TenantsAPI.TENANT_CONTEXT);
		int status = (Integer)context.get(FacilioConstants.ContextNames.TENANT_STATUS);
		Long tenantId = (Long)context.get(FacilioConstants.ContextNames.RECORD_ID);
		int rowsUpdated = TenantsAPI.updateTenantStatus(tenantId, status);
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED,rowsUpdated);
		return false;
		
		
		
	}

}