package com.facilio.bmsconsoleV3.commands.tenantcontact;

import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetOldTenantContactRecordMap extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        Map<Long, V3PeopleContext> oldPeopleRecordMap = new HashMap<>();
        List<V3TenantContactContext> tenantContacts = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(tenantContacts)) {
            for(V3TenantContactContext tc : tenantContacts) {
                V3TenantContactContext existingPeople = V3RecordAPI.getRecord(FacilioConstants.ContextNames.TENANT_CONTACT, tc.getId(), V3TenantContactContext.class);
                if(existingPeople!= null){
                    oldPeopleRecordMap.put(tc.getId(),existingPeople);
                }
            }
        }
        context.put(FacilioConstants.ContextNames.OLD_RECORD_MAP,oldPeopleRecordMap);
        return false;
    }
}
