package com.facilio.bmsconsoleV3.commands.tenant;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3ContactsContext;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3ContactsAPI;
import com.facilio.bmsconsoleV3.util.V3PeopleAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class FetchCurrentUserTenantCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Boolean fetchCurrentUserTenant = false;
        Map<String, List<Object>> queryParams = (Map<String, List<Object>>)context.get(Constants.QUERY_PARAMS);
        //fetch logged in user tenant details(used in tenant portal)
        if(MapUtils.isNotEmpty(queryParams) && queryParams.containsKey("fetchCurrentTenant") && AccountUtil.getCurrentUser().getAppType() == 2) {
            fetchCurrentUserTenant = true;
        }
        if(fetchCurrentUserTenant) {
            long currentUserId = AccountUtil.getCurrentUser().getOuid();
                V3TenantContext tenant = V3PeopleAPI.getTenantForUser(currentUserId);
                context.put(Constants.RECORD_ID, tenant.getId());


        }
        return false;
    }
}
