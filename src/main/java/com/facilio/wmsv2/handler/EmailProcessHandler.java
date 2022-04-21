package com.facilio.wmsv2.handler;

import java.io.InputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.mail.util.MimeMessageParser;

import com.amazonaws.services.s3.model.S3Object;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.jobs.WorkOrderRequestEmailParser.Status;
import com.facilio.bmsconsole.util.SupportEmailAPI;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.queueingservice.QueueingServiceBean;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;

import lombok.extern.log4j.Log4j;

@TopicHandler(
        topic = EmailProcessHandler.TOPIC,
        priority = -5,
        deliverTo = TopicHandler.DELIVER_TO.SESSION,
        sendToAllWorkers = false
)
@Log4j
public class EmailProcessHandler extends BaseHandler{
	public static final String TOPIC = "email-process";
	public static final String S3_BUCKET_NAME = FacilioProperties.getIncomingEmailS3Bucket();

	@Override
	public Message processOutgoingMessage(Message message) {
        try {
    		long emailID = -1L;
        	if (message != null && message.getContent()!=null) {
        			Map<String, Object> messageMap = message.getContent();
						String s3Id = (String) messageMap.get("s3MessageId");
						emailID = (long) Long.parseLong(messageMap.get("id").toString());
						try (S3Object rawEmail = AwsUtil.getAmazonS3Client().getObject(S3_BUCKET_NAME, s3Id); InputStream is = rawEmail.getObjectContent()) {
							MimeMessage emailMsg = new MimeMessage(null, is);
							MimeMessageParser parser = new MimeMessageParser(emailMsg);
							parser.parse();
							SupportEmailContext supportEmail = getSupportEmail(parser);
							long requestID = -1L;
							long orgID = -1L;
							if (supportEmail != null) {
								orgID = supportEmail.getOrgId();
								requestID = NewTransactionService.newTransactionWithReturn(() -> addWorkRequest(emailMsg, parser, supportEmail));
							}
							markAsDone(emailID, requestID, orgID);
	
						} catch (Exception e) {
							LOGGER.error("Exception occurred for id - " + s3Id, e);
							markAsFailed(emailID);
						}
                }
      } catch (Exception e) {
        	LOGGER.error("ERROR IN ADDING SCRIPT LOGS : "+ e);
        }
        return null;
    }
	
	private long addWorkRequest(MimeMessage emailMsg, MimeMessageParser parser, SupportEmailContext supportEmail) throws Exception {
		ModuleCRUDBean bean = (ModuleCRUDBean) TransactionBeanFactory.lookup("ModuleCRUD", supportEmail.getOrgId());
		return bean.addRequestFromEmail(emailMsg, parser, supportEmail);
	}

	private SupportEmailContext getSupportEmail(MimeMessageParser parser) throws Exception {
		SupportEmailContext supportEmail = getSupportEmail(parser.getTo());
		if (supportEmail != null) {
			return supportEmail;
		}

		supportEmail = getSupportEmail(parser.getCc());
		if (supportEmail != null) {
			return supportEmail;
		}

		supportEmail = getSupportEmail(parser.getBcc());
		if (supportEmail != null) {
			return supportEmail;
		}

		return null;
	}

	private SupportEmailContext getSupportEmail(List<Address> toAddresses) throws Exception {
		LOGGER.info("Support email addresses : " + toAddresses);
		if (CollectionUtils.isNotEmpty(toAddresses)) {
			StringJoiner emails = new StringJoiner(",");
			for (Address address : toAddresses) {
				String email = ((InternetAddress) address).getAddress();
				emails.add(email);
			}
			SupportEmailContext supportEmail = SupportEmailAPI.getSupportEmailFromFwdEmail(emails.toString());
			LOGGER.info("Support email object : " + supportEmail);
			return supportEmail;
		}
		return null;
	}
	
	private void markAsFailed(long id) throws Exception {
		Map<String, Object> dataBag = new HashMap<>();
		dataBag.put("state", Status.FAILED.getVal());
		updateEmailProp(id, dataBag);
	}

	private void markAsDone(long id, long requestID, long orgID) throws Exception {
		Map<String, Object> dataBag = new HashMap<>();
		dataBag.put("requestId", requestID);
		dataBag.put("orgId", orgID);
		dataBag.put("state", Status.PROCESSED.getVal());
		updateEmailProp(id, dataBag);
		LOGGER.info("Mark as Done Completed");
	}
	
	private void updateEmailProp(long id, Map<String, Object> dataBag) throws Exception {
		FacilioModule module = ModuleFactory.getWorkOrderRequestEMailModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.fields(FieldFactory.getWorkorderEmailFields())
				.table("WorkOrderRequest_EMail")
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		LOGGER.info("Update Query in Email Process handler : "+updateBuilder.toString());
		updateBuilder.update(dataBag);
		LOGGER.info("Update Query in Email Process handler ran Successfully ");
	}
}
