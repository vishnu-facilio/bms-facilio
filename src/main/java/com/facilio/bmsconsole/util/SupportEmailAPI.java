package com.facilio.bmsconsole.util;

import com.facilio.accounts.dto.Group;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
					.andCustomWhere("ORGID = ?", AccountUtil.getCurrentOrg().getOrgId());
			
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
}
