package com.facilio.bmsconsoleV3.actions;

import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceAssociatedTermsContext;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceLineItemsContext;
import com.facilio.bmsconsoleV3.context.jobplan.JobPlanContext;
import com.facilio.bmsconsoleV3.util.InvoiceAPI;
import com.facilio.bmsconsoleV3.util.JobPlanAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.V3Action;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvoiceAction extends V3Action {
    public long getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(long invoiceId) {
        this.invoiceId = invoiceId;
    }

    public List<Long> getInvoiceIds() {
        return invoiceIds;
    }

    public void setInvoiceIds(List<Long> invoiceIds) {
        this.invoiceIds = invoiceIds;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public long getInvoiceVersion() {
        return invoiceVersion;
    }

    public void setInvoiceVersion(long invoiceVersion) {
        this.invoiceVersion = invoiceVersion;
    }

    private long invoiceId;

    private List<Long> invoiceIds;
    private long groupId;

    private long invoiceVersion;

    public List<InvoiceAssociatedTermsContext> getTermsAssociated() {
        return termsAssociated;
    }

    public void setTermsAssociated(List<InvoiceAssociatedTermsContext> termsAssociated) {
        this.termsAssociated = termsAssociated;
    }

    private List<InvoiceAssociatedTermsContext> termsAssociated;

    public Integer getInvoiceType() {
        return invoiceType;
    }

    public void setInvoiceType(Integer invoiceType) {
        this.invoiceType = invoiceType;
    }

    private Integer invoiceType;


    public Integer getRecordId() {
        return recordId;
    }

    public void setRecordId(Integer recordId) {
        this.recordId = recordId;
    }

    private Integer recordId;

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

    public InvoiceContextV3 getInvoice() {
        return invoice;
    }

    public void setInvoice(InvoiceContextV3 invoice) {
        this.invoice = invoice;
    }

    private InvoiceContextV3 invoice;

    public String getPdfUrl() {
        return pdfUrl;
    }

    public void setPdfUrl(String pdfUrl) {
        this.pdfUrl = pdfUrl;
    }

    private String pdfUrl;





    public String reviseInvoice() throws Exception{
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.INVOICE.INVOICE_ID,invoiceId);
        context.put(FacilioConstants.INVOICE.INVOICE_STATUS, InvoiceContextV3.InvoiceStatus.DRAFT);
        FacilioChain chain = TransactionChainFactoryV3.reviseInvoice();
        chain.setContext(context);
        chain.execute();
        Map<String ,Object> result = new HashMap<>();
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap != null && recordMap.containsKey(FacilioConstants.INVOICE.INVOICE_LIST)){
            List<InvoiceContextV3> invoiceContextList = (List<InvoiceContextV3>) recordMap.get(FacilioConstants.INVOICE.INVOICE_LIST);
            if(invoiceContextList != null && !invoiceContextList.isEmpty()){
                result.put(FacilioConstants.INVOICE.INVOICE_ID,invoiceContextList.get(0).getId());
                result.put("groupId",invoiceContextList.get(0).getGroup().getId());
                result.put("invoiceVersion",invoiceContextList.get(0).getInvoiceVersion());
            }
        }
        setData("result",result);
        return SUCCESS;
    }
    public String getInvoiceGroupAndVersion() throws Exception{
        long id = invoiceId;
        Map<String,Object> recordMap = InvoiceAPI.getInvoiceGroupAndVersion(invoiceId);
        setData("result",recordMap);
        return SUCCESS;
    }
    public String getInvoiceIdFromGroupAndVersion() throws Exception{
        long group = groupId;
        long version = invoiceVersion;
        long invoiceId = InvoiceAPI.getInvoiceIdFromGroupAndVersion(group,version);
        setData("result",invoiceId);
        return SUCCESS;
    }
    public String cloneInvoice() throws Exception{
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.INVOICE.INVOICE_ID,invoiceId);
        context.put(FacilioConstants.INVOICE.INVOICE_STATUS, InvoiceContextV3.InvoiceStatus.DRAFT);
        context.put(FacilioConstants.INVOICE.IS_CLONING,true);
        FacilioChain chain = TransactionChainFactoryV3.cloneInvoiceChain();
        chain.setContext(context);
        chain.execute();
        Map<String ,Object> result = new HashMap<>();
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap != null && recordMap.containsKey(FacilioConstants.INVOICE.INVOICE_LIST)){
            List<JobPlanContext> jobPlanContextList = (List<JobPlanContext>) recordMap.get(FacilioConstants.INVOICE.INVOICE_LIST);
            if(jobPlanContextList != null && !jobPlanContextList.isEmpty()){
                result.put(FacilioConstants.INVOICE.INVOICE_LIST,jobPlanContextList.get(0));
            }
        }
        setData("result",result);
        return SUCCESS;
    }

    public String fetchVersionHistory() throws Exception{
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.INVOICE.INVOICE_ID,invoiceId);
        FacilioChain chain = TransactionChainFactoryV3.fetchInvoiceVersionHistoryChain();
        chain.setContext(context);
        chain.execute();
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        List<InvoiceContextV3> jobPlanContextList = (List<InvoiceContextV3>) recordMap.get(FacilioConstants.INVOICE.INVOICE_LIST);
        setData("result",jobPlanContextList);
        return SUCCESS;
    }

    public String associateTerms() throws Exception {

        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID, invoiceId );
        context.put(FacilioConstants.ContextNames.INVOICE_ASSOCIATED_TERMS, termsAssociated );

        FacilioChain chain = TransactionChainFactoryV3.associateTermsInvoiceChain();
        chain.execute(context);

        setData(FacilioConstants.ContextNames.INVOICE_ASSOCIATED_TERMS, context.get(FacilioConstants.ContextNames.INVOICE_ASSOCIATED_TERMS));

        return SUCCESS;
    }

    public String convertInvoice() throws Exception{
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.INVOICE.INVOICE_ID,invoiceId);
        context.put(FacilioConstants.ContextNames.TYPE,invoiceType);
        context.put(FacilioConstants.INVOICE.INVOICE_STATUS, InvoiceContextV3.InvoiceStatus.DRAFT);
        context.put(FacilioConstants.INVOICE.IS_CLONING,true);
        context.put(FacilioConstants.INVOICE.IS_CONVERSION,true);
        FacilioChain chain = TransactionChainFactoryV3.convertInvoiceTypeChain();
        chain.setContext(context);
        chain.execute();
        Map<String,Object> recordMap = (Map<String, Object>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
        if(recordMap != null && recordMap.containsKey(FacilioConstants.INVOICE.INVOICE_LIST)){
            List<InvoiceContextV3> invoiceContextList = (List<InvoiceContextV3>) recordMap.get(FacilioConstants.INVOICE.INVOICE_LIST);
            if(invoiceContextList != null && !invoiceContextList.isEmpty()){
                setData("invoice",invoiceContextList.get(0));
            }
        }
        setData("form",context.get(FacilioConstants.ContextNames.FORM));
        return SUCCESS;
    }

    public String convertPOtoInvoice() throws Exception{
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID,recordId);
        FacilioChain chain = TransactionChainFactoryV3.convertPOtoInvoiceChain();
        chain.setContext(context);
        chain.execute();
        setData("invoice",context.get(FacilioConstants.ContextNames.RECORD));
        setData("form",context.get(FacilioConstants.ContextNames.FORM));
        return SUCCESS;
    }

    public String convertQuoteToInvoice() throws Exception{
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID,recordId);
        FacilioChain chain = TransactionChainFactoryV3.convertQuoteToInvoiceChain();
        chain.setContext(context);
        chain.execute();
        setData("invoice",context.get(FacilioConstants.ContextNames.RECORD));
        setData("form",context.get(FacilioConstants.ContextNames.FORM));
        return SUCCESS;
    }

    public String convertWorkOrderToInvoice() throws Exception{
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID,recordId);
        context.put(FacilioConstants.ContextNames.TYPE,invoiceType);
        FacilioChain chain = TransactionChainFactoryV3.convertWorkOrderToInvoiceChain();
        chain.setContext(context);
        chain.execute();
        setData("invoice",context.get(FacilioConstants.ContextNames.RECORD));
        setData("form",context.get(FacilioConstants.ContextNames.FORM));
        return SUCCESS;
    }
    public String  sendMail() throws Exception {

        FacilioChain chain = TransactionChainFactoryV3.getSendInvoiceMailChain();
        chain.getContext().put(FacilioConstants.ContextNames.TEMPLATE, emailTemplate);
        chain.getContext().put(FacilioConstants.ContextNames.INVOICE_MAIL_ATTACHMENTS, mailAttachments);
        chain.getContext().put(FacilioConstants.ContextNames.INVOICE_PDF_URL, pdfUrl);
        chain.getContext().put(FacilioConstants.ContextNames.INVOICE, invoice);
        chain.execute();

        return SUCCESS;
    }

}
