package com.facilio.accounts.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.MapUtils;

import com.facilio.accounts.bean.GroupBean;
import com.facilio.accounts.bean.OrgBean;
import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.iam.accounts.util.IAMUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.service.FacilioService;

public class AccountUtil {

	private static ThreadLocal<Account> currentAccount = new ThreadLocal<Account>();
	public static final String JWT_DELIMITER = "#";
	
	public static void setCurrentAccount(Account account) throws Exception {
		currentAccount.set(account);
		setScopingMap(account);
	}
	
	public static void setCurrentAccount(long orgId) throws Exception {
		Organization org = null;
		org = FacilioService.runAsServiceWihReturn(() ->IAMUtil.getOrgBean().getOrgv2(orgId));
		
		if (org != null) {

			Account account = new Account(org, null);
			setCurrentAccount(account);
			User user = AccountUtil.getOrgBean().getSuperAdmin(org.getId());
			account.setUser(user);
			setScopingMap(account);
		}
	}
	
	public static void setScopingMap(Account account) throws Exception {
		if(account != null && account.getUser() != null && account.getOrg() != null) {
			long appId = account.getUser().getApplicationId();
			if(appId > 0) {
				account.setAppScopingMap(ApplicationApi.getScopingMapForApp(appId, account.getOrg().getOrgId()));
			}
		}
	}
	
	public static void setCurrentSiteId(long siteId) throws Exception {
		getCurrentAccount().setCurrentSiteId(siteId);
		setScopingMap(getCurrentAccount());
	}
	
	public static Account getCurrentAccount() {
		if (currentAccount.get() != null && !currentAccount.get().isScoped()) {
			return null;
		}
		return currentAccount.get();
	}
	
	public static Organization getCurrentOrg() {
		if (currentAccount.get() != null) {
			return currentAccount.get().getOrg();
		}
		return null;
	}
	
	public static User getCurrentUser() {
		if (currentAccount.get() != null) {
			return currentAccount.get().getUser();
		}
		return null;
	}
	
	public static void cleanCurrentAccount() {
		currentAccount.remove();
	}
	
	public static Object getSwitchScopingFieldValue(String fieldName) {
		if (currentAccount.get() != null) {
			Map<String, Object> switchScopingMap = currentAccount.get().getSwitchScopingMap();
			if(MapUtils.isNotEmpty(switchScopingMap)) {
				return switchScopingMap.get(fieldName);
			}
		}
		return null;
	}
	
	public static long getCurrentSiteId() {
		if (currentAccount.get() != null) {
			return currentAccount.get().getCurrentSiteId();
		}
		return -1;
	}
	
	public static void setSwitchScopingFieldValue(String fieldName, Object value) {
		if (currentAccount.get() != null) {
			Map<String, Object> switchScopingMap = currentAccount.get().getSwitchScopingMap();
			if(MapUtils.isEmpty(switchScopingMap)) {
				switchScopingMap = new HashMap<String, Object>();
			}
			switchScopingMap.put(fieldName, value);
		}
	}
	
	public static Map<String, Object> getSwitchScopingFieldMap() {
		if (currentAccount.get() != null) {
			Map<String, Object> switchScopingMap = currentAccount.get().getSwitchScopingMap();
			return switchScopingMap;
		}
		return null;
	}
	
	public static long getCurrentUserSessionId() {
		if (currentAccount.get() != null) {
			return currentAccount.get().getUserSessionId();
		}
		return -1;
	}

	public static Map<Long, Map<String, Object>> getCurrentAppScopingMap() {
		if (currentAccount.get() != null) {
			return currentAccount.get().getAppScopingMap();
		}
		return null;
	}
	
	public static Map<String, Object> getCurrentAppScopingMap(long modId) {
		if (currentAccount.get() != null) {
			Map<Long, Map<String, Object>> scopingMap = currentAccount.get().getAppScopingMap();
			if(MapUtils.isNotEmpty(scopingMap)){
				return scopingMap.get(modId);
			}
		}
		return null;
	}
	
	public static void incrementInsertQueryCount(int count) {
		if(currentAccount.get() != null) {
			currentAccount.get().incrementInsertQueryCount(count);
		}
	}

	public static void incrementInsertQueryTime(long duration) {
		if(currentAccount.get() != null) {
			currentAccount.get().incrementInsertQueryTime(duration);
		}
	}

	public static void incrementSelectQueryCount(int count) {
		if(currentAccount.get() != null) {
			currentAccount.get().incrementSelectQueryCount(count);
		}
	}

	public static void incrementSelectQueryTime(long duration) {
		if(currentAccount.get() != null) {
			currentAccount.get().incrementSelectQueryTime(duration);
		}
	}

	public static void incrementUpdateQueryCount(int count) {
		if (currentAccount.get() != null) {
			currentAccount.get().incrementUpdateQueryCount(count);
		}
	}

	public static void incrementUpdateQueryTime(long duration) {
		if(currentAccount.get() != null) {
			currentAccount.get().incrementUpdateQueryTime(duration);
		}
	}

	public static void incrementDeleteQueryCount(int count) {
		if(currentAccount.get() != null) {
			currentAccount.get().incrementDeleteQueryCount(count);
		}
	}

	public static void incrementDeleteQueryTime(long duration) {
		if(currentAccount.get() != null) {
				currentAccount.get().incrementDeleteQueryTime(duration);
		}
	}

	public static void incrementRedisGetTime(long redisTime) {
		if(currentAccount.get() != null) {
			currentAccount.get().incrementRedisGetTime(redisTime);
		}
	}

	public static void incrementRedisPutTime(long redisTime) {
		if(currentAccount.get() != null) {
			currentAccount.get().incrementRedisPutTime(redisTime);
		}
	}

	public static void incrementRedisDeleteTime(long redisTime) {
		if(currentAccount.get() != null) {
			currentAccount.get().incrementRedisDeleteTime(redisTime);
		}
	}

	public static void incrementRedisGetCount(int redisQueries) {
		if(currentAccount.get() != null) {
			currentAccount.get().incrementRedisGetCount(redisQueries);
		}
	}

	public static void incrementRedisPutCount(int redisQueries) {
		if(currentAccount.get() != null) {
			currentAccount.get().incrementRedisPutCount(redisQueries);
		}
	}

	public static void incrementRedisDeleteCount(int redisQueries) {
		if(currentAccount.get() != null) {
			currentAccount.get().incrementRedisDeleteCount(redisQueries);
		}
	}
	
	public static UserBean getUserBean() throws Exception {
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
		return userBean;
	}

	public static UserBean getUserBean(long orgId) throws Exception {
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean",orgId);
		return userBean;
	}
	
	public static UserBean getTransactionalUserBean() throws Exception {
		return (UserBean) TransactionBeanFactory.lookup("UserBean");
	}
	
	public static UserBean getTransactionalUserBean(long orgId) throws Exception {
		return (UserBean) TransactionBeanFactory.lookup("UserBean",orgId);
	}
	
	
	public static OrgBean getOrgBean() throws Exception {
		OrgBean orgBean = (OrgBean) BeanFactory.lookup("OrgBean");
		return orgBean;
	}

	public static OrgBean getOrgBean(long orgId) throws Exception {
		OrgBean orgBean = (OrgBean) BeanFactory.lookup("OrgBean",orgId);
		return orgBean;
	}
	
	public static OrgBean getTransactionalOrgBean(long orgId) throws Exception {
		OrgBean orgBean = (OrgBean) TransactionBeanFactory.lookup("OrgBean",orgId);
		return orgBean;
	}
	
	public static GroupBean getGroupBean() throws Exception {
		GroupBean groupBean = (GroupBean) BeanFactory.lookup("GroupBean");
		return groupBean;
	}
	
	public static RoleBean getRoleBean() throws Exception {
		RoleBean roleBean = (RoleBean) BeanFactory.lookup("RoleBean");
		return roleBean;
	}
	public static RoleBean getRoleBean(long orgId) throws Exception {
		RoleBean roleBean = (RoleBean) BeanFactory.lookup("RoleBean",orgId);
		return roleBean;
	}

    public static void setReqUri(String requestURI) {
		if (currentAccount.get() != null) {
			currentAccount.get().setRequestUri(requestURI);
		}
    }
	public static void setRequestParams(Map<String, String[]> requestParams) {
		if (currentAccount.get() != null && requestParams != null) {
			StringBuilder builder = new StringBuilder("[");
			boolean isFirst = true;
			for (Map.Entry<String, String[]> param : requestParams.entrySet()) {
				if (isFirst) {
					isFirst = false;
				}
				else {
					builder.append(",");
				}

				builder.append(param.getKey())
						.append(" : ");
				if (param.getValue() != null) {
					if (param.getValue().length == 1) {
						builder.append(param.getValue()[0]);
					}
					else {
						builder.append(Arrays.toString(param.getValue()));
					}
				}
				else {
					builder.append(param.getValue());
				}
			}
			builder.append("]");
			currentAccount.get().setRequestParams(builder.toString());
			try {
				if(requestParams.containsKey("loggerLevel")) {
					currentAccount.get().setLoggerLevel(Integer.parseInt(requestParams.get("loggerLevel")[0]));
				}
			} catch (NumberFormatException e) {
			}
		}
	}
    
    public static void setTimeZone(String timeZone) {
		if (currentAccount.get() != null) {
			currentAccount.get().setTimeZone(timeZone);
		}
    }

    public enum FeatureLicense {
		MAINTENANCE (1),
		ALARMS (2),
		ENERGY (4),
		SPACE_ASSET (8),
		WEATHER_INTEGRATION (16),
		ANOMALY_DETECTOR (32),
		NEW_LAYOUT (64),
		SHIFT_HOURS (128),
		SITE_SWITCH (256),
		PEOPLE (512),
		APPROVAL (1024),
		MOBILE_DASHBOARD (2048),
		CONTROL_ACTIONS (4096),
		INVENTORY (8192),
		SCHEDULED_WO (16384),
		TENANTS (32768),
		HUDSON_YARDS (65536), // TEMP
		CONNECTEDAPPS (131072),
		M_AND_V (262144),
		GRAPHICS (524288),
		CONTRACT (1048576),
        NEW_ALARMS (2097152),
        DEVELOPER_SPACE (4194304),
		SKIP_TRIGGERS (8388608),
		RESOURCE_BOOKING (16777216),
		ANOMALY(33554432),
		READING_FIELD_UNITS_VALIDATION (67108864),
		DEVICES(134217728),
		VISITOR(268435456),
		KPI (536870912),
		SERVICE_REQUEST(1073741824),
		SAFETY_PLAN(2147483648L),
		CLIENT(4294967296L),
		WEB_TAB(8589934592L),
		BIM(17179869184L),
		PEOPLE_CONTACTS(34359738368L),
		NEW_APPROVALS(68719476736L),
		CHATBOT(137438953472L),
		SCOPING(274877906944L),
		OPERATIONAL_ALARM(549755813888L),	// 39
		FIELD_PERMISSIONS(1099511627776L)
		;
		
		private long license;

		FeatureLicense(long license) {
			this.license = license;
		}
		public long getLicense() {
			return license;
		}
		public static FeatureLicense getFeatureLicense (long value) {
			return FEATURE_MAP.get(value);
		}
		
		public static Map<Long, FeatureLicense> getAllFeatureLicense() {	
			return FEATURE_MAP;
		}
	
		private static final Map<Long, FeatureLicense> FEATURE_MAP = Collections.unmodifiableMap(initFeatureMap());
		private static Map<Long, FeatureLicense> initFeatureMap() {
			Map<Long, FeatureLicense> typeMap = new HashMap<>();
			for(FeatureLicense fLicense : values()) {
				typeMap.put(fLicense.getLicense(), fLicense);
			}
			return typeMap;
		}
	}

    
    public static long getOrgFeatureLicense(long orgId) throws Exception
    {
    	OrgBean bean = (OrgBean) BeanFactory.lookup("OrgBean", orgId);
    	long licence =bean.getFeatureLicense();
    	System.out.println("#########$$$$ Orgbean : orgid "+orgId+", license : "+licence);
    	return licence;
    	
    }

    	public static long getFeatureLicense() throws Exception {
		long orgId = getCurrentOrg().getOrgId();
		long module = 0;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement("select MODULE from FeatureLicense where ORGID = ?");
			pstmt.setLong(1, orgId);
			rs = pstmt.executeQuery();
			if(rs.next())
			{
				module = rs.getLong("MODULE");
			}

		}catch(Exception e)
		{
			throw e;
		}
		finally
		{
			DBUtil.closeAll(conn, pstmt, rs);
		}	
		return module;
	}
	
	public static boolean isFeatureEnabled(FeatureLicense featureLicense) throws Exception {
		return (getFeatureLicense() & featureLicense.getLicense()) == featureLicense.getLicense();
	}
	
	
	
	
	
	public static PortalInfoContext getPortalInfo() throws Exception {
		FacilioModule module = ModuleFactory.getServicePortalModule();
		List<FacilioField> fields = FieldFactory.getServicePortalFields();
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
												.table(module.getTableName())
												.select(fields);
//												.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		
		List<Map<String, Object>> portalInfoList = builder.get();
		
		if(portalInfoList != null && portalInfoList.size() > 0) {
			Map<String, Object> props = portalInfoList.get(0);
			PortalInfoContext protalInfo = FieldUtil.getAsBeanFromMap(props, PortalInfoContext.class);
			return protalInfo;
		}
		return null;
	}
	
	
	public static String getDefaultAppDomain() {
		if(FacilioProperties.isDevelopment()) {
			return "localhost";
		}
		return FacilioProperties.getAppDomain();
	}
	
	public static void setShouldApplySwitchScope(Boolean shouldApplySwitchScope) throws Exception {
		if(shouldApplySwitchScope == null){
			shouldApplySwitchScope = true;
		}
		getCurrentAccount().setShouldApplySwitchScope(shouldApplySwitchScope);
	}
	
	
	public static Boolean getShouldApplySwitchScope() {
		if (currentAccount.get() != null && currentAccount.get().getShouldApplySwitchScope() != null) {
			return currentAccount.get().getShouldApplySwitchScope();
		}
		return true;
	}
		
	
}
