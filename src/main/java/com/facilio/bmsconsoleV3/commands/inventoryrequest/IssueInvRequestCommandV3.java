package com.facilio.bmsconsoleV3.commands.inventoryrequest;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.inventory.V3InventoryRequestContext;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;
@Deprecated
public class IssueInvRequestCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3InventoryRequestContext> inventoryRequestContext = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(inventoryRequestContext)) {
            for (V3InventoryRequestContext inventoryRequestContexts : inventoryRequestContext) {
                Map<String, Object> bodyParams = Constants.getBodyParams(context);
                if (MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("issue")) {
                    inventoryRequestContexts.setIsIssued(true);
                    FacilioChain chain = TransactionChainFactoryV3.getIssueInventoryRequestChainV3();
                    chain.getContext().put(FacilioConstants.ContextNames.INVENTORY_REQUEST,inventoryRequestContext);
                    chain.execute();
                }
            }
        }
        return false;
    }
}
