package com.facilio.bmsconsoleV3.commands.tenant;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3TenantContext;
import com.facilio.bmsconsoleV3.util.V3TenantsAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class LoadTenantLookUpsCommandV3  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3TenantContext> tenants = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(tenants)) {
            V3TenantsAPI.loadTenantLookups(tenants);
        }
        return false;
    }
}
