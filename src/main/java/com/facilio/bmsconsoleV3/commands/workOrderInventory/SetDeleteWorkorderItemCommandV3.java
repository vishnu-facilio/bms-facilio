package com.facilio.bmsconsoleV3.commands.workOrderInventory;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsoleV3.context.inventory.V3WorkorderItemContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.sun.xml.bind.v2.runtime.reflect.opt.Const;
import org.apache.commons.chain.Context;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SetDeleteWorkorderItemCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        List<V3WorkorderItemContext> recordList = (List<V3WorkorderItemContext>) context.get("deletedRecords");
        List<Long> parentIdList = recordList.stream().map(record -> record.getParentId()).collect(Collectors.toList());
        context.put(FacilioConstants.ContextNames.WORKORDER_COST_TYPE, 1);
        context.put(FacilioConstants.ContextNames.RECORD_LIST,recordList);
        context.put(FacilioConstants.ContextNames.PARENT_ID_LIST, parentIdList);

        return false;
    }
}
