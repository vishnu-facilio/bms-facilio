package com.facilio.bmsconsoleV3.commands.item;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.asset.V3ItemTransactionsContext;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class UpdateItemTransactionsCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3ItemTransactionsContext> itemTransactions = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(itemTransactions) && MapUtils.isNotEmpty(bodyParams) && ((bodyParams.containsKey("issue") && (boolean) bodyParams.get("issue")) || (bodyParams.containsKey("return") && (boolean) bodyParams.get("return")))) {
            FacilioChain chain = TransactionChainFactoryV3.getItemTransactionsAfterSaveChainV3();
            chain.getContext().put(FacilioConstants.ContextNames.RECORD_LIST,context.get(FacilioConstants.ContextNames.RECORD_LIST));
            chain.getContext().put(FacilioConstants.ContextNames.PARENT_ID,context.get(FacilioConstants.ContextNames.PARENT_ID));
            chain.getContext().put(FacilioConstants.ContextNames.ITEM_ID,context.get(FacilioConstants.ContextNames.ITEM_ID));
            chain.getContext().put(FacilioConstants.ContextNames.ITEM_IDS,context.get(FacilioConstants.ContextNames.ITEM_IDS));
            chain.getContext().put(FacilioConstants.ContextNames.ITEM_TYPES_ID,context.get(FacilioConstants.ContextNames.ITEM_TYPES_ID));
            chain.getContext().put(FacilioConstants.ContextNames.ITEM_TYPES_IDS,context.get(FacilioConstants.ContextNames.ITEM_TYPES_IDS));
            chain.getContext().put(FacilioConstants.ContextNames.TRANSACTION_STATE,context.get(FacilioConstants.ContextNames.TRANSACTION_STATE));
            chain.execute();
        }
        else if(CollectionUtils.isNotEmpty(itemTransactions) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("adjustQuantity") && (boolean) bodyParams.get("adjustQuantity")) {
            FacilioChain chain = TransactionChainFactoryV3.getAdjustmentItemTransactionsChainV3();
            chain.getContext().put(FacilioConstants.ContextNames.RECORD_LIST,context.get(FacilioConstants.ContextNames.RECORD_LIST));
            chain.getContext().put(FacilioConstants.ContextNames.PARENT_ID,context.get(FacilioConstants.ContextNames.PARENT_ID));
            chain.getContext().put(FacilioConstants.ContextNames.ITEM_ID,context.get(FacilioConstants.ContextNames.ITEM_ID));
            chain.getContext().put(FacilioConstants.ContextNames.ITEM_IDS,context.get(FacilioConstants.ContextNames.ITEM_IDS));
            chain.getContext().put(FacilioConstants.ContextNames.ITEM_TYPES_ID,context.get(FacilioConstants.ContextNames.ITEM_TYPES_ID));
            chain.getContext().put(FacilioConstants.ContextNames.ITEM_TYPES_IDS,context.get(FacilioConstants.ContextNames.ITEM_TYPES_IDS));
            chain.getContext().put(FacilioConstants.ContextNames.TRANSACTION_STATE,context.get(FacilioConstants.ContextNames.TRANSACTION_STATE));
            chain.getContext().put(FacilioConstants.ContextNames.ITEMS,context.get(FacilioConstants.ContextNames.ITEMS));
            chain.execute();
        }
            return false;
    }
}
