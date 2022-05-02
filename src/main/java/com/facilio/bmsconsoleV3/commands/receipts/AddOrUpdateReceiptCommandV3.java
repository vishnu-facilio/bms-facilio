package com.facilio.bmsconsoleV3.commands.receipts;

import com.facilio.bmsconsoleV3.context.purchaseorder.V3ReceiptContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class AddOrUpdateReceiptCommandV3 extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3ReceiptContext> receipts = recordMap.get(moduleName);
        Set<Long> receivableIds = new HashSet<>();
        Set<Long> lineitemIds = new HashSet<>();
        if (!CollectionUtils.isEmpty(receipts) && StringUtils.isNotEmpty(moduleName)) {
            for (V3ReceiptContext receiptContext : receipts) {
                receivableIds.add(receiptContext.getReceivableId());
                lineitemIds.add(receiptContext.getLineItem().getId());
            }
        }
        context.put(FacilioConstants.ContextNames.RECEIVABLE_ID, receivableIds);
        context.put(FacilioConstants.ContextNames.PURCHASE_ORDER_LINE_ITEMS_ID, lineitemIds);
        return false;
    }
}