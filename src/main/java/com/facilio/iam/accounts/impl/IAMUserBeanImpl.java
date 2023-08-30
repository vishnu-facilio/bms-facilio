package com.facilio.iam.accounts.impl;

import java.io.*;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.xml.bind.DatatypeConverter;

import com.facilio.accounts.dto.*;
import com.facilio.accounts.dto.User;
import com.facilio.accounts.sso.AccountSSO;
import com.facilio.accounts.sso.DomainSSO;
import com.facilio.accounts.sso.SSOUtil;
import com.facilio.auth.actions.PasswordHashUtil;
import com.facilio.auth.beans.SecretsManagerBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.bmsconsole.context.APIClient;
import com.facilio.db.criteria.operators.*;
import com.facilio.db.util.DBConf;
import com.facilio.fw.BeanFactory;
import com.facilio.iam.accounts.context.SecurityPolicy;
import com.facilio.iam.accounts.exceptions.SecurityPolicyException;
import com.facilio.iam.accounts.util.*;
import com.facilio.modules.*;
import com.facilio.service.FacilioService;
import com.facilio.util.FacilioUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.admin.directory.Directory;
import com.google.api.services.admin.directory.DirectoryScopes;
import com.google.api.services.admin.directory.model.MembersHasMember;
import dev.samstevens.totp.recovery.RecoveryCodeGenerator;
import lombok.SneakyThrows;
import lombok.var;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.facilio.accounts.dto.AppDomain.AppDomainType;
import com.facilio.accounts.dto.AppDomain.GroupType;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bmsconsole.util.EncryptionUtil;
import com.facilio.db.builder.GenericDeleteRecordBuilder;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.fw.cache.LRUCache;
import com.facilio.iam.accounts.bean.IAMUserBean;
import com.facilio.iam.accounts.exceptions.AccountException;
import com.facilio.iam.accounts.exceptions.AccountException.ErrorCode;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;
import sh.ory.hydra.model.OAuth2Client;

import static com.facilio.constants.FacilioConstants.INVITATION_EXPIRY_DAYS;


public class IAMUserBeanImpl implements IAMUserBean {

	private static final String USER_TOKEN_REGEX = "#";
	private static final Logger USER_LOGIN = LogManager.getLogger("UserLogin");
	private static final Logger LOGGER = LogManager.getLogger(IAMUserBeanImpl.class.getName());
	public static final String JWT_DELIMITER = "#";
	public static int YEAR_IN_SECONDS = 365 * 24 * 3600;
	public static int MINUTE_IN_SECONDS = 60;

	public boolean updateUserv2(IAMUser user, List<FacilioField> fields) throws Exception {
		return updateUserEntryv2(user, fields);
	}

	private boolean updateUserEntryv2(IAMUser user, List<FacilioField> fields) throws Exception {
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getAccountsUserModule().getTableName())
				.fields(fields);

		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(user.getUid()), NumberOperators.EQUALS));

		validateUsername(user, fields);

		Map<String, Object> props = FieldUtil.getAsProperties(user);
		int updatedRows = updateBuilder.update(props);

		if (fieldMap.containsKey("securityPolicyId")) {
			IAMUtil.dropUserSecurityPolicyCache(Collections.singletonList(user.getUid()));
		}

		return (updatedRows > 0);
	}


	@Override
	public long signUpSuperAdminUserv3(long orgId, IAMUser user, String identifier) throws Exception {
		AppDomain appDomain = getAppDomain(AccountUtil.getDefaultAppDomain());
		return addUserv3(orgId, user, appDomain.getIdentifier());
	}

	@Override
	public long addUserv3(long orgId, IAMUser user, String identifier) throws Exception {
		return addUserV3(orgId, user, false, identifier);
	}

	public IAMUser getUserFromToken(String userToken) throws Exception {
		String[] tokenPortal = userToken.split("&");
		String token = EncryptionUtil.decode(tokenPortal[0]);
		String[] userObj = token.split(USER_TOKEN_REGEX);
		IAMUser user = null;
		if (userObj.length == 4) {
			String creationTimeStr = userObj[3];
			long creationTime = Long.parseLong(creationTimeStr);
			Instant creationInstant = Instant.ofEpochMilli(creationTime);
			Instant expiryDay = creationInstant.plus(INVITATION_EXPIRY_DAYS, ChronoUnit.DAYS);
			user = getFacilioUser(Long.parseLong(userObj[0]), Long.parseLong(userObj[1]), true);
			if (((long) Long.parseLong(userObj[0])) == 17) {
				expiryDay = creationInstant.plus(45, ChronoUnit.DAYS); // temp code for investa
			} else if (((long) Long.parseLong(userObj[0])) == 418) {
				expiryDay = creationInstant.plus(45, ChronoUnit.DAYS); // temp code for altayer
			}
			Instant currentTime = Instant.ofEpochMilli(System.currentTimeMillis());
			if (currentTime.isAfter(expiryDay)) {
				LOGGER.error("user token expired " + userToken);
				return null;
			}
		}
		return user;
	}

	public static void main(String[] args) {
		IAMUserBeanImpl us = new IAMUserBeanImpl();
		IAMUser s;
		try {
			s = us.getUserFromToken("xSb_ezHQ_udcFU8l5P67wq_z809tlkMMIZxMbHAV0hbs9TKfyRniDoVCfmvVGF3wl4nuHLJ53Ho=");
			System.out.println(s.getEmail());
			System.out.println(s.getUid());
//			System.out.println(s.getOuid());
//			System.out.println(s.getPortalId());
			System.out.println(s.getOrgId());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}


	@Override
	public IAMUser verifyEmailv2(String token) throws Exception {
		IAMUser user = getUserFromToken(token);

		if (user != null) {
//			if((System.currentTimeMillis() - user.getInvitedTime()) < INVITE_LINK_EXPIRE_TIME) {
			try {
				user.setUserVerified(true);
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(IAMAccountConstants.getAccountsUserFields());
				List<FacilioField> fields = new ArrayList<FacilioField>();
				fields.add(fieldMap.get("userVerified"));

				updateUserv2(user, fields);
			} catch (Exception e) {
				LOGGER.info("Exception occurred ", e);
			}
			return user;
//			}
		}
		return null;
	}

	@Override
	public void validatePasswordWithSecurityPolicy(long uid, String password) throws Exception {
		SecurityPolicy userSecurityPolicy = getUserSecurityPolicy(uid);
		if (userSecurityPolicy == null) {
			return;
		}
		validatePassword(uid, userSecurityPolicy, password);
	}

	public String cryptWithMD5(String pass) {
		return PasswordHashUtil.cryptWithMD5(pass);
	}

	private boolean isOneOfPreviousPasswords(long uid, int allowedPrevPassword, String password) throws Exception {
		List<String> userPrevPassword = getUserPrevPassword(uid, allowedPrevPassword);
		if (CollectionUtils.isEmpty(userPrevPassword)) {
			return false;
		}

		String hashedPassord = PasswordHashUtil.cryptWithMD5(password);

		for (String pass : userPrevPassword) {
			if (hashedPassord.equals(pass)) {
				return true;
			}
		}

		return false;
	}


	private void validatePassword(long uid, SecurityPolicy userSecurityPolicy, String password) throws Exception {
		Boolean isPwdPolicyEnabled = userSecurityPolicy.getIsPwdPolicyEnabled();
		if (isPwdPolicyEnabled == null || !isPwdPolicyEnabled) {
			return;
		}

		Integer pwdMinLength = userSecurityPolicy.getPwdMinLength();
		if (pwdMinLength != null) {
			if (password.length() < pwdMinLength) {
				throw new SecurityPolicyException(SecurityPolicyException.ErrorCode.MIN_LENGTH_VIOLATION, "Password should be atleast " + pwdMinLength + " characters in length.");
			}
		}

		Integer pwdMinNumDigits = userSecurityPolicy.getPwdMinNumDigits();
		Integer pwdMinSplChars = userSecurityPolicy.getPwdMinSplChars();
		int numChars = 0;
		int splChars = 0;
		int upperCase = 0;
		int lowerCase = 0;
		for (int i = 0; i < password.length(); i++) {
			if (StringUtils.isNumeric(password.charAt(i) + "")) {
				numChars++;
			}

			if (!StringUtils.isAlphanumeric(password.charAt(i) + "")) {
				splChars++;
			}

			if (StringUtils.isAllUpperCase(password.charAt(i) + "")) {
				upperCase++;
			}
			if (StringUtils.isAllLowerCase(password.charAt(i) + "")) {
				lowerCase++;
			}
		}

		if (pwdMinNumDigits != null && (numChars < pwdMinNumDigits)) {
			throw new SecurityPolicyException(SecurityPolicyException.ErrorCode.MIN_NUM_DIGIT_VIOLATION, "Password should have atleast " + pwdMinNumDigits + " numeric digits.");
		}

		if (pwdMinSplChars != null && (splChars < pwdMinSplChars)) {
			throw new SecurityPolicyException(SecurityPolicyException.ErrorCode.MIN_SPL_CHARS_VIOLATION, "Password should have atleast " + pwdMinSplChars + " special characters.");
		}

		if (userSecurityPolicy.getPwdIsMixed() != null && userSecurityPolicy.getPwdIsMixed() && (upperCase < 1 || lowerCase < 1)) {
			throw new SecurityPolicyException(SecurityPolicyException.ErrorCode.MIN_UPPER_CASE_VIOLATION, "Password should have atleast 1 upper case character and 1 lower case character.");
		}

		if (userSecurityPolicy.getPwdPrevPassRefusal() != null && isOneOfPreviousPasswords(uid, userSecurityPolicy.getPwdPrevPassRefusal(), password)) {
			throw new SecurityPolicyException(SecurityPolicyException.ErrorCode.PREV_PWD_VIOLATION, "Should not be one of previous passwords.");
		}

	}

	@Override
	public void savePreviousPassword(long uid, String encryptedPassword) throws Exception {
		Map<String, Object> prop = new HashMap<>();
		prop.put("userId", uid);
		prop.put("password", encryptedPassword);
		prop.put("changedTime", System.currentTimeMillis());

		List<FacilioField> userPrevPwdsFields = IAMAccountConstants.getUserPrevPwdsFields();
		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder();
		insertRecordBuilder.fields(userPrevPwdsFields)
				.table("UserPrevPwds")
				.addRecord(prop)
				.save();
	}

	@Override
	public IAMUser resetExpiredPassword(String digest, String password, String confirmPassword) throws Exception {
		long uid = getUIDFromPWDResetToken(digest);
		IAMUser user = getFacilioUser(-1, uid, true);

		return resetUserPassword(password, user, confirmPassword);
	}

	@Override
	public IAMUser resetPasswordv2(String token, String password, String confirmPassword) throws Exception {
		IAMUser user = getUserFromToken(token);

		return resetUserPassword(password, user, confirmPassword);
	}

	private IAMUser resetUserPassword(String password, IAMUser user, String confirmPassword) throws Exception {
		if (user != null) {
			if (confirmPassword != null) {
				if (!password.equals(confirmPassword)) {
					throw new IllegalArgumentException("Passwords doest not match");
				}
			}
			validatePasswordWithSecurityPolicy(user.getUid(), password);
			password = PasswordHashUtil.cryptWithMD5(password);
			savePreviousPassword(user.getUid(), password);
//			if ((System.currentTimeMillis() - user.getInvitedTime()) < INVITE_LINK_EXPIRE_TIME) {
			try {
				user.setPassword(password);
				user.setUserVerified(true);
				user.setPwdLastUpdatedTime(System.currentTimeMillis());
				Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(IAMAccountConstants.getAccountsUserFields());
				List<FacilioField> fields = new ArrayList<FacilioField>();
				fields.add(fieldMap.get("userVerified"));
				fields.add(IAMAccountConstants.getUserPasswordField());
				fields.add(fieldMap.get("pwdLastUpdatedTime"));

				IAMUtil.getTransactionalUserBean().updateUserv2(user, fields);
				IAMUtil.getTransactionalUserBean().endUserSessionv2(user.getUid());
			} catch (Exception e) {
				LOGGER.info("Exception occurred ", e);
			}
			return user;
//			}
		}
		return null;
	}

	@Override
	public IAMUser validateUserInvitev2(String token) throws Exception {
		IAMUser user = getUserFromToken(token);
		return user;
	}

	@Override
	public IAMUser acceptInvitev2(String token, String password) throws Exception {
		IAMUser user = getUserFromToken(token);
		if (user != null) {
			if (!user.isUserVerified()) {
				String hashedPassword = PasswordHashUtil.cryptWithMD5(password);
				user.setPassword(hashedPassword);
				IAMUserUtil.validatePasswordWithSecurityPolicy(user.getUid(), password);
				if (IAMUtil.getTransactionalUserBean().acceptUserv2(user)) {
					return user;
				}
				return null;
			}
			return user;
		}
		return null;
	}

	public boolean acceptUserv2(IAMUser user) throws Exception {
		if (user != null && user.isActive()) {
			FacilioField isDefaultOrg = new FacilioField();
			isDefaultOrg.setName("isDefaultOrg");
			isDefaultOrg.setDataType(FieldType.BOOLEAN);
			isDefaultOrg.setColumnName("ISDEFAULT");
			isDefaultOrg.setModule(IAMAccountConstants.getAccountOrgUserModule());

			FacilioField userStatus = new FacilioField();
			userStatus.setName("userStatus");
			userStatus.setDataType(FieldType.BOOLEAN);
			userStatus.setColumnName("USER_STATUS");
			userStatus.setModule(IAMAccountConstants.getAccountOrgUserModule());

			List<FacilioField> fields = new ArrayList<>();
			fields.add(userStatus);

			GenericUpdateRecordBuilder updateBuilder = getUpdateBuilder(fields);

			updateBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.USERID", "userId", String.valueOf(user.getUid()), NumberOperators.EQUALS));
			updateBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ORGID", "orgId", String.valueOf(user.getOrgId()), NumberOperators.EQUALS));

			Map<String, Object> props = new HashMap<>();
			//props.put("isDefaultOrg", true);
			props.put("userStatus", true);


			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				String password = user.getPassword();
				if (user != null) {
					user.setUserVerified(true);
					user.setPassword(password);
					user.setPwdLastUpdatedTime(System.currentTimeMillis());
					Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(IAMAccountConstants.getAccountsUserFields());
					List<FacilioField> fieldsToBeUpdated = new ArrayList<FacilioField>();
					fieldsToBeUpdated.add(fieldMap.get("userVerified"));
					fieldsToBeUpdated.add(IAMAccountConstants.getUserPasswordField());
					fieldsToBeUpdated.add(fieldMap.get("pwdLastUpdatedTime"));
					updateUserv2(user, fieldsToBeUpdated);
					IAMUtil.getUserBean().savePreviousPassword(user.getUid(), user.getPassword());
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public boolean enableUser(long orgId, long userId) throws Exception {

		FacilioField userStatus = new FacilioField();
		userStatus.setName("userStatus");
		userStatus.setDataType(FieldType.BOOLEAN);
		userStatus.setColumnName("USER_STATUS");
		userStatus.setModule(IAMAccountConstants.getAccountOrgUserModule());

		List<FacilioField> fields = new ArrayList<>();
		fields.add(userStatus);

		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getAccountOrgUserModule().getTableName())
				.fields(fields);

		updateBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(userId), NumberOperators.EQUALS));

		Map<String, Object> props = new HashMap<>();
		props.put("userStatus", true);

		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}

	@Override
	public boolean disableUser(long orgId, long uId) throws Exception {

		FacilioField userStatus = new FacilioField();
		userStatus.setName("userStatus");
		userStatus.setDataType(FieldType.BOOLEAN);
		userStatus.setColumnName("USER_STATUS");
		userStatus.setModule(IAMAccountConstants.getAccountOrgUserModule());

		IAMUser user = getFacilioUser(orgId, uId, true);
		if (user.isDefaultOrg()) {
			updateDefaultOrgForUser(user.getUid(), user.getOrgId());
		}

		List<FacilioField> fields = new ArrayList<>();
		fields.add(userStatus);

		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getAccountOrgUserModule().getTableName())
				.fields(fields);

		updateBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uId), NumberOperators.EQUALS));


		Map<String, Object> props = new HashMap<>();
		props.put("userStatus", false);

		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return true;
		}
		return false;
	}


	@Override
	public boolean deleteUserv2(long userId, long orgId) throws Exception {
		IAMUser user = getFacilioUser(orgId, userId, false);
		if (user != null) {
			FacilioField deletedTime = new FacilioField();
			deletedTime.setName("deletedTime");
			deletedTime.setDataType(FieldType.NUMBER);
			deletedTime.setColumnName("DELETED_TIME");
			deletedTime.setModule(IAMAccountConstants.getAccountOrgUserModule());

			if (user.isDefaultOrg()) {
				updateDefaultOrgForUser(user.getUid(), user.getOrgId());
			}

			List<FacilioField> fields = new ArrayList<>();
			fields.add(deletedTime);

			GenericUpdateRecordBuilder updateBuilder = getUpdateBuilder(fields);

			updateBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USERID", "userId", String.valueOf(user.getUid()), NumberOperators.EQUALS));
			updateBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(user.getOrgId()), NumberOperators.EQUALS));
			updateBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));

			Map<String, Object> props = new HashMap<>();
			props.put("deletedTime", System.currentTimeMillis());

			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				return true;
			}
			return false;
		} else {
			throw new AccountException(ErrorCode.USER_ALREADY_DELETED, "IAMUser is already deleted");
		}
	}

	private void updateDefaultOrgForUser(long uId, long currentOrg) throws Exception {
		List<Organization> orgs = getOrgsv2(uId);
		if (CollectionUtils.isNotEmpty(orgs)) {
			for (Organization org : orgs) {
				if (org.getOrgId() != currentOrg) {
					setDefaultOrgv2(uId, org.getOrgId());
					break;
				}
			}
		}
	}


	private GenericUpdateRecordBuilder getUpdateBuilder(List<FacilioField> fields) {

		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getAccountOrgUserModule().getTableName())
				.innerJoin("Account_Users")
				.on("Account_Users.USERID = Account_ORG_Users.USERID")
				.fields(fields);

		return updateBuilder;
	}


	@Override
	public boolean setDefaultOrgv2(long uid, long orgId) throws Exception {

		FacilioField isDefaultOrg = new FacilioField();
		isDefaultOrg.setName("isDefaultOrg");
		isDefaultOrg.setDataType(FieldType.BOOLEAN);
		isDefaultOrg.setColumnName("ISDEFAULT");
		isDefaultOrg.setModule(IAMAccountConstants.getAccountOrgUserModule());

		List<FacilioField> fields = new ArrayList<>();
		fields.add(isDefaultOrg);

		GenericUpdateRecordBuilder updateBuilder = getUpdateBuilder(fields);

		updateBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));


		Map<String, Object> props = new HashMap<>();
		props.put("isDefaultOrg", false);

		updateBuilder.update(props);

		GenericUpdateRecordBuilder updateBuilder1 = getUpdateBuilder(fields);
		updateBuilder1.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		updateBuilder1.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		updateBuilder1.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));

		Map<String, Object> props1 = new HashMap<>();
		props1.put("isDefaultOrg", true);

		updateBuilder1.update(props1);
		return true;
	}

	private boolean hasGoogleLogin(long uid) throws Exception {
		List<Map<String, Object>> props = getUserSocialLogins(uid);
		if (CollectionUtils.isEmpty(props)) {
			return false;
		}
		Boolean isgoogle = (Boolean) props.get(0).get("isGoogle");
		return isgoogle != null && isgoogle;
	}

	private void upsertAccountSocialLogin(long uid, IAMAccountConstants.SocialLogin socialLogin) throws Exception {
		List<Map<String, Object>> props = getUserSocialLogins(uid);

		if (CollectionUtils.isEmpty(props)) {
			insertIntoAccountSocialLogin(uid, socialLogin);
			return;
		}

		boolean hasSocialLogin = false;
		if (socialLogin == IAMAccountConstants.SocialLogin.GOOGLE) {
			Boolean isgoogle = (Boolean) props.get(0).get("isGoogle");
			if (isgoogle != null && isgoogle) {
				hasSocialLogin = true;
			}
		}

		if (!hasSocialLogin) {
			updateAccountSocialLogin(uid, socialLogin);
		}
	}

	private List<Map<String, Object>> getUserSocialLogins(long uid) throws Exception {
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder();
		selectRecordBuilder.select(IAMAccountConstants.getAccountSocialLoginFields())
				.table("Account_Social_Login");

		selectRecordBuilder.andCondition(CriteriaAPI.getCondition("Account_Social_Login.USERID", "uid", String.valueOf(uid), NumberOperators.EQUALS));
		return selectRecordBuilder.get();
	}

	private void updateAccountSocialLogin(long uid, IAMAccountConstants.SocialLogin socialLogin) throws SQLException {
		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder();
		updateRecordBuilder.fields(IAMAccountConstants.getAccountSocialLoginFields())
				.table("Account_Social_Login")
				.andCondition(CriteriaAPI.getCondition("Account_Social_Login.USERID", "uid", String.valueOf(uid), NumberOperators.EQUALS));
		Map<String, Object> props = new HashMap<>();
		if (socialLogin == IAMAccountConstants.SocialLogin.GOOGLE) {
			props.put("isGoogle", true);
		}
		updateRecordBuilder.update(props);
	}

	private void insertIntoAccountSocialLogin(long uid, IAMAccountConstants.SocialLogin socialLogin) throws Exception {
		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder();
		insertRecordBuilder.fields(IAMAccountConstants.getAccountSocialLoginFields())
				.table("Account_Social_Login");
		Map<String, Object> props = new HashMap<>();
		props.put("uid", uid);
		if (socialLogin == IAMAccountConstants.SocialLogin.GOOGLE) {
			props.put("isGoogle", true);
		}

		insertRecordBuilder.insert(props);
	}

	@Override
	public List<Organization> getOrgsv2(long uid) throws Exception {
		return getOrgsv2(uid, Organization.OrgType.PRODUCTION);
	}

	@Override
	public List<Organization> getOrgsv2(long uid, Organization.OrgType orgType) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getOrgFields())
				.table("Organizations")
				.innerJoin("Account_ORG_Users")
				.on("Organizations.ORGID = Account_ORG_Users.ORGID");

		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USER_STATUS", "userStatus", "1", NumberOperators.EQUALS));
		selectBuilder.andCriteria(getOrgTypeCriteria(orgType));

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			List<Organization> orgs = new ArrayList<>();
			for (Map<String, Object> prop : props) {
				orgs.add(IAMOrgUtil.createOrgFromProps(prop));
			}
			return orgs;
		}
		return null;
	}

	@Override
	public String updateUserPhoto(long uid, User user) throws Exception {

		FileStore fs = FacilioFactory.getFileStore();
		long fileId = fs.addFile(user.getAvatarFileName(), user.getAvatar(), user.getAvatarContentType());
		String url = fs.newPreviewFileUrl("user", fileId);
		FacilioField photoId = new FacilioField();
		photoId.setName("photoId");
		photoId.setDataType(FieldType.NUMBER);
		photoId.setColumnName("PHOTO_ID");
		photoId.setModule(IAMAccountConstants.getAccountsUserModule());

		List<FacilioField> fields = new ArrayList<FacilioField>();
		fields.add(photoId);


		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getAccountsUserModule().getTableName())
				.fields(fields);

		updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));

		Map<String, Object> props = new HashMap<>();
		props.put("photoId", fileId);

		int updatedRows = updateBuilder.update(props);
		if (updatedRows > 0) {
			return url;
		}
		return null;
	}

	@Override
	public boolean deleteUserPhoto(long uid, long photoId) throws Exception {

		if (photoId > 0) {
			FileStore fs = FacilioFactory.getFileStore();
			boolean isDeleted = fs.deleteFile(photoId);
			if (isDeleted) {
				FacilioField photoField = new FacilioField();
				photoField.setName("photoId");
				photoField.setDataType(FieldType.NUMBER);
				photoField.setColumnName("PHOTO_ID");
				photoField.setModule(IAMAccountConstants.getAccountsUserModule());

				List<FacilioField> fields = new ArrayList<FacilioField>();
				fields.add(photoField);

				GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
						.table(IAMAccountConstants.getAccountsUserModule().getTableName())
						.fields(fields);

				updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));

				Map<String, Object> props = new HashMap<>();
				props.put("photoId", -99);

				int updatedRows = updateBuilder.update(props);
				if (updatedRows > 0) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public Map<String, Object> getLoginModes(String userName, AppDomainType appDomainType, AppDomain.GroupType groupType) throws Exception {
		if (StringUtils.isEmpty(userName)) {
			throw new IllegalArgumentException("user name is missing");
		}

		final List<Map<String, Object>> userForUsername = getUserData(userName, groupType);
		final long userPresent = 1;
		final long userNotPresent = 2;
		final Map<String, Object> result = new HashMap<>();

		if (CollectionUtils.isEmpty(userForUsername)) {
			result.put("code", userNotPresent);
			result.put("message", "user not present");
			return result;
		}

		result.put("code", userPresent);
		result.put("message", "user present");

		List<Long> orgIds = new ArrayList<>();
		userForUsername.forEach(i -> orgIds.add((long) i.get("orgId")));

		boolean hasPassword = hasPassword(userName);
		if (orgIds.size() == 1) {
			Organization org = IAMOrgUtil.getOrg(orgIds.get(0));
			result.put("logo_url", org.getLogoUrl());
		}

		List<String> loginModes = new ArrayList<>();
		if (hasPassword) {
			loginModes.add("password");
		}

		if (hasGoogleLogin((Long) userForUsername.get(0).get("uid"))) {
			loginModes.add("google");
		}

		List<AccountSSO> accountSSODetails = IAMOrgUtil.getAccountSSO(orgIds);
		DomainSSO domainSSO = IAMOrgUtil.getDomainSSODetails(orgIds.get(0), appDomainType, groupType, AppDomain.DomainType.DEFAULT);
		if (groupType == AppDomain.GroupType.TENANT_OCCUPANT_PORTAL) {
			if (domainSSO != null && domainSSO.getIsActive() != null && domainSSO.getIsActive()) {
				loginModes.add("SAML");
				AppDomain appDomain = IAMAppUtil.getAppDomain(domainSSO.getAppDomainId());
				String domainLoginURL = SSOUtil.getDomainSSOEndpoint(appDomain.getDomain());
				result.put("SSOURL", domainLoginURL);
			}
		} else if (groupType == AppDomain.GroupType.FACILIO) {
			if (CollectionUtils.isNotEmpty(accountSSODetails)) {
				AccountSSO accountSSO = accountSSODetails.get(0);
				if (accountSSO != null && accountSSO.getIsActive() != null && accountSSO.getIsActive()) {
					loginModes.add("SAML");
					long orgId = accountSSO.getOrgId();
					String domainLoginURL = SSOUtil.getSSOEndpoint(IAMOrgUtil.getOrg(orgId).getDomain());
					result.put("SSOURL", domainLoginURL);
				}
			}
		}

		List<String> domains = new ArrayList<>();
		for (Long orgId : orgIds) {
			Organization org = IAMOrgUtil.getOrg(orgId);
			domains.add(org.getDomain());
		}

		result.put("domainLookupRequired", domains.size() > 1);

		result.put("loginModes", loginModes);

		String jwt = createJWT("id", "auth0", userName, System.currentTimeMillis());
		result.put("digest", jwt);

		long uid = (Long) userForUsername.get(0).get("uid");
		insertTokenIntoSession(uid, null, jwt, IAMAccountConstants.SessionType.DIGEST_SESSION);

		return result;
	}

	@Override
	public String generateTotpSessionToken(String userName, String token) throws Exception {
		String jwt = createJWT("id", "auth0", token, System.currentTimeMillis());
		final List<Map<String, Object>> userForUsername = getUserData(userName, -1, null);

		Map<String, Object> user = userForUsername.get(0);
		insertTokenIntoSession((long) user.get("uid"), null, jwt, IAMAccountConstants.SessionType.TOTP_SESSION);

		return jwt;
	}

	@Override
	public String generateProxyUserSessionToken(String proxyUser) throws Exception {
		String jwt = createJWT("id", "auth0", proxyUser, System.currentTimeMillis(),true);
		return jwt;
	}

	@Override
	public String generateMFAConfigSessionToken(String userName, String token) throws Exception {
		String jwt = createJWT("id", "auth0", token, System.currentTimeMillis());
		final List<Map<String, Object>> userForUserName = getUserData(userName, -1, null);

		Map<String, Object> user = userForUserName.get(0);
		insertTokenIntoSession((long) user.get("uid"), null, jwt, IAMAccountConstants.SessionType.MFA_CONFIG_SESSION);

		return jwt;
	}

	@Override
	public long getUIDFromPWDResetToken(String digest) throws Exception {
		String emailFromDigest = getEmailFromDigest(digest, IAMAccountConstants.SessionType.PWD_POLICY_PWD_RESET);
		return Long.valueOf(emailFromDigest);
	}

	@Override
	public String generatePWDPolicyPWDResetToken(String userName, AppDomain.GroupType groupType) throws Exception {
		final List<Map<String, Object>> userForUserName = getUserData(userName, groupType);
		var uid = (long) userForUserName.get(0).get("uid");
		String jwt = createJWT("id", "auth0", uid + "", System.currentTimeMillis());
		insertTokenIntoSession(uid, null, jwt, IAMAccountConstants.SessionType.PWD_POLICY_PWD_RESET);
		return jwt;
	}

	private boolean isUserPresentInCurrentDC(String username, GroupType groupType) throws Exception {
		final List<Map<String, Object>> userInfo;
		if (groupType == null || groupType == GroupType.FACILIO) {
			userInfo = getUserData(username, -1, null);
		} else {
			userInfo = getUserData(username, groupType);
		}
		return CollectionUtils.isNotEmpty(userInfo);
	}

	// Assumes single user per DC
	@Override
	public int findDCForUser(String username, GroupType groupType) throws Exception {
		List<FacilioField> fields = IAMAccountConstants.getDCFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(IAMAccountConstants.getDCLookupModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("userName"), username, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("groupType"), String.valueOf(groupType.getIndex()), StringOperators.IS));

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return (int) props.get(0).get("dc");
		}
		throw new IllegalArgumentException("username not found");
	}

	@Override
	public void deleteDCLookup(String username, GroupType groupType) throws Exception {
		List<FacilioField> fields = IAMAccountConstants.getDCFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		GenericDeleteRecordBuilder deleteRecordBuilder = new GenericDeleteRecordBuilder();
		deleteRecordBuilder.table(IAMAccountConstants.getDCLookupModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("userName"), username, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("groupType"), groupType.getIndex() + "", NumberOperators.EQUALS));
		deleteRecordBuilder.delete();
	}

	@Override
	public long addDCLookup(Map<String, Object> props) throws Exception {
		List<FacilioField> fields = IAMAccountConstants.getDCFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder();
		selectRecordBuilder.table(IAMAccountConstants.getDCLookupModule().getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("userName"), String.valueOf(props.get("userName")), StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("groupType"), String.valueOf(props.get("groupType")), NumberOperators.EQUALS));
		List<Map<String, Object>> result = selectRecordBuilder.get();

		boolean ignoreEntry = CollectionUtils.isNotEmpty(result);

		if (CollectionUtils.isNotEmpty(result)) {
			if (!result.get(0).get("dc").toString().equals(props.get("dc").toString())) {
				throw new IllegalArgumentException("User cannot be of different DC.");
			}
		}

		if (!ignoreEntry) {
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(IAMAccountConstants.getDCLookupModule().getTableName())
					.fields(fields);

			return insertBuilder.insert(props);
		}

		return 0;
	}

	@SneakyThrows
	@Override
	public Integer lookupUserDC(String username, GroupType groupType) {
		if (isUserPresentInCurrentDC(username, groupType)) {
			return null;
		}

		Integer dc = IamClient.lookupUserDC(username, groupType);
		if (dc == null || dc <= 0) {
			throw new IllegalArgumentException("user name is missing");
		}
		return dc;
	}

	@Override
	public Map<String, Object> getLoginModes(String userName, String domain, AppDomain appDomain) throws Exception {
		if (StringUtils.isEmpty(userName)) {
			throw new IllegalArgumentException("user name is missing");
		}

		long orgId = -1L;
		Organization appOrg = null;
		if (domain != null) {
			appOrg = IAMOrgUtil.getOrg(domain);
			orgId = appOrg.getOrgId();
		}

		final List<Map<String, Object>> userForUsername = getUserData(userName, orgId, appDomain.getIdentifier());
		final long userPresent = 1;
		final long userNotPresent = 2;
		final Map<String, Object> result = new HashMap<>();

		if (CollectionUtils.isEmpty(userForUsername)) {
			result.put("code", userNotPresent);
			result.put("message", "user not present");
			return result;
		}

		result.put("code", userPresent);
		result.put("message", "user present");

		List<Long> orgIds = new ArrayList<>();
		userForUsername.forEach(i -> orgIds.add((long) i.get("orgId")));

		boolean hasPassword = hasPassword(userName);
		if (orgIds.size() == 1) {
			Organization org = appOrg;
			if (appOrg == null) {
				org = IAMOrgUtil.getOrg(orgIds.get(0));
			}

			result.put("logo_url", org.getLogoUrl());
		}

		List<String> loginModes = new ArrayList<>();
		if (hasPassword) {
			loginModes.add("password");
		}

		if (hasGoogleLogin((Long) userForUsername.get(0).get("uid"))) {
			loginModes.add("google");
		}

		List<AccountSSO> accountSSODetails = IAMOrgUtil.getAccountSSO(orgIds);

		boolean samlEnabled = false;
		if (CollectionUtils.isNotEmpty(accountSSODetails)) {
			for (AccountSSO ss : accountSSODetails) {
				if (ss.getIsActive() != null && ss.getIsActive()) {
					samlEnabled = true;
					break;
				}
			}
		}


		if (samlEnabled) {
			loginModes.add("SAML");
			AccountSSO accountSSO = accountSSODetails.get(0);
			orgId = accountSSO.getOrgId();
			String domainLoginURL = SSOUtil.getSSOEndpoint(IAMOrgUtil.getOrg(orgId).getDomain());
			result.put("SSOURL", domainLoginURL);
		}

		result.put("loginModes", loginModes);

		String jwt = createJWT("id", "auth0", userName, System.currentTimeMillis());
		result.put("digest", jwt);

		long uid = (Long) userForUsername.get(0).get("uid");
		insertTokenIntoSession(uid, null, jwt, IAMAccountConstants.SessionType.DIGEST_SESSION);

		return result;
	}

	public long startUserSessionv2(long uid, String token, String ipAddress, String userAgent, String userType) throws Exception {
		return startUserSessionv2(uid, token, ipAddress, userAgent, userType, false);
	}

	private long startUserSessionv2(long uid, String token, String ipAddress, String userAgent, String userType, long endTime, boolean isProxySession) throws Exception {
		TransactionManager transactionManager = null;
		try {
			transactionManager = FacilioTransactionManager.INSTANCE.getTransactionManager();

			Transaction transaction = transactionManager.getTransaction();
			if (transaction == null) {
				transactionManager.begin();
			}
			List<FacilioField> fields = IAMAccountConstants.getUserSessionFields();

			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(IAMAccountConstants.getUserSessionModule().getTableName())
					.fields(fields);

			Map<String, Object> props = new HashMap<>();
			props.put("uid", uid);
			props.put("sessionType", IAMAccountConstants.SessionType.USER_LOGIN_SESSION.getValue());
			props.put("token", token);
			props.put("startTime", System.currentTimeMillis());
			if (endTime > 0) {
				props.put("endTime", endTime);
			}
			props.put("isActive", true);
			props.put("ipAddress", ipAddress);
			props.put("userAgent", userAgent);
			props.put("userType", userType);
			props.put("isProxySession", isProxySession);
			props.put("lastActivityTime", System.currentTimeMillis());

			insertBuilder.addRecord(props);
			insertBuilder.save();
			transactionManager.commit();
			long sessionId = (Long) props.get("id");
			return sessionId;
		} catch (Exception e) {
			if (transactionManager != null) {
				transactionManager.rollback();
			}
			LOGGER.info("exception while adding user session transaction ", e);
		}
		return -1L;
	}

	private long startUserSessionv2(long uid, String token, String ipAddress, String userAgent, String userType, boolean isProxySession) throws Exception {
		return startUserSessionv2(uid, token, ipAddress, userAgent, userType, -1L, isProxySession);
	}

	@Override
	public boolean endUserSessionv2(long uid) throws Exception {
		return endUserSessionv2(uid, (Criteria) null);
	}

	@Override
	public boolean endUserSessionv2(long uid, Criteria criteria) throws Exception {
		boolean status = false;
		TransactionManager transactionManager = null;
		try {
			transactionManager = FacilioTransactionManager.INSTANCE.getTransactionManager();
			Transaction transaction = transactionManager.getTransaction();
			if (transaction == null) {
				transactionManager.begin();
			}
			List<FacilioField> fields = IAMAccountConstants.getUserSessionFields();

			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(IAMAccountConstants.getUserSessionModule().getTableName())
					.fields(fields);

			updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));

			if (criteria != null) {
				updateBuilder.andCriteria(criteria);
			}

			Map<String, Object> props = new HashMap<>();
			props.put("endTime", System.currentTimeMillis());
			props.put("isActive", false);

			int updatedRows = updateBuilder.update(props);
			if (updatedRows > 0) {
				LRUCache.getUserSessionCache().remove(String.valueOf(uid));
				status = true;
			}
			transactionManager.commit();
		} catch (Exception e) {
			if (transactionManager != null) {
				transactionManager.rollback();
			}
			LOGGER.info("exception while adding ending user session ", e);
		}
		return status;
	}

	@Override
	public boolean endUserSessionv2(long uid, String token) throws Exception {
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("TOKEN", "token", token, StringOperators.IS));
		return endUserSessionv2(uid, criteria);
	}

	@SneakyThrows
	private List<Map<String, Object>> getUserWebSessions(long uid) {
		List<Map<String, Object>> sessions = (List<Map<String, Object>>) LRUCache.getUserSessionCache().get(uid + "");
		if (sessions != null) {
			return sessions;
		}

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getUserSessionFields())
				.table("Account_Users")
				.innerJoin("UserSessions")
				.on("Account_Users.USERID = UserSessions.USERID");

		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.USERID", "userId", uid + "", StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.IS_ACTIVE", "isActive", "1", NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			sessions = props;
			LRUCache.getUserSessionCache().put(uid + "", sessions);
		}
		return sessions;
	}

	@Override
	public long getSessionExpiry(long uid, long sessionId) throws Exception {
		Map<String, Object> prop = null;
		List<Map<String, Object>> sessions = getUserWebSessions(uid);
		for (Map<String, Object> session : sessions) {
			long s = (long) session.get("id");
			if (s == sessionId && ("web".equalsIgnoreCase((String) session.get("userType")) || "mobile".equalsIgnoreCase((String) session.get("userType")))) {
				prop = session;
				break;
			}
		}

		if (prop == null) {
			return -1L;
		}

		SecurityPolicy userSecurityPolicy = getUserSecurityPolicy(uid);
		if (userSecurityPolicy == null) {
			return -1L;
		}

		boolean isWebSessManagementEnabled = BooleanUtils.isTrue(userSecurityPolicy.getIsWebSessManagementEnabled());

		if (userSecurityPolicy != null && isWebSessManagementEnabled) {
			Integer webSessLifeTime;
			Long startTime = (Long) prop.get("startTime");
			Instant startInstant = Instant.ofEpochMilli(startTime);
			if (userSecurityPolicy.getWebSessLifeTime() != null) {
				webSessLifeTime = userSecurityPolicy.getWebSessLifeTime();
				return startInstant.plus(webSessLifeTime, ChronoUnit.DAYS).toEpochMilli();
			} else if (userSecurityPolicy.getWebSessLifeTimesec() != null) {
				webSessLifeTime = userSecurityPolicy.getWebSessLifeTimesec();
				return startInstant.plus(webSessLifeTime, ChronoUnit.SECONDS).toEpochMilli();
			}
		}

		return -1L;
	}

	@Override
	public String isSessionExpired(long uid, long sessionId) throws Exception {
		return isSessionExpired(uid, sessionId, null);
	}

	@Override
	public String isSessionExpired(long uid, long sessionId, AppDomain appdomainObj) throws Exception {
		Map<String, Object> prop = null;
		List<Map<String, Object>> sessions = getUserWebSessions(uid);
		for (Map<String, Object> session : sessions) {
			long s = (long) session.get("id");
			if (s == sessionId && ("web".equalsIgnoreCase((String) session.get("userType")) || "mobile".equalsIgnoreCase((String) session.get("userType")))) {
				prop = session;
				break;
			}
		}

		if (prop == null) {
			return "false";
		}

		SecurityPolicy userSecurityPolicy = getUserSecurityPolicy(uid, appdomainObj);

		if (userSecurityPolicy == null) {
			return "false";
		}

		Long currentTime = System.currentTimeMillis();
		boolean isWebSessionTimeOutEnabled = BooleanUtils.isTrue(userSecurityPolicy.getIdleSessionTimeOut() != null && userSecurityPolicy.getIdleSessionTimeOut() > 0);

		if (userSecurityPolicy != null && isWebSessionTimeOutEnabled) {
			Duration duration = Duration.ofSeconds(userSecurityPolicy.getIdleSessionTimeOut());
			Long lastActivityTime = (Long) prop.get("lastActivityTime");
			if (lastActivityTime != null) {
				Instant activityInstant = Instant.ofEpochMilli(lastActivityTime);
				Instant currentInstant = Instant.ofEpochMilli(currentTime);
				Duration durationBetween = Duration.between(activityInstant, currentInstant);
				if (durationBetween.compareTo(duration) >= 0) {
					return "sessiontimeout";
				} else {
					Duration oneMinute = Duration.ofSeconds(MINUTE_IN_SECONDS);
					if (durationBetween.compareTo(oneMinute) >= 0) {
						updateUserSessionsLastActivityTime(sessionId, uid, currentTime);
					}
				}
			}
		}

		boolean isWebSessManagementEnabled = isWebSessManagementEnabled(userSecurityPolicy);

		if (userSecurityPolicy != null && isWebSessManagementEnabled) {
			Duration duration = null;
			if (userSecurityPolicy.getWebSessLifeTime() != null) {
				duration = Duration.ofDays(userSecurityPolicy.getWebSessLifeTime());
				LOGGER.info("Session Expired " + userSecurityPolicy.getWebSessLifeTime() + " days");
			} else if (userSecurityPolicy.getWebSessLifeTimesec() != null) {
				duration = Duration.ofSeconds(userSecurityPolicy.getWebSessLifeTimesec());
				LOGGER.info("Session Expired  " + userSecurityPolicy.getWebSessLifeTimesec() + " seconds");
			}
			Long startTime = (Long) prop.get("startTime");
			if (startTime != null) {
				Instant startInstant = Instant.ofEpochMilli(startTime);
				Instant currentInstant = Instant.ofEpochMilli(System.currentTimeMillis());
				Duration durationBetween = Duration.between(startInstant, currentInstant);
				if (durationBetween.compareTo(duration) >= 0) {
					return "sessionexpired";
				}
			}
		}
		return "false";
	}

	public boolean isWebSessManagementEnabled(SecurityPolicy userSecurityPolicy) {
		if (userSecurityPolicy.getWebSessLifeTime() != null && userSecurityPolicy.getWebSessLifeTime() > 0) {
			return true;
		}
		if (userSecurityPolicy.getWebSessLifeTimesec() != null && userSecurityPolicy.getWebSessLifeTimesec() > 0) {
			return true;
		}
		return false;
	}

	public void updateUserSessionsLastActivityTime(long sessionId, long uid, Long currentTime) throws Exception {
		FacilioField lastActivityTime = new FacilioField();
		lastActivityTime.setName("lastActivityTime");
		lastActivityTime.setDataType(FieldType.NUMBER);
		lastActivityTime.setColumnName("LAST_ACTIVITY_TIME");
		lastActivityTime.setModule(IAMAccountConstants.getUserSessionModule());

		List<FacilioField> fields = new ArrayList<>();
		fields.add(lastActivityTime);

		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getUserSessionModule().getTableName())
				.fields(fields);

		updateBuilder.andCondition(CriteriaAPI.getCondition("SESSIONID", "sessionId", String.valueOf(sessionId), NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		updateBuilder.andCondition(CriteriaAPI.getCondition("IS_ACTIVE", "isActive", "1", NumberOperators.EQUALS));
		Map<String, Object> props = new HashMap<>();
		props.put("lastActivityTime", currentTime);

		updateBuilder.update(props);

		LOGGER.info("Last Activity Time updated for sessionId: " + sessionId);

		LRUCache.getUserSessionCache().removeStartsWith(uid + "");
	}

	@Override
	public List<Map<String, Object>> getUserSessionsv2(long uid, Boolean isActive) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getUserSessionFields())
				.table("Account_Users")
				.innerJoin("UserSessions")
				.on("Account_Users.USERID =UserSessions.USERID");

		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));

		if (isActive != null) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.IS_ACTIVE", "isActive", isActive ? "1" : "0", NumberOperators.EQUALS));
		}

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return props;
		}
		return null;

	}

	@Override
	public void clearUserSessionv2(long uid, String token) throws Exception {

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(IAMAccountConstants.getUserSessionModule().getTableName());

		builder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		builder.andCondition(CriteriaAPI.getCondition("TOKEN", "token", token, StringOperators.IS));

		builder.delete();

		LRUCache.getUserSessionCache().remove(String.valueOf(uid));
	}

	@Override
	public void clearAllUserSessionsv2(long uid) throws Exception {

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(IAMAccountConstants.getUserSessionModule().getTableName());
		builder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		builder.delete();

		//LRUCache.getUserSessionCache().remove(email);
		LRUCache.getUserSessionCache().remove(String.valueOf(uid));

	}

	@Override
	public User createUserFromProps(Map<String, Object> prop) throws Exception {
		User user = FieldUtil.getAsBeanFromMap(prop, User.class);
		if (user.getPhotoId() > 0) {
			FileStore fs = FacilioFactory.getFileStoreFromOrg(user.getOrgId(), -1);
			user.setAvatarUrl(fs.newPreviewFileUrl("user", user.getPhotoId()));
		}

		return user;
	}

	@Override
	public String generatePermalinkForURL(long uid, long orgId, JSONObject sessionInfo) throws Exception {
		Organization org = IAMUtil.getOrgBean().getOrgv2(orgId);
		String tokenKey = orgId + "-" + uid;
		String jwt = createJWT("id", "auth0", tokenKey, System.currentTimeMillis() + 24 * 60 * 60000);

		insertTokenIntoSession(uid, sessionInfo, jwt, IAMAccountConstants.SessionType.PERMALINK_SESSION);

		return jwt;
	}

	public String addProxySession(String proxyUsername, String proxiedUserName, long proxiedSessionId) throws Exception {
		String jwt = createJWT("id", "auth0", proxyUsername + "_" + proxiedUserName, System.currentTimeMillis());
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(IAMAccountConstants.getProxySessionsModule().getTableName())
				.fields(IAMAccountConstants.getProxySessionsFields());

		Map<String, Object> vals = new HashMap<>();
		vals.put("email", proxyUsername);
		vals.put("token", jwt);
		vals.put("proxiedSessionId", proxiedSessionId);
		vals.put("createdTime", System.currentTimeMillis());
		vals.put("isActive", true);

		insertBuilder.addRecord(vals);
		insertBuilder.save();

		return jwt;
	}

	private long insertTokenIntoSession(long uid, JSONObject sessionInfo, String jwt, IAMAccountConstants.SessionType sessionType) throws SQLException {
		List<FacilioField> fields = IAMAccountConstants.getUserSessionFields();

		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(IAMAccountConstants.getUserSessionModule().getTableName())
				.fields(fields);

		Map<String, Object> props = new HashMap<>();
		props.put("uid", uid);
		props.put("sessionType", sessionType.getValue());
		props.put("token", jwt);
		props.put("startTime", System.currentTimeMillis());
		props.put("isActive", true);
		if (sessionInfo != null) {
			props.put("sessionInfo", sessionInfo.toJSONString());
		} else {
			props.put("sessionInfo", null);
		}

		insertBuilder.addRecord(props);
		insertBuilder.save();
		return (Long) props.get("id");
	}

	@Override
	public String generatePermalinkForURL(String url, long uid, long orgId) throws Exception {

		JSONObject sessionInfo = new JSONObject();
		sessionInfo.put("allowUrls", url);

		return generatePermalinkForURL(uid, orgId, sessionInfo);
	}

	@Override
	public boolean verifyPermalinkForURL(String token, List<String> urls) throws Exception {

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getUserSessionFields())
				.table("UserSessions");

		selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.TOKEN", "token", token, StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("IS_ACTIVE", "email", "1", NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			Map<String, Object> session = props.get(0);
			Long startTime = (Long) session.get("startTime");
			String sessionInfo = (String) session.get("sessionInfo");

			if ((startTime + 2678400000l) < System.currentTimeMillis()) { // one month
				// expired
				return false;
			}

			if (urls != null) {
				JSONParser parser = new JSONParser();
				try {
					JSONObject sessionInfoObj = (JSONObject) parser.parse(sessionInfo);
					String allowUrls = (String) sessionInfoObj.get("allowUrls");
					List<String> allowedUrlList = Arrays.asList(allowUrls.split(","));

					for (String url : urls) {
						if (allowedUrlList.contains(url)) {
							// url valid
							return true;
						}
					}
				} catch (ParseException e) {
					LOGGER.info("Exception occurred ", e);
				}
			} else {
				// token valid
				return true;
			}
		}
		return false;
	}


	@Override
	public String getEncodedTokenv2(IAMUser user) throws Exception {
		IAMUser iamUser = getFacilioUser(user.getOrgId(), user.getUid(), true);
		if (iamUser != null) {
			return EncryptionUtil.encode(iamUser.getOrgId() + USER_TOKEN_REGEX + iamUser.getUid() + USER_TOKEN_REGEX + iamUser.getEmail() + USER_TOKEN_REGEX + System.currentTimeMillis());
		}
		throw new IllegalArgumentException("Cannot generate token for invalid user");
	}


	@Override
	public IAMAccount getPermalinkAccount(String token, List<String> urls) throws Exception {
		// TODO Auto-generated method stub
		if (verifyPermalinkForURL(token, urls)) {
			DecodedJWT decodedjwt = validateJWT(token, "auth0");

			String[] tokens = null;
			if (decodedjwt.getSubject().contains(JWT_DELIMITER)) {
				tokens = decodedjwt.getSubject().split(JWT_DELIMITER)[0].split("-");
			} else {
				tokens = decodedjwt.getSubject().split("_")[0].split("-");
			}
			long orgId = Long.parseLong(tokens[0]);
			long uid = Long.parseLong(tokens[1]);

			IAMAccount currentAccount = new IAMAccount(IAMUtil.getOrgBean().getOrgv2(orgId), getFacilioUser(orgId, uid, true));
			return currentAccount;
		}
		return null;
	}

	public static String createJWT(String id, String issuer, String subject, long ttlMillis) {
		return createJWT(id, issuer, subject, ttlMillis, false);
	}

	public static String createJWT(String id, String issuer, String subject, long ttlMillis, boolean secretFromAWS) {

		try {
			String secret = "secret";
			if (secretFromAWS && FacilioProperties.getProxyJwtTokenSecret() != null) {
				secret = FacilioProperties.getProxyJwtTokenSecret();
			}
			Algorithm algorithm = Algorithm.HMAC256(secret);

			String key = subject + JWT_DELIMITER + System.currentTimeMillis();
			JWTCreator.Builder builder = JWT.create().withSubject(key)
					.withIssuer(issuer);

			return builder.sign(algorithm);
		} catch (UnsupportedEncodingException | JWTCreationException exception) {
			LOGGER.info("exception occurred while creating JWT " + exception.toString());
			//UTF-8 encoding not supported
		}
		return null;
	}
	public static DecodedJWT validateJWT(String token, String issuer) {
		return validateJWT(token, issuer, false);
	}
	public static DecodedJWT validateJWT(String token, String issuer, boolean secretFromAWS) {
		try {
			String secret = "secret";
			if (secretFromAWS && FacilioProperties.getProxyJwtTokenSecret() != null) {
				secret = FacilioProperties.getProxyJwtTokenSecret();
			}
			Algorithm algorithm = Algorithm.HMAC256(secret);
			JWTVerifier verifier = JWT.require(algorithm).withIssuer(issuer).build(); // Reusable verifier instance

			DecodedJWT jwt = verifier.verify(token);
			System.out.println("\ndecoded " + jwt.getSubject());
			System.out.println("\ndecoded " + jwt.getClaims());

			return jwt;
		} catch (UnsupportedEncodingException | JWTVerificationException exception) {
			LOGGER.info("exception occurred while decoding JWT "+ exception.toString());
			// UTF-8 encoding not supported
			return null;

		}
	}


	@Override
	public boolean verifyUser(long userId) throws Exception {
		// TODO Auto-generated method stub
		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(com.facilio.iam.accounts.util.IAMAccountConstants.getAccountsUserModule().getTableName()).fields(com.facilio.iam.accounts.util.IAMAccountConstants.getAccountsUserFields())
				.andCustomWhere("USERID = ?", userId);
		Map<String, Object> prop = new HashMap<>();
		prop.put("userVerified", true);
		if(updateBuilder.update(prop) > 0) {
			return true;
		}
		else {
			return false;
		}
	}


	@Override
	public boolean addUserMobileSetting(UserMobileSetting userMobileSetting) throws Exception {
		// TODO Auto-generated method stub
		if (userMobileSetting.getUserId() == -1) {
			Map<String, Object> map = getUserForEmail(userMobileSetting.getEmail(), -1, null);
			if(MapUtils.isEmpty(map)) {
				throw new AccountException(ErrorCode.ERROR_VALIDATING_CREDENTIALS, "Invalid Email");
			}
			userMobileSetting.setUserId((Long)map.get("uid"));
		}
		if (userMobileSetting.getCreatedTime() == -1) {
			userMobileSetting.setCreatedTime(System.currentTimeMillis());
		}

		// Fetching and adding only if it's not present already
		FacilioModule module = IAMAccountConstants.getUserMobileSettingModule();
		List<FacilioField> fields = IAMAccountConstants.getUserMobileSettingFields();
		String appLinkName = AccountUtil.getCurrentApp().getLinkName() == null ? FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP : AccountUtil.getCurrentApp().getLinkName();
		UserMobileSetting currentSetting = getUserMobileSetting(userMobileSetting.getUserId(),
				userMobileSetting.getMobileInstanceId(), module, fields,appLinkName);
		if (currentSetting == null) {
			userMobileSetting.setAppLinkName(appLinkName);
			long id = addUserMobileSetting(userMobileSetting, module, fields);
			if(id > 0) {
				return true;
			}
		} else {
			userMobileSetting.setUserMobileSettingId(currentSetting.getUserMobileSettingId());
			userMobileSetting.setUserId(-1);
			userMobileSetting.setMobileInstanceId(null);
			userMobileSetting.setAppLinkName(null);
			if(updateUserMobileSetting(userMobileSetting, module, fields) > 0) {
				return true;
			}
		}
		return false;
	}
	
	private long addUserMobileSetting(UserMobileSetting userMobileSetting, FacilioModule module,
			List<FacilioField> fields) throws Exception {
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder().table(module.getTableName())
				.fields(fields);

		return insertBuilder.insert(FieldUtil.getAsProperties(userMobileSetting));
	}

	private int updateUserMobileSetting(UserMobileSetting userMobileSetting, FacilioModule module,
			List<FacilioField> fields) throws Exception {
		FacilioField idField = FieldFactory.getAsMap(fields).get("userMobileSettingId");
		long id = userMobileSetting.getUserMobileSettingId();
		userMobileSetting.setUserMobileSettingId(-1);

		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder().table(module.getTableName())
				.fields(fields)
				.andCondition(CriteriaAPI.getCondition(idField, String.valueOf(id), PickListOperators.IS));
		return updateBuilder.update(FieldUtil.getAsProperties(userMobileSetting));
	}

	public int updateUserMobileFcmToken(UserMobileSetting userMobileSetting, FacilioModule module,
										HashMap<String, Object> updateValue) throws Exception {
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(IAMAccountConstants.getUserMobileSettingFields());

		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getUserMobileSettingModule().getTableName())
				.fields(IAMAccountConstants.getUserMobileSettingFields())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("userMobileSettingId"), String.valueOf(userMobileSetting.getUserMobileSettingId()), NumberOperators.EQUALS));
		return builder.update(updateValue);
	}
	
	private UserMobileSetting getUserMobileSetting(long userId, String instance, FacilioModule module,
			List<FacilioField> fields,String appLinkName) throws Exception {
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField userIdField = fieldMap.get("userId");
		FacilioField instanceField = fieldMap.get("mobileInstanceId");
		FacilioField appLinkNameField = fieldMap.get("appLinkName");
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(userIdField, String.valueOf(userId), PickListOperators.IS));
		if (instance!=null){
			criteria.addAndCondition(CriteriaAPI.getCondition(instanceField, instance, StringOperators.IS));
		}
		if (appLinkName != null){
			criteria.addAndCondition(CriteriaAPI.getCondition(appLinkNameField,appLinkName,StringOperators.IS));
		}
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().table(module.getTableName())
				.select(fields)
				.andCriteria(criteria);
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return FieldUtil.getAsBeanFromMap(props.get(0), UserMobileSetting.class);
		}
		return null;
	}

	public boolean removeUserMobileSetting(String mobileInstanceId, String appLinkName) throws Exception {
		List<FacilioField> fields = IAMAccountConstants.getUserMobileSettingFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(IAMAccountConstants.getUserMobileSettingModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("mobileInstanceId"), mobileInstanceId,
						StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("appLinkName"),appLinkName,StringOperators.IS));

		if(builder.delete() > 0) {
			return true;
		}
		return false;
	}


	@Override
	public boolean removeUserMobileSetting(String mobileInstanceId, boolean isPortal) throws Exception {
		// TODO Auto-generated method stub
		String appLinkName = AccountUtil.getCurrentApp().getLinkName() == null ? FacilioConstants.ApplicationLinkNames.FACILIO_MAIN_APP : AccountUtil.getCurrentApp().getLinkName();
		return removeUserMobileSetting(mobileInstanceId, appLinkName);
	}

	@Override
	public List<UserMobileSetting> getMobileInstanceIds(List<Long> uIds,String appLinkName) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioField> fields = new ArrayList<>();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(IAMAccountConstants.getUserMobileSettingFields());

		fields.add(fieldMap.get("mobileInstanceId"));
		fields.add(fieldMap.get("fromPortal"));
		fields.add(fieldMap.get("userId"));
		fields.add(fieldMap.get("userMobileSettingId"));

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields).table("User_Mobile_Setting")
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("userId"), uIds, NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("appLinkName"),appLinkName,StringOperators.IS))
				.orderBy("USER_MOBILE_SETTING_ID");

		List<UserMobileSetting> props = FieldUtil.getAsBeanListFromMapList(selectBuilder.get(),UserMobileSetting.class);
		return props;
	}

	@Override
	public List<UserMobileSetting> getUserMobileInstance(List<Long> intanceIds) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioField> fields = new ArrayList<>();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(IAMAccountConstants.getUserMobileSettingFields());

		fields.add(fieldMap.get("mobileInstanceId"));
		fields.add(fieldMap.get("fromPortal"));
		fields.add(fieldMap.get("userId"));
		fields.add(fieldMap.get("userMobileSettingId"));

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder().select(fields).table("User_Mobile_Setting")
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("userMobileSettingId"), intanceIds, NumberOperators.EQUALS))
				.orderBy("USER_MOBILE_SETTING_ID");

		List<UserMobileSetting> props = FieldUtil.getAsBeanListFromMapList(selectBuilder.get(),UserMobileSetting.class);
		return props;
	}

	@Override
	public Organization getOrgv2(String currentOrgDomain, long uid) throws Exception {
		return getOrgv2(currentOrgDomain, uid, Organization.OrgType.PRODUCTION);
	}

	@Override
	public Organization getOrgv2(String currentOrgDomain, long uid, Organization.OrgType orgType) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getOrgFields())
				.table("Organizations")
				.innerJoin("Account_ORG_Users")
				.on("Organizations.ORGID = Account_ORG_Users.ORGID")
				;

		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USER_STATUS", "userStatus", "1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "orgUserDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("FACILIODOMAINNAME", "domainName", currentOrgDomain, StringOperators.IS));
		selectBuilder.andCriteria(getOrgTypeCriteria(orgType));

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return IAMOrgUtil.createOrgFromProps(props.get(0));
		}
		return null;
	}


	@Override
	public Object getPermalinkDetails(String token) throws Exception {
		// TODO Auto-generated method stub
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
                .select(IAMAccountConstants.getUserSessionFields())
                .table("UserSessions");

        selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.TOKEN", "token", token, StringOperators.IS));
        selectBuilder.andCondition(CriteriaAPI.getCondition("IS_ACTIVE", "email", "1", NumberOperators.EQUALS));

        List<Map<String, Object>> props = selectBuilder.get();
        if (props != null && !props.isEmpty()) {
            Map<String, Object> session = props.get(0);
            Object sessionInfo = session.get("sessionInfo");
            return sessionInfo;
        }
        return null;


	}
	
	@Override
	public IAMUser getFacilioUserV3(String username, String identifier) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Account_Users")
				.andCondition(CriteriaAPI.getCondition("Account_Users.USERNAME","username" , username, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition("Account_Users.IDENTIFIER","identifier" , identifier, StringOperators.IS));
			
		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			IAMUser user =  createUserFromProps(props.get(0));
			return user;
		}
		return null;
	}
	
	private long addUserV3(long orgId, IAMUser user, boolean emailRegRequired, String identifier) throws Exception {

		user.setIdentifier(identifier);
		
		IAMUser appUser = getFacilioUserV3(user.getUserName(), identifier);
		if (appUser != null) {
			user.setUid(appUser.getUid());
			user.setUserVerified(appUser.getUserVerified());
		}
		else {
			addUserEntryV3(user, orgId);
			String[] s = StringUtils.split(identifier, '_');
			IamClient.addUserToDC(user.getUserName(), GroupType.valueOf(Integer.parseInt(s[0])));
		}
		IAMUser userExistsForAnyOrg = getFacilioUserFromUserIdv3(user.getUid(), null);
		if(userExistsForAnyOrg != null) {
			user.setDefaultOrg(false);
		}
		else {
			user.setDefaultOrg(true);
		}
		user.setUserStatus(true);
		user.setOrgId(orgId);
		long ouId = checkIfExistsInOrgUsers(orgId, user);
		user.setIamOrgUserId(ouId);
		return ouId;
	}

	private long checkIfExistsInOrgUsers(long orgId, IAMUser user) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		List<FacilioField> orgUserFields = IAMAccountConstants.getAccountsOrgUserFields();
		fields.addAll(orgUserFields);
	
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Account_ORG_Users")
		;
		if(orgId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		}
		selectBuilder.andCondition(CriteriaAPI.getCondition("userId", "userId", String.valueOf(user.getUid()), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		
		List<Map<String, Object>> mapList = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(mapList)) {
			return (long)mapList.get(0).get("iamOrgUserId");
		}
		return addAccountOrgUserEntry(orgId, user);

	}
	private long addUserEntryV3(IAMUser user, long orgId) throws Exception {

		List<FacilioField> fields = IAMAccountConstants.getAccountsUserFields();
		fields.add(IAMAccountConstants.getUserPasswordField());
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(IAMAccountConstants.getAccountsUserModule().getTableName())
				.fields(fields);

		validateUsername(user, fields);

		Map<String, Object> props = FieldUtil.getAsProperties(user);

		insertBuilder.addRecord(props);
		insertBuilder.save();
		long userId = (Long) props.get("id");
		user.setUid(userId);
//		in case of adduser, password will be null
		if(StringUtils.isNotEmpty(user.getPassword())){
			FacilioService.runAsService(FacilioConstants.Services.IAM_SERVICE, () -> IAMUtil.getUserBean().savePreviousPassword(user.getUid(),user.getPassword()));
		}

		if(!isUserMfaSettingsExists(userId)) {
			initialiseUserMfaSettings(userId);
		}
		return userId;
	}

	private void validateUsername(IAMUser user, List<FacilioField> fields) throws IAMUserException {
		Map<String, FacilioField> asMap = FieldFactory.getAsMap(fields);
		if (asMap.containsKey("userName")) {
			if (StringUtils.isNotEmpty(user.getUserName()) && StringUtils.containsWhitespace(user.getUserName())) {
				throw new IAMUserException(IAMUserException.ErrorCode.USERNAME_HAS_WHITESPACE, "Username contains space.");
			}
		}
	}

	private boolean isUserMfaSettingsExists(long userId) throws Exception{

		if(userId <= 0) {
			throw new IllegalArgumentException("Invalid UserId");
		}
		else {

			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(IAMAccountConstants.getUserMfaSettingsFields())
					.table(IAMAccountConstants.getUserMfaSettings().getTableName())
					.andCondition(CriteriaAPI.getCondition("UserMfaSettings.USERID", "userId", userId + "", NumberOperators.EQUALS));

			List<Map<String, Object>> props = selectBuilder.get();
			return !props.isEmpty();
		}
	}
	

	private long addAccountOrgUserEntry(long orgId, IAMUser user) throws Exception {

		List<FacilioField> fields = IAMAccountConstants.getAccountsOrgUserFields();
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.table(IAMAccountConstants.getAccountOrgUserModule().getTableName())
				.fields(fields);

		
		user.setOrgId(orgId);
		Map<String, Object> props = FieldUtil.getAsProperties(user);
		insertBuilder.addRecord(props);
		insertBuilder.save();
		long accUserAppOrgId = (Long) props.get("id");
		return accUserAppOrgId;
			
	}


	private LoggingEvent getUserLoginEvent(String username, String remoteIp) {
		LoggingEvent event = new LoggingEvent(LOGGER.getName(), LOGGER, Level.INFO, "", null);
		event.setProperty("email", username);
		event.setProperty("remoteIp", remoteIp);
		return event;
	}

	@Override
	public SecurityPolicy getUserSecurityPolicy(String email, AppDomain.GroupType groupType) throws Exception {
		List<Map<String, Object>> userData = getUserData(email, groupType);
		if (CollectionUtils.isEmpty(userData)) {
			return null;
		}

		var uid = (long) userData.get(0).get("uid");
		return getUserSecurityPolicy(uid);
	}

	@Override
	public Map<String, Object> getUserMfaSettings(String email, AppDomain.GroupType groupType) throws Exception {
		List<Map<String, Object>> userData = getUserData(email, groupType);
		if (CollectionUtils.isEmpty(userData)) {
			return null;
		}

		var user = userData.get(0);
		long uid = (long) user.get("uid");

		return getUserMfaSettings(uid);
	}

	public Map<String, Object> validateAndGenerateTokenV3(String username, String password, String appDomainName, String userAgent, String userType,
														  String ipAddress) throws Exception {
		long validUid = verifyPasswordv3(username, password, appDomainName, userType);
		if ( FacilioProperties.logUserAccessLog()) {
			LoggingEvent event = getUserLoginEvent(username, ipAddress);
			if(validUid > 0) {
				event.setProperty("responseCode", "200");
			} else {
				event.setProperty("responseCode", "500");
			}
			USER_LOGIN.callAppenders(event);
		}

		if (validUid > 0) {
			//IAMUser user = getFacilioUser(emailaddress, -1, domain);
			String jwt = createJWT("id", "auth0", String.valueOf(validUid),
					System.currentTimeMillis() + 24 * 60 * 60000);
			long sessionId = startUserSessionv2(validUid, jwt, ipAddress, userAgent, userType);
			Map<String, Object> ret = new HashMap<>();
			ret.put("uid", validUid);
			ret.put("token", jwt);
			ret.put("sessionId", sessionId);
			ret.put("username", username);
			return ret;
			//throw new AccountException(ErrorCode.USER_DEACTIVATED_FROM_THE_ORG, "User is deactivated, Please contact admin to activate.");
		}
		throw new AccountException(ErrorCode.ERROR_VALIDATING_CREDENTIALS, "Invalid username or password");
	}


	@Override
	public String validateAndGenerateTokenV3(String username, String password, String appDomainName,
			String userAgent, String userType, String ipAddress, boolean startUserSession) throws Exception {
		long validUid = verifyPasswordv3(username, password, appDomainName, userType);

		// added to log log in failures for etisalat.
		if ( FacilioProperties.logUserAccessLog()) {
			LoggingEvent event = getUserLoginEvent(username, ipAddress);
			if(validUid > 0) {
				event.setProperty("responseCode", "200");
			} else {
				event.setProperty("responseCode", "500");
			}
			USER_LOGIN.callAppenders(event);
		}

		if (validUid > 0) {
			//IAMUser user = getFacilioUser(emailaddress, -1, domain);
			String jwt = createJWT("id", "auth0", String.valueOf(validUid),
					System.currentTimeMillis() + 24 * 60 * 60000);
			if (startUserSession) {
				startUserSessionv2(validUid, jwt, ipAddress, userAgent, userType);
			}
			return jwt;
			//throw new AccountException(ErrorCode.USER_DEACTIVATED_FROM_THE_ORG, "User is deactivated, Please contact admin to activate.");
		}
		throw new AccountException(ErrorCode.ERROR_VALIDATING_CREDENTIALS, "Invalid username or password");
	}

	@Override
	public String decodeProxyUserToken(String token) throws Exception {
		DecodedJWT decodedJWT = validateJWT(token, "auth0",true);
		String[] split = decodedJWT.getSubject().split(JWT_DELIMITER);
		long createdTime = Long.parseLong(split[1]);

		Instant creationInstant = Instant.ofEpochMilli(createdTime);
		Instant expiry = creationInstant.plus(3, ChronoUnit.MINUTES);


		if (Instant.ofEpochMilli(System.currentTimeMillis()).isAfter(expiry)) {
			if (FacilioProperties.isDevelopment()) {
				return split[0];
			}
			LOGGER.error("Token expired:" + token);
			return null;
		}
		return split[0];
	}

	@Override
	public String getEmailFromDigest(String digest) throws Exception {
		return getEmailFromDigest(digest, IAMAccountConstants.SessionType.DIGEST_SESSION);
	}

	@Override
	public String getEmailFromDigest(String digest, IAMAccountConstants.SessionType sessionType) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getUserSessionFields())
				.table("UserSessions");
		selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.TOKEN", "token", digest, StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("IS_ACTIVE", "isActive", "1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("SESSION_TYPE", "sessionType", sessionType.getValue()+"", NumberOperators.EQUALS));

		List<Map<String, Object>> props = selectBuilder.get();

		if (CollectionUtils.isEmpty(props)) {
			throw new AccountException(ErrorCode.INVALID_DIGEST, "Digest has expired");
		}

		Map<String, Object> prop = props.get(0);

		DecodedJWT decodedJWT = validateJWT(digest, "auth0");
		String[] split = decodedJWT.getSubject().split(JWT_DELIMITER);
		String email = split[0];

		long createdTime = (long) prop.get("startTime");
		long currentTime = System.currentTimeMillis();

		if (currentTime > (createdTime + (15 * 60 * 1000))) {
			throw new AccountException(ErrorCode.INVALID_DIGEST, "Digest has expired");
		}

		return email;
	}

	public Map<String, Object> generateRecoveryCode(long uid) throws Exception {
		IAMAccount userV3 = getAccountv3(uid);
		String name = userV3.getUser().getName();
		String fileName = name + " recovery-codes.txt";
		Map<String, Object> recoveryCode = new HashMap<>();
		RecoveryCodeGenerator recoveryCodes = new RecoveryCodeGenerator();
		String[] codes = recoveryCodes.generateCodes(16);
		String content = StringUtils.join(codes, '\n');
		long l = DBConf.getInstance().addFile(content, fileName, "text/plain");
		recoveryCode.put("codes", codes);

		return recoveryCode;
	}


	@Override
	public long verifyPasswordv3(String username, String password, String appDomainName, String userType) throws Exception {
		try {
			if (StringUtils.isEmpty(appDomainName)) {
				appDomainName = AccountUtil.getDefaultAppDomain();
			}
			
			List<FacilioField> fields = new ArrayList<>();
			fields.addAll(IAMAccountConstants.getAccountsUserFields());
			fields.add(IAMAccountConstants.getUserPasswordField());
			
			GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
					.select(fields)
					.table("Account_Users")
					.innerJoin("Account_ORG_Users")
					.on("Account_Users.USERID = Account_ORG_Users.USERID")
					;
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.USERNAME", "username", username, StringOperators.IS));
			selectBuilder.andCondition(CriteriaAPI.getCondition("USER_VERIFIED", "userVerified", "1", NumberOperators.EQUALS));
			selectBuilder.andCondition(CriteriaAPI.getCondition("DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
			selectBuilder.andCondition(CriteriaAPI.getCondition("USER_STATUS", "userStatus", "1", NumberOperators.EQUALS));

			AppDomain appDomainObj = getAppDomain(appDomainName);
			if(appDomainObj != null) {
				selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.IDENTIFIER", "identifier", appDomainObj.getIdentifier(), StringOperators.IS));
				if(appDomainObj.getDomainTypeEnum() == AppDomain.DomainType.CUSTOM && appDomainObj.getOrgId() > 0){
					//this check can be removed later..adding it for sutherland demo(894 is the custom domain id for org 343 in production)
					if(!FacilioProperties.isProduction() || appDomainObj.getId() != 894l) {
						selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ORGID", "orgId", String.valueOf(appDomainObj.getOrgId	()), StringOperators.IS));
					}
				}
			}
			
			List<Map<String, Object>> props = selectBuilder.get();

			if (CollectionUtils.isNotEmpty(props)) {
				Map<String, Object> result = props.get(0);
				String storedPass = (String)result.get("password");
				if (storedPass.equals(password)) {
					return (long)result.get("uid");
				}
			} else {
				LOGGER.info("No records found for  " + username +" --> User doesn't exists");
				throw new AccountException(ErrorCode.ERROR_VALIDATING_CREDENTIALS, "Invalid username or password");
			}

		} catch (SQLException | RuntimeException e) {
			LOGGER.info("Exception while verifying password, "+ e.toString());
		} 
		return -1;
	}

	public List<APIClient> getApiClientList(long orgId, long userId) throws Exception {
		Map<String, FacilioField> asMap = FieldFactory.getAsMap(IAMAccountConstants.getDevClientFields());
		List<FacilioField> fields = Arrays.asList(asMap.get("id"), asMap.get("authType"), asMap.get("name"), asMap.get("oauth2ClientId"), asMap.get("createdTime"));
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
		selectBuilder.select(fields)
				.table(IAMAccountConstants.getDevClientModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("USERID", "uid", userId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("ORGID", "orgId", orgId+"", NumberOperators.EQUALS));
		List<Map<String, Object>> props = selectBuilder.get();

		if (CollectionUtils.isEmpty(props)) {
			return Collections.emptyList();
		}

		return FieldUtil.getAsBeanListFromMapList(props, APIClient.class);
	}

	private static int OAUTH2  = 1;
	private static int APIKEY  = 2;

	public void deleteApiClient(long orgId, long userId, long id) throws Exception {
		Map<String, FacilioField> asMap = FieldFactory.getAsMap(IAMAccountConstants.getDevClientFields());

		GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
				.table(IAMAccountConstants.getDevClientModule().getTableName())
				.andCondition(CriteriaAPI.getCondition(asMap.get("id"), id+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(asMap.get("orgId"), orgId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(asMap.get("uid"), userId+"", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getIdCondition(id, IAMAccountConstants.getDevClientModule()));
		builder.delete();
	}

	public Map<String, String> createOauth2Client(long orgId, long userId, String name) throws Exception {
		OAuth2Client client = new HydraClient().createClient(name);

		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder();
		insertRecordBuilder.table(IAMAccountConstants.getDevClientModule().getTableName())
				.fields(IAMAccountConstants.getDevClientFields());

		Map<String, Object> prop = new HashMap<>();
		prop.put("orgId", orgId);
		prop.put("uid", userId);
		prop.put("authType", OAUTH2);
		prop.put("createdTime", System.currentTimeMillis());
		prop.put("name", name);
		prop.put("oauth2ClientId", client.getClientId());

		insertRecordBuilder.addRecord(prop);
		insertRecordBuilder.save();

		Map<String, String> res = new HashMap<>();
		res.put("token", client.getClientSecret());
		res.put("clientId", client.getClientId());

		return res;
	}

	public String createApiKey(long orgId, long userId, String name) throws Exception {
		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder();
		insertRecordBuilder.table(IAMAccountConstants.getDevClientModule().getTableName())
				.fields(IAMAccountConstants.getDevClientFields());

		Map<String, Object> prop = new HashMap<>();
		prop.put("orgId", orgId);
		prop.put("uid", userId);
		prop.put("authType", APIKEY);
		prop.put("createdTime", System.currentTimeMillis());
		prop.put("name", name);
		KeyGenerator keyGen = KeyGenerator.getInstance("AES");
		keyGen.init(256); // for example
		SecretKey secretKey = keyGen.generateKey();
		byte[] encoded = secretKey.getEncoded();

		String apiKey = DatatypeConverter.printHexBinary(encoded).toLowerCase();

		prop.put("apiKey", apiKey);
		prop.put("uid", userId);

		insertRecordBuilder.addRecord(prop);
		insertRecordBuilder.save();

		return apiKey;
	}

	public IAMAccount fetchAccountByAPIKey(String apiKey) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
		selectBuilder.select(IAMAccountConstants.getDevClientFields())
				.table(IAMAccountConstants.getDevClientModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("API_KEY", "apiKey", apiKey, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition("AUTH_TYPE", "authType", APIKEY+"", NumberOperators.EQUALS));
		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isEmpty(props)) {
			LOGGER.error("No client found for " + apiKey);
			return null;
		}

		long uid = (Long) props.get(0).get("uid");
		long orgId = (Long) props.get(0).get("orgId");

		IAMAccount accountv3 = getAccountv3(uid);
		accountv3.setOrg(IAMOrgUtil.getOrg(orgId));
		return accountv3;
	}

	public IAMAccount fetchAccountByOauth2ClientId(String oauth2ClientId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder();
		selectBuilder.select(IAMAccountConstants.getDevClientFields())
				.table(IAMAccountConstants.getDevClientModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("OAUTH2_CLIENT_ID", "oauth2ClientId", oauth2ClientId, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition("AUTH_TYPE", "authType", OAUTH2+"", NumberOperators.EQUALS));
		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isEmpty(props)) {
			LOGGER.error("No client found for " + oauth2ClientId);
			return null;
		}

		long uid = (Long) props.get(0).get("uid");
		long orgId = (Long) props.get(0).get("orgId");

		IAMAccount accountv3 = getAccountv3(uid);
		accountv3.setOrg(IAMOrgUtil.getOrg(orgId));
		return accountv3;
	}

	@Override
	public Map<String, Object> getUserSession(long sessionId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getUserSessionFields())
				.table("Account_Users")
				.innerJoin("UserSessions")
				.on("Account_Users.USERID = UserSessions.USERID");

		selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.SESSIONID", "sessionId", sessionId + "", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.IS_ACTIVE", "isActive", "1", NumberOperators.EQUALS));

		return selectBuilder.fetchFirst();
	}

	public IAMAccount verifyProxyToken(String proxyToken, IAMAccount proxyUser) throws Exception {
		Long sessionId = proxyUser.getUserSessionId();
		if (sessionId == null) {
			return null;
		}

		Map<String, Object> props = getProxySession(proxyToken);
		long sessionIdFromDb = (long) props.get("sessionId");
		if (sessionId != sessionIdFromDb) {
			throw new AccountException(ErrorCode.NOT_PERMITTED, "Session does not match");
		}

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getUserSessionFields())
				.table("Account_Users")
				.innerJoin("UserSessions")
				.on("Account_Users.USERID = UserSessions.USERID");

		selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.SESSIONID", "sessionId", ((long) props.get("proxiedSessionId")) + "", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.IS_ACTIVE", "isActive", "1", NumberOperators.EQUALS));

		Map<String, Object> sessionprops = selectBuilder.fetchFirst();

		long proxiedUserId = (long) sessionprops.get("uid");
		long proxiedSessionId = (long) sessionprops.get("id");

		IAMAccount accountv3 = getAccountv3(proxiedUserId);
		accountv3.setUserSessionId(proxiedSessionId);
		return accountv3;
	}

	@Override
	public Map<String, Object> getProxySession(String proxyToken) throws Exception {
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder();
		selectRecordBuilder.table(IAMAccountConstants.getProxySessionsModule().getTableName())
				.select(IAMAccountConstants.getProxySessionsFields())
				.andCondition(CriteriaAPI.getCondition("IS_ACTIVE", "isActive","true" ,BooleanOperators.IS))
				.andCondition(CriteriaAPI.getCondition("TOKEN", "token",proxyToken, StringOperators.IS));
		return selectRecordBuilder.fetchFirst();
	}

	@Override
	public IAMAccount verifyFacilioTokenv3(String idToken, boolean overrideSessionCheck,
			String userType) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("verifiyFacilioToken() :idToken :"+idToken);
		try {
			DecodedJWT decodedjwt = validateJWT(idToken, "auth0");
			if(decodedjwt != null) {
				String uId = null;
				if (decodedjwt.getSubject().contains(JWT_DELIMITER)) {
					uId = decodedjwt.getSubject().split(JWT_DELIMITER)[0];
				}
				else {
					uId = decodedjwt.getSubject().split("_")[0];
				}
				IAMAccount account = null;
				long userId = Long.parseLong(uId);
				if(overrideSessionCheck) {
					account = IAMUtil.getUserBean().getAccountv3(userId);
				}
				else {
					account = IAMUtil.getUserBean().verifyUserSessionv3(uId, idToken, userType);
				}
			
				return account;
			}
			return null;
		}
		catch (AccountException e) {
			throw e;
		}
		catch (Exception e) {
			var cause = e.getCause();
			if (cause instanceof SecurityPolicyException) {
				throw (SecurityPolicyException) cause;
			}
			LOGGER.info("Exception occurred "+e.toString());
			return null;
		}

	}


	@Override
	public IAMAccount verifyUserSessionv3(String uId, String token, String userType)
			throws Exception {
		// TODO Auto-generated method stub
		List<Map<String, Object>> sessions = (List<Map<String, Object>>) LRUCache.getUserSessionCache().get(uId);
		if (sessions == null) {
			sessions = new ArrayList<>();
		}
		for (Map<String, Object> session : sessions) {
			String sessionToken = (String) session.get("token");
			if (Objects.equals(sessionToken, token)) {
				Long endTime = (Long) session.get("endTime");
				if (endTime != null && System.currentTimeMillis() > endTime) {
					LOGGER.info("Session expired: " + (long) session.get("id"));
					return null;
				}
				IAMAccount account = getAccountv3((long) session.get("uid"));
				if(account != null) {
					account.setUserSessionId((long) session.get("id"));
				}
				LOGGER.debug("Verified token from cache entry for : " + uId);
				return account;
			}
		}

		LOGGER.debug("No session cache entry for : " + uId);
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getUserSessionFields())
				.table("Account_Users")
				.innerJoin("UserSessions")
				.on("Account_Users.USERID = UserSessions.USERID");
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.USERID", "userId", uId, StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.TOKEN", "token", token, StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("UserSessions.IS_ACTIVE", "isActive", "1", NumberOperators.EQUALS));
		
		
		Map<String, Object> props = selectBuilder.fetchFirst();
		if (MapUtils.isNotEmpty(props)) {
			LRUCache.getUserSessionCache().put(uId, sessions);
			IAMAccount account = getAccountv3((long) props.get("uid"));
			if(account != null && account.getUser() != null) {
				account.setUserSessionId((long)props.get("id"));
			}
			LOGGER.debug("Verified token from DB entry for : " + uId);
			sessions.add(props);

			Long endTime = (Long) props.get("endTime");
			if (endTime != null && System.currentTimeMillis() > endTime) {
				LOGGER.debug("Session expired: " + (long) props.get("id"));
				return null;
			}
			
			return account;
		}
		return null;
	}
	
	private GenericSelectRecordBuilder getSelectBuilder(List<FacilioField> fields) {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Account_Users")
				.innerJoin("Account_ORG_Users")
				.on("Account_Users.USERID = Account_ORG_Users.USERID")
				.innerJoin("Organizations")
				.on("Organizations.ORGID = Account_ORG_Users.ORGID")
				;
		
		return selectBuilder;
	}

	public IAMUser getFacilioUserFromUserIdv3(long userId, String orgDomain) throws Exception {
		
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		fields.add(IAMAccountConstants.getUserPasswordField());
		
		GenericSelectRecordBuilder selectBuilder = getSelectBuilder(fields);
		
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.USERID", "userId", String.valueOf(userId), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("USER_VERIFIED", "userVerified", "1", NumberOperators.EQUALS));
		
		if (!StringUtils.isEmpty(orgDomain)) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.FACILIODOMAINNAME", "facilioDomainName", orgDomain, StringOperators.IS));
			selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		} 
		else {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ISDEFAULT", "isDefault", "1", NumberOperators.EQUALS));
		}
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "deletedTime", "-1", NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();


		if (props != null && !props.isEmpty()) {
			IAMUser user =  createUserFromProps(props.get(0));
			return user;
		}
		return null;
	}
	
	@Override
	public IAMAccount getAccountv3(long userId) throws Exception {
		IAMUser user = getFacilioUserFromUserIdv3(userId, null);
		if (user == null) {
			throw new AccountException(ErrorCode.USER_DOESNT_EXIST_IN_ORG, "IAMUser doesn't exists in the current Org");
		}
		
		IAMAccount account = new IAMAccount(null, user);
		return account;
	}
	
	@Override
	public Organization getDefaultOrgv3(long uid) throws Exception {
		return getDefaultOrgv3(uid, Organization.OrgType.PRODUCTION);
	}

	@Override
	public Organization getDefaultOrgv3(long uid, Organization.OrgType orgType) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getOrgFields())
				.table("Organizations")
				.innerJoin("Account_ORG_Users")
				.on("Organizations.ORGID = Account_ORG_Users.ORGID")
				;

		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USERID", "userId", String.valueOf(uid), NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "orgUserDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ISDEFAULT", "isDefault", "1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USER_STATUS", "userStatus", "1", NumberOperators.EQUALS));
		selectBuilder.andCriteria(getOrgTypeCriteria(orgType));

		List<Map<String, Object>> props = selectBuilder.get();
		if (props != null && !props.isEmpty()) {
			return IAMOrgUtil.createOrgFromProps(props.get(0));
		}
		return null;
	}

	@Override
	public List<IAMUser> getUserDataForUidsv3(String userIds, long orgId, boolean shouldFetchDeleted) throws Exception {
		// TODO Auto-generated method stub
		
		List<FacilioField> fields = new ArrayList<FacilioField>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		fields.addAll(IAMAccountConstants.getAccountsOrgUserFields());
		
		GenericSelectRecordBuilder selectBuilder = getSelectBuilder(fields);

		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ORG_USERID", "iamOrgUserId", userIds, NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		if(orgId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		}
		if(!shouldFetchDeleted) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "orgUserDeletedTime", "-1", NumberOperators.EQUALS));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(props)) {
			List<IAMUser> iamUsers = new ArrayList<IAMUser>();
			for(Map<String, Object> map : props) {
				iamUsers.add(createUserFromProps(map));
			}
			return iamUsers;
		}
		return null;
		
	}

	@Override
	public boolean verifyPassword(long orgId, long userId, String oldPassword) throws Exception {
		// TODO Auto-generated method stub

		IAMUser iamUser = getFacilioUser(orgId, userId, true);
		if(iamUser == null) {
			return false;
		}
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		fields.add(IAMAccountConstants.getUserPasswordField());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Account_Users")
				;
		selectBuilder.andCondition(CriteriaAPI.getCondition("USERID", "userId", String.valueOf(userId), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			Map<String, Object> result = props.get(0);
			String storedPass = (String)result.get("password");
			if (storedPass.equals(oldPassword)) {
				return true;
			}
		} 
		return false;
	}


	private List<String> getUserPrevPassword(long uid, int numberOfPass) throws Exception {
		List<FacilioField> userPrevPwdsFields = IAMAccountConstants.getUserPrevPwdsFields();
		Map<String, FacilioField> userPwdFieldMap = FieldFactory.getAsMap(userPrevPwdsFields);
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(userPrevPwdsFields)
				.table("UserPrevPwds")
				.andCondition(CriteriaAPI.getCondition(userPwdFieldMap.get("userId"), uid+"", NumberOperators.EQUALS))
				.limit(numberOfPass)
				.orderBy("CHANGED_TIME DESC");
		List<Map<String, Object>> maps = selectBuilder.get();
		if (CollectionUtils.isEmpty(maps)) {
			return null;
		}

		List<String> prevPwds = new ArrayList<>();
		for (Map<String, Object> map: maps) {
			prevPwds.add((String) map.get("password"));
		}
		return prevPwds;
	}

	@Override
	public SecurityPolicy getUserSecurityPolicy(long uid) throws Exception {
		return getUserSecurityPolicy(uid ,null);
	}

	public SecurityPolicy getUserSecurityPolicy(long uid, AppDomain appDomain) throws Exception {
		var prop = (Map<String, Object>) LRUCache.getUserSecurityPolicyCache().get(uid+"");
		if (MapUtils.isNotEmpty(prop)) {
			return FieldUtil.getAsBeanFromMap(prop, SecurityPolicy.class);
		}
		List<FacilioField> securityPolicyFields = IAMAccountConstants.getSecurityPolicyFields();
		Map<String, FacilioField> accountUserFieldMap = FieldFactory.getAsMap(IAMAccountConstants.getAccountsUserFields());

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(securityPolicyFields);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Account_Users")
				.innerJoin("SecurityPolicies")
				.on("SecurityPolicies.SECURITY_POLICY_ID = Account_Users.SECURITY_POLICY_ID")
				.andCondition(CriteriaAPI.getCondition(accountUserFieldMap.get("uid"), uid+"", NumberOperators.EQUALS));

		Map<String, Object> map = selectBuilder.fetchFirst();
		if(map == null && appDomain != null && appDomain.getSecurityPolicyId() > 0){
			map = getUserSecurityPolicyForApp(appDomain.getSecurityPolicyId());
		}
		LRUCache.getUserSecurityPolicyCache().put(uid+"", map);
		return FieldUtil.getAsBeanFromMap(map, SecurityPolicy.class);
	}

	public Map<String, Object> getUserSecurityPolicyForApp(long securityPolicyId) throws Exception {
		List<FacilioField> securityPolicyFields = IAMAccountConstants.getSecurityPolicyFields();
		Map<String, FacilioField> appDomainFieldMap = FieldFactory.getAsMap(IAMAccountConstants.getAppDomainFields());

		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(securityPolicyFields);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("App_Domain")
				.innerJoin("SecurityPolicies")
				.on("SecurityPolicies.SECURITY_POLICY_ID = App_Domain.SECURITY_POLICY_ID")
				.andCondition(CriteriaAPI.getCondition(appDomainFieldMap.get("securityPolicyId"), securityPolicyId+"", NumberOperators.EQUALS));

		Map<String, Object> map = selectBuilder.fetchFirst();
		return map;
	}
	@Override
	public Map<String, Object> getUserForEmail(String email, long orgId, String identifier) throws Exception {
		return getUserForEmail(email, orgId, identifier, false);
	}
	
	@Override
	public Map<String, Object> getUserForEmail(String email, long orgId, String identifier, boolean fetchInactive) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioField> fields = new ArrayList<FacilioField>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		fields.addAll(IAMAccountConstants.getAccountsOrgUserFields());
		
		GenericSelectRecordBuilder selectBuilder = getSelectBuilder(fields);

		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "orgUserDeletedTime", "-1", NumberOperators.EQUALS));

		if (!fetchInactive) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USER_STATUS", "status", "1", NumberOperators.EQUALS));
		}

		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.EMAIL", "email", email, StringOperators.IS));
		
		if(orgId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		}
		if(StringUtils.isNotEmpty(identifier)) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.IDENTIFIER", "identifier", identifier, StringOperators.IS));
		}
		List<Map<String, Object>> list = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	
	}
	
	@Override
	public Map<String, Object> getUserForPhone(String phone, long orgId, String identifier) throws Exception {
		// TODO Auto-generated method stub
		List<FacilioField> fields = new ArrayList<FacilioField>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		fields.addAll(IAMAccountConstants.getAccountsOrgUserFields());
		
		GenericSelectRecordBuilder selectBuilder = getSelectBuilder(fields);

		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "orgUserDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USER_STATUS", "status", "1", NumberOperators.EQUALS));

		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition("Account_Users.MOBILE", "mobile", phone, StringOperators.IS));
		criteria.addOrCondition(CriteriaAPI.getCondition("Account_Users.PHONE", "phone", phone, StringOperators.IS));
		
		selectBuilder.andCriteria(criteria);
		if(StringUtils.isNotEmpty(identifier)) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.IDENTIFIER", "identifier", identifier, StringOperators.IS));
		}
		if(orgId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		}
		List<Map<String, Object>> list = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	
	}

	@Override
	public IAMUser getFacilioUser(long orgId, long userId, boolean checkStatus) throws Exception {
		// TODO Auto-generated method stub
		
		List<FacilioField> fields = new ArrayList<FacilioField>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		fields.addAll(IAMAccountConstants.getAccountsOrgUserFields());
		
		GenericSelectRecordBuilder selectBuilder = getSelectBuilder(fields);

		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "orgUserDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.USERID", "userId", String.valueOf(userId), NumberOperators.EQUALS));
		
		if(orgId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		}
		
		if(checkStatus) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USER_STATUS", "status", "1", NumberOperators.EQUALS));
		}

		List<Map<String, Object>> mapList = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(mapList)) {
			IAMUser iamUser = createUserFromProps(mapList.get(0));
			return iamUser;
		}
		LOGGER.error("getFacilioUser is null");
		return null;
	
	}

	@Override
	public Map<String, Object> getUserForUsername(String username, long orgId, String identifier) throws Exception {
		// TODO Auto-generated method stub
		List<Map<String, Object>> list = getUserData(username, orgId, identifier);
		if(CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	
	}

	private boolean hasPassword(String userName) throws Exception {
		List<FacilioField> fields = new ArrayList<FacilioField>();
		FacilioField password = new FacilioField();
		password.setName("password");
		password.setDataType(FieldType.BOOLEAN);
		password.setColumnName("PASSWORD IS NOT NULL");
		password.setModule(IAMAccountConstants.getAccountsUserModule());
		fields.add(password);

		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Account_Users")
				.andCondition(CriteriaAPI.getCondition("Account_Users.USERNAME", "username", userName, StringOperators.IS));
		List<Map<String, Object>> props = selectBuilder.get();
		return (Boolean) props.get(0).get("password");
	}

	public List<Map<String, Object>> getUserData(String username, AppDomain.GroupType groupType) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		fields.addAll(IAMAccountConstants.getAccountsOrgUserFields());

		GenericSelectRecordBuilder selectBuilder = getSelectBuilder(fields);
		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "orgUserDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USER_STATUS", "userStatus", "1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.USERNAME", "username", username, StringOperators.IS));

		Criteria cr = new Criteria();
		cr.addAndCondition(CriteriaAPI.getCondition("Account_Users.IDENTIFIER", "identifier", ""+groupType.getIndex()+"\\_", StringOperators.STARTS_WITH));
		cr.addOrCondition(CriteriaAPI.getCondition("Account_Users.IDENTIFIER", "identifier", groupType.getIndex()+"", StringOperators.IS));
		selectBuilder.andCriteria(cr);
		return selectBuilder.get();
	}

	public List<Map<String, Object>> getUserData(String username, long orgId, String identifier) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(IAMAccountConstants.getAccountsUserFields());
		fields.addAll(IAMAccountConstants.getAccountsOrgUserFields());

		GenericSelectRecordBuilder selectBuilder = getSelectBuilder(fields);

		selectBuilder.andCondition(CriteriaAPI.getCondition("Organizations.DELETED_TIME", "orgDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.DELETED_TIME", "orgUserDeletedTime", "-1", NumberOperators.EQUALS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.USER_STATUS", "userStatus", "1", NumberOperators.EQUALS));

		if(StringUtils.isNotEmpty(identifier)) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.IDENTIFIER", "identifier", identifier, StringOperators.IS));
		}


		selectBuilder.andCondition(CriteriaAPI.getCondition("Account_Users.USERNAME", "username", username, StringOperators.IS));

		if(orgId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("Account_ORG_Users.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		}
		return selectBuilder.get();
	}

	private long addAppDomain(AppDomain appDomain) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.addAll(IAMAccountConstants.getAppDomainFields());
		
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("App_Domain")
				.andCondition(CriteriaAPI.getCondition("App_Domain.DOMAIN","domainName" , appDomain.getDomain(), StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition("App_Domain.APP_DOMAIN_TYPE","appDomainType" , String.valueOf(appDomain.getAppDomainType()), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("App_Domain.APP_GROUP_TYPE","groupType" , String.valueOf(appDomain.getGroupType()), NumberOperators.EQUALS));
		Organization org = IAMOrgUtil.getOrg(appDomain.getOrgId());
		if(org.getOrgType()==Organization.OrgType.SANDBOX.getIndex()){
			selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.ORGID","orgId" , String.valueOf(appDomain.getOrgId()), NumberOperators.EQUALS));
		}
		List<Map<String, Object>> props = selectBuilder.get();
		if(CollectionUtils.isNotEmpty(props)) {
			Map<String, Object> map = props.get(0);
			return (long)map.get("id");
		}
		else {
			return addNewAppDomain(appDomain);
		}
		
	}
	
	@Override
	public int deleteAppDomain(long appDomainId) throws Exception {
		return deleteAppDomains(Collections.singletonList(appDomainId));
	}
	
	private static long addNewAppDomain(AppDomain appDomain) throws Exception {

		if(StringUtils.isNotEmpty(appDomain.getDomain())) {
			
			List<FacilioField> fields = IAMAccountConstants.getAppDomainFields();
			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(IAMAccountConstants.getAppDomainModule().getTableName())
					.fields(fields);
	
			Map<String, Object> props = FieldUtil.getAsProperties(appDomain);
	
			insertBuilder.addRecord(props);
			insertBuilder.save();
			long appdomainId = (Long) props.get("id");
			return appdomainId;
		}
		
		return -1;
			
	}
	

	@Override
	public List<AppDomain> getAppDomain(AppDomainType type, long orgId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getAppDomainFields())
				.table("App_Domain");
				
		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.APP_DOMAIN_TYPE", "appDomainType", String.valueOf(type.getIndex()), EnumOperators.IS));
		if(type != AppDomainType.FACILIO) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		}
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			return FieldUtil.getAsBeanListFromMapList(props, AppDomain.class);
		}
		return null;

	}

	@Override
	public List<AppDomain> getAppDomainForType(Integer domainType, Long orgId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getAppDomainFields())
				.table("App_Domain");

		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.APP_DOMAIN_TYPE", "domainType", String.valueOf(domainType), NumberOperators.EQUALS));

		Criteria criteria = new Criteria();
		if(domainType == null) {
			return null;
		}
		if(orgId != null && orgId > 0){
			criteria.addAndCondition(CriteriaAPI.getCondition("App_Domain.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		}
		if(domainType == 1 || domainType == 6) {
			criteria.addOrCondition(CriteriaAPI.getCondition("App_Domain.ORGID", "orgId", "1", CommonOperators.IS_EMPTY));
		}

		selectBuilder.andCriteria(criteria);
		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			return FieldUtil.getAsBeanListFromMapList(props, AppDomain.class);
		}
		return null;

	}

	public String getPortalDomainUrlForUser(String username ,AppDomainType appDomainType) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getAccountsUserFields())
				.table(IAMAccountConstants.getAccountsUserModule().getTableName());

		selectBuilder.andCondition(CriteriaAPI.getCondition("USERNAME","username",username, StringOperators.IS));
		selectBuilder.andCondition(CriteriaAPI.getCondition("IDENTIFIER","identifier",String.valueOf(1),StringOperators.ISN_T));

		List<Map<String,Object>> props = selectBuilder.get();
		if(props!=null && !props.isEmpty()){
			for(Map<String,Object> prop : props){
				String identifier = (String) prop.get("identifier");
				List<AppDomain> appDomainList = getAppDomainForIdentifier(identifier);
				if (appDomainList != null) {
					for (AppDomain appDomain : appDomainList) {
						if (appDomain.getAppDomainTypeEnum() == appDomainType) {
							return appDomain.getDomain();
						}
					}
				}
			}
		}
		return null;
	}
	public List<AppDomain> getAppDomainForIdentifier(String identifier)throws Exception{

		int groupType = Integer.parseInt(identifier.split("_")[0]);
		long orgId = Long.parseLong(identifier.split("_")[1]);

		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getAppDomainFields())
				.table(IAMAccountConstants.getAppDomainModule().getTableName());

		selectRecordBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.ORGID","orgId",orgId +"", NumberOperators.EQUALS));
		selectRecordBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.APP_GROUP_TYPE", "groupType", groupType +"", NumberOperators.EQUALS));

		List<Map<String, Object>> result = selectRecordBuilder.get();
		if(result != null && !result.isEmpty()){
			List<AppDomain> appDomains = new ArrayList<>();
			for (Map<String, Object> res : result) {
				appDomains.add(FieldUtil.getAsBeanFromMap(res, AppDomain.class));
			}
			return appDomains;
		}
		return null;
	}


	@Override
	public List<AppDomain> getAppDomainsForOrg(long orgId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getAppDomainFields())
				.table("App_Domain");
				
		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			return FieldUtil.getAsBeanListFromMapList(props, AppDomain.class);
		}
		return null;

	}
	
	@Override
	public AppDomain getAppDomain(String domain) throws Exception {
		return getAppDomain(domain, -1);
	}

	@Override
	public AppDomain getAppDomain(String domain, long orgId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getAppDomainFields())
				.table("App_Domain");
		//handling done for domain aliases for our main app
		if(StringUtils.isNotEmpty(domain)) {
			String allowedAppdomains = FacilioProperties.getAllowedAppDomains();
			String[] appdomains = FacilioUtil.splitByComma(allowedAppdomains);
			if(appdomains.length > 0){
				List<String> allowedAppDomainList = Arrays.asList(appdomains);
				if(allowedAppDomainList.contains(domain)) {
					domain = AccountUtil.getDefaultAppDomain();
				}
			}

		}
		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.DOMAIN", "domain", domain, StringOperators.IS));
		if (orgId > 0) {
			selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.ORGID", "orgId", String.valueOf(orgId), NumberOperators.EQUALS));
		}

		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			return FieldUtil.getAsBeanFromMap(props.get(0), AppDomain.class);
		}
		LOGGER.info("App domain doesnt exists -->  " + domain);
    	return null;

	}

	@Override
	public AppDomain getAppDomain(long domainId) throws Exception {
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getAppDomainFields())
				.table("App_Domain");
				
		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.ID", "id", String.valueOf(domainId), NumberOperators.EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			return FieldUtil.getAsBeanFromMap(props.get(0), AppDomain.class);
		}
		return null;

	}

	@Override
	public List<AppDomain> getPortalAppDomains() throws Exception {
		// TODO Auto-generated method stub
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getAppDomainFields())
				.table("App_Domain");
				
		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.APP_DOMAIN_TYPE", "appDomainType", String.valueOf(AppDomainType.FACILIO.getIndex()), NumberOperators.NOT_EQUALS));
		
		List<Map<String, Object>> props = selectBuilder.get();
		if (CollectionUtils.isNotEmpty(props)) {
			return FieldUtil.getAsBeanListFromMapList(props, AppDomain.class);
		}
		return null;
	}

	@Override
	public void addAppDomains(List<AppDomain> appDomains) throws Exception {
		if(CollectionUtils.isNotEmpty(appDomains)) {
			for(AppDomain appDomain : appDomains) {
				addAppDomain(appDomain);
			}
		}
		
	}
	
	@Override
	public int deleteAppDomains(List<Long> appDomainIds) throws Exception {
		if(CollectionUtils.isNotEmpty(appDomainIds)) {
			GenericDeleteRecordBuilder builder = new GenericDeleteRecordBuilder()
					.table(IAMAccountConstants.getAppDomainModule().getTableName());
			
			builder.andCondition(CriteriaAPI.getIdCondition(appDomainIds, IAMAccountConstants.getAppDomainModule()));
			return builder.delete();
			
		}
		return -1;
	}

	@Override
	public Map<String, Object> generatePropsForWithoutPassword(String emailaddress, String userAgent, String userType,
												  String ipAddress, String appDomain, boolean isProxySession) throws Exception {
		// TODO Auto-generated method stub
		if(StringUtils.isEmpty(appDomain)) {
			appDomain = AccountUtil.getDefaultAppDomain();
		}
		AppDomain appDomainObj = getAppDomain(appDomain);
		if(appDomainObj == null) {
			throw new AccountException(ErrorCode.INVALID_APP_DOMAIN, "invalid App Domain");
		}
		String identifier = appDomainObj.getIdentifier();
		IAMUser user = getFacilioUserV3(emailaddress, identifier);

		if (user == null) {
			throw new AccountException(ErrorCode.USER_DEACTIVATED_FROM_THE_ORG, emailaddress+" is not exists or inactive.");
		}

		long uid = user.getUid();
		String jwt = createJWT("id", "auth0", String.valueOf(user.getUid()),
				System.currentTimeMillis() + 24 * 60 * 60000);

		long sessionId = startUserSessionv2(uid, jwt, ipAddress, userAgent, userType,
				Instant.ofEpochMilli(System.currentTimeMillis()).plus(1, ChronoUnit.HOURS).toEpochMilli(), isProxySession);

		Map<String, Object> props = new HashMap<>();
		props.put("uid", uid);
		props.put("sessionId", sessionId);
		props.put("token", jwt);
		return props;
	}

	@Override
	public String generateTokenForWithoutPassword(String emailaddress, String userAgent, String userType,
			String ipAddress, boolean startUserSession, String appDomain, IAMAccountConstants.SocialLogin socialLogin) throws Exception {
		// TODO Auto-generated method stub
		if(StringUtils.isEmpty(appDomain)) {
			appDomain = AccountUtil.getDefaultAppDomain();
		}
		AppDomain appDomainObj = getAppDomain(appDomain);
		if(appDomainObj == null) {
			throw new AccountException(ErrorCode.INVALID_APP_DOMAIN, "invalid App Domain");
		}
		String identifier = appDomainObj.getIdentifier();
		IAMUser user = getFacilioUserV3(emailaddress, identifier);

		if (user == null) {
			throw new AccountException(ErrorCode.USER_DEACTIVATED_FROM_THE_ORG, emailaddress+" is not exists or inactive.");
		}

		long uid = user.getUid();
		String jwt = createJWT("id", "auth0", String.valueOf(user.getUid()),
				System.currentTimeMillis() + 24 * 60 * 60000);
		if (startUserSession) {
			startUserSessionv2(uid, jwt, ipAddress, userAgent, userType);
		}

		if (socialLogin != null) {
			upsertAccountSocialLogin(uid, socialLogin);
		}

		return jwt;
	}

	@Override
	public void deleteDefaultAppDomains(long orgId) throws Exception {
		// TODO Auto-generated method stub
		List<AppDomain> orgDomains = getAppDomainsForOrg(orgId);
		List<Long> domainIds = new ArrayList<Long>();
		if(CollectionUtils.isNotEmpty(orgDomains)) {
			for(AppDomain domain : orgDomains) {
				domainIds.add(domain.getId());
			}
			deleteAppDomains(domainIds);
		}
	}

	private static void initialiseUserMfaSettings(long userId) throws Exception{

		if(userId <= 0) {
			throw new IllegalArgumentException("Invalid UserId");
		}
		else {

			GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
					.table(IAMAccountConstants.getUserMfaSettings().getTableName())
					.fields(IAMAccountConstants.getUserMfaSettingsFields());

			Map<String,Object> props = new HashMap<>();
			props.put("userId",userId);
			props.put("totpSecret",null);
			props.put("totpStatus",false);
			insertBuilder.addRecord(props);
			insertBuilder.save();
		}
	}

	public Map<String,Object> getUserMfaSettings(long userId) throws Exception{
		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getUserMfaSettingsFields())
				.table(IAMAccountConstants.getUserMfaSettings().getTableName())
		        .andCondition(CriteriaAPI.getCondition("UserMfaSettings.USERID", "userId",userId + "", NumberOperators.EQUALS));
		List<Map<String, Object>> props = selectBuilder.get();
		if(props != null && !props.isEmpty()) {
			Map<String,Object> x = props.get(0);
			Map<String,Object> result = new HashMap<>();
			result.put("userId", x.get("userId"));
			result.put("totpSecret", x.get("totpSecret"));
			result.put("totpStatus",x.get("totpStatus"));
			return result;
			
		}
		return null;
	}

	public void updateUserMfaSettingsStatus(long userId,boolean value) throws Exception{

		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getUserMfaSettings().getTableName())
				.fields(IAMAccountConstants.getUserMfaSettingsFields())
				.andCondition(CriteriaAPI.getCondition("UserMfaSettings.USERID", "userId",userId + "", NumberOperators.EQUALS));

		Map<String, Object> props = new HashMap<>();
		props.put("totpStatus",value);
		updateBuilder.update(props);

	}

	public boolean updateUserMfaSettingsSecretKey(long userId,String value) throws Exception {

		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getUserMfaSettings().getTableName())
				.fields(IAMAccountConstants.getUserMfaSettingsFields())
				.andCondition(CriteriaAPI.getCondition("UserMfaSettings.USERID", "userId",userId + "", NumberOperators.EQUALS));

		Map<String, Object> props = new HashMap<>();
		props.put("totpSecret",value);
		updateBuilder.update(props);

		return true;
	}

	public boolean clearUserMfaSettings(long userId) throws Exception {

		GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
				.table(IAMAccountConstants.getUserMfaSettings().getTableName())
				.fields(IAMAccountConstants.getUserMfaSettingsFields())
				.andCondition(CriteriaAPI.getCondition("UserMfaSettings.USERID", "userId",userId + "", NumberOperators.EQUALS));

		Map<String, Object> props = new HashMap<>();
		props.put("totpSecret","-99");
		props.put("totpStatus",false);
		updateBuilder.update(props);

		return true;
	}

	public String validateDigestAndDomain(String domain, String digest, AppDomain.GroupType groupType) throws Exception {
		String emailFromDigest = IAMUserUtil.getEmailFromDigest(digest);
		List<Map<String, Object>> userData = getUserData(emailFromDigest, groupType);
		if (CollectionUtils.isEmpty(userData)) {
			return null;
		}
		List<Long> orgIds = new ArrayList<>();
		userData.forEach(i -> orgIds.add((long) i.get("orgId")));
		for (long orgId: orgIds) {
			Organization org = IAMOrgUtil.getOrg(orgId);
			if (org.getDomain().equalsIgnoreCase(domain.trim())) {
				return org.getDomain();
			}
		}
		return null;
	}
	private void throwInvalidWebSessLifeTime(SecurityPolicy securityPolicy){
		if (securityPolicy.getWebSessLifeTimesec() != null){
			if(securityPolicy.getWebSessLifeTimesec() < 3600) {
				throw new IllegalArgumentException("Minimum Session Life Time should be 1 hour" );
			}
			if(securityPolicy.getWebSessLifeTimesec() > YEAR_IN_SECONDS){
				throw new IllegalArgumentException("Session Life Time should be less than 365 days");
			}}
		if(securityPolicy.getIdleSessionTimeOut() != null){
			if(securityPolicy.getIdleSessionTimeOut() < 300){
				throw new IllegalArgumentException("Minimum Session Idle Timeout Period should be 5 minutes");
			}
			if(securityPolicy.getIdleSessionTimeOut() > YEAR_IN_SECONDS){
				throw new IllegalArgumentException("Session Idle Timeout Period should be less than 365 days");
			}
		}
	}
	@Override
	public long createSecurityPolicy(SecurityPolicy securityPolicy) throws Exception {
		throwInvalidWebSessLifeTime(securityPolicy);
		GenericInsertRecordBuilder insertRecordBuilder = new GenericInsertRecordBuilder();
		List<FacilioField> securityPolicyFields = IAMAccountConstants.getSecurityPolicyFields();
		insertRecordBuilder
				.fields(securityPolicyFields)
				.table("SecurityPolicies");
		Map<String, Object> prop = FieldUtil.getAsProperties(securityPolicy);
		return insertRecordBuilder.insert(prop);
	}

	@Override
	public void updateSecurityPolicy(SecurityPolicy securityPolicy) throws Exception {
		throwInvalidWebSessLifeTime(securityPolicy);
		if(securityPolicy.getWebSessLifeTime() != null){
			securityPolicy.setWebSessLifeTime(null);
		}
		List<FacilioField> securityPolicyFields = IAMAccountConstants.getSecurityPolicyFields();
		GenericUpdateRecordBuilder updateRecordBuilder = new GenericUpdateRecordBuilder();
		updateRecordBuilder
				.fields(securityPolicyFields)
				.table("SecurityPolicies")
				.andCondition(CriteriaAPI.getCondition("SecurityPolicies.SECURITY_POLICY_ID", "securityPolicyId", securityPolicy.getId()+"", NumberOperators.EQUALS));


		updateRecordBuilder.update(securityPolicy.getAsMap());

		List<Long> userIds = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getAccountsUserFields())
				.table(IAMAccountConstants.getAccountsUserModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("SECURITY_POLICY_ID", "securityPolicyId", securityPolicy.getId() + "", NumberOperators.EQUALS))
				.get()
				.stream()
				.map(i -> (long) i.get("uid"))
				.collect(Collectors.toList());

		IAMUtil.dropUserSecurityPolicyCache(userIds);
	}

	@Override
	public SecurityPolicy fetchSecurityPolicy(long id, long orgId) throws Exception {
		Criteria cr = new Criteria();
		cr.addAndCondition(CriteriaAPI.getCondition("SecurityPolicies.SECURITY_POLICY_ID", "secPolId", id + "", NumberOperators.EQUALS));
		cr.addAndCondition(CriteriaAPI.getCondition("SecurityPolicies.ORGID", "orgId", orgId+"", NumberOperators.EQUALS));
		List<SecurityPolicy> securityPolicies = securityPolicyList(cr);
		return securityPolicies.get(0);
	}

	private List<SecurityPolicy> securityPolicyList(Criteria criteria) throws Exception {
		return new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getSecurityPolicyFields())
				.table("SecurityPolicies")
				.andCriteria(criteria)
				.get()
				.stream()
				.map(i -> FieldUtil.getAsBeanFromMap(i, SecurityPolicy.class))
				.collect(Collectors.toList());
	}

	@Override
	public List<SecurityPolicy> fetchAllSecurityPolicies(long orgId) throws Exception {
		Criteria cr = new Criteria();
		cr.addAndCondition(CriteriaAPI.getCondition("SecurityPolicies.ORGID", "orgId", orgId+"", NumberOperators.EQUALS));
		return securityPolicyList(cr);
	}

	public SecurityPolicy fetchDefaultSecurityPolicy(long orgId) throws Exception {
		Criteria cr = new Criteria();
		cr.addAndCondition(CriteriaAPI.getCondition("SecurityPolicies.ORGID", "orgId", orgId+"", NumberOperators.EQUALS));
		cr.addAndCondition(CriteriaAPI.getCondition("SecurityPolicies.IS_DEFAULT", "isDefault", "true", BooleanOperators.IS));

		List<SecurityPolicy> securityPolicies = securityPolicyList(cr);

		if (CollectionUtils.isEmpty(securityPolicies)) {
			return null;
		}

		return securityPolicies.get(0);
	}

	@Override
	public void deleteSecurityPolicy(long id, long orgId) throws Exception {
		List<Long> userIds = new GenericSelectRecordBuilder()
				.select(IAMAccountConstants.getAccountsUserFields())
				.table(IAMAccountConstants.getAccountsUserModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("SECURITY_POLICY_ID", "securityPolicyId", id + "", NumberOperators.EQUALS))
				.get()
				.stream()
				.map(i -> (long) i.get("uid"))
				.collect(Collectors.toList());

		new GenericDeleteRecordBuilder()
				.table(IAMAccountConstants.getSecurityPolicyModule().getTableName())
				.andCondition(CriteriaAPI.getCondition("SecurityPolicies.SECURITY_POLICY_ID", "secPolId", id + "", NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition("SecurityPolicies.ORGID", "orgId", orgId+"", NumberOperators.EQUALS))
				.delete();

		IAMUtil.dropUserSecurityPolicyCache(userIds);
	}

	public boolean isUserInProxyList(String username) throws Exception {
		if (FacilioProperties.isDevelopment()) {
			return true;
		}
		Boolean isPresent = LRUCache.getProxyUsersCache().get(username);
		if (isPresent != null && isPresent) {
			return true;
		}
		SecretsManagerBean secretsManagerBean = (SecretsManagerBean) BeanFactory.lookup("SecretsManagerBean");
		Map<String, String> secret = secretsManagerBean.getSecret("proxy-user-service-account");
		String key = secret.get("secret");
		NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

		GoogleCredential credential = GoogleCredential.fromStream(new ByteArrayInputStream(key.getBytes()))
				.createScoped(Collections.singleton(DirectoryScopes.ADMIN_DIRECTORY_GROUP_READONLY));
		credential = credential.toBuilder().setServiceAccountUser(FacilioProperties.getServiceaccountuser()).build();

		Directory directory = new Directory.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
				.setApplicationName("Facilio Proxy")
				.build();

		MembersHasMember execute = directory.members().hasMember(FacilioProperties.getProxygroup(), username).execute();
		if (execute.getIsMember() != null && execute.getIsMember()) {
			LRUCache.getProxyUsersCache().put(username, true);
			return true;
		}
		return false;
	}

	public static Criteria getOrgTypeCriteria(Organization.OrgType orgType) {
		Criteria orgTypeCriteria = new Criteria();
		orgTypeCriteria.addAndCondition(CriteriaAPI.getCondition("Organizations.ORG_TYPE", "orgType", String.valueOf(orgType.getIndex()), NumberOperators.EQUALS));
		if (orgType.equals(Organization.OrgType.PRODUCTION)) {
			orgTypeCriteria.addOrCondition(CriteriaAPI.getCondition("Organizations.ORG_TYPE", "orgType", "NULL", CommonOperators.IS_EMPTY));
		}

		return orgTypeCriteria;
	}

//	private Map<String, String> getDomainForIdentifiers(List<String> identifiers) throws Exception {
//		GenericSelectRecordBuilder selectBuilder = new GenericSelectRecordBuilder()
//				.select(IAMAccountConstants.getAppDomainFields())
//				.table("App_Domain");
//
//		// Indentifier is <groupType>_<orgId>
//		long groupType = -1;
//		long orgId = -1;
//
//		try {
//
//		}
//
//		selectBuilder.andCondition(CriteriaAPI.getCondition("App_Domain.ID", "id", String.valueOf(domainId), NumberOperators.EQUALS));
//
//		List<Map<String, Object>> props = selectBuilder.get();
//		if (CollectionUtils.isNotEmpty(props)) {
//			return FieldUtil.getAsBeanFromMap(props.get(0), AppDomain.class);
//		}
//	}
}
