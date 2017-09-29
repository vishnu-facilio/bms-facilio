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
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class TemplateAPI {
	public static UserTemplate getTemplate(long orgId, long id) throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder selectBuider = new GenericSelectRecordBuilder()
														.connection(conn)
														.select(FieldFactory.getTemplateFields())
														.table("Templates")
														.innerJoin("EMail_Templates")
														.on("Templates.ID = EMail_Templates.ID")
														.innerJoin("SMS_Templates")
														.on("Templates.ID = SMS_Templates.ID")
														.andCustomWhere("Templates.ORGID = ? AND Templates.ID = ?", orgId, id);
			
			List<Map<String, Object>> templates = selectBuider.get();
			
			if(templates != null && !templates.isEmpty()) {
				Map<String, Object> templateMap = templates.get(0);
				return getTemplateFromMap(templateMap);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}
	
	public static long addEmailTemplate(EMailTemplate template) throws Exception {
		
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
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
	
	public static long addSMSTemplate(SMSTemplate template) throws Exception {
		
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
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
	
	private static UserTemplate getTemplateFromMap(Map<String, Object> templateMap) throws Exception {
		int type = (int) templateMap.get("type");
		
		if(type == UserTemplate.Type.EMAIL.getIntVal()) {
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
		else if(type == UserTemplate.Type.SMS.getIntVal()) {
			SMSTemplate template = new SMSTemplate();
			BeanUtils.populate(template, templateMap);
			return template;
		}
		
		return null;
	}
}
