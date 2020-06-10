package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.WorkorderHazardContext;
import com.facilio.bmsconsole.util.HazardsAPI;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GetworkorderHazardsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);
        if(CollectionUtils.isNotEmpty(wos)){
            V3WorkOrderContext workorder = wos.get(0);
            if(workorder != null && workorder.getId() > 0 && AccountUtil.isFeatureEnabled(AccountUtil.FeatureLicense.SAFETY_PLAN)) {
                List<WorkorderHazardContext> workorderHazards = HazardsAPI.fetchWorkorderAssociatedHazards(workorder.getId());
                if (CollectionUtils.isNotEmpty(workorderHazards)) {
                    List<Long> hazardIds = new ArrayList<Long>();
                    for (WorkorderHazardContext sfh : workorderHazards) {
                        hazardIds.add(sfh.getHazard().getId());
                    }
                    workorder.setHazardIds(hazardIds);
                }
            }
        }
        return false;
    }
}
