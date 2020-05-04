package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.bmsconsole.context.TenantContactContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UpdateChangeSet;

public class UpdateTenantAppPortalAccessCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<TenantContactContext> tenantContacts = (List<TenantContactContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>) context.get(FacilioConstants.ContextNames.CHANGE_SET);
		if(CollectionUtils.isNotEmpty(tenantContacts) && MapUtils.isNotEmpty(changeSet)) {
			for(TenantContactContext tc : tenantContacts) {
				List<UpdateChangeSet> changes = changeSet.get(tc.getId());
				if(CollectionUtils.isNotEmpty(changes) && RecordAPI.checkChangeSet(changes, "isTenantPortalAccess", FacilioConstants.ContextNames.TENANT_CONTACT)) {
					PeopleAPI.updateTenantContactAppPortalAccess(tc, FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
				}
				if(CollectionUtils.isNotEmpty(changes) && RecordAPI.checkChangeSet(changes, "isOccupantPortalAccess", FacilioConstants.ContextNames.TENANT_CONTACT)) {
					PeopleAPI.updatePeoplePortalAccess(tc, FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
				}
			}
		}
		return false;
	}

}
