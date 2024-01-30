package com.facilio.bmsconsoleV3.commands.vendor.multi_import;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
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
import java.util.stream.Collectors;

public class ValidateVendorPeopleEmailBeforeAddOrUpdateImportCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        EventType eventType = (EventType) context.getOrDefault(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
        List<Pair<Long,ModuleBaseWithCustomFields>> vendors = null;
        Map<Long, ImportRowContext> logIdVsImportRows = ImportConstants.getLogIdVsRowContextMap(context);
        Map<String,V3PeopleContext> emailVsPeople = (Map<String,V3PeopleContext>)context.get(FacilioConstants.ContextNames.EMAIL_VS_PEOPLE_MAP);


        if(eventType == EventType.CREATE){
            Map<String, List<Pair<Long, ModuleBaseWithCustomFields>>> insertRecordMap = ImportConstants.getInsertRecordMap(context);
            vendors = insertRecordMap.get(moduleName);
        }else if(eventType == EventType.EDIT){
            Map<String, List<Pair<Long, ModuleBaseWithCustomFields>>> updateRecordMap = ImportConstants.getUpdateRecordMap(context);
            vendors = updateRecordMap.get(moduleName);
        }

        if(CollectionUtils.isEmpty(vendors)) {
            return false;
        }

        Map<Long, V3VendorContactContext> vendorIdVsPrimaryVendorContactMap = null;
        if(eventType == EventType.EDIT ){
            vendorIdVsPrimaryVendorContactMap = getVendorIdVsPrimaryTenantContactMap(vendors);
        }
        context.put(FacilioConstants.ContextNames.VENDOR_ID_VS_PRIMARY_TENANT_CONTACT,vendorIdVsPrimaryVendorContactMap);
        context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.VENDOR_ACTIVITY);

        if (MapUtils.isEmpty(emailVsPeople)) {
            return false;
        }

        for (Pair<Long,ModuleBaseWithCustomFields> logIdVsVendor : vendors) {
            Long logId = logIdVsVendor.getKey();

            try {
                V3VendorContext vendor= (V3VendorContext) logIdVsVendor.getValue();
                V3VendorContactContext vc = getDefaultVendorContact(vendor);
                String trimmedEmail = vc.getEmail().trim();
                vc.setEmail(trimmedEmail);
                V3PeopleAPI.validatePeopleEmail(trimmedEmail);

                if(eventType == EventType.CREATE){
                    if(StringUtils.isNotEmpty(vc.getEmail()) && emailVsPeople.containsKey(vc.getEmail())){
                        throw  new IllegalArgumentException("People with the same email id already exists");
                    }
                }
                else if(eventType == EventType.EDIT){
                    V3VendorContactContext vendorPrimaryContact = vendorIdVsPrimaryVendorContactMap.get(vc.getVendor().getId());
                    if(checkEmailUniqueness(vc,vendorPrimaryContact)){
                        if(emailVsPeople.containsKey(vc.getEmail())) {
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
    private V3VendorContactContext getDefaultVendorContact(V3VendorContext vendor) throws Exception {
        V3VendorContactContext vc = new V3VendorContactContext();
        vc.setName(vendor.getPrimaryContactName());
        vc.setEmail(vendor.getPrimaryContactEmail());
        vc.setPhone(vendor.getPrimaryContactPhone());
        vc.setPeopleType(V3PeopleContext.PeopleType.VENDOR_CONTACT.getIndex());
        vc.setVendor(vendor);
        vc.setIsPrimaryContact(true);

        return vc;
    }
    private boolean checkEmailUniqueness(V3VendorContactContext vc,V3VendorContactContext vendorPrimaryContact){
        if(vendorPrimaryContact!=null){
            if(StringUtils.isNotEmpty(vc.getEmail())){
                if(StringUtils.isEmpty(vendorPrimaryContact.getEmail()) ||  !vendorPrimaryContact.getEmail().equals(vc.getEmail())){
                    return true;
                }
            }
        }else if(StringUtils.isNotEmpty(vc.getEmail())) {
                return true;
        }

        return false;
    }
    private Map<Long,V3VendorContactContext> getVendorIdVsPrimaryTenantContactMap(List<Pair<Long,ModuleBaseWithCustomFields>> vendors) throws Exception {
        Set<Long> vendorIds = vendors.stream().map(p->p.getValue().getId()).collect(Collectors.toSet());

        List<V3VendorContactContext> primaryContacts = V3PeopleAPI.getVendorContacts(vendorIds,true,false);

        Map<Long,V3VendorContactContext> vendorIdVsPrimaryContactMap = new HashMap<>();
        if(CollectionUtils.isNotEmpty(primaryContacts)){
            primaryContacts.forEach(vc->{
                long vendorId = vc.getVendor().getId();
                if(!vendorIdVsPrimaryContactMap.containsKey(vendorId)){
                    vendorIdVsPrimaryContactMap.put(vendorId,vc);
                }
            });
        }
        return vendorIdVsPrimaryContactMap;
    }

}
