package com.facilio.bmsconsoleV3.commands.tenant;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3ContactsContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3ContactsAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class AddTenantUserCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TenantContext> tenants = recordMap.get(moduleName);

        Map<String, List<Object>> queryParams = (Map<String, List<Object>>)context.get(Constants.QUERY_PARAMS);
        Boolean spaceUpdate = false;

        if(MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("spacesUpdate")) {
            spaceUpdate = true;
        }
        if(CollectionUtils.isNotEmpty(tenants)) {
            for(V3TenantContext tenant : tenants) {
                ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
                FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
                List<FacilioField> fields = modBean.getAllFields(module.getName());
                FacilioModule tcModule = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
                List<FacilioField> tcFields = modBean.getAllFields(tcModule.getName());
                EventType eventType = (EventType) context.getOrDefault(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);

                if (eventType == EventType.CREATE) {
                    V3ContactsContext primarycontact = addDefaultTenantPrimaryContact(tenant);
                    V3RecordAPI.addRecord(true, Collections.singletonList(primarycontact), module, fields);
                } else {
                    if (StringUtils.isNotEmpty(tenant.getPrimaryContactPhone()) && !spaceUpdate) {
                        V3ContactsContext existingcontactForPhone = V3ContactsAPI.getContactforPhone(tenant.getPrimaryContactPhone(), tenant.getId(), false);
                        if (existingcontactForPhone == null) {
                            existingcontactForPhone = addDefaultTenantPrimaryContact(tenant);
                            V3RecordAPI.addRecord(true, Collections.singletonList(existingcontactForPhone), module, fields);
                        } else {
                            existingcontactForPhone.setName(tenant.getPrimaryContactName());
                            existingcontactForPhone.setEmail(tenant.getPrimaryContactEmail());
                            V3RecordAPI.updateRecord(existingcontactForPhone, module, fields);
                        }
                    }
                }
                if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_CONTACTS) && !spaceUpdate) {
                    V3TenantContactContext tc = getDefaultTenantContact(tenant);
                    List<V3TenantContactContext> primarycontatsIfAny = V3PeopleAPI.getTenantContacts(tc.getTenant().getId(), true, false);
                    V3TenantContactContext tenantPrimaryContact = null;
                    if (CollectionUtils.isNotEmpty(primarycontatsIfAny)) {
                        tenantPrimaryContact = primarycontatsIfAny.get(0);
                    }
                    V3PeopleAPI.addParentPrimaryContactAsPeople(tc, tcModule, tcFields, tc.getTenant().getId(), tenantPrimaryContact);
                }
            }
        }

        return false;
    }

    public static int unMarkPrimaryContact(long contactId, long id, V3ContactsContext.ContactType contactType) throws Exception{

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

        if(contactId > 0) {
            updateBuilder.andCondition(CriteriaAPI.getCondition("ID", "contactId", String.valueOf(contactId), NumberOperators.NOT_EQUALS));

        }
        if(contactType == V3ContactsContext.ContactType.VENDOR) {
            updateBuilder.andCondition(CriteriaAPI.getCondition("VENDOR_ID", "vendor", String.valueOf(id), NumberOperators.EQUALS));
        }
        else if(contactType == V3ContactsContext.ContactType.TENANT){
            updateBuilder.andCondition(CriteriaAPI.getCondition("TENANT_ID", "tenant", String.valueOf(id), NumberOperators.EQUALS));
        } else if(contactType == V3ContactsContext.ContactType.CLIENT){
            updateBuilder.andCondition(CriteriaAPI.getCondition("CLIENT_ID", "client", String.valueOf(id), NumberOperators.EQUALS));
        }

        Map<String, Object> value = new HashMap<>();
        value.put("isPrimaryContact", false);
        int count = updateBuilder.update(value);
        return count;

    }

    private V3ContactsContext addDefaultTenantPrimaryContact(V3TenantContext tenant) throws Exception {
        V3ContactsAPI.unMarkPrimaryContact(-1, tenant.getId(), V3ContactsContext.ContactType.TENANT);
        V3ContactsContext contact = new V3ContactsContext();
        contact.setName(tenant.getPrimaryContactName() != null ? tenant.getPrimaryContactName() : tenant.getName());
        contact.setContactType(V3ContactsContext.ContactType.TENANT.getIndex());
        contact.setTenant(tenant);
        contact.setEmail(tenant.getPrimaryContactEmail());
        contact.setPhone(tenant.getPrimaryContactPhone());
        contact.setIsPrimaryContact(true);
        contact.setIsPortalAccessNeeded(false);
        return contact;
    }

    private V3TenantContactContext getDefaultTenantContact(V3TenantContext tenant) throws Exception {
        V3TenantContactContext tc = new V3TenantContactContext();
        tc.setName(tenant.getPrimaryContactName());
        tc.setEmail(tenant.getPrimaryContactEmail());
        tc.setPhone(tenant.getPrimaryContactPhone());
        tc.setPeopleType(V3PeopleContext.PeopleType.TENANT_CONTACT.getIndex());
        tc.setTenant(tenant);
        tc.setIsPrimaryContact(true);

        return tc;
    }
}
