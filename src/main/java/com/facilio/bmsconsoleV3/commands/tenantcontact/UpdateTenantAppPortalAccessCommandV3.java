package com.facilio.bmsconsoleV3.commands.tenantcontact;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.UpdateChangeSet;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class UpdateTenantAppPortalAccessCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TenantContactContext> tenantContacts = recordMap.get(moduleName);

        Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>) context.get(FacilioConstants.ContextNames.CHANGE_SET);
        if(CollectionUtils.isNotEmpty(tenantContacts) && MapUtils.isNotEmpty(changeSet)) {
            for(V3TenantContactContext tc : tenantContacts) {
                List<UpdateChangeSet> changes = changeSet.get(tc.getId());
                if(CollectionUtils.isNotEmpty(changes) && V3RecordAPI.checkChangeSet(changes, "isTenantPortalAccess", FacilioConstants.ContextNames.TENANT_CONTACT)) {
                    V3PeopleAPI.updateTenantContactAppPortalAccess(tc, FacilioConstants.ApplicationLinkNames.TENANT_PORTAL_APP);
                }
                if(CollectionUtils.isNotEmpty(changes) && V3RecordAPI.checkChangeSet(changes, "isOccupantPortalAccess", FacilioConstants.ContextNames.TENANT_CONTACT)) {
                    V3PeopleAPI.updatePeoplePortalAccess(tc, FacilioConstants.ApplicationLinkNames.OCCUPANT_PORTAL_APP);
                }
            }
        }
        return false;
    }
}
