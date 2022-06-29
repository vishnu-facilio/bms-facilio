package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.bmsconsole.workflow.rule.ApprovalState;
import com.facilio.bmsconsoleV3.context.V3TicketContext;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class PMBeforeCreateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        List<ModuleBaseWithCustomFields> plannedmaintenanceList = recordMap.get("plannedmaintenance");

        for (ModuleBaseWithCustomFields record: plannedmaintenanceList) {
            PlannedMaintenance pm = (PlannedMaintenance) record;
            pm.setSourceType(V3TicketContext.SourceType.PM_TEMPLATE.getIntVal());
            pm.setApprovalState(ApprovalState.YET_TO_BE_REQUESTED.getValue());
        }
        return false;
    }
}
