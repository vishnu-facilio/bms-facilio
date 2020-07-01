package com.facilio.bmsconsole.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.modules.ModuleFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.fields.FacilioField;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

public class SupportEmailAPI {
	public static final String TABLE_NAME = "SupportEmails";
	private static Logger log = LogManager.getLogger(SupportEmailAPI.class.getName());

	public static SupportEmailContext getSupportEmailFromFwdEmail(String fwdMail) throws Exception {
		try {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(TABLE_NAME)
					.select(FieldFactory.getSupportEmailFields())
					.andCustomWhere("FWD_EMAIL = ?", fwdMail);
			
			List<Map<String, Object>> emailList = builder.get();
			if(emailList != null && emailList.size() > 0) {
				Map<String, Object> email = emailList.get(0);
				return getSupportEmailFromMap(email);
			}
		}
		catch(Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
		return null;
	}
	
	public static SupportEmailContext getSupportEmailFromId(long orgId, long id) throws Exception {
		try {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(TABLE_NAME)
					.select(FieldFactory.getSupportEmailFields())
					.andCustomWhere("ORGID = ? AND ID = ?",orgId, id);
			
			List<Map<String, Object>> emailList = builder.get();
			if(emailList != null && emailList.size() > 0) {
				Map<String, Object> email = emailList.get(0);
				return FieldUtil.getAsBeanFromMap(email, SupportEmailContext.class);
//				return getSupportEmailFromMap(email);
			}
		}
		catch(Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
		return null;
	}


	
	public static List<SupportEmailContext> getSupportEmailsOfOrg(long orgId) throws Exception {
		try {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(TABLE_NAME)
					.select(FieldFactory.getSupportEmailFields())
					.andCustomWhere("ORGID = ?", orgId);
			
			List<Map<String, Object>> emailList = builder.get();
			if(emailList != null && emailList.size() > 0) {
				List<SupportEmailContext> emails = new ArrayList<>();
				for(Map<String, Object> emailProp : emailList) {
					emails.add(getSupportEmailFromMap(emailProp));
				}
				return emails;
			}
		}
		catch(Exception e) {
			log.info("Exception occurred ", e);
			throw e;
		}
		return null;
	}
	
	private static SupportEmailContext getSupportEmailFromMap(Map<String, Object> props) throws Exception {
		long groupId = -1;
		if(props.get("autoAssignGroup") != null) {
			groupId = (long) props.get("autoAssignGroup");
		}
		if(groupId != -1) {
			Group group = AccountUtil.getGroupBean().getGroup(groupId);
			props.put("autoAssignGroup", group);
		}
		else {
			props.remove("autoAssignGroup");
		}
		return FieldUtil.getAsBeanFromMap(props, SupportEmailContext.class);
	}
	
	public static void addSupportEmailSetting(SupportEmailContext supportEmail) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.setSerializationInclusion(Include.NON_DEFAULT);
		Map<String, Object> emailProps = mapper.convertValue(supportEmail, Map.class);
//		emailProps.put("autoAssignGroup", ((Map<String, Object>)emailProps.get("autoAssignGroup")).get("id"));
		if(emailProps.get("autoAssignGroup")!=null)
		{
					emailProps.put("autoAssignGroup", ((Map<String, Object>)emailProps.get("autoAssignGroup")).get("id"));
		}
		System.out.println(emailProps);
//		FacilioService.runAsService(() ->  addSupportEmailASservice(supportEmail, emailProps));
		
		List<FacilioField> fields = FieldFactory.getSupportEmailFields();
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
												.table("SupportEmails")
												.fields(fields)
												.addRecord(emailProps);
		builder.save();
		supportEmail.setId((long) emailProps.get("id"));
	}
	
	public static void updateSupportEmailSetting(SupportEmailContext supportEmail) throws Exception {
		Map<String, Object> emailProps = FieldUtil.getAsProperties(supportEmail);
		System.out.println("mpa"+emailProps);
		System.out.println("supportEmail"+supportEmail);
		if(emailProps.get("autoAssignGroup")!=null)
		{
			emailProps.put("autoAssignGroup", ((Map<String, Object>)emailProps.get("autoAssignGroup")).get("id"));
		}
		emailProps.remove("id");
		
			List<FacilioField> fields = FieldFactory.getSupportEmailFields();
			GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
													.table("SupportEmails")
													.fields(fields)
													.andCustomWhere("ID = ?", supportEmail.getId());
			builder.update(emailProps);
		}
	
	public static void deleteSupportEmail (long supportEmailId) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table("SupportEmails")
				.andCustomWhere("ID = ?", supportEmailId);
		builder.delete();
	}


	public static List<SupportEmailContext> getImapsEmailsOfOrg() throws Exception {

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getSupportEmailFields())
				.table(ModuleFactory.getSupportEmailsModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("IS_CUSTOM_MAIL", "isCustomMail", String.valueOf(true), BooleanOperators.IS));

		List<Map<String, Object>> props = selectBuilder.get();
		List<SupportEmailContext> imapsMails = new ArrayList<SupportEmailContext>();
		if (props != null && !props.isEmpty()) {
			imapsMails  = FieldUtil.getAsBeanListFromMapList(props, SupportEmailContext.class);
		}
		return imapsMails;
	}
 }
