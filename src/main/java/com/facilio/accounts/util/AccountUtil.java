package com.facilio.accounts.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.facilio.accounts.bean.GroupBean;
import com.facilio.accounts.bean.OrgBean;
import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.bean.UserBean;
import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.fs.FileStore;
import com.facilio.fs.FileStoreFactory;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class AccountUtil {

	private static ThreadLocal<Account> currentAccount = new ThreadLocal<Account>();
	
	public static void setCurrentAccount(Account account) throws Exception {
		currentAccount.set(account);
		if (account.getOrg().getLogoId() > 0) {
			FileStore fs = FileStoreFactory.getInstance().getFileStore();
			account.getOrg().setLogoUrl(fs.getPrivateUrl(account.getOrg().getLogoId()));
		}
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
	
	public static UserBean getUserBean() throws Exception {
		UserBean userBean = (UserBean) BeanFactory.lookup("UserBean");
		return userBean;
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
	public static final int FEATURE_ANOMALY_DETECTOR = 32;
	
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
}
