package com.facilio.bmsconsoleV3.commands.tool;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import com.facilio.bmsconsoleV3.context.inventory.V3ToolContext;
import com.facilio.bmsconsoleV3.context.purchaseorder.V3PurchaseOrderContext;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class StockOrUpdateToolsCommandV3  extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3ToolContext> toolContexts = recordMap.get(moduleName);

        if (CollectionUtils.isNotEmpty(toolContexts) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("updateToolAttributes")) {
            FacilioChain chain = TransactionChainFactoryV3.getUpdateIsUnderStockedChainV3();
            chain.getContext().put(FacilioConstants.ContextNames.TOOLS,toolContexts);
            chain.execute();
        } else{
            FacilioChain chain = TransactionChainFactoryV3.getBulkAddToolChainV3();
            chain.getContext().put(FacilioConstants.ContextNames.TOOLS,toolContexts);
            chain.execute();
        }
        return false;
    }
}
