package com.facilio.bmsconsole.commands.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.FacilioTablePrinter;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.queue.FAWSQueue;
import com.facilio.sql.DBUtil;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class CommonCommandUtil {
	private static Logger log = LogManager.getLogger(CommonCommandUtil.class.getName());
	public static final String DELIMITER = "######";
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
			log.info("Exception occurred ", e);
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
			log.info("Exception occurred ", e);
		}
		return null;	
	}
	
	public static void emailException(String fromClass, String msg, Throwable e) {
		emailException(fromClass, msg, e, null);
	}
	
	public static void emailException(String fromClass, String msg, Throwable e, String info) {
		try {
			JSONObject json = new JSONObject();
			
			json.put("sender", "error@facilio.com");
			json.put("to", "error@facilio.com");
			StringBuilder subject = new StringBuilder();
			String environment = AwsUtil.getConfig("environment");
			if(environment == null) {
				subject.append("Local - ");
			}
			else {
				subject.append(environment)
						.append(" - ");
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
				body.append(org.getOrgId()).append('-');
			}

			body.append(fromClass).append(DELIMITER);
			
			body.append("\n Time").append(System.currentTimeMillis()).append("\n\nMsg : ")
				.append(msg)
				.append("\n\nApp Url : ")
				.append(AwsUtil.getConfig("app.url"))
				.append("\n\nTrace : \n--------\n")
				.append(ExceptionUtils.getStackTrace(e));
			
			if (info != null && !info.isEmpty()) {
				body.append(info);
			}
			
			checkDB(e.getMessage(), body);
			String message = body.toString();
			json.put("message", message);
			//AwsUtil.sendEmail(json);
			if("production".equals(AwsUtil.getConfig("environment"))) {
				FAWSQueue.sendMessage("Exception", message);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			log.info("Exception occurred ", e1);
		}
	}
	
	private static void checkDB(String msg, StringBuilder body) {
		if (msg != null) {
			if(msg.toLowerCase().contains("deadlock")) {
				String sql = "show engine innodb status";
				try (Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql);ResultSet rs = pstmt.executeQuery()) {
					rs.first();
					body.append("\n\nInno DB Status : \n------------\n\n")
						.append(rs.getString("Status"));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					log.info("Exception occurred ", e);
				}
			}
			if(msg.toLowerCase().contains("timeout")) {
				String sql = "show processlist";
				try (Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql);ResultSet rs = pstmt.executeQuery()) {
					body.append("\n\nProcess List : \n------------\n\n")
						.append(FacilioTablePrinter.getResultSetData(rs));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					log.info("Exception occurred ", e);
				}
			}
		}
	}
	
	public static void insertOrgInfo( long orgId, String name, String value) throws Exception
	{
		if (getOrgInfo(orgId, name) == null) {
		
		    GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder()
		            .table(AccountConstants.getOrgInfoModule().getTableName())
		            .fields(AccountConstants.getOrgInfoFields());
		
		    Map<String, Object> properties = new HashMap<>();
		    properties.put("orgId", orgId);
		    properties.put("name", name);
		    properties.put("value", value);
		    insertRecordBuilder.addRecord(properties);
		    insertRecordBuilder.save();
		}
		else {
			// update
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(AccountConstants.getOrgInfoModule().getTableName()).fields(AccountConstants.getOrgInfoFields())
					.andCustomWhere("OrgID = ? AND NAME = ?", orgId, name );
			Map<String, Object> props = new HashMap<>();
			props.put("name", name);
		    props.put("value", value);
		    updateBuilder.update(props);
		    
		}
	}
    public static Map<String, Object> getOrgInfo(long orgId, String name) throws Exception {
    	
    	GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getOrgInfoFields())
				.table(AccountConstants.getOrgInfoModule().getTableName())
				.andCustomWhere("ORGID = ? AND NAME = ?", orgId, name);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return props.get(0);
		}
		return null;		
	}
    
    public static JSONObject getOrgInfo(long orgId) throws Exception {
    	
    	JSONObject result = new JSONObject();
    	FacilioModule module = AccountConstants.getOrgInfoModule();
    	GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getOrgInfoFields())
				.table(module.getTableName())
				.andCustomWhere("ORGID = ?", orgId);
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				result.put((String) prop.get("name"), prop.get("value"));
			}
		}
		return result;		
	}
public static JSONObject getOrgInfo() throws Exception {
    	
    	JSONObject result = new JSONObject();
    	FacilioModule module = AccountConstants.getOrgInfoModule();
    	GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getOrgInfoFields())
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				result.put((String) prop.get("name"), prop.get("value"));
			}
		}
		return result;		
	}
    
    public static Map<String, String> getOrgInfo(String... names) throws Exception {
    	
    	if (names != null && names.length > 0) {
	    	Map<String, String> result = new HashMap<>();
	    	FacilioModule module = AccountConstants.getOrgInfoModule();
	    	List<FacilioField> fields = AccountConstants.getOrgInfoFields();
	    	FacilioField name = FieldFactory.getAsMap(fields).get("name");
	    	
	    	GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table(module.getTableName())
					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCondition(CriteriaAPI.getCondition(name, String.join(",", names), StringOperators.IS))
					;
			
			List<Map<String, Object>> props = selectBuilder.get();
			if (props != null && !props.isEmpty()) {
				for (Map<String, Object> prop : props) {
					result.put((String) prop.get("name"), (String) prop.get("value"));
				}
			}
			return result;	
    	}
    	return null;
	}
    
    public static Map<String, List<ReadingContext>> getReadingMap(FacilioContext context) {
    	Map<String, List<ReadingContext>> readingMap = (Map<String, List<ReadingContext>>) context.get(FacilioConstants.ContextNames.READINGS_MAP);
    	if (readingMap == null) {
	    	List<ReadingContext> readings = (List<ReadingContext>) context.get(FacilioConstants.ContextNames.READINGS);
			if(readings == null) {
				ReadingContext reading = (ReadingContext) context.get(FacilioConstants.ContextNames.READING);
				if(reading != null) {
					readings = Collections.singletonList(reading);
				}
			}
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			
			if (moduleName != null && !moduleName.isEmpty() && readings != null && !readings.isEmpty()) {
				readingMap = Collections.singletonMap(moduleName, readings);
				context.put(FacilioConstants.ContextNames.READINGS_MAP, readingMap);
			}
    	}
    	return readingMap;
    }
    
    public static String getStackTraceString(StackTraceElement[] traces) {
    	StringJoiner joiner = new StringJoiner("\n");
    	for (StackTraceElement trace : traces) {
    		joiner.add(trace.toString());
    	}
    	joiner.add("###################################################");
    	return joiner.toString();
    }
}
