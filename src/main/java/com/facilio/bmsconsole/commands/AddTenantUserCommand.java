package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;

public class AddTenantUserCommand implements Command {
	
	
	public boolean execute(Context context) throws Exception {
		//TenantContext tenant = (TenantContext) context.get(TenantsAPI.TENANT_CONTEXT);
		User user = (User)context.get(FacilioConstants.ContextNames.USER);
		Long tenantId = (Long)context.get(FacilioConstants.ContextNames.RECORD_ID);
		TenantsAPI.addTenantContact(user, tenantId);
		return false;
		
		
		
	}

}
