package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContactsContext;
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
		ContactsContext contact = (ContactsContext) context.get(FacilioConstants.ContextNames.CONTACT);
		if(contact != null) {
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
		
		return false;
		
		
		
	}

}
