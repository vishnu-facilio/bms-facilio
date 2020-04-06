package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.TenantContactContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;

public class SetTenantSpaceAndContactsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		TenantContext tenant= (TenantContext) context.get(FacilioConstants.ContextNames.TENANT);
		List<BaseSpaceContext> spaces = TenantsAPI.fetchTenantSpaces(tenant.getId());
		if (CollectionUtils.isNotEmpty(spaces)) {
			tenant.setSpaces(spaces);
		}
		if(AccountUtil.isFeatureEnabled(FeatureLicense.PEOPLE_CONTACTS)) {
		  List<TenantContactContext> tenantContacts = PeopleAPI.getTenantContacts(tenant.getId(), false);
		  tenant.setPeopleTenantContacts(tenantContacts);
		}
		else {
			List<ContactsContext> tenantcontacts = TenantsAPI.getTenantContacts(tenant.getId());
			if(CollectionUtils.isNotEmpty(tenantcontacts)) {
				tenant.setTenantContacts(tenantcontacts);
			}
		}
		
		return false;
	}

}
