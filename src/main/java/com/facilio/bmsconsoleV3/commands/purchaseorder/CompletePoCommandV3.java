package com.facilio.bmsconsoleV3.commands.purchaseorder;


import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
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

public class CompletePoCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3PurchaseOrderContext> purchaseOrderContexts = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(purchaseOrderContexts) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("completePO")) {
            FacilioChain chain = TransactionChainFactoryV3.getPurchaseOrderCompleteChain();
            chain.getContext().put(FacilioConstants.ContextNames.PURCHASE_ORDERS,purchaseOrderContexts.get(0));
            chain.getContext().put(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS,purchaseOrderContexts.get(0).getLineItems());
            chain.execute();
        }
        return false;
    }

}
