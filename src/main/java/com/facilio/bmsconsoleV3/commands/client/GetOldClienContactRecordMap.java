package com.facilio.bmsconsoleV3.commands.client;

import com.facilio.bmsconsoleV3.context.V3ClientContactContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetOldClienContactRecordMap extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        Map<Long, V3ClientContactContext> oldPeopleRecordMap = new HashMap<>();
        List<V3ClientContactContext> clientContacts = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(clientContacts)) {
            for(V3ClientContactContext client : clientContacts) {
                V3ClientContactContext existingPeople = V3RecordAPI.getRecord(FacilioConstants.ContextNames.CLIENT_CONTACT, client.getId(), V3ClientContactContext.class);
                if(existingPeople!= null){
                    oldPeopleRecordMap.put(client.getId(),existingPeople);
                }
            }
        }
        context.put(FacilioConstants.ContextNames.OLD_RECORD_MAP,oldPeopleRecordMap);
        return false;
    }
}
