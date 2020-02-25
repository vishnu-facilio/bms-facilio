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
		Integer accessChangeFor = (Integer)context.get(FacilioConstants.ContextNames.ACCESS_NEEDED_FOR);
		if(CollectionUtils.isNotEmpty(clientContacts)) {
			if(accessChangeFor == 1) {
				for(ClientContactContext tc : clientContacts) {
					PeopleAPI.updateClientContactAppPortalAccess(tc, AppDomainType.CLIENT_PORTAL);
				}
			}
		}
		return false;
	}

}
