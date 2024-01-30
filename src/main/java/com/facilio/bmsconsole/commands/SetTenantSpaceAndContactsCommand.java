package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.stream.Collectors;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.tenant.TenantSpaceContext;
import com.facilio.bmsconsole.util.SpaceAPI;
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
		List<TenantSpaceContext> spaces = TenantsAPI.fetchTenantSpaces(tenant.getId());
		BaseSpaceContext site = SpaceAPI.getBaseSpace(tenant.getSiteId());
		tenant.setSite(site);
		if (CollectionUtils.isNotEmpty(spaces)) {
			tenant.setTenantSpaces(spaces);
			tenant.setSpaces(spaces.stream().map(TenantSpaceContext::getSpace).collect(Collectors.toList()));
		}
		  List<TenantContactContext> tenantContacts = PeopleAPI.getTenantContacts(tenant.getId(), false);
		  tenant.setPeopleTenantContacts(tenantContacts);


		
		return false;
	}

}
