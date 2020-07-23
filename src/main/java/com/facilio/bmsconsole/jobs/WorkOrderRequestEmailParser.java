package com.facilio.bmsconsole.jobs;

import com.amazonaws.services.s3.model.S3Object;
import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.ServiceRequestContext;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.util.CustomMailMessageApi;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.SupportEmailAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkOrderRequestEmailParser extends FacilioJob {

	public static final String S3_BUCKET_NAME = "ses-facilio-support";
	private static final Logger LOGGER = LogManager.getLogger(WorkOrderRequestEmailParser.class.getName());
	
	private Map<String, Object> updateIsProcessed = new HashMap<>();
	
	public WorkOrderRequestEmailParser() {
		// TODO Auto-generated constructor stub
		updateIsProcessed.put("isProcessed", true);
	}
	
	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		try {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.select(FieldFactory.getWorkorderEmailFields())
															.table("WorkOrderRequest_EMail")
															.andCustomWhere("IS_PROCESSED IS NULL OR IS_PROCESSED = false");
			List<Map<String, Object>> emailProps = selectBuilder.get();
			LOGGER.info("EMail Props : "+emailProps);
			if(emailProps != null) {
				for(Map<String, Object> emailProp : emailProps) {
					String s3Id = (String) emailProp.get("s3MessageId");
					try(S3Object rawEmail = AwsUtil.getAmazonS3Client().getObject(S3_BUCKET_NAME, s3Id); InputStream is = rawEmail.getObjectContent()) {

							createWorkOrderRequest((long) emailProp.get("id"), is);
//						updateEmailProp((long) emailProp.get("id"), requestId);
//						if(AccountUtil.isFeatureEnabled(FeatureLicense.SERVICE_REQUEST)) {
//							createServiceRequest(rawEmail);
//						} else {
//							long requestId = createWorkOrderRequest(rawEmail);
//	//						if (requestId != -1) { //Marked as processed even if no request is created because of no matching support email
//								updateEmailProp((long) emailProp.get("id"), requestId);
//	//						}
//						}
					}
					catch(Exception e) {
						LOGGER.error("Exception occurred ", e);
					}
				}
			}
		}
		catch(Exception e) {
			LOGGER.error("Exception occurred ", e);
		}
	}
	
	private void updateEmailProp(long id, long requestId, long orgId) throws Exception {
		updateIsProcessed.put("requestId",requestId);
		updateIsProcessed.put("orgId", orgId);
		FacilioModule module = ModuleFactory.getWorkOrderRequestEMailModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.fields(FieldFactory.getWorkorderEmailFields())
				.table("WorkOrderRequest_EMail")
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		updateBuilder.update(updateIsProcessed);
		updateIsProcessed.remove("requestId");
		updateIsProcessed.remove("orgId");
	}
	
	
	private void createWorkOrderRequest(long emailpropId, InputStream is) throws Exception {
		MimeMessage emailMsg = new MimeMessage(null, is);
		MimeMessageParser parser = new MimeMessageParser(emailMsg);
		parser.parse();
		SupportEmailContext supportEmail = getSupportEmail(parser); 
		long requestId = -1;
		long orgId = -1;
		if(supportEmail != null) {
			orgId = supportEmail.getOrgId();
			if (AccountUtil.getOrgBean(supportEmail.getOrgId()).isFeatureEnabled(FeatureLicense.CUSTOM_MAIL)) {
				CustomMailMessageApi.createRecordToMailModule(supportEmail, emailMsg);
			}
			else if (AccountUtil.getOrgBean(supportEmail.getOrgId()).isFeatureEnabled(FeatureLicense.SERVICE_REQUEST)) {
				ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", supportEmail.getOrgId());
				ServiceRequestContext serviceRequestContext = new ServiceRequestContext();
				User requester = new User();
				requester.setEmail(parser.getFrom());
				serviceRequestContext.setSubject(parser.getSubject());
				if (parser.getPlainContent() != null) {
					serviceRequestContext.setDescription(StringUtils.trim(parser.getPlainContent()));
				} 
				else {
					serviceRequestContext.setDescription(StringUtils.trim(parser.getHtmlContent()));
				}
				List<DataSource> attachments = parser.getAttachmentList();
				List<File> attachedFiles = null;
				List<String> attachedFilesFileName = null;
				List<String> attachedFilesContentType = null;
				if (attachments != null && !attachments.isEmpty()) {
					LOGGER.info("Attachment List : "+attachments);
					attachedFiles = new ArrayList<>();
					attachedFilesFileName = new ArrayList<>();
					attachedFilesContentType = new ArrayList<>();
					for (DataSource attachment : attachments) {
						if (attachment.getName() != null) {
							attachedFilesFileName.add(attachment.getName());
							attachedFilesContentType.add(attachment.getContentType());
							File file = File.createTempFile(attachment.getName(), "");
							FileUtils.copyInputStreamToFile(attachment.getInputStream(), file);
							attachedFiles.add(file);
						}
					}
					LOGGER.info("Parsed Attachments : "+attachedFiles);
				}
				serviceRequestContext.setSiteId(supportEmail.getSiteId());
				serviceRequestContext.setSourceType(ServiceRequestContext.SourceType.EMAIL_REQUEST.getIntVal());
				serviceRequestContext.setRequester(requester);
				requestId = bean.addServcieRequestFromEmail(serviceRequestContext, attachedFiles, attachedFilesFileName, attachedFilesContentType);
				LOGGER.info("Added servicerequest from Email Parser : " + requestId );	
			}
			else {
				ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", supportEmail.getOrgId());
				WorkOrderContext workorderContext = new WorkOrderContext();
				workorderContext.setSendForApproval(true);
				User requester = new User();
				requester.setEmail(parser.getFrom());
				//not to send email while creating wo from email 
				//requester.setInviteAcceptStatus(true);
				
				workorderContext.setSubject(parser.getSubject());
				if (parser.getPlainContent() != null) {
					workorderContext.setDescription(StringUtils.trim(parser.getPlainContent()));
				} 
				else {
					workorderContext.setDescription(StringUtils.trim(parser.getHtmlContent()));
				}
				
				List<DataSource> attachments = parser.getAttachmentList();
				List<File> attachedFiles = null;
				List<String> attachedFilesFileName = null;
				List<String> attachedFilesContentType = null;
				if (attachments != null && !attachments.isEmpty()) {
					LOGGER.info("Attachment List : "+attachments);
					attachedFiles = new ArrayList<>();
					attachedFilesFileName = new ArrayList<>();
					attachedFilesContentType = new ArrayList<>();
					for (DataSource attachment : attachments) {
//						LOGGER.info("Attachment Class name : "+attachment.getClass());
//						LOGGER.info("Attachment File Name : "+attachment.getName());
//						LOGGER.info("Attachment File Type : "+attachment.getContentType());
						if (attachment.getName() != null) {
							attachedFilesFileName.add(attachment.getName());
							attachedFilesContentType.add(attachment.getContentType());
							File file = File.createTempFile(attachment.getName(), "");
							FileUtils.copyInputStreamToFile(attachment.getInputStream(), file);
							attachedFiles.add(file);
						}
					}
					LOGGER.info("Parsed Attachments : "+attachedFiles);
				}
				if (supportEmail.getAutoAssignGroupId() != -1) {
					workorderContext.setAssignmentGroup((Group) LookupSpecialTypeUtil.getEmptyLookedupObject(FacilioConstants.ContextNames.GROUPS, supportEmail.getAutoAssignGroupId()));
				}
				workorderContext.setSiteId(supportEmail.getSiteId());
				
				workorderContext.setSourceType(TicketContext.SourceType.EMAIL_REQUEST);
				
				workorderContext.setRequester(requester);
				requestId = bean.addWorkOrderFromEmail(workorderContext, attachedFiles, attachedFilesFileName, attachedFilesContentType);
				LOGGER.info("Added Workorder from Email Parser : " + requestId );
			}
		}
		updateEmailProp(emailpropId, requestId, orgId);
	}

	private SupportEmailContext getSupportEmail(MimeMessageParser parser) throws Exception {
		SupportEmailContext supportEmail = getSupportEmail(parser.getTo());
		if(supportEmail != null) {
			return supportEmail;
		}
		
		supportEmail = getSupportEmail(parser.getCc());
		if(supportEmail != null) {
			return supportEmail;
		}
		
		supportEmail = getSupportEmail(parser.getBcc());
		if(supportEmail != null) {
			return supportEmail;
		}
		
		return null;
	}
	
	private SupportEmailContext getSupportEmail(List<Address> toAddresses) throws Exception {
		if(toAddresses != null) {
			LOGGER.info("Support email addresses : "+toAddresses);
			for(Address address : toAddresses) {
				String email = ((InternetAddress) address).getAddress();
				if(email.endsWith(".facilio.com")) {
					SupportEmailContext supportEmail = SupportEmailAPI.getSupportEmailFromFwdEmail(email);
					LOGGER.info("Support email object : "+supportEmail);
					return supportEmail;
				}
			}
		}
		return null;
	}
}
