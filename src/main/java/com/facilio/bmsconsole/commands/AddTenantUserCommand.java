package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.context.ContactsContext.ContactType;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddTenantUserCommand extends FacilioCommand {
	
	
	public boolean executeCommand(Context context) throws Exception {
		TenantContext tenant = (TenantContext) context.get(FacilioConstants.ContextNames.RECORD);
		List<ContactsContext> contacts = (List<ContactsContext>) context.get(FacilioConstants.ContextNames.CONTACTS);
		if(tenant != null && CollectionUtils.isNotEmpty(contacts)) {
			for(ContactsContext contact : contacts)	{
				contact.setTenant(tenant);
				contact.setContactType(ContactType.TENANT);
			
				if(contact.isPortalAccessNeeded()) {
					ContactsAPI.addUserAsRequester(contact);
				}
			
			}
			
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
			List<FacilioField> fields = modBean.getAllFields(module.getName());
			RecordAPI.addRecord(true, contacts, module, fields);
		}
		
		return false;
		
		
		
	}

}
