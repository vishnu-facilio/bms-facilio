package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.bmsconsole.context.TenantContactContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;

public class UpdateTenantAppPortalAccessCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<TenantContactContext> tenantContacts = (List<TenantContactContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		Long tenantPortalappId = (Long)context.getOrDefault(FacilioConstants.ContextNames.TENANT_PORTAL_APP_ID, -1l);
		Long servicePortalappId = (Long)context.getOrDefault(FacilioConstants.ContextNames.SERVICE_PORTAL_APP_ID, -1l);
		
		if(CollectionUtils.isNotEmpty(tenantContacts)) {
			for(TenantContactContext tc : tenantContacts) {
				PeopleAPI.updateTenantContactAppPortalAccess(tc, AppDomainType.TENANT_PORTAL, tenantPortalappId);
				PeopleAPI.updatePeoplePortalAccess(tc, AppDomainType.SERVICE_PORTAL, servicePortalappId);
			}
		}
		return false;
	}

}
