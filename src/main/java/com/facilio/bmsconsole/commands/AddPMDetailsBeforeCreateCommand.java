package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.PlannedMaintenance;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class AddPMDetailsBeforeCreateCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        List<ModuleBaseWithCustomFields> plannedmaintenanceList = recordMap.get("plannedmaintenance");

        for (ModuleBaseWithCustomFields record: plannedmaintenanceList) {
            PlannedMaintenance pm = (PlannedMaintenance) record;
            // update createdBy manually as it's not a system field.
            pm.setCreatedBy(AccountUtil.getCurrentUser());
            // update modifiedTime manually as it's not a system field.
            pm.setModifiedTime(System.currentTimeMillis());

            // Adding site ID if there's only one site. Add to support Global Site Filter.
            if(pm.getSites() != null && pm.getSites().size() == 1){
                pm._setSiteId(pm.getSites().get(0).getId());
            } else {
                pm._setSiteId(null);
            }
        }

        return false;
    }
}
