package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.util.V3TicketAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class LoadWorkorderRelatedModulesCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(wos)) {
            V3TicketAPI.loadRelatedModules(wos.get(0));
            V3TicketAPI.loadPmV1IntoWorkOrderObject(wos);
        }

        return false;
    }
}
