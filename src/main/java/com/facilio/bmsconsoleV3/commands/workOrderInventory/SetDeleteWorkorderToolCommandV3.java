package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.V3WorkorderToolsContext;
import com.facilio.bmsconsoleV3.context.inventory.V3WorkorderItemContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SetDeleteWorkorderToolCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<V3WorkorderToolsContext> recordList = (List<V3WorkorderToolsContext>) context.get("deletedRecords");
        List<Long> parentIdList = recordList.stream().map(record -> record.getParentId()).collect(Collectors.toList());
        context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, parentIdList);
        context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 2);
        context.put(FacilioConstants.ContextNames.RECORD_LIST, recordList);

        return false;
    }
}
