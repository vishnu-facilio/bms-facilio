package com.facilio.bmsconsole.util;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.billing.context.ExcelTemplate;
import com.facilio.bmsconsole.context.PMIncludeExcludeResourceContext;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.InputType;
import com.facilio.bmsconsole.context.TaskContext.TaskStatus;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.templates.AssignmentTemplate;
import com.facilio.bmsconsole.templates.DefaultTemplate;
import com.facilio.bmsconsole.templates.DefaultTemplateWorkflowsConf;
import com.facilio.bmsconsole.templates.DefaultTemplateWorkflowsConf.TemplateWorkflowConf;
import com.facilio.bmsconsole.templates.EMailTemplate;
import com.facilio.bmsconsole.templates.JSONTemplate;
import com.facilio.bmsconsole.templates.PushNotificationTemplate;
import com.facilio.bmsconsole.templates.SLATemplate;
import com.facilio.bmsconsole.templates.SMSTemplate;
import com.facilio.bmsconsole.templates.TaskSectionTemplate;
import com.facilio.bmsconsole.templates.TaskTemplate;
import com.facilio.bmsconsole.templates.Template;
import com.facilio.bmsconsole.templates.Template.Type;
import com.facilio.bmsconsole.templates.WebNotificationTemplate;
import com.facilio.bmsconsole.templates.WorkorderTemplate;
import com.facilio.constants.FacilioConstants;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.ParameterContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.util.WorkflowUtil;

public class TemplateAPI {
	private static Logger log = LogManager.getLogger(TemplateAPI.class.getName());

	private static final String[] LANG = new String[]{"en"};
	private static final Map<String, Map<Integer,DefaultTemplate>> DEFAULT_TEMPLATES = Collections.unmodifiableMap(loadDefaultTemplates());
	private static Map<String, Map<Integer,DefaultTemplate>> loadDefaultTemplates() {
		try {
			Map<String, Map<Integer,DefaultTemplate>> defaultTemplates = new HashMap<>();
			ClassLoader classLoader = TemplateAPI.class.getClassLoader();
			
			JAXBContext jaxbContext = JAXBContext.newInstance(DefaultTemplateWorkflowsConf.class);
			DefaultTemplateWorkflowsConf workflowsConf = (DefaultTemplateWorkflowsConf) jaxbContext.createUnmarshaller().unmarshal(new File(classLoader.getResource("conf/templates/templateWorkflows.xml").getFile()));
			Map<Integer, WorkflowContext> defaultWorkflows = new HashMap<>();
			for (TemplateWorkflowConf workflowConf : workflowsConf.getDefaultTemplatesWorkflows()) {
				WorkflowContext workflow = new WorkflowContext();
				workflow.setWorkflowString(workflowConf.getWorkflowXml());
				defaultWorkflows.put(workflowConf.getId(), workflow);
			}
			
			JSONParser parser = new JSONParser();
			for (String lang : LANG) {
				JSONObject templateJsons = (JSONObject) parser.parse(new FileReader(classLoader.getResource("conf/templates/defaultTemplates_"+lang+".json").getFile()));
				Map<Integer, DefaultTemplate> templates = new HashMap<>();
				for (Object key : templateJsons.keySet()) {
					Integer templateId = Integer.parseInt(key.toString());
					JSONObject template = (JSONObject) templateJsons.get(key);
					String name = (String) template.remove("name");
					DefaultTemplate defaultTemplate = new DefaultTemplate();
					defaultTemplate.setId(templateId);
					defaultTemplate.setName(name);
					defaultTemplate.setJson(template);
					defaultTemplate.setPlaceholder(getPlaceholders(defaultTemplate));
					
					WorkflowContext defaultWorkflow = defaultWorkflows.get(templateId);
					if (defaultWorkflow != null) {
						defaultWorkflow = WorkflowUtil.parseStringToWorkflowObject(defaultWorkflow.getWorkflowString());
						WorkflowUtil.parseExpression(defaultWorkflow);
						defaultTemplate.setWorkflow(defaultWorkflow);
					}
					else {
						throw new IllegalArgumentException("Workflow cannot be null for Default Template : "+templateId);
					}
					templates.put(templateId, defaultTemplate);
				}
				defaultTemplates.put(lang, templates);
			}
			return defaultTemplates;
		}
		catch (Exception e) {
			log.log(Level.ERROR, "Error in Parsing default templates",e);
			throw new IllegalArgumentException(e);
		}
	}
	
	private static String getLang() {
		return "en"; //This has to be changed according to org from thread local
	}
	
	public static DefaultTemplate getDefaultTemplate (int id) {
		return DEFAULT_TEMPLATES.get(getLang()).get(id);
	}
	
	public static long addTemplate(Template template) throws Exception {
		long id = -1;
		if(template instanceof EMailTemplate) {
			id = TemplateAPI.addEmailTemplate(AccountUtil.getCurrentOrg().getOrgId(), (EMailTemplate) template);
			template.setId(id);
		}
		else if(template instanceof SMSTemplate) {
			id = TemplateAPI.addSMSTemplate(AccountUtil.getCurrentOrg().getOrgId(), (SMSTemplate) template);
			template.setId(id);
		}
		else if(template instanceof PushNotificationTemplate) {
			id = TemplateAPI.addPushNotificationTemplate(AccountUtil.getCurrentOrg().getOrgId(), (PushNotificationTemplate) template);
			template.setId(id);
		}
		else if(template instanceof WebNotificationTemplate) {
			id = TemplateAPI.addWebNotificationTemplate(AccountUtil.getCurrentOrg().getOrgId(), (WebNotificationTemplate) template);
			template.setId(id);
		}
		else if(template instanceof JSONTemplate) {
			id = TemplateAPI.addJsonTemplate(AccountUtil.getCurrentOrg().getOrgId(), (JSONTemplate) template);
			template.setId(id);
		}
		else if (template instanceof AssignmentTemplate) {
			id = TemplateAPI.addAssignmentTemplate(AccountUtil.getCurrentOrg().getOrgId(), (AssignmentTemplate) template);
			template.setId(id);
		}
		else if (template instanceof SLATemplate) {
			id = TemplateAPI.addSlaTemplate(AccountUtil.getCurrentOrg().getOrgId(), (SLATemplate) template);
			template.setId(id);
		}
		else if (template instanceof WorkorderTemplate) {
			id = TemplateAPI.addWorkOrderTemplate((WorkorderTemplate) template);
		}
		return id;
	}
	
	public static List<Template> getTemplatesOfType(Type type) throws Exception {
		FacilioModule module = ModuleFactory.getTemplatesModule();
		List<FacilioField> fields = FieldFactory.getTemplateFields();
		FacilioField typeField = FieldFactory.getAsMap(fields).get("type");
		
		GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
														.andCondition(CriteriaAPI.getCondition(typeField, String.valueOf(type.getIntVal()), NumberOperators.EQUALS))
														;
		List<Map<String, Object>> props = selectBuider.get();
		if(props != null && !props.isEmpty()) {
			List<Template> templates = new ArrayList<>();
			Class<? extends Template> templateClass = getClassOfType(type);
			for (Map<String, Object> prop : props) {
				templates.add(FieldUtil.getAsBeanFromMap(prop, templateClass));
			}
			return templates;
		}
		return null;
	}
	
	private static Class<? extends Template> getClassOfType(Type type) {
		switch (type) {
			case EMAIL:
				return EMailTemplate.class;
			case SMS:
				return SMSTemplate.class;
			case JSON:
			case ALARM:
				return JSONTemplate.class;
			case EXCEL:
				return ExcelTemplate.class;
			case WORKORDER:
			case PM_WORKORDER:
				return WorkorderTemplate.class;
			case TASK_GROUP_TASK:
			case PM_TASK:
			case WO_TASK:
				return TaskTemplate.class;
			case TASK_GROUP:
			case PM_TASK_SECTION:
			case WO_TASK_SECTION:
				return TaskSectionTemplate.class;
			case PUSH_NOTIFICATION:
				return PushNotificationTemplate.class;
			case WEB_NOTIFICATION:
				return WebNotificationTemplate.class;
			case ASSIGNMENT:
				return AssignmentTemplate.class;
			case SLA:
				return SLATemplate.class;
			default:
				return null;
		}
	}
	
	public static Template getTemplate(long id) throws Exception {
		FacilioModule module = ModuleFactory.getTemplatesModule();
		GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
													.select(FieldFactory.getTemplateFields())
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getIdCondition(id, module))
													;
		
		List<Map<String, Object>> templates = selectBuider.get();
		
		if(templates != null && !templates.isEmpty()) {
			Map<String, Object> templateMap = templates.get(0);
			return getExtendedTemplate(templateMap);
		}
		return null;
	}
	
	public static List<ExcelTemplate> getAllExcelTemplates() throws Exception {
		List<FacilioField> fields = FieldFactory.getTemplateFields();
		fields.addAll(FieldFactory.getExcelTemplateFields());
		
		FacilioModule templateModule = ModuleFactory.getTemplatesModule();
		FacilioModule excelTemplateModule = ModuleFactory.getExcelTemplatesModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
													  .select(fields)
													  .table(templateModule.getTableName())
													  .innerJoin(excelTemplateModule.getTableName())
													  .on(templateModule.getTableName()+".ID = "+excelTemplateModule.getTableName()+".ID")
													  .andCondition(CriteriaAPI.getCurrentOrgIdCondition(templateModule));
		
		List<Map<String, Object>> templatePropList = selectBuilder.get();
		List<ExcelTemplate> excelTemplates = new ArrayList();
		for(int i=0;i<templatePropList.size();i++)
		{
			Map<String,Object> templateProps = templatePropList.get(i);
			excelTemplates.add(getExcelTemplateFromMap(templateProps));
			
		}
		return excelTemplates;
	}
	
	public static List<WorkorderTemplate> getAllWOTemplates() throws Exception {
		List<FacilioField> fields = FieldFactory.getTemplateFields();
		FacilioField typeField = FieldFactory.getAsMap(fields).get("type");
		fields.addAll(FieldFactory.getWorkOrderTemplateFields());
		
		
		FacilioModule templateModule = ModuleFactory.getTemplatesModule();
		FacilioModule woTemplateModule = ModuleFactory.getWorkOrderTemplateModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
													  .select(fields)
													  .table(templateModule.getTableName())
													  .innerJoin(woTemplateModule.getTableName())
													  .on(templateModule.getTableName()+".ID = "+woTemplateModule.getTableName()+".ID")
													  .andCondition(CriteriaAPI.getCurrentOrgIdCondition(templateModule))
													  .andCondition(CriteriaAPI.getCondition(typeField, String.valueOf(Type.WORKORDER.getIntVal()), NumberOperators.EQUALS))
													  ;
		
		List<Map<String, Object>> templatePropList = selectBuilder.get();
		if (templatePropList != null && !templatePropList.isEmpty()) {
			List<WorkorderTemplate> woTemplates = new ArrayList<>();
			List<Long> resourceIds = new ArrayList<>();
			for(Map<String,Object> templateProps : templatePropList)
			{
				WorkorderTemplate template = FieldUtil.getAsBeanFromMap(templateProps,WorkorderTemplate.class);
				woTemplates.add(template);
				if (template.getResourceId() != -1) {
					resourceIds.add(template.getResourceId());
				}
			}
			Map<Long, ResourceContext> resourceMap = ResourceAPI.getResourceAsMapFromIds(resourceIds);
			for (WorkorderTemplate template : woTemplates) {
				template.setResource(resourceMap.get(template.getResourceId()));
			}
			return woTemplates;
		}
		return null;
	}
	
	private static Template getExtendedTemplate(Map<String, Object> templateMap) throws Exception {
		Template.Type type = Template.Type.getType((int) templateMap.get("type"));
		long id = (long) templateMap.get("id");
		Template template = null;
		switch (type) {
			case EMAIL: {
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getEMailTemplatesModule(), FieldFactory.getEMailTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					template = getEMailTemplateFromMap(templateMap);
				}
			}break;
			case SMS: {
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getSMSTemplatesModule(), FieldFactory.getSMSTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					template = getSMSTemplateFromMap(templateMap);
				}
			}break;
			case PUSH_NOTIFICATION: {
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getPushNotificationTemplateModule(), FieldFactory.getPushNotificationTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					template = getPushNotificationTemplateFromMap(templateMap);
				}
			}break;
			case WEB_NOTIFICATION: {
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getWebNotificationTemplateModule(), FieldFactory.getWebNotificationTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					template = getWebNotificationTemplateFromMap(templateMap);
				}
			}break;
			case EXCEL: {
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getExcelTemplatesModule(), FieldFactory.getExcelTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					template = getExcelTemplateFromMap(templateMap);
				}
			}break;
			case ASSIGNMENT: {
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getAssignmentTemplatesModule(), FieldFactory.getAssignmentTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					template = getAssignmentTemplateFromMap(templateMap);
				}
			}break;
			case SLA: {
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getSlaTemplatesModule(), FieldFactory.getSlaTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					template = getSlaTemplateFromMap(templateMap);
				}
			}break;
			case WORKORDER:
			case PM_WORKORDER: {
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getWorkOrderTemplateModule(), FieldFactory.getWorkOrderTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					template = getWOTemplateFromMap(templateMap);
				}
			}break;
			case PM_TASK_SECTION:
			case WO_TASK_SECTION:
			case TASK_GROUP:{
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getTaskSectionTemplateModule(), FieldFactory.getTaskSectionTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					template = getTaskGroupTemplateFromMap(templateMap);
				}
			}break;
			case JSON:
			case ALARM: {
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getJSONTemplateModule(), FieldFactory.getJSONTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					template = getJSONTemplateFromMap(templateMap);
				}
			}break;
			default: break;
		}
		
		if (template != null && template.getWorkflowId() != -1) {
			template.setWorkflow(WorkflowUtil.getWorkflowContext(template.getWorkflowId(), true));
		}
		
		return template;
	}
 	
	private static List<Map<String, Object>> getExtendedProps(FacilioModule module, List<FacilioField> fields, long id) throws Exception {
		GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(id, module))
				;
		
		return selectBuider.get();
	}
	
	public static void deleteTemplate(long id) throws Exception {
		Template template = getTemplate(id);
		List<Long> ids = new ArrayList<>();
		ids.add(id);
		templatePreDelete(template, ids);
		FacilioModule module = ModuleFactory.getTemplatesModule();
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getIdCondition(ids, module));
		builder.delete();
		templatePostDelete(template);
	}
	
	private static void templatePreDelete(Template template, List<Long> ids) throws Exception {
		switch (template.getTypeEnum()) {
			case WORKORDER:
			case PM_WORKORDER:
				WorkorderTemplate woTemplate = (WorkorderTemplate) template;
				if (woTemplate.getTaskTemplates() != null) {
					for (TaskTemplate taskTemplate : woTemplate.getTaskTemplates()) {
						ids.add(taskTemplate.getId());
					}
				}
				if (woTemplate.getSectionTemplates() != null) {
					for (TaskSectionTemplate sectionTemplate : woTemplate.getSectionTemplates()) {
						ids.add(sectionTemplate.getId());
					}
				}
				break;
			case TASK_GROUP:
				TaskSectionTemplate sectionTemplate = (TaskSectionTemplate) template;
				if (sectionTemplate.getTaskTemplates() != null) {
					for (TaskTemplate taskTemplate : sectionTemplate.getTaskTemplates()) {
						ids.add(taskTemplate.getId());
					}
				}
				break;
			default: break;
		}
	}
	
	private static void templatePostDelete(Template template) throws Exception {
		if (template.getWorkflowId() != -1) {
			WorkflowUtil.deleteWorkflow(template.getWorkflowId());
		}
		switch (template.getTypeEnum()) {
			case ALARM:
			case JSON:
				deleteJSONTemplate((JSONTemplate)template);
				break;
			case EMAIL:
				deleteTemplateFile(((EMailTemplate)template).getBodyId());
				break;
			default: break;
		}
	}
	
	public static void deleteTemplates(List<Long> ids) throws Exception {
		for (Long id : ids) {
			deleteTemplate(id);
		}
	}
	
	private static void deleteJSONTemplate(JSONTemplate template) throws Exception {
		deleteTemplateFile(template.getContentId());
	}
	
	private static void deleteTemplateFile(Long contentId) throws Exception {
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		fs.deleteFile(contentId);
	}
	
	public static Template getTemplate(long orgId, String templateName, Template.Type type) throws Exception {
		GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
													.select(FieldFactory.getTemplateFields())
													.table("Templates")
													.andCustomWhere("Templates.ORGID = ? AND Templates.NAME = ? AND Templates.TEMPLATE_TYPE = ?", orgId, templateName, type.getIntVal());
		
		List<Map<String, Object>> templates = selectBuider.get();
		
		if(templates != null && !templates.isEmpty()) {
			Map<String, Object> templateMap = templates.get(0);
			return getExtendedTemplate(templateMap);
		}
		return null;
	}
	
	public static long addEmailTemplate(long orgId, EMailTemplate template) throws Exception {
		addDefaultProps(template);
		User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
		
		template.setType(Type.EMAIL);
		template.setBodyId(FileStoreFactory.getInstance().getFileStore(superAdmin.getId()).addFile("Email_Template_"+template.getName(), template.getMessage(), "text/plain"));
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
		GenericInsertRecordBuilder userTemplateBuilder = new GenericInsertRecordBuilder()
															.table("Templates")
															.fields(FieldFactory.getTemplateFields())
															.addRecord(templateProps);
		
		userTemplateBuilder.save();
		
		GenericInsertRecordBuilder emailTemplateBuilder = new GenericInsertRecordBuilder()
																.table("EMail_Templates")
																.fields(FieldFactory.getEMailTemplateFields())
																.addRecord(templateProps);
		emailTemplateBuilder.save();
		return (long) templateProps.get("id"); 
	}
	
	public static int updateEmailTemplate(long orgId, EMailTemplate template, long id) throws Exception {
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
		
		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
																.table("EMail_Templates")
																.fields(FieldFactory.getEMailTemplateFields())
																.andCustomWhere("ID = ?", id);
		
		return updateRecordBuilder.update(templateProps);
	}
	
	public static long addAssignmentTemplate(long orgId, AssignmentTemplate template) throws Exception {
		addDefaultProps(template);
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
		GenericInsertRecordBuilder userTemplateBuilder = new GenericInsertRecordBuilder()
															.table("Templates")
															.fields(FieldFactory.getTemplateFields())
															.addRecord(templateProps);
		
		userTemplateBuilder.save();
		
		GenericInsertRecordBuilder assignmentTemplateBuilder = new GenericInsertRecordBuilder()
																.table("Assignment_Templates")
																.fields(FieldFactory.getAssignmentTemplateFields())
																.addRecord(templateProps);
		assignmentTemplateBuilder.save();
		
		return (long) templateProps.get("id");
	}
	
	public static long addSlaTemplate(long orgId, SLATemplate template) throws Exception {
		addDefaultProps(template);
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
		GenericInsertRecordBuilder userTemplateBuilder = new GenericInsertRecordBuilder()
															.table("Templates")
															.fields(FieldFactory.getTemplateFields())
															.addRecord(templateProps);
		
		userTemplateBuilder.save();
		
		GenericInsertRecordBuilder slaTemplateBuilder = new GenericInsertRecordBuilder()
																.table("SLA_Templates")
																.fields(FieldFactory.getSlaTemplateFields())
																.addRecord(templateProps);
		slaTemplateBuilder.save();
		
		return (long) templateProps.get("id");
	}
	
	public static long addSMSTemplate(long orgId, SMSTemplate template) throws Exception {
		addDefaultProps(template);
		template.setType(Type.EMAIL);
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
		GenericInsertRecordBuilder userTemplateBuilder = new GenericInsertRecordBuilder()
															.table("Templates")
															.fields(FieldFactory.getTemplateFields())
															.addRecord(templateProps);
		
		userTemplateBuilder.save();
		
		GenericInsertRecordBuilder smsTemplateBuilder = new GenericInsertRecordBuilder()
																.table("SMS_Templates")
																.fields(FieldFactory.getSMSTemplateFields())
																.addRecord(templateProps);
		smsTemplateBuilder.save();
		
		return (long) templateProps.get("id");
	}
	
	public static int updateSMSTemplate(long orgId, SMSTemplate template, long id) throws Exception {
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
		
		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
																.table("SMS_Templates")
																.fields(FieldFactory.getSMSTemplateFields())
																.andCustomWhere("ID = ?", id);
		
		return updateRecordBuilder.update(templateProps);
	}
	
	public static long addPushNotificationTemplate(long orgId, PushNotificationTemplate template) throws Exception {
		addDefaultProps(template);
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
//		JSONArray placeholders = getPlaceholders(template);
//		template.setPlaceholder(placeholders);
		GenericInsertRecordBuilder userTemplateBuilder = new GenericInsertRecordBuilder()
															.table("Templates")
															.fields(FieldFactory.getTemplateFields())
															.addRecord(templateProps);
		
		userTemplateBuilder.save();
		
		GenericInsertRecordBuilder pushNotificationTemplateBuilder = new GenericInsertRecordBuilder()
																.table("Push_Notification_Templates")
																.fields(FieldFactory.getPushNotificationTemplateFields())
																.addRecord(templateProps);
		pushNotificationTemplateBuilder.save();
		
		return (long) templateProps.get("id");
	}
	
	public static long addWebNotificationTemplate(long orgId, WebNotificationTemplate template) throws Exception {
		addDefaultProps(template);
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
		GenericInsertRecordBuilder userTemplateBuilder = new GenericInsertRecordBuilder()
															.table("Templates")
															.fields(FieldFactory.getTemplateFields())
															.addRecord(templateProps);
		
		userTemplateBuilder.save();
		
		GenericInsertRecordBuilder pushNotificationTemplateBuilder = new GenericInsertRecordBuilder()
																.table("Web_Notification_Templates")
																.fields(FieldFactory.getWebNotificationTemplateFields())
																.addRecord(templateProps);
		pushNotificationTemplateBuilder.save();
		
		return (long) templateProps.get("id");
	}
	
	private static EMailTemplate getEMailTemplateFromMap(Map<String, Object> templateMap) throws Exception {
		EMailTemplate template = FieldUtil.getAsBeanFromMap(templateMap, EMailTemplate.class);
		
		User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
		
		try(InputStream body = FileStoreFactory.getInstance().getFileStore(superAdmin.getId()).readFile(template.getBodyId())) {
			template.setMessage(IOUtils.toString(body));
		}
		catch(Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
		
		return template;
	}
	
	private static SMSTemplate getSMSTemplateFromMap(Map<String, Object> templateMap) throws Exception {
		SMSTemplate template = FieldUtil.getAsBeanFromMap(templateMap, SMSTemplate.class);
		return template;
	}
	private static AssignmentTemplate getAssignmentTemplateFromMap(Map<String, Object> templateMap) throws Exception {
		AssignmentTemplate template = FieldUtil.getAsBeanFromMap(templateMap, AssignmentTemplate.class);
		return template;
	}
	private static SLATemplate getSlaTemplateFromMap(Map<String, Object> templateMap) throws Exception {
		SLATemplate template = FieldUtil.getAsBeanFromMap(templateMap, SLATemplate.class);
		return template;
	}
	private static PushNotificationTemplate getPushNotificationTemplateFromMap(Map<String, Object> templateMap) throws Exception {
		PushNotificationTemplate template = FieldUtil.getAsBeanFromMap(templateMap, PushNotificationTemplate.class);
		return template;
	}
	private static WebNotificationTemplate getWebNotificationTemplateFromMap(Map<String, Object> templateMap) throws Exception {
		WebNotificationTemplate template = FieldUtil.getAsBeanFromMap(templateMap, WebNotificationTemplate.class);
		return template;
	}
	
	private static ExcelTemplate getExcelTemplateFromMap(Map<String, Object> templateMap) throws Exception {
		ExcelTemplate template = FieldUtil.getAsBeanFromMap(templateMap, ExcelTemplate.class);
		AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
		//template.setWorkbook(WorkbookFactory.create(FileStoreFactory.getInstance().getFileStore(superAdmin.getId()).readFile(template.getExcelFileId())));
		return template;
	}
	
	private static WorkorderTemplate getWOTemplateFromMap(Map<String, Object> templateProps) throws Exception {
		WorkorderTemplate woTemplate = FieldUtil.getAsBeanFromMap(templateProps, WorkorderTemplate.class);
		
		Map<Long, TaskSectionTemplate> sectionMap = getTaskSectionTemplatesFromWOTemplate(woTemplate);
		woTemplate.setTasks(getTasksFromWOTemplate(woTemplate, sectionMap));
		
		return woTemplate;
	}
	
	private static Map<Long, TaskSectionTemplate> getTaskSectionTemplatesFromWOTemplate(WorkorderTemplate woTemplate) throws Exception {
		FacilioModule module = ModuleFactory.getTaskSectionTemplateModule();
		List<FacilioField> fields = FieldFactory.getTaskSectionTemplateFields();
		FacilioField parentIdField = FieldFactory.getAsMap(fields).get("parentWOTemplateId");
		List<Map<String, Object>> sectionProps = getTemplateJoinedProps(module, fields, CriteriaAPI.getCondition(parentIdField, String.valueOf(woTemplate.getId()), PickListOperators.IS));
		
		if (sectionProps != null && !sectionProps.isEmpty()) {
			Map<Long, TaskSectionTemplate> sections = new HashMap<>();
			List<TaskSectionTemplate> sectionTemplates = new ArrayList<>();
			for (Map<String, Object> prop : sectionProps) {
				TaskSectionTemplate sectionTemplate = FieldUtil.getAsBeanFromMap(prop, TaskSectionTemplate.class);
				sections.put(sectionTemplate.getId(), sectionTemplate);
				sectionTemplates.add(sectionTemplate);
			}
			woTemplate.setSectionTemplates(sectionTemplates);
			return sections;
		}
		return null;
	}
	
	private static Map<String, List<TaskContext>> getTasksFromWOTemplate(WorkorderTemplate woTemplate, Map<Long, TaskSectionTemplate> sectionMap) throws Exception {
		FacilioModule module = ModuleFactory.getTaskTemplateModule();
		List<FacilioField> fields = FieldFactory.getTaskTemplateFields();
		FacilioField parentIdField = FieldFactory.getAsMap(fields).get("parentTemplateId");
		List<Map<String, Object>> taskProps = getTemplateJoinedProps(module, fields, CriteriaAPI.getCondition(parentIdField, String.valueOf(woTemplate.getId()), PickListOperators.IS));
		
		if (taskProps != null && !taskProps.isEmpty()) {
			Map<String, List<TaskContext>> taskMap = new HashMap<>();
			List<TaskTemplate> taskTemplates = new ArrayList<>();
			for (Map<String, Object> prop : taskProps) {
				TaskTemplate template = FieldUtil.getAsBeanFromMap(prop, TaskTemplate.class);
				taskTemplates.add(template);
				
				TaskContext task = template.getTask();
				
				String sectionName = null;
				if (template.getSectionId() == -1) {
					sectionName = FacilioConstants.ContextNames.DEFAULT_TASK_SECTION;
				}
				else {
					TaskSectionTemplate sectionTemplate = sectionMap.get(template.getSectionId());
					sectionName = sectionTemplate.getName();
					sectionTemplate.getId();
					sectionTemplate.addTaskTemplates(template);
				}
				List<TaskContext> tasks = taskMap.get(sectionName);
				if (tasks == null) {
					tasks = new ArrayList<>();
					taskMap.put(sectionName, tasks);
				}
				tasks.add(task);
			}
			woTemplate.setTaskTemplates(taskTemplates);
			if(sectionMap != null) {
				woTemplate.setSectionTemplates(new ArrayList<>(sectionMap.values()));
			}
			
			return taskMap;
		}
		return null;
	}
	
	private static List<Map<String, Object>> getTemplateJoinedProps(FacilioModule extendedModule, List<FacilioField> extendedFields, Condition extraCondition) throws Exception {
		FacilioModule templateModule = ModuleFactory.getTemplatesModule();
		
		List<FacilioField> fields = FieldFactory.getTemplateFields();
		fields.addAll(extendedFields);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
													  .select(fields)
													  .table(templateModule.getTableName())
													  .innerJoin(extendedModule.getTableName())
													  .on(templateModule.getTableName()+".ID = "+extendedModule.getTableName()+".ID")
													  .andCondition(CriteriaAPI.getCurrentOrgIdCondition(templateModule));
		
		if (extraCondition != null) {
			selectBuilder.andCondition(extraCondition);
		}
		
		return selectBuilder.get();
	}
	
	private static TaskSectionTemplate getTaskGroupTemplateFromMap(Map<String, Object> templateMap) throws Exception {
		TaskSectionTemplate template = FieldUtil.getAsBeanFromMap(templateMap, TaskSectionTemplate.class);
		template.setPmIncludeExcludeResourceContexts(getPMIncludeExcludeList(null,template.getId(),null));
		template.setTasks(getTasksFromSection(template));
		return template;
	}
	
	private static List<TaskContext> getTasksFromSection(TaskSectionTemplate sectionTemplate) throws Exception {
		FacilioModule module = ModuleFactory.getTaskTemplateModule();
		List<FacilioField> fields = FieldFactory.getTaskTemplateFields();
		FacilioField sectionIdField = FieldFactory.getAsMap(fields).get("sectionId");
		List<Map<String, Object>> taskProps = getTemplateJoinedProps(module, fields, CriteriaAPI.getCondition(sectionIdField, String.valueOf(sectionTemplate.getId()), PickListOperators.IS));
		
		if (taskProps != null && !taskProps.isEmpty()) {
			List<TaskContext> tasks = new ArrayList<>();
			List<TaskTemplate> taskTemplates = new ArrayList<>();
			for (Map<String, Object> prop : taskProps) {
				TaskTemplate template = FieldUtil.getAsBeanFromMap(prop, TaskTemplate.class);
				template.setPmIncludeExcludeResourceContexts(getPMIncludeExcludeList(null,null,template.getId()));
				TaskContext task = template.getTask();
				tasks.add(task);
				taskTemplates.add(template);
			}
			sectionTemplate.setTaskTemplates(taskTemplates);
			return tasks;
		}
		return null;
	}
	
	public static List<PMIncludeExcludeResourceContext> getPMIncludeExcludeList(Long pmId,Long taskSectionId,Long taskId) throws Exception {

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder();
		builder.table(ModuleFactory.getPMIncludeExcludeResourceModule().getTableName())
		.select(FieldFactory.getPMIncludeExcludeResourceFields());
		
		if(pmId != null && pmId > 0) {
			builder.andCustomWhere("PM_ID = ? ", pmId);
			builder.andCustomWhere("PARENT_TYPE = ? ", PMIncludeExcludeResourceContext.ParentType.PM.getVal());
		}
		else if(taskSectionId != null && taskSectionId > 0) {
			builder.andCustomWhere("TASK_SECTION_TEMPLATE_ID = ? ", taskSectionId);
			builder.andCustomWhere("PARENT_TYPE = ? ", PMIncludeExcludeResourceContext.ParentType.TASK_SECTION_TEMPLATE.getVal());
		}
		else if(taskId != null && taskId > 0) {
			builder.andCustomWhere("TASK_TEMPLATE_ID = ? ", taskId);
			builder.andCustomWhere("PARENT_TYPE = ? ", PMIncludeExcludeResourceContext.ParentType.TASK_TEMPLATE.getVal());
		}
		List<Map<String, Object>> props = builder.get();
		
		List<PMIncludeExcludeResourceContext> pmIncludeExcludeResourceContexts = null;
		if(props != null && !props.isEmpty()) {
			pmIncludeExcludeResourceContexts = new ArrayList<>();
			for(Map<String, Object> prop :props) {
				PMIncludeExcludeResourceContext pmIncludeExcludeResourceContext = FieldUtil.getAsBeanFromMap(prop, PMIncludeExcludeResourceContext.class);
				pmIncludeExcludeResourceContexts.add(pmIncludeExcludeResourceContext);
			}
		}
		return pmIncludeExcludeResourceContexts;
	}
	
	private static JSONTemplate getJSONTemplateFromMap(Map<String, Object> templateMap) throws Exception {
		JSONTemplate template = FieldUtil.getAsBeanFromMap(templateMap, JSONTemplate.class);
		User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
		try(InputStream body = FileStoreFactory.getInstance().getFileStore(superAdmin.getId()).readFile(template.getContentId())) {
			template.setContent(IOUtils.toString(body));
		}
		catch(Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
		return template;
	}
	
	public static long addJsonTemplate(long orgId, JSONTemplate template) throws Exception {
		return addJsonTemplate(orgId, template, Template.Type.JSON);
	}
	
	private static TaskTemplate constructTaskTemplate(TaskContext task, long sectionId, long woTemplateId, Type type) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		TaskTemplate taskTemplate = new TaskTemplate();
		taskTemplate.setTask(task);
		taskTemplate.setSectionId(sectionId);
		taskTemplate.setParentTemplateId(woTemplateId);
		taskTemplate.setType(type);
		taskTemplate.setOrgId(AccountUtil.getCurrentOrg().getId());
		return taskTemplate;
	}
	
	private static TaskTemplate fillDefaultPropsTaskTemplate(TaskTemplate taskTemplate, long sectionId, long woTemplateId, Type type) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		taskTemplate.setSectionId(sectionId);
		taskTemplate.setParentTemplateId(woTemplateId);
		taskTemplate.setType(type);
		if(taskTemplate.getInputType() < 0) {
			taskTemplate.setInputType(InputType.NONE);
		}
		taskTemplate.setOrgId(AccountUtil.getCurrentOrg().getId());
		return taskTemplate;
	}
	
	public static long addTaskGroupTemplate (TaskSectionTemplate template) throws Exception {
		addDefaultProps(template);
		template.setType(Type.TASK_GROUP);
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
		long sectionId = insertTemplateWithExtendedProps(ModuleFactory.getTaskSectionTemplateModule(), FieldFactory.getTaskSectionTemplateFields(), templateProps);
		List<TaskContext> taskList = template.getTasks();
		List<Map<String, Object>> taskTemplateProps = new ArrayList<>();
		for (TaskContext task : taskList) {
			taskTemplateProps.add(FieldUtil.getAsProperties(constructTaskTemplate(task, sectionId, -1, Type.TASK_GROUP_TASK)));
		}
		insertTemplatesWithExtendedProps(ModuleFactory.getTaskTemplateModule(), FieldFactory.getTaskTemplateFields(), taskTemplateProps);
		return sectionId;
	}
	
	public static long addPMWorkOrderTemplate (WorkorderTemplate template) throws Exception {
		return addWorkOrderTemplate(template, Type.PM_WORKORDER, Type.PM_TASK, Type.PM_TASK_SECTION);
	}
	
	public static long addWorkOrderTemplate (WorkorderTemplate template) throws Exception {
		return addWorkOrderTemplate(template, Type.WORKORDER, Type.WO_TASK, Type.WO_TASK_SECTION);
	}
	
	private static long addWorkOrderTemplate(WorkorderTemplate template, Type woType, Type taskType, Type sectionType) throws Exception {
		addDefaultProps(template);
		template.setType(woType);
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
		long templateId = insertTemplateWithExtendedProps(ModuleFactory.getWorkOrderTemplateModule(), FieldFactory.getWorkOrderTemplateFields(), templateProps); //inserting WO template
		Map<String, List<TaskContext>> tasks = template.getTasks();
		if (tasks != null && !tasks.isEmpty()) {
			Map<String, Long> sectionMap = addSectionTemplatesForWO(templateId, tasks.keySet(), sectionType);	//add sections
			List<Map<String, Object>> taskTemplateProps = new ArrayList<>();
			for (Entry<String, List<TaskContext>> entry : tasks.entrySet()) {
				long sectionId = -1;
				String sectionName = entry.getKey(); 
				if (!sectionName.equals(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION)) {
					sectionId = sectionMap.get(sectionName);
				}
				List<TaskContext> taskList = entry.getValue();
				for (TaskContext task : taskList) {
					task.setStatusNew(TaskStatus.OPEN);
					taskTemplateProps.add(FieldUtil.getAsProperties(constructTaskTemplate(task, sectionId, templateId, taskType)));
				}
			}
			insertTemplatesWithExtendedProps(ModuleFactory.getTaskTemplateModule(), FieldFactory.getTaskTemplateFields(), taskTemplateProps); //add tasks
		}
		return templateId;
	}
	
	private static Map<String, Long> addSectionTemplatesForWO(List<TaskSectionTemplate> sectionTemplates,long woTemplateId, Type taskType,Type sectiontype) throws Exception {
		List<Map<String, Object>> templatePropList = new ArrayList<>();
		
		for (TaskSectionTemplate sectionTemplate : sectionTemplates) {
			//sectionTemplate.setType(sectiontype);
			sectionTemplate.setOrgId(AccountUtil.getCurrentOrg().getId());
			sectionTemplate.setParentWOTemplateId(woTemplateId);
			sectionTemplate.setType(Type.WO_TASK_SECTION);
			templatePropList.add(FieldUtil.getAsProperties(sectionTemplate));
		}
		insertTemplatesWithExtendedProps(ModuleFactory.getTaskSectionTemplateModule(), FieldFactory.getTaskSectionTemplateFields(), templatePropList);
		
		Map<String, Long> sectionMap = new HashMap<>();
		for (Map<String, Object> prop : templatePropList) {
			sectionMap.put((String)prop.get("name"), (Long)prop.get("id"));
		}
		
		
		for (TaskSectionTemplate sectionTemplate : sectionTemplates) {
			if(sectionMap.get(sectionTemplate.getName()) != null) {
				
				Long id = sectionMap.get(sectionTemplate.getName());
				sectionTemplate.setId(id);
				
				addIncludeExcludePropsForSection(sectionTemplate);
			}
			
			for(TaskTemplate taskTeplate : sectionTemplate.getTaskTemplates()) {
				fillDefaultPropsTaskTemplate(taskTeplate, sectionTemplate.getId(), woTemplateId, taskType);
				Map<String, Object> props = FieldUtil.getAsProperties(taskTeplate);
				props.remove("id");
				insertTemplateWithExtendedProps(ModuleFactory.getTaskTemplateModule(), FieldFactory.getTaskTemplateFields(),props); //add tasks
				
				taskTeplate.setId((Long)props.get("id"));
				
				addIncludeExcludePropsForTask(taskTeplate);
			}
		}
		return sectionMap;
	}
	
	public static void addIncludeExcludePropsForSection(TaskSectionTemplate sectionTemplate) throws Exception {
		
		if(sectionTemplate.getPmIncludeExcludeResourceContexts() != null && !sectionTemplate.getPmIncludeExcludeResourceContexts().isEmpty()) {
			
			List<Map<String, Object>> props = new ArrayList<>();
			for(PMIncludeExcludeResourceContext includeExcludeProps :sectionTemplate.getPmIncludeExcludeResourceContexts()) {
				includeExcludeProps.setTaskSectionTemplateId(sectionTemplate.getId());
				Map<String, Object> map = FieldUtil.getAsProperties(includeExcludeProps);
				props.add(map);
			}
			
			addPMIncludeExclude(props);
		}
	}
	
	public static void addIncludeExcludePropsForPM(PreventiveMaintenance pm) throws Exception {
		
		if(pm.getPmIncludeExcludeResourceContexts() != null && !pm.getPmIncludeExcludeResourceContexts().isEmpty()) {
			
			List<Map<String, Object>> props = new ArrayList<>();
			for(PMIncludeExcludeResourceContext includeExcludeProps :pm.getPmIncludeExcludeResourceContexts()) {
				includeExcludeProps.setPmId(pm.getId());
				Map<String, Object> map = FieldUtil.getAsProperties(includeExcludeProps);
				props.add(map);
			}
			
			addPMIncludeExclude(props);
		}
	}
	
	public static void addIncludeExcludePropsForTask(TaskTemplate taskTemplate) throws Exception {
		
		if(taskTemplate.getPmIncludeExcludeResourceContexts() != null && !taskTemplate.getPmIncludeExcludeResourceContexts().isEmpty()) {
			
			List<Map<String, Object>> props = new ArrayList<>();
			for(PMIncludeExcludeResourceContext includeExcludeProps :taskTemplate.getPmIncludeExcludeResourceContexts()) {
				includeExcludeProps.setTaskTemplateId(taskTemplate.getId());
				Map<String, Object> map = FieldUtil.getAsProperties(includeExcludeProps);
				props.add(map);
			}
			addPMIncludeExclude(props);
		}
	}
	
	private static void addPMIncludeExclude(List<Map<String, Object>> props)  throws Exception {
		FacilioModule includeExcludeModule = ModuleFactory.getPMIncludeExcludeResourceModule();
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(includeExcludeModule.getTableName())
				.fields(FieldFactory.getPMIncludeExcludeResourceFields())
				.addRecords(props);

		insertBuilder.save();
	}

	public static long addNewWorkOrderTemplate(WorkorderTemplate template, Type woType, Type taskType, Type sectionType) throws Exception {
		addDefaultProps(template);
		template.setType(woType);
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
		long templateId = insertTemplateWithExtendedProps(ModuleFactory.getWorkOrderTemplateModule(), FieldFactory.getWorkOrderTemplateFields(), templateProps); //inserting WO template
		
		addSectionTemplatesForWO(template.getSectionTemplates(), templateId,taskType, sectionType);
		return templateId;
	}
	
	private static Map<String, Long> addSectionTemplatesForWO(long woId, Set<String> sectionNames, Type sectionType) throws Exception {
		List<Map<String, Object>> templatePropList = new ArrayList<>();
		for (String section : sectionNames) {
			if (!section.equals(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION)) {
				TaskSectionTemplate sectionTemplate = new TaskSectionTemplate();
				sectionTemplate.setName(section);
				sectionTemplate.setParentWOTemplateId(woId);
				sectionTemplate.setType(sectionType);
				sectionTemplate.setOrgId(AccountUtil.getCurrentOrg().getId());
				templatePropList.add(FieldUtil.getAsProperties(sectionTemplate));
			}
		}
		insertTemplatesWithExtendedProps(ModuleFactory.getTaskSectionTemplateModule(), FieldFactory.getTaskSectionTemplateFields(), templatePropList);
		Map<String, Long> sectionMap = new HashMap<>();
		for (Map<String, Object> prop : templatePropList) {
			sectionMap.put((String)prop.get("name"), (Long)prop.get("id"));
		}
		return sectionMap;
	}
	
	private static long insertTemplateWithExtendedProps(FacilioModule extendedModule, List<FacilioField> fields, Map<String, Object> templateProps) throws SQLException, RuntimeException {
		insertTemplatesWithExtendedProps(extendedModule, fields, Collections.singletonList(templateProps));
		return (long) templateProps.get("id"); 
	}
	
	private static void insertTemplatesWithExtendedProps(FacilioModule extendedModule, List<FacilioField> fields, List<Map<String, Object>> templatePropList) throws SQLException, RuntimeException {
		GenericInsertRecordBuilder userTemplateBuilder = new GenericInsertRecordBuilder()
				.table("Templates")
				.fields(FieldFactory.getTemplateFields())
				.addRecords(templatePropList);

		userTemplateBuilder.save();
		
		GenericInsertRecordBuilder workorderTemplateBuilder = new GenericInsertRecordBuilder()
							.table(extendedModule.getTableName())
							.fields(fields)
							.addRecords(templatePropList);
		workorderTemplateBuilder.save();
	}
	
	public static long addAlarmTemplate(long orgId, JSONTemplate template) throws Exception {
		return addJsonTemplate(orgId, template, Template.Type.ALARM);
	}
	
	private static long addJsonTemplate(long orgId, JSONTemplate template, Template.Type type) throws Exception {
		addDefaultProps(template);
		User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
		template.setContentId((FileStoreFactory.getInstance().getFileStore(superAdmin.getId()).addFile("JSON_Template_"+template.getName(), template.getContent(), "text/plain")));
		template.setType(type);
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
		
		GenericInsertRecordBuilder userTemplateBuilder = new GenericInsertRecordBuilder()
															.table("Templates")
															.fields(FieldFactory.getTemplateFields())
															.addRecord(templateProps);
		
		userTemplateBuilder.save();
		
		GenericInsertRecordBuilder workorderTemplateBuilder = new GenericInsertRecordBuilder()
																.table("JSON_Template")
																.fields(FieldFactory.getJSONTemplateFields())
																.addRecord(templateProps);
		workorderTemplateBuilder.save();
		return (long) templateProps.get("id"); 
	}
	
	public static long addExcelTemplate(long orgId, ExcelTemplate template, String fileName) throws Exception {
		addDefaultProps(template);
		User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
		template.setExcelFileId(FileStoreFactory.getInstance().getFileStore(superAdmin.getId()).addFile(fileName, template.getExcelFile(), "application/xlsx"));
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
		GenericInsertRecordBuilder userTemplateBuilder = new GenericInsertRecordBuilder()
															.table("Templates")
															.fields(FieldFactory.getTemplateFields())
															.addRecord(templateProps);
		
		userTemplateBuilder.save();
		
		GenericInsertRecordBuilder excelTemplateBuilder = new GenericInsertRecordBuilder()
																.table(ModuleFactory.getExcelTemplatesModule().getTableName())
																.fields(FieldFactory.getExcelTemplateFields())
																.addRecord(templateProps);
		excelTemplateBuilder.save();
		
		return (long) templateProps.get("id");
	}
	
	private static void addDefaultProps(Template template) throws Exception {
		template.setOrgId(AccountUtil.getCurrentOrg().getId());
		JSONArray placeholders = getPlaceholders(template);
		template.setPlaceholder(placeholders);
		
		if (placeholders != null && !placeholders.isEmpty()) {
			if (template.getWorkflow() == null && template.getWorkflowId() == -1) {
				throw new IllegalArgumentException("Workflow cannot be null if placeholders are present for a template.");
			}
			template.setWorkflowId(WorkflowUtil.addWorkflow(template.getWorkflow()));
		}
	}
	
	public static JSONArray getPlaceholders(Template template) throws Exception {
		String formatSpecifier = "(\\$\\{([^\\:}]*))";
		Pattern pattern = Pattern.compile(formatSpecifier);
		JSONObject templateString = template.getOriginalTemplate();
		if (templateString != null) {
			Matcher matcher = pattern.matcher(templateString.toJSONString());
			JSONArray templatePlaceholder = new JSONArray();
			while (matcher.find()) {
				String placeHolder = matcher.group(2);
				if (!templatePlaceholder.contains(placeHolder)) {
					templatePlaceholder.add(placeHolder);
				}
			}
			return templatePlaceholder;
		}
		return null;
		
	}
	
	public static WorkflowContext getWorkflow(Template template) throws Exception {
		JSONArray placeHolders = getPlaceholders(template);
		if (placeHolders != null && !placeHolders.isEmpty()) {
			List<ParameterContext> params = new ArrayList<>();
			WorkflowContext workflow = new WorkflowContext();
			for (int i = 0; i < placeHolders.size(); i++) {
				String placeHolder = (String) placeHolders.get(i);
				ParameterContext param = new ParameterContext();
				param.setName(placeHolder);
				param.setTypeString("String"); //By default string for now
				params.add(param);
				
				ExpressionContext expression = new ExpressionContext();
				expression.setName(placeHolder);
				expression.setConstant("${"+placeHolder+"}");
				workflow.addWorkflowExpression(expression);
			}
			workflow.setParameters(params);
			return workflow;
		}
		return null;
	}
	
}
