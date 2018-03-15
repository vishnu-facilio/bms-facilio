package com.facilio.bmsconsole.commands.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PMReminder;
import com.facilio.bmsconsole.context.PMReminder.ReminderType;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.DBUtil;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.tasker.FacilioTimer;
import com.facilio.transaction.FacilioConnectionPool;

public class CommonCommandUtil {
	public static void setFwdMail(SupportEmailContext supportEmail) {
		String actualEmail = supportEmail.getActualEmail();
		String orgEmailDomain = "@"+AccountUtil.getCurrentOrg().getDomain()+".facilio.com";
		
		if(actualEmail.toLowerCase().endsWith(orgEmailDomain)) {
			supportEmail.setFwdEmail(actualEmail);
			supportEmail.setVerified(true);
		}
		else {
			String[] emailSplit = actualEmail.toLowerCase().split("@");
			if(emailSplit.length < 2) {
				throw new IllegalArgumentException("Actual email address of SupportEmail is not valid");
			}
			supportEmail.setFwdEmail(emailSplit[1].replaceAll("\\.", "")+emailSplit[0]+orgEmailDomain);
			supportEmail.setVerified(false);
		}
	}
	
	public static Map<Long, User> getRequesters(String ids) throws Exception {
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		Map<Long, User> requesters = new HashMap<>();
		Connection conn =null;
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("SELECT ORG_USERID, EMAIL, NAME FROM ORG_Users, Users where ORG_Users.USERID = Users.USERID and ORG_Users.ORGID = ? and ? & ORG_Users.USER_TYPE = ? ORDER BY EMAIL");
			
			pstmt.setLong(1, AccountUtil.getCurrentOrg().getOrgId());
			pstmt.setInt(2, AccountConstants.UserType.REQUESTER.getValue());
			pstmt.setInt(3, AccountConstants.UserType.REQUESTER.getValue());
			
			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				User rc = new User();
				rc.setEmail((String)rs.getString("EMAIL"));
				rc.setName((String) rs.getString("NAME"));
				
				requesters.put(rs.getLong("ORG_USERID"), rc);
			}
			
			return requesters;
		}
		catch (SQLException e) {
			throw e;
		}
		finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
	}
	
	public static String getNumberWithSuffix(int i) {
	    String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
	    switch (i % 100) {
	    case 11:
	    case 12:
	    case 13:
	        return i + "th";
	    default:
	        return i + sufixes[i % 10];

	    }
	}
	
	public static void appendModuleNameInKey(String moduleName, String prefix, Map<String, Object> beanMap, Map<String, Object> placeHolders) throws Exception {
		if(beanMap != null) {
			if(moduleName != null && !moduleName.isEmpty() && !LookupSpecialTypeUtil.isSpecialType(moduleName)) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				List<FacilioField> fields = modBean.getAllFields(moduleName);
				
				if(fields != null && !fields.isEmpty()) {
					for(FacilioField field : fields) {
						if(field.getDataTypeEnum() == FieldType.LOOKUP) {
							Map<String, Object> props = (Map<String, Object>) beanMap.remove(field.getName());
							if(props != null && !props.isEmpty()) {
								LookupField lookupField = (LookupField) field;
								//Commenting out because max level is set as 0 by default and anyway we need this. And also because of the change in library of mapper
//								if(props.size() <= 3) {
									Object lookupVal = FieldUtil.getLookupVal(lookupField, (long) props.get("id"), 0);
									placeHolders.put(prefix+"."+field.getName(), lookupVal);
									props = FieldUtil.getAsProperties(lookupVal);
//								}
								String childModuleName = lookupField.getLookupModule() == null?lookupField.getSpecialType():lookupField.getLookupModule().getName();
								appendModuleNameInKey(childModuleName, prefix+"."+field.getName(), props, placeHolders);
							}
						}
						else {
							placeHolders.put(prefix+"."+field.getName(), beanMap.remove(field.getName()));
						}
					}
				}
			}
			for(Map.Entry<String, Object> entry : beanMap.entrySet()) {
				if(entry.getValue() instanceof Map<?, ?>) {
					appendModuleNameInKey(null, prefix+"."+entry.getKey(), (Map<String, Object>) entry.getValue(), placeHolders);
				}
				else {
					placeHolders.put(prefix+"."+entry.getKey(), entry.getValue());
				}
			}
		}
	}
	
	public static Map<Long,Object> getPickList(String moduleName) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField primaryField = (FacilioField) modBean.getPrimaryField(moduleName);
		if( primaryField == null) {
			return null;
		}

		try {
			List<FacilioField> fields = new ArrayList<>();
			fields.add(primaryField);				
			SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
					.moduleName(moduleName)
					.select(fields);
			List<Map<String, Object>> records = builder.getAsProps();
			Map<Long,Object> pickList = new HashMap<Long,Object>();

			for(Map<String, Object> record : records) {
				pickList.put((Long) record.get("id"), record.get(primaryField.getName()));
			}
			return pickList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
	}
	
	public static Map<Long,Object> getPickList(List<Long> idList, FacilioModule module) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField primaryField = (FacilioField) modBean.getPrimaryField(module.getName());
		if( primaryField == null) {
			return null;
		}

		try {
			List<FacilioField> fields = new ArrayList<>();
			fields.add(primaryField);				
			SelectRecordsBuilder<ModuleBaseWithCustomFields> builder = new SelectRecordsBuilder<ModuleBaseWithCustomFields>()
					.module(module)
					.select(fields).andCondition(CriteriaAPI.getIdCondition(idList, module));
			List<Map<String, Object>> records = builder.getAsProps();
			Map<Long,Object> pickList = new HashMap<Long,Object>();

			for(Map<String, Object> record : records) {
				pickList.put((Long) record.get("id"), record.get(primaryField.getName()));
			}
			return pickList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
	}
	
	
	public static void scheduleBeforePMReminder(PMReminder reminder, long nextExecutionTime, long triggerId) throws Exception {
		if(reminder.getTypeEnum() == ReminderType.BEFORE && nextExecutionTime != -1 && triggerId != -1) {
			long id = deleteOrAddPreviousBeforeRemindersRel(reminder.getId(), triggerId);
			FacilioTimer.scheduleOneTimeJob(id, "BeforePMReminder", nextExecutionTime-reminder.getDuration(), "facilio");
		}
		else {
			throw new IllegalArgumentException("Invalid parameters for scheduling Before PMReminder job"+reminder.getId());
		}
	}
	
	private static long deleteOrAddPreviousBeforeRemindersRel(long pmReminderId, long triggerId) throws Exception {
		List<FacilioField> fields = FieldFactory.getBeforePMRemindersTriggerRelFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioModule module = ModuleFactory.getBeforePMRemindersTriggerRelModule();
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
														.select(fields)
														.table(module.getTableName())
														.andCondition(CriteriaAPI.getCondition(fieldMap.get("pmReminderId"), String.valueOf(pmReminderId), NumberOperators.EQUALS))
														.andCondition(CriteriaAPI.getCondition(fieldMap.get("pmTriggerId"), String.valueOf(triggerId), NumberOperators.EQUALS))
														;
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			Map<String, Object> relProp = props.get(0);
			long id = (long) relProp.get("id");
			FacilioTimer.deleteJob(id, "BeforePMReminder");
			return id;
		}
		else {
			Map<String, Object> relProp = new HashMap<>();
			relProp.put("pmReminderId", pmReminderId);
			relProp.put("pmTriggerId", triggerId);
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
															.table(module.getTableName())
															.fields(fields)
															.addRecord(relProp);
			
			insertBuilder.save();
			return (long) relProp.get("id");
		}
	}
	
	public static void scheduleAfterPMReminder(PMReminder reminder, long currentExecutionTime, long woId) throws SQLException, RuntimeException, Exception {
		if(reminder.getTypeEnum() == ReminderType.AFTER && currentExecutionTime != -1 && woId != -1) {
			FacilioTimer.scheduleOneTimeJob(addPMReminderToWORel(reminder.getId(), woId), "AfterPMReminder", currentExecutionTime+reminder.getDuration(), "facilio");
		}
		else {
			throw new IllegalArgumentException("Invalid parameters for scheduling After PMReminder job"+reminder.getId());
		}
	}
	
	private static long addPMReminderToWORel(long pmReminderId, long woId) throws SQLException, RuntimeException {
		Map<String, Object> props = new HashMap<>();
		props.put("pmReminderId", pmReminderId);
		props.put("woId", woId);
		
		GenericInsertRecordBuilder recordBuilder = new GenericInsertRecordBuilder()
														.fields(FieldFactory.getAfterPMReminderWORelFields())
														.table(ModuleFactory.getAfterPMRemindersWORelModule().getTableName())
														.addRecord(props);
		
		recordBuilder.save();
		return (long) props.get("id");
	}
	
	public static void emailException(String msg, Throwable e) {
		try {
			JSONObject json = new JSONObject();
			
			json.put("sender", "error@facilio.com");
			json.put("to", "manthosh@facilio.com");
			StringBuilder subject = new StringBuilder();
			
			if(AwsUtil.getConfig("app.url").contains("localhost")) {
				subject.append("Local - ");
			}
			else {
				subject.append("Prod - ");
			}
			
			if (msg != null) {
				subject.append(msg)
						.append(" - ");
			}
			
			subject.append(e.getMessage());
			json.put("subject", subject.toString());
			
			StringBuilder body = new StringBuilder();
			
			Organization org = AccountUtil.getCurrentOrg();
			if(org != null) {
				body.append(org);
			}
			
			body.append("\n\nMsg : ")
				.append(msg)
				.append("\n\nApp Url : ")
				.append(AwsUtil.getConfig("app.url"))
				.append("\n\nTrace : \n--------\n")
				.append(ExceptionUtils.getStackTrace(e));
			checkDB(e.getMessage(), body);
			
			json.put("message", body.toString());
			AwsUtil.sendEmail(json);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
	
	private static void checkDB(String msg, StringBuilder body) {
		if (msg != null && msg.toLowerCase().contains("deadlock")) {
			String sql = "show engine innodb status";
			try (Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql);ResultSet rs = pstmt.executeQuery()) {
				rs.first();
				body.append("\n\nInno DB Status : \n------------\n\n")
					.append(rs.getString("Status"));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
