package com.facilio.bmsconsoleV3.commands.vendor;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.util.V3ContactsAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class AddVendorContactsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        boolean skipPeopleContact = FacilioUtil.parseBoolean(Constants.getQueryParam(context, FacilioConstants.ContextNames.SKIP_PEOPLE_CONTACTS));
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3VendorContext> vendors = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(vendors)) {

            EventType eventType = (EventType) context.getOrDefault(FacilioConstants.ContextNames.EVENT_TYPE, EventType.CREATE);
            ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
            FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.CONTACT);
            List<FacilioField> fields = modBean.getAllFields(module.getName());

            FacilioModule vcModule = modBean.getModule(FacilioConstants.ContextNames.VENDOR_CONTACT);
            List<FacilioField> vcFields = modBean.getAllFields(vcModule.getName());
            for(V3VendorContext vendor : vendors) {

                if (eventType == EventType.CREATE) {
                    V3ContactsContext primarycontact = addDefaultVendorPrimaryContact(vendor);
                    V3RecordAPI.addRecord(true, Collections.singletonList(primarycontact), module, fields);
                } else {
                    if (StringUtils.isNotEmpty(vendor.getPrimaryContactPhone())) {
                        V3ContactsContext existingcontactForPhone = V3ContactsAPI.getContactforPhone(vendor.getPrimaryContactPhone(), vendor.getId(), true);
                        if (existingcontactForPhone == null) {
                            existingcontactForPhone = addDefaultVendorPrimaryContact(vendor);
                            V3RecordAPI.addRecord(true, Collections.singletonList(existingcontactForPhone), module, fields);
                        } else {
                            existingcontactForPhone.setName(vendor.getPrimaryContactName());
                            existingcontactForPhone.setEmail(vendor.getPrimaryContactEmail());
                            V3RecordAPI.updateRecord(existingcontactForPhone, module, fields);
                        }
                    }
                }

                if (!skipPeopleContact) {
                    V3VendorContactContext vc = getDefaultVendorContact(vendor);
                    List<V3VendorContactContext> primarycontatsIfAny = V3PeopleAPI.getVendorContacts(vc.getVendor().getId(), true);
                    V3VendorContactContext vendorPrimaryContact = null;
                    if (CollectionUtils.isNotEmpty(primarycontatsIfAny)) {
                        vendorPrimaryContact = primarycontatsIfAny.get(0);
                    }
                    V3PeopleAPI.addParentPrimaryContactAsPeople(vc, vcModule, vcFields, vc.getVendor().getId(), vendorPrimaryContact);
                }
            }
        }

        return false;
    }

    private V3ContactsContext addDefaultVendorPrimaryContact(V3VendorContext vendor) throws Exception {
        V3ContactsAPI.unMarkPrimaryContact(-1, vendor.getId(), V3ContactsContext.ContactType.VENDOR);
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
