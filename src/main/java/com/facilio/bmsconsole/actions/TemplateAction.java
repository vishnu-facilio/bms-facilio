package com.facilio.bmsconsole.actions;

import java.util.List;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.templates.PushNotificationTemplate;
import com.facilio.bmsconsole.templates.SMSTemplate;
import com.facilio.bmsconsole.templates.WebNotificationTemplate;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class TemplateAction extends ActionSupport {
	

	List<JSONTemplate> workorderTemplates;
	public List<JSONTemplate> getWorkorderTemplates() {
		return workorderTemplates;
	}
	public void setWorkorderTemplates(List<JSONTemplate> workorderTemplates) {
		this.workorderTemplates = workorderTemplates;
	}
	
	public String getWOTemplates() throws Exception {
		
		FacilioContext context = new FacilioContext();
		Chain allWOTemplates = FacilioChainFactory.getAllWOTemplatesChain();
		allWOTemplates.execute(context);
		
		setWorkorderTemplates((List<JSONTemplate>) context.get(FacilioConstants.ContextNames.WORK_ORDER_TEMPLATE_LIST));
		
		return SUCCESS;
	}
	
	public String addEmail() throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.Workflow.TEMPLATE, emailTemplate);
		
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
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.Workflow.TEMPLATE, smsTemplate);
		
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
	
	public String addPushNotification() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.Workflow.TEMPLATE, pushNotificationTemplate);
		
		Chain addTemplate = FacilioChainFactory.getAddTemplateChain();
		addTemplate.execute(context);
		
		setTemplateId(pushNotificationTemplate.getId());
		return SUCCESS;
	}
	
	private PushNotificationTemplate pushNotificationTemplate;
	public PushNotificationTemplate getPushNotificationTemplate() {
		return pushNotificationTemplate;
	}
	public void setPushNotificationTemplate(PushNotificationTemplate pushNotificationTemplate) {
		this.pushNotificationTemplate = pushNotificationTemplate;
	}
	
	public String addWebNotification() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.Workflow.TEMPLATE, webNotificationTemplate);
		
		Chain addTemplate = FacilioChainFactory.getAddTemplateChain();
		addTemplate.execute(context);
		
		setTemplateId(webNotificationTemplate.getId());
		return SUCCESS;
		
	}
	
	private WebNotificationTemplate webNotificationTemplate;
	public WebNotificationTemplate getWebNotificationTemplate() {
		return webNotificationTemplate;
	}
	public void setWebNotificationTemplate(WebNotificationTemplate webNotificationTemplate) {
		this.webNotificationTemplate = webNotificationTemplate;
	}

	private long templateId;
	public long getTemplateId() {
		return templateId;
	}
	public void setTemplateId(long templateId) {
		this.templateId = templateId;
	}
}
