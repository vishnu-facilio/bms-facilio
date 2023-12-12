package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.bmsconsole.context.TermsAndConditionContext;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.bmsconsoleV3.context.quotation.QuotationAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.bmsconsoleV3.util.InvoiceAPI;
import com.facilio.bmsconsoleV3.util.QuotationAPI;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UpdateInvoiceTermsAndConditionCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
        //For updating quotation associated terms in related records
        String moduleName = Constants.getModuleName(context);
        Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        List<InvoiceContextV3> invoices = recordMap.get(moduleName);


        if (CollectionUtils.isNotEmpty(invoices)) {
            for (InvoiceContextV3 invoice : invoices) {

                //Default Terms And condition on clone and revise
                List<TermsAndConditionContext> defaultTerms = new ArrayList<>();
                if(invoice.getTermsAssociated() != null) {
                    List<InvoiceAssociatedTermsContext> invoiceTermsContextList = invoice.getTermsAssociated();
                    for (InvoiceAssociatedTermsContext associatedTermsContext : invoiceTermsContextList) {
                        defaultTerms.add(associatedTermsContext.getTerms());
                    }
                }

                List<TermsAndConditionContext> terms = InvoiceAPI.fetchInvoiceDefaultTerms();
                if(CollectionUtils.isNotEmpty(defaultTerms))
                {
                    List<InvoiceAssociatedTermsContext> invoiceAssociatedTerms = new ArrayList<>();
                    for (TermsAndConditionContext term : defaultTerms) {
                        InvoiceAssociatedTermsContext invoiceAssociatedTerm = new InvoiceAssociatedTermsContext();
                        invoiceAssociatedTerm.setInvoice(invoice);
                        invoiceAssociatedTerm.setTerms(term);
                        invoiceAssociatedTerms.add(invoiceAssociatedTerm);
                    }
                    InvoiceAPI.updateTermsAssociated(invoiceAssociatedTerms);
                }
                else if (CollectionUtils.isNotEmpty(terms)) {
                    List<InvoiceAssociatedTermsContext> invoiceAssociatedTerms = new ArrayList<>();
                    for (TermsAndConditionContext term : terms) {
                        InvoiceAssociatedTermsContext invoiceAssociatedTerm = new InvoiceAssociatedTermsContext();
                        invoiceAssociatedTerm.setInvoice(invoice);
                        invoiceAssociatedTerm.setTerms(term);
                        invoiceAssociatedTerms.add(invoiceAssociatedTerm);
                    }
                    InvoiceAPI.updateTermsAssociated(invoiceAssociatedTerms);
                }
            }

        }
        return false;
    }
}
