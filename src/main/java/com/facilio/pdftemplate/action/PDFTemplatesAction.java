package com.facilio.pdftemplate.action;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.pdftemplate.context.PDFTemplate;
import com.facilio.util.FacilioUtil;
import com.facilio.v3.V3Action;
import com.facilio.v3.exception.ErrorCode;
import com.facilio.v3.exception.RESTException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class PDFTemplatesAction extends V3Action {

    private String moduleName;
    private Boolean fetchAll;
    private long templateId = -1;
    private long recordId = -1;
    private long appId = -1;
    private PDFTemplate pdfTemplate;

    public String addOrUpdate() throws Exception {
        FacilioUtil.throwIllegalArgumentException(StringUtils.isEmpty(moduleName), "moduleName is empty");

        FacilioChain chain = TransactionChainFactory.getAddOrUpdatePDFTemplateChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        context.put(FacilioConstants.ContextNames.PDF_TEMPLATE, pdfTemplate);
        chain.execute();

        setData(FacilioConstants.ContextNames.PDF_TEMPLATE, context.get(FacilioConstants.ContextNames.PDF_TEMPLATE));
        return SUCCESS;
    }

    public String list() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getPDFTemplatesListChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
        chain.execute();

        setData(FacilioConstants.ContextNames.PDF_TEMPLATES, context.get(FacilioConstants.ContextNames.PDF_TEMPLATES));
        return SUCCESS;
    }

    public String view() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getPDFTemplateChain();
        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.ID, getId());
        context.put(FacilioConstants.ContextNames.RECORD_ID, getRecordId());
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        chain.execute();

        setData(FacilioConstants.ContextNames.PDF_TEMPLATE, context.get(FacilioConstants.ContextNames.PDF_TEMPLATE));

        return SUCCESS;
    }

    public String delete() throws Exception {
        FacilioChain chain = TransactionChainFactory.getDeletePDFTemplateChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, getId());

        chain.execute();
        setData("message", "PDF Template is deleted successfully");
        return SUCCESS;
    }

    public String preview() throws Exception {

        validateParameters();


        FacilioChain chain = ReadOnlyChainFactory.getPreviewPDFTemplateChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, getId());
        context.put(FacilioConstants.ContextNames.RECORD_ID, getRecordId());
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        chain.execute();

        setData(FacilioConstants.ContextNames.PDF_TEMPLATE_HTML, context.get(FacilioConstants.ContextNames.PDF_TEMPLATE_HTML));
        return SUCCESS;
    }

    public String download() throws Exception {

        validateParameters();

        FacilioChain chain = ReadOnlyChainFactory.getDownloadPDFTemplateChain();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.ID, getId());
        context.put(FacilioConstants.ContextNames.RECORD_ID, getRecordId());
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        chain.execute();

        setData(FacilioConstants.ContextNames.FILE_ID, context.get(FacilioConstants.ContextNames.FILE_ID));
        return SUCCESS;
    }

    public String getTemplatesForDownloadFromPage() throws Exception {
        FacilioChain chain = ReadOnlyChainFactory.getTemplatesForDownloadFromPage();
        FacilioContext context = chain.getContext();
        context.put(FacilioConstants.ContextNames.RECORD_ID, getRecordId());
        context.put(FacilioConstants.ContextNames.MODULE_NAME, getModuleName());
        context.put(FacilioConstants.ContextNames.APP_ID, getAppId());
        chain.execute();

        setData(FacilioConstants.ContextNames.PDF_TEMPLATES, context.get(FacilioConstants.ContextNames.PDF_TEMPLATES));
        return SUCCESS;
    }

    private void validateParameters() throws RESTException {
        if (getId() <= 0 || getRecordId() <= 0) {
            throw new RESTException(ErrorCode.VALIDATION_ERROR, "Invalid templateId or recordId values.");
        }
    }
}
