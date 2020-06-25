package com.facilio.bmsconsoleV3.commands.vendorcontact;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class CheckForMandatoryVendorIdCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3VendorContactContext> vendorContacts = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(vendorContacts)) {
            for(V3VendorContactContext vc : vendorContacts) {
                vc.setPeopleType(V3PeopleContext.PeopleType.VENDOR_CONTACT);
                if(vc.getVendor() == null || vc.getVendor().getId() <=0 ) {
                    throw new IllegalArgumentException("Vendor Contact must have a vendor id associated");
                }
            }
        }
        return false;
    }
}
