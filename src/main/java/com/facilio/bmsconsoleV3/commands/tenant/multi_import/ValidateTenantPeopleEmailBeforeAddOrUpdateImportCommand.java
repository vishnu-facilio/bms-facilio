package com.facilio.bmsconsoleV3.commands.tenant.multi_import;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.multiImport.constants.ImportConstants;
import com.facilio.multiImport.context.ImportRowContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class ValidateTenantPeopleEmailBeforeAddOrUpdateImportCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        EventType eventType = (EventType) context.getOrDefault(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
        List<Pair<Long,ModuleBaseWithCustomFields>> tenants = null;
        Map<Long, ImportRowContext> logIdVsImportRows = ImportConstants.getLogIdVsRowContextMap(context);
        Map<String,V3PeopleContext> emailVsPeople = (Map<String,V3PeopleContext>)context.get(FacilioConstants.ContextNames.EMAIL_VS_PEOPLE_MAP);


        if(eventType == EventType.CREATE){
            Map<String, List<Pair<Long, ModuleBaseWithCustomFields>>> insertRecordMap = ImportConstants.getInsertRecordMap(context);
            tenants = insertRecordMap.get(moduleName);
        }else if(eventType == EventType.EDIT){
            Map<String, List<Pair<Long, ModuleBaseWithCustomFields>>> updateRecordMap = ImportConstants.getUpdateRecordMap(context);
            tenants = updateRecordMap.get(moduleName);
        }
        if(CollectionUtils.isEmpty(tenants)) {
            return false;
        }

        Map<Long,V3TenantContactContext> tenantIdVsPrimaryTenantContactMap = null;
        if(eventType == EventType.EDIT ){
            tenantIdVsPrimaryTenantContactMap = getTenantIdVsPrimaryTenantContactMap(tenants);
        }
        context.put(FacilioConstants.ContextNames.TENANT_ID_VS_PRIMARY_TENANT_CONTACT,tenantIdVsPrimaryTenantContactMap);
        context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.TENANT_ACTIVITY);


        if (!AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_CONTACTS) || MapUtils.isEmpty(emailVsPeople)) {
            return false;
        }


        for (Pair<Long,ModuleBaseWithCustomFields> logIdVsTenant : tenants) {
            Long logId = logIdVsTenant.getKey();

            try {
                V3TenantContext tenant= (V3TenantContext) logIdVsTenant.getValue();
                V3TenantContactContext tc = getDefaultTenantContact(tenant);
                String trimmedEmail = tc.getEmail().trim();
                tc.setEmail(trimmedEmail);
                V3PeopleAPI.validatePeopleEmail(trimmedEmail);

                if(eventType == EventType.CREATE){
                    if(StringUtils.isNotEmpty(tc.getEmail()) && emailVsPeople.containsKey(tc.getEmail())){
                        throw  new IllegalArgumentException("People with the same email id already exists");
                    }
                }
                else if(eventType == EventType.EDIT){
                    V3TenantContactContext tenantPrimaryContact = tenantIdVsPrimaryTenantContactMap.get(tc.getTenant().getId());
                    if(checkEmailUniqueness(tc,tenantPrimaryContact)){
                        if(emailVsPeople.containsKey(tc.getEmail())) {
                            throw  new IllegalArgumentException("People with the same email id already exists");
                        }
                    }
                }


            }
            catch (Exception e){
                ImportRowContext importRowContext = logIdVsImportRows.get(logId);
                importRowContext.setErrorOccurredRow(true);
                importRowContext.setErrorMessage(e.getMessage());
            }

        }
        return false;
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
    private boolean checkEmailUniqueness(V3TenantContactContext tc,V3TenantContactContext tenantPrimaryContact){
        if(tenantPrimaryContact!=null){
            if(StringUtils.isNotEmpty(tc.getEmail())){
                if(StringUtils.isEmpty(tenantPrimaryContact.getEmail()) ||  !tenantPrimaryContact.getEmail().equals(tc.getEmail())){
                    return true;
                }
            }
        }else if(StringUtils.isNotEmpty(tc.getEmail())){
                return true;
        }

        return false;
    }
    private Map<Long,V3TenantContactContext> getTenantIdVsPrimaryTenantContactMap(List<Pair<Long,ModuleBaseWithCustomFields>> tenants) throws Exception {
        Set<Long> tenantIds = new HashSet<>();
        tenants.forEach(p->{
            tenantIds.add(p.getValue().getId());
        });
        List<V3TenantContactContext> primaryContacts = V3PeopleAPI.getTenantContacts(tenantIds,true,false);

        Map<Long,V3TenantContactContext> tenantIdVsPrimaryContactMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(primaryContacts)){
            primaryContacts.forEach(tc->{
                long tenantId = tc.getTenant().getId();
                if(!tenantIdVsPrimaryContactMap.containsKey(tenantId)){
                    tenantIdVsPrimaryContactMap.put(tenantId,tc);
                }
            });
        }
        return tenantIdVsPrimaryContactMap;
    }
}
