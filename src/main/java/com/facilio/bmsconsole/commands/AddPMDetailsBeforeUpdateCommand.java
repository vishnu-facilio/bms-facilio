package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class AddPMDetailsBeforeUpdateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        List<ModuleBaseWithCustomFields> plannedmaintenanceList = recordMap.get("plannedmaintenance");

        for (ModuleBaseWithCustomFields record: plannedmaintenanceList) {
            PlannedMaintenance pm = (PlannedMaintenance) record;
            // update modifiedTime manually as it's not a system field.
            pm.setModifiedTime(System.currentTimeMillis());
        }

        return false;
    }
}
