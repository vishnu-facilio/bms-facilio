package com.facilio.bmsconsoleV3.commands.tenantcontact;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3ClientContactContext;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

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
                if(people instanceof V3TenantContactContext && ((V3TenantContactContext) people).getTenant() != null && ((V3TenantContactContext)people).isPrimaryContact()) {
                    parentId = ((V3TenantContactContext)people).getTenant().getId();
                    V3PeopleAPI.unMarkPrimaryContact(people, parentId);
                    V3PeopleAPI.rollUpModulePrimarycontactFields(parentId, FacilioConstants.ContextNames.TENANT, people.getName(), people.getEmail(), people.getPhone());
                }
                else if(people instanceof V3VendorContactContext && ((V3VendorContactContext) people).getVendor() != null && ((V3VendorContactContext)people).isPrimaryContact()) {
                    parentId = ((V3VendorContactContext)people).getVendor().getId();
                    V3PeopleAPI.unMarkPrimaryContact(people, parentId);
                    V3PeopleAPI.rollUpModulePrimarycontactFields(parentId, FacilioConstants.ContextNames.VENDORS, people.getName(), people.getEmail(), people.getPhone());
                }
                else if(people instanceof V3ClientContactContext && ((V3ClientContactContext) people).getClient() != null && ((V3ClientContactContext)people).isPrimaryContact()) {
                    parentId = ((V3ClientContactContext)people).getClient().getId();
                    V3PeopleAPI.unMarkPrimaryContact(people, parentId);
                    V3PeopleAPI.rollUpModulePrimarycontactFields(parentId, FacilioConstants.ContextNames.CLIENT, people.getName(), people.getEmail(), people.getPhone());
                }
            }
        }
        return false;
    }
}
