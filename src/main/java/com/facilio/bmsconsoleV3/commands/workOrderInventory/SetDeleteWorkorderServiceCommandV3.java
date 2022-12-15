package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.WorkOrderServiceContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3WorkOrderServiceContext;
import com.facilio.bmsconsoleV3.context.V3WorkorderToolsContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SetDeleteWorkorderServiceCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<V3WorkOrderServiceContext> recordList = (List<V3WorkOrderServiceContext>) context.get("deletedRecords");
        List<Long> parentIdList = recordList.stream().map(record -> record.getParentId()).collect(Collectors.toList());
        context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 4);
        context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, parentIdList);

        return false;
    }
}
