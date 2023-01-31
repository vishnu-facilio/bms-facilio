package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class AddFailureClassFromResource extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(AddFailureClassFromResource.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        LOGGER.info("assigning failure classes form resources to wos");
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);
        for (V3WorkOrderContext wo : wos) {
            ResourceContext resource = wo.getResource();
            if (resource == null || resource.isDeleted()) {
                continue;
            }
            resource = ResourceAPI.getResource(resource.getId());
            wo.setFailureClass(resource.getFailureClass());
        }
        return false;
    }
}
