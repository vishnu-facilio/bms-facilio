package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsoleV3.context.BaseMailMessageContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fs.FileInfo;
import com.facilio.modules.*;
import com.facilio.pdf.PdfUtil;

import org.apache.commons.io.FileUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class MailMessageUtil {

    private static final Logger LOGGER = LogManager.getLogger(MailMessageUtil.class.getName());

    public static void updateLatestMailUID(SupportEmailContext supportEmail, long id) throws Exception {
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getSupportEmailsModule().getTableName())
                .fields(FieldFactory.getSupportEmailFields()).andCondition(
                        CriteriaAPI.getIdCondition(id, ModuleFactory.getSupportEmailsModule()));
        int rowupdate = builder.update(FieldUtil.getAsProperties(supportEmail));
        LOGGER.info("Updated" + rowupdate);
    }

    public static long createRecordToMailModule(SupportEmailContext supportMail, Message rawEmail) throws Exception {
        BaseMailMessageContext mailMessage = BaseMailMessageContext.instance(rawEmail);
        mailMessage.setParentId(supportMail.getId());
        Map<String, List<Map<String, Object>>> attachments = new HashMap<>();
        if (mailMessage.getAttachmentsList().size() > 0) {
            attachments.put("mailAttachments", mailMessage.getAttachmentsList());
            mailMessage.setSubForm(attachments);
        }
        FacilioChain chain = TransactionChainFactory.getSaveMailMessage();

        FacilioContext context = chain.getContext();

        context.put(FacilioConstants.ContextNames.SUPPORT_EMAIL, supportMail);
        context.put(FacilioConstants.ContextNames.MESSAGES, Collections.singletonList(mailMessage));

        chain.execute();
        long recordId = (long) context.getOrDefault(FacilioConstants.ContextNames.RECORD_ID, -1);
        return recordId;
    }
    
    
    
    public static String parseMessageContent(Part part, BaseMailMessageContext mailContext) throws Exception {
        if (part.isMimeType("text/*")) {
            String s = (String) part.getContent();
            mailContext.setContent(s);
        }
        if (part.isMimeType("multipart/alternative")) {
            Multipart mp = (Multipart)part.getContent();
            String text = null;
            for (int i = 0; i < mp.getCount(); i++) {
                Part bp = mp.getBodyPart(i);
                if (bp.isMimeType("text/plain")) {
                    if (text == null)
                        text =  (String) bp.getContent();
                    continue;
                } else if (bp.isMimeType("text/html")) {
                    String s = (String) bp.getContent();
                    if (s != null) {
                        if (s.length() > 2000) {
                            // TODO
                            // Saving large content to pdf
                            // Should be handle when big text field is supported
                            // service to parse mail content
//                            saveContentAsPdf(s, mailContext);
                            return "Content too big. Please check attachment";
                        }
                        else {
                            return s;
                        }
                    }
                } else {
                    return getMimeMultipartFromMessage(((MimeMultipart)part.getContent()), mailContext);
                }
            }
            mailContext.setContent(text);
        } else if (part.isMimeType("multipart/*")) {
            return getMimeMultipartFromMessage(((MimeMultipart)part.getContent()), mailContext);
        }

        return null;
    }

    private static String getMimeMultipartFromMessage(MimeMultipart mimeMultipart, BaseMailMessageContext mailContext) throws Exception {
        String result = "";
        int partCount = mimeMultipart.getCount();
        for (int i = 0; i < partCount; i++) {
            MimeBodyPart bodyPart = (MimeBodyPart) mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break;
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = html;
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result = result + getMimeMultipartFromMessage((MimeMultipart) bodyPart.getContent(), mailContext);
            } else  if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
                String fileName = bodyPart.getFileName();
                MimeMessage attachmentMessage = new MimeMessage(null, bodyPart.getInputStream());
                MimeMessageParser parser = new MimeMessageParser(attachmentMessage);
                parser.parse();
                Map<String, Object> attachmentObject = new HashMap<>();
                attachmentObject.put("fileFileName", bodyPart.getFileName());
                attachmentObject.put("fileContentType", bodyPart.getContentType());
                File file = File.createTempFile(fileName, "");
                FileUtils.copyInputStreamToFile(bodyPart.getInputStream(), file);
                attachmentObject.put("file", file);
                mailContext.addAttachmentList(attachmentObject);
            }
        }
        return result;
    }
    
    public static Function<String,String> getFirstMessageId = (messageIDs) -> messageIDs.substring(messageIDs.indexOf('<')+1, messageIDs.indexOf('>'));
    
    private static void saveContentAsPdf (String s, BaseMailMessageContext mailContext) throws IOException {
        Map<String, Object> attachmentObject = new HashMap<>();
        File tmpFile = File.createTempFile("Email_Content_", ".pdf");
        if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 155) {
            LOGGER.info("PDF Content => \n"+s);
        }
        String pdfFileLocation = PdfUtil.convertUrlToPdf(tmpFile.getPath(), s, null , FileInfo.FileFormat.PDF);
        File pdfFile = new File(pdfFileLocation);
        attachmentObject.put("file", pdfFile);
        attachmentObject.put("fileFileName", tmpFile.getName());
        attachmentObject.put("fileContentType", "application/pdf");
        mailContext.addAttachmentList(attachmentObject);
    }
}
