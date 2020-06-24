package com.facilio.bmsconsoleV3.commands.tenantcontact;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3PeopleContext;
import com.facilio.bmsconsoleV3.context.V3TenantContactContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class CheckforPeopleDuplicationCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3PeopleContext> peopleList = recordMap.get(moduleName);

        if (CollectionUtils.isNotEmpty(peopleList)) {
            for (V3PeopleContext people : peopleList) {
                if(StringUtils.isNotEmpty(people.getEmail()) && V3PeopleAPI.checkForDuplicatePeople(people)) {
                    throw new IllegalArgumentException("People with the same email id already exists");
                }
                //setting tenant's site to all the contacts
                if(people instanceof V3TenantContactContext && ((V3TenantContactContext)people).getTenant() != null && ((V3TenantContactContext)people).getTenant().getId() > 0) {
                    V3TenantContext tenant = (V3TenantContext) V3RecordAPI.getRecord(FacilioConstants.ContextNames.TENANT, ((V3TenantContactContext)people).getTenant().getId(), V3TenantContext.class);
                    people.setSiteId(tenant.getSiteId());
                }
            }
        }
        return false;
    }
}
