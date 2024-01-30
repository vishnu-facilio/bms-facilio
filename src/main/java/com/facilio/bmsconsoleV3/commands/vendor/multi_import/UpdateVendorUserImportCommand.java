package com.facilio.bmsconsoleV3.commands.vendor.multi_import;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.*;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class UpdateVendorUserImportCommand extends FacilioCommand {
    private static final List<String> vcUpdateFieldNames = Arrays.asList("name","vendor","email","phone","isPrimaryContact");
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List<Pair<Long, ModuleBaseWithCustomFields>>> updateRecordMap = ImportConstants.getUpdateRecordMap(context);
        List<Pair<Long,ModuleBaseWithCustomFields>> vendors = updateRecordMap.get(moduleName);
        Map<Long, ImportRowContext> logIdVsImportRows = ImportConstants.getLogIdVsRowContextMap(context);

        if(CollectionUtils.isEmpty(vendors)) {
            return false;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule contactModule = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
        List<FacilioField> contactFields = modBean.getAllFields(contactModule.getName());
        FacilioModule vcModule = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);
        List<FacilioField> vcFields = modBean.getAllFields(vcModule.getName());
        Map<Long, V3VendorContactContext> vendorIdVsPrimaryVendorContactMap = (Map<Long, V3VendorContactContext>) context.get(FacilioConstants.ContextNames.VENDOR_ID_VS_PRIMARY_TENANT_CONTACT);

        List<V3ContactsContext> addPrimaryContacts = new ArrayList<>();
        List<V3ContactsContext> updatePrimaryContacts = new ArrayList<>();
        List<V3PeopleContext> addVendorContacts = new ArrayList<>();
        List<V3PeopleContext> updateVendorContacts = new ArrayList<>();

        List<Long> unMarkPrimaryContactVendorIdsInContactModule = new ArrayList<>();
        List<Long> unMarkPrimaryContactVendorIdsInVendorContactModule = new ArrayList<>();

        for (Pair<Long,ModuleBaseWithCustomFields> logIdVsVendor : vendors) {
            Long logId = logIdVsVendor.getKey();

            try {
                V3VendorContext vendor= (V3VendorContext) logIdVsVendor.getValue();

                if (StringUtils.isNotEmpty(vendor.getPrimaryContactPhone()) ) {
                    V3ContactsContext existingcontactForPhone = V3ContactsAPI.getContactforPhone(vendor.getPrimaryContactPhone(), vendor.getId(), false);
                    if (existingcontactForPhone == null) {
                        existingcontactForPhone = getDefaultVendorPrimaryContact(vendor);
                        addPrimaryContacts.add(existingcontactForPhone);
                        unMarkPrimaryContactVendorIdsInContactModule.add(vendor.getId());
                    } else {
                        existingcontactForPhone.setName(vendor.getPrimaryContactName());
                        existingcontactForPhone.setEmail(vendor.getPrimaryContactEmail());
                        updatePrimaryContacts.add(existingcontactForPhone);
                    }
                }
                    V3VendorContactContext tc = getDefaultVendorContact(vendor);
                    V3VendorContactContext primaryVendorContact = vendorIdVsPrimaryVendorContactMap.get(tc.getVendor().getId());

                    V3PeopleAPI.addParentPrimaryContactAsPeople(tc,primaryVendorContact,tc.getVendor().getId(),addVendorContacts,updateVendorContacts,unMarkPrimaryContactVendorIdsInVendorContactModule);

            }
            catch (Exception e){
                ImportRowContext importRowContext = logIdVsImportRows.get(logId);
                importRowContext.setErrorOccurredRow(true);
                importRowContext.setErrorMessage(e.toString());
            }

        }


        if(CollectionUtils.isNotEmpty(addPrimaryContacts)){
            V3ContactsAPI.unMarkPrimaryContact(null,unMarkPrimaryContactVendorIdsInContactModule,V3ContactsContext.ContactType.VENDOR);
            V3RecordAPI.addRecord(true, addPrimaryContacts, contactModule, contactFields);
        }
        if(CollectionUtils.isNotEmpty(addVendorContacts)){
            V3PeopleAPI.unMarkPrimaryContact(addVendorContacts,unMarkPrimaryContactVendorIdsInVendorContactModule);
            V3RecordAPI.addRecord(true, addVendorContacts, vcModule, vcFields);
        }

        if(CollectionUtils.isNotEmpty(updatePrimaryContacts)){
            V3RecordAPI.batchUpdateRecordsWithHandlingLookup(contactModule.getName(),updatePrimaryContacts,contactFields);
        }
        if(CollectionUtils.isNotEmpty(updateVendorContacts)){
            V3RecordAPI.batchUpdateRecordsWithHandlingLookup(vcModule.getName(),updateVendorContacts,getVendorContactUpdateFields(vcFields));
        }

        context.put(FacilioConstants.ContextNames.CURRENT_ACTIVITY, FacilioConstants.ContextNames.TENANT_ACTIVITY);
        return false;
    }
    private V3ContactsContext getDefaultVendorPrimaryContact(V3VendorContext vendor) throws Exception {
        V3ContactsContext contact = new V3ContactsContext();
        contact.setName(vendor.getPrimaryContactName() != null ? vendor.getPrimaryContactName() : vendor.getName());
        contact.setContactType(V3ContactsContext.ContactType.VENDOR.getIndex());
        contact.setVendor(vendor);
        contact.setEmail(vendor.getPrimaryContactEmail());
        contact.setPhone(vendor.getPrimaryContactPhone());
        contact.setIsPrimaryContact(true);
        contact.setIsPortalAccessNeeded(false);
        return contact;
    }
    private V3VendorContactContext getDefaultVendorContact(V3VendorContext vendor) throws Exception {
        V3VendorContactContext tc = new V3VendorContactContext();
        tc.setName(vendor.getPrimaryContactName());
        tc.setEmail(vendor.getPrimaryContactEmail());
        tc.setPhone(vendor.getPrimaryContactPhone());
        tc.setPeopleType(V3PeopleContext.PeopleType.VENDOR_CONTACT.getIndex());
        tc.setVendor(vendor);
        tc.setIsPrimaryContact(true);

        return tc;
    }
    private static List<FacilioField> getVendorContactUpdateFields(List<FacilioField> tcFields){
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(tcFields);
        List<FacilioField> tcUpdateFields = new ArrayList<>();
        for(String fieldName : vcUpdateFieldNames){
            if(fieldMap.containsKey(fieldName)){
                tcUpdateFields.add(fieldMap.get(fieldName));
            }

        }
        return tcUpdateFields;
    }

}
