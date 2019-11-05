package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;

public class UpdatePrimaryContactCommand extends FacilioCommand {
	
	
	public boolean executeCommand(Context context) throws Exception {
		//TenantContext tenant = (TenantContext) context.get(TenantsAPI.TENANT_CONTEXT);
		ContactsContext contact = (ContactsContext)context.get(FacilioConstants.ContextNames.CONTACT);
		Long tenantId = (Long)context.get(FacilioConstants.ContextNames.RECORD_ID);
		int rowsUpdated = TenantsAPI.updateTenantPrimaryContact(contact, tenantId);
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED,rowsUpdated);
		return false;
		
		
		
	}

}