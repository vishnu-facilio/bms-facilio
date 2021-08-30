package com.facilio.bmsconsole.jobs;

import com.amazonaws.services.s3.model.S3Object;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.util.SupportEmailAPI;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.transaction.NewTransactionService;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.taskengine.job.FacilioJob;
import com.facilio.taskengine.job.JobContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.mail.util.MimeMessageParser;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class WorkOrderRequestEmailParser extends FacilioJob {

	public enum Status {
		NEW,
		PROCESSED,
		FAILED;

		public int getVal() {
			return ordinal();
		}
	}

	public static final String S3_BUCKET_NAME = FacilioProperties.getIncomingEmailS3Bucket();
	private static final Logger LOGGER = LogManager.getLogger(WorkOrderRequestEmailParser.class.getName());


	@Override
	public void execute(JobContext jc) throws Exception {

		long emailID = -1L;

		try {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(FieldFactory.getWorkorderEmailFields())
					.table("WorkOrderRequest_EMail")
					.andCondition(CriteriaAPI.getCondition("STATE", "state", Integer.toString(Status.NEW.getVal()), NumberOperators.EQUALS))
					.limit(100);
			List<Map<String, Object>> emailProps = selectBuilder.get();
			LOGGER.info("EMail Props : " + emailProps);

			if (emailProps != null) {
				for (Map<String, Object> emailProp : emailProps) {
					String s3Id = (String) emailProp.get("s3MessageId");
					emailID = (long) emailProp.get("id");
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
			}

		} catch (Exception e) {
			LOGGER.error("Exception occurred ", e);
			markAsFailed(emailID);
		}
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
	}

	private void updateEmailProp(long id, Map<String, Object> dataBag) throws Exception {
		FacilioModule module = ModuleFactory.getWorkOrderRequestEMailModule();
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.fields(FieldFactory.getWorkorderEmailFields())
				.table("WorkOrderRequest_EMail")
				.andCondition(CriteriaAPI.getIdCondition(id, module));
		updateBuilder.update(dataBag);
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
}
