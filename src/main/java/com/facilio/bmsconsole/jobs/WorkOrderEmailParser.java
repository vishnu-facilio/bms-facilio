package com.facilio.bmsconsole.jobs;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.util.MimeMessageParser;

import com.amazonaws.services.s3.model.S3Object;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.RequesterContext;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.bmsconsole.context.WorkOrderRequestContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.util.SupportEmailAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.tasker.job.FacilioJob;
import com.facilio.tasker.job.JobContext;
import com.facilio.transaction.FacilioConnectionPool;

public class WorkOrderEmailParser extends FacilioJob {

	public static final String S3_BUCKET_NAME = "ses-support-mail-parser";
	
	private Map<String, Object> updateIsProcessed = new HashMap<>();
	
	public WorkOrderEmailParser() {
		// TODO Auto-generated constructor stub
		updateIsProcessed.put("isProcessed", true);
	}
	
	@Override
	public void execute(JobContext jc) {
		// TODO Auto-generated method stub
		System.out.println("Workorder Email Parser");
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
															.connection(conn)
															.select(FieldFactory.getWorkorderEmailFields())
															.table("WorkOrder_EMail")
															.andCustomWhere("IS_PROCESSED IS NULL OR IS_PROCESSED = false");
			List<Map<String, Object>> emailProps = selectBuilder.get();
			StringBuilder idsToBeRemoved = new StringBuilder("ID in (");
			boolean isEmpty = true;
			if(emailProps != null) {
				for(Map<String, Object> emailProp : emailProps) {
					try {
						String s3Id = (String) emailProp.get("s3MessageId");
						S3Object rawEmail = AwsUtil.getAmazonS3Client().getObject(S3_BUCKET_NAME, s3Id);
						createWorkOrderRequest(rawEmail);
						if(!isEmpty) {
							idsToBeRemoved.append(", ");
						}
						else {
							isEmpty = false;
						}
						idsToBeRemoved.append(emailProp.get("id"));
					}
					catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
			
			if(!isEmpty) {
				idsToBeRemoved.append(")");
				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
																.connection(conn)
																.fields(FieldFactory.getWorkorderEmailFields())
																.table("WorkOrder_Email")
																.andCustomWhere(idsToBeRemoved.toString());
				updateBuilder.update(updateIsProcessed);
			}

		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void createWorkOrderRequest(S3Object rawEmail) throws Exception {
		MimeMessage emailMsg = new MimeMessage(null, rawEmail.getObjectContent());
		MimeMessageParser parser = new MimeMessageParser(emailMsg);
		parser.parse();
		SupportEmailContext supportEmail = getSupportEmail(parser); 
		
		if(supportEmail != null) {
			WorkOrderRequestContext workOrderRequest = new WorkOrderRequestContext();
			
			RequesterContext requester = new RequesterContext();
			requester.setEmail(parser.getFrom());
			workOrderRequest.setRequester(requester);
			
			TicketContext ticket = new TicketContext();
			ticket.setSubject(parser.getSubject());
			ticket.setDescription(parser.getPlainContent());
			if(supportEmail.getAutoAssignGroup() != null) {
				ticket.setAssignmentGroup(supportEmail.getAutoAssignGroup());
			}
			
			workOrderRequest.setTicket(ticket);
			
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", supportEmail.getOrgId());
			System.out.println("Added Workorder from Email Parser : " + bean.addWorkOrderRequest(workOrderRequest));
		}
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
			for(Address address : toAddresses) {
				String email = ((InternetAddress) address).getAddress();
				if(email.endsWith(".facilio.me")) {
					return SupportEmailAPI.getSupportEmailFromFwdEmail(email);
				}
			}
		}
		return null;
	}

}
