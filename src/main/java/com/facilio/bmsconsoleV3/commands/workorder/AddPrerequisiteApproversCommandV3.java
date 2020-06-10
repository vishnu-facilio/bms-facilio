package com.facilio.bmsconsoleV3.commands.workorder;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.SharingContext;
import com.facilio.bmsconsole.context.SingleSharingContext;
import com.facilio.bmsconsole.util.SharingAPI;
import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.ModuleFactory;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Map;

public class AddPrerequisiteApproversCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        String moduleName = (String) context.get(Constants.MODULE_NAME);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3WorkOrderContext> wos = recordMap.get(moduleName);

        if(CollectionUtils.isNotEmpty(wos)) {
            V3WorkOrderContext workOrder = wos.get(0);
            SharingContext<SingleSharingContext> prerequisiteApproversList = (SharingContext<SingleSharingContext>) context.get(FacilioConstants.ContextNames.PREREQUISITE_APPROVERS_LIST);
            if (prerequisiteApproversList != null && !prerequisiteApproversList.isEmpty()) {
                SharingAPI.addSharing(prerequisiteApproversList, workOrder.getId(), ModuleFactory.getPrerequisiteApproversModule());
            }
        }
        return false;
    }
}
