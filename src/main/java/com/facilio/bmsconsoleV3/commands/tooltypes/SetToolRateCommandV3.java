package com.facilio.bmsconsoleV3.commands.tooltypes;

import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class SetToolRateCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3ToolContext> list = recordMap.get(moduleName);
        for (V3ToolContext tool:list) {
            tool.setRate(tool.getToolType().getSellingPrice());
        }
        return false;
    }
}
