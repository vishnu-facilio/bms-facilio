package com.facilio.bmsconsoleV3.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsoleV3.context.ocr.ActualBillContext;
import com.facilio.bmsconsoleV3.context.ocr.ParsedBillContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import com.facilio.ims.endpoint.Messenger;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.Map;

public class PushActualBillToWMSHandlerCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        Map<String, List<ActualBillContext>> actualBillMap = (Map<String, List<ActualBillContext>>) context.get(FacilioConstants.ContextNames.RECORD_MAP);

        List<ActualBillContext> actualBills = actualBillMap.get(FacilioConstants.Ocr.ACTUAL_BILL);

        for(ActualBillContext actualBill : actualBills) {

            long orgId = AccountUtil.getCurrentOrg().getOrgId();
            JSONObject message = new JSONObject();
            message.put("orgId", orgId);
            message.put(FacilioConstants.Ocr.ACTUAL_BILL_ID, actualBill.getId());

            Messenger.getMessenger().sendMessage(new Message()
                    .setKey("ocrActualBillHandler/" + actualBill.getId())
                    .setOrgId(orgId)
                    .setContent(message)
            );
        }

        return false;
    }
}
