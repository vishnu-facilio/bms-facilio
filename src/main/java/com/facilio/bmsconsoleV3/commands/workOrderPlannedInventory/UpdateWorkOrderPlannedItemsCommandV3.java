package com.facilio.bmsconsoleV3.commands.workOrderPlannedInventory;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.bmsconsoleV3.context.workOrderPlannedInventory.WorkOrderPlannedItemsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class UpdateWorkOrderPlannedItemsCommandV3  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<WorkOrderPlannedItemsContext> workOrderPlannedItems = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(workOrderPlannedItems) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("reserve") && (boolean) bodyParams.get("reserve")) {
            FacilioChain chain = TransactionChainFactoryV3.getReserveItemsChainV3();
            chain.getContext().put(FacilioConstants.ContextNames.WO_PLANNED_ITEMS,workOrderPlannedItems);
            chain.execute();
        }
        return false;
    }
}
