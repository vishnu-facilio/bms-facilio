package com.facilio.bmsconsoleV3.commands.inventoryrequest;


import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import com.facilio.command.FacilioCommand;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;

import com.facilio.constants.FacilioConstants;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class CopyToToolTransactionCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        context.put(FacilioConstants.ContextNames.RECORD_LIST, context.get(FacilioConstants.ContextNames.TOOL_TRANSACTIONS));


        return false;
    }

}
