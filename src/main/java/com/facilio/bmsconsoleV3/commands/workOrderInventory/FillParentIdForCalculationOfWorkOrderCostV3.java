package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.bmsconsoleV3.context.V3WorkOrderContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderCostContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FillParentIdForCalculationOfWorkOrderCostV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        HashMap<String, ArrayList<V3WorkorderCostContext>> workOrderCostRecordMap = (HashMap<String, ArrayList<V3WorkorderCostContext>>) context.getOrDefault("recordMap", new HashMap<String, ArrayList<V3WorkorderCostContext>>());
        if(workOrderCostRecordMap == null){
            return false;
        }

        List<V3WorkorderCostContext> workorderCostContextList = workOrderCostRecordMap.get(FacilioConstants.ContextNames.WORKORDER_COST);
        if(CollectionUtils.isEmpty(workorderCostContextList)){
            return false;
        }

        List<Long> parentIds = new ArrayList<>();
        for(V3WorkorderCostContext workorderCostContext: workorderCostContextList){
            V3WorkOrderContext workOrderContext = workorderCostContext.getParentId();
            if(workOrderContext != null && workOrderContext.getId() > 0) {
                parentIds.add(workOrderContext.getId());
            }
        }
        if(CollectionUtils.isNotEmpty(parentIds)) {
            context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, parentIds);
        }
        return false;
    }
}
