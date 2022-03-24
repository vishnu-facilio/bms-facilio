package com.facilio.bmsconsole.actions;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.text.StringSubstitutor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.AssetCategoryContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.templates.DefaultTemplate;
import com.facilio.bmsconsole.templates.DefaultTemplate.DefaultTemplateType;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.templates.PushNotificationTemplate;
import com.facilio.bmsconsole.templates.SMSTemplate;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.templates.WebNotificationTemplate;
import com.facilio.bmsconsole.util.AssetsAPI;
import com.facilio.bmsconsole.util.TemplateAPI;
import com.facilio.bmsconsole.workflow.rule.ActionContext;
import com.facilio.bmsconsole.workflow.rule.ActionType;
import com.facilio.bmsconsole.workflow.rule.AlarmRuleContext;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.facilio.util.FacilioUtil;
import com.facilio.workflows.context.WorkflowContext;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TemplateAction  extends FacilioAction {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id = -1;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	private String result;
	public String getRes() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	public String deleteTemplate() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, id);
		
		FacilioChain deleteTemplateChain = FacilioChainFactory.deleteTemplateChain();
		deleteTemplateChain.execute(context);
		
		result = "success";
		
		return SUCCESS;
	}
	
	private String templateName;
	public String getTemplateName() {
		return templateName;
	}
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}
	
	public String addWorkOrderTemplate() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		context.put(FacilioConstants.ContextNames.TASK_MAP, tasks);
		context.put(FacilioConstants.ContextNames.TEMPLATE_TYPE, Type.WORKORDER);
		context.put(FacilioConstants.ContextNames.TEMPLATE_NAME, templateName);
		
		FacilioChain addTemplate = FacilioChainFactory.getAddWorkorderTemplateChain();
		addTemplate.execute(context);
		
		id = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		return SUCCESS;
	}
	
	public String updateWorkOrderTemplate() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, id);
		context.put(FacilioConstants.ContextNames.WORK_ORDER, workorder);
		context.put(FacilioConstants.ContextNames.TASK_MAP, tasks);
		context.put(FacilioConstants.ContextNames.TEMPLATE_TYPE, Type.WORKORDER);
		context.put(FacilioConstants.ContextNames.TEMPLATE_NAME, templateName);
		
		FacilioChain addTemplate = FacilioChainFactory.updateWorkorderTemplateChain();
		addTemplate.execute(context);
		
		id = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		return SUCCESS;
	}
	private List<Long> ids;
	public List<Long> getIds() {
		return ids;
	}
	public void setIds(List<Long> ids) {
		this.ids = ids;
	}
	public String getDefaultTemplate() {
		HashMap<Long, DefaultTemplate> templateMap = new HashMap<Long, DefaultTemplate>();
		for (long id : ids) {
			templateMap.put(id, TemplateAPI.getDefaultTemplate(DefaultTemplateType.ACTION, (int) id));
		}
		setResult(FacilioConstants.ContextNames.DEFAULT_TEMPLATE, templateMap);
		return SUCCESS;
	}
	
	JSONObject placeHolder;
	
	public JSONObject getPlaceHolder() {
		return placeHolder;
	}
	public void setPlaceHolder(JSONObject placeHolder) {
		this.placeHolder = placeHolder;
	}
	@SuppressWarnings("unchecked")
	public String createRulefromTemplates () {
		JSONObject templateJson = TemplateAPI.getDefaultTemplate(DefaultTemplateType.RULE, (int) id).getJson();
		JSONObject rules = (JSONObject) templateJson.get("fdd_rule");
		JSONObject placeholders = (JSONObject) rules.get("placeHolder");
		try {
			Map<String, Object> placeHolderMap = new HashMap<>();
			placeholders.keySet().forEach(keyStr -> {
				final JSONObject keyvalue = (JSONObject) placeholders.get(keyStr);
				placeHolderMap.put((String) keyvalue.get("uniqueId"), keyvalue.get("default_value"));
			});
			JSONParser parser = new JSONParser();
			String resolvedString =  StringSubstitutor.replace(rules, placeHolderMap);
			JSONObject obj = (JSONObject) parser.parse(resolvedString);
			JSONObject rulesObj = (JSONObject) obj.get("rules");
			Set<String> ruleskeys = rulesObj.keySet();
			AssetCategoryContext assetCategory = AssetsAPI.getCategory((String) obj.get("category"));
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule((String) obj.get("moduleName"));
			System.out.println((String) obj.get("threshold_metric"));
			
			AlarmRuleContext alarmRule = new AlarmRuleContext();
			alarmRule.setPreRequsite(new ReadingRuleContext());
			alarmRule.setAlarmTriggerRule(new ReadingRuleContext());
			ReadingRuleContext preRequsiteRule = alarmRule.getPreRequsite();
			preRequsiteRule.setName((String) obj.get("name"));
			preRequsiteRule.setDescription((String) obj.get("description"));
			preRequsiteRule.setAssetCategoryId(assetCategory.getId());
			
			FacilioField field = modBean.getField((String) obj.get("threshold_metric"), module.getName());

			((ReadingRuleContext) alarmRule.getAlarmTriggerRule()).setReadingFieldId(field.getFieldId());
			preRequsiteRule.setModuleId(module.getModuleId());
			preRequsiteRule.setActivityType(EventType.CREATE.getValue());
			alarmRule.setIsAutoClear(true);
			for (String key : ruleskeys) {
				JSONObject rule = (JSONObject) rulesObj.get(key);
				ObjectMapper mapper = FieldUtil.getMapper(WorkflowContext.class);
				JSONArray fieldJsons = FacilioUtil.getSingleTonJsonArray(rule.get("workflow"));
				long thresholdType = (long) rule.get("thresholdType");
				List<WorkflowContext> list = mapper.readValue(JSONArray.toJSONString(fieldJsons), mapper.getTypeFactory().constructCollectionType(List.class, WorkflowContext.class));
				if (((String)rule.get("action")).equals("PreRequsite")) {
					preRequsiteRule.setWorkflow(list.get(0));
					FacilioModule preModule = modBean.getModule((String) rule.get("moduleName"));
					FacilioField preRequesitefield = modBean.getField((String) rule.get("threshold_metric"), preModule.getName());
					preRequsiteRule.setThresholdType((int) thresholdType);
					preRequsiteRule.setReadingFieldId(preRequesitefield.getId());
				} else if (((String)rule.get("action")).equals("TRIGGER_ALARM")) {
					alarmRule.getAlarmTriggerRule().setName("TRIGGER_ALARM");
					((ReadingRuleContext) alarmRule.getAlarmTriggerRule()).setWorkflow(list.get(0));
					((ReadingRuleContext) alarmRule.getAlarmTriggerRule()).setThresholdType((int) thresholdType);
					ActionContext action = new ActionContext();
					action.setActionType(ActionType.ADD_ALARM);
					JSONObject possible = new JSONObject();
					JSONArray fieldMatcher = new JSONArray();
					JSONObject content = new JSONObject();
					content.put("field", "problem");
					content.put("value", obj.get("problem"));
					fieldMatcher.add(content);
					content = new JSONObject();
					content.put("field", "message");
					content.put("value", obj.get("name"));
					fieldMatcher.add(content);
					content = new JSONObject();
					content.put("field", "possibleCauses");
					content.put("value", obj.get("possible_causes"));
					fieldMatcher.add(content);
					content = new JSONObject();
					content.put("field", "severity");
					content.put("value", obj.get("severity"));
					content = new JSONObject();
					fieldMatcher.add(content);
					content.put("field", "recommendation");
					content.put("value", obj.get("possible_solution"));
					fieldMatcher.add(content);
					content = new JSONObject();
					possible.put("fieldMatcher", fieldMatcher);
					action.setTemplateJson(possible);
					((ReadingRuleContext) alarmRule.getAlarmTriggerRule()).setActions(Collections.singletonList(action));
				}
			}
			FacilioContext facilioContext = new FacilioContext();

			facilioContext.put(FacilioConstants.ContextNames.ALARM_RULE, alarmRule);
			FacilioChain addRule = TransactionChainFactory.addAlarmRuleChain();
			addRule.execute(facilioContext);
			
			setResult("rule", alarmRule);
			return SUCCESS;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	private TaskSectionTemplate taskGroup;
	public TaskSectionTemplate getTaskGroup() {
		return taskGroup;
	}
	public void setTaskGroup(TaskSectionTemplate taskGroup) {
		this.taskGroup = taskGroup;
	}
	
	public String addTaskGroupTemplate() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TEMPLATE, taskGroup);
		
		FacilioChain addTaskGroup = FacilioChainFactory.addTaskGroupTemplateChain();
		addTaskGroup.execute(context);
		
		id = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		return SUCCESS;
	}
	
	public String updateTaskGroupTemplate() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, id);
		context.put(FacilioConstants.ContextNames.TEMPLATE, taskGroup);
		
		FacilioChain addTaskGroup = FacilioChainFactory.updateTaskGroupTemplateChain();
		addTaskGroup.execute(context);
		
		id = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		
		return SUCCESS;
	}
	
	public String fetchTemplate() throws Exception {
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.RECORD_ID, id);
		
		FacilioChain getTemplate = FacilioChainFactory.getTemplateChain();
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
		
		FacilioChain getTemplatesChain = FacilioChainFactory.getTemplatesOfTypeChain();
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
		
		FacilioChain addTemplate = FacilioChainFactory.getAddTemplateChain();
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
		
		FacilioChain addTemplate = FacilioChainFactory.getAddTemplateChain();
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
		
		FacilioChain addTemplate = FacilioChainFactory.getAddTemplateChain();
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
		
		FacilioChain addTemplate = FacilioChainFactory.getAddTemplateChain();
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
