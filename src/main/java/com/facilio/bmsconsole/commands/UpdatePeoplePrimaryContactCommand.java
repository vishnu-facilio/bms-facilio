package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import com.chargebee.models.Contact;
import com.facilio.bmsconsoleV3.context.communityfeatures.ContactDirectoryContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.bmsconsole.context.ClientContactContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.PeopleContext.PeopleType;
import com.facilio.bmsconsole.context.TenantContactContext;
import com.facilio.bmsconsole.context.VendorContactContext;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.constants.FacilioConstants;

public class UpdatePeoplePrimaryContactCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<PeopleContext> peopleList = (List<PeopleContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(peopleList)) {
			for(PeopleContext people : peopleList) {
				long parentId = -1;
				if(people instanceof TenantContactContext && ((TenantContactContext) people).getTenant() != null && ((TenantContactContext)people).isPrimaryContact()) {
					parentId = ((TenantContactContext)people).getTenant().getId();
					PeopleAPI.unMarkPrimaryContact(people, parentId);
					PeopleAPI.rollUpModulePrimarycontactFields(parentId, FacilioConstants.ContextNames.TENANT, people.getName(), people.getEmail(), people.getPhone());
				}
				else if(people instanceof VendorContactContext && ((VendorContactContext) people).getVendor() != null && ((VendorContactContext)people).isPrimaryContact()) {
					parentId = ((VendorContactContext)people).getVendor().getId();
					PeopleAPI.unMarkPrimaryContact(people, parentId);
					PeopleAPI.rollUpModulePrimarycontactFields(parentId, FacilioConstants.ContextNames.VENDORS, people.getName(), people.getEmail(), people.getPhone());
				}
				else if(people instanceof ClientContactContext && ((ClientContactContext) people).getClient() != null && ((ClientContactContext)people).isPrimaryContact()) {
					parentId = ((ClientContactContext)people).getClient().getId();
					PeopleAPI.unMarkPrimaryContact(people, parentId);
					PeopleAPI.rollUpModulePrimarycontactFields(parentId, FacilioConstants.ContextNames.CLIENT, people.getName(), people.getEmail(), people.getPhone());
				}

				//update Contact directory fields if people is associated
				List<ContactDirectoryContext> contactList = CommunityFeaturesAPI.getContacts(people.getId());
				if(CollectionUtils.isNotEmpty(contactList)){
					List<Long> ids = new ArrayList<>();
					for(ContactDirectoryContext contact : contactList){
						ids.add(contact.getId());
					}
					CommunityFeaturesAPI.updateContactDirectoryList(ids, people);
				}
			}
		}
		return false;
	}

}
