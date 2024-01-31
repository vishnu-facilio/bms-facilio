package com.facilio.wmsv2.handler;

import org.json.simple.JSONObject;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fms.message.Message;
import com.facilio.ims.handler.ImsHandler;
import com.facilio.v3.util.V3Util;

import lombok.extern.log4j.Log4j;

@Log4j
public class OCRTemplateBillParserHandler extends ImsHandler {
    @Override
    public void processMessage(Message message) {
        try {
            JSONObject object = message.getContent();
            Long templateId =  Long.parseLong(String.valueOf(object.get(FacilioConstants.Ocr.TEMPLATE_ID)));

            OCRTemplateContext ocrTemplate = (OCRTemplateContext) V3Util.getRecord(FacilioConstants.Ocr.OCR_TEMPLATE, templateId, null);

            FacilioChain chain = TransactionChainFactoryV3.getOCRTemplateBillParserChain();

            FacilioContext context = chain.getContext();
            context.put(FacilioConstants.Ocr.OCR_TEMPLATE, ocrTemplate);

            chain.execute();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

}
