package com.facilio.bmsconsole.util;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsoleV3.context.BaseMailMessageContext;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.bmsconsoleV3.context.EmailToModuleDataContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fs.FileInfo;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.pdf.PdfUtil;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

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
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.function.Function;

public class MailMessageUtil {

    private static final Logger LOGGER = LogManager.getLogger(MailMessageUtil.class.getName());
    
    public static final String EMAIL_TO_MODULE_DATA_MODULE_NAME = "emailToModuleData";
    
    public static final String BASE_MAIL_CONTEXT = "baseMailContext";
    
    public static final String EMAIL_CONVERSATION_THREADING_MODULE_NAME = "emailConversationThreading";

    public static void updateLatestMailUID(SupportEmailContext supportEmail, long id) throws Exception {
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getSupportEmailsModule().getTableName())
                .fields(FieldFactory.getSupportEmailFields()).andCondition(
                        CriteriaAPI.getIdCondition(id, ModuleFactory.getSupportEmailsModule()));
        int rowupdate = builder.update(FieldUtil.getAsProperties(supportEmail));
        LOGGER.info("Updated" + rowupdate);
    }
    
    public static EmailToModuleDataContext getEmailToModuleData(String referenceMessageId,FacilioModule module) throws Exception {
    	
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	
    	List<FacilioField> selectFields = new ArrayList<FacilioField>();
    	
    	selectFields.addAll(modBean.getAllFields(MailMessageUtil.EMAIL_TO_MODULE_DATA_MODULE_NAME));
    	
    	Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(selectFields);
    	
    	SelectRecordsBuilder<EmailToModuleDataContext> select = new SelectRecordsBuilder<EmailToModuleDataContext>()
    			.select(selectFields)
    			.beanClass(EmailToModuleDataContext.class)
    			.moduleName(MailMessageUtil.EMAIL_TO_MODULE_DATA_MODULE_NAME)
    			.andCondition(CriteriaAPI.getCondition(fieldMap.get("messageId"), referenceMessageId, StringOperators.IS))
    			.andCondition(CriteriaAPI.getCondition(fieldMap.get("dataModuleId"), ""+module.getModuleId(), NumberOperators.EQUALS))
    			;
    	
    	List<EmailToModuleDataContext> emailToModuleDatas = select.get();
    	
    	if(emailToModuleDatas != null && !emailToModuleDatas.isEmpty()) {
    		EmailToModuleDataContext emailToModuleData = emailToModuleDatas.get(0);
    		if(emailToModuleData.getDataModuleId() > 0 && emailToModuleData.getRecordId() > 0) {
    			
    			List<ModuleBaseWithCustomFields> records = V3RecordAPI.getRecordsList(modBean.getModule(emailToModuleData.getDataModuleId()).getName(), Collections.singletonList(emailToModuleData.getRecordId()));
    			
    			if(records != null && !records.isEmpty()) {
    				return emailToModuleData;
    			}
    		}
    	}
    	return null;
    }
    
    public static EmailToModuleDataContext getEmailToModuleContext(Long recordId,Long moduleId) throws Exception {
    	
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	
    	List<FacilioField> selectFields = new ArrayList<FacilioField>();
    	
    	selectFields.addAll(modBean.getAllFields(MailMessageUtil.EMAIL_TO_MODULE_DATA_MODULE_NAME));
    	
    	Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(selectFields);
    	
    	SelectRecordsBuilder<EmailToModuleDataContext> select = new SelectRecordsBuilder<EmailToModuleDataContext>()
    			.select(selectFields)
    			.beanClass(EmailToModuleDataContext.class)
    			.moduleName(MailMessageUtil.EMAIL_TO_MODULE_DATA_MODULE_NAME)
    			.andCondition(CriteriaAPI.getCondition(fieldMap.get("recordId"), ""+recordId, NumberOperators.EQUALS))
    			.andCondition(CriteriaAPI.getCondition(fieldMap.get("dataModuleId"), ""+moduleId, NumberOperators.EQUALS))
    			;
    	
    	List<EmailToModuleDataContext> emailToModuleDatas = select.get();
    	
    	if(emailToModuleDatas != null && !emailToModuleDatas.isEmpty()) {
    		return emailToModuleDatas.get(0);
    	}
    	return null;
    }
    
    public static EmailConversationThreadingContext getEmailConversationThreadingContext(Long recordId,Long moduleId) throws Exception {
    	
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	
    	List<FacilioField> selectFields = new ArrayList<FacilioField>();
    	
    	selectFields.addAll(modBean.getAllFields(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME));
    	
    	Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(selectFields);
    	
    	SelectRecordsBuilder<EmailConversationThreadingContext> select = new SelectRecordsBuilder<EmailConversationThreadingContext>()
    			.select(selectFields)
    			.beanClass(EmailConversationThreadingContext.class)
    			.moduleName(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME)
    			.andCondition(CriteriaAPI.getCondition(fieldMap.get("recordId"), ""+recordId, NumberOperators.EQUALS))
    			.andCondition(CriteriaAPI.getCondition(fieldMap.get("dataModuleId"), ""+moduleId, NumberOperators.EQUALS))
    			.orderBy("ID Asc")
    			.limit(1)
    			;
    	
    	List<EmailConversationThreadingContext> emailConversationContexts = select.get();
    	
    	if(emailConversationContexts != null && !emailConversationContexts.isEmpty()) {
    		return emailConversationContexts.get(0);
    	}
    	return null;
    }
    
    public static Map<String, Object> fetchRecordWithLocalIdOrId(FacilioModule module,Long localId) throws Exception {
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		FacilioField localIdField = modBean.getField("localId", module.getName());
		if(localIdField != null) {
			
		}
		SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
				.module(modBean.getModule(module.getName()))
				.select(modBean.getAllFields(module.getName()))
				.beanClass(ModuleBaseWithCustomFields.class)
				;
		
		if(localIdField != null) {
			builder.andCondition(CriteriaAPI.getCondition(localIdField, localId+"", NumberOperators.EQUALS));
		}
		else {
			builder.andCondition(CriteriaAPI.getIdCondition(localId, module));
		}
		List<Map<String, Object>> props = builder.getAsProps();
		
		if(props != null && !props.isEmpty()) {
			return props.get(0);
		}
		return null;
	}
    
    public static Long getLocalIdFromSubject(String subject) {
    	try {
    		if(subject.contains("#")) {
    			subject = subject + " ";
        		int indexOfHash = subject.indexOf("#");
        		String localId = subject.substring(indexOfHash+1, subject.indexOf(' ', indexOfHash+1));
        		return Long.parseLong(localId.trim());
        	}
    	}
    	catch(Exception e) {
    		
    	}
    	return null;
    }
    
    public static void addEmailToModuleDataContext(BaseMailMessageContext mailContext,long recordId,long moduleId) throws Exception {
    	
    	EmailToModuleDataContext emailToModuleData = FieldUtil.getAsBeanFromJson(FieldUtil.getAsJSON(mailContext), EmailToModuleDataContext.class);
		
		emailToModuleData.setParentBaseMail(mailContext);
		emailToModuleData.setRecordId(recordId);
		emailToModuleData.setDataModuleId(moduleId);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		InsertRecordBuilder<EmailToModuleDataContext> insert = new InsertRecordBuilder<EmailToModuleDataContext>()
				.moduleName(MailMessageUtil.EMAIL_TO_MODULE_DATA_MODULE_NAME)
				.fields(modBean.getAllFields(MailMessageUtil.EMAIL_TO_MODULE_DATA_MODULE_NAME))
				.addRecord(emailToModuleData);
		
		insert.save();
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
