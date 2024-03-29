package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;

public class AddTenantCommand extends GenericAddModuleDataCommand {

//	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		if (context.get(FacilioConstants.ContextNames.RECORD) != null) {
		
		TenantContext tenant = (TenantContext) context.get(FacilioConstants.ContextNames.RECORD);
		List<Long> spaceIds = (ArrayList<Long>)context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		List<ContactsContext> tenantContacts = (List<ContactsContext>)context.get(FacilioConstants.ContextNames.CONTACTS);
		
	   // TenantsAPI.addTenantLogo(tenant);
		super.executeCommand(context);
		tenant.setId((Long)context.get(FacilioConstants.ContextNames.RECORD_ID));
		TenantsAPI.addUtilityMapping(tenant,spaceIds);
		context.put(FacilioConstants.ContextNames.TENANT, tenant);
		if(CollectionUtils.isNotEmpty(tenantContacts)) {
			for(ContactsContext contact : tenantContacts) {
				contact.setTenant(tenant);
			}
		}
		
	  }
		 return false;
	}

}






































