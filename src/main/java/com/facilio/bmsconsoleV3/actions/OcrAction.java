package com.facilio.bmsconsoleV3.actions;

import java.util.List;
import java.util.Map;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.ocr.OCRTemplateOutputModuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OcrAction extends V3Action {
    Map<String, Object> parsedBill;
    int billingYear;
    long templateId;

    public String getParsedSampleBill() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getOcrParsedBillChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.Ocr.TEMPLATE_ID, templateId);

        chain.execute();

        this.setData("parsedBill", context.get(FacilioConstants.ContextNames.RESULT));

        return SUCCESS;
    }
    
    List<OCRTemplateOutputModuleContext> opModules; 
    
    
    public String addOrUpdateLineItems() throws Exception {
    	
    	
    	FacilioChain chain = TransactionChainFactoryV3.addOrUpdateOCRBillTemplateLineItems();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.Ocr.LINE_ITEMS, opModules);

        chain.execute();

        this.setData("result", context.get(FacilioConstants.Ocr.LINE_ITEM_RES));
    	
    	return SUCCESS;
    }
}
