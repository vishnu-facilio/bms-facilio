package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.ExecuteStateFlowCommand;
import com.facilio.bmsconsole.commands.ExecuteStateTransitionsCommand;
import com.facilio.bmsconsole.commands.SetTableNamesCommand;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
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
import org.apache.commons.collections.CollectionUtils;
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

    public static List<V3TenantContactContext> getTenantContacts(Long tenantId, boolean fetchOnlyPrimaryContact, boolean fetchOnlyWithAccess) throws Exception {
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

        if(fetchOnlyWithAccess) {
            builder.andCondition(CriteriaAPI.getCondition("TENANT_PORTAL_ACCESS", "isTenantPortalAccess", "true", BooleanOperators.IS));
        }
        List<V3TenantContactContext> records = builder.get();
        return records;

    }

    public static List<V3ClientContactContext> getClientContacts(Long clientId, boolean fetchOnlyPrimaryContact) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CLIENT_CONTACT);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.CLIENT_CONTACT);

        SelectRecordsBuilder<V3ClientContactContext> builder = new SelectRecordsBuilder<V3ClientContactContext>()
                .module(module)
                .beanClass(V3ClientContactContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("CLIENT_ID", "client", String.valueOf(clientId), NumberOperators.EQUALS));
        ;
        if(fetchOnlyPrimaryContact) {
            builder.andCondition(CriteriaAPI.getCondition("IS_PRIMARY_CONTACT", "isPrimaryContact", "true", BooleanOperators.IS));
        }
        List<V3ClientContactContext> records = builder.get();
        return records;

    }

    public static List<V3PeopleContext> getAllPeople() throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);

        SelectRecordsBuilder<V3PeopleContext> builder = new SelectRecordsBuilder<V3PeopleContext>()
                .module(module)
                .beanClass(V3PeopleContext.class)
                .select(fields)
        ;
        List<V3PeopleContext> records = builder.get();
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
            updateStateForPrimaryContact(tc);
        }

    }

    private static void updateStateForPrimaryContact(V3PeopleContext ppl) throws Exception {
        FacilioChain c = FacilioChain.getTransactionChain();
        if(ppl instanceof V3TenantContactContext) {
            c.addCommand(SetTableNamesCommand.getForTenantContact());
        }
        else if(ppl instanceof V3VendorContactContext){
            c.addCommand(SetTableNamesCommand.getForVendorContact());
        }
        else if(ppl instanceof V3ClientContactContext) {
            c.addCommand(SetTableNamesCommand.getForClientContact());
        }
        c.getContext().put(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
        c.getContext().put(FacilioConstants.ContextNames.RECORD_LIST, Collections.singletonList(ppl));
        c.addCommand(new ExecuteStateFlowCommand());
        c.addCommand(new ExecuteStateTransitionsCommand(WorkflowRuleContext.RuleType.STATE_RULE));
        c.execute();

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
            updateStateForPrimaryContact(ppl);
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
    
    public static V3PeopleContext getPeopleById(Long id) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
        SelectRecordsBuilder<V3PeopleContext> builder = new SelectRecordsBuilder<V3PeopleContext>()
                .module(module)
                .beanClass(V3PeopleContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getIdCondition(id, module));

        if(id != null && id != -1l) {
        	V3PeopleContext record = builder.fetchFirst();
            return record;
        }

        return null;
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

    public static V3TenantContext getTenantForUser(long ouId) throws Exception {
        long pplId = V3PeopleAPI.getPeopleIdForUser(ouId);
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

        V3TenantContactContext existingPeople = (V3TenantContactContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.TENANT_CONTACT, person.getId(), V3TenantContactContext.class);
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
                    if(person.getRoleId() <= 0){
                        throw new IllegalArgumentException("Role is mandatory");
                    }
                    if(user != null) {
                        user.setAppDomain(appDomain);
                        user.setApplicationId(appId);
                        user.setRoleId(person.getRoleId());
                        boolean shouldUpdateRole = existingPeople.getRoleId() == person.getRoleId() ? false : true;

                        ApplicationApi.addUserInApp(user, false, shouldUpdateRole);
                    }
                    else {
                        addPortalAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP, appDomain.getIdentifier(), person.getRoleId());
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

    public static User addPortalAppUser(V3PeopleContext existingPeople, String linkName, String identifier, Long roleId) throws Exception {
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
        user.setRoleId(roleId);

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
                    if(person.getRoleId() <= 0){
                        throw new IllegalArgumentException("Role is mandatory");
                    }
                    if(user != null) {
                        user.setAppDomain(appDomain);
                        user.setApplicationId(appId);
                        user.setRoleId(person.getRoleId());
                        boolean shouldUpdateRole = existingPeople.getRoleId() == person.getRoleId() ? false : true;

                        ApplicationApi.addUserInApp(user, false, shouldUpdateRole);
                    }
                    else {
                        addPortalAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP, appDomain.getIdentifier(), person.getRoleId());
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
                    if(person.getRoleId() <= 0){
                        throw new IllegalArgumentException("Role is mandatory");
                    }
                    if(user != null) {
                        user.setAppDomain(appDomain);
                        user.setRoleId(person.getRoleId());
                        user.setApplicationId(appId);
                        boolean shouldUpdateRole = existingPeople.getRoleId() == person.getRoleId() ? false : true;

                        ApplicationApi.addUserInApp(user, false, shouldUpdateRole);
                    }
                    else {
                        User newUser = addPortalAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP, appDomain.getIdentifier(), person.getRoleId());
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

            AppDomain appDomain = null;
            long appId = -1;
            appId = ApplicationApi.getApplicationIdForLinkName(linkname);
            appDomain = ApplicationApi.getAppDomainForApplication(appId);

            if(appDomain != null) {
                User user = AccountUtil.getUserBean().getUser(existingPeople.getEmail(), appDomain.getIdentifier());
                if((linkname.equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP) && existingPeople.isAppAccess())) {
                    if(person.getRoleId() <= 0){
                        throw new IllegalArgumentException("Role is mandatory");
                    }
                    if(user != null) {
                        user.setApplicationId(appId);
                        user.setAppDomain(appDomain);
                        user.setRoleId(person.getRoleId());

                        boolean shouldUpdateRole = existingPeople.getRoleId() == person.getRoleId() ? false : true;
                        ApplicationApi.addUserInApp(user, false, shouldUpdateRole);
                    }
                    else {
                        addAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, person.getRoleId());
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

    public static User addAppUser(V3PeopleContext existingPeople, String linkName, Long roleId) throws Exception {
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
        user.setRoleId(roleId);

        user.setApplicationId(appId);
        user.setAppDomain(appDomainObj);

        AccountUtil.getUserBean().createUser(AccountUtil.getCurrentOrg().getOrgId(), user, appDomainObj.getIdentifier(), true, false);
        return user;

    }

    public static void updateClientContactAppPortalAccess(V3ClientContactContext person, String linkName, Long roleId) throws Exception {

        V3ClientContactContext existingPeople = (V3ClientContactContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.CLIENT_CONTACT, person.getId(), V3ClientContactContext.class);

        if(StringUtils.isEmpty(existingPeople.getEmail()) && (existingPeople.isClientPortalAccess())){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Email Id associated with this contact is empty");
        }
        if(StringUtils.isNotEmpty(existingPeople.getEmail())) {
            AppDomain appDomain = null;
            long appId = ApplicationApi.getApplicationIdForLinkName(linkName);
            appDomain = ApplicationApi.getAppDomainForApplication(appId);
            if(appDomain != null) {
                User user = AccountUtil.getUserBean().getUser(existingPeople.getEmail(), appDomain.getIdentifier());
                if((linkName.equals(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP) && existingPeople.isClientPortalAccess())) {
                    if(roleId == null || roleId <= 0){
                        throw new IllegalArgumentException("Role is mandatory");
                    }
                    if(user != null) {
                        user.setAppDomain(appDomain);
                        user.setApplicationId(appId);
                        user.setRoleId(roleId);
                        boolean shouldUpdateRole = existingPeople.getRoleId() == roleId ? false : true;
                        ApplicationApi.addUserInApp(user, false, shouldUpdateRole);
                    }
                    else {
                        addPortalAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP, appDomain.getIdentifier(), roleId);
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


    public static V3VendorContext getVendorForUser(long ouId) throws Exception {
        long pplId = PeopleAPI.getPeopleIdForUser(ouId);
        if(pplId <= 0) {
            throw new IllegalArgumentException("Invalid People Id mapped with ORG_User");
        }
        V3VendorContactContext vc = (V3VendorContactContext)V3RecordAPI.getRecord(FacilioConstants.ContextNames.VENDOR_CONTACT, pplId, V3VendorContactContext.class);
        if (vc != null && vc.getVendor() != null && vc.getVendor().getId() > 0) {
            return (V3VendorContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.VENDORS, vc.getVendor().getId(), V3VendorContext.class);
        }

        return null;

    }

    public static List<Long> getTenantContactsIdsForLoggedInTenantUser(long ouid) throws Exception {
        V3TenantContext tenant = V3PeopleAPI.getTenantForUser(ouid);
        if(tenant != null) {
            List<V3TenantContactContext> tenantContacts = V3PeopleAPI.getTenantContacts(tenant.getId(), false, false);
            if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(tenantContacts)) {
                List<Long> idList = new ArrayList<>();
                for(V3PeopleContext people : tenantContacts) {
                    idList.add(people.getId());
                }
                return idList;
            }
        }
        return Collections.emptyList();
    }


}
