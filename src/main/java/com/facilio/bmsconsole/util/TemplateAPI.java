package com.facilio.bmsconsole.util;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.billing.context.ExcelTemplate;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.workflow.EMailTemplate;
import com.facilio.bmsconsole.workflow.JSONTemplate;
import com.facilio.bmsconsole.workflow.SMSTemplate;
import com.facilio.bmsconsole.workflow.UserTemplate;
import com.facilio.fs.FileStoreFactory;
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
 	
	public static UserTemplate getTemplate(long orgId, long id) throws Exception {
		GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
													.select(FieldFactory.getUserTemplateFields())
													.table("Templates")
													.andCustomWhere("Templates.ORGID = ? AND Templates.ID = ?", orgId, id);
		
		List<Map<String, Object>> templates = selectBuider.get();
		
		if(templates != null && !templates.isEmpty()) {
			Map<String, Object> templateMap = templates.get(0);
			int type = (int) templateMap.get("type");
			if(type == UserTemplate.Type.EMAIL.getIntVal()) {
				selectBuider = new GenericSelectRecordBuilder()
						.select(FieldFactory.getEMailTemplateFields())
						.table("EMail_Templates")
						.andCustomWhere("EMail_Templates.ID = ?", id);
				
				templates = selectBuider.get();
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getEMailTemplateFromMap(templateMap);
				}
			}
			else if(type == UserTemplate.Type.SMS.getIntVal()) {
				selectBuider = new GenericSelectRecordBuilder()
						.select(FieldFactory.getSMSTemplateFields())
						.table("SMS_Templates")
						.andCustomWhere("SMS_Templates.ID = ?", id);
				
				templates = selectBuider.get();
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getSMSTemplateFromMap(templateMap);
				}
			}
			else if(type == UserTemplate.Type.JSON.getIntVal()) {
				selectBuider = new GenericSelectRecordBuilder()
						.select(FieldFactory.getJSONTemplateFields())
						.table("JSON_Template")
						.andCustomWhere("JSON_Template.ID = ?", id);
				
				templates = selectBuider.get();
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getJSONTemplateFromMap(templateMap);
				}
			}
			else if(type == UserTemplate.Type.EXCEL.getIntVal()) {
				selectBuider = new GenericSelectRecordBuilder()
						.select(FieldFactory.getExcelTemplateFields())
						.table("Excel_Templates")
						.andCustomWhere("Excel_Templates.ID = ?", id);
				
				templates = selectBuider.get();
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getExcelTemplateFromMap(templateMap);
				}
			}
		}
		return null;
	}
	
	public static UserTemplate getTemplate(long orgId, String templateName, UserTemplate.Type type) throws Exception {
		GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
													.select(FieldFactory.getUserTemplateFields())
													.table("Templates")
													.andCustomWhere("Templates.ORGID = ? AND Templates.NAME = ? AND Templates.TEMPLATE_TYPE = ?", orgId, templateName, type.getIntVal());
		
		List<Map<String, Object>> templates = selectBuider.get();
		
		if(templates != null && !templates.isEmpty()) {
			Map<String, Object> templateMap = templates.get(0);
			int templateType = (int) templateMap.get("type");
			long id = (long) templateMap.get("id");
			if(templateType == UserTemplate.Type.EMAIL.getIntVal()) {
				selectBuider = new GenericSelectRecordBuilder()
						.select(FieldFactory.getEMailTemplateFields())
						.table("EMail_Templates")
						.andCustomWhere("EMail_Templates.ID = ?", id);
				
				templates = selectBuider.get();
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getEMailTemplateFromMap(templateMap);
				}
			}
			else if(templateType == UserTemplate.Type.SMS.getIntVal()) {
				selectBuider = new GenericSelectRecordBuilder()
						.select(FieldFactory.getSMSTemplateFields())
						.table("SMS_Templates")
						.andCustomWhere("SMS_Templates.ID = ?", id);
				
				templates = selectBuider.get();
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getSMSTemplateFromMap(templateMap);
				}
			}
			else if(templateType == UserTemplate.Type.EXCEL.getIntVal()) {
				selectBuider = new GenericSelectRecordBuilder()
						.select(FieldFactory.getExcelTemplateFields())
						.table("Excel_Templates")
						.andCustomWhere("Excel_Templates.ID = ?", id);
				
				templates = selectBuider.get();
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getExcelTemplateFromMap(templateMap);
				}
			}
		}
		return null;
	}
	
	public static long addEmailTemplate(long orgId, EMailTemplate template) throws Exception {
		
		User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
		
		template.setOrgId(orgId);
		template.setBodyId(FileStoreFactory.getInstance().getFileStore(superAdmin.getId()).addFile("Email_Template_"+template.getName(), template.getBody(), "text/plain"));
		
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
	
	public static long addSMSTemplate(long orgId, SMSTemplate template) throws Exception {
		template.setOrgId(orgId);
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
	
	private static EMailTemplate getEMailTemplateFromMap(Map<String, Object> templateMap) throws Exception {
		EMailTemplate template = FieldUtil.getAsBeanFromMap(templateMap, EMailTemplate.class);
		
		User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
		
		try(InputStream body = FileStoreFactory.getInstance().getFileStore(superAdmin.getId()).readFile(template.getBodyId())) {
			template.setBody(IOUtils.toString(body));
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
		
		User superAdmin = AccountUtil.getOrgBean().getSuperAdmin(AccountUtil.getCurrentOrg().getOrgId());
		
		template.setOrgId(orgId);
		template.setContentId((FileStoreFactory.getInstance().getFileStore(superAdmin.getId()).addFile("JSON_Template_"+template.getName(), template.getContent(), "text/plain")));
		template.setType(UserTemplate.Type.JSON);
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
		template.setExcelFileId(FileStoreFactory.getInstance().getFileStore(superAdmin.getId()).addFile(fileName, template.getExcelFile(), ""));
		
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
}
