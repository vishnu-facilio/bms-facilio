package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.PeopleContext.PeopleType;
import com.facilio.bmsconsole.context.TenantContactContext;
import com.facilio.constants.FacilioConstants;

public class CheckForMandatoryTenantIdCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<TenantContactContext> tenantContacts = (List<TenantContactContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(tenantContacts)) {
			for(TenantContactContext tc : tenantContacts) {
				tc.setPeopleType(PeopleType.TENANT_CONTACT);
				if(tc.isTenantPortalAccess()) {
					context.put(FacilioConstants.ContextNames.ACCESS_NEEDED_FOR, 1);
				}
				else if(tc.isOccupantPortalAccess()) {
					context.put(FacilioConstants.ContextNames.ACCESS_NEEDED_FOR, 2);
				}
				if(tc.getTenant() == null || tc.getTenant().getId() <=0 ) {
					throw new IllegalArgumentException("Tenant Contact must have a tenant id associated");
				}
			}
		}
		return false;
	}

}
