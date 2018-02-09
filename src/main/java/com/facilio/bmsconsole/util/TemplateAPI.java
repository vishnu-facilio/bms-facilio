package com.facilio.bmsconsole.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.billing.context.ExcelTemplate;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.workflow.AssignmentTemplate;
import com.facilio.bmsconsole.workflow.EMailTemplate;
import com.facilio.bmsconsole.workflow.JSONTemplate;
import com.facilio.bmsconsole.workflow.PushNotificationTemplate;
import com.facilio.bmsconsole.workflow.SLATemplate;
import com.facilio.bmsconsole.workflow.SMSTemplate;
import com.facilio.bmsconsole.workflow.UserTemplate;
import com.facilio.bmsconsole.workflow.WebNotificationTemplate;
import com.facilio.fs.FileStoreFactory;
import com.facilio.sql.GenericDeleteRecordBuilder;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class TemplateAPI {
	
	public static List<ExcelTemplate> getAllExcelTemplates() throws Exception {
		List<FacilioField> fields = FieldFactory.getUserTemplateFields();
		fields.addAll(FieldFactory.getExcelTemplateFields());
		
		FacilioModule userTemplateModule = ModuleFactory.getTemplatesModule();
		FacilioModule excelTemplateModule = ModuleFactory.getExcelTemplatesModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
													  .select(fields)
													  .table(userTemplateModule.getTableName())
													  .innerJoin(excelTemplateModule.getTableName())
													  .on(userTemplateModule.getTableName()+".ID = "+excelTemplateModule.getTableName()+".ID")
													  .andCondition(CriteriaAPI.getCurrentOrgIdCondition(userTemplateModule));
		
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
		List<FacilioField> fields = FieldFactory.getUserTemplateFields();
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
	
	private static UserTemplate getExtendedTemplate(Map<String, Object> templateMap) throws Exception {
		UserTemplate.Type type = UserTemplate.Type.getType((int) templateMap.get("type"));
		long id = (long) templateMap.get("id");
		switch (type) {
			case EMAIL: {
				GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
						.select(FieldFactory.getEMailTemplateFields())
						.table("EMail_Templates")
						.andCustomWhere("EMail_Templates.ID = ?", id);
				
				List<Map<String, Object>> templates = selectBuider.get();
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getEMailTemplateFromMap(templateMap);
				}
			}break;
			case SMS: {
				GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
						.select(FieldFactory.getSMSTemplateFields())
						.table("SMS_Templates")
						.andCustomWhere("SMS_Templates.ID = ?", id);
				
				List<Map<String, Object>> templates = selectBuider.get();
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getSMSTemplateFromMap(templateMap);
				}
			}break;
			case PUSH_NOTIFICATION: {
				GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
						.select(FieldFactory.getPushNotificationTemplateFields())
						.table("Push_Notification_Templates")
						.andCustomWhere("Push_Notification_Templates.ID = ?", id);
				List<Map<String, Object>> templates = selectBuilder.get();
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getPushNotificationTemplateFromMap(templateMap);
				}
			}break;
			case WEB_NOTIFICATION: {
				GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
						.select(FieldFactory.getWebNotificationTemplateFields())
						.table("Web_Notification_Templates")
						.andCustomWhere("Web_Notification_Templates.ID = ?", id);
				List<Map<String, Object>> templates = selectBuilder.get();
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getWebNotificationTemplateFromMap(templateMap);
				}
			}break;
			case EXCEL: {
				GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
						.select(FieldFactory.getExcelTemplateFields())
						.table("Excel_Templates")
						.andCustomWhere("Excel_Templates.ID = ?", id);
				
				List<Map<String, Object>> templates = selectBuider.get();
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getExcelTemplateFromMap(templateMap);
				}
			}break;
			case ASSIGNMENT: {
				FacilioModule module = ModuleFactory.getAssignmentTemplatesModule();
				GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
						.select(FieldFactory.getAssignmentTemplateFields())
						.table(module.getTableName())
						.andCondition(CriteriaAPI.getIdCondition(id, module))
						;
				
				List<Map<String, Object>> templates = selectBuider.get();
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getAssignmentTemplateFromMap(templateMap);
				}
			}break;
			case SLA: {
				FacilioModule module = ModuleFactory.getSlaTemplatesModule();
				GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
						.select(FieldFactory.getSlaTemplateFields())
						.table(module.getTableName())
						.andCondition(CriteriaAPI.getIdCondition(id, module))
						;
				
				List<Map<String, Object>> templates = selectBuider.get();
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getSlaTemplateFromMap(templateMap);
				}
			}break;
			case JSON:
			case WORKORDER:
			case ALARM:
			case TASK_GROUP:
			{
				GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
						.select(FieldFactory.getJSONTemplateFields())
						.table("JSON_Template")
						.andCustomWhere("JSON_Template.ID = ?", id);
				
				List<Map<String, Object>> templates = selectBuider.get();
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getJSONTemplateFromMap(templateMap);
				}
			}break;
		}
		return null;
	}
 	
	public static UserTemplate getTemplate(long orgId, long id) throws Exception {
		GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
													.select(FieldFactory.getUserTemplateFields())
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
		FacilioModule module = ModuleFactory.getTemplatesModule();
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
													.table(module.getTableName())
													.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
													.andCondition(CriteriaAPI.getIdCondition(id, module));
		builder.delete();
	}
	
	public static UserTemplate getTemplate(long orgId, String templateName, UserTemplate.Type type) throws Exception {
		GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
													.select(FieldFactory.getUserTemplateFields())
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
															.fields(FieldFactory.getUserTemplateFields())
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
															.fields(FieldFactory.getUserTemplateFields())
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
															.fields(FieldFactory.getUserTemplateFields())
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
															.fields(FieldFactory.getUserTemplateFields())
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
															.fields(FieldFactory.getUserTemplateFields())
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
															.fields(FieldFactory.getUserTemplateFields())
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
		return addJsonTemplate(orgId, template, UserTemplate.Type.JSON);
	}
	
	public static long addWorkOrderTemplate(long orgId, JSONTemplate template) throws Exception {
		return addJsonTemplate(orgId, template, UserTemplate.Type.WORKORDER);
	}
	
	public static long addAlarmTemplate(long orgId, JSONTemplate template) throws Exception {
		return addJsonTemplate(orgId, template, UserTemplate.Type.ALARM);
	}
	
	public static long addTaskGroupTemplate(long orgId, JSONTemplate template) throws Exception {
		return addJsonTemplate(orgId, template, UserTemplate.Type.TASK_GROUP);
	}
	
	private static long addJsonTemplate(long orgId, JSONTemplate template, UserTemplate.Type type) throws Exception {
		User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
		
		template.setOrgId(orgId);
		template.setContentId((FileStoreFactory.getInstance().getFileStore(superAdmin.getId()).addFile("JSON_Template_"+template.getName(), template.getContent(), "text/plain")));
		template.setType(type);
		JSONArray placeholders = getPlaceholders(template);
		template.setPlaceholder(placeholders);
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
		
		GenericInsertRecordBuilder userTemplateBuilder = new GenericInsertRecordBuilder()
															.table("Templates")
															.fields(FieldFactory.getUserTemplateFields())
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
															.fields(FieldFactory.getUserTemplateFields())
															.addRecord(templateProps);
		
		userTemplateBuilder.save();
		
		GenericInsertRecordBuilder excelTemplateBuilder = new GenericInsertRecordBuilder()
																.table(ModuleFactory.getExcelTemplatesModule().getTableName())
																.fields(FieldFactory.getExcelTemplateFields())
																.addRecord(templateProps);
		excelTemplateBuilder.save();
		
		return (long) templateProps.get("id");
	}
	
	private static JSONArray getPlaceholders(UserTemplate template) {
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
