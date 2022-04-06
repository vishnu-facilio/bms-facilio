package com.facilio.bmsconsoleV3.commands.people;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3ClientContactContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.bmsconsoleV3.context.communityfeatures.ContactDirectoryContext;
import com.facilio.bmsconsoleV3.util.CommunityFeaturesAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdatePeoplePrimaryContactCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3PeopleContext> peopleList = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(peopleList)) {
            for(V3PeopleContext people : peopleList) {
                long parentId = -1;
                if(people instanceof V3TenantContactContext && ((V3TenantContactContext) people).getTenant() != null) {
                    parentId = ((V3TenantContactContext) people).getTenant().getId();
                    if(((V3TenantContactContext)people).isPrimaryContact()) {
                        V3PeopleAPI.unMarkPrimaryContact(people, parentId);
                        V3PeopleAPI.rollUpModulePrimarycontactFields(parentId, FacilioConstants.ContextNames.TENANT, people.getName(), people.getEmail(), people.getPhone());
                    }
                    else{
                        List<V3TenantContactContext> tenantContacts = V3PeopleAPI.getTenantContacts(parentId, false, false);
                        if(CollectionUtils.isNotEmpty(tenantContacts)){
                            if(tenantContacts.size() <= 1) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot mark contact as non primary");
                            }
                            else{
                                for(V3TenantContactContext tenantContact : tenantContacts){
                                    if(tenantContact.getId() != people.getId()){
                                        V3PeopleAPI.unMarkPrimaryContact(tenantContact, parentId);
                                        V3PeopleAPI.markPrimaryContact(tenantContact);
                                        V3PeopleAPI.rollUpModulePrimarycontactFields(parentId, FacilioConstants.ContextNames.TENANT, tenantContact.getName(), tenantContact.getEmail(), tenantContact.getPhone());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                else if(people instanceof V3VendorContactContext && ((V3VendorContactContext) people).getVendor() != null) {
                    parentId = ((V3VendorContactContext)people).getVendor().getId();
                    if(((V3VendorContactContext)people).isPrimaryContact()) {
                        V3PeopleAPI.unMarkPrimaryContact(people, parentId);
                        V3PeopleAPI.rollUpModulePrimarycontactFields(parentId, FacilioConstants.ContextNames.VENDORS, people.getName(), people.getEmail(), people.getPhone());
                    }
                    else{
                        List<V3VendorContactContext> vendorContacts = V3PeopleAPI.getVendorContacts(parentId, false);
                        if(CollectionUtils.isNotEmpty(vendorContacts)){
                            if(vendorContacts.size() <= 1) {
                                throw new RESTException(ErrorCode.VALIDATION_ERROR, "Cannot mark contact as non primary");
                            }
                            else{
                                for(V3VendorContactContext vendorContact : vendorContacts){
                                    if(vendorContact.getId() != people.getId()){
                                        V3PeopleAPI.unMarkPrimaryContact(vendorContact, parentId);
                                        V3PeopleAPI.markPrimaryContact(vendorContact);
                                        V3PeopleAPI.rollUpModulePrimarycontactFields(parentId, FacilioConstants.ContextNames.VENDORS, vendorContact.getName(), vendorContact.getEmail(), vendorContact.getPhone());
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                else if(people instanceof V3ClientContactContext && ((V3ClientContactContext) people).getClient() != null && ((V3ClientContactContext)people).isPrimaryContact()) {
                    parentId = ((V3ClientContactContext)people).getClient().getId();
                    V3PeopleAPI.unMarkPrimaryContact(people, parentId);
                    V3PeopleAPI.rollUpModulePrimarycontactFields(parentId, FacilioConstants.ContextNames.CLIENT, people.getName(), people.getEmail(), people.getPhone());
                }

                //update Contact directory fields if people is associated
                List<ContactDirectoryContext> contactList = CommunityFeaturesAPI.getContacts(people.getId());
                if(CollectionUtils.isNotEmpty(contactList)){
                    List<Long> ids = new ArrayList<>();
                    for(ContactDirectoryContext contact : contactList){
                        ids.add(contact.getId());
                    }
                    CommunityFeaturesAPI.updateContactDirectoryList(ids, people);
                }
            }
        }
        return false;
    }
}
