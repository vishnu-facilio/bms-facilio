package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;

public class AddTenantCommand extends GenericAddModuleDataCommand {

//	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		if (context.get(FacilioConstants.ContextNames.RECORD) != null) {
		
		TenantContext tenant = (TenantContext) context.get(FacilioConstants.ContextNames.RECORD);
		User user = tenant.getContact();
		long currentOccupancy = (Long) context.get(FacilioConstants.ContextNames.TOTAL_CURRENT_OCCUPANCY);
		tenant.setOccupancy(currentOccupancy);
		long orgid = AccountUtil.getCurrentOrg().getOrgId();
		user.setOrgId(orgid);
		if(user.getEmail() == null || user.getEmail().isEmpty()) {
			user.setEmail(user.getMobile());
		}

		long userId = AccountUtil.getUserBean().inviteRequester(orgid, user);
	    tenant.getContact().setId(userId);
    	TenantsAPI.addTenantLogo(tenant);
		super.execute(context);
		tenant.setId((Long)context.get(FacilioConstants.ContextNames.RECORD_ID));
		TenantsAPI.addUtilityMapping(tenant);
		context.put(FacilioConstants.ContextNames.TENANT, tenant);
		
		context.put(FacilioConstants.ContextNames.USER, tenant.getContact());
	  }
		 return false;
	}

}






































