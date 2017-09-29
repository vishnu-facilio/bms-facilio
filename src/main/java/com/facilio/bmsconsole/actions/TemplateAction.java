package com.facilio.bmsconsole.actions;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.workflow.EMailTemplate;
import com.facilio.bmsconsole.workflow.SMSTemplate;
import com.facilio.bmsconsole.workflow.UserTemplate;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class TemplateAction extends ActionSupport {
	public String addEmail() throws Exception {
		
		emailTemplate = new EMailTemplate();
		emailTemplate.setName("Create Alarm Email");
		emailTemplate.setType(UserTemplate.Type.EMAIL);
		emailTemplate.setFrom("support@fazilio.com");
		emailTemplate.setTo("manthosh@facilio.com");
		emailTemplate.setSubject("Create Alarm");
		emailTemplate.setBody("Test creation of template");
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TEMPLATE, emailTemplate);
		
		Chain addTemplate = FacilioChainFactory.getAddTemplateChain();
		addTemplate.execute(context);
		
		setTemplateId(emailTemplate.getId());
		
		return SUCCESS;
	}
	
	private EMailTemplate emailTemplate;
	public EMailTemplate getEmailTemplate() {
		return emailTemplate;
	}
	public void setEmailTemplate(EMailTemplate emailTemplate) {
		this.emailTemplate = emailTemplate;
	}

	public String addSms() throws Exception {
		
		smsTemplate = new SMSTemplate();
		smsTemplate.setName("Create Alarm SMS");
		smsTemplate.setType(UserTemplate.Type.SMS);
		smsTemplate.setTo("+919003625354");
		smsTemplate.setMsg("Test sms template");
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TEMPLATE, smsTemplate);
		
		Chain addTemplate = FacilioChainFactory.getAddTemplateChain();
		addTemplate.execute(context);
		
		setTemplateId(smsTemplate.getId());
		
		return SUCCESS;
	}
	
	private SMSTemplate smsTemplate;
	public SMSTemplate getSmsTemplate() {
		return smsTemplate;
	}
	public void setSmsTemplate(SMSTemplate smsTemplate) {
		this.smsTemplate = smsTemplate;
	}
	
	private long templateId;
	public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}
}
