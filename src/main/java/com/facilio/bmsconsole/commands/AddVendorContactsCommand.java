package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.VendorContext;
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
		if(vendor != null) {
			List<ContactsContext> contacts = vendor.getVendorContacts();
			if(CollectionUtils.isNotEmpty(contacts)) {
				for(ContactsContext contact : contacts) {
					if(contact.getEmail() == null || contact.getEmail().isEmpty()) {
						contact.setEmail(contact.getPhone());
					}
			
					if(contact.isPortalAccessNeeded()) {
						TenantsAPI.addTenantUserAsRequester(contact);
					}
					ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
					List<FacilioField> fields = modBean.getAllFields(module.getName());
					RecordAPI.addRecord(true, Collections.singletonList(contact), module, fields);
				}
			}
		}
		
		return false;
	}

}
