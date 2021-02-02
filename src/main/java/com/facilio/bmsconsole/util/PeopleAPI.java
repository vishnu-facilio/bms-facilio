package com.facilio.bmsconsole.util;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.PeopleContext.PeopleType;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAppUtil;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

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
	
	public static PeopleContext getPeopleForId(long id) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
		SelectRecordsBuilder<PeopleContext> builder = new SelectRecordsBuilder<PeopleContext>()
														.module(module)
														.beanClass(PeopleContext.class)
														.select(fields)
														;
		
		builder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(id), NumberOperators.EQUALS));
		
		PeopleContext records = builder.fetchFirst();
		return records;
	}

	
	public static void addPeopleForUser(User user, boolean isSignup) throws Exception {
		
		PeopleContext peopleExisiting = getPeople(user.getEmail());
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.EMPLOYEE);
		long pplId = 1;
		if(peopleExisiting == null) {
			EmployeeContext people = new EmployeeContext();
			people.setIsAssignable(false);
			people.setEmail(user.getEmail());
			people.setName(user.getName());
			people.setActive(true);
			people.setPhone(user.getMobile());
			people.setPeopleType(PeopleType.EMPLOYEE);
			people.setIsAppAccess(true);
			people.setRoleId(user.getRoleId());
			
			//special handling for signup because employee gets added even before the default module script gets executed.hence the last localid seems to be null
			if(isSignup) {
				people.setLocalId(1);
			}
			RecordAPI.addRecord(!isSignup, Collections.singletonList(people), module, modBean.getAllFields(module.getName()));
			pplId = people.getId();
		}
		else {
			EmployeeContext emp = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(peopleExisiting), EmployeeContext.class);
			emp.setIsAppAccess(true);
			RecordAPI.updateRecord(emp, module, modBean.getAllFields(module.getName()));
			pplId =emp.getId();
		}
		updatePeopleIdInOrgUsers(pplId, user.getOuid());
		
	}
	
	
	public static void addPeopleForRequester(User user) throws Exception {
		
		PeopleContext peopleExisiting = getPeople(user.getEmail());
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
		long pplId = -1;
		if(peopleExisiting == null) {
			PeopleContext people = new PeopleContext();
			people.setEmail(user.getEmail());
			people.setName(user.getName());
			people.setActive(true);
			people.setPhone(user.getMobile());
			people.setPeopleType(PeopleType.OCCUPANT);
			people.setIsOccupantPortalAccess(true);
			
			
			RecordAPI.addRecord(true, Collections.singletonList(people), module, modBean.getAllFields(module.getName()));
			pplId = people.getId();
			updatePeopleIdInOrgUsers(pplId, user.getOuid());
			
		}
//		else {
//			PeopleContext emp = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(peopleExisiting), PeopleContext.class);
//			emp.setIsOccupantPortalAccess(true);
//			RecordAPI.updateRecord(emp, module, modBean.getAllFields(module.getName()));
//			pplId = emp.getId();
//		}
//		updatePeopleIdInOrgUsers(pplId, user.getOuid());
		
		
	}
	
	public static void updatePeopleIdInOrgUsers(long pplId, long ouId) throws Exception {
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
		
		updateBuilder.andCondition(CriteriaAPI.getCondition("ORG_USERID", "ouId", String.valueOf(ouId), NumberOperators.EQUALS));
		
		Map<String, Object> props = new HashMap<>();
		props.put("peopleId", pplId);
	    updateBuilder.update(props);
	
	}
	
	public static List<TenantContactContext> getTenantContacts(Long tenantId, boolean fetchPrimarycontact) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.TENANT_CONTACT);
		
		SelectRecordsBuilder<TenantContactContext> builder = new SelectRecordsBuilder<TenantContactContext>()
														.module(module)
														.beanClass(TenantContactContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition("TENANT_ID", "tenant", String.valueOf(tenantId), NumberOperators.EQUALS));
														;
		if(fetchPrimarycontact) {
			builder.andCondition(CriteriaAPI.getCondition("IS_PRIMARY_CONTACT", "isPrimaryContact", "true", BooleanOperators.IS));
		}
		List<TenantContactContext> records = builder.get();
		return records;
		
	}

	public static List<TenantContactContext> getTenantContactsList(List<Long> idList) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.TENANT_CONTACT);

		SelectRecordsBuilder<TenantContactContext> builder = new SelectRecordsBuilder<TenantContactContext>()
				.module(module)
				.beanClass(TenantContactContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(module), idList, NumberOperators.EQUALS));
		;

		List<TenantContactContext> records = builder.get();
		return records;

	}
	
	public static List<VendorContactContext> getVendorContacts(long vendorId, boolean fetchPrimaryContact) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.VENDOR_CONTACT);
		
		SelectRecordsBuilder<VendorContactContext> builder = new SelectRecordsBuilder<VendorContactContext>()
														.module(module)
														.beanClass(VendorContactContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(vendorId), NumberOperators.EQUALS))
														;
		
		if(fetchPrimaryContact) {
			builder.andCondition(CriteriaAPI.getCondition("IS_PRIMARY_CONTACT", "isPrimaryContact", "true", BooleanOperators.IS));
		}
	
		
		List<VendorContactContext> records = builder.get();
		return records;
		
	}
	
	public static List<ClientContactContext> getClientContacts(long clientId, boolean fetchPrimaryContact) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CLIENT_CONTACT);
		List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.CLIENT_CONTACT);
		
		SelectRecordsBuilder<ClientContactContext> builder = new SelectRecordsBuilder<ClientContactContext>()
														.module(module)
														.beanClass(ClientContactContext.class)
														.select(fields)
														.andCondition(CriteriaAPI.getCondition("CLIENT_ID", "client", String.valueOf(clientId), NumberOperators.EQUALS))
														;
		
		if(fetchPrimaryContact) {
			builder.andCondition(CriteriaAPI.getCondition("IS_PRIMARY_CONTACT", "isPrimaryContact", "true", BooleanOperators.IS));
		}
	
		
		List<ClientContactContext> records = builder.get();
		return records;
		
	}
	
	
	public static void updateEmployeeAppPortalAccess(EmployeeContext person, String linkname) throws Exception {
	   
		EmployeeContext existingPeople = (EmployeeContext) RecordAPI.getRecord(FacilioConstants.ContextNames.EMPLOYEE, person.getId());
		if(StringUtils.isEmpty(existingPeople.getEmail()) && (existingPeople.isAppAccess())){
			throw new IllegalArgumentException("Email Id associated with this contact is empty");
		}
		if(StringUtils.isNotEmpty(existingPeople.getEmail())) {
		
			if(existingPeople.isAppAccess() && existingPeople.getRoleId() <= 0) {
				throw new IllegalArgumentException("Role cannot be null");
			}
		
			AppDomain appDomain = null;
			long appId = -1;
			appId = ApplicationApi.getApplicationIdForLinkName(linkname);
			appDomain = ApplicationApi.getAppDomainForApplication(appId);
					
			if(appDomain != null) {
				User user = AccountUtil.getUserBean().getUser(existingPeople.getEmail(), appDomain.getIdentifier());
				if((linkname.equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP) && existingPeople.isAppAccess())) {
					if(user != null) {
						user.setApplicationId(appId);
						user.setAppDomain(appDomain);
						ApplicationApi.addUserInApp(user, false);
						if(user.getRoleId() != existingPeople.getRoleId()) {
							user.setRoleId(existingPeople.getRoleId());
							AccountUtil.getUserBean().updateUser(user);
						}
					}
					else {
						addAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
					}
				}
				else {
					if(user != null) {
						ApplicationApi.deleteUserFromApp(user, appId);
					}
				}
			}
	        else {
	        	throw new IllegalArgumentException("Invalid App Domain");
	        }
		}	
		
	}
	
	public static void updateTenantContactAppPortalAccess(TenantContactContext person, String appLinkName) throws Exception {
		
		TenantContactContext existingPeople = (TenantContactContext) RecordAPI.getRecord(FacilioConstants.ContextNames.TENANT_CONTACT, person.getId());
		if(StringUtils.isEmpty(existingPeople.getEmail()) && (existingPeople.isTenantPortalAccess())){
			throw new IllegalArgumentException("Email Id associated with this contact is empty");
		}
		if(StringUtils.isNotEmpty(existingPeople.getEmail())) {
			long appId = -1;
			appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);
			AppDomain appDomain = ApplicationApi.getAppDomainForApplication(appId);
			if(appDomain != null) {
	        	User user = AccountUtil.getUserBean().getUser(existingPeople.getEmail(), appDomain.getIdentifier());
	        	if((appLinkName.equals(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP) && existingPeople.isTenantPortalAccess())) {
					if(user != null) {
						user.setAppDomain(appDomain);
						user.setApplicationId(appId);
				    	ApplicationApi.addUserInApp(user, false);
					}
					else {
						addPortalAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP, appDomain.getIdentifier());
					}
				}
				else {
					if(user != null) {
						ApplicationApi.deleteUserFromApp(user, appId);
					}
				}
			}
	        else {
	        	throw new IllegalArgumentException("Invalid App Domain");
	        }
		}
	}

	public static void updatePeoplePortalAccess(PeopleContext person, String linkName) throws Exception {
		updatePeoplePortalAccess(person, linkName, false);
	}
	
	public static void updatePeoplePortalAccess(PeopleContext person, String linkName, boolean verifyUser) throws Exception {
	
		PeopleContext existingPeople = (PeopleContext) RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE, person.getId());
		if(StringUtils.isEmpty(existingPeople.getEmail()) && (existingPeople.isOccupantPortalAccess())){
			throw new IllegalArgumentException("Email Id associated with this contact is empty");
		}
		if(StringUtils.isNotEmpty(existingPeople.getEmail())) {
			AppDomain appDomain = null;
			long appId = ApplicationApi.getApplicationIdForLinkName(linkName); 
			appDomain = ApplicationApi.getAppDomainForApplication(appId);
			if(appDomain != null) {
	        	User user = AccountUtil.getUserBean().getUser(existingPeople.getEmail(), appDomain.getIdentifier());
	        	if((linkName.equals(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP) && existingPeople.isOccupantPortalAccess())) {
					if(user != null) {
						user.setAppDomain(appDomain);
						user.setApplicationId(appId);
				    	ApplicationApi.addUserInApp(user, false);
					}
					else {
						addPortalAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP, appDomain.getIdentifier(), verifyUser);
					}
				}
				else {
					if(user != null) {
						ApplicationApi.deleteUserFromApp(user, appId);
					}
				}
			}
	        else {
	        	throw new IllegalArgumentException("Invalid App Domain");
	        }
		}
	}
	
	public static void updateClientContactAppPortalAccess(ClientContactContext person, String linkName) throws Exception {
		
		ClientContactContext existingPeople = (ClientContactContext) RecordAPI.getRecord(FacilioConstants.ContextNames.CLIENT_CONTACT, person.getId());
		
		if(StringUtils.isEmpty(existingPeople.getEmail()) && (existingPeople.isClientPortalAccess())){
			throw new IllegalArgumentException("Email Id associated with this contact is empty");
		}
		if(StringUtils.isNotEmpty(existingPeople.getEmail())) {
			AppDomain appDomain = null;
			long appId = ApplicationApi.getApplicationIdForLinkName(linkName);
			appDomain = ApplicationApi.getAppDomainForApplication(appId);
			if(appDomain != null) {
	        	User user = AccountUtil.getUserBean().getUser(existingPeople.getEmail(), appDomain.getIdentifier());
	        	if((linkName.equals(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP) && existingPeople.isClientPortalAccess())) {
					if(user != null) {
						user.setAppDomain(appDomain);
						user.setApplicationId(appId);
					    ApplicationApi.addUserInApp(user, false);
					}
					else {
						addPortalAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP, appDomain.getIdentifier());
					}
				}
				else {
					if(user != null) {
						ApplicationApi.deleteUserFromApp(user, appId);
					}
				}
		    }
	        else {
	        	throw new IllegalArgumentException("Invalid App Domain");
	        }
			
		}	
	}
	
	public static void updateVendorContactAppPortalAccess(VendorContactContext person, String linkName) throws Exception {
		
		VendorContactContext existingPeople = (VendorContactContext) RecordAPI.getRecord(FacilioConstants.ContextNames.VENDOR_CONTACT, person.getId());
		
		if(StringUtils.isEmpty(existingPeople.getEmail()) && (existingPeople.isVendorPortalAccess())){
			throw new IllegalArgumentException("Email Id associated with this contact is empty");
		}
		if(StringUtils.isNotEmpty(existingPeople.getEmail())) {
			AppDomain appDomain = null;
			long appId = ApplicationApi.getApplicationIdForLinkName(linkName);
			appDomain = ApplicationApi.getAppDomainForApplication(appId); 
			if(appDomain != null) {
	        	User user = AccountUtil.getUserBean().getUser(existingPeople.getEmail(), appDomain.getIdentifier());
	        	if((linkName.equals(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP) && existingPeople.isVendorPortalAccess())) {
					if(user != null) {
						user.setAppDomain(appDomain);
						user.setApplicationId(appId);
					    ApplicationApi.addUserInApp(user, false);
					}
					else {
						User newUser = addPortalAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP, appDomain.getIdentifier());
						newUser.setAppDomain(appDomain);
			    	}
				}
				else {
					if(user != null) {
						ApplicationApi.deleteUserFromApp(user, appId);
					}
				}
			}
	        else {
	        	throw new IllegalArgumentException("Invalid App Domain");
	        }
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
			AccountUtil.getUserBean().deleteUser((long)list.get(0).get("ouid"), false);
		}
	}

	public static void deletePeopleUsers(List<Long> peopleIds) throws Exception {
		List<FacilioField> fields = AccountConstants.getAppOrgUserFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users")
				;
		selectBuilder.andCondition(CriteriaAPI.getCondition("PEOPLE_ID", "peopleId", String.valueOf(peopleIds), NumberOperators.EQUALS));

		List<Map<String, Object>> list = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(list)) {
			for(Map<String, Object> map : list) {
				AccountUtil.getUserBean().deleteUser((long) map.get("ouid"), false);
			}
		}
	}

	
	public static int deletePeopleForUser(long ouId) throws Exception {
		long peopleId = PeopleAPI.getPeopleIdForUser(ouId);
		if(peopleId > 0) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			
			com.facilio.modules.DeleteRecordBuilder<PeopleContext> deleteBuilder = new com.facilio.modules.DeleteRecordBuilder<PeopleContext>()
					.module(modBean.getModule(FacilioConstants.ContextNames.PEOPLE));
			deleteBuilder.andCondition(CriteriaAPI.getIdCondition(peopleId, ModuleFactory.getPeopleModule()));
			return deleteBuilder.markAsDelete();
		
		}
		return -1;
	}
		
	
	public static long getPeopleIdForUser(long ouId) throws Exception {
		List<FacilioField> fields = AccountConstants.getAppOrgUserFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
									.select(fields)
									.table("ORG_Users")
									;
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_USERID", "ouId", String.valueOf(ouId), NumberOperators.EQUALS));
		
		List<Map<String, Object>> list = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(list)) {
			if(list.get(0).get("peopleId") != null) {
				return (long)list.get(0).get("peopleId");
			}
		}
		return -1;
	}
	
	public static User addAppUser(PeopleContext existingPeople, String linkName) throws Exception {
		if(StringUtils.isEmpty(linkName)) {
			throw new IllegalArgumentException("Invalid Link name");
		}
		long appId = ApplicationApi.getApplicationIdForLinkName(linkName);
		AppDomain appDomainObj = ApplicationApi.getAppDomainForApplication(appId);
		User user = new User();
		user.setEmail(existingPeople.getEmail());
		user.setPhone(existingPeople.getPhone());
		user.setName(existingPeople.getName());
		user.setUserVerified(false);
		user.setInviteAcceptStatus(false);
		user.setInvitedTime(System.currentTimeMillis());
		user.setPeopleId(existingPeople.getId());
		user.setUserType(AccountConstants.UserType.USER.getValue());
		user.setRoleId(existingPeople.getRoleId());
		
		user.setApplicationId(appId);
		user.setAppDomain(appDomainObj);
		
		AccountUtil.getUserBean().createUser(AccountUtil.getCurrentOrg().getOrgId(), user, appDomainObj.getIdentifier(), true, false);
		return user;
		
	}

	public static User addPortalAppUser(PeopleContext existingPeople, String linkName, String identifier) throws Exception {
		return addPortalAppUser(existingPeople, linkName, identifier, false);
	}
	
	public static User addPortalAppUser(PeopleContext existingPeople, String linkName, String identifier, boolean verifyUser) throws Exception {
		if(StringUtils.isEmpty(linkName)) {
			throw new IllegalArgumentException("Invalid link name");
		}
		long appId = ApplicationApi.getApplicationIdForLinkName(linkName);
		
		User user = new User();
		user.setEmail(existingPeople.getEmail());
		user.setPhone(existingPeople.getPhone());
		user.setName(existingPeople.getName());
		user.setUserVerified(verifyUser);
		user.setInviteAcceptStatus(false);
		user.setInvitedTime(System.currentTimeMillis());
		user.setPeopleId(existingPeople.getId());
		user.setUserType(AccountConstants.UserType.REQUESTER.getValue());
		
		user.setApplicationId(appId);
		user.setAppDomain(ApplicationApi.getAppDomainForApplication(appId));
		
		if (!verifyUser) {
			AccountUtil.getUserBean().inviteRequester(AccountUtil.getCurrentOrg().getOrgId(), user, true, false, identifier, false, false);
		} else {
			AccountUtil.getUserBean().createUser(AccountUtil.getCurrentOrg().getOrgId(), user, identifier, false, false);
		}

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
		
		if(person instanceof TenantContactContext) {
			module = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
			fields.addAll(modBean.getAllFields(FacilioConstants.ContextNames.TENANT_CONTACT));
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			FacilioField primaryContactField = fieldMap.get("isPrimaryContact");
			updatedfields.add(primaryContactField);
			condition = CriteriaAPI.getCondition("TENANT_ID", "tenant", String.valueOf(parentId), NumberOperators.EQUALS);
			
			
		}
		else if(person instanceof VendorContactContext) {
			module = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);
			fields.addAll(modBean.getAllFields(FacilioConstants.ContextNames.VENDOR_CONTACT));
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
			FacilioField primaryContactField = fieldMap.get("isPrimaryContact");
			updatedfields.add(primaryContactField);
			condition = CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(parentId), NumberOperators.EQUALS);
		}
		else if(person instanceof ClientContactContext) {
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
	
	public static void addParentPrimaryContactAsPeople(PeopleContext tc, FacilioModule module, List<FacilioField> fields, long parentId, PeopleContext primaryContactForParent) throws Exception {
		
		if(primaryContactForParent != null) {
			if(StringUtils.isNotEmpty(tc.getEmail())) {
				if(StringUtils.isEmpty(primaryContactForParent.getEmail())) {
					tc.setId(primaryContactForParent.getId());
					updatePeopleRecord(tc, module, fields);
					return;
				}
				else if(primaryContactForParent.getEmail().equals(tc.getEmail())) {
					tc.setId(primaryContactForParent.getId());
					RecordAPI.updateRecord(tc, module, fields);
					return;
				}
				else {
					addPeopleRecord(tc, module, fields, parentId);
					return;
				}
			}
			else {
				tc.setId(primaryContactForParent.getId());
				RecordAPI.updateRecord(tc, module, fields);
				return;
			}
		}
		else {
			if(StringUtils.isNotEmpty(tc.getEmail())) {
				addPeopleRecord(tc, module, fields, parentId);
				return;
			}
			RecordAPI.addRecord(true, Collections.singletonList(tc), module, fields);
		}
		
	}
	
	public static void updatePeopleRecord(PeopleContext ppl, FacilioModule module, List<FacilioField> fields) throws Exception {
		PeopleContext peopleExisting = getPeople(ppl.getEmail());
		if(peopleExisting == null) {
			RecordAPI.updateRecord(ppl, module, fields);
			return;
		}
		throw new IllegalArgumentException("People with the same email id already exists");
	
	}
	
	public static void addPeopleRecord(PeopleContext ppl, FacilioModule module, List<FacilioField> fields, long parentId) throws Exception {
		PeopleContext peopleExisting = getPeople(ppl.getEmail());
		if(peopleExisting == null) {
			RecordAPI.addRecord(true, Collections.singletonList(ppl), module, fields);
			PeopleAPI.unMarkPrimaryContact(ppl, parentId);
			
			return;
		}
		throw new IllegalArgumentException("People with the same email id already exists");
	
	}

	public static TenantContext getTenantForUser(long ouId) throws Exception {
		long pplId = PeopleAPI.getPeopleIdForUser(ouId);
		if(pplId <= 0) {
			throw new IllegalArgumentException("Invalid People Id mapped with ORG_User");
		}
		TenantContactContext tc = (TenantContactContext)RecordAPI.getRecord(FacilioConstants.ContextNames.TENANT_CONTACT, pplId);
		if (tc != null && tc.getTenant() != null && tc.getTenant().getId() > 0) {
			return (TenantContext)RecordAPI.getRecord(FacilioConstants.ContextNames.TENANT, tc.getTenant().getId());
		}
		return null;
	}

	public static VendorContext getVendorForUser(long ouId) throws Exception {
		long pplId = PeopleAPI.getPeopleIdForUser(ouId);
		if(pplId <= 0) {
			throw new IllegalArgumentException("Invalid People Id mapped with ORG_User");
		}
		VendorContactContext vc = (VendorContactContext)RecordAPI.getRecord(FacilioConstants.ContextNames.VENDOR_CONTACT, pplId);
		if (vc != null && vc.getVendor() != null && vc.getVendor().getId() > 0) {
			return (VendorContext)RecordAPI.getRecord(FacilioConstants.ContextNames.VENDORS, vc.getVendor().getId());
		}
	
		return null;

	}

	public static ClientContext getClientForUser(long ouId) throws Exception {
		long pplId = PeopleAPI.getPeopleIdForUser(ouId);
		if(pplId <= 0) {
			throw new IllegalArgumentException("Invalid People Id mapped with ORG_User");
		}
		ClientContactContext cc = (ClientContactContext)RecordAPI.getRecord(FacilioConstants.ContextNames.CLIENT_CONTACT, pplId);
		if (cc != null && cc.getClient() != null && cc.getClient().getId() > 0) {
			return (ClientContext)RecordAPI.getRecord(FacilioConstants.ContextNames.CLIENT, cc.getClient().getId());
		}
	
		return null;

	}
	
	public static void rollUpModulePrimarycontactFields(long id, String moduleName, String name, String email, String phone) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		List<FacilioField> fields = modBean.getAllFields(moduleName);
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
											.fields(fields)
											.andCondition(CriteriaAPI.getIdCondition(id, module));
									
		Map<String, Object> value = new HashMap<>();
		value.put("primaryContactEmail", email);
		value.put("primaryContactPhone", phone);
		value.put("primaryContactName", name);
		updateBuilder.update(value);

	}


}
