package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.constants.FacilioConstants;

public class AddContactsAsRequesterCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ContactsContext> contactsList = (List<ContactsContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
//		if(CollectionUtils.isNotEmpty(contactsList)) {
//			for(ContactsContext contact : contactsList) {
//				if(contact.isPortalAccessNeeded()) {
//					ContactsAPI.addUserAsRequester(contact);
//				}
//			}
//		}
		return false;
	}

}
