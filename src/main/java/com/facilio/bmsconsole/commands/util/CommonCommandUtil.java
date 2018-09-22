package com.facilio.bmsconsole.commands.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainExceptionHandler;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.BaseSpaceContext;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.context.SupportEmailContext;
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
import com.facilio.bmsconsole.util.DateTimeUtil;
import com.facilio.bmsconsole.util.FacilioTablePrinter;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.bmsconsole.util.WorkflowRuleAPI;
import com.facilio.bmsconsole.view.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.WorkflowRuleContext.RuleType;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.queue.FAWSQueue;
import com.facilio.sql.DBUtil;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowExpression;
import com.facilio.workflows.util.WorkflowUtil;

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
				.append(AwsUtil.getConfig("app.url"));
			
			if (e != null) {
				body.append("\n\nTrace : \n--------\n")
					.append(ExceptionUtils.getStackTrace(e));
			}
			
			if (info != null && !info.isEmpty()) {
				body.append("\n")
					.append(info);
			}
			
			if (e != null) {
				checkDB(e.getMessage(), body);
			}
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
			if(msg.toLowerCase().contains("deadlock") || body.toString().toLowerCase().contains("deadlock")) {
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
				result.put((String) prop.get("name"), prop.get("value"));
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
    
    public static Pair<Double, Double> getSafeLimitForField(long fieldId) throws Exception {
    	Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition("READING_FIELD_ID", "readingFieldId", String.valueOf(fieldId), NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", String.valueOf(RuleType.VALIDATION_RULE.getIntVal()), NumberOperators.EQUALS));
        List<ReadingRuleContext> readingRules = WorkflowRuleAPI.getReadingRules(criteria);
        Double min = null;
        Double max = null;
        if (readingRules != null && !readingRules.isEmpty()) {
        	List<Long> workFlowIds = readingRules.stream().map(ReadingRuleContext::getWorkflowId).collect(Collectors.toList());
            Map<Long, WorkflowContext> workflowMap = WorkflowUtil.getWorkflowsAsMap(workFlowIds, true);
            Map<Long, List<ReadingRuleContext>> fieldVsRules = new HashMap<>();
            
        	for (ReadingRuleContext r:  readingRules) {
        		long workflowId = r.getWorkflowId();
        		if (workflowId != -1) {
        			r.setWorkflow(workflowMap.get(workflowId));
        		}
        		if (r.getWorkflow().getResultEvaluator().equals("(b!=-1&&a<b)||(c!=-1&&a>c)")) {
        			
        			List <ExpressionContext> expresions = new ArrayList<>();
        			for(WorkflowExpression worklfowExp : r.getWorkflow().getWorkflowExpressions()) {
        				
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
    
    public static void addCleanUpCommand(Chain c)
	{
		c.addCommand(new FacilioChainExceptionHandler());
	}
}
