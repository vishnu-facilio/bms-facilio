package com.facilio.bmsconsoleV3.commands.vendorcontact;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.VendorContactContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class UpdateVendorContactAppPortalAccessCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3VendorContactContext> vendorContacts = recordMap.get(moduleName);

        Map<Long, List<UpdateChangeSet>> changeSet = Constants.getModuleChangeSets(context);
        if(CollectionUtils.isNotEmpty(vendorContacts) && MapUtils.isNotEmpty(changeSet)) {
            for (V3VendorContactContext vc : vendorContacts) {
                List<UpdateChangeSet> changes = changeSet.get(vc.getId());
                if (CollectionUtils.isNotEmpty(changes) && RecordAPI.checkChangeSet(changes, "isVendorPortalAccess", FacilioConstants.ContextNames.VENDOR_CONTACT)) {
                    V3PeopleAPI.updateVendorContactAppPortalAccess(vc, FacilioConstants.ApplicationLinkNames.VENDOR_PORTAL_APP);
                }
            }
        }
        return false;
    }
}
