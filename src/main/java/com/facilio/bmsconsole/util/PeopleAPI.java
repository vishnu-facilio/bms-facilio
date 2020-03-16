package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ClientContactContext;
import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.PeopleContext.PeopleType;
import com.facilio.bmsconsole.context.TenantContactContext;
import com.facilio.bmsconsole.context.VendorContactContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
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

	
	public static void addPeopleForUser(User user) throws Exception {
		
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
			people.setAppAccess(true);
			
			RecordAPI.addRecord(true, Collections.singletonList(people), module, modBean.getAllFields(module.getName()));
		
			FacilioField peopleId = new FacilioField();
			peopleId.setName("peopleId");
			peopleId.setDataType(FieldType.NUMBER);
			peopleId.setColumnName("PEOPLE_ID");
			peopleId.setModule(AccountConstants.getAppOrgUserModule());
			
			List<FacilioField> fields = new ArrayList<>();
			fields.add(peopleId);
			
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(AccountConstants.getAppOrgUserModule().getTableName())
					.fields(fields);
			
			updateBuilder.andCondition(CriteriaAPI.getCondition("ORG_USERID", "ouId", String.valueOf(user.getOuid()), NumberOperators.EQUALS));
			
			Map<String, Object> props = new HashMap<>();
			props.put("peopleId", people.getId());
		    updateBuilder.update(props);
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
	
	public static void updateEmployeeAppPortalAccess(EmployeeContext person, AppDomainType appDomainType) throws Exception {
        AppDomain appDomain = IAMAppUtil.getAppDomain(appDomainType);
		
        if(appDomain != null) {
			User user = AccountUtil.getUserBean().getUser(person.getEmail(), appDomain.getDomain());
			if((appDomainType == AppDomainType.FACILIO && person.isAppAccess()) || (appDomainType == AppDomainType.SERVICE_PORTAL && person.isOccupantPortalAccess())) {
				if(user != null) {
					AppUserAPI.addUserInApp(user, appDomain);
				}
				else {
					User newUser = addAppUser(person, appDomain.getDomain());
					AppUserAPI.addUserInApp(newUser, appDomain);
				}
			}
			else {
				AppUserAPI.deleteUserFromApp(user, appDomain);
			}
		}
        else {
        	throw new IllegalArgumentException("Invalid App Domain");
        }
		
	}
	
	public static void updateTenantContactAppPortalAccess(TenantContactContext person, AppDomainType appDomainType) throws Exception {
        AppDomain appDomain = IAMAppUtil.getAppDomain(appDomainType);
		
        if(appDomain != null) {
        	User user = AccountUtil.getUserBean().getUser(person.getEmail(), appDomain.getDomain());
			if((appDomainType == AppDomainType.TENANT_PORTAL && person.isTenantPortalAccess()) || (appDomainType == AppDomainType.SERVICE_PORTAL && person.isOccupantPortalAccess())) {
				if(user != null) {
					AppUserAPI.addUserInApp(user, appDomain);
				}
				else {
					User newUser = addPortalAppUser(person, appDomain.getDomain(), 1);
					
					AppUserAPI.addUserInApp(newUser, appDomain);
				}
			}
			else {
				AppUserAPI.deleteUserFromApp(user, appDomain);
			}
		}
        else {
        	throw new IllegalArgumentException("Invalid App Domain");
        }
		
	}
	
	public static void updateClientContactAppPortalAccess(ClientContactContext person, AppDomainType appDomainType) throws Exception {
        AppDomain appDomain = IAMAppUtil.getAppDomain(appDomainType);
		
        if(appDomain != null) {
        	User user = AccountUtil.getUserBean().getUser(person.getEmail(), appDomain.getDomain());
			if((appDomainType == AppDomainType.CLIENT_PORTAL && person.isClientPortalAccess())) {
				if(user != null) {
					AppUserAPI.addUserInApp(user, appDomain);
				}
				else {
					User newUser = addPortalAppUser(person, appDomain.getDomain(), 1);
					
					AppUserAPI.addUserInApp(newUser, appDomain);
				}
			}
			else {
				AppUserAPI.deleteUserFromApp(user, appDomain);
			}
	    }
        else {
        	throw new IllegalArgumentException("Invalid App Domain");
        }
		
	}
	
	public static void updateVendorContactAppPortalAccess(VendorContactContext person, AppDomainType appDomainType) throws Exception {
        AppDomain appDomain = IAMAppUtil.getAppDomain(appDomainType);
		
        if(appDomain != null) {
        	User user = AccountUtil.getUserBean().getUser(person.getEmail(), appDomain.getDomain());
			if((appDomainType == AppDomainType.VENDOR_PORTAL && person.isVendorPortalAccess())) {
				if(user != null) {
					AppUserAPI.addUserInApp(user, appDomain);
				}
				else {
					User newUser = addPortalAppUser(person, appDomain.getDomain(), appDomain.getId());
					
					AppUserAPI.addUserInApp(newUser, appDomain);
				}
			}
			else {
				AppUserAPI.deleteUserFromApp(user, appDomain);
			}
		}
        else {
        	throw new IllegalArgumentException("Invalid App Domain");
        }
		
	}
	
	public static void deletePeopleUsers(long peopleId) throws Exception {
		List<FacilioField> fields = AccountConstants.getAppOrgUserFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
									.select(fields)
									.table("ORG_Users")
									;
		selectBuilder.andCondition(CriteriaAPI.getCondition("PEOPLE_ID", "peopleId", String.valueOf(peopleId), NumberOperators.EQUALS));
		
		List<Map<String, Object>> list = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(list)) {
			AccountUtil.getUserBean().deleteUser(AccountUtil.getCurrentOrg().getOrgId(), (long)list.get(0).get("uid"));
		}
	}
		
	
	public static User addAppUser(PeopleContext person, String appDomain) throws Exception {
		if(StringUtils.isEmpty(appDomain)) {
			throw new IllegalArgumentException("Invalid App Domain");
		}
		User user = new User();
		user.setEmail(person.getEmail());
		user.setPhone(person.getPhone());
		user.setName(person.getName());
		user.setUserVerified(false);
		user.setInviteAcceptStatus(false);
		user.setInvitedTime(System.currentTimeMillis());
		user.setPeopleId(person.getId());
		user.setUserType(AccountConstants.UserType.USER.getValue());
		
		
		AccountUtil.getUserBean().createUser(AccountUtil.getCurrentOrg().getOrgId(), user, 1, appDomain);
		return user;
		
	}
	
	public static User addPortalAppUser(PeopleContext people, String appDomain, long identifier) throws Exception {
		if(StringUtils.isEmpty(appDomain)) {
			throw new IllegalArgumentException("Invalid App Domain");
		}
		User user = new User();
		user.setEmail(people.getEmail());
		user.setPhone(people.getPhone());
		user.setName(people.getName());
		user.setUserVerified(false);
		user.setInviteAcceptStatus(false);
		user.setInvitedTime(System.currentTimeMillis());
		user.setPeopleId(people.getId());
		
		
		AccountUtil.getUserBean().inviteRequester(AccountUtil.getCurrentOrg().getOrgId(), user, true, false, appDomain, identifier);
		return user;
	}
	
	public static void rollUpPrimarycontactFields(long parentId, PeopleContext people) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = null;
		List<FacilioField> fields = new ArrayList<FacilioField>();
		List<FacilioField> updatedfields = new ArrayList<FacilioField>();
		
		if(people.getPeopleTypeEnum() == PeopleType.TENANT_CONTACT) {
			module = modBean.getModule(FacilioConstants.ContextNames.TENANT);
			fields.addAll(modBean.getAllFields(FacilioConstants.ContextNames.TENANT));
			Map<String, FacilioField> contactFieldMap = FieldFactory.getAsMap(fields);
			
			FacilioField primaryEmailField = contactFieldMap.get("primaryContactEmail");
			FacilioField primaryPhoneField = contactFieldMap.get("primaryContactPhone");
			FacilioField primaryNameField = contactFieldMap.get("primaryContactName");
			
			updatedfields.add(primaryEmailField);
			updatedfields.add(primaryPhoneField);
			updatedfields.add(primaryNameField);
		}
		else if(people.getPeopleTypeEnum() == PeopleType.VENDOR_CONTACT) {
			module = modBean.getModule(FacilioConstants.ContextNames.VENDORS);
			fields.addAll(modBean.getAllFields(FacilioConstants.ContextNames.VENDORS));
			Map<String, FacilioField> contactFieldMap = FieldFactory.getAsMap(fields);
			
			FacilioField primaryEmailField = contactFieldMap.get("primaryContactEmail");
			FacilioField primaryPhoneField = contactFieldMap.get("primaryContactPhone");
			FacilioField primaryNameField = contactFieldMap.get("primaryContactName");
			
			updatedfields.add(primaryEmailField);
			updatedfields.add(primaryPhoneField);
			updatedfields.add(primaryNameField);
		}
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
											.table(module.getTableName())
											.fields(updatedfields)
											.andCondition(CriteriaAPI.getIdCondition(parentId, module));
									
		Map<String, Object> value = new HashMap<>();
		value.put("primaryContactEmail", people.getEmail());
		value.put("primaryContactPhone", people.getPhone());
		value.put("primaryContactName", people.getName());
		updateBuilder.update(value);

	}
	
	public static int unMarkPrimaryContact(PeopleContext person, long parentId) throws Exception{
        
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = null;
		List<FacilioField> fields = new ArrayList<FacilioField>();
		List<FacilioField> updatedfields = new ArrayList<FacilioField>();
		com.facilio.db.criteria.Condition condition = null;
		
		if(person.getPeopleTypeEnum() == PeopleType.TENANT_CONTACT) {
			module = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
			fields.addAll(modBean.getAllFields(FacilioConstants.ContextNames.TENANT_CONTACT));
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			FacilioField primaryContactField = fieldMap.get("isPrimaryContact");
			updatedfields.add(primaryContactField);
			condition = CriteriaAPI.getCondition("TENANT_ID", "tenant", String.valueOf(parentId), NumberOperators.EQUALS);
			
			
		}
		else if(person.getPeopleTypeEnum() == PeopleType.VENDOR_CONTACT) {
			module = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);
			fields.addAll(modBean.getAllFields(FacilioConstants.ContextNames.VENDOR_CONTACT));
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			FacilioField primaryContactField = fieldMap.get("isPrimaryContact");
			updatedfields.add(primaryContactField);
			condition = CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(parentId), NumberOperators.EQUALS);
		}
		else if(person.getPeopleTypeEnum() == PeopleType.CLIENT_CONTACT) {
			module = modBean.getModule(FacilioConstants.ContextNames.CLIENT_CONTACT);
			fields.addAll(modBean.getAllFields(FacilioConstants.ContextNames.CLIENT_CONTACT));
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			FacilioField primaryContactField = fieldMap.get("isPrimaryContact");
			updatedfields.add(primaryContactField);
			condition = CriteriaAPI.getCondition("CLIENT_ID", "client", String.valueOf(parentId), NumberOperators.EQUALS);
		}
		
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(module.getTableName())
				.fields(updatedfields)
				.andCondition(CriteriaAPI.getCondition("IS_PRIMARY_CONTACT", "isPrimaryContact", "true", BooleanOperators.IS))
				
				;

		updateBuilder.andCondition(CriteriaAPI.getCondition("ID", "peopleId", String.valueOf(person.getId()), NumberOperators.NOT_EQUALS));
		updateBuilder.andCondition(condition);							
		
		Map<String, Object> value = new HashMap<>();
		value.put("isPrimaryContact", false);
		int count = updateBuilder.update(value);
		return count;
			
	}

}
