package com.facilio.bmsconsole.util;

import java.io.InputStream;
import java.sql.Connection;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.io.IOUtils;

import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.workflow.EMailTemplate;
import com.facilio.bmsconsole.workflow.SMSTemplate;
import com.facilio.bmsconsole.workflow.UserTemplate;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class TemplateAPI {
	public static UserTemplate getTemplate(long orgId, long id) throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
														.connection(conn)
														.select(FieldFactory.getUserTemplateFields())
														.table("Templates")
														.andCustomWhere("Templates.ORGID = ? AND Templates.ID = ?", orgId, id);
			
			List<Map<String, Object>> templates = selectBuider.get();
			
			if(templates != null && !templates.isEmpty()) {
				Map<String, Object> templateMap = templates.get(0);
				int type = (int) templateMap.get("type");
				if(type == UserTemplate.Type.EMAIL.getIntVal()) {
					selectBuider = new GenericSelectRecordBuilder()
							.connection(conn)
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
							.connection(conn)
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
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}
	
	public static UserTemplate getTemplate(long orgId, String templateName, UserTemplate.Type type) throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
														.connection(conn)
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
							.connection(conn)
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
							.connection(conn)
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
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}
	
	public static long addEmailTemplate(long orgId, EMailTemplate template) throws Exception {
		
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			template.setOrgId(orgId);
			template.setBodyId(FileStoreFactory.getInstance().getFileStore().addFile("Email_Template_"+template.getName(), template.getBody(), "text/plain"));
			
			Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
			
			GenericInsertRecordBuilder userTemplateBuilder = new GenericInsertRecordBuilder()
																.connection(conn)
																.table("Templates")
																.fields(FieldFactory.getUserTemplateFields())
																.addRecord(templateProps);
			
			userTemplateBuilder.save();
			
			GenericInsertRecordBuilder emailTemplateBuilder = new GenericInsertRecordBuilder()
																	.connection(conn)
																	.table("EMail_Templates")
																	.fields(FieldFactory.getEMailTemplateFields())
																	.addRecord(templateProps);
			emailTemplateBuilder.save();
			
			return (long) templateProps.get("id"); 
			
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static int updateEmailTemplate(long orgId, EMailTemplate template, long id) throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
			
			GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
																	.connection(conn)
																	.table("EMail_Templates")
																	.fields(FieldFactory.getEMailTemplateFields())
																	.andCustomWhere("ID = ?", id);
			
			return updateRecordBuilder.update(templateProps);
																	
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static long addSMSTemplate(long orgId, SMSTemplate template) throws Exception {
		
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			template.setOrgId(orgId);
			Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
			
			GenericInsertRecordBuilder userTemplateBuilder = new GenericInsertRecordBuilder()
																.connection(conn)
																.table("Templates")
																.fields(FieldFactory.getUserTemplateFields())
																.addRecord(templateProps);
			
			userTemplateBuilder.save();
			
			GenericInsertRecordBuilder smsTemplateBuilder = new GenericInsertRecordBuilder()
																	.connection(conn)
																	.table("SMS_Templates")
																	.fields(FieldFactory.getSMSTemplateFields())
																	.addRecord(templateProps);
			smsTemplateBuilder.save();
			
			return (long) templateProps.get("id");
			
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	public static int updateSMSTemplate(long orgId, SMSTemplate template, long id) throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			Map<String, Object> templateProps = FieldUtil.getAsProperties(template);
			
			GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder()
																	.connection(conn)
																	.table("SMS_Templates")
																	.fields(FieldFactory.getSMSTemplateFields())
																	.andCustomWhere("ID = ?", id);
			
			return updateRecordBuilder.update(templateProps);
																	
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
	}
	
	private static EMailTemplate getEMailTemplateFromMap(Map<String, Object> templateMap) throws Exception {
		EMailTemplate template = new EMailTemplate();
		BeanUtils.populate(template, templateMap);
		
		try(InputStream body = FileStoreFactory.getInstance().getFileStore().readFile(template.getBodyId())) {
			template.setBody(IOUtils.toString(body));
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		
		return template;
	}
	
	private static SMSTemplate getSMSTemplateFromMap(Map<String, Object> templateMap) throws Exception {
		SMSTemplate template = new SMSTemplate();
		BeanUtils.populate(template, templateMap);
		return template;
	}
}
