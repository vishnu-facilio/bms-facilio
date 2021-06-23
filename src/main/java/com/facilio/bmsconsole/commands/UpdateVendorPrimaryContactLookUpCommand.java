package com.facilio.bmsconsole.commands;

import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.ContactsContext.ContactType;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.constants.FacilioConstants;

public class UpdateVendorPrimaryContactLookUpCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ContactsContext> contactList = (List<ContactsContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(contactList)) {
			for(ContactsContext contact : contactList) {
				if(contact.isPrimaryContact() && contact.getContactTypeEnum() == ContactType.VENDOR && contact.getVendor() != null && contact.getVendor().getId() > 0) {
					ContactsAPI.unMarkPrimaryContact(contact.getId(), contact.getVendor().getId(), ContactType.VENDOR);
					ContactsAPI.rollUpVendorPrimarycontactFields(contact.getVendor().getId(), contact);
				}
				else if(contact.isPrimaryContact() && contact.getContactTypeEnum() == ContactType.TENANT && contact.getTenant() != null && contact.getTenant().getId() > 0) {
					ContactsAPI.unMarkPrimaryContact(contact.getId(), contact.getTenant().getId(), ContactType.TENANT);
					ContactsAPI.rollUpTenantPrimarycontactFields(contact.getTenant().getId(), contact);
				} 
				else if(contact.isPrimaryContact() && contact.getContactTypeEnum() == ContactType.CLIENT && contact.getClient() != null && contact.getClient().getId() > 0) {
					ContactsAPI.unMarkPrimaryContact(contact.getId(), contact.getClient().getId(), ContactType.CLIENT);
					ContactsAPI.rollUpClientPrimarycontactFields(contact.getClient().getId(), contact);
				}
			}
		}
		return false;
	}

}
