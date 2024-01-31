package com.facilio.wmsv2.handler;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.ocr.ActualBillContext;
import com.facilio.bmsconsoleV3.context.ocr.BillTemplateContext;
import com.facilio.bmsconsoleV3.context.ocr.ParsedBillContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import com.facilio.ims.handler.ImsHandler;
import com.facilio.v3.util.V3Util;
import lombok.extern.log4j.Log4j;
import org.json.simple.JSONObject;

@Log4j
public class OcrActualBillHandler extends ImsHandler {
    @Override
    public void processMessage(Message message) {
        try {
            ActualBillContext actualBill = getActualBill(message);

            FacilioChain chain = TransactionChainFactoryV3.getOCRProcessResultChain();

            FacilioContext context = chain.getContext();
            
            context.put(FacilioConstants.Ocr.OCR_TEMPLATE, actualBill.getBillTemplate());
            context.put(FacilioConstants.Ocr.DOC_ID, actualBill.getBillFileId());
            context.put(FacilioConstants.Ocr.PARENT_RECORD_ID, actualBill.getId());
            context.put(FacilioConstants.Ocr.PARENT_MODULE_ID, actualBill.getModuleId());
            context.put(FacilioConstants.Ocr.BILLING_YEAR, actualBill.getBillMonth());

            chain.execute();
        } catch (Exception e) {
            LOGGER.error("Error while adding actual bill to parsed bill", e);
        }
    }
    private ActualBillContext getActualBill(Message message) throws Exception {
        JSONObject object = message.getContent();
        Long actualBillId =  Long.parseLong(String.valueOf(object.get(FacilioConstants.Ocr.ACTUAL_BILL_ID)));

        ActualBillContext actualBillContext =  (ActualBillContext) V3Util.getRecord(FacilioConstants.Ocr.ACTUAL_BILL, actualBillId, null);
        
        BillTemplateContext billTemplate =  (BillTemplateContext) V3Util.getRecord(FacilioConstants.Ocr.BILL_TEMPLATE, actualBillContext.getBillTemplate().getId(), null);
        
        actualBillContext.setBillTemplate(billTemplate);
        
        return actualBillContext;
    }
}
