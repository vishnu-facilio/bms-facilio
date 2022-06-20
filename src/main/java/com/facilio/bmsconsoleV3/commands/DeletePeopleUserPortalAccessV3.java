package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class DeletePeopleUserPortalAccessV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3PeopleContext> peopleList = recordMap.get(moduleName);
        for (V3PeopleContext people : peopleList) {
            List records = recordMap.get(moduleName);
            long id =0;
            if (records != null && !records.isEmpty()) {
                for (int i = 0; i < records.size(); i++) {
                    if (people.getPeopleType() == V3PeopleContext.PeopleType.TENANT_CONTACT.getIndex()) {
                        V3TenantContactContext tc = (V3TenantContactContext) records.get(i);
                        id = tc.getId();
                    } else if (people.getPeopleType() == V3PeopleContext.PeopleType.VENDOR_CONTACT.getIndex()) {
                        V3VendorContactContext vc = (V3VendorContactContext) records.get(i);
                        id = vc.getId();
                    }
                }
            }
            V3PeopleAPI.deletePeopleUsers(id);
        }
        return false;
    }
}
