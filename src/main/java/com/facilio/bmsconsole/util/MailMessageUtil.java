package com.facilio.bmsconsole.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.io.FileUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsoleV3.context.BaseMailMessageContext;
import com.facilio.bmsconsoleV3.context.EmailConversationThreadingContext;
import com.facilio.bmsconsoleV3.context.EmailFromAddress;
import com.facilio.bmsconsoleV3.context.EmailFromAddress.SourceType;
import com.facilio.bmsconsoleV3.context.EmailToModuleDataContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.InsertRecordBuilder;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LargeTextField;
import com.facilio.modules.fields.SupplementRecord;

public class MailMessageUtil {

    private static final Logger LOGGER = LogManager.getLogger(MailMessageUtil.class.getName());
    
    public static final String EMAIL_TO_MODULE_DATA_MODULE_NAME = "emailToModuleData";
    
    public static final String BASE_MAIL_CONTEXT = "baseMailContext";
    
    public static final String TEXT_CONTENT_TYPE = "text/plain";

    public static final String HTML_CONTENT_TYPE = "text/html";
    
    public static final String EMAIL_CONVERSATION_THREADING_MODULE_NAME = "emailConversationThreading";
    
    public static final String BASE_MAIL_MESSAGE_MODULE_NAME = "customMailMessages";

    public static BiFunction<String, String, String> getWholeEmailFromNameAndEmail = (name,email) ->{
		
		String newEmail = "\""+name+"\" <"+email+">";
		
		return newEmail;
	};
	
	public static Function<String, String> getNameFromEmail = (email) ->{
		
		return email.split("@")[0];
	};
    
    public static void updateLatestMailUID(SupportEmailContext supportEmail, long id) throws Exception {
        GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
                .table(ModuleFactory.getSupportEmailsModule().getTableName())
                .fields(FieldFactory.getSupportEmailFields()).andCondition(
                        CriteriaAPI.getIdCondition(id, ModuleFactory.getSupportEmailsModule()));
        int rowupdate = builder.update(FieldUtil.getAsProperties(supportEmail));
        LOGGER.info("Updated" + rowupdate);
    }
    
    public static EmailConversationThreadingContext getEmailConversationData(String referenceMessageId,FacilioModule module) throws Exception {
    	
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
    	
    	List<FacilioField> selectFields = new ArrayList<FacilioField>();
    	
    	selectFields.addAll(modBean.getAllFields(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME));
    	
    	Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(selectFields);
    	
    	SelectRecordsBuilder<EmailConversationThreadingContext> select = new SelectRecordsBuilder<EmailConversationThreadingContext>()
    			.select(selectFields)
    			.beanClass(EmailConversationThreadingContext.class)
    			.moduleName(MailMessageUtil.EMAIL_CONVERSATION_THREADING_MODULE_NAME)
    			.andCondition(CriteriaAPI.getCondition(fieldMap.get("messageId"), referenceMessageId, StringOperators.IS))
    			.andCondition(CriteriaAPI.getCondition(fieldMap.get("dataModuleId"), ""+module.getModuleId(), NumberOperators.EQUALS))
    			;
    	
    	List<EmailConversationThreadingContext> EmailConversationThreadingContexts = select.get();
    	
    	if(EmailConversationThreadingContexts != null && !EmailConversationThreadingContexts.isEmpty()) {
    		EmailConversationThreadingContext EmailConversationThreadingContext = EmailConversationThreadingContexts.get(0);
    		if(EmailConversationThreadingContext.getDataModuleId() > 0 && EmailConversationThreadingContext.getRecordId() > 0) {
    			
    			List<ModuleBaseWithCustomFields> records = V3RecordAPI.getRecordsList(modBean.getModule(EmailConversationThreadingContext.getDataModuleId()).getName(), Collections.singletonList(EmailConversationThreadingContext.getRecordId()));
    			
    			if(records != null && !records.isEmpty()) {
    				return EmailConversationThreadingContext;
    			}
    		}
    	}
    	return null;
    }
    
    public static EmailFromAddress getEmailFromAddress(String email,boolean isVerified) throws Exception {
    	
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> emailFromAddressField = modBean.getAllFields(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(emailFromAddressField);
    	
    	SelectRecordsBuilder<EmailFromAddress> select = new SelectRecordsBuilder<EmailFromAddress>()
				.beanClass(EmailFromAddress.class)
				.select(emailFromAddressField)
				.moduleName(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("email"), email, StringOperators.IS));
    	if(isVerified) {
    		select.andCondition(CriteriaAPI.getCondition(fieldMap.get("verificationStatus"), Boolean.TRUE.toString(), BooleanOperators.IS));
    	}
			
    	List<EmailFromAddress> fromAddress = select.get();
    	
    	if(fromAddress != null && !fromAddress.isEmpty()) {
    		return fromAddress.get(0);
    	}
    	return null;
    }
    
    public static EmailFromAddress getDefaultEmailFromAddress(SourceType sourceType) throws Exception {
		// TODO Auto-generated method stub
		
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> emailFromAddressField = modBean.getAllFields(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(emailFromAddressField);
    	
    	SelectRecordsBuilder<EmailFromAddress> select = new SelectRecordsBuilder<EmailFromAddress>()
				.beanClass(EmailFromAddress.class)
				.select(emailFromAddressField)
				.moduleName(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("creationType"), EmailFromAddress.CreationType.DEFAULT.getIndex()+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("sourceType"), sourceType.getIndex()+"", NumberOperators.EQUALS));
			
    	List<EmailFromAddress> fromAddress = select.get();
    	
    	if(fromAddress != null && !fromAddress.isEmpty()) {
    		return fromAddress.get(0);
    	}
    	return null;
	}
    
    public static List<EmailFromAddress> getEmailFromAddress(Criteria criteria) throws Exception {
		// TODO Auto-generated method stub
		
    	ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		
		List<FacilioField> emailFromAddressField = modBean.getAllFields(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME);
		
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(emailFromAddressField);
    	
    	SelectRecordsBuilder<EmailFromAddress> select = new SelectRecordsBuilder<EmailFromAddress>()
				.beanClass(EmailFromAddress.class)
				.select(emailFromAddressField)
				.moduleName(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME);
				
		if(criteria != null) {
			select.andCriteria(criteria);
		}
    	List<EmailFromAddress> fromAddress = select.get();
    	
    	return fromAddress;
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
    
    public static List<SupplementRecord> getMailMessageSupliments() throws Exception {
    	
        ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
        List<FacilioField> fields = modBean.getAllFields(BASE_MAIL_MESSAGE_MODULE_NAME);
        
        Map<String, FacilioField> fieldsAsMap = FieldFactory.getAsMap(fields);
        List<SupplementRecord> supplementList = new ArrayList<SupplementRecord>();
        
        supplementList.add((LargeTextField) fieldsAsMap.get("textContent"));
        supplementList.add((LargeTextField) fieldsAsMap.get("htmlContent"));
        
        return supplementList;
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
				.insertSupplements(getMailMessageSupliments())
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
    
    
    public static String getContentFromMessage(Message message,String contentToExtract) throws Exception {
        String result = "";
        if (message.isMimeType(contentToExtract)) {
            result = message.getContent().toString();
        } 
        else if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            result = getContentFromMimeMultipart(mimeMultipart,contentToExtract);
        }
        return result;
    }

    private static String getContentFromMimeMultipart(MimeMultipart mimeMultipart,String contentToExtract)  throws Exception{
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
        	
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            
            if (bodyPart.isMimeType(contentToExtract)) {
                result = result + bodyPart.getContent();
                break; 
            }
            else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getContentFromMimeMultipart((MimeMultipart)bodyPart.getContent(),contentToExtract);
            }
        }
        return result;
    }
    
    public static List<Map<String, Object>> getAttachments(Message message) throws Exception {
        List<Map<String, Object>> attachmentsList = new ArrayList<>();
        if (message.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
            
            int partCount = mimeMultipart.getCount();
            for (int i = 0; i < partCount; i++) {
                MimeBodyPart bodyPart = (MimeBodyPart) mimeMultipart.getBodyPart(i);
                if (Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())) {
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
                    
                    attachmentsList.add(attachmentObject);
                }
            }
            
        }
        return attachmentsList;
    }
    

    
    public static Function<String,String> getFirstMessageId = (messageIDs) -> messageIDs.substring(messageIDs.indexOf('<')+1, messageIDs.indexOf('>'));

}
