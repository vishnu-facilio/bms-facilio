package com.facilio.bmsconsole.util;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.dto.IAMUser.AppType;
import com.facilio.accounts.util.AccountConstants.UserType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ContactsContext;
import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.PeopleContext.PeopleType;
import com.facilio.bmsconsole.context.TenantContactContext;
import com.facilio.bmsconsole.context.VendorContactContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;

public class PeopleAPI {

	public static boolean checkForDuplicatePeople(PeopleContext people) throws Exception {
		PeopleContext peopleExisiting = getPeople(people.getEmail());
		if(peopleExisiting != null && people.getId() != peopleExisiting.getId()) {
			return true;
		}
		return false;
	}
	
	public static PeopleContext getPeople(String email) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
		SelectRecordsBuilder<PeopleContext> builder = new SelectRecordsBuilder<PeopleContext>()
														.module(module)
														.beanClass(PeopleContext.class)
														.select(fields)
														;
		
		if(StringUtils.isNotEmpty(email)) {
			builder.andCondition(CriteriaAPI.getCondition("EMAIL", "email", String.valueOf(email), StringOperators.IS));
		}
		
		PeopleContext records = builder.fetchFirst();
		return records;
	}

	
	public static void addPeopleForUser(User user, boolean isRequester) throws Exception {
		
		PeopleContext peopleExisiting = getPeople(user.getEmail());
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.EMPLOYEE);
		
		if(peopleExisiting == null) {
			EmployeeContext people = new EmployeeContext();
			people.setCanBeAssigned(false);
			people.setEmail(user.getEmail());
			people.setName(user.getName());
			people.setActive(true);
			people.setPhone(user.getMobile());
			people.setPeopleType(PeopleType.EMPLOYEE);
			if(!isRequester) {
				people.setAppAccess(true);
			}
			else {
				people.setEmployeePortalAccess(true);
			}
			
			RecordAPI.addRecord(true, Collections.singletonList(people), module, modBean.getAllFields(module.getName()));
		}
		else {
			if(!isRequester) {
				((EmployeeContext)peopleExisiting).setAppAccess(true);
			}
			else {
				((EmployeeContext)peopleExisiting).setEmployeePortalAccess(true);
			}
		}
	}
	
	public static List<Map<String,Object>> getTenantContacts(List<Long> tenantIds) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.TENANT_CONTACT);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField tenantId = fieldMap.get("tenant");
		
		SelectRecordsBuilder<TenantContactContext> builder = new SelectRecordsBuilder<TenantContactContext>()
														.module(module)
														.beanClass(TenantContactContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition(tenantId, tenantIds, PickListOperators.IS))
														;
		
		List<Map<String,Object>> records = builder.getAsProps();
		return records;
		
	}
	
	public static List<VendorContactContext> getVendorContacts(long vendorId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VENDOR_CONTACT);
		
		SelectRecordsBuilder<VendorContactContext> builder = new SelectRecordsBuilder<VendorContactContext>()
														.module(module)
														.beanClass(VendorContactContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(vendorId), NumberOperators.EQUALS))
														;
		
		List<VendorContactContext> records = builder.get();
		return records;
		
	}
	
	public static void addTenantContacts(List<TenantContactContext> contactContexts) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
		
		RecordAPI.addRecord(true, contactContexts, module, modBean.getAllFields(module.getName()));
	}
	
	public static void addVendorContacts(List<VendorContactContext> contactContexts) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);
		
		RecordAPI.addRecord(true, contactContexts, module, modBean.getAllFields(module.getName()));
	}
	
	public static void updateVendorContacts(VendorContactContext contactContexts) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);
		
		RecordAPI.updateRecord(contactContexts, module, modBean.getAllFields(module.getName()));
	}
	
	public static void updateTenantContacts(TenantContactContext contactContexts) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
		
		RecordAPI.updateRecord(contactContexts, module, modBean.getAllFields(module.getName()));
	}

	public static void updateAppAccess(EmployeeContext person, boolean updateContactRecord) throws Exception {
		User user = AccountUtil.getUserBean().getUser(person.getEmail());
		if(person.isAppAccess()) {
			if(user != null) {
				if(!user.isUserVerified() || !user.isInviteAcceptStatus() ) {
					AccountUtil.getUserBean().resendInvite(user.getOuid());
				}
				else {
					AccountUtil.getUserBean().enableUser(user.getOuid());
				}
			}
			else {
				User userNew = new User();
				userNew.setName(person.getName());
				userNew.setEmail(person.getEmail());
				userNew.setMobile(person.getPhone());
				if(person.getRoleId() <= 0) {
					throw new IllegalArgumentException("Role cannot be null");
				}
				userNew.setRoleId(person.getRoleId());
				userNew.setDomainName("app");
				userNew.setUserVerified(false);
				userNew.setInviteAcceptStatus(false);
				userNew.setInvitedTime(System.currentTimeMillis());
				userNew.setTimezone(AccountUtil.getCurrentAccount().getTimeZone());
				userNew.setLanguage(AccountUtil.getCurrentUser().getLanguage());
				userNew.setUserType(UserType.USER.getValue());
				AccountUtil.getUserBean().createUser(AccountUtil.getCurrentOrg().getOrgId(), userNew);
			}
		}
		else {
			AccountUtil.getUserBean().disableUser(user.getOuid());
		}
		
		if(updateContactRecord) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.EMPLOYEE);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.EMPLOYEE);
		
			RecordAPI.updateRecord(person, module, fields);
		}
		
	}
	
	public static void addUserAsRequester(PeopleContext contact, int appType) throws Exception {
		User user = new User();
		user.setEmail(contact.getEmail());
		user.setPhone(contact.getPhone());
		user.setName(contact.getName());
		user.setUserVerified(false);
		user.setInviteAcceptStatus(false);
		user.setInvitedTime(System.currentTimeMillis());
		user.setAppType(appType);
		
		long userId = AccountUtil.getUserBean().inviteRequester(AccountUtil.getCurrentOrg().getOrgId(), user, true, false);
		user.setId(userId);
	}
	
	
}
