package com.facilio.bmsconsoleV3.commands.visitorlogging;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GetTriggerForRecurringLogCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3VisitorLoggingContext> list = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(list)) {
            V3VisitorLoggingContext vLog = V3VisitorManagementAPI.getVisitorLoggingTriggers(list.get(0).getId(), null, true);
            recordMap.put(moduleName, Collections.singletonList(vLog));
            context.put(Constants.RECORD_MAP, recordMap);

        }
        return false;
    }
}
