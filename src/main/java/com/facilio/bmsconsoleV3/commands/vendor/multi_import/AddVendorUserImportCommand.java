package com.facilio.bmsconsoleV3.commands.vendor.multi_import;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsoleV3.context.V3ContactsContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.bmsconsoleV3.context.V3VendorContext;
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

public class AddVendorUserImportCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List<Pair<Long, ModuleBaseWithCustomFields>>> insertRecordMap = ImportConstants.getInsertRecordMap(context);
        List<Pair<Long,ModuleBaseWithCustomFields>> vendors = insertRecordMap.get(moduleName);
        Map<Long, ImportRowContext> logIdVsImportRows = ImportConstants.getLogIdVsRowContextMap(context);

        if(CollectionUtils.isEmpty(vendors)) {
            return false;
        }
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        FacilioModule contactModule = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
        List<FacilioField> contactFields = modBean.getAllFields(contactModule.getName());
        FacilioModule vcModule = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);
        List<FacilioField> vcFields = modBean.getAllFields(vcModule.getName());

        List<V3ContactsContext> primaryContacts = new ArrayList<>();
        List<V3VendorContactContext> vendorContacts = new ArrayList<>();
        for (Pair<Long,ModuleBaseWithCustomFields> logIdVsTenant : vendors) {
            Long logId = logIdVsTenant.getKey();

            try {
                V3VendorContext vendor= (V3VendorContext) logIdVsTenant.getValue();

                V3ContactsContext primaryContact = getDefaultVendorPrimaryContact(vendor);
                primaryContacts.add(primaryContact);

                if (AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.PEOPLE_CONTACTS)) {
                    V3VendorContactContext vc = getDefaultVendorContact(vendor);
                    vendorContacts.add(vc);
                }

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
        if(CollectionUtils.isNotEmpty(vendorContacts)){
            V3RecordAPI.addRecord(true, vendorContacts, vcModule, vcFields);
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

}
