package com.facilio.bmsconsole.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsoleV3.context.quotation.QuotationAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
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

    public EMailTemplate getEmailTemplate() {
        return emailTemplate;
    }

    public void setEmailTemplate(EMailTemplate emailTemplate) {
        this.emailTemplate = emailTemplate;
    }

    private EMailTemplate emailTemplate;

    public List<File> getMailAttachments() {
        return mailAttachments;
    }

    public void setMailAttachments(List<File> mailAttachments) {
        this.mailAttachments = mailAttachments;
    }

    private List<File> mailAttachments;

    public QuotationContext getQuotation() {
        return quotation;
    }

    public void setQuotation(QuotationContext quotation) {
        this.quotation = quotation;
    }

    private QuotationContext quotation;

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    private String pdfUrl;

    public String associateTerms() throws Exception {

        FacilioChain chain = TransactionChainFactory.getAssociateQuotationTermsChain();
        chain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, recordId);
        chain.getContext().put(FacilioConstants.ContextNames.QUOTE_ASSOCIATED_TERMS, termsAssociated );
        chain.execute();
        setResult(FacilioConstants.ContextNames.QUOTE_ASSOCIATED_TERMS, chain.getContext().get(FacilioConstants.ContextNames.QUOTE_ASSOCIATED_TERMS));

        return SUCCESS;
    }
    public String manageTerms() throws Exception {

        FacilioChain chain = TransactionChainFactory.getManageTermsToQuotationChain();
        chain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, recordId);
        chain.getContext().put(FacilioConstants.ContextNames.QUOTE_ASSOCIATED_TERMS, termsAssociated );
        chain.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST,recordIds);
        chain.getContext().put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ContextNames.QUOTE_ASSOCIATED_TERMS);

        chain.execute();
        setResult(FacilioConstants.ContextNames.QUOTE_ASSOCIATED_TERMS, chain.getContext().get(FacilioConstants.ContextNames.QUOTE_ASSOCIATED_TERMS));

        return SUCCESS;
    }

    public String disAssociateTerms() throws Exception {

        FacilioChain chain = TransactionChainFactory.getDisAssociateQuotationTermsChain();
        chain.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST, recordIds );
        chain.execute();
        setResult(FacilioConstants.ContextNames.RECORD_ID_LIST, chain.getContext().get(FacilioConstants.ContextNames.RECORD_ID_LIST));

        return SUCCESS;
    }

    public String  sendMail() throws Exception {

        FacilioChain chain = TransactionChainFactory.getSendQuotationMailChain();
        chain.getContext().put(FacilioConstants.ContextNames.TEMPLATE, emailTemplate);
        chain.getContext().put(FacilioConstants.ContextNames.QUOTE_MAIL_ATTACHMENTS, mailAttachments);
        chain.getContext().put(FacilioConstants.ContextNames.QUOTE_PDF_URL, pdfUrl);
        chain.getContext().put(FacilioConstants.ContextNames.QUOTE, quotation);
        chain.execute();

        return SUCCESS;
    }


}
