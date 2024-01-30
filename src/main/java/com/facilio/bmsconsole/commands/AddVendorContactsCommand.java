package com.facilio.bmsconsole.commands;

import java.util.Collections;
import java.util.List;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.PeopleAPI;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContactsContext.ContactType;
import com.facilio.bmsconsole.util.ContactsAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;

public class AddVendorContactsCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		// TODO Auto-generated method stub
		VendorContext vendor = (VendorContext) context.get(FacilioConstants.ContextNames.RECORD);
		
		
		List<ContactsContext> contacts = (List<ContactsContext>)context.get(FacilioConstants.ContextNames.CONTACTS);
		EventType eventType = (EventType)context.getOrDefault(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
		 		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
		List<FacilioField> fields = modBean.getAllFields(module.getName());
		
		FacilioModule vcModule = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);
		List<FacilioField> vcFields = modBean.getAllFields(vcModule.getName());
	
		 		
		if(eventType == EventType.CREATE) {
			ContactsContext primarycontact = addDefaultVendorPrimaryContact(vendor);
			RecordAPI.addRecord(true, Collections.singletonList(primarycontact), module, fields);
		}
		else {
			if(StringUtils.isNotEmpty(vendor.getPrimaryContactPhone())) {
				ContactsContext existingcontactForPhone = ContactsAPI.getContactforPhone(vendor.getPrimaryContactPhone(), vendor.getId(), true);
				if(existingcontactForPhone == null) {
					existingcontactForPhone = addDefaultVendorPrimaryContact(vendor);
					RecordAPI.addRecord(true, Collections.singletonList(existingcontactForPhone), module, fields);
				}
				else {
					existingcontactForPhone.setName(vendor.getPrimaryContactName());
					existingcontactForPhone.setEmail(vendor.getPrimaryContactEmail());
					RecordAPI.updateRecord(existingcontactForPhone, module, fields);
				}
			}
		}
		

			if(StringUtils.isNotEmpty(vendor.getPrimaryContactName())) {
				VendorContactContext vc = getDefaultVendorContact(vendor);
				List<VendorContactContext> primarycontatsIfAny = PeopleAPI.getVendorContacts(vc.getVendor().getId(), true);
				VendorContactContext vendorPrimaryContact = null;
				if (CollectionUtils.isNotEmpty(primarycontatsIfAny)) {
					vendorPrimaryContact = primarycontatsIfAny.get(0);
				}
				PeopleAPI.addParentPrimaryContactAsPeople(vc, vcModule, vcFields, vc.getVendor().getId(), vendorPrimaryContact);
			}


		
		return false;
	}
	
	private ContactsContext addDefaultVendorPrimaryContact(VendorContext vendor) throws Exception {
		ContactsAPI.unMarkPrimaryContact(-1, vendor.getId(), ContactType.VENDOR);
		ContactsContext contact = new ContactsContext();
		contact.setName(vendor.getPrimaryContactName() != null ? vendor.getPrimaryContactName() : vendor.getName());
		contact.setContactType(ContactType.VENDOR);
		contact.setVendor(vendor);
		contact.setEmail(vendor.getPrimaryContactEmail());
		contact.setPhone(vendor.getPrimaryContactPhone());
		contact.setIsPrimaryContact(true);
		contact.setIsPortalAccessNeeded(false);
		return contact;
	}
	private VendorContactContext getDefaultVendorContact(VendorContext vendor) throws Exception {
		VendorContactContext tc = new VendorContactContext();
		tc.setName(vendor.getPrimaryContactName());
		tc.setEmail(vendor.getPrimaryContactEmail());
		tc.setPhone(vendor.getPrimaryContactPhone());
		tc.setPeopleType(PeopleContext.PeopleType.VENDOR_CONTACT);
		tc.setVendor(vendor);
		tc.setIsPrimaryContact(true);

		return tc;
	}
}
