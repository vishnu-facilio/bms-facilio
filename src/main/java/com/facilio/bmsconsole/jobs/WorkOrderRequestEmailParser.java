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
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.service.FacilioService;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import org.apache.commons.collections4.CollectionUtils;
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
import java.util.*;

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
			requestId = NewTransactionService.newTransactionWithReturn(() -> addWorkRequest(emailMsg, parser, supportEmail));
		}
		updateEmailProp(emailpropId, requestId, orgId);
	}

	private long addWorkRequest(MimeMessage emailMsg, MimeMessageParser parser, SupportEmailContext supportEmail) throws Exception {
		ModuleCRUDBean bean = (ModuleCRUDBean) TransactionBeanFactory.lookup("ModuleCRUD", supportEmail.getOrgId());
		return bean.addRequestFromEmail(emailMsg, parser, supportEmail);
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
		LOGGER.info("Support email addresses : "+toAddresses);
		if(CollectionUtils.isNotEmpty(toAddresses)) {
			StringJoiner emails = new StringJoiner(",");
			for(Address address : toAddresses) {
				String email = ((InternetAddress) address).getAddress();
				emails.add(email);
			}
			SupportEmailContext supportEmail = SupportEmailAPI.getSupportEmailFromFwdEmail(emails.toString());
			LOGGER.info("Support email object : "+supportEmail);
			return supportEmail;
		}
		return null;
	}
}
