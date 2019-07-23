package com.iam.accounts.util;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.facilio.accounts.bean.GroupBean;
import com.facilio.accounts.bean.RoleBean;
import com.facilio.accounts.dto.Account;
import com.facilio.accounts.dto.Organization;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.exception.AccountException;
import com.facilio.accounts.exception.AccountException.ErrorCode;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PortalInfoContext;
import com.facilio.bmsconsole.util.LookupSpecialTypeUtil;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.fw.BeanFactory;
import com.facilio.fw.TransactionBeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.LookupField;
import com.iam.accounts.bean.OrgBeanv2;
import com.iam.accounts.bean.UserBeanv2;

public class AuthUtill {
	private static org.apache.log4j.Logger logger = LogManager.getLogger(AuthUtill.class.getName());
	public static final String JWT_DELIMITER = "#";

	public static UserBeanv2 getUserBean() throws Exception {
		UserBeanv2 userBean = (UserBeanv2) BeanFactory.lookup("UserBeanv2");
		return userBean;
	}

	public static UserBeanv2 getUserBean(long orgId) throws Exception {
		UserBeanv2 userBean = (UserBeanv2) BeanFactory.lookup("UserBeanv2", orgId);
		return userBean;
	}

	public static UserBeanv2 getTransactionalUserBean() throws Exception {
		return (UserBeanv2) TransactionBeanFactory.lookup("UserBeanv2");
	}

	public static UserBeanv2 getTransactionalUserBean(long orgId) throws Exception {
		return (UserBeanv2) TransactionBeanFactory.lookup("UserBeanv2", orgId);
	}

	public static OrgBeanv2 getOrgBean() throws Exception {
		Object ob = BeanFactory.lookup("OrgBeanv2");
		OrgBeanv2 orgBean = (OrgBeanv2) ob;
		return orgBean;
	}

	public static OrgBeanv2 getOrgBean(long orgId) throws Exception {
		OrgBeanv2 orgBean = (OrgBeanv2) BeanFactory.lookup("OrgBeanv2", orgId);
		return orgBean;
	}

	public static OrgBeanv2 getTransactionalOrgBean(long orgId) throws Exception {
		OrgBeanv2 orgBean = (OrgBeanv2) TransactionBeanFactory.lookup("OrgBeanv2", orgId);
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
		RoleBean roleBean = (RoleBean) BeanFactory.lookup("RoleBean", orgId);
		return roleBean;
	}

	public static PortalInfoContext getPortalInfo() throws Exception {
		FacilioModule module = ModuleFactory.getServicePortalModule();
		List<FacilioField> fields = FieldFactory.getServicePortalFields();

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(module.getTableName())
				.select(fields);
//												.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module));

		List<Map<String, Object>> portalInfoList = builder.get();

		if (portalInfoList != null && portalInfoList.size() > 0) {
			Map<String, Object> props = portalInfoList.get(0);
			PortalInfoContext protalInfo = FieldUtil.getAsBeanFromMap(props, PortalInfoContext.class);
			return protalInfo;
		}
		return null;
	}

	public static Account verifiyFacilioToken(String idToken, boolean isPortalUser, boolean overrideSessionCheck, String orgDomain)
			throws AccountException {
		System.out.println("verifiyFacilioToken() :idToken :"+idToken);
		try {
			DecodedJWT decodedjwt = validateJWT(idToken, "auth0");
			if(decodedjwt != null) {
				String email = null;
				if (decodedjwt.getSubject().contains(JWT_DELIMITER)) {
					email = decodedjwt.getSubject().split(JWT_DELIMITER)[0];
				}
				else {
					email = decodedjwt.getSubject().split("_")[0];
				}
				Account account = AuthUtill.getUserBean().verifyUserSessionv2(email, idToken, orgDomain);
				if (overrideSessionCheck || account != null) {
					return account;
				} else {
					return null;
				}
			}
			return null;
		}
		catch (AccountException e) {
			throw e;
		}
		catch (Exception e) {
			logger.info("Exception occurred ", e);
			return null;
		}
	}
	
	public static String createJWT(String id, String issuer, String subject, long ttlMillis,boolean isPortalUser) {
		 
		try {
		    Algorithm algorithm = Algorithm.HMAC256("secret");
		    
		    String key = subject + JWT_DELIMITER + System.currentTimeMillis();
		    JWTCreator.Builder builder = JWT.create().withSubject(key)
	        .withIssuer(issuer);
		    builder = builder.withClaim("portaluser", isPortalUser);
		    
		    return builder.sign(algorithm);
		} catch (UnsupportedEncodingException | JWTCreationException exception){
			logger.info("exception occurred while creating JWT ", exception);
		    //UTF-8 encoding not supported
		}
		return null;
	}

	public static DecodedJWT validateJWT(String token, String issuer) {
		try {
			Algorithm algorithm = Algorithm.HMAC256("secret");
			JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build(); // Reusable verifier instance

			DecodedJWT jwt = verifier.verify(token);
			System.out.println("\ndecoded " + jwt.getSubject());
			System.out.println("\ndecoded " + jwt.getClaims());

			return jwt;
		} catch (UnsupportedEncodingException | JWTVerificationException exception) {
			logger.info("exception occurred while decoding JWT ", exception);
			// UTF-8 encoding not supported
			return null;

		}
	}

	public static String generateAuthToken(String emailaddress, String password, String domain) throws Exception {
		return validateLoginv2(emailaddress, password, null, null, null, domain, false, false);
	}

	public static String generateportalAuthToken(String emailaddress, String password, String domain) throws Exception {
		return validateLoginv2(emailaddress, password, null, null, null, domain, false, true);
	}

	public static boolean verifyPasswordv2(String emailAddress, String domain, String password) throws Exception {
		boolean passwordValid = false;
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			if (StringUtils.isEmpty(domain)) {
				domain = "app";
			}
			
			conn = FacilioConnectionPool.INSTANCE.getConnection();
			pstmt = conn.prepareStatement(
					"SELECT Account_Users.password,Account_Users.EMAIL FROM Account_Users WHERE Account_Users.EMAIL = ? and Account_Users.city = ? and USER_VERIFIED=1");
			pstmt.setString(1, emailAddress);
			pstmt.setString(2, domain);

			rs = pstmt.executeQuery();
			if (rs.next()) {
				String storedPass = rs.getString("password");
				String emailindb = rs.getString(2);
				logger.info("Stored : " + storedPass);
				logger.info("UserGiv: " + password);
				if (storedPass.equals(password)) {
					passwordValid = true;
				}
			} else {
				logger.info("No records found for  " + emailAddress);
				throw new AccountException(ErrorCode.EMAIL_ALREADY_EXISTS, "User doesn't exists");
			}

		} catch (SQLException | RuntimeException e) {
			logger.info("Exception while verifying password, ", e);
		} finally {
			DBUtil.closeAll(conn, pstmt, rs);
		}
		return passwordValid;
	}

	public static String validateLoginv2(String emailaddress, String password, String userAgent, String userType,
			String ipAddress, String domain, boolean startUserSession, boolean isPortalUser) throws Exception {

		if (verifyPasswordv2(emailaddress, domain, password)) {

			User user = AuthUtill.getUserBean().getFacilioUserv3(emailaddress, -1, domain);
			if (user != null) {
				long uid = user.getUid();
				String jwt = AuthUtill.createJWT("id", "auth0", emailaddress,
						System.currentTimeMillis() + 24 * 60 * 60000, isPortalUser);
				if (startUserSession) {
					AuthUtill.getUserBean().startUserSessionv2(uid, emailaddress, jwt, ipAddress, userAgent, userType);
				}
				return jwt;
			}
			throw new AccountException(ErrorCode.EMAIL_ALREADY_EXISTS, "User is deactivated, Please contact admin to activate.");

		}
		throw new AccountException(ErrorCode.EMAIL_ALREADY_EXISTS, "Invalid Password");
	}

	public static void appendModuleNameInKey(String moduleName, String prefix, Map<String, Object> beanMap,
			Map<String, Object> placeHolders) throws Exception {
		if (beanMap != null) {
			if (moduleName != null && !moduleName.isEmpty() && !LookupSpecialTypeUtil.isSpecialType(moduleName)) {
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				List<FacilioField> fields = modBean.getAllFields(moduleName);

				if (fields != null && !fields.isEmpty()) {
					for (FacilioField field : fields) {
						if (field.getDataTypeEnum() == FieldType.LOOKUP && prefix.split("\\.").length < 5) {
							Map<String, Object> props = (Map<String, Object>) beanMap.remove(field.getName());
							if (props != null && !props.isEmpty() && props.get("id") != null) {
								LookupField lookupField = (LookupField) field;
								// Commenting out because max level is set as 0 by default and anyway we need
								// this. And also because of the change in library of mapper
//								if(props.size() <= 3) {
								Object lookupVal = FieldUtil.getLookupVal(lookupField, (long) props.get("id"), 0);
								placeHolders.put(prefix + "." + field.getName(), lookupVal);
								props = FieldUtil.getAsProperties(lookupVal);
//								}
								String childModuleName = lookupField.getLookupModule() == null
										? lookupField.getSpecialType()
										: lookupField.getLookupModule().getName();
								appendModuleNameInKey(childModuleName, prefix + "." + field.getName(), props,
										placeHolders);
							}
						} else {
							placeHolders.put(prefix + "." + field.getName(), beanMap.remove(field.getName()));
						}
					}
				}
			}
			for (Map.Entry<String, Object> entry : beanMap.entrySet()) {
				if (entry.getValue() instanceof Map<?, ?>) {
					appendModuleNameInKey(null, prefix + "." + entry.getKey(), (Map<String, Object>) entry.getValue(),
							placeHolders);
				} else {
					placeHolders.put(prefix + "." + entry.getKey(), entry.getValue());
				}
			}
		}
	}

	public static Account getCurrentAccount(Organization org, User user) {
		Account account = new Account(org, null);
		account.setUser(user);
		return account;

	}

	public static Account getCurrentAccount(long orgId, User user) throws Exception {
		Organization org = getOrgBean().getOrgv2(orgId);
		return getCurrentAccount(org, user);
	}

	public static boolean logOut(long uId, String facilioToken, String userEmail) throws Exception {
		// end user session
		try {
			if (facilioToken != null) {
				return AuthUtill.getUserBean().endUserSessionv2(uId, userEmail, facilioToken);
			}
		} catch (Exception e) {
			logger.info("Exception occurred ", e);
		}
		return false;
	}

	public static String getResetPasswordToken(User user) throws Exception {
		return getUserBean().getEncodedTokenv2(user);
	}
}
