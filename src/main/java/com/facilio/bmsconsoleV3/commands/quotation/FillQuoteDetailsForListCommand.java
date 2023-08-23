package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public class FillQuoteDetailsForListCommand extends FacilioCommand {
    private static final Logger LOGGER = LogManager.getLogger(FillQuoteDetailsForListCommand.class.getName());

    @Override
    public boolean executeCommand(Context context) throws Exception {
        List<QuotationContext> Quotations = Constants.getRecordListFromContext((FacilioContext) context, FacilioConstants.ContextNames.QUOTE);

        if (CollectionUtils.isEmpty(Quotations)){
            return false;
        }

        Quotations.forEach(quotation -> {
            // handle

            if (quotation != null) {
                try {
                    QuotationAPI.setLineItems(quotation);
                    QuotationAPI.setQuotationAssociatedTerms(quotation);
                    QuotationAPI.setTaxSplitUp(quotation, quotation.getLineItems());
                }
                catch (Exception e) {
                    LOGGER.error("Error while set the quote data ", e);
                }
            }

        });



        return false;
    }
}
