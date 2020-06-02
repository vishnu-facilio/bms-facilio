package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.quotation.QuotationAssociatedTermsContext;
import com.facilio.bmsconsole.util.QuotationAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Context;

import java.util.List;

public class AssociateQuotationTermsCommand extends FacilioCommand {

    @Override
    public boolean executeCommand(Context context) throws Exception {
        // TODO Auto-generated method stub
        Long recordId = (Long) context.get(FacilioConstants.ContextNames.RECORD_ID);
        List<QuotationAssociatedTermsContext> terms = (List<QuotationAssociatedTermsContext>) context.get(FacilioConstants.ContextNames.QUOTATION_ASSOCIATED_TERMS);
        QuotationAPI.addQuotationTerms(recordId, terms);

        return false;

    }
}
