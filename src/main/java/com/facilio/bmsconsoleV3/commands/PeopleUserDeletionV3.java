package com.facilio.bmsconsoleV3.commands;

import com.facilio.bmsconsoleV3.context.*;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import java.util.List;

public class PeopleUserDeletionV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        List records = (List) context.get("deletedRecords");
        long id = 0;
        if (records != null && !records.isEmpty()) {
            for(int  i=0; i< records.size(); i++){
                if(records.get(0) instanceof V3TenantContext) {
                    if (records.get(0) instanceof V3TenantContext) {
                        V3TenantContext tenant = (V3TenantContext) records.get(i);
                        long tenant_id = tenant.getId();
                        List<V3TenantContactContext> tenantContacts = V3PeopleAPI.getTenantContacts(tenant_id, false, false, true);
                        for (V3TenantContactContext tc : tenantContacts) {
                            id = tc.getId();
                        }
                    }
                }
                else if(records.get(0) instanceof V3VendorContext){
                    V3VendorContext vendor = (V3VendorContext) records.get(i);
                    long vendor_id = vendor.getId();
                    List<V3VendorContactContext> vendorContacts = V3PeopleAPI.getVendorContacts(vendor_id, false, true);
                        for(V3VendorContactContext vc : vendorContacts){
                            id = vc.getId();
                        }
                    }
                else if(records.get(0) instanceof V3EmployeeContext){
                    V3EmployeeContext employee = (V3EmployeeContext) records.get(i);
                    id = employee.getId();
                }
            }
        }
        V3PeopleAPI.deletePeopleUsers(id);
        return false;
    }
}
