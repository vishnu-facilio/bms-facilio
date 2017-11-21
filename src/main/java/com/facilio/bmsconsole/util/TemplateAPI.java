package com.facilio.bmsconsole.util;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.workflow.AlarmTemplate;
import com.facilio.bmsconsole.workflow.EMailTemplate;
import com.facilio.bmsconsole.workflow.SMSTemplate;
import com.facilio.bmsconsole.workflow.UserTemplate;
import com.facilio.bmsconsole.workflow.WorkorderTemplate;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class TemplateAPI {
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
			else if(type == UserTemplate.Type.WORKORDER.getIntVal()) {
				selectBuider = new GenericSelectRecordBuilder()
						.select(FieldFactory.getWorkorderTemplateFields())
						.table("Workorder_Template")
						.andCustomWhere("Workorder_Template.ID = ?", id);
				
				templates = selectBuider.get();
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getWorkorderTemplateFromMap(templateMap);
				}
			}
			else if(type == UserTemplate.Type.ALARM.getIntVal()) {
				selectBuider = new GenericSelectRecordBuilder()
						.select(FieldFactory.getAlarmTemplateFields())
						.table("Alarm_Template")
						.andCustomWhere("Alarm_Template.ID = ?", id);
				
				templates = selectBuider.get();
				if(templates != null && !templates.isEmpty()) {
					templateMap.putAll(templates.get(0));
					return getAlarmTemplateFromMap(templateMap);
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
		}
		return null;
	}
	
	public static long addEmailTemplate(long orgId, EMailTemplate template) throws Exception {
		template.setOrgId(orgId);
		template.setBodyId(FileStoreFactory.getInstance().getFileStore(OrgInfo.getCurrentOrgInfo().getSuperAdmin().getOrgUserId()).addFile("Email_Template_"+template.getName(), template.getBody(), "text/plain"));
		
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
		
		try(InputStream body = FileStoreFactory.getInstance().getFileStore(OrgInfo.getCurrentOrgInfo().getSuperAdmin().getOrgUserId()).readFile(template.getBodyId())) {
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
	
	private static WorkorderTemplate getWorkorderTemplateFromMap(Map<String, Object> templateMap) throws Exception {
		WorkorderTemplate template = FieldUtil.getAsBeanFromMap(templateMap, WorkorderTemplate.class);
		
		try(InputStream body = FileStoreFactory.getInstance().getFileStore(OrgInfo.getCurrentOrgInfo().getSuperAdmin().getOrgUserId()).readFile(template.getContentId())) {
			template.setContent(IOUtils.toString(body));
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return template;
	}
	
	private static AlarmTemplate getAlarmTemplateFromMap(Map<String, Object> templateMap) throws Exception {
		AlarmTemplate template = FieldUtil.getAsBeanFromMap(templateMap, AlarmTemplate.class);
		
		try(InputStream body = FileStoreFactory.getInstance().getFileStore(OrgInfo.getCurrentOrgInfo().getSuperAdmin().getOrgUserId()).readFile(template.getContentId())) {
			template.setContent(IOUtils.toString(body));
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return template;
	}
	
	public static long addWorkorderTemplate(long orgId, WorkorderTemplate template) throws Exception {
		template.setOrgId(orgId);
		template.setContentId((FileStoreFactory.getInstance().getFileStore(OrgInfo.getCurrentOrgInfo().getSuperAdmin().getOrgUserId()).addFile("Workorder_Template_"+template.getName(), template.getContent(), "text/plain")));
		
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
		
		GenericInsertRecordBuilder userTemplateBuilder = new GenericInsertRecordBuilder()
															.table("Templates")
															.fields(FieldFactory.getUserTemplateFields())
															.addRecord(templateProps);
		
		userTemplateBuilder.save();
		
		GenericInsertRecordBuilder workorderTemplateBuilder = new GenericInsertRecordBuilder()
																.table("Workorder_Template")
																.fields(FieldFactory.getWorkorderTemplateFields())
																.addRecord(templateProps);
		workorderTemplateBuilder.save();
		return (long) templateProps.get("id"); 
	}
	
	public static long addAlarmTemplate(long orgId, AlarmTemplate template) throws Exception {
		template.setOrgId(orgId);
		template.setContentId((FileStoreFactory.getInstance().getFileStore(OrgInfo.getCurrentOrgInfo().getSuperAdmin().getOrgUserId()).addFile("Alarm_Template_"+template.getName(), template.getContent(), "text/plain")));
		
		Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
		
		GenericInsertRecordBuilder userTemplateBuilder = new GenericInsertRecordBuilder()
															.table("Templates")
															.fields(FieldFactory.getUserTemplateFields())
															.addRecord(templateProps);
		
		userTemplateBuilder.save();
		
		GenericInsertRecordBuilder alarmTemplateBuilder = new GenericInsertRecordBuilder()
																.table("Alarm_Template")
																.fields(FieldFactory.getAlarmTemplateFields())
																.addRecord(templateProps);
		alarmTemplateBuilder.save();
		
		return (long) templateProps.get("id"); 
	}
}
