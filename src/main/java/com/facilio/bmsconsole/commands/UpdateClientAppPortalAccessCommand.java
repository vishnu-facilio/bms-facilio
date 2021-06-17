package com.facilio.bmsconsole.commands;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.bmsconsole.context.ClientContactContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UpdateChangeSet;

public class UpdateClientAppPortalAccessCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ClientContactContext> clientContacts = (List<ClientContactContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>) context.get(FacilioConstants.ContextNames.CHANGE_SET);
		Long roleId = (Long) context.get(FacilioConstants.ContextNames.ROLE_ID);
		if(CollectionUtils.isNotEmpty(clientContacts) && MapUtils.isNotEmpty(changeSet)) {
			for(ClientContactContext tc : clientContacts) {
				List<UpdateChangeSet> changes = changeSet.get(tc.getId());
				if(CollectionUtils.isNotEmpty(changes) && RecordAPI.checkChangeSet(changes, "isClientPortalAccess", FacilioConstants.ContextNames.CLIENT_CONTACT)) {
					PeopleAPI.updateClientContactAppPortalAccess(tc, FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP, roleId);
				}
			}
		}
		return false;
	}

}
