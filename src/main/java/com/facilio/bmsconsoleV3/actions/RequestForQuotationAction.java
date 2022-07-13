package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;
import org.json.simple.JSONObject;

import java.util.List;

public class RequestForQuotationAction extends V3Action {
    private static final long serialVersionUID = 1L;

    private List<Long> recordIds;
    public List<Long> getRecordIds() {
        return recordIds;
    }
    public void setRecordIds(List<Long> recordIds) {
        this.recordIds = recordIds;
    }
    public String convertPrToRfq() throws Exception {
        FacilioChain chain = TransactionChainFactoryV3.getConvertPrToRfqChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds);
        chain.execute();
        setData(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION, context.get(FacilioConstants.ContextNames.REQUEST_FOR_QUOTATION));
        return V3Action.SUCCESS;
    }

}
