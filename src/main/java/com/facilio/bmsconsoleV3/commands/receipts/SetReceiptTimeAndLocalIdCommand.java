package com.facilio.bmsconsoleV3.commands.receipts;

import com.facilio.bmsconsoleV3.context.purchaseorder.V3ReceiptContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class SetReceiptTimeAndLocalIdCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        //ReceiptTime
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3ReceiptContext> receipts = recordMap.get(moduleName);
        if (!CollectionUtils.isEmpty(receipts) && StringUtils.isNotEmpty(moduleName)) {
            for (V3ReceiptContext receiptContext : receipts) {
                receiptContext.setReceiptTime(System.currentTimeMillis());
            }
        }
        //Local Id
        context.put(FacilioConstants.ContextNames.SET_LOCAL_MODULE_ID, true);
        return false;
    }
}
