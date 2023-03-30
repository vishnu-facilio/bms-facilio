package com.facilio.wmsv2.handler;

import com.amazonaws.services.s3.model.S3Object;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.jobs.WorkOrderRequestEmailParser.Status;
import com.facilio.bmsconsole.util.SupportEmailAPI;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.util.FacilioUtil;
import com.facilio.wmsv2.message.Group;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;
import lombok.extern.log4j.Log4j;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.mail.internet.MimeMessage;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@TopicHandler(
        topic = EmailProcessHandler.TOPIC+"/#",
        priority = -5,
        deliverTo = TopicHandler.DELIVER_TO.SESSION,
        group = Group.RECIEVE_MAIL_WORKER,
		recordTimeout = 300 //5 mins
)
@Log4j
public class EmailProcessHandler extends BaseHandler{
	public static final String TOPIC = "email-process";
	public static final String S3_BUCKET_NAME = FacilioProperties.getIncomingEmailS3Bucket();

	@Override
	public Message processOutgoingMessage(Message message) {
        try {
			LOGGER.info("KAFKA EMAIL PROCESS HANDLER STARTED");
    		Long workOrderRequestEmailId = 0L;
        	if (message != null && message.getContent()!=null) {
        			Map<String, Object> messageMap = message.getContent();
        			if(messageMap!=null) {
						String s3Id = (String) messageMap.get("s3MessageId");
						LOGGER.info("s3Id --- "+s3Id);
						workOrderRequestEmailId = FacilioUtil.parseLong(messageMap.get("id"));

						String recepient = (String) messageMap.get("recipient");
						S3Object rawEmail = null;
						InputStream is=null;
						try {
							if(FacilioProperties.isDevelopment()){
								is = new FileInputStream("/resources/test.eml");
							}
							else {
								rawEmail = AwsUtil.getAmazonS3Client().getObject(S3_BUCKET_NAME, s3Id);
								is = rawEmail.getObjectContent();
							}
							MimeMessage emailMsg = new MimeMessage(null, is);
							MimeMessageParser parser = new MimeMessageParser(emailMsg);
							parser.parse();
							SupportEmailContext supportEmail =getSupportEmail(recepient);
							long requestID = -1L;
							long orgID = -1L;
							if (supportEmail != null) {
								orgID = supportEmail.getOrgId();
								Long workorderRequestEmailId=workOrderRequestEmailId;
								requestID = NewTransactionService.newTransactionWithReturn(() -> addWorkRequest(emailMsg, parser, supportEmail,workorderRequestEmailId));
							}
							markAsDone(workOrderRequestEmailId, requestID, orgID);
	
						} catch (Exception e) {
							LOGGER.error("Exception occurred for id - " + s3Id, e);
							markAsFailed(workOrderRequestEmailId);
						}
						finally {
							if(rawEmail!=null)
							{
								rawEmail.close();
							}
							if(is!=null)
							{
								is.close();
							}
						}
        			}
                }
      } catch (Exception e) {
        	LOGGER.error("ERROR IN ADDING SCRIPT LOGS : "+ e);
        }
        return null;
    }
	
	private long addWorkRequest(MimeMessage emailMsg, MimeMessageParser parser, SupportEmailContext supportEmail,Long workOrderRequestEmailId) throws Exception {
		ModuleCRUDBean bean = (ModuleCRUDBean) TransactionBeanFactory.lookup("ModuleCRUD", supportEmail.getOrgId());
		return bean.addRequestFromEmail(emailMsg, parser, supportEmail,workOrderRequestEmailId);
	}

//	private SupportEmailContext getSupportEmail(MimeMessageParser parser) throws Exception {
	private SupportEmailContext getSupportEmail(String recepient) throws Exception {
		SupportEmailContext supportEmail = SupportEmailAPI.getSupportEmailFromFwdEmail(recepient);
		LOGGER.info("Support email object : " + supportEmail);
		return supportEmail;
//		SupportEmailContext supportEmail = getSupportEmail(recepient);
//		if (supportEmail != null) {
//			return supportEmail;
//		}
//
//		supportEmail = getSupportEmail(parser.getCc());
//		if (supportEmail != null) {
//			return supportEmail;
//		}
//
//		supportEmail = getSupportEmail(parser.getBcc());
//		if (supportEmail != null) {
//			return supportEmail;
//		}

//		return null;
	}

//	private SupportEmailContext getSupportEmail(List<Address> toAddresses) throws Exception {
//		LOGGER.info("Support email addresses : " + toAddresses);
//		if (CollectionUtils.isNotEmpty(toAddresses)) {
//			StringJoiner emails = new StringJoiner(",");
//			for (Address address : toAddresses) {
//				String email = ((InternetAddress) address).getAddress();
//				emails.add(email);
//			}
//			SupportEmailContext supportEmail = SupportEmailAPI.getSupportEmailFromFwdEmail(emails.toString());
//			LOGGER.info("Support email object : " + supportEmail);
//			return supportEmail;
//		}
//		return null;
//	}
	
	public static void markAsFailed(Long workOrderRequestEmailId) throws Exception {
		Map<String, Object> dataBag = new HashMap<>();
		dataBag.put("state", Status.FAILED.getVal());
		updateEmailProp(workOrderRequestEmailId, dataBag);

	}

	private static void markAsDone(Long workOrderRequestEmailId, long requestID, long orgID) throws Exception {
		FacilioModule module=ModuleFactory.getWorkOrderRequestEMailModule();

			GenericSelectRecordBuilder select = new GenericSelectRecordBuilder()
					.table(module.getTableName())
					.select(FieldFactory.getWorkorderEmailFields())
					.andCondition(CriteriaAPI.getIdCondition(workOrderRequestEmailId,module));
				

			Map<String, Object> props = select.fetchFirst();
			if((Long)props.get("state")!=Status.FAILED.getVal() && props!=null)
			{
				Map<String, Object> dataBag = new HashMap<>();
				dataBag.put("requestId", requestID);
				dataBag.put("orgId", orgID);
				dataBag.put("state", Status.PROCESSED.getVal());
				updateEmailProp(workOrderRequestEmailId, dataBag);
				LOGGER.info("Mark as Done Completed");
			}

	}

	public static void updateEmailProp(Long workOrderRequestEmailId, Map<String, Object> dataBag) throws Exception {

			FacilioModule module = ModuleFactory.getWorkOrderRequestEMailModule();
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.fields(FieldFactory.getWorkorderEmailFields())
					.table("WorkOrderRequest_EMail")
					.andCondition(CriteriaAPI.getIdCondition(workOrderRequestEmailId, module));
			updateBuilder.update(dataBag);
			LOGGER.info("Update Query in Email Process handler : " + updateBuilder.toString() + "map -- "+dataBag);


	}
}
