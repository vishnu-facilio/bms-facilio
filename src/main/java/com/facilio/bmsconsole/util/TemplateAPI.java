package com.facilio.bmsconsole.util;

import java.io.InputStream;
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

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.billing.context.ExcelTemplate;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.templates.AssignmentTemplate;
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

public class TemplateAPI {
	
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
	
	public static List<JSONTemplate> getAllWOTemplates() throws Exception {
		List<FacilioField> fields = FieldFactory.getTemplateFields();
		fields.addAll(FieldFactory.getJSONTemplateFields());
		
		FacilioModule userTemplateModule = ModuleFactory.getTemplatesModule();
		FacilioModule woTemplateModule = ModuleFactory.getJSONTemplateModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
													  .select(fields)
													  .table(userTemplateModule.getTableName())
													  .innerJoin(woTemplateModule.getTableName())
													  .on(userTemplateModule.getTableName()+".ID = "+woTemplateModule.getTableName()+".ID")
													  .andCondition(CriteriaAPI.getCurrentOrgIdCondition(userTemplateModule));
		
		List<Map<String, Object>> templatePropList = selectBuilder.get();
		List<JSONTemplate> woTemplates = new ArrayList<JSONTemplate>();
		for(int i=0;i<templatePropList.size();i++)
		{
			Map<String,Object> templateProps = templatePropList.get(i);
			JSONTemplate template = FieldUtil.getAsBeanFromMap(templateProps,JSONTemplate.class);
			woTemplates.add(template);
			
		}
		return woTemplates;
	}
	
	private static Template getExtendedTemplate(Map<String, Object> templateMap) throws Exception {
		Template.Type type = Template.Type.getType((int) templateMap.get("type"));
		long id = (long) templateMap.get("id");
		switch (type) {
			case EMAIL: {
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getEMailTemplatesModule(), FieldFactory.getEMailTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getEMailTemplateFromMap(templateMap);
				}
			}break;
			case SMS: {
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getSMSTemplatesModule(), FieldFactory.getSMSTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getSMSTemplateFromMap(templateMap);
				}
			}break;
			case PUSH_NOTIFICATION: {
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getPushNotificationTemplateModule(), FieldFactory.getPushNotificationTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getPushNotificationTemplateFromMap(templateMap);
				}
			}break;
			case WEB_NOTIFICATION: {
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getWebNotificationTemplateModule(), FieldFactory.getWebNotificationTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getWebNotificationTemplateFromMap(templateMap);
				}
			}break;
			case EXCEL: {
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getExcelTemplatesModule(), FieldFactory.getExcelTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getExcelTemplateFromMap(templateMap);
				}
			}break;
			case ASSIGNMENT: {
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getAssignmentTemplatesModule(), FieldFactory.getAssignmentTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getAssignmentTemplateFromMap(templateMap);
				}
			}break;
			case SLA: {
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getSlaTemplatesModule(), FieldFactory.getSlaTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getSlaTemplateFromMap(templateMap);
				}
			}break;
			case WORKORDER:
			case PM_WORKORDER: {
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getWorkOrderTemplateModule(), FieldFactory.getWorkOrderTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getWOTemplateFromMap(templateMap);
				}
			}break;
			case TASK_GROUP:{
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getTaskSectionModule(), FieldFactory.getTaskSectionTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getTaskGroupTemplateFromMap(templateMap);
				}
			}break;
			case JSON:
			case ALARM: {
				List<Map<String, Object>> templates = getExtendedProps(ModuleFactory.getJSONTemplateModule(), FieldFactory.getJSONTemplateFields(), id);
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getJSONTemplateFromMap(templateMap);
				}
			}break;
			default: break;
		}
		return null;
	}
 	
	private static List<Map<String, Object>> getExtendedProps(FacilioModule module, List<FacilioField> fields, long id) throws Exception {
		GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getIdCondition(id, module))
				;
		
		return selectBuider.get();
	}
	
	public static Template getTemplate(long orgId, long id) throws Exception {
		GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
													.select(FieldFactory.getTemplateFields())
													.table("Templates")
													.andCustomWhere("Templates.ORGID = ? AND Templates.ID = ?", orgId, id);
		
		List<Map<String, Object>> templates = selectBuider.get();
		
		if(templates != null && !templates.isEmpty()) {
			Map<String, Object> templateMap = templates.get(0);
			return getExtendedTemplate(templateMap);
		}
		return null;
	}
	
	public static void deleteTemplate(long id) throws Exception {
		Template template = getTemplate(AccountUtil.getCurrentOrg().getId(), id);
		
		switch (template.getTypeEnum()) {
			case ALARM:
			case JSON:
				deleteJSONTemplate((JSONTemplate)template);
				break;
			default: break;
		}
		
		FacilioModule module = ModuleFactory.getTemplatesModule();
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getIdCondition(id, module));
		builder.delete();
	}
	
	public static void deleteTemplates(List<Long> ids) throws Exception {
		for (Long id : ids) {
			deleteTemplate(id);
		}
	}
	
	private static void deleteJSONTemplate(JSONTemplate template) throws Exception {
		FileStore fs = FileStoreFactory.getInstance().getFileStore();
		fs.deleteFile(template.getContentId());
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
		
		User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
		
		template.setOrgId(orgId);
		template.setBodyId(FileStoreFactory.getInstance().getFileStore(superAdmin.getId()).addFile("Email_Template_"+template.getName(), template.getMessage(), "text/plain"));
		JSONArray placeholders = getPlaceholders(template);
		template.setPlaceholder(placeholders);
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
		template.setOrgId(orgId);
		JSONArray placeholders = getPlaceholders(template);
		template.setPlaceholder(placeholders);
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
		template.setOrgId(orgId);
		JSONArray placeholders = getPlaceholders(template);
		template.setPlaceholder(placeholders);	
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
		template.setOrgId(orgId);
		JSONArray placeholders = getPlaceholders(template);
		template.setPlaceholder(placeholders);
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
		template.setOrgId(orgId);
		JSONArray placeholders = getPlaceholders(template);
		template.setPlaceholder(placeholders);
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
	
	public static int updatePushNotificationTemplate(long orgId, PushNotificationTemplate template, long id) throws Exception {
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
		
		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
																.table("Push_Notification_Templates")
																.fields(FieldFactory.getPushNotificationTemplateFields())
																.andCustomWhere("ID = ?", id);
		
		return updateRecordBuilder.update(templateProps);
	}
	
	public static long addWebNotificationTemplate(long orgId, WebNotificationTemplate template) throws Exception {
		template.setOrgId(orgId);
		JSONArray placeholders = getPlaceholders(template);
		template.setPlaceholder(placeholders);
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
	
	public static int updateWebNotificationTemplate(long orgId, WebNotificationTemplate template, long id) throws Exception {
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
		
		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
																.table("Web_Notification_Templates")
																.fields(FieldFactory.getWebNotificationTemplateFields())
																.andCustomWhere("ID = ?", id);
		
		return updateRecordBuilder.update(templateProps);
	}
	
	private static EMailTemplate getEMailTemplateFromMap(Map<String, Object> templateMap) throws Exception {
		EMailTemplate template = FieldUtil.getAsBeanFromMap(templateMap, EMailTemplate.class);
		
		User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
		
		try(InputStream body = FileStoreFactory.getInstance().getFileStore(superAdmin.getId()).readFile(template.getBodyId())) {
			template.setMessage(IOUtils.toString(body));
		}
		catch(Exception e) {
			e.printStackTrace();
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
		User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
		//template.setWorkbook(WorkbookFactory.create(FileStoreFactory.getInstance().getFileStore(superAdmin.getId()).readFile(template.getExcelFileId())));
		return template;
	}
	
	private static WorkorderTemplate getWOTemplateFromMap(Map<String, Object> templateProps) throws Exception {
		WorkorderTemplate woTemplate = FieldUtil.getAsBeanFromMap(templateProps, WorkorderTemplate.class);
		
		Map<Long, TaskSectionTemplate> sectionMap = getTaskSectionTemplatesFromWOTemplate(woTemplate.getId());
		woTemplate.setTasks(getTasksFromWOTemplate(woTemplate.getId(), sectionMap));
		
		return woTemplate;
	}
	
	private static Map<Long, TaskSectionTemplate> getTaskSectionTemplatesFromWOTemplate(long woId) throws Exception {
		FacilioModule module = ModuleFactory.getTaskSectionTemplateModule();
		List<FacilioField> fields = FieldFactory.getTaskSectionTemplateFields();
		FacilioField parentIdField = FieldFactory.getAsMap(fields).get("parentWOTemplateId");
		List<Map<String, Object>> sectionProps = getTemplateJoinedProps(module, fields, CriteriaAPI.getCondition(parentIdField, String.valueOf(woId), PickListOperators.IS));
		
		if (sectionProps != null && !sectionProps.isEmpty()) {
			Map<Long, TaskSectionTemplate> sections = new HashMap<>();
			for (Map<String, Object> prop : sectionProps) {
				TaskSectionTemplate sectionTemplate = FieldUtil.getAsBeanFromMap(prop, TaskSectionTemplate.class);
				sections.put(sectionTemplate.getId(), sectionTemplate);
			}
			return sections;
		}
		return null;
	}
	
	private static Map<String, List<TaskContext>> getTasksFromWOTemplate(long woId, Map<Long, TaskSectionTemplate> sectionMap) throws Exception {
		FacilioModule module = ModuleFactory.getTaskTemplateModule();
		List<FacilioField> fields = FieldFactory.getTaskTemplateFields();
		FacilioField parentIdField = FieldFactory.getAsMap(fields).get("parentTemplateId");
		List<Map<String, Object>> taskProps = getTemplateJoinedProps(module, fields, CriteriaAPI.getCondition(parentIdField, String.valueOf(woId), PickListOperators.IS));
		
		if (taskProps != null && !taskProps.isEmpty()) {
			Map<String, List<TaskContext>> taskMap = new HashMap<>();
			for (Map<String, Object> prop : taskProps) {
				TaskTemplate template = FieldUtil.getAsBeanFromMap(prop, TaskTemplate.class);
				TaskContext task = template.getTask();
				
				String sectionName = null;
				if (template.getSectionId() == -1) {
					sectionName = FacilioConstants.ContextNames.DEFAULT_TASK_SECTION;
				}
				else {
					TaskSectionTemplate sectionTemplate = sectionMap.get(template.getSectionId());
					sectionName = sectionTemplate.getName();
				}
				
				List<TaskContext> tasks = taskMap.get(sectionName);
				if (tasks == null) {
					tasks = new ArrayList<>();
					taskMap.put(sectionName, tasks);
				}
				tasks.add(task);
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
		template.setTasks(getTasksFromSection(template.getId()));
		return template;
	}
	
	private static List<TaskContext> getTasksFromSection(long sectionId) throws Exception {
		FacilioModule module = ModuleFactory.getTaskTemplateModule();
		List<FacilioField> fields = FieldFactory.getTaskTemplateFields();
		FacilioField sectionIdField = FieldFactory.getAsMap(fields).get("sectionId");
		List<Map<String, Object>> taskProps = getTemplateJoinedProps(module, fields, CriteriaAPI.getCondition(sectionIdField, String.valueOf(sectionId), PickListOperators.IS));
		
		if (taskProps != null && !taskProps.isEmpty()) {
			List<TaskContext> tasks = new ArrayList<>();
			for (Map<String, Object> prop : taskProps) {
				TaskTemplate template = FieldUtil.getAsBeanFromMap(prop, TaskTemplate.class);
				TaskContext task = template.getTask();
				tasks.add(task);
			}
			return tasks;
		}
		return null;
	}
	
	private static JSONTemplate getJSONTemplateFromMap(Map<String, Object> templateMap) throws Exception {
		JSONTemplate template = FieldUtil.getAsBeanFromMap(templateMap, JSONTemplate.class);
		User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
		try(InputStream body = FileStoreFactory.getInstance().getFileStore(superAdmin.getId()).readFile(template.getContentId())) {
			template.setContent(IOUtils.toString(body));
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return template;
	}
	
	public static long addJsonTemplate(long orgId, JSONTemplate template) throws Exception {
		return addJsonTemplate(orgId, template, Template.Type.JSON);
	}
	
	public static long addPMWorkOrderTemplate (long orgId, WorkorderTemplate template) throws Exception {
		return addWorkOrderTemplate(orgId, template, Type.PM_WORKORDER, Type.PM_TASK, Type.PM_TASK_SECTION);
	}
	
	public static long addWorkOrderTemplate (long orgId, WorkorderTemplate template) throws Exception {
		return addWorkOrderTemplate(orgId, template, Type.WORKORDER, Type.WO_TASK, Type.WO_TASK_SECTION);
	}
	
	private static long addWorkOrderTemplate(long orgId, WorkorderTemplate template, Type woType, Type taskType, Type sectionType) throws Exception {
		template.setType(woType);
		template.setOrgId(AccountUtil.getCurrentOrg().getId());
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
		long templateId = insertTemplateWithExtendedProps(ModuleFactory.getWorkOrderTemplateModule(), FieldFactory.getWorkOrderTemplateFields(), templateProps);
		Map<String, List<TaskContext>> tasks = template.getTasks();
		if (tasks != null && !tasks.isEmpty()) {
			Map<String, Long> sectionMap = addSectionTemplatesForWO(templateId, tasks.keySet(), sectionType);
			List<Map<String, Object>> taskTemplateProps = new ArrayList<>();
			for (Entry<String, List<TaskContext>> entry : tasks.entrySet()) {
				long sectionId = -1;
				String sectionName = entry.getKey(); 
				if (!sectionName.equals(FacilioConstants.ContextNames.DEFAULT_TASK_SECTION)) {
					sectionId = sectionMap.get(sectionName);
				}
				List<TaskContext> taskList = entry.getValue();
				for (TaskContext task : taskList) {
					TaskTemplate taskTemplate = new TaskTemplate();
					taskTemplate.setTask(task);
					taskTemplate.setSectionId(sectionId);
					taskTemplate.setParentTemplateId(templateId);
					taskTemplate.setType(taskType);
					taskTemplate.setOrgId(AccountUtil.getCurrentOrg().getId());
					taskTemplateProps.add(FieldUtil.getAsProperties(taskTemplate));
				}
			}
			insertTemplatesWithExtendedProps(ModuleFactory.getTaskTemplateModule(), FieldFactory.getTaskTemplateFields(), taskTemplateProps);
		}
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
	
	public static long addTaskGroupTemplate(long orgId, TaskSectionTemplate template) throws Exception {
		template.setType(Type.TASK_GROUP);
		template.setOrgId(AccountUtil.getCurrentOrg().getId());
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
		long templateId = insertTemplateWithExtendedProps(ModuleFactory.getTaskSectionTemplateModule(), FieldFactory.getTaskSectionTemplateFields(), templateProps);
		List<TaskContext> tasks = template.getTasks();
		
		List<Map<String, Object>> taskTemplateProps = new ArrayList<>();
		for (TaskContext task : tasks) {
			TaskTemplate taskTemplate = new TaskTemplate();
			taskTemplate.setTask(task);
			taskTemplate.setSectionId(templateId);
			taskTemplate.setType(Type.TASK_GROUP_TASK);
			taskTemplate.setOrgId(AccountUtil.getCurrentOrg().getId());
			taskTemplateProps.add(FieldUtil.getAsProperties(taskTemplate));
		}
		insertTemplatesWithExtendedProps(ModuleFactory.getTaskTemplateModule(), FieldFactory.getTaskTemplateFields(), taskTemplateProps);
		
		return templateId;
	}
	
	private static long addJsonTemplate(long orgId, JSONTemplate template, Template.Type type) throws Exception {
		User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
		
		template.setOrgId(orgId);
		template.setContentId((FileStoreFactory.getInstance().getFileStore(superAdmin.getId()).addFile("JSON_Template_"+template.getName(), template.getContent(), "text/plain")));
		template.setType(type);
		JSONArray placeholders = getPlaceholders(template);
		template.setPlaceholder(placeholders);
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
		User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
		
		template.setOrgId(orgId);
		template.setExcelFileId(FileStoreFactory.getInstance().getFileStore(superAdmin.getId()).addFile(fileName, template.getExcelFile(), "application/xlsx"));
		
		JSONArray placeholders = getPlaceholders(template);
		template.setPlaceholder(placeholders);
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
	
	private static JSONArray getPlaceholders(Template template) {
		String formatSpecifier = "(\\$\\{([^\\:}]*))";
		Pattern pattern = Pattern.compile(formatSpecifier);
		JSONObject templateString = template.getOriginalTemplate();
		if (templateString != null) {
			Matcher matcher = pattern.matcher(templateString.toJSONString());
			JSONArray templatePlaceholder = new JSONArray();
			while (matcher.find()) {
				templatePlaceholder.add(matcher.group(2));
			}
			return templatePlaceholder;
		}
		return null;
		
	}
	
}
