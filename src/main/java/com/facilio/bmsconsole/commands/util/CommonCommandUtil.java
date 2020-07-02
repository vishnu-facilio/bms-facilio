package com.facilio.bmsconsole.commands.util;

import com.facilio.accounts.bean.OrgBean;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.accounts.util.AccountUtil.FeatureLicense;
import com.facilio.activity.ActivityContext;
import com.facilio.activity.ActivityType;
import com.facilio.activity.AlarmActivityContext;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.*;
import com.facilio.bmsconsole.context.BaseSpaceContext.SpaceType;
import com.facilio.bmsconsole.context.FormulaFieldContext.FormulaFieldType;
import com.facilio.bmsconsole.context.TaskContext.TaskStatus;
import com.facilio.bmsconsole.util.FacilioTablePrinter;
import com.facilio.bmsconsole.util.*;
import com.facilio.bmsconsole.workflow.rule.EventType;
import com.facilio.bmsconsole.workflow.rule.ReadingRuleContext;
import com.facilio.bmsconsole.workflow.rule.WorkflowRuleContext.RuleType;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.*;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.facilio.queue.FacilioQueueException;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.time.DateTimeUtil;
import com.facilio.unitconversion.Metric;
import com.facilio.unitconversion.Unit;
import com.facilio.unitconversion.UnitsUtil;
import com.facilio.workflows.context.ExpressionContext;
import com.facilio.workflows.context.WorkflowContext;
import com.facilio.workflows.context.WorkflowExpression;
import com.facilio.workflows.util.WorkflowUtil;
import com.opensymphony.xwork2.ActionContext;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

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
								Object lookupVal = FieldUtil.getLookupVal(lookupField, (long) props.get("id"));
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
			
			if (FacilioProperties.isProduction()) {
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
										.append(FacilioProperties.getConfig("clientapp.url"))
										.append("\n\nOrg Info : \n--------\n")
										.append(org.toString())
										;
				json.put("message", body.toString());
				
				FacilioFactory.getEmailClient().sendEmail(json);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
								
	}
	
	public static void emailNewLead (String subject, String name, String email, String locale, String domain) {
		try {
			
			if (FacilioProperties.isProduction()) {
				JSONObject json = new JSONObject();
				json.put("sender", "alert@facilio.com");
				json.put("to", "getsmart@facilio.com");
				json.put("subject", subject);
				
				StringBuilder body = new StringBuilder()
										.append("\n\nInfo : \n--------\n")
										.append("\n Name : ").append(name)
										.append("\n Email : ").append(email)
										.append("\n Locale : ").append(locale)
										.append("\n Domain : ").append(domain)
										;
				json.put("message", body.toString());
				
				FacilioFactory.getEmailClient().sendEmail(json);
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
			String environment = FacilioProperties.getConfig("environment");
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
			if(org != null) {
				body.append("\nOrg Info: \n").append(org.toString());
			}
			body.append("\n\nOrg Time : ").append(DateTimeUtil.getDateTime())
				.append("\nIndian Time : ").append(DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata")))
				.append("\nThread Name : ")
				.append(Thread.currentThread().getName())
				.append("\n\nMsg : ")
				.append(msg)
			;
			
			if (ActionContext.getContext() != null && ServletActionContext.getRequest() != null) {
				User currentUser = AccountUtil.getCurrentUser();
				body.append("\nUser: ")
				.append(currentUser.getName()).append(" - ").append(currentUser.getEmail()).append(" - ").append(currentUser.getOuid())
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
			//FacilioFactory.getEmailClient().sendEmail(json);
//			if(FacilioProperties.isProduction() && !FacilioProperties.isOnpremise()) {
//				FAWSQueue.sendMessage("Exception", message);
//			}
			// New FacilioException Queue code need to remove condition for Production
			if(FacilioProperties.isProduction() && !FacilioProperties.isOnpremise()) {
				// LOGGER.debug("#####Facilio Exception Queue is push Msg is Entered"+message);
			FacilioQueueException.addException(message);
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
    	
    	OrgBean bean = (OrgBean) BeanFactory.lookup("OrgBean", orgId);	
    	return bean.orgInfo();	
	}
      public static JSONObject getOrgInfo() throws Exception {
    	
    	JSONObject result = new JSONObject();
    	FacilioModule module = AccountConstants.getOrgInfoModule();
    	GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(AccountConstants.getOrgInfoFields())
				.table(module.getTableName());
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			for (Map<String, Object> prop : props) {
				result.put(prop.get("name"), prop.get("value"));
			}
		}
		return result;		
	}
    
     //will be changed soon
    public static List<Long> getMySiteIds() throws Exception {
    	
    	if(!AccountUtil.isFeatureEnabled(FeatureLicense.SCOPING)) {
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
    	else {
    		List<SiteContext> sites = SpaceAPI.getAllSites();
    		List<Long> siteIds = new ArrayList<>();
    		if(CollectionUtils.isNotEmpty(sites)) {
    			for(SiteContext site : sites) {
    				siteIds.add(site.getSiteId());
    			}
    		}
    	  return siteIds;	
    	}
    }
    
    //will be removed soon..so please dont use this further
    public static List<BaseSpaceContext> getMySites() throws Exception {
    	if(!AccountUtil.isFeatureEnabled(FeatureLicense.SCOPING)) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.BASE_SPACE);
			List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.BASE_SPACE);
			
			List<Long> siteIds = new ArrayList<>();
			
			if (AccountUtil.getCurrentUser() != null) {
				FacilioModule accessibleSpaceMod = ModuleFactory.getAccessibleSpaceModule();
				GenericSelectRecordBuilder selectAccessibleBuilder = new GenericSelectRecordBuilder()
						.select(AccountConstants.getAccessbileSpaceFields())
						.table(accessibleSpaceMod.getTableName())
						.andCustomWhere("ORG_USER_ID = ?", AccountUtil.getCurrentAccount().getUser().getOuid());
				
				List<Map<String, Object>> props = selectAccessibleBuilder.get();
				
				if (props != null && !props.isEmpty()) {
					for(Map<String, Object> prop : props) {
						Long siteId = (Long) prop.get("siteId");
						if (siteId != null) {
							siteIds.add(siteId);
						}
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
    	else {
    		List<SiteContext> sites = SpaceAPI.getAllSites(false);
    		if(CollectionUtils.isNotEmpty(sites)) {
    			List<BaseSpaceContext> bsList = new ArrayList<BaseSpaceContext>();
    			for(SiteContext site : sites) {
    				bsList.add(site);
    			}
    			return bsList;
    		}
    		return Collections.emptyList();
    	}
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
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
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
    
    public static Pair<Double, Double> getSafeLimitForField(long fieldId) throws Exception {
    		Double min = null;
        Double max = null;
        
    		FormulaFieldContext field = FormulaFieldAPI.getFormulaFieldFromReadingField(fieldId);
    		if (field != null && field.getFormulaFieldTypeEnum() == FormulaFieldType.ENPI && (field.getMinTarget() > 0 || field.getTarget() > 0 ) ) {
    	        if (field.getMinTarget() > 0) {
    	        		min = field.getMinTarget();
    	        }
    	        else if (field.getTarget() > 0) {
    	        		max = field.getTarget();
    	        }
    		}
    		else {
    			Criteria criteria = new Criteria();
    	        criteria.addAndCondition(CriteriaAPI.getCondition("READING_FIELD_ID", "readingFieldId", String.valueOf(fieldId), NumberOperators.EQUALS));
    	        criteria.addAndCondition(CriteriaAPI.getCondition("RULE_TYPE", "ruleType", String.valueOf(RuleType.VALIDATION_RULE.getIntVal()), NumberOperators.EQUALS));
    	        List<ReadingRuleContext> readingRules = ReadingRuleAPI.getReadingRules(criteria);
    	        
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
			
			FacilioStatus open = TicketAPI.getStatus("Submitted");
			FacilioStatus closed = TicketAPI.getStatus("Closed");
			
			tasks.stream().forEach(task -> {
				if (task.getStatusNewEnum() == null || task.getStatusNewEnum() == TaskStatus.OPEN) {
					task.setStatus(open);
				} else {
					task.setStatus(closed);
				}
			});
			
			
		}
	}

	public static void addToRecordMap(FacilioContext context, String moduleName, ModuleBaseWithCustomFields record) {
		Map<String, List> recordMap = (Map<String, List>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
		if (recordMap == null) {
			recordMap = new HashMap<>();
			context.put(FacilioConstants.ContextNames.RECORD_MAP, recordMap);
			recordMap.put(moduleName, Collections.singletonList(record));
		}
		else {
			List records = recordMap.get(moduleName);
			if (records == null) {
				recordMap.put(moduleName, Collections.singletonList(record));
			}
			else {
				if (!(records instanceof ArrayList)) {
					records = new ArrayList<>(records);
					recordMap.put(moduleName, records);
				}
				records.add(record);
			}
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
	
	public static void addBulkActivityToContext(List<Long> parentIds, long ttime, ActivityType type, Map<Long, JSONObject> infoMap, FacilioContext context) {
		List<ActivityContext> activities = (List<ActivityContext>) context.get(FacilioConstants.ContextNames.ACTIVITY_LIST);
		if (activities == null) {
			activities = new ArrayList<>();
			for (int i = 0; i < parentIds.size(); i++) {
				ActivityContext activity = new ActivityContext();
				activity.setParentId(parentIds.get(i));
				
				if (ttime == -1) {
					activity.setTtime(System.currentTimeMillis());
				}
				else {
					activity.setTtime(ttime);
				}
				activity.setType(type);
				activity.setInfo(infoMap.get(parentIds.get(i)));
				activity.setDoneBy(AccountUtil.getCurrentUser());
				activities.add(activity);		
			}
		}
		context.put(FacilioConstants.ContextNames.ACTIVITY_LIST, activities);
	}
	
    public static void addAlarmActivityToContext(long parentId, long ttime, ActivityType type, JSONObject info, FacilioContext context, long lastOccurrenceId) {
        AlarmActivityContext activity = new AlarmActivityContext();
        activity.setParentId(parentId);
        activity.setOccurrenceId(lastOccurrenceId);
        if (ttime == -1) {
            activity.setTtime(System.currentTimeMillis());
        }
        else {
            activity.setTtime(ttime);
        }
        activity.setType(type);
        activity.setInfo(info);
        activity.setDoneBy(AccountUtil.getCurrentUser());
        List<AlarmActivityContext> activities = (List<AlarmActivityContext>) context.get(FacilioConstants.ContextNames.ACTIVITY_LIST);
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
	
	public static List<EventType> getEventTypes(Context context) {
		List<EventType> eventTypes = (List<EventType>) context.get(FacilioConstants.ContextNames.EVENT_TYPE_LIST);
		if (eventTypes == null) {
			EventType eventType = (EventType) context.get(FacilioConstants.ContextNames.EVENT_TYPE);
			if (eventType != null) {
				eventTypes = new ArrayList<>();
				eventTypes.add(eventType);
			}
		}
		return eventTypes;
	}

	public static void migrateFieldAccessType() throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER);
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		Map<String, Long> calculatedVals = new HashMap<>();
		calculatedVals.put("subject", calculateAccessType(FacilioField.AccessType.CREATE, FacilioField.AccessType.CRITERIA, FacilioField.AccessType.SORT, FacilioField.AccessType.READ));
		calculatedVals.put("description", calculateAccessType(FacilioField.AccessType.CREATE, FacilioField.AccessType.CRITERIA, FacilioField.AccessType.SORT, FacilioField.AccessType.READ));
		calculatedVals.put("priority", calculateAccessType(FacilioField.AccessType.CREATE, FacilioField.AccessType.EDIT, FacilioField.AccessType.CRITERIA, FacilioField.AccessType.REPORT, FacilioField.AccessType.SORT, FacilioField.AccessType.READ));
		calculatedVals.put("category", calculateAccessType(FacilioField.AccessType.CREATE, FacilioField.AccessType.EDIT, FacilioField.AccessType.CRITERIA, FacilioField.AccessType.REPORT, FacilioField.AccessType.SORT, FacilioField.AccessType.READ));
		calculatedVals.put("type", calculateAccessType(FacilioField.AccessType.CREATE, FacilioField.AccessType.EDIT, FacilioField.AccessType.CRITERIA, FacilioField.AccessType.REPORT, FacilioField.AccessType.SORT, FacilioField.AccessType.READ));
		calculatedVals.put("sourceType", calculateAccessType(FacilioField.AccessType.CRITERIA));
		calculatedVals.put("assignmentGroup", calculateAccessType(FacilioField.AccessType.CREATE, FacilioField.AccessType.EDIT, FacilioField.AccessType.CRITERIA, FacilioField.AccessType.REPORT, FacilioField.AccessType.SORT, FacilioField.AccessType.READ));
		calculatedVals.put("assignedTo", calculateAccessType(FacilioField.AccessType.CREATE, FacilioField.AccessType.EDIT, FacilioField.AccessType.CRITERIA, FacilioField.AccessType.REPORT, FacilioField.AccessType.SORT, FacilioField.AccessType.READ));
		calculatedVals.put("createdBy", calculateAccessType(FacilioField.AccessType.CRITERIA));
		calculatedVals.put("assignedBy", calculateAccessType(FacilioField.AccessType.CRITERIA));
		calculatedVals.put("resource", calculateAccessType(FacilioField.AccessType.CREATE, FacilioField.AccessType.EDIT, FacilioField.AccessType.CRITERIA, FacilioField.AccessType.REPORT, FacilioField.AccessType.SORT, FacilioField.AccessType.READ));
		calculatedVals.put("dueDate", calculateAccessType(FacilioField.AccessType.CREATE, FacilioField.AccessType.EDIT, FacilioField.AccessType.CRITERIA, FacilioField.AccessType.REPORT, FacilioField.AccessType.SORT, FacilioField.AccessType.READ));
		calculatedVals.put("serialNumber", calculateAccessType(FacilioField.AccessType.CRITERIA));
		calculatedVals.put("noOfNotes", calculateAccessType(FacilioField.AccessType.READ));
		calculatedVals.put("noOfAttachments", calculateAccessType(FacilioField.AccessType.READ));
		calculatedVals.put("noOfTasks", calculateAccessType(FacilioField.AccessType.READ));
		calculatedVals.put("noOfClosedTasks", calculateAccessType(FacilioField.AccessType.READ));
		calculatedVals.put("scheduledStart", calculateAccessType(FacilioField.AccessType.CRITERIA, FacilioField.AccessType.REPORT, FacilioField.AccessType.SORT, FacilioField.AccessType.READ));
		calculatedVals.put("estimatedStart", calculateAccessType());
		calculatedVals.put("estimatedEnd", calculateAccessType());
		calculatedVals.put("actualWorkStart", calculateAccessType(FacilioField.AccessType.CRITERIA, FacilioField.AccessType.REPORT, FacilioField.AccessType.SORT, FacilioField.AccessType.READ));
		calculatedVals.put("actualWorkEnd", calculateAccessType(FacilioField.AccessType.CRITERIA, FacilioField.AccessType.REPORT, FacilioField.AccessType.SORT, FacilioField.AccessType.READ));
		calculatedVals.put("actualWorkDuration", calculateAccessType(FacilioField.AccessType.CRITERIA, FacilioField.AccessType.REPORT, FacilioField.AccessType.SORT, FacilioField.AccessType.READ));
		calculatedVals.put("estimatedWorkDuration", calculateAccessType(FacilioField.AccessType.CRITERIA, FacilioField.AccessType.REPORT, FacilioField.AccessType.SORT, FacilioField.AccessType.READ));
		calculatedVals.put("resumedWorkStart", calculateAccessType());
		calculatedVals.put("moduleState", calculateAccessType(FacilioField.AccessType.CRITERIA, FacilioField.AccessType.REPORT, FacilioField.AccessType.SORT, FacilioField.AccessType.READ));
		calculatedVals.put("stateFlowId", calculateAccessType());
		calculatedVals.put("serviceRequest", calculateAccessType(FacilioField.AccessType.CRITERIA, FacilioField.AccessType.READ));
		calculatedVals.put("slaPolicyId", calculateAccessType());
		calculatedVals.put("requester", calculateAccessType(FacilioField.AccessType.CREATE, FacilioField.AccessType.CRITERIA, FacilioField.AccessType.READ));
		calculatedVals.put("createdTime", calculateAccessType(FacilioField.AccessType.CRITERIA, FacilioField.AccessType.REPORT, FacilioField.AccessType.SORT, FacilioField.AccessType.READ));
		calculatedVals.put("modifiedTime", calculateAccessType(FacilioField.AccessType.CRITERIA, FacilioField.AccessType.SORT, FacilioField.AccessType.READ));
		calculatedVals.put("pm", calculateAccessType(FacilioField.AccessType.CRITERIA, FacilioField.AccessType.REPORT, FacilioField.AccessType.READ));
		calculatedVals.put("isWorkDurationChangeAllowed", calculateAccessType());
		calculatedVals.put("approvalState", calculateAccessType());
		calculatedVals.put("approvalRuleId", calculateAccessType(FacilioField.AccessType.CRITERIA, FacilioField.AccessType.READ));
		calculatedVals.put("requestedBy", calculateAccessType(FacilioField.AccessType.CRITERIA, FacilioField.AccessType.READ));
		calculatedVals.put("sendForApproval", calculateAccessType(FacilioField.AccessType.CRITERIA, FacilioField.AccessType.READ));
		calculatedVals.put("qrEnabled", calculateAccessType());
		calculatedVals.put("urgency", calculateAccessType(FacilioField.AccessType.CREATE, FacilioField.AccessType.CRITERIA, FacilioField.AccessType.SORT, FacilioField.AccessType.READ));
		calculatedVals.put("isSignatureRequired", calculateAccessType());
		calculatedVals.put("signature", calculateAccessType(FacilioField.AccessType.READ));
		calculatedVals.put("totalCost", calculateAccessType(FacilioField.AccessType.CRITERIA, FacilioField.AccessType.REPORT, FacilioField.AccessType.READ));
		calculatedVals.put("trigger", calculateAccessType(FacilioField.AccessType.CRITERIA));
		calculatedVals.put("sysCreatedTime", calculateAccessType());
		calculatedVals.put("jobStatus", calculateAccessType());
		calculatedVals.put("preRequestStatus", calculateAccessType(FacilioField.AccessType.CRITERIA));
		calculatedVals.put("photoMandatory", calculateAccessType());
		calculatedVals.put("prerequisiteEnabled", calculateAccessType(FacilioField.AccessType.CRITERIA));
		calculatedVals.put("allowNegativePreRequisite", calculateAccessType());
		calculatedVals.put("preRequisiteApproved", calculateAccessType());
		calculatedVals.put("parentWO", calculateAccessType(FacilioField.AccessType.CRITERIA));
		calculatedVals.put("woCreationOffset", calculateAccessType());
		calculatedVals.put("responseDueDate", calculateAccessType(FacilioField.AccessType.CRITERIA, FacilioField.AccessType.REPORT, FacilioField.AccessType.SORT, FacilioField.AccessType.READ));
		calculatedVals.put("safetyPlan", calculateAccessType(FacilioField.AccessType.CREATE, FacilioField.AccessType.READ));
		calculatedVals.put("workPermitIssued", calculateAccessType(FacilioField.AccessType.CRITERIA));
		calculatedVals.put("workPermitNeeded", calculateAccessType(FacilioField.AccessType.CREATE, FacilioField.AccessType.EDIT, FacilioField.AccessType.READ));
		calculatedVals.put("tenant", calculateAccessType(FacilioField.AccessType.CREATE, FacilioField.AccessType.CRITERIA, FacilioField.AccessType.REPORT, FacilioField.AccessType.READ));
		calculatedVals.put("vendor", calculateAccessType(FacilioField.AccessType.CREATE, FacilioField.AccessType.EDIT, FacilioField.AccessType.CRITERIA, FacilioField.AccessType.REPORT, FacilioField.AccessType.READ));

		List<FacilioField> selectFieldFields = FieldFactory.getSelectFieldFields();
		Map<String, FacilioField> fieldFieldMap = FieldFactory.getAsMap(selectFieldFields);

		for (Map.Entry<String, Long> entry: calculatedVals.entrySet()) {
			String key = entry.getKey();
			FacilioField facilioField = fieldMap.get(key);
			if (facilioField == null) {
				continue;
			}
			facilioField.setAccessType(entry.getValue());
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table("Fields")
					.fields(Arrays.asList(fieldFieldMap.get("accessType")))
					.andCustomWhere("ORGID = ? AND FIELDID = ?", AccountUtil.getCurrentOrg().getOrgId(), facilioField.getFieldId());
			updateBuilder.update(FieldUtil.getAsProperties(facilioField));
		}
	}

	private static long calculateAccessType(FacilioField.AccessType... accessTypes) {
		long result = 0;
		for (FacilioField.AccessType accessType: accessTypes) {
			result += accessType.getIntVal();
		}
		return result;
	}
	
	public static void handleLookupFormData(List<FacilioField> fields, Map<String, Object> data) {
		if (data == null) {
			return;
		}
		for(FacilioField field: fields) {
			if (field.getDataTypeEnum() != null && field.getDataTypeEnum() == FieldType.LOOKUP) {
				if (data.get(field.getName()) != null) {
					String val = data.get(field.getName()).toString();
					if (NumberUtils.isCreatable(val)) {
						data.put(field.getName(), FieldUtil.getEmptyLookedUpProp(Long.parseLong(val)));
					}
				}
			}
		}
	}

	public static void appendChangeSetMapToContext(Context context, Map<Long, List<UpdateChangeSet>> changeSet, String moduleName) {
		Map<String, Map<Long, List<UpdateChangeSet>>> oldChangeSetMap = (Map<String, Map<Long, List<UpdateChangeSet>>>) context.getOrDefault(FacilioConstants.ContextNames.CHANGE_SET_MAP, null);
		if (MapUtils.isNotEmpty(oldChangeSetMap)) {
			if (oldChangeSetMap.containsKey(moduleName)) {
				Map<Long, List<UpdateChangeSet>> oldModuleChangeSetMap = oldChangeSetMap.get(moduleName);
				if (MapUtils.isNotEmpty(oldModuleChangeSetMap)) {
					if (MapUtils.isNotEmpty(changeSet)) {
						for (Long recordId : changeSet.keySet()) {
							if (oldModuleChangeSetMap.containsKey(recordId)) {
								List<UpdateChangeSet> recordChangeSet = oldModuleChangeSetMap.get(recordId);
								recordChangeSet.addAll(changeSet.get(recordId));
							} else {
								oldModuleChangeSetMap.put(recordId, changeSet.get(recordId));
							}
						}
					}
				}
			} else {
				oldChangeSetMap.put(moduleName, changeSet);
			}
		} else {
			Map<String, Map<Long, List<UpdateChangeSet>>> newChangeSetMap = new HashMap<>();
			newChangeSetMap.put(moduleName, changeSet);
			context.put(FacilioConstants.ContextNames.CHANGE_SET_MAP, newChangeSetMap);
		}

	}

}
