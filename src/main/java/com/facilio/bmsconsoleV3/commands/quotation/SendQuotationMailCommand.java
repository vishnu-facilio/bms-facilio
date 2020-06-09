package com.facilio.bmsconsoleV3.commands.quotation;

import com.facilio.bmsconsole.activity.QuotationActivityType;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsoleV3.context.quotation.QuotationContext;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileInfo;
import com.facilio.pdf.PdfUtil;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SendQuotationMailCommand extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {

        EMailTemplate emailTemplate = (EMailTemplate) context.get(FacilioConstants.ContextNames.TEMPLATE);
        List<File> mailAttachments = (List<File>) context.get(FacilioConstants.ContextNames.QUOTATION_MAIL_ATTACHMENTS);
        String quotationPdfUrl = (String) context.get(FacilioConstants.ContextNames.QUOTATION_PDF_URL);
        QuotationContext quotationContext = (QuotationContext) context.get(FacilioConstants.ContextNames.QUOTATION);

        emailTemplate.setFrom("noreply@facilio.com");
        JSONObject template = emailTemplate.getOriginalTemplate();
        Map<String, String> filesMap = new HashMap<>();
        if (StringUtils.isNotEmpty(quotationPdfUrl)) {
            String originalUrl = PdfUtil.exportUrlAsPdf(quotationPdfUrl, true, quotationContext.getSubject(), FileInfo.FileFormat.PDF);
            filesMap.put(quotationContext.getSubject(), originalUrl);
        }
        if (CollectionUtils.isNotEmpty(mailAttachments)) {
            for (File attachment: mailAttachments) {
                FileStore fs = FacilioFactory.getFileStore();
                Long fileId = fs.addFile(attachment.getName(),attachment,new MimetypesFileTypeMap().getContentType(attachment));
                filesMap.put(attachment.getName(), fs.getDownloadUrl(fileId));
            }
        }
        JSONObject info = new JSONObject();
        info.put("to", emailTemplate.getTo());
        CommonCommandUtil.addActivityToContext(quotationContext.getId(), -1, QuotationActivityType.EMAIL_QUOTATION, info, (FacilioContext) context);
        FacilioFactory.getEmailClient().sendEmail(template,filesMap);

        return false;
    }
}
