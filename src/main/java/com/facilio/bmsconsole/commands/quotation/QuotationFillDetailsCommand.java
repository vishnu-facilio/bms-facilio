package com.facilio.bmsconsole.commands.quotation;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.quotation.QuotationContext;
import com.facilio.bmsconsole.util.QuotationAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import com.facilio.v3.util.CommandUtil;
import org.apache.commons.chain.Context;

public class QuotationFillDetailsCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        long id = (long) context.get(Constants.RECORD_ID);
        QuotationContext quotation = (QuotationContext) CommandUtil.getModuleData(context, FacilioConstants.ContextNames.QUOTATION, id);
        QuotationAPI.setLineItems(quotation);
        QuotationAPI.setQuotationAssociatedTerms(quotation);

        return false;
    }
}
