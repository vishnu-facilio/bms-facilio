package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;

import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.bmsconsole.context.ClientContactContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;

public class UpdateClientAppPortalAccessCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ClientContactContext> clientContacts = (List<ClientContactContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		Long appId = (Long)context.getOrDefault(FacilioConstants.ContextNames.CLIENT_PORTAL_APP_ID, -1l);
		if(CollectionUtils.isNotEmpty(clientContacts)) {
			for(ClientContactContext tc : clientContacts) {
				PeopleAPI.updateClientContactAppPortalAccess(tc, AppDomainType.CLIENT_PORTAL, appId);
			}
		}
		return false;
	}

}
