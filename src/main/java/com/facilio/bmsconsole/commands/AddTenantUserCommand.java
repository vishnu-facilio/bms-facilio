package com.facilio.bmsconsole.commands;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.List;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.ContactsContext.ContactType;
import com.facilio.bmsconsole.context.PeopleContext.PeopleType;
import com.facilio.bmsconsole.context.TenantContactContext;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
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
		Boolean spaceUpdate = (Boolean)context.getOrDefault(FacilioConstants.ContextNames.SPACE_UPDATE, false);
		FacilioModule tcModule = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
		List<FacilioField> tcFields = modBean.getAllFields(tcModule.getName());
		if(spaceUpdate == null) {
			spaceUpdate = false;
		}
		
		EventType eventType = (EventType)context.getOrDefault(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		
		if(eventType == EventType.CREATE) {
			ContactsContext primarycontact = addDefaultTenantPrimaryContact(tenant);
			RecordAPI.addRecord(true, Collections.singletonList(primarycontact), module, fields);
		}
		else {
			if(StringUtils.isNotEmpty(tenant.getPrimaryContactPhone()) && !spaceUpdate) {
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
			}
		}
		if( !spaceUpdate) {
			if(StringUtils.isNotEmpty(tenant.getPrimaryContactName())) {
				TenantContactContext tc = getDefaultTenantContact(tenant);
				List<TenantContactContext> primarycontatsIfAny = PeopleAPI.getTenantContacts(tc.getTenant().getId(), true);
				TenantContactContext tenantPrimaryContact = null;
				if (CollectionUtils.isNotEmpty(primarycontatsIfAny)) {
					tenantPrimaryContact = primarycontatsIfAny.get(0);
				}
				PeopleAPI.addParentPrimaryContactAsPeople(tc, tcModule, tcFields, tc.getTenant().getId(), tenantPrimaryContact);
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
	
	private TenantContactContext getDefaultTenantContact(TenantContext tenant) throws Exception {
		TenantContactContext tc = new TenantContactContext();
		tc.setName(tenant.getPrimaryContactName());
		tc.setEmail(tenant.getPrimaryContactEmail());
		tc.setPhone(tenant.getPrimaryContactPhone());
		tc.setPeopleType(PeopleType.TENANT_CONTACT);
		tc.setSiteId(tenant.getSiteId());
		tc.setTenant(tenant);
		tc.setIsPrimaryContact(true);
		
		return tc;
	}
}
