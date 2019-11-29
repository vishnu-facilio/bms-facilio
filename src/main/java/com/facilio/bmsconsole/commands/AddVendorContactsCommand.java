package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.context.ContactsContext.ContactType;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddVendorContactsCommand extends FacilioCommand{

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		VendorContext vendor = (VendorContext) context.get(FacilioConstants.ContextNames.RECORD);
		List<ContactsContext> contacts = (List<ContactsContext>)context.get(FacilioConstants.ContextNames.CONTACTS);
		if(vendor != null && CollectionUtils.isNotEmpty(contacts)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			ArrayList<ContactsContext> contactsToBeUpdated = new ArrayList<ContactsContext>();
			for(ContactsContext contact : contacts) {
				
				if(contact.getEmail() == null || contact.getEmail().isEmpty()) {
					contact.setEmail(contact.getPhone());
				}
				contact.setVendor(vendor);
				contact.setContactType(ContactType.VENDOR);
				ContactsAPI.updatePortalUserAccess(contact, false);
				if(contact.getId() > 0) {
					RecordAPI.updateRecord(contact, module, fields);
				}
				else {
					RecordAPI.addRecord(true, Collections.singletonList(contact), module, fields);
				}
			}
		}
		
		return false;
	}

}
