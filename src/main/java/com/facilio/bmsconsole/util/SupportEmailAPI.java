package com.facilio.bmsconsole.util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;

import com.facilio.bmsconsole.context.GroupContext;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.fw.OrgInfo;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class SupportEmailAPI {
	public static final String TABLE_NAME = "SupportEmails";
	
	public static SupportEmailContext getSupportEmailFromFwdEmail(String fwdMail) throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.connection(conn)
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
			e.printStackTrace();
			throw e;
		}
		return null;
	}
	
	public static SupportEmailContext getSupportEmailFromId(long orgId, long id) throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.connection(conn)
					.table(TABLE_NAME)
					.select(FieldFactory.getSupportEmailFields())
					.andCustomWhere("ORGID = ? AND ID = ?",orgId, id);
			
			List<Map<String, Object>> emailList = builder.get();
			if(emailList != null && emailList.size() > 0) {
				Map<String, Object> email = emailList.get(0);
				return getSupportEmailFromMap(email);
			}
		}
		catch(Exception e) {
			e.printStackTrace();
			throw e;
		}
		return null;
	}
	
	public static List<SupportEmailContext> getSupportEmailsOfOrg(long orgId) throws Exception {
		try(Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
			GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
					.connection(conn)
					.table(TABLE_NAME)
					.select(FieldFactory.getSupportEmailFields())
					.andCustomWhere("ORGID = ?", OrgInfo.getCurrentOrgInfo().getOrgid());
			
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
			e.printStackTrace();
			throw e;
		}
		return null;
	}
	
	private static SupportEmailContext getSupportEmailFromMap(Map<String, Object> props) throws Exception {
		SupportEmailContext email = new SupportEmailContext();
		long groupId = -1;
		if(props.get("autoAssignGroup") != null) {
			groupId = (long) props.get("autoAssignGroup");
		}
		if(groupId != -1) {
			GroupContext group = GroupAPI.getGroup(groupId);
			props.put("autoAssignGroup", group);
		}
		else {
			props.remove("autoAssignGroup");
		}
		
		BeanUtils.populate(email, props);
		return email;
	}
}
