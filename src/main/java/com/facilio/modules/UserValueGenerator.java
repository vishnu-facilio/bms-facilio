package com.facilio.modules;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.ClientContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;

public class UserValueGenerator extends ValueGenerator{

	@Override
	public String generateValueForCondition(int appType) {
		try {
			if(appType == AppDomainType.FACILIO.getIndex()) {
				return String.valueOf(AccountUtil.getCurrentUser().getId());
				 
			}
			else if(appType == AppDomainType.TENANT_PORTAL.getIndex()) {
				V3TenantContext tenant = V3PeopleAPI.getTenantForUser(AccountUtil.getCurrentUser().getId());
				if(tenant != null) {
					return String.valueOf(tenant.getId());
				}
			}
			else if(appType == AppDomainType.VENDOR_PORTAL.getIndex()) {
				V3VendorContext vendor = V3PeopleAPI.getVendorForUser(AccountUtil.getCurrentUser().getId());
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
			else if(appType == AppDomainType.SERVICE_PORTAL.getIndex()) {
				if(AccountUtil.getCurrentUser() != null) {
					return String.valueOf(AccountUtil.getCurrentUser().getOuid());
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

}
