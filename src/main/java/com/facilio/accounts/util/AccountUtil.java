package com.facilio.accounts.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

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
			User user = AccountUtil.getOrgBean().getSuperAdmin(org.getId());
			setCurrentAccount(new Account(org, user));
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
	
	public static final int FEATURE_MAINTENANCE 			= 1;
	public static final int FEATURE_ALARMS 				= 2;
	public static final int FEATURE_ENERGY 				= 4;
	public static final int FEATURE_SPACE_ASSET 			= 8;
	public static final int FEATURE_WEATHER_INTEGRATION 	= 16;
	public static final int FEATURE_ANOMALY_DETECTOR 		= 32;
	public static final int FEATURE_WIDGET_LEGENDS 		= 64;
	public static final int FEATURE_SHIFT_HOURS	 		= 128;
	public static final int FEATURE_SITE_SWITCH = 256;
	
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
	
	public static boolean isFeatureEnabled(int featureId) throws Exception {
		return (getFeatureLicense() & featureId) == featureId;
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
