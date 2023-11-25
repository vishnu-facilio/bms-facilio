package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.BooleanOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.modules.FacilioModule;
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
	private static final FacilioModule SUPPORT_EMAIL_MODULE = ModuleFactory.getSupportEmailsModule();
	private static final List<FacilioField> SUPPORT_EMAIL_FIELDS = FieldFactory.getSupportEmailFields();
	private static final Map<String, FacilioField> SUPPORT_EMAIL_FIELDS_AS_MAP = FieldFactory.getAsMap(SUPPORT_EMAIL_FIELDS);
	private static final Logger LOGGER = LogManager.getLogger(SupportEmailAPI.class.getName());

	public static SupportEmailContext getSupportEmailFromFwdEmail(String email) throws Exception {
		try {
			Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(SUPPORT_EMAIL_FIELDS);
			FacilioField actualEmail = fieldMap.get("actualEmail"), fwdEmail = fieldMap.get("fwdEmail"),
					isCustomEmail = fieldMap.get("isCustomMail"), legacyEmail = fieldMap.get("legacyEmail");

			Criteria emailCriteria = new Criteria();
			emailCriteria.addOrCondition(CriteriaAPI.getCondition(actualEmail, email, StringOperators.IS));
			emailCriteria.addOrCondition(CriteriaAPI.getCondition(fwdEmail, email, StringOperators.IS));
			emailCriteria.addOrCondition(CriteriaAPI.getCondition(legacyEmail, email, StringOperators.IS));

			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(SUPPORT_EMAIL_MODULE.getTableName())
					.select(SUPPORT_EMAIL_FIELDS)
					.andCondition(CriteriaAPI.getCondition(isCustomEmail, "false", BooleanOperators.IS))
					.andCriteria(emailCriteria);
			
			List<Map<String, Object>> emailList = builder.get();
			if(emailList != null && !emailList.isEmpty()) {
				Map<String, Object> supportEmail = emailList.get(0);
				return getSupportEmailFromMap(supportEmail);
			}
		}
		catch(Exception e) {
			LOGGER.info("Exception occurred ", e);
			throw e;
		}
		return null;
	}
	
	public static SupportEmailContext getSupportEmailFromId(long orgId, long id) throws Exception {
		try {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(SUPPORT_EMAIL_MODULE.getTableName())
					.select(SUPPORT_EMAIL_FIELDS)
					.andCustomWhere("ORGID = ? AND ID = ?",orgId, id);
			
			List<Map<String, Object>> emailList = builder.get();
			if(emailList != null && !emailList.isEmpty()) {
				Map<String, Object> email = emailList.get(0);
				return FieldUtil.getAsBeanFromMap(email, SupportEmailContext.class);
			}
		}
		catch(Exception e) {
			LOGGER.info("Exception occurred ", e);
			throw e;
		}
		return null;
	}
	
	public static List<SupportEmailContext> getSupportEmailsOfOrg(long orgId) throws Exception {
		try {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(SUPPORT_EMAIL_MODULE.getTableName())
					.select(SUPPORT_EMAIL_FIELDS)
					.andCriteria(getEmailSourceTypeCriteria())
					.andCustomWhere("ORGID = ?", orgId);
			List<Map<String, Object>> emailList = builder.get();
			if(emailList != null && !emailList.isEmpty()) {
				List<SupportEmailContext> emails = new ArrayList<>();
				for(Map<String, Object> emailProp : emailList) {
					emails.add(getSupportEmailFromMap(emailProp));
				}
				return emails;
			}
		}
		catch(Exception e) {
			LOGGER.info("Exception occurred ", e);
			throw e;
		}
		return null;
	}

	private static Criteria getEmailSourceTypeCriteria(){
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(SUPPORT_EMAIL_FIELDS_AS_MAP.get("emailSourceType"), "2", NumberOperators.NOT_EQUALS));

		return criteria;
	}

	public static SupportEmailContext getSupportEmailsOfSite(long orgId,long siteId) throws Exception {
		try {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.table(SUPPORT_EMAIL_MODULE.getTableName())
					.select(SUPPORT_EMAIL_FIELDS)
					.andCriteria(getEmailSourceTypeCriteria())
					.andCustomWhere("ORGID = ?", orgId)
					.andCustomWhere("SITE_ID = ?", siteId);
			
			List<Map<String, Object>> emailList = builder.get();
			if(emailList != null && emailList.size() > 0) {
				return getSupportEmailFromMap(emailList.get(0));
			}
		}
		catch(Exception e) {
			LOGGER.info("Exception occurred ", e);
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

		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder()
			.table(SUPPORT_EMAIL_MODULE.getTableName())
			.fields(SUPPORT_EMAIL_FIELDS)
			.addRecord(emailProps);
		builder.save();
		supportEmail.setId((long) emailProps.get("id"));
	}
	
	public static void updateSupportEmailSetting(SupportEmailContext supportEmail) throws Exception {
		Map<String, Object> emailProps = FieldUtil.getAsProperties(supportEmail);
		System.out.println("mpa"+emailProps);
		System.out.println("supportEmail"+supportEmail);
		if(emailProps.get("autoAssignGroup")!=null) {
			emailProps.put("autoAssignGroup", ((Map<String, Object>)emailProps.get("autoAssignGroup")).get("id"));
		}
		emailProps.remove("id");

		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
			.table(SUPPORT_EMAIL_MODULE.getTableName())
			.fields(SUPPORT_EMAIL_FIELDS)
			.andCustomWhere("ID = ?", supportEmail.getId());
		builder.update(emailProps);
	}
	
	public static void deleteSupportEmail (long supportEmailId) throws Exception {
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(SUPPORT_EMAIL_MODULE.getTableName())
				.andCustomWhere("ID = ?", supportEmailId);
		builder.delete();
	}

	public static List<SupportEmailContext> getImapsEmailsOfOrg(long orgId) throws Exception {

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(SUPPORT_EMAIL_FIELDS)
				.table(SUPPORT_EMAIL_MODULE.getTableName())
				.andCondition(CriteriaAPI.getCondition("IS_CUSTOM_MAIL", "isCustomMail", String.valueOf(true), BooleanOperators.IS))
				.andCriteria(getEmailSourceTypeCriteria())
				.andCustomWhere("ORGID = ?", orgId);

		List<Map<String, Object>> props = selectBuilder.get();
		List<SupportEmailContext> imapsMails = new ArrayList<SupportEmailContext>();
		if (props != null && !props.isEmpty()) {
			imapsMails  = FieldUtil.getAsBeanListFromMapList(props, SupportEmailContext.class);
		}
		return imapsMails;
	}
 }
