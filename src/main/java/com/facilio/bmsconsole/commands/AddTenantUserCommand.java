package com.facilio.bmsconsole.commands;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class AddTenantUserCommand implements Command {
	
	
	public boolean execute(Context context) throws Exception {
		//TenantContext tenant = (TenantContext) context.get(TenantsAPI.TENANT_CONTEXT);
		User user = (User)context.get(FacilioConstants.ContextNames.USER);
		Long tenantId = (Long)context.get(FacilioConstants.ContextNames.RECORD_ID);
		TenantsAPI.addTenantContact(user, tenantId);
		return false;
		
		
		
	}

}
