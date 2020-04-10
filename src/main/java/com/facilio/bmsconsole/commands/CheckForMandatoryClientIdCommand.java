package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.ClientContactContext;
import com.facilio.bmsconsole.context.PeopleContext.PeopleType;
import com.facilio.constants.FacilioConstants;

public class CheckForMandatoryClientIdCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ClientContactContext> clientContacts = (List<ClientContactContext>) context.get(FacilioConstants.ContextNames.RECORD_LIST);
		
		if(CollectionUtils.isNotEmpty(clientContacts)) {
			for(ClientContactContext cc : clientContacts) {
				cc.setPeopleType(PeopleType.CLIENT_CONTACT);
				if(cc.isClientPortalAccess()) {
					context.put(FacilioConstants.ContextNames.ACCESS_NEEDED_FOR, 1);
				}
				if(cc.getClient() == null || cc.getClient().getId() <=0 ) {
					throw new IllegalArgumentException("Client Contact must have a client id associated");
				}
			}
		}
		return false;
	}

}
