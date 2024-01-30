package com.facilio.bmsconsoleV3.commands.tenant.multi_import;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3ContactsContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AddTenantUserImportCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List<Pair<Long, ModuleBaseWithCustomFields>>> insertRecordMap = ImportConstants.getInsertRecordMap(context);
        List<Pair<Long,ModuleBaseWithCustomFields>> tenants = insertRecordMap.get(moduleName);
        Map<Long, ImportRowContext> logIdVsImportRows = ImportConstants.getLogIdVsRowContextMap(context);

        if(CollectionUtils.isEmpty(tenants)) {
            return false;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule contactModule = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
        List<FacilioField> contactFields = modBean.getAllFields(contactModule.getName());
        FacilioModule tcModule = modBean.getModule(FacilioConstants.ContextNames.TENANT_CONTACT);
        List<FacilioField> tcFields = modBean.getAllFields(tcModule.getName());

        List<V3ContactsContext> primaryContacts = new ArrayList<>();
        List<V3TenantContactContext> tenantContacts = new ArrayList<>();
        for (Pair<Long,ModuleBaseWithCustomFields> logIdVsTenant : tenants) {
            Long logId = logIdVsTenant.getKey();

            try {
                V3TenantContext tenant= (V3TenantContext) logIdVsTenant.getValue();

                V3ContactsContext primaryContact = getDefaultTenantPrimaryContact(tenant);
                primaryContacts.add(primaryContact);


                    V3TenantContactContext tc = getDefaultTenantContact(tenant);
                    tenantContacts.add(tc);


            }
            catch (Exception e){
                ImportRowContext importRowContext = logIdVsImportRows.get(logId);
                importRowContext.setErrorOccurredRow(true);
                importRowContext.setErrorMessage(e.getMessage());
            }

        }

        if(CollectionUtils.isNotEmpty(primaryContacts)){
            V3RecordAPI.addRecord(true, primaryContacts, contactModule, contactFields);
        }
        if(CollectionUtils.isNotEmpty(tenantContacts)){
            V3RecordAPI.addRecord(true, tenantContacts, tcModule, tcFields);
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

}
