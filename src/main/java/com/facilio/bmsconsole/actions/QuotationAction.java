package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.quotation.QuotationAssociatedTermsContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;

import java.util.List;

public class QuotationAction extends FacilioAction {

    private static final long serialVersionUID = 1L;


    public List<QuotationAssociatedTermsContext> getTermsAssociated() {
        return termsAssociated;
    }

    public void setTermsAssociated(List<QuotationAssociatedTermsContext> termsAssociated) {
        this.termsAssociated = termsAssociated;
    }

    private List<QuotationAssociatedTermsContext> termsAssociated;

    public long getRecordId() {
        return recordId;
    }

    public void setRecordId(long recordId) {
        this.recordId = recordId;
    }

    private long recordId = -1;

    public List<Long> getRecordIds() {
        return recordIds;
    }

    public void setRecordIds(List<Long> recordIds) {
        this.recordIds = recordIds;
    }

    private List<Long> recordIds;

    public String associateTerms() throws Exception {

        FacilioChain chain = TransactionChainFactory.getAssociateQuotationTermsChain();
        chain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, recordId);
        chain.getContext().put(FacilioConstants.ContextNames.QUOTATION_ASSOCIATED_TERMS, termsAssociated );
        chain.execute();
        setResult(FacilioConstants.ContextNames.QUOTATION_ASSOCIATED_TERMS, chain.getContext().get(FacilioConstants.ContextNames.QUOTATION_ASSOCIATED_TERMS));

        return SUCCESS;
    }

    public String disAssociateTerms() throws Exception {

        FacilioChain chain = TransactionChainFactory.getDisAssociateQuotationTermsChain();
        chain.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds );
        chain.execute();
        setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, chain.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));

        return SUCCESS;
    }


}
