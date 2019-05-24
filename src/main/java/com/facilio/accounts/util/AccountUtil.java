package com.facilio.accounts.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.facilio.accounts.bean.GroupBean;
import com.facilio.accounts.bean.OrgBean;
import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldUtil;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.sql.DBUtil;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.transaction.FacilioConnectionPool;

public class AccountUtil {

	private static ThreadLocal<Account> currentAccount = new ThreadLocal<Account>();
	
	public static void setCurrentAccount(Account account) throws Exception {

		currentAccount.set(account);
	}
	
	public static void setCurrentAccount(long orgId) throws Exception {
		Organization org = AccountUtil.getOrgBean().getOrg(orgId);
		
		if (org != null) {

			Account account = new Account(org, null);
			setCurrentAccount(account);
			User user = AccountUtil.getOrgBean().getSuperAdmin(org.getId());
			account.setUser(user);
		}
	}
	
	public static Account getCurrentAccount() {
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
	
	public static long getCurrentSiteId() {
		if (currentAccount.get() != null) {
			return currentAccount.get().getCurrentSiteId();
		}
		return -1;
	}
	
	public static UserBean getUserBean() throws Exception {
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
		return userBean;
	}

	public static UserBean getTransactionalUserBean() throws Exception {
		return (UserBean) TransactionBeanFactory.lookup("UserBean");
	}
	
	public static OrgBean getOrgBean() throws Exception {
		OrgBean orgBean = (OrgBean) BeanFactory.lookup("OrgBean");
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


    public enum FeatureLicense {
		MAINTENANCE (1),
		ALARMS (2),
		ENERGY (4),
		SPACE_ASSET (8),
		WEATHER_INTEGRATION (16),
		ANOMALY_DETECTOR (32),
		SHIFT_HOURS (128),
		SITE_SWITCH (256),
		APPROVAL (1024),
		MOBILE_DASHBOARD (2048),
		CONTROL_ACTIONS (4096),
		INVENTORY (8192),
		SCHEDULED_WO (16384),
		TENANTS (32768),
		NEW_FORM (65536),
		CONNECTEDAPPS (131072),
		NEW_LAYOUT (262144)
		;
		
		// Use 64, 512 for next features
		
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


	public static int getFeatureLicense() throws Exception {
		long orgId = getCurrentOrg().getOrgId();
		int module = 0;
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
				module = rs.getInt("MODULE");
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
												.select(fields)
												.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));
		
		List<Map<String, Object>> portalInfoList = builder.get();
		
		if(portalInfoList != null && portalInfoList.size() > 0) {
			Map<String, Object> props = portalInfoList.get(0);
			PortalInfoContext protalInfo = FieldUtil.getAsBeanFromMap(props, PortalInfoContext.class);
			return protalInfo;
		}
		return null;
	}
}
