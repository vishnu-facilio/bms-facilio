package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.VendorContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
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
		
		builder.fetchLookup(requesterField);
		
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
		
		builder.fetchLookup(requesterField);
		
		List<ContactsContext> records = builder.get();
		return records;
		
	}
	
	public static void updatePortalUserAccess(ContactsContext contact) throws Exception {
		
		
		if(contact != null && contact.getRequester() != null && contact.getRequester().getOuid() > 0) {
			if(!contact.isPortalAccessNeeded()) {
				AccountUtil.getUserBean().disableUser(contact.getRequester().getOuid());
			}
			else {
				User user = AccountUtil.getUserBean().getPortalUser(contact.getRequester().getOuid());
				AccountUtil.getUserBean().resendInvite(user.getOuid());
			}
		}
		else {
			addUserAsRequester(contact);
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

		long userId = AccountUtil.getUserBean().inviteRequester(AccountUtil.getCurrentOrg().getOrgId(), user, true, false);
		contact.setId(userId);
	}
	
	public static int updatePrimaryContact(ContactsContext contact) throws Exception{
        
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
											.andCondition(CriteriaAPI.getIdCondition(contact.getId(), module));
									
		Map<String, Object> value = new HashMap<>();
		value.put("isPrimaryContact", true);
		int count = updateBuilder.update(value);
		return count;
			
	}

	
}
