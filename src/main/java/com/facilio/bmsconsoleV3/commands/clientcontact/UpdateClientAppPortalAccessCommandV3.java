package com.facilio.bmsconsoleV3.commands.clientcontact;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.ClientContactContext;
import com.facilio.bmsconsole.util.PeopleAPI;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsoleV3.context.V3ClientContactContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class UpdateClientAppPortalAccessCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3ClientContactContext> clientContacts = recordMap.get(moduleName);

        Map<Long, List<UpdateChangeSet>> changeSet = Constants.getModuleChangeSets(context);
        if(CollectionUtils.isNotEmpty(clientContacts) && MapUtils.isNotEmpty(changeSet)) {
            for(V3ClientContactContext tc : clientContacts) {
                List<UpdateChangeSet> changes = changeSet.get(tc.getId());
                if(CollectionUtils.isNotEmpty(changes) && RecordAPI.checkChangeSet(changes, "isClientPortalAccess", FacilioConstants.ContextNames.CLIENT_CONTACT)) {
                    V3PeopleAPI.updateClientContactAppPortalAccess(tc, FacilioConstants.ApplicationLinkNames.CLIENT_PORTAL_APP);
                }
            }
        }
        return false;
    }
}
