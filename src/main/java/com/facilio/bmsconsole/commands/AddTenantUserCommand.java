package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.bmsconsole.context.ContactsContext.ContactType;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddTenantUserCommand extends FacilioCommand {
	
	
	public boolean executeCommand(Context context) throws Exception {
		TenantContext tenant = (TenantContext) context.get(FacilioConstants.ContextNames.RECORD);
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		EventType eventType = (EventType)context.getOrDefault(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		
		if(eventType == EventType.CREATE) {
			ContactsContext primarycontact = addDefaultTenantPrimaryContact(tenant);
			RecordAPI.addRecord(true, Collections.singletonList(primarycontact), module, fields);
			if(AccountUtil.isFeatureEnabled(FeatureLicense.PEOPLE_CONTACTS)) {
				PeopleAPI.addTenantPrimaryContactAsPeople(primarycontact);
			}
		}
		else {
			if(StringUtils.isNotEmpty(tenant.getPrimaryContactPhone())) {
				ContactsContext existingcontactForPhone = ContactsAPI.getContactforPhone(tenant.getPrimaryContactPhone(), tenant.getId(), false);
				if(existingcontactForPhone == null) {
					existingcontactForPhone = addDefaultTenantPrimaryContact(tenant);
					RecordAPI.addRecord(true, Collections.singletonList(existingcontactForPhone), module, fields);
				}
				else {
					existingcontactForPhone.setName(tenant.getPrimaryContactName());
					existingcontactForPhone.setEmail(tenant.getPrimaryContactEmail());
					RecordAPI.updateRecord(existingcontactForPhone, module, fields);
				}
				if(AccountUtil.isFeatureEnabled(FeatureLicense.PEOPLE_CONTACTS)) {
					PeopleAPI.addTenantPrimaryContactAsPeople(existingcontactForPhone);
				}
			}
		}
		
		

//		if(tenant != null && CollectionUtils.isNotEmpty(contacts)) {
//			for(ContactsContext contact : contacts)	{
//				contact.setTenant(tenant);
//				contact.setContactType(ContactType.TENANT);
//			
//				ContactsAPI.updatePortalUserAccess(contact, false);
//				if(contact.getId() > 0) {
//					RecordAPI.updateRecord(contact, module, fields);
//				}
//				else {
//					RecordAPI.addRecord(true, Collections.singletonList(contact), module, fields);
//				}
//			
//			}
//			
//		}
		return false;
	}

	private ContactsContext addDefaultTenantPrimaryContact(TenantContext tenant) throws Exception {
		ContactsAPI.unMarkPrimaryContact(-1, tenant.getId(), ContactType.TENANT);
		ContactsContext contact = new ContactsContext();
		contact.setName(tenant.getPrimaryContactName() != null ? tenant.getPrimaryContactName() : tenant.getName());
		contact.setContactType(ContactType.TENANT);
		contact.setTenant(tenant);
		contact.setEmail(tenant.getPrimaryContactEmail());
		contact.setPhone(tenant.getPrimaryContactPhone());
		contact.setIsPrimaryContact(true);
		contact.setIsPortalAccessNeeded(false);
		return contact;
	}
}
