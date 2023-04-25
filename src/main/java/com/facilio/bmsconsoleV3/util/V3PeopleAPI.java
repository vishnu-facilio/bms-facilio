package com.facilio.bmsconsoleV3.util;

import com.facilio.accounts.dto.AppDomain;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.impl.UserBeanImpl;
import com.facilio.accounts.sso.AccountSSO;
import com.facilio.accounts.sso.DomainSSO;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.beans.PermissionSetBean;
import com.facilio.bmsconsole.commands.ExecuteStateFlowCommand;
import com.facilio.bmsconsole.commands.ExecuteStateTransitionsCommand;
import com.facilio.bmsconsole.commands.SetTableNamesCommand;
import com.facilio.bmsconsole.forms.FacilioForm;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.bmsconsole.util.FormsAPI;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.util.IAMAccountConstants;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.iam.accounts.util.IAMUserUtil;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.permission.context.PermissionSetContext;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import com.facilio.v3.util.V3Util;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
public class V3PeopleAPI {

    public static final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);
    private static final Logger LOGGER = org.apache.log4j.Logger.getLogger(V3PeopleAPI.class);

    public static List<V3VendorContactContext> getVendorContacts(long vendorId, boolean fetchPrimaryContact) throws Exception {
        return getVendorContacts(vendorId, fetchPrimaryContact, false);
    }

    public static List<V3VendorContactContext> getVendorContacts(long vendorId, boolean fetchPrimaryContact,boolean fetchDeleted) throws Exception {
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
        List<V3VendorContactContext> records;
        if(fetchDeleted){
            records = builder.fetchDeleted().get();
        }
        else {
            records = builder.get();
        }
        return records;
    }

    public static List<V3TenantContactContext> getTenantContacts(Long tenantId, boolean fetchOnlyPrimaryContact, boolean fetchOnlyWithAccess) throws Exception {
        return getTenantContacts(tenantId, fetchOnlyPrimaryContact, fetchOnlyWithAccess, false);
    }
    public static List<V3TenantContactContext> getTenantContacts(Long tenantId, boolean fetchOnlyPrimaryContact, boolean fetchOnlyWithAccess,boolean fetchDeleted) throws Exception {
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
        List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.TENANT_CONTACT);

        SelectRecordsBuilder<V3TenantContactContext> builder = new SelectRecordsBuilder<V3TenantContactContext>()
                .module(module)
                .beanClass(V3TenantContactContext.class)
                .select(fields)
                .andCondition(CriteriaAPI.getCondition("TENANT_ID", "tenant", String.valueOf(tenantId), NumberOperators.EQUALS));

        if(fetchOnlyPrimaryContact) {
            builder.andCondition(CriteriaAPI.getCondition("IS_PRIMARY_CONTACT", "isPrimaryContact", "true", BooleanOperators.IS));
        }

        if(fetchOnlyWithAccess) {
            builder.andCondition(CriteriaAPI.getCondition("TENANT_PORTAL_ACCESS", "isTenantPortalAccess", "true", BooleanOperators.IS));
        }
        List<V3TenantContactContext> records;
        if(fetchDeleted){
            records = builder.fetchDeleted().get();
        }
        else{
            records = builder.get();
        }
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

                String trimmedEmail = tc.getEmail().trim();
                tc.setEmail(trimmedEmail);
                validatePeopleEmail(trimmedEmail);

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
                    FacilioForm defaultform = FormsAPI.getDefaultForm(module.getName(),AccountUtil.getCurrentApp().getLinkName(),false);
                    if(defaultform != null){
                        tc.setFormId(defaultform.getId());
                    }
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
            FacilioForm defaultform = FormsAPI.getFormFromDB("default_" + module.getName() + "_web_" + AccountUtil.getCurrentApp().getLinkName(),module);
            if(defaultform != null){
                tc.setFormId(defaultform.getId());
            }
            if(StringUtils.isNotEmpty(tc.getEmail())) {

                String trimmedEmail = tc.getEmail().trim();
                tc.setEmail(trimmedEmail);
                validatePeopleEmail(trimmedEmail);

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

    public static List<V3PeopleContext> getPeopleByPeopleTypes(List<V3PeopleContext.PeopleType> peopleTypes) throws Exception {
        if(CollectionUtils.isNotEmpty(peopleTypes)) {
            String peopletypeids = peopleTypes.stream().map(type -> type.getIndex().toString()).collect(Collectors.joining(","));
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
            List<FacilioField> fields  = modBean.getAllFields(FacilioConstants.ContextNames.PEOPLE);
            SelectRecordsBuilder<V3PeopleContext> builder = new SelectRecordsBuilder<V3PeopleContext>()
                    .module(module)
                    .beanClass(V3PeopleContext.class)
                    .select(fields)
                    .andCondition(CriteriaAPI.getCondition("PEOPLE_TYPE", "peopleType", peopletypeids, NumberOperators.EQUALS));
            List<V3PeopleContext> record = builder.get();

            if(CollectionUtils.isNotEmpty(record)) {
                return record;
            }
        }
        return null;
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

    public static int markPrimaryContact(V3PeopleContext person) throws Exception{

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
        }
        else if(person instanceof V3VendorContactContext) {
            module = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);
            fields.addAll(modBean.getAllFields(FacilioConstants.ContextNames.VENDOR_CONTACT));
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            FacilioField primaryContactField = fieldMap.get("isPrimaryContact");
            updatedfields.add(primaryContactField);
        }
        else if(person instanceof V3ClientContactContext) {
            module = modBean.getModule(FacilioConstants.ContextNames.CLIENT_CONTACT);
            fields.addAll(modBean.getAllFields(FacilioConstants.ContextNames.CLIENT_CONTACT));
            Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
            FacilioField primaryContactField = fieldMap.get("isPrimaryContact");
            updatedfields.add(primaryContactField);
        }
        GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
                .table(module.getTableName())
                .fields(updatedfields)
                .andCondition(CriteriaAPI.getCondition("ID", "peopleId", String.valueOf(person.getId()), NumberOperators.EQUALS));

        Map<String, Object> value = new HashMap<>();
        value.put("isPrimaryContact", true);
        int count = updateBuilder.update(value);
        return count;

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

    public static void deletePeopleUsers(long peopleId) throws Exception {
        List<FacilioField> fields = AccountConstants.getAppOrgUserFields();
        GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(fields)
                .table("ORG_Users")
                ;
        selectBuilder.andCondition(CriteriaAPI.getCondition("PEOPLE_ID", "peopleId", String.valueOf(peopleId), NumberOperators.EQUALS));

        List<Map<String, Object>> list = selectBuilder.get();
        if(CollectionUtils.isNotEmpty(list)) {
            for(int i=0;i < list.size(); i++){
                AccountUtil.getUserBean().deleteUser((long)list.get(i).get("ouid"), false);
            }
        }
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

    public static V3TenantContext getTenantForUser(long ouId, boolean skipScoping) throws Exception {
        long pplId = V3PeopleAPI.getPeopleIdForUser(ouId);
        if(pplId <= 0) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid People Id mapped with ORG_User");
        }
        V3TenantContactContext tc = (V3TenantContactContext)V3RecordAPI.getRecord(FacilioConstants.ContextNames.TENANT_CONTACT, pplId, V3TenantContactContext.class, skipScoping);
        if (tc != null && tc.getTenant() != null && tc.getTenant().getId() > 0) {
            return (V3TenantContext)V3RecordAPI.getRecord(FacilioConstants.ContextNames.TENANT, tc.getTenant().getId(), V3TenantContext.class, skipScoping);
        }
        return null;
    }

    public static V3TenantContext getTenantForUser(long ouId) throws Exception {
        return getTenantForUser(ouId, false);
    }



    public static V3ClientContext getClientForUser(long ouId, boolean skipScoping) throws Exception {
        long pplId = V3PeopleAPI.getPeopleIdForUser(ouId);
        if(pplId <= 0) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid People Id mapped with ORG_User");
        }
        V3ClientContactContext tc = (V3ClientContactContext)V3RecordAPI.getRecord(FacilioConstants.ContextNames.CLIENT_CONTACT, pplId, V3ClientContactContext.class, skipScoping);
        if (tc != null && tc.getClient() != null && tc.getClient().getId() > 0) {
            return (V3ClientContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.CLIENT, tc.getClient().getId(), V3ClientContext.class, skipScoping);
        }
        return null;
    }

    public static V3ClientContext getClientForUser(long ouId) throws Exception {
        return getClientForUser(ouId, false);
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
                .fields(updatedfields)
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
                User user = AccountUtil.getUserBean().getUserFromEmail(existingPeople.getEmail(), appDomain.getIdentifier(),AccountUtil.getCurrentOrg().getId(),true);
                if((appLinkName.equals(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP) && existingPeople.isTenantPortalAccess())) {
                    if(MapUtils.isEmpty(person.getRolesMap()) || !person.getRolesMap().containsKey(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP)){
                        throw new IllegalArgumentException("Role is mandatory");
                    }
                    long roleId = person.getRolesMap().get(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
                    if(user != null) {
                        user.setAppDomain(appDomain);
                        user.setApplicationId(appId);
                        user.setRoleId(roleId);
                        V3PeopleAPI.enableUser(user);
                        ApplicationApi.addUserInApp(user, false);
                        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
                            addPermissionSetsForPeople(person.getPermissionSets(), person.getId(), appLinkName);
                        }
                    }
                    else {
                        addPortalAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP, appDomain.getIdentifier(), roleId, person.getPermissionSets());
                    }
                }
                else {
                    if(user != null) {
                        ApplicationApi.deleteUserFromApp(user, appId);
                        V3PeopleAPI.disableUser(existingPeople.getId(),user);
                    }
                }
            }
            else {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid App Domain");
            }
        }
    }
    public static User addPortalAppUser(V3PeopleContext existingPeople, String linkName, String identifier, long roleId,List<Long> permissionSetIds) throws Exception {
        return addPortalAppUser(existingPeople, linkName, identifier, false, roleId, permissionSetIds);
    }

    public static User addPortalAppUser(V3PeopleContext existingPeople, String linkName, String identifier, boolean verifyUser, Long roleId, List<Long> permissionSetIds) throws Exception {
        if(StringUtils.isEmpty(linkName)) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid link name");
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

        user.setApplicationId(appId);
        user.setAppDomain(ApplicationApi.getAppDomainForApplication(appId));
        AccountUtil.getUserBean().inviteRequester(AccountUtil.getCurrentOrg().getOrgId(), user, true, false, identifier, false, false);
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
            addPermissionSetsForPeople(permissionSetIds, existingPeople.getId(), linkName);
        }
        return user;
    }


    public static void updatePeoplePortalAccess(V3PeopleContext person, String linkName) throws Exception {

        V3PeopleContext existingPeople = (V3PeopleContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.PEOPLE, person.getId(), V3PeopleContext.class);
        if(StringUtils.isEmpty(existingPeople.getEmail()) && (existingPeople.isOccupantPortalAccess())){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Email Id associated with this contact is empty");
        }
        boolean isSsoEnabled = isSsoEnabledForApplication(linkName),verifyUser = false;
        if(isSsoEnabled){
            verifyUser = true;
        }
        if(StringUtils.isNotEmpty(existingPeople.getEmail())) {
            AppDomain appDomain = null;
            long appId = ApplicationApi.getApplicationIdForLinkName(linkName);
            appDomain = ApplicationApi.getAppDomainForApplication(appId);
            if(appDomain != null) {
                User user = AccountUtil.getUserBean().getUserFromEmail(existingPeople.getEmail(), appDomain.getIdentifier(),AccountUtil.getCurrentOrg().getId(),true);
                if((linkName.equals(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP) && existingPeople.isOccupantPortalAccess())) {
                    if(MapUtils.isEmpty(person.getRolesMap()) || !person.getRolesMap().containsKey(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP)){
                        throw new IllegalArgumentException("Role is mandatory");
                    }
                    long roleId = person.getRolesMap().get(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
                    if(user != null) {
                        user.setAppDomain(appDomain);
                        user.setApplicationId(appId);
                        user.setRoleId(roleId);
                        if(isSsoEnabled)
                        {
                            user.setUserVerified(true);
                            user.setInviteAcceptStatus(true);
                        }
                        V3PeopleAPI.enableUser(user);
                        ApplicationApi.addUserInApp(user, false, !isSsoEnabled);
                        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
                            addPermissionSetsForPeople(person.getPermissionSets(), person.getId(), linkName);
                        }
                    }
                    else {
                        addPortalAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP, appDomain.getIdentifier(), verifyUser , roleId, person.getPermissionSets());
                    }
                }
                else {
                    if(user != null) {
                        ApplicationApi.deleteUserFromApp(user, appId);
                        V3PeopleAPI.disableUser(existingPeople.getId(),user);
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
                User user = AccountUtil.getUserBean().getUserFromEmail(existingPeople.getEmail(), appDomain.getIdentifier(),AccountUtil.getCurrentOrg().getId(),true);
                if((linkName.equals(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP) && existingPeople.isVendorPortalAccess())) {
                    if(MapUtils.isEmpty(person.getRolesMap()) || !person.getRolesMap().containsKey(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP)){
                        throw new IllegalArgumentException("Role is mandatory");
                    }
                    long roleId = person.getRolesMap().get(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
                    if(user != null) {
                        user.setAppDomain(appDomain);
                        user.setRoleId(roleId);
                        user.setApplicationId(appId);
                        V3PeopleAPI.enableUser(user);
                        ApplicationApi.addUserInApp(user, false);
                        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
                            addPermissionSetsForPeople(person.getPermissionSets(), person.getId(), linkName);
                        }
                    }
                    else {
                        User newUser = addPortalAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP, appDomain.getIdentifier(), roleId, person.getPermissionSets());
                        newUser.setAppDomain(appDomain);
                    }
                }
                else {
                    if(user != null) {
                        ApplicationApi.deleteUserFromApp(user, appId);
                        V3PeopleAPI.disableUser(existingPeople.getId(),user);
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
        boolean isSsoEnabled = isSsoEnabledForApplication(linkname);
        if(StringUtils.isEmpty(existingPeople.getEmail()) && (existingPeople.isAppAccess())){
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Email Id associated with this contact is empty");
        }
        if(StringUtils.isNotEmpty(existingPeople.getEmail())) {

            AppDomain appDomain = null;
            long appId = -1;
            appId = ApplicationApi.getApplicationIdForLinkName(linkname);
            appDomain = ApplicationApi.getAppDomainForApplication(appId);

            if(appDomain != null) {
                User user = AccountUtil.getUserBean().getUserFromEmail(existingPeople.getEmail(), appDomain.getIdentifier(),AccountUtil.getCurrentOrg().getId(),true);
                if((linkname.equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP) && existingPeople.isAppAccess())) {
                    if(MapUtils.isEmpty(person.getRolesMap()) || !person.getRolesMap().containsKey(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)){
                        throw new IllegalArgumentException("Role is mandatory");
                    }

                    long roleId = person.getRolesMap().get(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP);
                    if(user != null) {
                        user.setApplicationId(appId);
                        user.setAppDomain(appDomain);
                        user.setRoleId(roleId);
                        if(isSsoEnabled)
                        {
                            user.setUserVerified(true);
                            user.setInviteAcceptStatus(true);
                        }
                        V3PeopleAPI.enableUser(user);
                        ApplicationApi.addUserInApp(user, false, !isSsoEnabled);
                        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
                            addPermissionSetsForPeople(person.getPermissionSets(), person.getId(), linkname);
                        }
                    }
                    else {
                        addAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP, roleId, !isSsoEnabled);
                    }
                }
                else {
                    if(user != null) {
                        ApplicationApi.deleteUserFromApp(user, appId);
                        V3PeopleAPI.disableUser(existingPeople.getId(),user);
                    }
                }
            }
            else {
                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid App Domain");
            }
        }

    }

    public static boolean enableUser(V3PeopleContext people, User user) throws Exception {
        return enableUser(user);
    }
    public static boolean enableUser(User user) throws Exception {
        AccountUtil.getUserBean().enableUser(AccountUtil.getCurrentOrg().getOrgId(), user.getUid());
        return true;
    }
    //temp handling should be removed soon
    public static boolean disableUser(V3PeopleContext people,User user) throws Exception {
        return disableUser(people.getId(),user);
    }
    //temp handling should be removed soon
    public static boolean disableUser(long peopleId,User user) throws Exception {
        PeopleAPI.deletePermissionSetsForPeople(peopleId);
        V3TenantContactContext tc = V3RecordAPI.getRecord(FacilioConstants.ContextNames.TENANT_CONTACT,peopleId,V3TenantContactContext.class);
        if(user.getUserStatus() != null && user.getUserStatus() == true) {
            if (tc != null && !tc.isTenantPortalAccess()) {
                AccountUtil.getUserBean().disableUser(AccountUtil.getCurrentOrg().getOrgId(), user.getUid());
                return true;
            }
            V3EmployeeContext emp = V3RecordAPI.getRecord(FacilioConstants.ContextNames.EMPLOYEE, peopleId, V3EmployeeContext.class);
            if (emp != null && !emp.isEmployeePortalAccess()) {
                AccountUtil.getUserBean().disableUser(AccountUtil.getCurrentOrg().getOrgId(), user.getUid());
                return true;
            }
            if (emp == null && tc == null) {
                AccountUtil.getUserBean().disableUser(AccountUtil.getCurrentOrg().getOrgId(), user.getUid());
                return true;
            }
        }
        return false;
    }

    public static User addAppUser(V3PeopleContext existingPeople, String linkName, Long roleId, boolean isEmailVerificationNeeded) throws Exception {
        if(StringUtils.isEmpty(linkName)) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid Link name");
        }
        long appId = ApplicationApi.getApplicationIdForLinkName(linkName);
        AppDomain appDomainObj = ApplicationApi.getAppDomainForApplication(appId);
        User user = new User();
        user.setEmail(existingPeople.getEmail());
        user.setPhone(existingPeople.getPhone());
        user.setName(existingPeople.getName());
        user.setUserVerified(!isEmailVerificationNeeded);
        user.setInviteAcceptStatus(!isEmailVerificationNeeded);
        user.setInvitedTime(System.currentTimeMillis());
        user.setPeopleId(existingPeople.getId());
        user.setUserType(AccountConstants.UserType.USER.getValue());
        user.setRoleId(roleId);
        user.setLanguage(existingPeople.getLanguage());

        user.setApplicationId(appId);
        user.setAppDomain(appDomainObj);
        AccountUtil.getUserBean().createUser(AccountUtil.getCurrentOrg().getOrgId(), user, appDomainObj.getIdentifier(), true, false);
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
            addPermissionSetsForPeople(existingPeople.getPermissionSets(), existingPeople.getId(), linkName);
        }
        return user;

    }

    public static void updateClientContactAppPortalAccess(V3ClientContactContext person, String linkName) throws Exception {

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
                    if(MapUtils.isEmpty(person.getRolesMap()) || !person.getRolesMap().containsKey(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP)){
                        throw new IllegalArgumentException("Role is mandatory");
                    }

                    long roleId = person.getRolesMap().get(FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
                    if(user != null) {
                        user.setAppDomain(appDomain);
                        user.setApplicationId(appId);
                        user.setRoleId(roleId);
                        ApplicationApi.addUserInApp(user, false);
                        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
                            addPermissionSetsForPeople(person.getPermissionSets(), person.getId(), linkName);
                        }
                    }
                    else {
                        addPortalAppUser(existingPeople, FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP, appDomain.getIdentifier(), roleId, person.getPermissionSets());
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


    public static V3VendorContext getVendorForUser(long ouId, boolean skipScoping) throws Exception {
        long pplId = PeopleAPI.getPeopleIdForUser(ouId);
        if(pplId <= 0) {
            throw new IllegalArgumentException("Invalid People Id mapped with ORG_User");
        }
        V3VendorContactContext vc = (V3VendorContactContext)V3RecordAPI.getRecord(FacilioConstants.ContextNames.VENDOR_CONTACT, pplId, V3VendorContactContext.class, skipScoping);
        if (vc != null && vc.getVendor() != null && vc.getVendor().getId() > 0) {
            return (V3VendorContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.VENDORS, vc.getVendor().getId(), V3VendorContext.class, skipScoping);
        }

        return null;

    }

    public static V3VendorContext getVendorForUser(long ouId) throws Exception {
        return getVendorForUser(ouId, false);
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
    private static boolean isSsoEnabledForApplication(String linkname) throws Exception {

        long orgId = AccountUtil.getCurrentOrg().getId();
        if (linkname.equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)) {
            AccountSSO accSso = IAMOrgUtil.getAccountSSO(orgId);
            if (accSso != null) {
                if (accSso.getIsActive()) {
                    return true;
                }
            }
        } else {
            List < Map < String, Object >> domainSsoList;
            if (linkname.equals(FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP)) {
                domainSsoList = IAMOrgUtil.getDomainSSODetails(orgId, AppDomain.AppDomainType.SERVICE_PORTAL, AppDomain.GroupType.TENANT_OCCUPANT_PORTAL);
            } else if (linkname.equals(FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP)) {
                domainSsoList = IAMOrgUtil.getDomainSSODetails(orgId, AppDomain.AppDomainType.TENANT_PORTAL, AppDomain.GroupType.TENANT_OCCUPANT_PORTAL);
            } else if (linkname.equals(FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP)) {
                domainSsoList = IAMOrgUtil.getDomainSSODetails(orgId, AppDomain.AppDomainType.VENDOR_PORTAL, AppDomain.GroupType.VENDOR_PORTAL);
            } else if (linkname.equals(FacilioConstants.ApplicationLinkNames.EMPLOYEE_PORTAL_APP)) {
                domainSsoList = IAMOrgUtil.getDomainSSODetails(orgId, AppDomain.AppDomainType.EMPLOYEE_PORTAL, AppDomain.GroupType.EMPLOYEE_PORTAL);
            } else {
                return false;
            }
            if (!CollectionUtils.isEmpty(domainSsoList)) {
                for (Map < String, Object > domainSso: domainSsoList) {
                    if (!domainSso.isEmpty()) {
                        DomainSSO sso = FieldUtil.getAsBeanFromMap(domainSso, DomainSSO.class);
                        if (sso.getIsActive() != null) {
                            if (sso.getIsActive()) {
                                return true;
                            }
                        }
                    }

                }
            }
        }
        return false;

    }

    public static List<V3TenantContactContext> getTenantContactTenantAndRoles(Long tenantId,List<Long> roleIds) throws Exception {
        if(tenantId != null  && CollectionUtils.isNotEmpty(roleIds)){
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule tenantContactModule = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
            List<FacilioField> fields = Collections.singletonList(FieldFactory.getIdField("id","ID", tenantContactModule));

            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .table(tenantContactModule.getTableName())
                    .select(fields)
                    .innerJoin("ORG_Users")
                    .on("ORG_Users.PEOPLE_ID = Tenant_Contacts.ID")
                    .innerJoin("ORG_User_Apps")
                    .on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID")
                    .andCondition(CriteriaAPI.getCondition("ORG_User_Apps.ROLE_ID", "roleId", String.valueOf(StringUtils.join(roleIds, ",")), NumberOperators.EQUALS))
                    .andCondition(CriteriaAPI.getCondition("Tenant_Contacts.TENANT_ID", "tenantId", String.valueOf(tenantId), NumberOperators.EQUALS));

            List<Map<String, Object>> props = selectBuilder.get();
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(props)) {
                return FieldUtil.getAsBeanListFromMapList(props, V3TenantContactContext.class);
            }
        }
        return null;
    }
    public static List<Map<String, Object>> getPeopleForRoles(List<Long> roleIds) throws Exception {

        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(roleIds)) {
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule peopleModule = modBean.getModule(FacilioConstants.ContextNames.PEOPLE);
            List<FacilioField> fields = Collections.singletonList(FieldFactory.getIdField("id","ID", peopleModule));

            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .table(ModuleFactory.getPeopleModule().getTableName())
                    .select(fields)
                    .innerJoin("ORG_Users")
                    .on("ORG_Users.PEOPLE_ID = People.ID")
                    .innerJoin("ORG_User_Apps")
                    .on("ORG_Users.ORG_USERID = ORG_User_Apps.ORG_USERID")
                    .andCondition(CriteriaAPI.getCondition("ORG_User_Apps.ROLE_ID", "roleId", String.valueOf(StringUtils.join(roleIds, ",")), NumberOperators.EQUALS));

            List<Map<String, Object>> props = selectBuilder.get();
            if (org.apache.commons.collections4.CollectionUtils.isNotEmpty(props)) {
                return props;
            }
        }
        return null;


    }

    public static List<User> fetchUserContext(List<Long> peopleIds, boolean isInvitedAcceptedUsers) throws Exception{

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("PEOPLE_ID", "peopleId",StringUtils.join(peopleIds,","), NumberOperators.EQUALS));

        if (isInvitedAcceptedUsers){
            criteria.addAndCondition(CriteriaAPI.getCondition("INVITATION_ACCEPT_STATUS", "inviteAcceptStatus", true+"", BooleanOperators.IS));
        }

        GenericSelectRecordBuilder builder = UserBeanImpl.fetchUserSelectBuilder(AccountUtil.getCurrentApp() != null ? AccountUtil.getCurrentApp().getId() : -1L, criteria, AccountUtil.getCurrentOrg().getOrgId(), null);
        List<Map<String,Object>> props = builder.get();

        if(org.apache.commons.collections4.CollectionUtils.isNotEmpty(props)){

            List<User> users = new ArrayList<>();
            IAMUserUtil.setIAMUserPropsv3(props, AccountUtil.getCurrentOrg().getOrgId(), false);
            for (Map<String,Object> prop : props){
                User user = UserBeanImpl.createUserFromProps(prop, true, true, null);
                users.add(user);
            }

            return users;
        }
        return null;
    }


    public static boolean checkForEmailMisMatch(String email,Long peopleId) throws Exception {
        try{
            FacilioField peopleEmail = new FacilioField();
            peopleEmail.setName("peopleEmail");
            peopleEmail.setDataType(FieldType.STRING);
            peopleEmail.setColumnName("EMAIL");
            peopleEmail.setModule(ModuleFactory.getPeopleModule());

            List<FacilioField> fields = new ArrayList<>();
            fields.add(peopleEmail);
            GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                    .table("People")
                    .select(fields)
                    .andCondition(CriteriaAPI.getCondition("ID","id", String.valueOf(peopleId),NumberOperators.EQUALS));

            List<Map<String, Object>> props = selectBuilder.get();
            if (props!=null && !props.isEmpty()) {
                String existingEmail = String.valueOf(props.get(0).get("peopleEmail"));
                if ((existingEmail == null) || (existingEmail.equals(email))) {
                    return false;
                } else {
                    GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
                            .table("ORG_Users")
                            .innerJoin(ModuleFactory.getPeopleModule().getTableName())
                            .on("People.ID=ORG_Users.PEOPLE_ID")
                            .select(AccountConstants.getAppOrgUserFields())
                            .andCondition(CriteriaAPI.getCondition("People.ID", "id", String.valueOf(peopleId), NumberOperators.EQUALS));
                    List<Map<String, Object>> mapList = selectRecordBuilder.get();
                    if (CollectionUtils.isEmpty(mapList)) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }
        catch (Exception e){
            LOGGER.info("Exception occurred ", e);
        }
        return false;
    }

    public static void validatePeopleEmail(String email) throws Exception {
        if (StringUtils.isNotEmpty(email) && !VALID_EMAIL_ADDRESS_REGEX.matcher(email).find()) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Not a valid email - " + email);
        }
    }

    private static void addPermissionSetsForPeople(List<Long> permissionSets, long peopleId,String linkName) throws Exception{
        PermissionSetBean permissionSetBean = (PermissionSetBean) BeanFactory.lookup("PermissionSetBean");
        if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PERMISSION_SET)) {
            permissionSetBean.updateUserPermissionSets(peopleId,permissionSets);
        }
    }
}
