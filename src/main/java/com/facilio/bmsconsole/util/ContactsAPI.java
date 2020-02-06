package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;

public class ContactsAPI {

	public static List<Map<String,Object>> getTenantContacts(List<Long> tenantIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.CONTACT);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField tenantId = fieldMap.get("tenant");
		
		SelectRecordsBuilder<ContactsContext> builder = new SelectRecordsBuilder<ContactsContext>()
														.module(module)
														.beanClass(ContactsContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition(tenantId, tenantIds, PickListOperators.IS))
														;
		
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		
		LookupField requesterField = (LookupField) fieldsAsMap.get("requester");
		
		builder.fetchSupplement(requesterField);
		
		List<Map<String,Object>> records = builder.getAsProps();
		return records;
		
	}
	
	public static List<ContactsContext> getVendorContacts(Long vendorId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.CONTACT);
		
		SelectRecordsBuilder<ContactsContext> builder = new SelectRecordsBuilder<ContactsContext>()
														.module(module)
														.beanClass(ContactsContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(vendorId), NumberOperators.EQUALS))
														;
		
		Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
		
		LookupField requesterField = (LookupField) fieldsAsMap.get("requester");
		
		builder.fetchSupplement(requesterField);
		
		List<ContactsContext> records = builder.get();
		return records;
		
	}
	
	public static ContactsContext getContactforPhone(String phone, long id, boolean isVendor) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.CONTACT);
		
		SelectRecordsBuilder<ContactsContext> builder = new SelectRecordsBuilder<ContactsContext>()
														.module(module)
														.beanClass(ContactsContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition("PHONE", "phone", phone, StringOperators.IS))
														
														;
		if(isVendor) {
			builder.andCondition(CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(id), NumberOperators.EQUALS));
		}
		else {
			builder.andCondition(CriteriaAPI.getCondition("TENANT_ID", "tenant", String.valueOf(id), NumberOperators.EQUALS));
		}
		ContactsContext records = builder.fetchFirst();
		return records;
		
	}
	
	public static void updatePortalUserAccess(ContactsContext contact, boolean updateContactRecord) throws Exception {
		
		if(contact != null && contact.getRequester() != null && contact.getRequester().getOuid() > 0) {
			if(!contact.isPortalAccessNeeded()) {
				AccountUtil.getUserBean().disableUser(contact.getRequester().getOuid());
			}
			else {
				User user = AccountUtil.getUserBean().getPortalUser(contact.getRequester().getUid());
				if(!user.isUserVerified() || !user.isInviteAcceptStatus() ) {
					AccountUtil.getUserBean().resendInvite(user.getOuid());
				}
				else {
					AccountUtil.getUserBean().enableUser(contact.getRequester().getOuid());
				}
			}
		}
		else {
			if(contact.getIsPortalAccessNeeded()) {
				addUserAsRequester(contact);
			}
		}
		if(updateContactRecord) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.CONTACT);
		
			RecordAPI.updateRecord(contact, module, fields);
		}
		
	}
	
	public static void addUserAsRequester(ContactsContext contact) throws Exception {
		User user = new User();
		user.setEmail(contact.getEmail());
		user.setPhone(contact.getPhone());
		user.setName(contact.getName());
		user.setUserVerified(false);
		user.setInviteAcceptStatus(false);
		user.setInvitedTime(System.currentTimeMillis());

		if(contact.getContactType() == 1) {
			user.setAppType(2);
		}
		else if(contact.getContactType() == 2) {
			user.setAppType(3);
		}
		else if(contact.getContactType() == 3) {
			user.setAppType(1);
		}
		long userId = AccountUtil.getUserBean().inviteRequester(AccountUtil.getCurrentOrg().getOrgId(), user, true, false);
		user.setId(userId);
		contact.setRequester(user);
	}
	
	public static int updatePrimaryContact(ContactsContext contact) throws Exception{
        
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.CONTACT);
		Map<String, FacilioField> contactFieldMap = FieldFactory.getAsMap(fields);
		List<FacilioField> updatedfields = new ArrayList<FacilioField>();
		FacilioField primaryContactField = contactFieldMap.get("isPrimaryContact");
		if(contact.getVendor() != null && contact.isPrimaryContact()) {
			unMarkPrimaryContact(contact.getVendor().getId(), true);
			rollUpVendorPrimarycontactFields(contact.getVendor().getId(), contact);
		}
		else if(contact.getTenant() != null && contact.isPrimaryContact()) {
			unMarkPrimaryContact(contact.getTenant().getId(), false);
			rollUpTenantPrimarycontactFields(contact.getTenant().getId(), contact);
		}
		updatedfields.add(primaryContactField);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
											.table(module.getTableName())
											.fields(updatedfields)
											.andCondition(CriteriaAPI.getIdCondition(contact.getId(), module));
									
		Map<String, Object> value = new HashMap<>();
		value.put("isPrimaryContact", contact.getIsPrimaryContact());
		int count = updateBuilder.update(value);
		return count;
			
	}
	
	public static void rollUpVendorPrimarycontactFields(long vendorId, ContactsContext contact) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VENDORS);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.VENDORS);
		Map<String, FacilioField> contactFieldMap = FieldFactory.getAsMap(fields);
		List<FacilioField> updatedfields = new ArrayList<FacilioField>();
		
		FacilioField primaryEmailField = contactFieldMap.get("primaryContactEmail");
		FacilioField primaryPhoneField = contactFieldMap.get("primaryContactPhone");
		FacilioField primaryNameField = contactFieldMap.get("primaryContactName");
		
		updatedfields.add(primaryEmailField);
		updatedfields.add(primaryPhoneField);
		updatedfields.add(primaryNameField);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
											.table(module.getTableName())
											.fields(updatedfields)
											.andCondition(CriteriaAPI.getIdCondition(vendorId, module));
									
		Map<String, Object> value = new HashMap<>();
		value.put("primaryContactEmail", contact.getEmail());
		value.put("primaryContactPhone", contact.getPhone());
		value.put("primaryContactName", contact.getName());
		updateBuilder.update(value);

	}
	
	public static void rollUpTenantPrimarycontactFields(long tenantId, ContactsContext contact) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT);
		Map<String, FacilioField> contactFieldMap = FieldFactory.getAsMap(fields);
		List<FacilioField> updatedfields = new ArrayList<FacilioField>();
		
		FacilioField primaryEmailField = contactFieldMap.get("primaryContactEmail");
		FacilioField primaryPhoneField = contactFieldMap.get("primaryContactPhone");
		FacilioField primaryNameField = contactFieldMap.get("primaryContactName");
		
		updatedfields.add(primaryEmailField);
		updatedfields.add(primaryPhoneField);
		updatedfields.add(primaryNameField);
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
											.table(module.getTableName())
											.fields(updatedfields)
											.andCondition(CriteriaAPI.getIdCondition(tenantId, module));
									
		Map<String, Object> value = new HashMap<>();
		value.put("primaryContactEmail", contact.getEmail());
		value.put("primaryContactPhone", contact.getPhone());
		value.put("primaryContactName", contact.getName());
		updateBuilder.update(value);

	}

	public static void deleteContactUser(long contactId) throws Exception {
		ContactsContext contact = (ContactsContext) RecordAPI.getRecord(FacilioConstants.ContextNames.CONTACT, contactId);
		if(contact != null && contact.getRequester() != null && contact.getRequester().getOuid() > 0) {
			AccountUtil.getUserBean().deleteUser(contact.getRequester().getOuid());
		}
	}
	
	public static ContactsContext getContactsIdForUser(Long userId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.CONTACT);
		
		SelectRecordsBuilder<ContactsContext> builder = new SelectRecordsBuilder<ContactsContext>()
														.module(module)
														.beanClass(ContactsContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition("REQUESTER_ID", "requester", String.valueOf(userId), NumberOperators.EQUALS))
														;
		
		ContactsContext record = builder.fetchFirst();
		return record;
		
	}
	
	public static int unMarkPrimaryContact(long id, boolean isVendor) throws Exception{
        
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.CONTACT);
		Map<String, FacilioField> contactFieldMap = FieldFactory.getAsMap(fields);
		List<FacilioField> updatedfields = new ArrayList<FacilioField>();
		FacilioField primaryContactField = contactFieldMap.get("isPrimaryContact");
		updatedfields.add(primaryContactField);
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
											.table(module.getTableName())
											.fields(updatedfields)
											.andCondition(CriteriaAPI.getCondition("IS_PRIMARY_CONTACT", "isPrimaryContact", "true", BooleanOperators.IS))
											
											;
		
		if(isVendor) {
			updateBuilder.andCondition(CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(id), NumberOperators.EQUALS));
			
		}
		else {
			updateBuilder.andCondition(CriteriaAPI.getCondition("TENANT_ID", "tenant", String.valueOf(id), NumberOperators.EQUALS));
		}
									
		Map<String, Object> value = new HashMap<>();
		value.put("isPrimaryContact", false);
		int count = updateBuilder.update(value);
		return count;
			
	}
	
	public static boolean checkForDuplicateContact(ContactsContext contact, boolean isPhone) throws Exception {
		ContactsContext contactExisiting = getContact(isPhone ? contact.getPhone() : contact.getEmail(), isPhone);
		if(contactExisiting != null && contact.getId() != contactExisiting.getId()) {
			return true;
		}
		return false;
	}
	
	public static ContactsContext getContact(String email, boolean isPhone) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.CONTACT);
		SelectRecordsBuilder<ContactsContext> builder = new SelectRecordsBuilder<ContactsContext>()
														.module(module)
														.beanClass(ContactsContext.class)
														.select(fields)
														;
		
		if(StringUtils.isNotEmpty(email)) {
			if(isPhone) {
				builder.andCondition(CriteriaAPI.getCondition("PHONE", "phone", String.valueOf(email), StringOperators.IS));
			}
			else {
				builder.andCondition(CriteriaAPI.getCondition("EMAIL", "email", String.valueOf(email), StringOperators.IS));
			}
		}
		
		ContactsContext records = builder.fetchFirst();
		return records;
	}
	
	

	
}
