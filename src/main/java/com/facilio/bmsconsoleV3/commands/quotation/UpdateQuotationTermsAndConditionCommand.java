package com.facilio.bmsconsoleV3.commands.quotation;


import com.facilio.bmsconsole.context.TermsAndConditionContext;
import com.facilio.command.FacilioCommand;
import com.facilio.bmsconsoleV3.context.quotation.QuotationAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateQuotationTermsAndConditionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        //For updating quotation associated terms in related records
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<QuotationContext> quotations = recordMap.get(moduleName);


        if (CollectionUtils.isNotEmpty(quotations)) {
            for (QuotationContext quoteContext : quotations) {
                List<TermsAndConditionContext> terms = QuotationAPI.fetchQuotationDefaultTerms();
                if (CollectionUtils.isNotEmpty(terms)) {
                    List<QuotationAssociatedTermsContext> quoteAssociatedTerms = new ArrayList<>();
                    for (TermsAndConditionContext term : terms) {
                        QuotationAssociatedTermsContext quoteAssociatedTerm = new QuotationAssociatedTermsContext();
                        quoteAssociatedTerm.setQuote(quoteContext);
                        quoteAssociatedTerm.setTerms(term);
                        quoteAssociatedTerms.add(quoteAssociatedTerm);
                    }
                    QuotationAPI.updateTermsAssociated(quoteAssociatedTerms);
                }
            }

        }
        return false;
    }
}
