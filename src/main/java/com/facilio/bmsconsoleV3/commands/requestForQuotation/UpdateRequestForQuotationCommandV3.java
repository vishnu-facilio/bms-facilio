package com.facilio.bmsconsoleV3.commands.requestForQuotation;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.requestforquotation.V3RequestForQuotationContext;
import com.facilio.chain.FacilioChain;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.List;
import java.util.Map;

public class UpdateRequestForQuotationCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        String moduleName = Constants.getModuleName(context);
        Map<String, Object> bodyParams = Constants.getBodyParams(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<V3RequestForQuotationContext> requestForQuotations = recordMap.get(moduleName);
        if (CollectionUtils.isNotEmpty(requestForQuotations) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("rfqFinalized") && (boolean) bodyParams.get("rfqFinalized")) {
            FacilioChain chain = TransactionChainFactoryV3.getCreateVendorQuotesChainV3();
            chain.getContext().put(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION,requestForQuotations.get(0));
            chain.getContext().put(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_LINE_ITEMS,requestForQuotations.get(0).getRequestForQuotationLineItems());
            chain.execute();
        }
        /*
         else if (CollectionUtils.isNotEmpty(requestForQuotations) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("createPo") && (boolean) bodyParams.get("createPo")) {
            FacilioChain chain = TransactionChainFactoryV3.getCreatePurchaseOrdersChainV3();
            chain.getContext().put(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION,requestForQuotations.get(0));
            chain.getContext().put(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_LINE_ITEMS,requestForQuotations.get(0).getRequestForQuotationLineItems());
            chain.getContext().put(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_CONTEXT,context);
            chain.execute();
            */
        else if (CollectionUtils.isNotEmpty(requestForQuotations) && MapUtils.isNotEmpty(bodyParams) && bodyParams.containsKey("awardQuotes") && (boolean) bodyParams.get("awardQuotes")) {
            FacilioChain chain = TransactionChainFactoryV3.getAwardVendorsChainV3();
            chain.getContext().put(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION,requestForQuotations.get(0));
            chain.getContext().put(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_LINE_ITEMS,requestForQuotations.get(0).getRequestForQuotationLineItems());
            chain.getContext().put(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION_CONTEXT,context);
            chain.execute();
        }
        return false;
    }
}
