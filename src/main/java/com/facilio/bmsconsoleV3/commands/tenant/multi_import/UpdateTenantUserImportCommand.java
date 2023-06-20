package com.facilio.bmsconsoleV3.commands.tenant.multi_import;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3ContactsContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3ContactsAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class UpdateTenantUserImportCommand extends FacilioCommand {
    private static final List<String> tcUpdateFieldNames = Arrays.asList("name","tenant","email","phone","isPrimaryContact");
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List<Pair<Long, ModuleBaseWithCustomFields>>> updateRecordMap = ImportConstants.getUpdateRecordMap(context);
        List<Pair<Long,ModuleBaseWithCustomFields>> tenants = updateRecordMap.get(moduleName);
        Map<Long, ImportRowContext> logIdVsImportRows = ImportConstants.getLogIdVsRowContextMap(context);

        if(CollectionUtils.isEmpty(tenants)) {
            return false;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule contactModule = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
        List<FacilioField> contactFields = modBean.getAllFields(contactModule.getName());
        FacilioModule tcModule = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
        List<FacilioField> tcFields = modBean.getAllFields(tcModule.getName());
        Map<Long,V3TenantContactContext> tenantIdVsPrimaryTenantContactMap = (Map<Long, V3TenantContactContext>) context.get(FacilioConstants.ContextNames.TENANT_ID_VS_PRIMARY_TENANT_CONTACT);

        List<V3ContactsContext> addPrimaryContacts = new ArrayList<>();
        List<V3ContactsContext> updatePrimaryContacts = new ArrayList<>();
        List<V3PeopleContext> addTenantContacts = new ArrayList<>();
        List<V3PeopleContext> updateTenantContacts = new ArrayList<>();

        for (Pair<Long,ModuleBaseWithCustomFields> logIdVsTenant : tenants) {
            Long logId = logIdVsTenant.getKey();

            try {
                V3TenantContext tenant= (V3TenantContext) logIdVsTenant.getValue();

                if (StringUtils.isNotEmpty(tenant.getPrimaryContactPhone()) ) {
                    V3ContactsContext existingcontactForPhone = V3ContactsAPI.getContactforPhone(tenant.getPrimaryContactPhone(), tenant.getId(), false);
                    if (existingcontactForPhone == null) {
                        existingcontactForPhone = getDefaultTenantPrimaryContact(tenant);
                        addPrimaryContacts.add(existingcontactForPhone);
                    } else {
                        existingcontactForPhone.setName(tenant.getPrimaryContactName());
                        existingcontactForPhone.setEmail(tenant.getPrimaryContactEmail());
                        updatePrimaryContacts.add(existingcontactForPhone);
                    }
                }
                if(AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_CONTACTS)){
                    V3TenantContactContext tc = getDefaultTenantContact(tenant);
                    V3TenantContactContext primaryTenantContact = tenantIdVsPrimaryTenantContactMap.get(tc.getTenant().getId());

                    V3PeopleAPI.addParentPrimaryContactAsPeople(tc,primaryTenantContact,addTenantContacts,updateTenantContacts);
                }

            }
            catch (Exception e){
                ImportRowContext importRowContext = logIdVsImportRows.get(logId);
                importRowContext.setErrorOccurredRow(true);
                importRowContext.setErrorMessage(e.getMessage());
            }

        }

        if(CollectionUtils.isNotEmpty(addPrimaryContacts)){
            V3RecordAPI.addRecord(true, addPrimaryContacts, contactModule, contactFields);
        }
        if(CollectionUtils.isNotEmpty(addTenantContacts)){
            V3RecordAPI.addRecord(true, addTenantContacts, tcModule, tcFields);
        }

        if(CollectionUtils.isNotEmpty(updatePrimaryContacts)){
            V3RecordAPI.batchUpdateRecordsWithHandlingLookup(contactModule.getName(),updatePrimaryContacts,contactFields);
        }
        if(CollectionUtils.isNotEmpty(updateTenantContacts)){
            V3RecordAPI.batchUpdateRecordsWithHandlingLookup(tcModule.getName(),updateTenantContacts,getTenantContactUpdateFields(tcFields));
        }

        context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.TENANT_ACTIVITY);
        return false;
    }
    private V3ContactsContext getDefaultTenantPrimaryContact(V3TenantContext tenant) throws Exception {
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
    private static List<FacilioField> getTenantContactUpdateFields(List<FacilioField> tcFields){
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(tcFields);
        List<FacilioField> tcUpdateFields = new ArrayList<>();
        for(String fieldName : tcUpdateFieldNames){
            if(fieldMap.containsKey(fieldName)){
                tcUpdateFields.add(fieldMap.get(fieldName));
            }

        }
        return tcUpdateFields;
    }

}
