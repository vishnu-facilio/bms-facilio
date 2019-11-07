package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.constants.FacilioConstants;

public class UpdatePrimaryContactCommand extends FacilioCommand {
	
	
	public boolean executeCommand(Context context) throws Exception {
		
		ContactsContext contact = (ContactsContext)context.get(FacilioConstants.ContextNames.CONTACT);
		contact.setIsPrimaryContact(!contact.isPrimaryContact());
		int rowsUpdated = ContactsAPI.updatePrimaryContact(contact);
		context.put(FacilioConstants.ContextNames.ROWS_UPDATED,rowsUpdated);
		return false;
		
	}

}