package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.EmployeeContext;
import com.facilio.bmsconsole.context.PeopleContext;
import com.facilio.bmsconsole.context.TenantContactContext;
import com.facilio.bmsconsole.context.VendorContactContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class V3PeopleAPI {

    public static List<V3VendorContactContext> getVendorContacts(long vendorId, boolean fetchPrimaryContact) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);
        List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.VENDOR_CONTACT);

        SelectRecordsBuilder<V3VendorContactContext> builder = new SelectRecordsBuilder<V3VendorContactContext>()
                .module(module)
                .beanClass(V3VendorContactContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(vendorId), NumberOperators.EQUALS));

        if (fetchPrimaryContact) {
            builder.andCondition(CriteriaAPI.getCondition("IS_PRIMARY_CONTACT", "isPrimaryContact", "true", BooleanOperators.IS));
        }
        List<V3VendorContactContext> records = builder.get();
        return records;
    }

    public static List<V3TenantContactContext> getTenantContacts(Long tenantId, boolean fetchOnlyPrimaryContact) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.TENANT_CONTACT);

        SelectRecordsBuilder<V3TenantContactContext> builder = new SelectRecordsBuilder<V3TenantContactContext>()
                .module(module)
                .beanClass(V3TenantContactContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("TENANT_ID", "tenant", String.valueOf(tenantId), NumberOperators.EQUALS));
        ;
        if(fetchOnlyPrimaryContact) {
            builder.andCondition(CriteriaAPI.getCondition("IS_PRIMARY_CONTACT", "isPrimaryContact", "true", BooleanOperators.IS));
        }
        List<V3TenantContactContext> records = builder.get();
        return records;

    }

    public static void addParentPrimaryContactAsPeople(V3PeopleContext tc, FacilioModule module, List<FacilioField> fields, long parentId, V3PeopleContext primaryContactForParent) throws Exception {

        if(primaryContactForParent != null) {
            if(StringUtils.isNotEmpty(tc.getEmail())) {
                if(StringUtils.isEmpty(primaryContactForParent.getEmail())) {
                    tc.setId(primaryContactForParent.getId());
                    updatePeopleRecord(tc, module, fields);
                    return;
                }
                else if(primaryContactForParent.getEmail().equals(tc.getEmail())) {
                    tc.setId(primaryContactForParent.getId());
                    V3RecordAPI.updateRecord(tc, module, fields);
                    return;
                }
                else {
                    addPeopleRecord(tc, module, fields, parentId);
                    return;
                }
            }
            else {
                tc.setId(primaryContactForParent.getId());
                V3RecordAPI.updateRecord(tc, module, fields);
                return;
            }
        }
        else {
            if(StringUtils.isNotEmpty(tc.getEmail())) {
                addPeopleRecord(tc, module, fields, parentId);
                return;
            }
            V3RecordAPI.addRecord(true, Collections.singletonList(tc), module, fields);
        }

    }

    public static void updatePeopleRecord(V3PeopleContext ppl, FacilioModule module, List<FacilioField> fields) throws Exception {
        V3PeopleContext peopleExisting = getPeople(ppl.getEmail());
        if(peopleExisting == null) {
            V3RecordAPI.updateRecord(ppl, module, fields);
            return;
        }
        throw new RESTException(ErrorCode.VALIDATION_ERROR, "People with the same email id already exists");

    }

    public static void addPeopleRecord(V3PeopleContext ppl, FacilioModule module, List<FacilioField> fields, long parentId) throws Exception {
        V3PeopleContext peopleExisting = getPeople(ppl.getEmail());
        if(peopleExisting == null) {
            V3RecordAPI.addRecord(true, Collections.singletonList(ppl), module, fields);
            unMarkPrimaryContact(ppl, parentId);

            return;
        }
        throw new RESTException(ErrorCode.VALIDATION_ERROR, "People with the same email id already exists");

    }

    public static V3PeopleContext getPeople(String email) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
        SelectRecordsBuilder<V3PeopleContext> builder = new SelectRecordsBuilder<V3PeopleContext>()
                .module(module)
                .beanClass(V3PeopleContext.class)
                .select(fields)
                ;

        if(StringUtils.isNotEmpty(email)) {
            builder.andCondition(CriteriaAPI.getCondition("EMAIL", "email", String.valueOf(email), StringOperators.IS));
        }

        V3PeopleContext records = builder.fetchFirst();
        return records;
    }

    public static int unMarkPrimaryContact(V3PeopleContext person, long parentId) throws Exception{

        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = null;
        List<FacilioField> fields = new ArrayList<FacilioField>();
        List<FacilioField> updatedfields = new ArrayList<FacilioField>();
        com.facilio.db.criteria.Condition condition = null;

        if(person instanceof V3TenantContactContext) {
            module = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
            fields.addAll(modBean.getAllFields(FacilioConstants.ContextNames.TENANT_CONTACT));
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            FacilioField primaryContactField = fieldMap.get("isPrimaryContact");
            updatedfields.add(primaryContactField);
            condition = CriteriaAPI.getCondition("TENANT_ID", "tenant", String.valueOf(parentId), NumberOperators.EQUALS);


        }
        else if(person instanceof V3VendorContactContext) {
            module = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);
            fields.addAll(modBean.getAllFields(FacilioConstants.ContextNames.VENDOR_CONTACT));
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            FacilioField primaryContactField = fieldMap.get("isPrimaryContact");
            updatedfields.add(primaryContactField);
            condition = CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(parentId), NumberOperators.EQUALS);
        }
        else if(person instanceof V3ClientContactContext) {
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

    public static V3TenantContext getTenantForUser(long ouId) throws Exception {
        long pplId = PeopleAPI.getPeopleIdForUser(ouId);
        if(pplId <= 0) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid People Id mapped with ORG_User");
        }
        V3TenantContactContext tc = (V3TenantContactContext)V3RecordAPI.getRecord(FacilioConstants.ContextNames.TENANT_CONTACT, pplId, V3TenantContactContext.class);
        if (tc != null && tc.getTenant() != null && tc.getTenant().getId() > 0) {
            return (V3TenantContext)V3RecordAPI.getRecord(FacilioConstants.ContextNames.TENANT, tc.getTenant().getId(), V3TenantContext.class);
        }
        return null;
    }

    public static boolean checkForDuplicatePeople(V3PeopleContext people) throws Exception {
        V3PeopleContext peopleExisiting = getPeople(people.getEmail());
        if(peopleExisiting != null && people.getId() != peopleExisiting.getId()) {
            return true;
        }
        return false;
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


    public static void updateTenantContactAppPortalAccess(V3TenantContactContext person, String appLinkName) throws Exception {

        V3TenantContactContext existingPeople = (V3TenantContactContext) RecordAPI.getRecord(FacilioConstants.ContextNames.TENANT_CONTACT, person.getId());
        if(StringUtils.isEmpty(existingPeople.getEmail()) && (existingPeople.isTenantPortalAccess())){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Email Id associated with this contact is empty");
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
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid App Domain");
            }
        }
    }

    public static User addPortalAppUser(V3PeopleContext existingPeople, String linkName, String identifier) throws Exception {
        if(StringUtils.isEmpty(linkName)) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid link name");
        }
        long appId = ApplicationApi.getApplicationIdForLinkName(linkName);

        User user = new User();
        user.setEmail(existingPeople.getEmail());
        user.setPhone(existingPeople.getPhone());
        user.setName(existingPeople.getName());
        user.setUserVerified(false);
        user.setInviteAcceptStatus(false);
        user.setInvitedTime(System.currentTimeMillis());
        user.setPeopleId(existingPeople.getId());
        user.setUserType(AccountConstants.UserType.REQUESTER.getValue());

        user.setApplicationId(appId);
        user.setAppDomain(ApplicationApi.getAppDomainForApplication(appId));


        AccountUtil.getUserBean().inviteRequester(AccountUtil.getCurrentOrg().getOrgId(), user, true, false, identifier, false, false);
        return user;
    }


    public static void updatePeoplePortalAccess(V3PeopleContext person, String linkName) throws Exception {

        V3PeopleContext existingPeople = (V3PeopleContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE, person.getId(), V3PeopleContext.class);
        if(StringUtils.isEmpty(existingPeople.getEmail()) && (existingPeople.isOccupantPortalAccess())){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Email Id associated with this contact is empty");
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
                        addPortalAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP, appDomain.getIdentifier());
                    }
                }
                else {
                    if(user != null) {
                        ApplicationApi.deleteUserFromApp(user, appId);
                    }
                }
            }
            else {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid App Domain");
            }
        }
    }

    public static void updateVendorContactAppPortalAccess(V3VendorContactContext person, String linkName) throws Exception {

        V3VendorContactContext existingPeople = (V3VendorContactContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.VENDOR_CONTACT, person.getId(), V3VendorContactContext.class);

        if(StringUtils.isEmpty(existingPeople.getEmail()) && (existingPeople.isVendorPortalAccess())){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Email Id associated with this contact is empty");
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
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid App Domain");
            }
        }
    }

    public static void updateEmployeeAppPortalAccess(V3EmployeeContext person, String linkname) throws Exception {

        V3EmployeeContext existingPeople = (V3EmployeeContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.EMPLOYEE, person.getId(),  V3EmployeeContext.class);
        if(StringUtils.isEmpty(existingPeople.getEmail()) && (existingPeople.isAppAccess())){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Email Id associated with this contact is empty");
        }
        if(StringUtils.isNotEmpty(existingPeople.getEmail())) {

            if(existingPeople.isAppAccess() && existingPeople.getRoleId() <= 0) {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Role cannot be null");
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
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid App Domain");
            }
        }

    }

    public static User addAppUser(V3PeopleContext existingPeople, String linkName) throws Exception {
        if(StringUtils.isEmpty(linkName)) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Link name");
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


}
