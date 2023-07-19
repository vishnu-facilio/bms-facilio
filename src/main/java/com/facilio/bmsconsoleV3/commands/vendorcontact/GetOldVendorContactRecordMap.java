package com.facilio.bmsconsoleV3.commands.vendorcontact;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3VendorContactContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetOldVendorContactRecordMap extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        Map<Long, V3PeopleContext> oldPeopleRecordMap = new HashMap<>();
        List<V3VendorContactContext> vendorContacts = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(vendorContacts)) {
            for(V3VendorContactContext vc : vendorContacts) {
                V3VendorContactContext existingPeople = V3RecordAPI.getRecord(FacilioConstants.ContextNames.VENDOR_CONTACT, vc.getId(), V3VendorContactContext.class);
                if(existingPeople!= null){
                    oldPeopleRecordMap.put(vc.getId(),existingPeople);
                }
            }
        }
        context.put(FacilioConstants.ContextNames.OLD_RECORD_MAP,oldPeopleRecordMap);
        return false;
    }
}
