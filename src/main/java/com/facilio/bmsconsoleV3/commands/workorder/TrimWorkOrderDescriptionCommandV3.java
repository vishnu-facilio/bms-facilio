package com.facilio.bmsconsoleV3.commands.workorder;

import com.beust.jcommander.Strings;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class TrimWorkOrderDescriptionCommandV3 extends FacilioCommand {

    private static final Logger LOGGER = Logger.getLogger(TrimWorkOrderDescriptionCommandV3.class.getName());
    public static final int DESCRIPTION_LENGTH = 2000;

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(wos)) {
            for (V3WorkOrderContext wo : wos) {
                if (!Strings.isStringEmpty(wo.getDescription()) && wo.getDescription().length() > DESCRIPTION_LENGTH) {
                    wo.setDescription(wo.getDescription().substring(0, DESCRIPTION_LENGTH));
                }
            }
        }
        return false;
    }
}
