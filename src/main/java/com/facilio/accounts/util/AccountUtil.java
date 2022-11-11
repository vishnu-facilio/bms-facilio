package com.facilio.accounts.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.facilio.accounts.bean.*;
import com.facilio.bmsconsole.context.ScopingConfigContext;
import com.facilio.bmsconsoleV3.context.GlobalScopeVariableEvaluationContext;
import com.facilio.bmsconsoleV3.context.scoping.GlobalScopeVariableContext;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.context.ApplicationContext;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.bmsconsole.util.ApplicationApi;
import com.facilio.constants.FacilioConstants;
import com.facilio.constants.FacilioConstants.ContextNames;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.iam.accounts.util.IAMUtil;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.service.FacilioService;
import com.facilio.service.FacilioServiceUtil;
import com.facilio.util.RequestUtil;

import lombok.extern.log4j.Log4j;
@Log4j
public class AccountUtil {

	private static ThreadLocal<Account> currentAccount = new ThreadLocal<>();
	private static ThreadLocal<AuthMethod> validationMethod = new ThreadLocal<>();
	public static final String JWT_DELIMITER = "#";

	public enum AuthMethod {
		PERMALINK,
		REMOTE_SCREEN,
		SSO,
		API,
		USER_PWD
	}

	public static void setAuthMethod(AuthMethod method) {
		validationMethod.set(method);
	}

	public static AuthMethod getAuthMethod() {
		return validationMethod.get();
	}

	public static void setCurrentAccount(Account account) throws Exception {
		currentAccount.set(account);
		setScopingMap(account);
		if(account != null && account.getOrg() !=null){
			String prevService = FacilioServiceUtil.getCurrentService() != null ? FacilioServiceUtil.getCurrentService().getServiceName() : null;
			if(prevService.equals(FacilioConstants.Services.APP_SERVICE)){
				LOGGER.info("######### Previous service in setCurrentAccount : "+prevService);
			}
			FacilioServiceUtil.setCurrentService(FacilioConstants.Services.APP_SERVICE);
			account.setPrevService(prevService);
		}
	}
	
	public static void setCurrentAccount(long orgId) throws Exception {
		Organization org = null;
		org = FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE,() ->IAMUtil.getOrgBean().getOrgv2(orgId));
		
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
				long scopingId = account.getUser().getScopingId();
				ApplicationContext app = ApplicationApi.getApplicationForId(appId);
				if(app != null) {
					account.setApp(app);
					account.setAppScopingMap(ApplicationApi.getScopingMapForApp(scopingId));
				}
			}
		}
	}
	
	public static void setCurrentSiteId(long siteId) throws Exception {
		getCurrentAccount().setCurrentSiteId(siteId);
		setScopingMap(getCurrentAccount());
		setSwitchScopingFieldValue("siteId", siteId);
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

	public static ApplicationContext getCurrentApp() {
		if (currentAccount.get() != null) {
			return currentAccount.get().getApp();
		}
		return null;
	}
	
	public static void cleanCurrentAccount() throws Exception {
		Account account = currentAccount.get();
		currentAccount.remove();
		if (account != null && account.getOrg() != null && StringUtils.isNotEmpty(account.getPrevService())) {
			if(account.getPrevService().equals(FacilioConstants.Services.APP_SERVICE)){
				LOGGER.info("######### Previous service in cleanCurrentAccount : "+account.getPrevService());
			}
			FacilioServiceUtil.setCurrentService(FacilioConstants.Services.DEFAULT_SERVICE);
		}
	}
	
	public static int getCurrentSelectQuery() {
		return getCurrentAccount() != null ? getCurrentAccount().getSelectQueries() : 0;
	}

	public static int getCurrentPublicSelectQuery() {
		return getCurrentAccount() != null ? getCurrentAccount().getPublicSelectQueries() : 0;
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

	public static Map<Long, ScopingConfigContext> getCurrentAppScopingMap() {
		if (currentAccount.get() != null) {
			return currentAccount.get().getAppScopingMap();
		}
		return null;
	}

	public static ScopingConfigContext getCurrentAppScopingMap(long modId) {
		if (currentAccount.get() != null) {
			Map<Long, ScopingConfigContext> scopingMap = currentAccount.get().getAppScopingMap();
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
	
	public static void incrementInstantJobCount(int instantJobs) {
		if(currentAccount.get() != null) {
			currentAccount.get().incrementInstantJobCount(instantJobs);
		}
	}
	
	public static void incrementInstantJobFileAddTime(long duration) {
		if(currentAccount.get() != null) {
			currentAccount.get().incrementInstantJobFileAddTime(duration);
		}
	}

	public static void setJsonConversionTime (long jsonConversionTime) {
		if(currentAccount.get() != null) {
			currentAccount.get().setJsonConversionTime(jsonConversionTime);
		}
	}

	public static SecurityBean getSecurityBean() throws Exception {
		SecurityBean securityBean = (SecurityBean) BeanFactory.lookup("SecurityBean");
		return securityBean;
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
		OrgBean orgBean = (OrgBean) BeanFactory.lookup("OrgBean", orgId);
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
			currentAccount.get().setRequestParams(RequestUtil.prettifyRequestParamMap(requestParams));
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

	private static Map<String, FeatureLicense> moduleVsLicense;
	
	public enum LicenseMapping
	{
		GROUP1LICENSE(1,FacilioConstants.LicenseKeys.LICENSE1),
		GROUP2LICENSE(2,FacilioConstants.LicenseKeys.LICENSE2);
		
		private int groupId;
		private String licenseKey;

		public String getLicenseKey() {
			return licenseKey;
		}

		public void setLicenseKey(String licenseKey) {
			this.licenseKey = licenseKey;
		}

		LicenseMapping(int groupId, String licenseKey) {
			this.groupId = groupId;
			this.licenseKey = licenseKey;
		}

//		LicenseMapping(int i) {
//			this.groupId=i;
//		}

		public int getGroupId() {
			return groupId;
		}
	}
	
    public enum FeatureLicense {
		MAINTENANCE(1,1,LicenseMapping.GROUP1LICENSE),
		ALARMS(2,2,LicenseMapping.GROUP1LICENSE),
		ENERGY(3,4,LicenseMapping.GROUP1LICENSE),
		SPACE_ASSET(4,8,LicenseMapping.GROUP1LICENSE),
		WEATHER_INTEGRATION(5,16,LicenseMapping.GROUP1LICENSE),
		ANOMALY_DETECTOR(6,32,LicenseMapping.GROUP1LICENSE),
		NEW_LAYOUT(7,64,LicenseMapping.GROUP1LICENSE),
		SHIFT_HOURS(8,128,LicenseMapping.GROUP1LICENSE),
		CLOUD_AGENT_SERVICE(9,256,LicenseMapping.GROUP1LICENSE),
		PEOPLE(10,512,LicenseMapping.GROUP1LICENSE),
		APPROVAL(11,1024,LicenseMapping.GROUP1LICENSE),
		MOBILE_DASHBOARD(12,2048,LicenseMapping.GROUP1LICENSE),
		CONTROL_ACTIONS(13,4096,LicenseMapping.GROUP1LICENSE),
		INVENTORY(14,8192, new String[]{ContextNames.TOOL, ContextNames.ITEM},LicenseMapping.GROUP1LICENSE),
		SCHEDULED_WO(15,16384,LicenseMapping.GROUP1LICENSE),
		TENANTS(16,32768, new String[]{ContextNames.TENANT, ContextNames.TENANT_UNIT_SPACE},LicenseMapping.GROUP1LICENSE),
		HUDSON_YARDS(17,65536, new String[]{ContextNames.WorkPermit.WORKPERMIT},LicenseMapping.GROUP1LICENSE), // TEMP
		CONNECTEDAPPS(18,131072,LicenseMapping.GROUP1LICENSE),
		M_AND_V(19,262144,LicenseMapping.GROUP1LICENSE),
		GRAPHICS(20,524288,LicenseMapping.GROUP1LICENSE),
		CONTRACT(21,1048576, new String[]{ContextNames.SERVICE, ContextNames.PURCHASE_CONTRACTS, ContextNames.LABOUR_CONTRACTS, ContextNames.RENTAL_LEASE_CONTRACTS, ContextNames.WARRANTY_CONTRACTS},LicenseMapping.GROUP1LICENSE),
		NEW_ALARMS(22,2097152,LicenseMapping.GROUP1LICENSE),
		DEVELOPER_SPACE(23,4194304,LicenseMapping.GROUP1LICENSE),
		SKIP_TRIGGERS(24,8388608,LicenseMapping.GROUP1LICENSE),
		RESOURCE_BOOKING(25,16777216,LicenseMapping.GROUP1LICENSE),
		ANOMALY(26,33554432,LicenseMapping.GROUP1LICENSE),
		READING_FIELD_UNITS_VALIDATION(27,67108864,LicenseMapping.GROUP1LICENSE),
		DEVICES(28,134217728,LicenseMapping.GROUP1LICENSE),
		VISITOR(29,268435456, new String[]{ContextNames.VISITOR_LOG, ContextNames.INVITE_VISITOR, ContextNames.BASE_VISIT},LicenseMapping.GROUP1LICENSE),
		KPI(30,536870912,LicenseMapping.GROUP1LICENSE),
		SERVICE_REQUEST(31,1073741824, new String[]{ContextNames.SERVICE_REQUEST},LicenseMapping.GROUP1LICENSE),
		SAFETY_PLAN(32,2147483648L,LicenseMapping.GROUP1LICENSE),
		CLIENT(33,4294967296L, new String[]{ContextNames.CLIENT},LicenseMapping.GROUP1LICENSE),
		WEB_TAB(34,8589934592L,LicenseMapping.GROUP1LICENSE),
		BIM(35,17179869184L,LicenseMapping.GROUP1LICENSE),
		PEOPLE_CONTACTS(36,34359738368L, new String[]{ContextNames.PEOPLE},LicenseMapping.GROUP1LICENSE),
		NEW_APPROVALS(37,68719476736L,LicenseMapping.GROUP1LICENSE),
		CHATBOT(38,137438953472L,LicenseMapping.GROUP1LICENSE),
		SCOPING(39,274877906944L,LicenseMapping.GROUP1LICENSE),
		OPERATIONAL_ALARM(40,549755813888L,LicenseMapping.GROUP1LICENSE),
		FIELD_PERMISSIONS(41,1099511627776L,LicenseMapping.GROUP1LICENSE),
		QUOTATION(42,2199023255552L, new String[]{ContextNames.QUOTE},LicenseMapping.GROUP1LICENSE),
		ENERGY_STAR_INTEG(43,4398046511104L,LicenseMapping.GROUP1LICENSE),
		ASSET_DEPRECIATION(44,8796093022208L,LicenseMapping.GROUP1LICENSE), // 2^43
		CUSTOM_MAIL(45,17592186044416L,LicenseMapping.GROUP1LICENSE),
		ETISALAT(46,35184372088832L,LicenseMapping.GROUP1LICENSE),
		COMMUNITY(47,70368744177664L,LicenseMapping.GROUP1LICENSE),
		TENANT_BILLING(48,140737488355328L,LicenseMapping.GROUP1LICENSE), // 2^47
		BUDGET_MONITORING(49,281474976710656L, new String[]{ContextNames.Budget.BUDGET, ContextNames.Budget.CHART_OF_ACCOUNT},LicenseMapping.GROUP1LICENSE),
		MULTIVARIATE_ANOMALY_ALARM(50,562949953421312L,LicenseMapping.GROUP1LICENSE), //2^49
		CUSTOM_BUTTON(51, 1125899906842624L, LicenseMapping.GROUP1LICENSE),
		MULTISITEPM(52, 2251799813685248L, LicenseMapping.GROUP1LICENSE), // 2 ^ 51
		FACILITY_BOOKING(53, 4503599627370496L, new String[]{ContextNames.FacilityBooking.FACILITY, ContextNames.FacilityBooking.FACILITY_BOOKING, ContextNames.FacilityBooking.AMENITY}, LicenseMapping.GROUP1LICENSE),
		INSPECTION(54, 9007199254740992L, new String[]{FacilioConstants.Inspection.INSPECTION_TEMPLATE, FacilioConstants.Inspection.INSPECTION_RESPONSE}, LicenseMapping.GROUP1LICENSE),
		INDUCTION(55, 18014398509481984L, new String[]{FacilioConstants.Induction.INDUCTION_TEMPLATE, FacilioConstants.Induction.INDUCTION_RESPONSE}, LicenseMapping.GROUP1LICENSE),// 2 ^ 54
		PURCHASE(56, 36028797018963968L, new String[]{ContextNames.PURCHASE_REQUEST, ContextNames.PURCHASE_ORDER}, LicenseMapping.GROUP1LICENSE),
		VENDOR(57, 72057594037927936L, new String[]{ContextNames.VENDORS, ContextNames.VENDOR_CONTACT, ContextNames.INSURANCE}, LicenseMapping.GROUP1LICENSE),    // 2 ^ 56
		SECURITY_POLICY(58, 144115188075855872L, LicenseMapping.GROUP1LICENSE), // 2 ^ 57
		SMS(59, 288230376151711744L, LicenseMapping.GROUP1LICENSE), // 2 ^ 58
		MULTI_LANGUAGE_TRANSLATION(60, 576460752303423488L, LicenseMapping.GROUP1LICENSE), // 2^59
		SHIFT(61, 1152921504606846976L, LicenseMapping.GROUP1LICENSE), // 2^60
		PIVOT_TABLE(62, 2305843009213693952L, LicenseMapping.GROUP1LICENSE),// 2^61
		TRANSFER_REQUEST(63, 4611686018427387904L, LicenseMapping.GROUP1LICENSE),//2^62
		// Add Module name if license is added for specific module
		SURVEY(64, 1, LicenseMapping.GROUP2LICENSE),
		GOOGLE_TRANSLATION(65, 2, LicenseMapping.GROUP2LICENSE),
		READING_LIVE_UPDATE(66, 4, LicenseMapping.GROUP2LICENSE),
		NEW_READING_RULE(67, 8, LicenseMapping.GROUP2LICENSE),
		RESOURCE_SCHEDULER(68, 16, LicenseMapping.GROUP2LICENSE),
		CUSTOM_MODULE_PERMISSION(69, 32, LicenseMapping.GROUP2LICENSE),
		CUSTOM_MODULE_SCOPING(70, 64, LicenseMapping.GROUP2LICENSE),
		POINT_FILTER(71, 128, LicenseMapping.GROUP2LICENSE),//2^7
		FETCH_SCRIPT_FROM_CACHE(72, 256, LicenseMapping.GROUP2LICENSE),
		SCRIPT_CRUD_FROM_V3(73, 512, LicenseMapping.GROUP2LICENSE),
		REQUEST_FOR_QUOTATION(74, 1024, LicenseMapping.GROUP2LICENSE),
		PM_OBSERVATION(75, 2048, LicenseMapping.GROUP2LICENSE),
		TUTEN_LABS(76, 4096, LicenseMapping.GROUP2LICENSE),
		FAILURE_CODES(77, 8192, LicenseMapping.GROUP2LICENSE),
		KIOSK_APP_FORM(78, 16384, LicenseMapping.GROUP2LICENSE),
		ML_POINTS_SUGGESTIONS(79,32768,LicenseMapping.GROUP2LICENSE),
		PLANNED_INVENTORY(80, 65536, LicenseMapping.GROUP2LICENSE),
		PM_PLANNER(81, 131072, new String[]{ContextNames.PLANNEDMAINTENANCE} , LicenseMapping.GROUP2LICENSE),
		RESOURCES(82,262144,LicenseMapping.GROUP2LICENSE), //2^18
		NEW_TAB_PERMISSIONS(83,524288,LicenseMapping.GROUP2LICENSE), //2^19
		SCOPE_VARIABLE(84,1048576,LicenseMapping.GROUP2LICENSE), //2^20
		NEW_KPI(85,2097152,LicenseMapping.GROUP2LICENSE), //2^21
		EMAIL_TRACKING(86,4194304,LicenseMapping.GROUP2LICENSE), // 2^22

		WORKPLACE_APPS(87, 8388608, LicenseMapping.GROUP2LICENSE), // 2^23
		ROUTES_AND_MULTI_RESOURCE(88,16777216,LicenseMapping.GROUP2LICENSE) , // 2^24
		ASSET_SPARE_PARTS(89,33554432,LicenseMapping.GROUP2LICENSE), // 2^25
		NEW_BOOKING(90,67108864,LicenseMapping.GROUP2LICENSE), // 2^26
		WO_STATE_TRANSITION_V3(91,134217728,LicenseMapping.GROUP2LICENSE), //2^27
		REPORT_SHARE(92,268435456,LicenseMapping.GROUP2LICENSE),//2^28
		NEW_LOOKUP_WIZARD(93,536870912,LicenseMapping.GROUP2LICENSE);//2^29
		
		public int featureId;
		private long license;
		private String[] modules;
		private LicenseMapping group;

		FeatureLicense(int featureId, long license, LicenseMapping group) {
			this.featureId = featureId;
			this.license = license;
			this.group = group;
		}
		
		FeatureLicense(int featureId,long license, String[] modules, LicenseMapping group) {
			this.featureId = featureId;
			this.license = license;
			this.modules = modules;
			this.group = group;
		}

		public long getLicense() {
			return license;
		}

		public String[] getModules() {
			return modules;
		}
		public LicenseMapping getGroup() {
			return group;
		}

		public static FeatureLicense getFeatureLicense (String licenseGroup, long value) {
			return FEATURE_MAP.get(licenseGroup).get(value);
		}
		
		public static FeatureLicense getFeatureLicense (int featureLicenseId) {
			return FEATURE_ID_MAP.get(featureLicenseId);
		}
		
		public static Map<String,Map<Long, FeatureLicense>> getAllFeatureLicense() {	
			return FEATURE_MAP;
		}
		
		public static Map<Integer, FeatureLicense> getAllFeatureLicenseIdMap() {	
			return FEATURE_ID_MAP;
		}
	
		private static final Map<Integer, FeatureLicense> FEATURE_ID_MAP = Collections.unmodifiableMap(initFeatureIdMap());
		
		private static final Map<String,Map<Long, FeatureLicense>> FEATURE_MAP = Collections.unmodifiableMap(initFeatureMap());
		private static Map<String,Map<Long, FeatureLicense>> initFeatureMap() {
			Map<String,Map<Long, FeatureLicense>> typeMap = new HashMap<>();
			
			Map<String, FeatureLicense> moduleMap = new HashMap<>();
			
			for(FeatureLicense fLicense : values()) {
				
				Map<Long, FeatureLicense> groupedLicenseMap = typeMap.getOrDefault(fLicense.getGroup().getLicenseKey(), new HashMap<Long, AccountUtil.FeatureLicense>());
				
				groupedLicenseMap.put(fLicense.getLicense(), fLicense);
				
				typeMap.put(fLicense.getGroup().getLicenseKey(), groupedLicenseMap);
				
				if (fLicense.getModules() != null) {
					for (String module : fLicense.getModules()){
						moduleMap.put(module, fLicense);
					}
				}
			}
			
			moduleVsLicense = Collections.unmodifiableMap(moduleMap);
			return typeMap;
		}

		private static Map<Integer, FeatureLicense> initFeatureIdMap() {
			// TODO Auto-generated method stub

			Map<Integer, FeatureLicense> featureIdMap = new HashMap<Integer, AccountUtil.FeatureLicense>();
			for (FeatureLicense fLicense : values()) {
				featureIdMap.put(fLicense.getFeatureId(), fLicense);
			}
			return featureIdMap;
		}

		public boolean isEnabled(Map<LicenseMapping, Long> totalLicense) {
			return (totalLicense.get(this.group) & this.license) == this.license;
		}

		public int getFeatureId() {
			return featureId;
		}
	}
    /*private static Map<String, FeatureLicense> moduleVsLicense = Collections.unmodifiableMap(initializeLicenseRelation());
    private static  Map<String, FeatureLicense> initializeLicenseRelation() {
	    	Map<String, FeatureLicense> licenseMap = new HashMap();
	    	licenseMap.put(ContextNames.TENANT, FeatureLicense.TENANTS);
	    	licenseMap.put(ContextNames.TENANT_UNIT_SPACE, FeatureLicense.TENANTS);
	    	licenseMap.put(ContextNames.PEOPLE, FeatureLicense.PEOPLE_CONTACTS);
	    	licenseMap.put(ContextNames.CLIENT, FeatureLicense.CLIENT);
	    	return licenseMap;
    }*/

    
    public static Map<String,Long> getOrgFeatureLicense(long orgId) throws Exception
    {
    	OrgBean bean = (OrgBean) BeanFactory.lookup("OrgBean", orgId);
    	Map<String,Long> licence =bean.getFeatureLicense();
    	System.out.println("#########$$$$ Orgbean : orgid "+orgId+", license : "+licence);
    	return licence;
    	
    }

    public static Map<String,Long> getFeatureLicense() throws Exception {
    	return getOrgBean().getFeatureLicense();
	}
	
	public static boolean isFeatureEnabled(FeatureLicense featureLicense) throws Exception {
		return getOrgBean().isFeatureEnabled(featureLicense);
	}
	
	public static boolean isModuleLicenseEnabled(String moduleName) throws Exception {
		FeatureLicense license = moduleVsLicense.get(moduleName);
		return license == null || isFeatureEnabled(license);
	}

	public static TreeMap<String, Boolean> getFeatureLicenseMap(long orgId) throws Exception {
		Map<Integer, FeatureLicense> featureLicenses = FeatureLicense.getAllFeatureLicenseIdMap();
		TreeMap<String,Boolean> features = new TreeMap<>();

		for(Integer featureId: featureLicenses.keySet()) {

			FeatureLicense license = featureLicenses.get(featureId);
			// boolean isEnabled = (FeatureLicense.TENANTS == license) || (FeatureLicense.VENDOR == license) || ((getOrgFeatureLicense(orgId).get(license.getGroup().getLicenseKey()) & license.getLicense()) == license.getLicense());
			boolean isEnabled = (getOrgFeatureLicense(orgId).get(license.getGroup().getLicenseKey()) & license.getLicense()) == license.getLicense();
			features.put(license.toString(), isEnabled);
		}
		return features;
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
		return FacilioProperties.getMainAppDomain();
	}

	public static void setValueGenerator(Map<String, String> valueGenerators) {
		getCurrentAccount().setValueGenerators(valueGenerators);
	}

	public static Map<String, String> getValueGenerator() {
		return getCurrentAccount().getValueGenerators();
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

	public static Map<String, GlobalScopeVariableEvaluationContext> getGlobalScopeVariableValues() {
		Account account = getCurrentAccount();
		if(account != null) {
			return account.getGlobalScopeVariableValues();
		}
		return null;
	}

	public static void setGlobalScopeVariableValues(Map<String, GlobalScopeVariableEvaluationContext> globalScopeVariableValues) {
		if(getCurrentAccount() != null) {
			getCurrentAccount().setGlobalScopeVariableValues(globalScopeVariableValues);
		}
	}

	public static Boolean applyDBScoping(FacilioModule module) {
		try {
			if (AccountUtil.isFeatureEnabled(FeatureLicense.SCOPING)) {
				if(AccountUtil.isFeatureEnabled(FeatureLicense.SCOPE_VARIABLE)){
					return true;
				}
				if(AccountUtil.getCurrentApp() != null && !AccountUtil.getCurrentApp().getLinkName().equals(FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP)){
					return true;
				}
				if(module.getName().equals(FacilioConstants.Email.EMAIL_FROM_ADDRESS_MODULE_NAME)
						|| module.getName().equals(FacilioConstants.Inspection.INSPECTION_TEMPLATE)
						|| module.getName().equals(FacilioConstants.Induction.INDUCTION_TEMPLATE)
						|| module.getName().equals(FacilioConstants.Survey.SURVEY_TEMPLATE)) {
					return true;
				}
				if(AccountUtil.isFeatureEnabled(FeatureLicense.CUSTOM_MODULE_SCOPING) && module.isCustom()) {
					return true;
				}
				if (
						AccountUtil.getCurrentOrg() != null &&
								(
										(AccountUtil.getCurrentOrg().getOrgId() == 398l && module.getName().equals("custom_client"))
										|| (AccountUtil.getCurrentOrg().getOrgId() == 10l && (module.getName().equals("storeRoom") || module.getName().equals("item") || module.getName().equals("tool")))
										|| (AccountUtil.getCurrentOrg().getOrgId() == 429l && (module.getName().equals("floor")))
										|| (AccountUtil.getCurrentOrg().getOrgId() == 583l && (module.getName().equals("storeRoom") || module.getName().equals("item") || module.getName().equals("tool")))
										|| (AccountUtil.getCurrentOrg().getOrgId() == 28l && module.getName().equals("vendors"))
										|| (AccountUtil.getCurrentOrg().getOrgId() == 183l && (module.getName().equals("storeRoom") || module.getName().equals("item") || module.getName().equals("tool")))
										|| (AccountUtil.getCurrentOrg().getOrgId() == 274l && (module.getName().equals(FacilioConstants.ContextNames.PURCHASE_ORDER) || module.getName().equals(FacilioConstants.ContextNames.PURCHASE_REQUEST) || module.getName().equals(ContextNames.RECEIVABLE)))
										|| (AccountUtil.getCurrentOrg().getOrgId() == 418l && (module.getName().equals(ContextNames.VENDORS) || module.getName().equals(ContextNames.VISITOR_LOG) || module.getName().equals(ContextNames.INVITE_VISITOR) || module.getName().equals(ContextNames.BASE_VISIT) || module.getName().equals("custom_vendormapping") || module.getName().equals("custom_utilityaccounts") || module.getName().equals("custom_utilityconnections") || module.getName().equals("custom_utilitybill") || module.getName().equals("custom_utilitybilllineitems") || module.getName().equals("custom_incidentmanagement_1") || module.getName().equals("custom_quoteselectionform") || module.getName().equals("custom_supplierquotation") || module.getName().equals("custom_supplierquote") || module.getName().equals(ContextNames.VENDOR_CONTACT)))
										|| (AccountUtil.getCurrentOrg().getOrgId() == 176l && (module.getName().equals("storeRoom") || module.getName().equals("item") || module.getName().equals("tool") || module.getName().equals("toolTypes") || module.getName().equals("itemTypes") || module.getName().equals("inventoryrequest")))
								)
				) {
					return true;
				}
			}
		}
		catch (Exception e){
			e.printStackTrace();
		}
		return false;
	}
	
}
