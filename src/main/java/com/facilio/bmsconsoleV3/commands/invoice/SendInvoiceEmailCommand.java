package com.facilio.bmsconsoleV3.commands.invoice;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.activity.InvoiceActivityType;
import com.facilio.bmsconsole.activity.QuotationActivityType;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsoleV3.context.invoice.InvoiceContextV3;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.chain.FacilioContext;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.modules.FieldUtil;
import com.facilio.pdf.PdfUtil;
import com.facilio.services.email.EmailFactory;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import com.facilio.workflows.util.WorkflowUtil;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendInvoiceEmailCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        EMailTemplate emailTemplate = (EMailTemplate) context.get(FacilioConstants.ContextNames.TEMPLATE);
        List<File> mailAttachments = (List<File>) context.get(FacilioConstants.ContextNames.INVOICE_MAIL_ATTACHMENTS);
        String quotationPdfUrl = (String) context.get(FacilioConstants.ContextNames.INVOICE_PDF_URL);
        InvoiceContextV3 invoiceContext = (InvoiceContextV3) context.get(FacilioConstants.ContextNames.INVOICE);

        emailTemplate.setFrom(EmailFactory.getEmailClient().getNoReplyFromEmail());

        emailTemplate.setHtml(true); // TODO remove this temp setting here as client value is not getting set

        if(emailTemplate.getWorkflow() != null && emailTemplate.getWorkflow().getWorkflowString() == null) {
            emailTemplate.getWorkflow().setWorkflowString(WorkflowUtil.getXmlStringFromWorkflow(emailTemplate.getWorkflow()));
        }
        Map<String, Object> placeHolders = new HashMap<>();
        CommonCommandUtil.appendModuleNameInKey(null, "org", FieldUtil.getAsProperties(AccountUtil.getCurrentOrg()), placeHolders);
        JSONObject template = emailTemplate.getTemplate(placeHolders);

        Map<String, String> filesMap = new HashMap<>();
        if (StringUtils.isNotEmpty(quotationPdfUrl)) {
            String originalUrl = PdfUtil.exportUrlAsPdf(quotationPdfUrl, true, invoiceContext.getSubject(), FileInfo.FileFormat.PDF);
            filesMap.put(invoiceContext.getSubject(), originalUrl);
        }
        if (CollectionUtils.isNotEmpty(mailAttachments)) {
            for (File attachment: mailAttachments) {
                FileStore fs = FacilioFactory.getFileStore();
                Long fileId = fs.addFile(attachment.getName(),attachment,new MimetypesFileTypeMap().getContentType(attachment));
                filesMap.put(attachment.getName(), fs.getDownloadUrl(fileId));
            }
        }
        Long activityInvoiceId = invoiceContext.getId();
        if(invoiceContext.getGroup() !=  null)
        {
            activityInvoiceId =invoiceContext.getGroup().getId();
        }
        JSONObject info = new JSONObject();
        info.put("to", emailTemplate.getTo());
        CommonCommandUtil.addActivityToContext(activityInvoiceId, -1, InvoiceActivityType.EMAIL_INVOICE, info, (FacilioContext) context);
        FacilioFactory.getEmailClient().sendEmail(template,filesMap);

        return false;
    }
}
