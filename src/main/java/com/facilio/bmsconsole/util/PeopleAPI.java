package com.facilio.bmsconsole.util;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.GroupMember;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.sso.AccountSSO;
import com.facilio.accounts.sso.DomainSSO;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.PermissionSetBean;
import com.facilio.bmsconsole.commands.ExecuteStateFlowCommand;
import com.facilio.bmsconsole.commands.ExecuteStateTransitionsCommand;
import com.facilio.bmsconsole.commands.SetTableNamesCommand;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.PeopleContext.PeopleType;
import com.facilio.bmsconsole.tenant.TenantContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.commands.peoplegroup.PeopleGroupUtils;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class PeopleAPI {
	private static Logger log = LogManager.getLogger(PeopleAPI.class.getName());

	public static boolean checkForDuplicatePeople(PeopleContext people) throws Exception {
		PeopleContext peopleExisiting = getPeople(people.getEmail(), true);
		if (peopleExisiting != null && people.getId() != peopleExisiting.getId()) {
			return true;
		}
		return false;
	}

	public static PeopleContext getPeople(String email) throws Exception {
		return getPeople(email, false);
	}

	public static PeopleContext getPeople(String email, boolean skipScoping) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
		SelectRecordsBuilder<PeopleContext> builder = new SelectRecordsBuilder<PeopleContext>()
				.module(module)
				.beanClass(PeopleContext.class)
				.select(fields);
		if (skipScoping) {
			builder.skipScopeCriteria();
		}

		if (StringUtils.isNotEmpty(email)) {
			builder.andCondition(CriteriaAPI.getCondition("EMAIL", "email", String.valueOf(email), StringOperators.IS));
		}

		PeopleContext records = builder.fetchFirst();
		return records;
	}

	public static PeopleContext getPeopleForId(long id) throws Exception {
		return getPeopleForId(id, false);
	}

	public static PeopleContext getPeopleForId(long id, boolean skipScoping) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
		SelectRecordsBuilder<PeopleContext> builder = new SelectRecordsBuilder<PeopleContext>()
				.module(module)
				.beanClass(PeopleContext.class)
				.select(fields);
		if (skipScoping) {
			builder.skipScopeCriteria();
		}
		builder.andCondition(CriteriaAPI.getCondition("ID", "id", String.valueOf(id), NumberOperators.EQUALS));

		PeopleContext records = builder.fetchFirst();
		return records;
	}


	public static void addPeopleForUser(User user, boolean isSignup) throws Exception {

		PeopleContext peopleExisiting = getPeople(user.getEmail());
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.EMPLOYEE);
		long pplId = 1;
		if (peopleExisiting == null) {
			EmployeeContext people = new EmployeeContext();
			people.setIsAssignable(false);
			V3PeopleAPI.validatePeopleEmail(user.getEmail());
			people.setEmail(user.getEmail());
			people.setName(user.getName());
			people.setActive(true);
			people.setPhone(user.getMobile());
			people.setPeopleType(PeopleType.EMPLOYEE);
			people.setIsAppAccess(true);
			people.setRoleId(user.getRoleId());
			people.setUser(user.getUserStatus());

			people.setLanguage(user.getLanguage());
			//special handling for signup because employee gets added even before the default module script gets executed.hence the last localid seems to be null
			if (isSignup) {
				people.setLocalId(1);
			}
			RecordAPI.addRecord(!isSignup, Collections.singletonList(people), module, modBean.getAllFields(module.getName()));
			pplId = people.getId();
		} else if (peopleExisiting.getPeopleTypeEnum() == PeopleType.EMPLOYEE || peopleExisiting.getPeopleTypeEnum() == PeopleType.OCCUPANT) {

			EmployeeContext emp = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(peopleExisiting), EmployeeContext.class);
			emp.setIsAppAccess(true);
			RecordAPI.updateRecord(emp, module, modBean.getAllFields(module.getName()));
			pplId = emp.getId();
			if (user.getUserStatus() != null) {
				FacilioModule people = Constants.getModBean().getModule(FacilioConstants.ContextNames.PEOPLE);
				V3PeopleContext peopleContext = new V3PeopleContext();
				peopleContext.setId(pplId);
				peopleContext.setUser(user.getUserStatus());
				RecordAPI.updateRecord(FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(peopleContext), V3PeopleContext.class), people, modBean.getAllFields(people.getName()));
			}
		} else {
			throw new RESTException(ErrorCode.VALIDATION_ERROR, "User with this email already exists as people type '" + peopleExisiting.getPeopleTypeEnum().getValue() + "'");
		}
		user.setPeopleId(pplId);
		updatePeopleIdInOrgUsers(pplId, user.getOuid());
		if (user.getGroups() != null) {
			updatePeopleIdInGroupMembers(pplId, user.getOuid());
		}
	}

	public static void updatePeopleOnUserUpdate(User user) throws Exception {
		PeopleContext peopleExisting = getPeople(user.getEmail());
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.EMPLOYEE);
		EmployeeContext emp = FieldUtil.getAsBeanFromMap(FieldUtil.getAsProperties(peopleExisting), EmployeeContext.class);
		emp.setName(user.getName());
		emp.setPhone(user.getMobile());
		emp.setRoleId(user.getRoleId());
		emp.setLanguage(user.getLanguage());
		RecordAPI.updateRecord(emp, module, modBean.getAllFields(module.getName()));
	}

	public static void updatePeopleOnUserStatusChange(User user, Boolean userStatus) throws Exception {
		PeopleContext peopleExisting = getPeopleForId(user.getPeopleId());
		if (peopleExisting == null && StringUtils.isNotEmpty(user.getEmail())) {
			peopleExisting = getPeople(user.getEmail());
		}
		if (peopleExisting != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
			peopleExisting.setUser(userStatus);
			RecordAPI.updateRecord(peopleExisting, module, modBean.getAllFields(module.getName()));
		}
	}

	public static PeopleContext getOrAddRequester(String email) throws Exception {  //used only for service Request Module

		email = MailMessageUtil.getEmailFromPrettifiedFromAddress.apply(email);

		PeopleContext people = PeopleAPI.getPeople(email);

		if (people == null) {
			people = new PeopleContext();
			people.setEmail(email);
			people.setPeopleType(PeopleType.OTHERS);
			people.setName(MailMessageUtil.getNameFromEmail.apply(people.getEmail()));

			FacilioChain c = TransactionChainFactory.addPeopleChain();
			c.getContext().put(FacilioConstants.ContextNames.VERIFY_USER, false);
			c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
			c.getContext().put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
			c.getContext().put(FacilioConstants.ContextNames.WITH_CHANGE_SET, true);

			c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(people));
			c.execute();
		}
		return people;
	}


	public static void addPeopleForRequester(User user) throws Exception {
		try {
			PeopleContext peopleExisiting = getPeople(user.getEmail());
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.EMPLOYEE);
			long pplId = -1;
			if (peopleExisiting == null) {
				EmployeeContext emp = new EmployeeContext();
				V3PeopleAPI.validatePeopleEmail(user.getEmail());
				emp.setEmail(user.getEmail());
				emp.setName(user.getName());
				emp.setActive(true);
				emp.setPhone(user.getMobile());
				emp.setPeopleType(PeopleType.EMPLOYEE);
				emp.setIsOccupantPortalAccess(true);
				emp.setLanguage(user.getLanguage());
				RecordAPI.addRecord(true, Collections.singletonList(emp), module, modBean.getAllFields(module.getName()));
				if (emp.getId() > 0) {
					pplId = emp.getId();
					updatePeopleIdInOrgUsers(pplId, user.getOuid());
					if (user.getGroups() != null) {
						updatePeopleIdInGroupMembers(pplId, user.getOuid());
					}
				}

			}
		} catch (Exception e) {
			log.error("Exception " + e);
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
		try {
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
		} catch (Exception e) {
			log.error("Exception while inviting user" + e);
		}

	}

	public static void updatePeopleIdInGroupMembers(long pplId, long ouId) throws Exception {
		try {
			FacilioModule module = Constants.getModBean().getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP_MEMBER);
			FacilioField peopleId = new FacilioField();
			peopleId.setName("people");
			peopleId.setDataType(FieldType.LOOKUP);
			peopleId.setColumnName("PEOPLE_ID");
			peopleId.setModule(module);

			List<FacilioField> fields = new ArrayList<>();
			fields.add(peopleId);

			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(module.getTableName())
					.fields(fields);

			updateBuilder.andCondition(CriteriaAPI.getCondition("ORG_USERID", "ouId", String.valueOf(ouId), NumberOperators.EQUALS));

			Map<String, Object> props = new HashMap<>();
			props.put("people", pplId);
			updateBuilder.update(props);
		} catch (Exception e) {
			log.error("Exception while inviting user" + e);
		}

	}

	public static List<TenantContactContext> getTenantContacts(Long tenantId, boolean fetchPrimarycontact) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT_CONTACT);

		SelectRecordsBuilder<TenantContactContext> builder = new SelectRecordsBuilder<TenantContactContext>()
				.module(module)
				.beanClass(TenantContactContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("TENANT_ID", "tenant", String.valueOf(tenantId), NumberOperators.EQUALS));
		;
		if (fetchPrimarycontact) {
			builder.andCondition(CriteriaAPI.getCondition("IS_PRIMARY_CONTACT", "isPrimaryContact", "true", BooleanOperators.IS));
		}
		List<TenantContactContext> records = builder.get();
		return records;

	}

	public static List<TenantContactContext> getTenantContactsList(List<Long> idList) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.TENANT_CONTACT);

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
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.VENDOR_CONTACT);

		SelectRecordsBuilder<VendorContactContext> builder = new SelectRecordsBuilder<VendorContactContext>()
				.module(module)
				.beanClass(VendorContactContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(vendorId), NumberOperators.EQUALS));

		if (fetchPrimaryContact) {
			builder.andCondition(CriteriaAPI.getCondition("IS_PRIMARY_CONTACT", "isPrimaryContact", "true", BooleanOperators.IS));
		}


		List<VendorContactContext> records = builder.get();
		return records;

	}

	public static List<ClientContactContext> getClientContacts(long clientId, boolean fetchPrimaryContact) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CLIENT_CONTACT);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.CLIENT_CONTACT);

		SelectRecordsBuilder<ClientContactContext> builder = new SelectRecordsBuilder<ClientContactContext>()
				.module(module)
				.beanClass(ClientContactContext.class)
				.select(fields)
				.andCondition(CriteriaAPI.getCondition("CLIENT_ID", "client", String.valueOf(clientId), NumberOperators.EQUALS));

		if (fetchPrimaryContact) {
			builder.andCondition(CriteriaAPI.getCondition("IS_PRIMARY_CONTACT", "isPrimaryContact", "true", BooleanOperators.IS));
		}


		List<ClientContactContext> records = builder.get();
		return records;

	}

	public static void updateEmployeeAppPortalAccess(EmployeeContext person, String linkname) throws Exception {

		EmployeeContext existingPeople = (EmployeeContext) RecordAPI.getRecord(FacilioConstants.ContextNames.EMPLOYEE, person.getId());
		boolean isSsoEnabled = isSsoEnabledForApplication(linkname);
		if (StringUtils.isEmpty(existingPeople.getEmail()) && (existingPeople.isAppAccess())) {
			throw new IllegalArgumentException("Email Id associated with this contact is empty");
		}
		if (StringUtils.isNotEmpty(existingPeople.getEmail())) {

			AppDomain appDomain = null;
			long appId = -1;
			appId = ApplicationApi.getApplicationIdForLinkName(linkname);
			appDomain = ApplicationApi.getAppDomainForApplication(appId);
			if (appDomain != null) {
				User user = AccountUtil.getUserBean().getUserFromEmail(existingPeople.getEmail(), appDomain.getIdentifier(), AccountUtil.getCurrentOrg().getId(), true);
				if ((linkname.equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP) && existingPeople.isAppAccess())) {
					if (MapUtils.isEmpty(person.getRolesMap()) || !person.getRolesMap().containsKey(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) {
						throw new IllegalArgumentException("Role is mandatory");
					}

					long roleId = person.getRolesMap().get(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
					if (user != null) {
						user.setApplicationId(appId);
						user.setAppDomain(appDomain);
						user.setRoleId(roleId);
						user.setLanguage(person.getLanguage());
						if (isSsoEnabled) {
							user.setUserVerified(true);
							user.setInviteAcceptStatus(true);
						}
						String email = user.getEmail();
						AccountUtil.getUserBean().updateUser(user);
						V3PeopleAPI.enableUser(user);
						//resetting email, bcz email is set null in updateUser method. email is required for sending invite email.
						user.setEmail(email);
						ApplicationApi.addUserInApp(user, false, !isSsoEnabled);

					} else {
						addAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, roleId, !isSsoEnabled);
					}
				} else {
					if (user != null) {
						ApplicationApi.deleteUserFromApp(user, appId);
						V3PeopleAPI.disableUser(existingPeople.getId(), user);
					}
				}
			} else {
				throw new IllegalArgumentException("Invalid App Domain");
			}
		}

	}


	public static void updateTenantContactAppPortalAccess(TenantContactContext person, String appLinkName) throws Exception {

		TenantContactContext existingPeople = (TenantContactContext) RecordAPI.getRecord(FacilioConstants.ContextNames.TENANT_CONTACT, person.getId());
		if (StringUtils.isEmpty(existingPeople.getEmail()) && (existingPeople.isTenantPortalAccess())) {
			throw new IllegalArgumentException("Email Id associated with this contact is empty");
		}
		if (StringUtils.isNotEmpty(existingPeople.getEmail())) {
			long appId = -1;
			appId = ApplicationApi.getApplicationIdForLinkName(appLinkName);
			AppDomain appDomain = ApplicationApi.getAppDomainForApplication(appId);
			if (appDomain != null) {
				long secPolId = -1L;
				if (person.getSecurityPolicyMap() != null) {
					Long securityPolicyId = person.getSecurityPolicyMap().get(appLinkName);
					if (securityPolicyId != null) {
						secPolId = securityPolicyId;
					}
				}
				User user = AccountUtil.getUserBean().getUserFromEmail(existingPeople.getEmail(), appDomain.getIdentifier(), AccountUtil.getCurrentOrg().getId(), true);
				if ((appLinkName.equals(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP) && existingPeople.isTenantPortalAccess())) {
					if (MapUtils.isEmpty(person.getRolesMap()) || !person.getRolesMap().containsKey(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP)) {
						throw new IllegalArgumentException("Role is mandatory");
					}
					long roleId = person.getRolesMap().get(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
					if (user != null) {
						user.setAppDomain(appDomain);
						user.setRoleId(roleId);
						user.setApplicationId(appId);
						user.setSecurityPolicyId(secPolId);
						user.setLanguage(person.getLanguage());
						String email = user.getEmail();
						AccountUtil.getUserBean().updateUser(user);
						V3PeopleAPI.enableUser(user);
						//resetting email, bcz email is set null in updateUser method. email is required for sending invite email.
						user.setEmail(email);
						ApplicationApi.addUserInApp(user, false);
						if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
							addPermissionSetsForPeople(person.getPermissionSets(), person.getId(), appLinkName);
						}
					} else {
						addPortalAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP, appDomain.getIdentifier(), false, roleId, secPolId, person.getPermissionSets());
					}
				} else {
					if (user != null) {
						ApplicationApi.deleteUserFromApp(user, appId);
						V3PeopleAPI.disableUser(existingPeople.getId(), user);
					}
				}
			} else {
				throw new IllegalArgumentException("Invalid App Domain");
			}
		}
	}

	public static void updatePeoplePortalAccess(PeopleContext person, String linkName) throws Exception {
		updatePeoplePortalAccess(person, linkName, false);
	}

	public static void updatePeoplePortalAccess(PeopleContext person, String linkName, boolean verifyUser) throws Exception {

		PeopleContext existingPeople = getPeopleForId(person.getId(), true);
		if (StringUtils.isEmpty(existingPeople.getEmail()) && (existingPeople.isOccupantPortalAccess() || existingPeople.employeePortalAccess())) {
			throw new IllegalArgumentException("Email Id associated with this contact is empty");
		}
		boolean isSsoEnabled = isSsoEnabledForApplication(linkName);
		if (isSsoEnabled) {
			verifyUser = true;
		}
		if (StringUtils.isNotEmpty(existingPeople.getEmail())) {
			AppDomain appDomain = null;
			long appId = ApplicationApi.getApplicationIdForLinkName(linkName);
			appDomain = ApplicationApi.getAppDomainForApplication(appId);
			long secPolId = -1L;
			if (person.getSecurityPolicyMap() != null) {
				Long securityPolicyId = person.getSecurityPolicyMap().get(linkName);
				if (securityPolicyId != null) {
					secPolId = securityPolicyId;
				}
			}
			if (appDomain != null) {
				User user = AccountUtil.getUserBean().getUserFromEmail(existingPeople.getEmail(), appDomain.getIdentifier(), AccountUtil.getCurrentOrg().getId(), true);
				if ((linkName.equals(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP) && existingPeople.isOccupantPortalAccess())) {
					if (MapUtils.isEmpty(person.getRolesMap()) || !person.getRolesMap().containsKey(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP)) {
						throw new IllegalArgumentException("Role is mandatory");
					}
					long roleId = person.getRolesMap().get(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
					if (user != null) {
						user.setAppDomain(appDomain);
						user.setApplicationId(appId);
						user.setRoleId(roleId);
						user.setLanguage(person.getLanguage());
						user.setSecurityPolicyId(secPolId);
						if (isSsoEnabled) {
							user.setUserVerified(true);
							user.setInviteAcceptStatus(true);
						}
						String email = user.getEmail();
						AccountUtil.getUserBean().updateUser(user);
						V3PeopleAPI.enableUser(user);
						//resetting email, bcz email is set null in updateUser method. email is required for sending invite email.
						user.setEmail(email);
						ApplicationApi.addUserInApp(user, false, !isSsoEnabled);
						if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
							addPermissionSetsForPeople(person.getPermissionSets(), person.getId(), linkName);
						}
					} else {
						addPortalAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP, appDomain.getIdentifier(), verifyUser, roleId, secPolId, person.getPermissionSets());
					}
				} else if ((linkName.equals(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP) && existingPeople.employeePortalAccess())) {
					if (MapUtils.isEmpty(person.getRolesMap()) || !person.getRolesMap().containsKey(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP)) {
						throw new IllegalArgumentException("Role is mandatory");
					}
					long roleId = person.getRolesMap().get(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP);
					if (user != null) {
						user.setAppDomain(appDomain);
						user.setApplicationId(appId);
						user.setRoleId(roleId);
						user.setLanguage(person.getLanguage());
						user.setSecurityPolicyId(secPolId);
						if (isSsoEnabled) {
							user.setUserVerified(true);
							user.setInviteAcceptStatus(true);
						}
						String email = user.getEmail();
						AccountUtil.getUserBean().updateUser(user);

						//resetting email, bcz email is set null in updateUser method. email is required for sending invite email.
						user.setEmail(email);
						V3PeopleAPI.enableUser(user);
						ApplicationApi.addUserInApp(user, false, !isSsoEnabled);
						if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
							addPermissionSetsForPeople(person.getPermissionSets(), person.getId(), linkName);
						}
					} else {
						addPortalAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP, appDomain.getIdentifier(), verifyUser, roleId, secPolId, person.getPermissionSets());
					}
				} else {
					if (user != null) {
						ApplicationApi.deleteUserFromApp(user, appId);
						V3PeopleAPI.disableUser(existingPeople.getId(), user);
					}
				}
			} else {
				throw new IllegalArgumentException("Invalid App Domain");
			}
		}
	}

	public static void updateClientContactAppPortalAccess(ClientContactContext person, String linkName) throws Exception {

		ClientContactContext existingPeople = (ClientContactContext) RecordAPI.getRecord(FacilioConstants.ContextNames.CLIENT_CONTACT, person.getId());

		if (StringUtils.isEmpty(existingPeople.getEmail()) && (existingPeople.isClientPortalAccess())) {
			throw new IllegalArgumentException("Email Id associated with this contact is empty");
		}
		if (StringUtils.isNotEmpty(existingPeople.getEmail())) {
			AppDomain appDomain = null;
			long appId = ApplicationApi.getApplicationIdForLinkName(linkName);
			appDomain = ApplicationApi.getAppDomainForApplication(appId);
			if(appDomain != null) {
				User user = AccountUtil.getUserBean().getUserFromEmail(existingPeople.getEmail(), appDomain.getIdentifier(),AccountUtil.getCurrentOrg().getId(),true);
				if((linkName.equals(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP) && existingPeople.isClientPortalAccess())) {
					if(MapUtils.isEmpty(person.getRolesMap()) || !person.getRolesMap().containsKey(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP)){
						throw new IllegalArgumentException("Role is mandatory");
					}
					long roleId = person.getRolesMap().get(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
					if (user != null) {
						user.setAppDomain(appDomain);
						user.setApplicationId(appId);
						user.setRoleId(roleId);
						String email = user.getEmail();
						user.setLanguage(person.getLanguage());
						AccountUtil.getUserBean().updateUser(user);
						V3PeopleAPI.enableUser(user);
						user.setEmail(email);
						ApplicationApi.addUserInApp(user, false);
					} else {
						addPortalAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP, appDomain.getIdentifier(),false, roleId, -1,person.getPermissionSets());
					}
				} else {
					if (user != null) {
						ApplicationApi.deleteUserFromApp(user, appId);
						V3PeopleAPI.disableUser(existingPeople.getId(),user);
					}
				}
			} else {
				throw new IllegalArgumentException("Invalid App Domain");
			}

		}
	}

	public static void updateVendorContactAppPortalAccess(VendorContactContext person, String linkName) throws Exception {

		VendorContactContext existingPeople = (VendorContactContext) RecordAPI.getRecord(FacilioConstants.ContextNames.VENDOR_CONTACT, person.getId());

		if (StringUtils.isEmpty(existingPeople.getEmail()) && (existingPeople.isVendorPortalAccess())) {
			throw new IllegalArgumentException("Email Id associated with this contact is empty");
		}
		if (StringUtils.isNotEmpty(existingPeople.getEmail())) {
			AppDomain appDomain = null;
			long appId = ApplicationApi.getApplicationIdForLinkName(linkName);
			appDomain = ApplicationApi.getAppDomainForApplication(appId);
			if (appDomain != null) {
				long secPolId = -1L;
				if (person.getSecurityPolicyMap() != null) {
					Long securityPolicyId = person.getSecurityPolicyMap().get(linkName);
					if (securityPolicyId != null) {
						secPolId = securityPolicyId;
					}
				}

				User user = AccountUtil.getUserBean().getUserFromEmail(existingPeople.getEmail(), appDomain.getIdentifier(), AccountUtil.getCurrentOrg().getId(), true);
				if ((linkName.equals(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP) && existingPeople.isVendorPortalAccess())) {
					if (MapUtils.isEmpty(person.getRolesMap()) || !person.getRolesMap().containsKey(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP)) {
						throw new IllegalArgumentException("Role is mandatory");
					}
					long roleId = person.getRolesMap().get(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);

					if (user != null) {
						user.setAppDomain(appDomain);
						user.setApplicationId(appId);
						user.setRoleId(roleId);
						user.setLanguage(person.getLanguage());
						user.setSecurityPolicyId(secPolId);
						String email = user.getEmail();
						AccountUtil.getUserBean().updateUser(user);
						V3PeopleAPI.enableUser(user);
						//resetting email, bcz email is set null in updateUser method. email is required for sending invite email.
						user.setEmail(email);
						ApplicationApi.addUserInApp(user, false);
						if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
							addPermissionSetsForPeople(person.getPermissionSets(), person.getId(), linkName);
						}
					} else {
						User newUser = addPortalAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP, appDomain.getIdentifier(), false, roleId, secPolId, person.getPermissionSets());
						newUser.setAppDomain(appDomain);
					}
				} else {
					if (user != null) {
						ApplicationApi.deleteUserFromApp(user, appId);
						V3PeopleAPI.disableUser(existingPeople.getId(), user);
					}
				}
			} else {
				throw new IllegalArgumentException("Invalid App Domain");
			}
		}
	}

	public static void deletePeopleUsers(long peopleId) throws Exception {
		List<FacilioField> fields = AccountConstants.getAppOrgUserFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users");
		selectBuilder.andCondition(CriteriaAPI.getCondition("PEOPLE_ID", "peopleId", String.valueOf(peopleId), NumberOperators.EQUALS));

		List<Map<String, Object>> list = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(list)) {
			AccountUtil.getUserBean().deleteUser((long) list.get(0).get("ouid"), false);
		}
	}

	public static void deletePeopleUsers(List<Long> peopleIds) throws Exception {
		List<FacilioField> fields = AccountConstants.getAppOrgUserFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users");
		selectBuilder.andCondition(CriteriaAPI.getCondition("PEOPLE_ID", "peopleId", String.valueOf(peopleIds), NumberOperators.EQUALS));

		List<Map<String, Object>> list = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(list)) {
			for (Map<String, Object> map : list) {
				AccountUtil.getUserBean().deleteUser((long) map.get("ouid"), false);
			}
		}
	}


	public static int deletePeopleForUser(User user) throws Exception {
		long peopleId = user.getPeopleId();

		if (peopleId < 0) {
			peopleId = PeopleAPI.getPeopleIdForUser(user.getOuid());
		}

		if (peopleId > 0) {
			List<Long> ouids = getUserIdForPeople(peopleId);

			if (ouids.size() == 1) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");

				com.facilio.modules.DeleteRecordBuilder<PeopleContext> deleteBuilder = new com.facilio.modules.DeleteRecordBuilder<PeopleContext>()
						.module(modBean.getModule(FacilioConstants.ContextNames.PEOPLE))
						.andCondition(CriteriaAPI.getIdCondition(peopleId, ModuleFactory.getPeopleModule()));

				PeopleGroupUtils.markAsDeletePeopleGroupMember(Collections.singletonList(peopleId));

				return deleteBuilder.markAsDelete();
			}
		}
		return -1;
	}

	public static List<Long> getUserIdForPeople(long peopleId) throws Exception {
		List<Long> ouIds = new ArrayList<>();
		List<FacilioField> fields = AccountConstants.getAppOrgUserFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users");
		selectBuilder.andCondition(CriteriaAPI.getCondition("PEOPLE_ID", "peopleId", String.valueOf(peopleId), NumberOperators.EQUALS));
		List<Map<String, Object>> mapList = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(mapList)) {
			return mapList.stream().map(user -> (Long) user.get("ouid")).collect(Collectors.toList());
		}
		return null;
	}

	public static long getPeopleIdForUser(long ouId) throws Exception {
		List<FacilioField> fields = AccountConstants.getAppOrgUserFields();
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users");
		selectBuilder.andCondition(CriteriaAPI.getCondition("ORG_USERID", "ouId", String.valueOf(ouId), NumberOperators.EQUALS));

		List<Map<String, Object>> list = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(list)) {
			if (list.get(0).get("peopleId") != null) {
				return (long) list.get(0).get("peopleId");
			}
		}
		return -1;
	}

	public static User addAppUser(PeopleContext existingPeople, String linkName, Long roleId, boolean isEmailVerificationNeeded) throws Exception {

		if (StringUtils.isEmpty(linkName)) {
			throw new IllegalArgumentException("Invalid Link name");
		}
		long appId = ApplicationApi.getApplicationIdForLinkName(linkName);
		AppDomain appDomainObj = ApplicationApi.getAppDomainForApplication(appId);
		User user = new User();
		user.setEmail(existingPeople.getEmail());
		user.setPhone(existingPeople.getPhone());
		user.setName(existingPeople.getName());
		user.setUserVerified(!isEmailVerificationNeeded);         //Set user verified as true when email verification not needed
		user.setInviteAcceptStatus(!isEmailVerificationNeeded);   //Set user invite accept status as true when email verification not needed
		user.setInvitedTime(System.currentTimeMillis());
		user.setPeopleId(existingPeople.getId());
		user.setUserType(AccountConstants.UserType.USER.getValue());
		user.setRoleId(roleId);
		user.setLanguage(existingPeople.getLanguage());

		user.setApplicationId(appId);
		user.setAppDomain(appDomainObj);
		AccountUtil.getUserBean().createUser(AccountUtil.getCurrentOrg().getOrgId(), user, appDomainObj.getIdentifier(), isEmailVerificationNeeded, false);
		if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
			addPermissionSetsForPeople(existingPeople.getPermissionSets(), existingPeople.getId(), linkName);
		}
		return user;

	}

	public static User addPortalAppUser(PeopleContext existingPeople, String linkName, String identifier, long roleId) throws Exception {
		return addPortalAppUser(existingPeople, linkName, identifier, false, roleId, -1);
	}
	public static User addPortalAppUser(PeopleContext existingPeople, String linkName, String identifier, boolean verifyUser, long roleId, long securityPolicyId) throws Exception {
		return addPortalAppUser(existingPeople, linkName, identifier, verifyUser, roleId, securityPolicyId, null);
	}
	public static User addPortalAppUser(PeopleContext existingPeople, String linkName, String identifier, boolean verifyUser, long roleId, long securityPolicyId,List<Long> permissionSetIds) throws Exception {
		if(StringUtils.isEmpty(linkName)) {
			throw new IllegalArgumentException("Invalid link name");
		}
		long appId = ApplicationApi.getApplicationIdForLinkName(linkName);

		User user = new User();
		user.setEmail(existingPeople.getEmail());
		user.setPhone(existingPeople.getPhone());
		user.setName(existingPeople.getName());
		user.setUserVerified(verifyUser);
		user.setInviteAcceptStatus(verifyUser);
		user.setInvitedTime(System.currentTimeMillis());
		user.setPeopleId(existingPeople.getId());
		user.setUserType(AccountConstants.UserType.REQUESTER.getValue());
		user.setRoleId(roleId);
		user.setLanguage(existingPeople.getLanguage());
		user.setSecurityPolicyId(securityPolicyId);
		user.setApplicationId(appId);
		user.setAppDomain(ApplicationApi.getAppDomainForApplication(appId));
		if (!verifyUser) {
			AccountUtil.getUserBean().inviteRequester(AccountUtil.getCurrentOrg().getOrgId(), user, true, false, identifier, false, false);
		} else {
			AccountUtil.getUserBean().createUser(AccountUtil.getCurrentOrg().getOrgId(), user, identifier, false, false);
		}
		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
			addPermissionSetsForPeople(permissionSetIds,existingPeople.getId(),linkName);
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
			updateStateForPrimaryContact(tc);
		}

	}

	private static void updateStateForPrimaryContact(PeopleContext ppl) throws Exception {
		FacilioChain c = FacilioChain.getTransactionChain();
		if(ppl instanceof TenantContactContext) {
			c.addCommand(SetTableNamesCommand.getForTenantContact());
		}
		else if(ppl instanceof VendorContactContext){
			c.addCommand(SetTableNamesCommand.getForVendorContact());
		}
		else if(ppl instanceof ClientContactContext) {
			c.addCommand(SetTableNamesCommand.getForClientContact());
		}
		c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE,EventType.CREATE);
		c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(ppl));
		c.addCommand(new ExecuteStateFlowCommand());
		c.addCommand(new ExecuteStateTransitionsCommand(WorkflowRuleContext.RuleType.STATE_RULE));
		c.execute();

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
			updateStateForPrimaryContact(ppl);
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
				.fields(updatedfields)
				.andCondition(CriteriaAPI.getIdCondition(id, module));

		Map<String, Object> value = new HashMap<>();
		value.put("primaryContactEmail", email);
		value.put("primaryContactPhone", phone);
		value.put("primaryContactName", name);
		updateBuilder.update(value);

	}


	public static List<Map<String, Object>> getPeopleForRoles(List<Long> roleIds) throws Exception {

		if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(roleIds)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule peopleModule = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);

			List<FacilioField> fields = modBean.getAllFields(peopleModule.getName());
			SelectRecordsBuilder<PeopleContext> selectBuilder = new SelectRecordsBuilder<PeopleContext>()
					.beanClass(PeopleContext.class)
					.module(peopleModule)
					.select(fields)
					.innerJoin("ORG_Users")
					.on("ORG_Users.PEOPLE_ID = People.ID")
					.innerJoin("ORG_User_Apps")
					.on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID")
					.andCondition(CriteriaAPI.getCondition("ORG_User_Apps.ROLE_ID", "roleId", String.valueOf(StringUtils.join(roleIds, ",")), NumberOperators.EQUALS));
			List<PeopleContext> pplList = selectBuilder.get();
			if(CollectionUtils.isNotEmpty(pplList)) {
				return FieldUtil.getAsMapList(pplList, PeopleContext.class);
			}
		}
		return null;


	}

	private static boolean isSsoEnabledForApplication(String linkname) throws Exception
	{

		long orgId = AccountUtil.getCurrentOrg().getId();
		if(linkname.equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP))
		{
			AccountSSO accSso = IAMOrgUtil.getAccountSSO(orgId);
			if(accSso != null)
			{
				if(accSso.getIsActive())
				{
					return true;
				}
			}
		}
		else
		{
			List<Map<String, Object>> domainSsoList;
			if(linkname.equals(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP))
			{
				domainSsoList = IAMOrgUtil.getDomainSSODetails(orgId, AppDomain.AppDomainType.SERVICE_PORTAL, AppDomain.GroupType.TENANT_OCCUPANT_PORTAL);
			}
			else if(linkname.equals(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP))
			{
				domainSsoList = IAMOrgUtil.getDomainSSODetails(orgId, AppDomain.AppDomainType.TENANT_PORTAL, AppDomain.GroupType.TENANT_OCCUPANT_PORTAL);
			}
			else if(linkname.equals(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP))
			{
				domainSsoList = IAMOrgUtil.getDomainSSODetails(orgId, AppDomain.AppDomainType.VENDOR_PORTAL, AppDomain.GroupType.VENDOR_PORTAL);
			}
			else if(linkname.equals(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP))
			{
				domainSsoList = IAMOrgUtil.getDomainSSODetails(orgId, AppDomain.AppDomainType.EMPLOYEE_PORTAL, AppDomain.GroupType.EMPLOYEE_PORTAL);
			}
			else
			{
				return false;
			}
			if(!CollectionUtils.isEmpty(domainSsoList))
			{
				for(Map<String,Object> domainSso : domainSsoList)
				{
					if(!domainSso.isEmpty())
					{
						DomainSSO sso = FieldUtil.getAsBeanFromMap(domainSso, DomainSSO.class);
						if(sso.getIsActive() != null)
						{
							if(sso.getIsActive())
							{
								return true;
							}
						}
					}

				}
			}
		}
		return false;

	}

	public static void addApplicationUsersByPeopleId(Long peopleId,Long appId, Long roleId,boolean sendInvitation) throws Exception {
		FacilioChain chain = TransactionChainFactory.addApplicationUsersChain();
		FacilioContext context = chain.getContext();
		PeopleContext people = getPeopleForId(peopleId);
		User user = new User();
		user.setRoleId(roleId);
		user.setEmail(people.getEmail());
		context.put(FacilioConstants.ContextNames.APPLICATION_ID, appId);
		context.put(FacilioConstants.ContextNames.USER, user);
		context.put(FacilioConstants.ContextNames.IS_EMAIL_VERIFICATION_NEEDED,sendInvitation);
		chain.execute();
	}

	public static void updateUserByPeopleId(Long peopleId,Long appId, Long roleId) throws Exception {
		FacilioChain updateUser = TransactionChainFactory.updateUserChain();
		long ouid = getOrgUserIdForPeople(peopleId,appId);
		if(ouid>0) {
			User user = AccountUtil.getUserBean().getUser(appId, ouid);
			user.setRoleId(roleId);
			updateUser.getContext().put(FacilioConstants.ContextNames.USER, user);
			updateUser.getContext().put(FacilioConstants.ContextNames.USER_OPERATION, "updating user");
			updateUser.execute();
		}
		else{
			throw new IllegalArgumentException("User not present");
		}
	}

	public static void deleteApplicationUsersByPeopleId(Long peopleId,Long appId) throws Exception {
		FacilioChain chain = TransactionChainFactory.deleteApplicationUsersChain();
		long ouid = getOrgUserIdForPeople(peopleId,appId);
		if(ouid>0) {
			User user = AccountUtil.getUserBean().getUser(appId, ouid);
			chain.getContext().put(FacilioConstants.ContextNames.APPLICATION_ID, appId);
			chain.getContext().put(FacilioConstants.ContextNames.USER, user);
			chain.execute();
		}
		else{
			throw new IllegalArgumentException("User not present");
		}
	}

	public static long getOrgUserIdForPeople(long peopleId,long appId) throws Exception{
		long ouid = 0L;
		List<FacilioField> fields = AccountConstants.getOrgUserAppsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		List<FacilioField> selectFields = Arrays.asList(fieldMap.get("ouid"));
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(selectFields)
				.table("ORG_Users")
				.innerJoin("ORG_User_Apps")
				.on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID")
				.andCondition(CriteriaAPI.getCondition("ORG_User_Apps.APPLICATION_ID", "applicationId", String.valueOf(appId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("ORG_Users.PEOPLE_ID", "peopleId", String.valueOf(peopleId), NumberOperators.EQUALS));
		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			for (Map<String, Object> prop : props) {
				ouid = (long) prop.get("ouid");
			}
		}
		return ouid;
	}

	public static List<Map<String, Object>> getOrgUserAndApplicationMap(long peopleId) throws Exception{
		List<FacilioField> fields = AccountConstants.getOrgUserAppsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		ArrayList<FacilioField> selectFields = new ArrayList<>(Arrays.asList(fieldMap.get(FacilioConstants.ContextNames.OUID),fieldMap.get(FacilioConstants.ContextNames.APPLICATION_ID)));
		selectFields.add(FieldFactory.getField(FacilioConstants.ContextNames.USER,"ORG_Users.ORG_USERID",FieldType.NUMBER));
		return getOrgUserAppsForPeople(peopleId,selectFields);
	}

	public static List<Map<String, Object>> getOrgUserAppsForPeople(long peopleId, List<FacilioField> selectFields) throws Exception{
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(selectFields)
				.table("ORG_Users")
				.innerJoin("ORG_User_Apps")
				.on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID")
				.andCondition(CriteriaAPI.getCondition("ORG_Users.PEOPLE_ID", "peopleId", String.valueOf(peopleId), NumberOperators.EQUALS));
		List<Map<String, Object>> props = selectBuilder.get();
		return props;
	}
	public static Map<Long, Map<String, Object>> getPeopleNameForUserIds(List<Long> ouIds) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("id","ID",FieldType.NUMBER));
		fields.add(FieldFactory.getField("name","NAME", FieldType.STRING));
		return getPeopleForUserIds(ouIds,fields);
	}
	public static Map<Long, Map<String, Object>> getPeopleForUserIds(List<Long> ouIds, Collection<FacilioField> selectFields) throws Exception {
		if(ouIds == null || ouIds.isEmpty()){
			return null;
		}
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);

		selectFields.add(FieldFactory.getField("ouid","ORG_Users.ORG_USERID",FieldType.NUMBER));
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.select(selectFields)
				.table(module.getTableName())
				.innerJoin("ORG_Users")
				.on("ORG_Users.PEOPLE_ID = People.ID")
				.andCondition(CriteriaAPI.getCondition("ORG_Users.ORG_USERID","ouid",String.valueOf(StringUtils.join(ouIds, ",")),NumberOperators.EQUALS));

		List<Map<String, Object>> peopleList = selectRecordBuilder.get();
		Map<Long,Map<String,Object>> peopleForOUIdMap = new HashMap<>();
		for(Map<String, Object> people : peopleList){
			peopleForOUIdMap.put((Long) people.get("ouid"),people);
		}
		return peopleForOUIdMap;
	}

	private static void addPermissionSetsForPeople(List<Long> permissionSets, long peopleId,String linkName) throws Exception{
		PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
		if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
			permissionSetBean.updateUserPermissionSets(peopleId,permissionSets);
		}
	}

	public static List<User> getScopedUsers(List<Long> appIds, long siteId) throws Exception {
		List<User> users = new ArrayList<>();

		List<FacilioField> fields = new ArrayList<>(AccountConstants.getAppOrgUserFields());

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users");

		if(CollectionUtils.isNotEmpty(appIds)) {
			selectBuilder
					.innerJoin("ORG_User_Apps")
					.on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID")
					.andCondition(CriteriaAPI.getCondition(AccountConstants.getApplicationIdField(), StringUtils.join(appIds,","), NumberOperators.EQUALS))
					.groupBy("ORG_Users.ORG_USERID");
		}

		if (siteId > 0) {
			selectBuilder.andCustomWhere("((not exists(select 1 from Accessible_Space where Accessible_Space.ORG_USER_ID=ORG_Users.ORG_USERID)) OR (exists(select 1 from Accessible_Space where Accessible_Space.ORG_USER_ID=ORG_Users.ORG_USERID AND Accessible_Space.SITE_ID = " + String.valueOf(siteId) + ")))");
		}

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			IAMUserUtil.setIAMUserPropsv3(props, AccountUtil.getCurrentOrg().getId(), false);
			if(CollectionUtils.isNotEmpty(props)) {
				for (Map<String, Object> prop : props) {
					User user = FieldUtil.getAsBeanFromMap(prop, User.class);
					user.setId((long)prop.get("ouid"));
					if( user.isActive() ) {
						users.add(user);
					}
				}
			}
		}
		return users;
	}

	public static List<Group> getScopedTeams(List<Long> appIds, long siteId) throws Exception {
		JSONObject resultObject = new JSONObject();
		List<Group> groups = new ArrayList<>();

		FacilioModule groupModule = Constants.getModBean().getModule(FacilioConstants.PeopleGroup.PEOPLE_GROUP);;
		FacilioField groupSiteIdField = FieldFactory.getSiteIdField(groupModule);

		Criteria siteCriteria = new Criteria();
		siteCriteria.addAndCondition(CriteriaAPI.getCondition(groupSiteIdField, CommonOperators.IS_EMPTY));
		siteCriteria.addOrCondition(CriteriaAPI.getCondition(groupSiteIdField,String.valueOf(siteId),NumberOperators.EQUALS));

		List<FacilioField> fields = new ArrayList<>(AccountConstants.getAppOrgUserFields());
		fields.addAll(AccountConstants.getGroupMemberFields());

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("ORG_Users")
				.innerJoin("FacilioGroupMembers")
				.on("FacilioGroupMembers.PEOPLE_ID = ORG_Users.PEOPLE_ID")
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getSysDeletedTimeField(AccountConstants.getGroupMemberModule()),CommonOperators.IS_EMPTY));

		if(CollectionUtils.isNotEmpty(appIds)){
			selectBuilder
					.innerJoin("ORG_User_Apps")
					.on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID")
					.andCondition(CriteriaAPI.getCondition(AccountConstants.getApplicationIdField(), StringUtils.join(appIds,","), NumberOperators.EQUALS))
					.groupBy("ORG_Users.ORG_USERID, FacilioGroupMembers.ID");
		}

		if (siteId > 0) {
			selectBuilder.andCustomWhere("((not exists(select 1 from Accessible_Space where Accessible_Space.ORG_USER_ID=ORG_Users.ORG_USERID)) OR (exists(select 1 from Accessible_Space where Accessible_Space.ORG_USER_ID=ORG_Users.ORG_USERID AND Accessible_Space.SITE_ID = " + String.valueOf(siteId) + ")))")
					.innerJoin(groupModule.getTableName())
					.on("FacilioGroupMembers.GROUPID = FacilioGroups.ID")
					.andCriteria(siteCriteria);
		}

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {

			IAMUserUtil.setIAMUserPropsv3(props, AccountUtil.getCurrentOrg().getId(), false);
			if(CollectionUtils.isNotEmpty(props)) {
				List<GroupMember> members = new ArrayList<>();
				for(Map<String, Object> prop : props) {
					if (prop.get("peopleId") != null){
						prop.put("people",PeopleAPI.getPeopleForId((long) prop.get("peopleId")));
					}
					prop.put("id", prop.get("ouid"));
					GroupMember member = FieldUtil.getAsBeanFromMap(prop, GroupMember.class);
					if(member.isActive()) {
						members.add(member);
					}
				}
				if(CollectionUtils.isNotEmpty(members)) {
					Map<Long, List<GroupMember>> groupMemberMap = CollectionUtils.isEmpty(members) ? Collections.emptyMap() : members.stream().collect(Collectors.groupingBy(gm -> gm.getGroupId()));

					SelectRecordsBuilder<Group> groupBuilder = new SelectRecordsBuilder<Group>()
							.module(groupModule)
							.select(Constants.getModBean().getAllFields(groupModule.getName()))
							.beanClass(Group.class)
							.andCondition(CriteriaAPI.getIdCondition(StringUtils.join(groupMemberMap.keySet(), ","), groupModule));

					if (siteId > 0) {
						groupBuilder.andCriteria(siteCriteria);
					}

					groups = groupBuilder.get();

					for (Group group : groups) {
						group.setMembers(groupMemberMap.get(group.getId()));
					}
				}
			}
		}
		return groups;
	}

}
