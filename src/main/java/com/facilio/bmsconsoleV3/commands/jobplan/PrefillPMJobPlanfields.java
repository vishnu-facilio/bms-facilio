package com.facilio.bmsconsoleV3.commands.jobplan;

import com.facilio.bmsconsole.context.PMJobPlan;
import com.facilio.command.FacilioCommand;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import java.util.List;
import java.util.Map;

public class PrefillPMJobPlanfields extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List<ModuleBaseWithCustomFields>> recordMap = Constants.getRecordMap(context);
        List<ModuleBaseWithCustomFields> jobPlans = recordMap.get(moduleName);
        for (ModuleBaseWithCustomFields obj : jobPlans) {
            PMJobPlan pmJobPlan = (PMJobPlan) obj;
            if (pmJobPlan.isPreRequisite()) {
                pmJobPlan.setName(pmJobPlan.getName() + "_" + pmJobPlan.getPmId() + "_" + "preReq" );
            } else {
                pmJobPlan.setName(pmJobPlan.getName() + "_" + pmJobPlan.getPmId());
            }
        }
        return false;
    }
}
