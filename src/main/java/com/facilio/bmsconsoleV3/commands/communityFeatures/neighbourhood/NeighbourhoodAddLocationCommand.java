package com.facilio.bmsconsoleV3.commands.communityFeatures.neighbourhood;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.util.TenantsAPI;
import com.facilio.bmsconsoleV3.context.tenantEngagement.NeighbourhoodContext;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class NeighbourhoodAddLocationCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<NeighbourhoodContext> list = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(list)) {
            for (NeighbourhoodContext record : list) {
                if (record.getLocation() != null) {
                    TenantsAPI.addAddress(record.getTitle() + "_location" , record.getLocation());
                }
            }
        }
        return false;
    }
}
