package com.facilio.bmsconsole.commands.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.chain.Context;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.activity.ActivityContext;
import com.facilio.activity.ActivityType;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.OrgUnitsContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.ResourceContext;
import com.facilio.bmsconsole.context.SupportEmailContext;
import com.facilio.bmsconsole.context.TaskContext;
import com.facilio.bmsconsole.context.TaskContext.TaskStatus;
import com.facilio.bmsconsole.context.TicketStatusContext;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.LookupField;
import com.facilio.bmsconsole.modules.ModuleBaseWithCustomFields;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.modules.UpdateChangeSet;
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.FacilioTablePrinter;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.ReadingRuleAPI;
import com.facilio.bmsconsole.util.ResourceAPI;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.queue.FAWSQueue;
import com.facilio.sql.DBUtil;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowExpression;
import com.facilio.workflows.util.WorkflowUtil;
import com.opensymphony.xwork2.ActionContext;

public class CommonCommandUtil {
	private static final Logger LOGGER = LogManager.getLogger(CommonCommandUtil.class.getName());
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
				rc.setEmail(rs.getString("EMAIL"));
				rc.setName(rs.getString("NAME"));
				
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
						if(field.getDataTypeEnum() == FieldType.LOOKUP && prefix.split("\\.").length < 5) {
							Map<String, Object> props = (Map<String, Object>) beanMap.remove(field.getName());
							if(props != null && !props.isEmpty() && props.get("id") != null) {
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
		FacilioField primaryField = modBean.getPrimaryField(moduleName);
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
			LOGGER.info("Exception occurred ", e);
		}
		return null;	
	}
	
	public static Map<Long,Object> getPickList(List<Long> idList, FacilioModule module) throws Exception
	{
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioField primaryField = modBean.getPrimaryField(module.getName());
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
			LOGGER.info("Exception occurred ", e);
		}
		return null;	
	}
	
	public static void emailAlert (String subject, String msg) {
		try {
			
			if (AwsUtil.isProduction()) {
				Organization org = AccountUtil.getCurrentOrg();
				
				JSONObject json = new JSONObject();
				json.put("sender", "alert@facilio.com");
				json.put("to", "error+alert@facilio.com");
				json.put("subject", org.getOrgId()+" - "+subject);
				
				StringBuilder body = new StringBuilder()
										.append(msg)
										.append("\n\nInfo : \n--------\n")
										.append("\n Org Time : ").append(DateTimeUtil.getDateTime())
										.append("\n Indian Time : ").append(DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata")))
										.append("\n\nMsg : ")
										.append(msg)
										.append("\n\nApp Url : ")
										.append(AwsUtil.getConfig("clientapp.url"))
										.append("\n\nOrg Info : \n--------\n")
										.append(org.toString())
										;
				json.put("message", body.toString());
				
				AwsUtil.sendEmail(json);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
								
	}
	
	public static void emailException(String fromClass, String msg, Throwable e) {
		emailException(fromClass, msg, e, null);
	}
	
	public static void emailException(String fromClass, String msg, String info) {
		emailException(fromClass, msg, null, info);
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
			
			if (e != null) {
				subject.append(e.getMessage());
			}
			json.put("subject", subject.toString());
			
			StringBuilder body = new StringBuilder();
			
			Organization org = AccountUtil.getCurrentOrg();
			if(org != null) {
				body.append(org.getOrgId()).append('-');
			}

			body.append(fromClass).append(DELIMITER);
			
			body.append("\n Org Time : ").append(DateTimeUtil.getDateTime())
				.append("\n Indian Time : ").append(DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata")))
				.append("\n\nMsg : ")
				.append(msg)
				.append("\n\nApp Url : ")
				.append(AwsUtil.getConfig("clientapp.url"));
				
			if (ActionContext.getContext() != null && ServletActionContext.getRequest() != null) {
				body.append("\nUser: ")
				.append(AccountUtil.getCurrentUser().getName()).append(" - ").append(AccountUtil.getCurrentUser().getOuid())
				.append("\nDevice Type: ")
				.append(AccountUtil.getCurrentAccount().getDeviceType())
				.append("\nRequest Url: ")
				.append(ServletActionContext.getRequest().getRequestURI());
			}
				json.put("message", body.toString());;
			
			String errorTrace = null;
			if (e != null) {
				errorTrace = ExceptionUtils.getStackTrace(e);
				body.append("\n\nTrace : \n--------\n")
					.append(errorTrace);
			}
			
			if (info != null && !info.isEmpty()) {
				body.append("\n")
					.append(info);
			}
			
			checkDB(errorTrace, body);
			String message = body.toString();
			json.put("message", message);
			//AwsUtil.sendEmail(json);
			if(AwsUtil.isProduction()) {
				FAWSQueue.sendMessage("Exception", message);
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			LOGGER.info("Exception occurred ", e1);
		}
	}
	
	private static void checkDB(String errorTrace, StringBuilder body) {
		if (errorTrace != null) {
			if(errorTrace.toLowerCase().contains("deadlock") || body.toString().toLowerCase().contains("deadlock")) {
				try (Connection conn = FacilioConnectionPool.INSTANCE.getConnection()) {
					String sql = "show engine innodb status";
					try (PreparedStatement pstmt = conn.prepareStatement(sql);ResultSet rs = pstmt.executeQuery()) {
						rs.first();
						body.append("\n\nInno DB Status : \n------------\n\n")
							.append(rs.getString("Status"));
					}
					catch (SQLException e) {
						LOGGER.info("Exception occurred while getting InnoDB status");
					}
					
					sql = "SELECT * FROM information_schema.innodb_locks";
					try (PreparedStatement pstmt = conn.prepareStatement(sql);ResultSet rs = pstmt.executeQuery()) {
						rs.first();
						body.append("\n\nLocks from Information Schema : \n------------\n\n")
							.append(FacilioTablePrinter.getResultSetData(rs));
					}
					catch (SQLException e) {
						LOGGER.info("Exception occurred while getting InnoDB status");
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					LOGGER.info("Exception occurred ", e);
				}
			}
			if(errorTrace.toLowerCase().contains("timeout")) {
				String sql = "show processlist";
				try (Connection conn = FacilioConnectionPool.INSTANCE.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql);ResultSet rs = pstmt.executeQuery()) {
					body.append("\n\nProcess List : \n------------\n\n")
						.append(FacilioTablePrinter.getResultSetData(rs));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					LOGGER.info("Exception occurred ", e);
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
				result.put(prop.get("name"), prop.get("value"));
			}
		}
		return result;		
	}
    public static Boolean verifiedUser(long userID) throws Exception {
//    	GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
//    	.andCustomWhere("", userID)
//    	
    	
    	// System.out.println("TableNAme " + AccountConstants.getUserModule().getTableName());
    //	System.out.println("ID" + userID);
    	
    	GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getOrgUserFields())
				.table(AccountConstants.getOrgUserModule().getTableName())
				.andCustomWhere("ORG_USERID = ?", userID);
    	
    	List<Map<String, Object>> props = selectBuilder.get();
    	Long ouid = null;
		if (props != null && !props.isEmpty()) {
			Map<String, Object> prop = props.get(0);
			ouid = (Long) prop.get("uid");
		}
    	
    	GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
		.table(AccountConstants.getUserModule().getTableName())
		.fields(AccountConstants.getUserFields())
		.andCustomWhere("USERID = ?", ouid );
    	Map<String, Object> prop = new HashMap<>();
	    prop.put("userVerified", true);
	    updateBuilder.update(prop);
    	
    	return true;
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
				result.put(prop.get("name"), prop.get("value"));
			}
		}
		return result;		
	}
    
    public static List<Long> getMySiteIds() throws Exception {
    	FacilioModule accessibleSpaceMod = ModuleFactory.getAccessibleSpaceModule();
		GenericSelectRecordBuilder selectAccessibleBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getAccessbileSpaceFields())
				.table(accessibleSpaceMod.getTableName())
				.andCustomWhere("ORG_USER_ID = ?", AccountUtil.getCurrentAccount().getUser().getOuid());
		List<Map<String, Object>> props = selectAccessibleBuilder.get();
		Set<Long> siteIds = new HashSet<>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				Long siteId = (Long) prop.get("siteId");
				if (siteId != null) {
					siteIds.add(siteId);
				}
			}
		}
		List<Long> toArray = new ArrayList<>(siteIds);
		return toArray;
    }
    
    public static List<BaseSpaceContext> getMySites() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
		
		FacilioModule accessibleSpaceMod = ModuleFactory.getAccessibleSpaceModule();
		GenericSelectRecordBuilder selectAccessibleBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getAccessbileSpaceFields())
				.table(accessibleSpaceMod.getTableName())
				.andCustomWhere("ORG_USER_ID = ?", AccountUtil.getCurrentAccount().getUser().getOuid());

		List<Map<String, Object>> props = selectAccessibleBuilder.get();
		List<Long> siteIds = new ArrayList<>();
		if (props != null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				Long siteId = (Long) prop.get("siteId");
				if (siteId != null) {
					siteIds.add(siteId);
				}
			}
		}
		
		SelectRecordsBuilder<BaseSpaceContext> selectBuilder = new SelectRecordsBuilder<BaseSpaceContext>()
				.select(fields)
				.module(module)
				.beanClass(BaseSpaceContext.class)
				.andCondition(CriteriaAPI.getCondition("SPACE_TYPE", "spaceType", String.valueOf(SpaceType.SITE.getIntVal()), NumberOperators.EQUALS));
		
		List<BaseSpaceContext> accessibleBaseSpace;
		if (siteIds.isEmpty()) {
			accessibleBaseSpace = selectBuilder.get();
		} else {
			accessibleBaseSpace = selectBuilder.andCondition(CriteriaAPI.getIdCondition(siteIds, module)).get();
		}
		
		if (accessibleBaseSpace == null || accessibleBaseSpace.isEmpty()) {
			return Collections.emptyList();
		}
		
		return accessibleBaseSpace;
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
					readings = new ArrayList<>(1);
					readings.add(reading);
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
    
    public static <T> List<T> getList (FacilioContext context, String singularKey, String pluralKey) {
    	List<T> list = (List<T>) context.get(pluralKey);
		if(list == null) {
			T singleTObj = (T) context.get(singularKey);
			if (singleTObj != null) {
				list = Collections.singletonList(singleTObj);
			}
		}
		return list;
    }
    
    public static String getStackTraceString(StackTraceElement[] traces) {
    	StringJoiner joiner = new StringJoiner("\n");
    	for (StackTraceElement trace : traces) {
    		joiner.add(trace.toString());
    	}
    	joiner.add("###################################################");
    	return joiner.toString();
    }
    
    public static Pair<Double, Double> getSafeLimitForField(long fieldId) throws Exception {
    	Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("READING_FIELD_ID", "readingFieldId", String.valueOf(fieldId), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", String.valueOf(RuleType.VALIDATION_RULE.getIntVal()), NumberOperators.EQUALS));
        List<ReadingRuleContext> readingRules = ReadingRuleAPI.getReadingRules(criteria);
        Double min = null;
        Double max = null;
        if (readingRules != null && !readingRules.isEmpty()) {
        	List<Long> workFlowIds = readingRules.stream().map(ReadingRuleContext::getWorkflowId).collect(Collectors.toList());
            Map<Long, WorkflowContext> workflowMap = WorkflowUtil.getWorkflowsAsMap(workFlowIds, true);
            new HashMap<>();
            
        	for (ReadingRuleContext r:  readingRules) {
        		long workflowId = r.getWorkflowId();
        		if (workflowId != -1) {
        			r.setWorkflow(workflowMap.get(workflowId));
        		}
        		if (r.getWorkflow().getResultEvaluator().equals("(b!=-1&&a<b)||(c!=-1&&a>c)")) {
        			
        			List <ExpressionContext> expresions = new ArrayList<>();
        			for(WorkflowExpression worklfowExp : r.getWorkflow().getExpressions()) {
        				
        				if(worklfowExp instanceof ExpressionContext) {
        					expresions.add((ExpressionContext) worklfowExp);
        				}
        			}
        			Optional<ExpressionContext> exp = expresions.stream().filter(e -> {return e.getName().equals("b");}).findFirst();
        			if (exp.isPresent()) {
        				min = Double.parseDouble((String) exp.get().getConstant());
        				if (min == -1) {
        					min = null;
        				}
        			}
        			
        			exp = expresions.stream().filter(e -> {return e.getName().equals("c");}).findFirst();
        			if (exp.isPresent()) {
        				max = Double.parseDouble((String) exp.get().getConstant());
        				if (max == -1) {
        					max = null;
        				}
        			}
        		}
        	}
        }
    	return Pair.of(min, max);
    }

	public static void loadTaskLookups(List<TaskContext> tasks) throws Exception {
		if(tasks != null && !tasks.isEmpty()) {
			List<Long> resourceIds = tasks.stream()
											.filter(task -> task.getResource() != null)
											.map(task -> task.getResource().getId())
											.collect(Collectors.toList());
			Map<Long, ResourceContext> resources = ResourceAPI.getExtendedResourcesAsMapFromIds(resourceIds, true);
			if(resources != null && !resources.isEmpty()) {
				for(TaskContext task: tasks) {
					ResourceContext resource = task.getResource();
					if(resource != null) {
						ResourceContext resourceDetail = resources.get(resource.getId());
						task.setResource(resourceDetail);
					}
				}
			}
			
			TicketStatusContext open = TicketAPI.getStatus("Submitted");
			TicketStatusContext closed = TicketAPI.getStatus("Closed");
			
			tasks.stream().forEach(task -> {
				if (task.getStatusNewEnum() == null || task.getStatusNewEnum() == TaskStatus.OPEN) {
					task.setStatus(open);
				} else {
					task.setStatus(closed);
				}
			});
			
			
		}
	}
	
	public static Map<String, List> getRecordMap(FacilioContext context) {
		Map<String, List> recordMap = (Map<String, List>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
		if (recordMap == null) {
			List records = (List) context.get(FacilioConstants.ContextNames.RECORD_LIST);
			if(records == null) {
				Object record = context.get(FacilioConstants.ContextNames.RECORD);
				if(record != null) {
					records = Collections.singletonList(record);
				}
			}
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			if (moduleName == null || moduleName.isEmpty() || records == null || records.isEmpty()) {
				LOGGER.log(Level.WARN, "Module Name / Records is null/ empty ==> "+moduleName+"==>"+records);
				return null;
			}
			
			recordMap = Collections.singletonMap(moduleName, records);
		}
		return recordMap;
	}
	
	public static Map<String, Map<Long, List<UpdateChangeSet>>> getChangeSetMap(FacilioContext context) {
		Map<String, Map<Long, List<UpdateChangeSet>>> changeSetMap = (Map<String, Map<Long, List<UpdateChangeSet>>>) context.get(FacilioConstants.ContextNames.CHANGE_SET_MAP);
		if (changeSetMap == null) {
			Map<Long, List<UpdateChangeSet>> changeSet = (Map<Long, List<UpdateChangeSet>>) context.get(FacilioConstants.ContextNames.CHANGE_SET);
			if (changeSet != null) {
				String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
				changeSetMap = Collections.singletonMap(moduleName, changeSet);
			}
		}
		return changeSetMap;
	}
	
	public static List<FacilioModule> getModulesWithFields(Context context) throws Exception {
		List<FacilioField> fields = (List<FacilioField>) context.get(FacilioConstants.ContextNames.MODULE_FIELD_LIST);
		if (fields != null) {
			FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
			if(module == null) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
				module = modBean.getModule(moduleName);
			}
			module.setFields(fields);
			return Collections.singletonList(module);
		}
		else {
			return (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		}
	}
	
	public static List<FacilioModule> getModules(Context context) {
		List<FacilioModule> modules = (List<FacilioModule>) context.get(FacilioConstants.ContextNames.MODULE_LIST);
		if (modules == null) {
			FacilioModule module = (FacilioModule) context.get(FacilioConstants.ContextNames.MODULE);
			if (module != null) {
				modules = Collections.singletonList(module);
			}
		}
		return modules;
	}
	
	public static List<OrgUnitsContext> orgUnitsList() throws Exception{
		return UnitsUtil.getOrgUnitsList();
	}
	public static JSONObject metricWithUnits() {
		JSONObject metricswithUnits = new JSONObject();
		
		Map<Metric, Collection<Unit>> metricWithUnit = Unit.getMetricUnitMap();
		for( Metric metric :metricWithUnit.keySet()) {
			
			Collection<Unit> units = metricWithUnit.get(metric);
			
			JSONArray unitsJson = new JSONArray();
			for(Unit unit :units) {
				unitsJson.add(unit);
			}
			metricswithUnits.put(metric.getMetricId(), unitsJson);
		}
		return metricswithUnits;
	}
	public static Collection<Metric> getAllMetrics(){
		return Metric.getAllMetrics();
	}
	public static JSONObject getOperators() {
		JSONObject operators = new JSONObject();
		for(FieldType ftype: FieldType.values()) {
			operators.put(ftype.name(), ftype.getOperators());
		}
		return operators;
	}
	
	public static void addActivityToContext(long parentId, long ttime, ActivityType type, JSONObject info, FacilioContext context) {
		ActivityContext activity = new ActivityContext();
		activity.setParentId(parentId);
		
		if (ttime == -1) {
			activity.setTtime(System.currentTimeMillis());
		}
		else {
			activity.setTtime(ttime);
		}
		activity.setType(type);
		activity.setInfo(info);
		activity.setDoneBy(AccountUtil.getCurrentUser());
		
		List<ActivityContext> activities = (List<ActivityContext>) context.get(FacilioConstants.ContextNames.ACTIVITY_LIST);
		if (activities == null) {
			activities = new ArrayList<>();
		}
		activities.add(activity);
		context.put(FacilioConstants.ContextNames.ACTIVITY_LIST, activities);

	}
	
	public static void addEventType (EventType type, FacilioContext context) {
		List<EventType> eventTypes = (List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST);
		if (eventTypes == null) {
			eventTypes = new ArrayList<>();
			EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
			if (eventType != null) {
				eventTypes.add(eventType);
			}
		}
		eventTypes = eventTypes instanceof ArrayList ? eventTypes : new ArrayList<>(eventTypes);
		eventTypes.add(type);
		context.put(FacilioConstants.ContextNames.EVENT_TYPE_LIST, eventTypes);
	}
}
