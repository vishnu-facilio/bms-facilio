package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.constants.FacilioConstants;

public class CheckForContactDuplicationCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ContactsContext> contacts = (List<ContactsContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(contacts)) {
			for(ContactsContext contact : contacts) {
			   if(ContactsAPI.checkForDuplicateContact(contact, true)) {
				   throw new IllegalArgumentException("A contact with the same phone number already exists");
			   }
			}
		}
		return false;
	}

}
