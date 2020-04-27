package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ClientContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.PeopleAPI;

public class UserValueGenerator extends ValueGenerator{

	@Override
	public String generateValueForCondition(int appType) {
		try {
			if(appType == AppDomainType.FACILIO.getIndex()) {
				return String.valueOf(AccountUtil.getCurrentUser().getId());
				 
			}
			else if(appType == AppDomainType.TENANT_PORTAL.getIndex()) {
				TenantContext tenant = PeopleAPI.getTenantForUser(AccountUtil.getCurrentUser().getId());
				if(tenant != null) {
					return String.valueOf(tenant.getId());
				}
			}
			else if(appType == AppDomainType.VENDOR_PORTAL.getIndex()) {
				VendorContext vendor = PeopleAPI.getVendorForUser(AccountUtil.getCurrentUser().getId());
				if(vendor != null) {
					return String.valueOf(vendor.getId());
				}
			}
			else if(appType == AppDomainType.CLIENT_PORTAL.getIndex()) {
				ClientContext client = PeopleAPI.getClientForUser(AccountUtil.getCurrentUser().getId());
				if(client != null) {
					return String.valueOf(client.getId());
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

}
