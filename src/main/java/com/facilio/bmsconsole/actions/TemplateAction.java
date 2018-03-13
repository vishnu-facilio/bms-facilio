package com.facilio.bmsconsole.actions;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.templates.PushNotificationTemplate;
import com.facilio.bmsconsole.templates.SMSTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.templates.WebNotificationTemplate;
import com.facilio.constants.FacilioConstants;
import com.opensymphony.xwork2.ActionSupport;

public class TemplateAction extends ActionSupport {
	
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	public String addWorkOrderTemplate() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		context.put(FacilioConstants.ContextNames.TASK_MAP, tasks);
		context.put(FacilioConstants.ContextNames.TEMPLATE_TYPE, Type.WORKORDER);
		
		Chain addTemplate = FacilioChainFactory.getAddWorkorderTemplateChain();
		addTemplate.execute(context);
		
		id = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		return SUCCESS;
	}
	
	public String fetchTemplate() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, id);
		
		Chain getTemplate = FacilioChainFactory.getTemplateChain();
		getTemplate.execute(context);
		
		template = (Template) context.get(FacilioConstants.ContextNames.TEMPLATE);
		
		return SUCCESS;
	}
	
	private Template template;
	public Template getTemplate() {
		return template;
	}
	public void setTemplate(Template template) {
		this.template = template;
	}
	public String fetchTemplates() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TEMPLATE_TYPE, type);
		
		Chain getTemplatesChain = FacilioChainFactory.getTemplatesOfTypeChain();
		getTemplatesChain.execute(context);
		
		templates = (List<Template>) context.get(FacilioConstants.ContextNames.TEMPLATE_LIST);
		
		return SUCCESS;
	}
	
	private List<Template> templates;
	public List<Template> getTemplates() {
		return templates;
	}
	public void setTemplates(List<Template> templates) {
		this.templates = templates;
	}
	
	private Template.Type type;
	public int getType() {
		if (type != null) {
			return type.getIntVal();
		}
		return -1;
	}
	public void setType(int type) {
		this.type = Template.Type.getAllTypes().get(type);
	}

	private WorkOrderContext workorder;
	public WorkOrderContext getWorkorder() {
		return workorder;
	}
	public void setWorkorder(WorkOrderContext workorder) {
		this.workorder = workorder;
	}
	
	private Map<String, List<TaskContext>> tasks;
	public Map<String, List<TaskContext>> getTasks() {
		return tasks;
	}
	public void setTasks(Map<String, List<TaskContext>> tasks) {
		this.tasks = tasks;
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
