package com.facilio.bmsconsole.jobs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataSource;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.amazonaws.services.s3.model.S3Object;
import com.facilio.accounts.dto.Group;
import com.facilio.accounts.dto.User;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.SiteContext;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.SupportEmailAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;

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
					try {
						String s3Id = (String) emailProp.get("s3MessageId");
						S3Object rawEmail = AwsUtil.getAmazonS3Client().getObject(S3_BUCKET_NAME, s3Id);
						long requestId = createWorkOrderRequest(rawEmail);
						if (requestId != -1) {
							updateEmailProp((long) emailProp.get("id"), requestId);
						}
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
	
	private void updateEmailProp(long id, long requestId) throws Exception {
		updateIsProcessed.put("requestId",requestId);
		FacilioModule module = ModuleFactory.getWorkOrderRequestEMailModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.fields(FieldFactory.getWorkorderEmailFields())
				.table("WorkOrderRequest_EMail")
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		updateBuilder.update(updateIsProcessed);
		updateIsProcessed.remove("requestId");
	}
	
	private long createWorkOrderRequest(S3Object rawEmail) throws Exception {
		MimeMessage emailMsg = new MimeMessage(null, rawEmail.getObjectContent());
		MimeMessageParser parser = new MimeMessageParser(emailMsg);
		parser.parse();
		SupportEmailContext supportEmail = getSupportEmail(parser); 
		
		if(supportEmail != null) {
			WorkOrderRequestContext workOrderRequest = new WorkOrderRequestContext();
			
			User requester = new User();
			requester.setEmail(parser.getFrom());
			workOrderRequest.setRequester(requester);
			
			workOrderRequest.setSubject(parser.getSubject());
			workOrderRequest.setDescription(StringUtils.trim(parser.getPlainContent()));
			
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
//					LOGGER.info("Attachment Class name : "+attachment.getClass());
//					LOGGER.info("Attachment File Name : "+attachment.getName());
//					LOGGER.info("Attachment File Type : "+attachment.getContentType());
					
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
				workOrderRequest.setAssignmentGroup((Group) LookupSpecialTypeUtil.getEmptyLookedupObject(FacilioConstants.ContextNames.GROUPS, supportEmail.getAutoAssignGroupId()));
			}
			
			if (supportEmail.getSiteId() != -1) {
				SiteContext site = new SiteContext();
				site.setId(supportEmail.getSiteId());
				workOrderRequest.setResource(site);
			}
			
			workOrderRequest.setSourceType(TicketContext.SourceType.EMAIL_REQUEST);
			
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", supportEmail.getOrgId());
			long requestId = bean.addWorkOrderRequest(workOrderRequest, attachedFiles, attachedFilesFileName, attachedFilesContentType);
			LOGGER.info("Added Workorder from Email Parser : " + requestId );
			return requestId;
		}
		return -1;
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
