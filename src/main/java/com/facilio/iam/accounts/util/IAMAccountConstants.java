package com.facilio.iam.accounts.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.fields.NumberField;
import com.facilio.modules.fields.SystemEnumField;

public class IAMAccountConstants {

		public enum SocialLogin {
			GOOGLE
		}
			
		
		
		public static enum SessionType {
			USER_LOGIN_SESSION(1),
			PERMALINK_SESSION(2),
			DIGEST_SESSION(3),
			TOTP_SESSION(4),
			MFA_CONFIG_SESSION(5),
			PWD_POLICY_PWD_RESET(6),
			PROXY_USER_TOKEN(7)
			;

		    private int sessionType;

		    SessionType(int sessionType) {
		        this.sessionType = sessionType;
		    }

		    public int getValue() {
		        return sessionType;
		    }
		    
		    public static SessionType valueOf(int sessionType) {
		    	return typeMap.get(sessionType);
		    }
		    
		    private static final Map<Integer, SessionType> typeMap = Collections.unmodifiableMap(initTypeMap());
			private static Map<Integer, SessionType> initTypeMap() {
				Map<Integer, SessionType> typeMap = new HashMap<>();
				
				for(SessionType type : values()) {
					typeMap.put(type.getValue(), type);
				}
				return typeMap;
			}
			public Map<Integer, SessionType> getAllTypes() {
				return typeMap;
			}
		}
		
		public static class DefaultSuperAdmin {
			public static final String SUPER_ADMIN 	= "Super Administrator";
			public static final String ADMINISTRATOR 	= "Administrator";
		}

		public static FacilioModule getSecurityPolicyModule() {
			FacilioModule securityPolicyModule = new FacilioModule();
			securityPolicyModule.setName("securitypolicy");
			securityPolicyModule.setDisplayName("Security Policy");
			securityPolicyModule.setTableName("SecurityPolicies");

			return securityPolicyModule;
		}

		public static FacilioModule getUserPrevPwdsModule() {
			FacilioModule userPrevPwds = new FacilioModule();
			userPrevPwds.setName("userPrevPwds");
			userPrevPwds.setDisplayName("User Prev Pwds");
			userPrevPwds.setTableName("UserPrevPwds");
			return userPrevPwds;
		}
		
		public static FacilioModule getAccountsUserModule() {
			FacilioModule userModule = new FacilioModule();
			userModule.setName("accountsuser");
			userModule.setDisplayName("Accounts Users");
			userModule.setTableName("Account_Users");

			return userModule;
		}

		public static FacilioModule getDevClientModule() {
			FacilioModule devClientModule = new FacilioModule();
			devClientModule.setName("devclient");
			devClientModule.setDisplayName("Dev Client");
			devClientModule.setTableName("Dev_Client");
			return devClientModule;
		}
		
		public static FacilioModule getAppDomainModule() {
			FacilioModule userModule = new FacilioModule();
			userModule.setName("appdomain");
			userModule.setDisplayName("App Domain");
			userModule.setTableName("App_Domain");

			return userModule;
		}
		
		
		public static FacilioModule getAccountOrgUserModule() {
			FacilioModule accountUserAppsModule = new FacilioModule();
			accountUserAppsModule.setName("accountOrgUser");
			accountUserAppsModule.setDisplayName("Account Org Users");
			accountUserAppsModule.setTableName("Account_ORG_Users");

			return accountUserAppsModule;
		}

		public static FacilioModule getOrgModule() {
			FacilioModule orgModule = new FacilioModule();
			orgModule.setName("org");
			orgModule.setDisplayName("Organizations");
			orgModule.setTableName("Organizations");

			return orgModule;
		}

		public static FacilioModule getUserSessionModule() {
			FacilioModule userSession = new FacilioModule();
			userSession.setName("usersession");
			userSession.setDisplayName("User Sessions");
			userSession.setTableName("UserSessions");

			return userSession;
		}

		public static FacilioModule getProxySessionsModule() {
			FacilioModule proxySessions = new FacilioModule();
			proxySessions.setName("proxysession");
			proxySessions.setDisplayName("Proxy Sessions");
			proxySessions.setTableName("ProxySessions");
			return proxySessions;
		}

		public static FacilioModule getDCLookupModule() {
			FacilioModule dcLookupModule = new FacilioModule();
			dcLookupModule.setName("dclookup");
			dcLookupModule.setTableName("DC Lookup");
			dcLookupModule.setTableName("DC_Lookup");
			return dcLookupModule;
		}

		public static List<FacilioField> getDCFields() {
			FacilioModule module = getDCLookupModule();
			List<FacilioField> fields = new ArrayList<>();

			FacilioField userName = new FacilioField();
			userName.setName("userName");
			userName.setDataType(FieldType.STRING);
			userName.setColumnName("USERNAME");
			userName.setModule(module);
			fields.add(userName);

			FacilioField dclookup = new FacilioField();
			dclookup.setName("dc");
			dclookup.setDataType(FieldType.NUMBER);
			dclookup.setColumnName("DC");
			dclookup.setModule(module);
			fields.add(dclookup);
			
			FacilioField appType = new FacilioField();
			appType.setName("groupType");
			appType.setDataType(FieldType.NUMBER);
			appType.setColumnName("APP_TYPE");
			appType.setModule(module);
			fields.add(appType);
			
			return fields;
		}

		
		public static List<FacilioField> getOrgFields() {
			FacilioModule module = getOrgModule();
			List<FacilioField> fields = new ArrayList<>();

			FacilioField orgId = new FacilioField();
			orgId.setName("orgId");
			orgId.setDataType(FieldType.ID);
			orgId.setColumnName("ORGID");
			orgId.setModule(module);
			fields.add(orgId);

			FacilioField name = new FacilioField();
			name.setName("name");
			name.setDataType(FieldType.STRING);
			name.setColumnName("ORGNAME");
			name.setModule(module);
			fields.add(name);

			FacilioField domain = new FacilioField();
			domain.setName("domain");
			domain.setDataType(FieldType.STRING);
			domain.setColumnName("FACILIODOMAINNAME");
			domain.setModule(module);
			fields.add(domain);

//			FacilioField logoId = new FacilioField();
//			logoId.setName("orgPhoto");
//			logoId.setDataType(FieldType.FILE);
//			logoId.setColumnName("LOGO_ID");
//			logoId.setModule(module);
//			fields.add(logoId);
			
			FacilioField logoId = new FacilioField();
			logoId.setName("logoId");
			logoId.setDataType(FieldType.NUMBER);
			logoId.setColumnName("LOGO_ID");
			logoId.setModule(module);
			fields.add(logoId);

			FacilioField phone = new FacilioField();
			phone.setName("phone");
			phone.setDataType(FieldType.STRING);
			phone.setColumnName("PHONE");
			phone.setModule(module);
			fields.add(phone);

			FacilioField mobile = new FacilioField();
			mobile.setName("mobile");
			mobile.setDataType(FieldType.STRING);
			mobile.setColumnName("MOBILE");
			mobile.setModule(module);
			fields.add(mobile);

			FacilioField fax = new FacilioField();
			fax.setName("fax");
			fax.setDataType(FieldType.STRING);
			fax.setColumnName("FAX");
			fax.setModule(module);
			fields.add(fax);

			FacilioField street = new FacilioField();
			street.setName("street");
			street.setDataType(FieldType.STRING);
			street.setColumnName("STREET");
			street.setModule(module);
			fields.add(street);

			FacilioField city = new FacilioField();
			city.setName("city");
			city.setDataType(FieldType.STRING);
			city.setColumnName("CITY");
			city.setModule(module);
			fields.add(city);

			FacilioField state = new FacilioField();
			state.setName("state");
			state.setDataType(FieldType.STRING);
			state.setColumnName("STATE");
			state.setModule(module);
			fields.add(state);

			FacilioField zip = new FacilioField();
			zip.setName("zip");
			zip.setDataType(FieldType.STRING);
			zip.setColumnName("ZIP");
			zip.setModule(module);
			fields.add(zip);

			FacilioField country = new FacilioField();
			country.setName("country");
			country.setDataType(FieldType.STRING);
			country.setColumnName("COUNTRY");
			country.setModule(module);
			fields.add(country);

			FacilioField timezone = new FacilioField();
			timezone.setName("timezone");
			timezone.setDataType(FieldType.STRING);
			timezone.setColumnName("TIMEZONE");
			timezone.setModule(module);
			fields.add(timezone);
			
			FacilioField currency = new FacilioField();
			currency.setName("currency");
			currency.setDataType(FieldType.STRING);
			currency.setColumnName("CURRENCY");
			currency.setModule(module);
			fields.add(currency);

			FacilioField createdTime = new FacilioField();
			createdTime.setName("createdTime");
			createdTime.setDataType(FieldType.NUMBER);
			createdTime.setColumnName("CREATED_TIME");
			createdTime.setModule(module);
			fields.add(createdTime);

			FacilioField loggerLevel = new FacilioField();
			loggerLevel.setName("loggerLevel");
			loggerLevel.setDataType(FieldType.NUMBER);
			loggerLevel.setColumnName("LOGGER_LEVEL");
			loggerLevel.setModule(module);
			fields.add(loggerLevel);
			
			FacilioField dataSourceName = new FacilioField();
			dataSourceName.setName("dataSource");
			dataSourceName.setDataType(FieldType.STRING);
			dataSourceName.setColumnName("DATASOURCE");
			dataSourceName.setModule(module);
			fields.add(dataSourceName);

			
			FacilioField dbName = new FacilioField();
			dbName.setName("dbName");
			dbName.setDataType(FieldType.STRING);
			dbName.setColumnName("DATABASE_NAME");
			dbName.setModule(module);
			fields.add(dbName);
			
			FacilioField dateFormat = new FacilioField();
			dateFormat.setName("dateFormat");
			dateFormat.setDataType(FieldType.STRING);
			dateFormat.setColumnName("DATE_FORMAT");
			dateFormat.setModule(module);
			fields.add(dateFormat);
			
			SystemEnumField timeFormat = new SystemEnumField();
			timeFormat.setName("timeFormat");
			timeFormat.setDataType(FieldType.SYSTEM_ENUM);
			timeFormat.setColumnName("TIME_FORMAT");
			timeFormat.setModule(module);
			timeFormat.setEnumName("TimeFormat");
			fields.add(timeFormat);

			FacilioField businessHourField = new NumberField();
			businessHourField.setName("businessHour");
			businessHourField.setDataType(FieldType.NUMBER);
			businessHourField.setColumnName("BUSINESS_HOUR");
			businessHourField.setModule(module);
			fields.add(businessHourField);

			FacilioField language = new FacilioField();
			language.setName("language");
			language.setDataType(FieldType.STRING);
			language.setColumnName("LANGUAGE");
			language.setModule(module);
			fields.add(language);

			FacilioField groupName = new FacilioField();
			groupName.setName("groupName");
			groupName.setDataType(FieldType.STRING);
			groupName.setColumnName("GROUP_NAME");
			groupName.setModule(module);
			fields.add(groupName);

			FacilioField allowUserTimeZone = new FacilioField();
			allowUserTimeZone.setName("allowUserTimeZone");
			allowUserTimeZone.setDataType(FieldType.BOOLEAN);
			allowUserTimeZone.setColumnName("ALLOW_USER_TIMEZONE");
			allowUserTimeZone.setModule(module);
			fields.add(allowUserTimeZone);

			return fields;
		}

		public static FacilioField getUserIdField(FacilioModule module) {
			FacilioField uid = new FacilioField();
			uid.setName("uid");
			uid.setDataType(FieldType.NUMBER);
			uid.setColumnName("USERID");
			uid.setModule(module);
			return uid;
		}

		public static FacilioField getUserPasswordField() {
			FacilioField password = new FacilioField();
			password.setName("password");
			password.setDataType(FieldType.STRING);
			password.setColumnName("PASSWORD");
			password.setModule(getAccountsUserModule());
			return password;
		}

		public static List<FacilioField> getUserPrevPwdsFields() {
			FacilioModule module = getUserPrevPwdsModule();
			List<FacilioField> fields = new ArrayList<>();

			FacilioField userId = new FacilioField();
			userId.setName("userId");
			userId.setDataType(FieldType.NUMBER);
			userId.setColumnName("USERID");
			userId.setModule(module);
			fields.add(userId);

			FacilioField password = new FacilioField();
			password.setName("password");
			password.setDataType(FieldType.STRING);
			password.setColumnName("PASSWORD");
			password.setModule(module);
			fields.add(password);

			FacilioField changedTime = new FacilioField();
			changedTime.setName("changedTime");
			changedTime.setDataType(FieldType.NUMBER);
			changedTime.setColumnName("CHANGED_TIME");
			changedTime.setModule(module);
			fields.add(changedTime);

			return fields;
		}

		public static List<FacilioField> getSecurityPolicyFields() {
			FacilioModule module = getSecurityPolicyModule();
			List<FacilioField> fields = new ArrayList<>();

			FacilioField id = new FacilioField();
			id.setName("id");
			id.setDataType(FieldType.ID);
			id.setColumnName("SECURITY_POLICY_ID");
			id.setModule(module);
			fields.add(id);

			FacilioField orgId = new FacilioField();
			orgId.setName("orgId");
			orgId.setDataType(FieldType.NUMBER);
			orgId.setColumnName("ORGID");
			orgId.setModule(module);
			fields.add(orgId);

			FacilioField description = new FacilioField();
			description.setName("description");
			description.setDataType(FieldType.STRING);
			description.setColumnName("DESCRIPTION");
			description.setModule(module);
			fields.add(description);

			FacilioField name = new FacilioField();
			name.setName("name");
			name.setDataType(FieldType.STRING);
			name.setColumnName("NAME");
			name.setModule(module);
			fields.add(name);

			FacilioField isDefault = new FacilioField();
			isDefault.setName("isDefault");
			isDefault.setDataType(FieldType.BOOLEAN);
			isDefault.setColumnName("IS_DEFAULT");
			isDefault.setModule(module);
			fields.add(isDefault);

			FacilioField isTotpEnabled = new FacilioField();
			isTotpEnabled.setName("isTOTPEnabled");
			isTotpEnabled.setDataType(FieldType.BOOLEAN);
			isTotpEnabled.setColumnName("IS_TOTP_ENABLED");
			isTotpEnabled.setModule(module);
			fields.add(isTotpEnabled);

			FacilioField isMotpEnabled = new FacilioField();
			isMotpEnabled.setName("isMOTPEnabled");
			isMotpEnabled.setDataType(FieldType.BOOLEAN);
			isMotpEnabled.setColumnName("IS_MOTP_ENABLED");
			isMotpEnabled.setModule(module);
			fields.add(isMotpEnabled);

			FacilioField isMFAEnabled = new FacilioField();
			isMFAEnabled.setName("isMFAEnabled");
			isMFAEnabled.setDataType(FieldType.BOOLEAN);
			isMFAEnabled.setColumnName("IS_MFA_ENABLED");
			isMFAEnabled.setModule(module);
			fields.add(isMFAEnabled);

			FacilioField isPwdPolicyEnabled = new FacilioField();
			isPwdPolicyEnabled.setName("isPwdPolicyEnabled");
			isPwdPolicyEnabled.setDataType(FieldType.BOOLEAN);
			isPwdPolicyEnabled.setColumnName("IS_PWD_POLICY_ENABLED");
			isPwdPolicyEnabled.setModule(module);
			fields.add(isPwdPolicyEnabled);

			FacilioField pwdMinLength = new FacilioField();
			pwdMinLength.setName("pwdMinLength");
			pwdMinLength.setDataType(FieldType.NUMBER);
			pwdMinLength.setColumnName("PWD_MIN_LENGTH");
			pwdMinLength.setModule(module);
			fields.add(pwdMinLength);

			FacilioField pwdIsMixed = new FacilioField();
			pwdIsMixed.setName("pwdIsMixed");
			pwdIsMixed.setDataType(FieldType.BOOLEAN);
			pwdIsMixed.setColumnName("PWD_IS_MIXED");
			pwdIsMixed.setModule(module);
			fields.add(pwdIsMixed);

			FacilioField pwdMinSplChars = new FacilioField();
			pwdMinSplChars.setName("pwdMinSplChars");
			pwdMinSplChars.setDataType(FieldType.NUMBER);
			pwdMinSplChars.setColumnName("PWD_MIN_SPL_CHARS");
			pwdMinSplChars.setModule(module);
			fields.add(pwdMinSplChars);

			FacilioField pwdMinNumDigits = new FacilioField();
			pwdMinNumDigits.setName("pwdMinNumDigits");
			pwdMinNumDigits.setDataType(FieldType.NUMBER);
			pwdMinNumDigits.setColumnName("PWD_MIN_NUM_DIGITS");
			pwdMinNumDigits.setModule(module);
			fields.add(pwdMinNumDigits);

			FacilioField pwdMinAge = new FacilioField();
			pwdMinAge.setName("pwdMinAge");
			pwdMinAge.setDataType(FieldType.NUMBER);
			pwdMinAge.setColumnName("PWD_MIN_AGE");
			pwdMinAge.setModule(module);
			fields.add(pwdMinAge);

			FacilioField pwdPrevPassRefusal = new FacilioField();
			pwdPrevPassRefusal.setName("pwdPrevPassRefusal");
			pwdPrevPassRefusal.setDataType(FieldType.NUMBER);
			pwdPrevPassRefusal.setColumnName("PWD_PREV_USE_REFUSAL");
			pwdPrevPassRefusal.setModule(module);
			fields.add(pwdPrevPassRefusal);

			FacilioField isWebSessManagementEnabled = new FacilioField();
			isWebSessManagementEnabled.setName("isWebSessManagementEnabled");
			isWebSessManagementEnabled.setDataType(FieldType.BOOLEAN);
			isWebSessManagementEnabled.setColumnName("IS_WEB_SESS_MANAGEMENT_ENABLED");
			isWebSessManagementEnabled.setModule(module);
			fields.add(isWebSessManagementEnabled);

			FacilioField webSessLifeTime = new FacilioField();
			webSessLifeTime.setName("webSessLifeTime");
			webSessLifeTime.setDataType(FieldType.NUMBER);
			webSessLifeTime.setColumnName("WEB_SESSION_LIFE_TIME");
			webSessLifeTime.setModule(module);
			fields.add(webSessLifeTime);

			FacilioField idleSessionTimeOut = new FacilioField();
			idleSessionTimeOut.setName("idleSessionTimeOut");
			idleSessionTimeOut.setDataType(FieldType.NUMBER);
			idleSessionTimeOut.setColumnName("IDLE_SESSION_TIME_OUT");
			idleSessionTimeOut.setModule(module);
			fields.add(idleSessionTimeOut);

			FacilioField webSessLifeTimesec = new FacilioField();
			webSessLifeTimesec.setName("webSessLifeTimesec");
			webSessLifeTimesec.setDataType(FieldType.NUMBER);
			webSessLifeTimesec.setColumnName("WEB_SESSION_LIFE_TIME_SEC");
			webSessLifeTimesec.setModule(module);
			fields.add(webSessLifeTimesec);

			return fields;
		}

		public static List<FacilioField> getDevClientFields() {
			FacilioModule module = getDevClientModule();

			List<FacilioField> fields = new ArrayList<>();

			FacilioField id = new FacilioField();
			id.setName("id");
			id.setDataType(FieldType.ID);
			id.setColumnName("ID");
			id.setModule(module);
			fields.add(id);

			FacilioField uid = new FacilioField();
			uid.setName("uid");
			uid.setDataType(FieldType.NUMBER);
			uid.setColumnName("USERID");
			uid.setModule(module);
			fields.add(uid);

			FacilioField orgId = new FacilioField();
			orgId.setName("orgId");
			orgId.setDataType(FieldType.NUMBER);
			orgId.setColumnName("ORGID");
			orgId.setModule(module);
			fields.add(orgId);

			FacilioField authType = new FacilioField();
			authType.setName("authType");
			authType.setDataType(FieldType.NUMBER);
			authType.setColumnName("AUTH_TYPE");
			authType.setModule(module);
			fields.add(authType);

			FacilioField name = new FacilioField();
			name.setName("name");
			name.setDataType(FieldType.STRING);
			name.setColumnName("NAME");
			name.setModule(module);
			fields.add(name);

			FacilioField oauth2ClientId = new FacilioField();
			oauth2ClientId.setName("oauth2ClientId");
			oauth2ClientId.setDataType(FieldType.STRING);
			oauth2ClientId.setColumnName("OAUTH2_CLIENT_ID");
			oauth2ClientId.setModule(module);
			fields.add(oauth2ClientId);

			fields.add(FieldFactory.getField("apiKey", "API_KEY", module, FieldType.STRING));

			fields.add(FieldFactory.getField("createdTime", "CREATED_TIME", module, FieldType.DATE_TIME));


			return fields;
		}
		
		

		public static List<FacilioField> getAccountsUserFields() {
			FacilioModule module = getAccountsUserModule();
			List<FacilioField> fields = new ArrayList<>();

			FacilioField uid = new FacilioField();
			uid.setName("uid");
			uid.setDataType(FieldType.ID);
			uid.setColumnName("USERID");
			uid.setModule(module);
			fields.add(uid);

			FacilioField name = new FacilioField();
			name.setName("name");
			name.setDisplayName("Name");
			name.setDataType(FieldType.STRING);
			name.setColumnName("NAME");
			name.setModule(module);
			fields.add(name);
			
			FacilioField userName = new FacilioField();
			userName.setName("userName");
			userName.setDataType(FieldType.STRING);
			userName.setColumnName("USERNAME");
			userName.setModule(module);
			fields.add(userName);

			FacilioField userVerified = new FacilioField();
			userVerified.setName("userVerified");
			userVerified.setDataType(FieldType.BOOLEAN);
			userVerified.setColumnName("USER_VERIFIED");
			userVerified.setModule(module);
			fields.add(userVerified);

			FacilioField email = new FacilioField();
			email.setName("email");
			email.setDisplayName("Email");
			email.setDataType(FieldType.STRING);
			email.setColumnName("EMAIL");
			email.setModule(module);
			fields.add(email);

			FacilioField photoId = new FacilioField();
			photoId.setName("photoId");
			photoId.setDataType(FieldType.NUMBER);
			photoId.setColumnName("PHOTO_ID");
			photoId.setModule(module);
			fields.add(photoId);

			FacilioField timezone = new FacilioField();
			timezone.setName("timezone");
			timezone.setDataType(FieldType.STRING);
			timezone.setColumnName("TIMEZONE");
			timezone.setModule(module);
			fields.add(timezone);

			FacilioField language = new FacilioField();
			language.setName("language");
			language.setDataType(FieldType.STRING);
			language.setColumnName("LANGUAGE");
			language.setModule(module);
			fields.add(language);

			FacilioField phone = new FacilioField();
			phone.setName("phone");
			phone.setDisplayName("Phone");
			phone.setDataType(FieldType.STRING);
			phone.setColumnName("PHONE");
			phone.setModule(module);
			fields.add(phone);

			FacilioField mobile = new FacilioField();
			mobile.setName("mobile");
			mobile.setDisplayName("Mobile");
			mobile.setDataType(FieldType.STRING);
			mobile.setColumnName("MOBILE");
			mobile.setModule(module);
			fields.add(mobile);		

			FacilioField street = new FacilioField();
			street.setName("street");
			street.setDataType(FieldType.STRING);
			street.setColumnName("STREET");
			street.setModule(module);
			fields.add(street);

			FacilioField city = new FacilioField();
			city.setName("city");
			city.setDataType(FieldType.STRING);
			city.setColumnName("CITY");
			city.setModule(module);
			fields.add(city);

			FacilioField state = new FacilioField();
			state.setName("state");
			state.setDataType(FieldType.STRING);
			state.setColumnName("STATE");
			state.setModule(module);
			fields.add(state);

			FacilioField zip = new FacilioField();
			zip.setName("zip");
			zip.setDataType(FieldType.STRING);
			zip.setColumnName("ZIP");
			zip.setModule(module);
			fields.add(zip);

			FacilioField country = new FacilioField();
			country.setName("country");
			country.setDataType(FieldType.STRING);
			country.setColumnName("COUNTRY");
			country.setModule(module);
			fields.add(country);

			FacilioField identifier = new FacilioField();
			identifier.setName("identifier");
			identifier.setDataType(FieldType.STRING);
			identifier.setColumnName("IDENTIFIER");
			identifier.setModule(module);
			fields.add(identifier);

			FacilioField securityPolicyId = new FacilioField();
			securityPolicyId.setName("securityPolicyId");
			securityPolicyId.setDataType(FieldType.NUMBER);
			securityPolicyId.setColumnName("SECURITY_POLICY_ID");
			securityPolicyId.setModule(module);
			fields.add(securityPolicyId);

			FacilioField pwdLastUpdatedTime = new FacilioField();
			pwdLastUpdatedTime.setName("pwdLastUpdatedTime");
			pwdLastUpdatedTime.setDataType(FieldType.NUMBER);
			pwdLastUpdatedTime.setColumnName("PWD_LAST_UPDATED_TIME");
			pwdLastUpdatedTime.setModule(module);
			fields.add(pwdLastUpdatedTime);

			return fields;
		}
		
		public static List<FacilioField> getAppDomainFields() {
			FacilioModule module = getAppDomainModule();
			List<FacilioField> fields = new ArrayList<>();

			FacilioField id = new FacilioField();
			id.setName("id");
			id.setDataType(FieldType.ID);
			id.setColumnName("ID");
			id.setModule(module);
			fields.add(id);

			FacilioField domain = new FacilioField();
			domain.setName("domain");
			domain.setDataType(FieldType.STRING);
			domain.setColumnName("DOMAIN");
			domain.setModule(module);
			fields.add(domain);
			
			FacilioField appDomainType = new FacilioField();
			appDomainType.setName("appDomainType"); 
			appDomainType.setDataType(FieldType.SYSTEM_ENUM);
			appDomainType.setColumnName("APP_DOMAIN_TYPE");
			appDomainType.setModule(module);
			fields.add(appDomainType);
			
			FacilioField groupType = new FacilioField();
			groupType.setName("groupType");
			groupType.setDataType(FieldType.NUMBER);
			groupType.setColumnName("APP_GROUP_TYPE");
			groupType.setModule(module);
			fields.add(groupType);
			
			FacilioField orgId = new FacilioField();
			orgId.setName("orgId");
			orgId.setDataType(FieldType.NUMBER);
			orgId.setColumnName("ORGID");
			orgId.setModule(module);
			fields.add(orgId);

			FacilioField domainType = new FacilioField();
			domainType.setName("domainType");
			domainType.setDataType(FieldType.SYSTEM_ENUM);
			domainType.setColumnName("DOMAIN_TYPE");
			domainType.setModule(module);
			fields.add(domainType);

			FacilioField securityPolicyId = new FacilioField();
			securityPolicyId.setName("securityPolicyId");
			securityPolicyId.setDataType(FieldType.NUMBER);
			securityPolicyId.setColumnName("SECURITY_POLICY_ID");
			securityPolicyId.setModule(module);
			fields.add(securityPolicyId);

			return fields;
		}
		
		public static List<FacilioField> getAccountsOrgUserFields() {
			FacilioModule module = getAccountOrgUserModule();
			List<FacilioField> fields = new ArrayList<>();

			FacilioField iamOrgUserid = new FacilioField();
			iamOrgUserid.setName("iamOrgUserId");
			iamOrgUserid.setDataType(FieldType.NUMBER);
			iamOrgUserid.setColumnName("ORG_USERID");
			iamOrgUserid.setModule(module);
			fields.add(iamOrgUserid);

			fields.add(getUserIdField(module));

			FacilioField orgId = new FacilioField();
			orgId.setName("orgId");
			orgId.setDataType(FieldType.NUMBER);
			orgId.setColumnName("ORGID");
			orgId.setModule(module);
			fields.add(orgId);
			
			FacilioField isDefaultOrg = new FacilioField();
			isDefaultOrg.setName("isDefaultOrg");
			isDefaultOrg.setDataType(FieldType.BOOLEAN);
			isDefaultOrg.setColumnName("ISDEFAULT");
			isDefaultOrg.setModule(module);
			fields.add(isDefaultOrg);
			
			FacilioField userStatus = new FacilioField();
			userStatus.setName("userStatus");
			userStatus.setDataType(FieldType.BOOLEAN);
			userStatus.setColumnName("USER_STATUS");
			userStatus.setModule(module);
			fields.add(userStatus);


			return fields;
		}
		
		
		public static FacilioModule getAccountSocialLoginModule() {
			FacilioModule accountsociallogin = new FacilioModule();
			accountsociallogin.setName("accountsociallogin");
			accountsociallogin.setDisplayName("Account Social Login");
			accountsociallogin.setTableName("Account_Social_Login");

			return accountsociallogin;
		}

		public static List<FacilioField> getAccountSocialLoginFields() {
			FacilioModule module = getAccountSocialLoginModule();
			List<FacilioField> fields = new ArrayList<>();

			FacilioField id = new FacilioField();
			id.setName("id");
			id.setDataType(FieldType.ID);
			id.setColumnName("ID");
			id.setModule(module);
			fields.add(id);

			fields.add(getUserIdField(module));

			FacilioField google = new FacilioField();
			google.setName("isGoogle");
			google.setDataType(FieldType.BOOLEAN);
			google.setColumnName("IS_GOOGLE");
			google.setModule(module);
			fields.add(google);

			return fields;
		}

		public static List<FacilioField> getProxySessionsFields() {
			FacilioModule module = getProxySessionsModule();
			List<FacilioField> fields = new ArrayList<>();

			FacilioField id = new FacilioField();
			id.setName("id");
			id.setDataType(FieldType.ID);
			id.setColumnName("ID");
			id.setModule(module);
			fields.add(id);

			FacilioField email = new FacilioField();
			email.setName("email");
			email.setDisplayName("Email");
			email.setDataType(FieldType.STRING);
			email.setColumnName("EMAIL");
			email.setModule(module);
			fields.add(email);

			fields.add(FieldFactory.getField("createdTime", "CREATED_TIME", module, FieldType.DATE_TIME));

			FacilioField token = new FacilioField();
			token.setName("token");
			token.setDataType(FieldType.STRING);
			token.setColumnName("TOKEN");
			token.setModule(module);
			fields.add(token);

			FacilioField psessionId = new FacilioField();
			psessionId.setName("proxiedSessionId");
			psessionId.setDataType(FieldType.NUMBER);
			psessionId.setColumnName("PROXIED_SESSIONID");
			psessionId.setModule(module);
			fields.add(psessionId);

			FacilioField isActive = new FacilioField();
			isActive.setName("isActive");
			isActive.setDataType(FieldType.BOOLEAN);
			isActive.setColumnName("IS_ACTIVE");
			isActive.setModule(module);
			fields.add(isActive);

			return fields;
		}

		
		public static List<FacilioField> getUserSessionFields() {
			FacilioModule module = getUserSessionModule();
			List<FacilioField> fields = new ArrayList<>();

			FacilioField id = new FacilioField();
			id.setName("id");
			id.setDataType(FieldType.ID);
			id.setColumnName("SESSIONID");
			id.setModule(module);
			fields.add(id);
			
			fields.add(getUserIdField(module));
			
			FacilioField sessionType = new FacilioField();
			sessionType.setName("sessionType");
			sessionType.setDataType(FieldType.NUMBER);
			sessionType.setColumnName("SESSION_TYPE");
			sessionType.setModule(module);
			fields.add(sessionType);
			
			FacilioField token = new FacilioField();
			token.setName("token");
			token.setDataType(FieldType.STRING);
			token.setColumnName("TOKEN");
			token.setModule(module);
			fields.add(token);

			FacilioField startTime = new FacilioField();
			startTime.setName("startTime");
			startTime.setDataType(FieldType.NUMBER);
			startTime.setColumnName("START_TIME");
			startTime.setModule(module);
			fields.add(startTime);
			
			FacilioField endTime = new FacilioField();
			endTime.setName("endTime");
			endTime.setDataType(FieldType.NUMBER);
			endTime.setColumnName("END_TIME");
			endTime.setModule(module);
			fields.add(endTime);
			
			FacilioField isActive = new FacilioField();
			isActive.setName("isActive");
			isActive.setDataType(FieldType.BOOLEAN);
			isActive.setColumnName("IS_ACTIVE");
			isActive.setModule(module);
			fields.add(isActive);

			FacilioField ipAddress = new FacilioField();
			ipAddress.setName("ipAddress");
			ipAddress.setDataType(FieldType.STRING);
			ipAddress.setColumnName("IPADDRESS");
			ipAddress.setModule(module);
			fields.add(ipAddress);
			
			FacilioField userAgent = new FacilioField();
			userAgent.setName("userAgent");
			userAgent.setDataType(FieldType.STRING);
			userAgent.setColumnName("USER_AGENT");
			userAgent.setModule(module);
			fields.add(userAgent);
			
			FacilioField sessionInfo = new FacilioField();
			sessionInfo.setName("sessionInfo");
			sessionInfo.setDataType(FieldType.STRING);
			sessionInfo.setColumnName("SESSION_INFO");
			sessionInfo.setModule(module);
			fields.add(sessionInfo);
			
			FacilioField userType = new FacilioField();
			userType.setName("userType");
			userType.setDataType(FieldType.STRING);
			userType.setColumnName("USER_TYPE");
			userType.setModule(module);
			fields.add(userType);

			FacilioField isProxySession = new FacilioField();
			isProxySession.setName("isProxySession");
			isProxySession.setDataType(FieldType.BOOLEAN);
			isProxySession.setColumnName("IS_PROXY_SESSION");
			isProxySession.setModule(module);
			fields.add(isProxySession);

			FacilioField lastActivityTime = new FacilioField();
			lastActivityTime.setName("lastActivityTime");
			lastActivityTime.setDataType(FieldType.NUMBER);
			lastActivityTime.setColumnName("LAST_ACTIVITY_TIME");
			lastActivityTime.setModule(module);
			fields.add(lastActivityTime);

			return fields;
		}
		
//		public static FacilioField getOrgUserDeletedTimeField() {
//			FacilioField deletedTime = new FacilioField();
//			deletedTime.setName("deletedTime");
//			deletedTime.setDataType(FieldType.NUMBER);
//			deletedTime.setColumnName("DELETED_TIME");
//			deletedTime.setModule(getAccountsOrgUserModule());
//			return deletedTime;
//		}

		public static FacilioField getOrgDeletedTimeField() {
			FacilioField deletedTime = new FacilioField();
			deletedTime.setName("deletedTime");
			deletedTime.setDataType(FieldType.NUMBER);
			deletedTime.setColumnName("DELETED_TIME");
			deletedTime.setModule(getOrgModule());
			return deletedTime;
		}
		
		public static FacilioField getOrgIdField() {
			return getOrgIdField(null);
		}

		public static FacilioField getOrgIdField(FacilioModule module) {
			FacilioField field = new FacilioField();
			field.setName("orgId");
			field.setDisplayName("Org Id");
			field.setDataType(FieldType.NUMBER);
			field.setColumnName("ORGID");
			if (module != null) {
				field.setModule(module);
			}
			return field;
		}
		public static List<FacilioField> getOrgMfaFields(){
			FacilioModule module = getMfaSettings();
			List<FacilioField> fields = new ArrayList<>();

			FacilioField orgId = new FacilioField();
			orgId.setName("orgId");
			orgId.setDataType(FieldType.NUMBER);
			orgId.setColumnName("ORGID");
			orgId.setModule(module);
			fields.add(orgId);
			
			FacilioField orgMfaSettingsId = new FacilioField();
			orgMfaSettingsId.setName("orgMfaSettingsId");
			orgMfaSettingsId.setDataType(FieldType.ID);
			orgMfaSettingsId.setColumnName("ORG_MFASETTINGS_ID");
			orgMfaSettingsId.setModule(module);
			fields.add(orgMfaSettingsId);
			
			FacilioField totpEnabled = new FacilioField();
			totpEnabled.setName("totpEnabled");
			totpEnabled.setDataType(FieldType.BOOLEAN);
			totpEnabled.setColumnName("IS_TOTP_ENABLED");
			totpEnabled.setModule(module);
			fields.add(totpEnabled);
			
			FacilioField motpEnabled = new FacilioField();
			motpEnabled.setName("motpEnabled");
			motpEnabled.setDataType(FieldType.BOOLEAN);
			motpEnabled.setColumnName("IS_MOTP_ENABLED");
			motpEnabled.setModule(module);
			fields.add(motpEnabled);

			FacilioField groupType = new FacilioField();
			groupType.setName("groupType");
			groupType.setDataType(FieldType.NUMBER);
			groupType.setColumnName("APP_GROUP_TYPE");
			groupType.setModule(module);
			fields.add(groupType);
			
			return fields;
			
		}
		public static FacilioModule getMfaSettings() {
			FacilioModule userModule = new FacilioModule();
			userModule.setName("orgMfaSettings");
			userModule.setDisplayName("Org MFA Settings");
			userModule.setTableName("OrgMFASettings");
			return userModule;
		}
		
		public static FacilioModule getUserMfaSettings() {
			FacilioModule userModule = new FacilioModule();
			userModule.setName("userMfaSettings");
			userModule.setDisplayName("User MFA Settings");
			userModule.setTableName("UserMfaSettings");
			return userModule;
		}
		
		public static List<FacilioField> getUserMfaSettingsFields(){
			FacilioModule module = getUserMfaSettings();
			List<FacilioField> fields = new ArrayList<>();
			
			FacilioField userId = new FacilioField();
			userId.setName("userId");
			userId.setDataType(FieldType.NUMBER);
			userId.setColumnName("USERID");
			userId.setModule(module);
			fields.add(userId);
			
			FacilioField userMfaSettings = new FacilioField();
			userMfaSettings.setName("userMfaSettings");
			userMfaSettings.setDataType(FieldType.ID);
			userMfaSettings.setColumnName("USERMFASETTINGS");
			userMfaSettings.setModule(module);
			fields.add(userMfaSettings);
			
			FacilioField totpSecret = new FacilioField();
			totpSecret.setName("totpSecret");
			totpSecret.setDataType(FieldType.STRING);
			totpSecret.setColumnName("TOTP_SECRET");
			totpSecret.setModule(module);
			fields.add(totpSecret);

			FacilioField totpStatus = new FacilioField();
			totpStatus.setName("totpStatus");
			totpStatus.setDataType(FieldType.BOOLEAN);
			totpStatus.setColumnName("TOTP_STATUS");
			totpStatus.setModule(module);
			fields.add(totpStatus);

			return fields;
		}
		
		public static FacilioModule getUserMobileSettingModule() {
			FacilioModule userModule = new FacilioModule();
			userModule.setName("userMobileSetting");
			userModule.setDisplayName("User Mobile Setting");
			userModule.setTableName("User_Mobile_Setting");

			return userModule;
		}
		
		public static List<FacilioField> getUserMobileSettingFields() {
			FacilioModule module = getUserMobileSettingModule();
			List<FacilioField> fields = new ArrayList<>();

			FacilioField uid = new FacilioField();
			uid.setName("userId");
			uid.setDataType(FieldType.NUMBER);
			uid.setColumnName("USERID");
			uid.setModule(module);
			fields.add(uid);

			FacilioField userMobileSettingId = new FacilioField();
			userMobileSettingId.setName("userMobileSettingId");
			userMobileSettingId.setDataType(FieldType.ID);
			userMobileSettingId.setColumnName("USER_MOBILE_SETTING_ID");
			userMobileSettingId.setModule(module);
			fields.add(userMobileSettingId);

			FacilioField mobileInstanceId = new FacilioField();
			mobileInstanceId.setName("mobileInstanceId");
			mobileInstanceId.setDataType(FieldType.STRING);
			mobileInstanceId.setColumnName("MOBILE_INSTANCE_ID");
			mobileInstanceId.setModule(module);
			fields.add(mobileInstanceId);

			fields.add(FieldFactory.getField("createdTime", "CREATED_TIME", module, FieldType.DATE_TIME));

			fields.add(FieldFactory.getField("fromPortal", "IS_FROM_PORTAL", module, FieldType.BOOLEAN));

			fields.add(FieldFactory.getStringField("appLinkName","APP_LINK_NAME",module));

			return fields;
		}

		public static FacilioModule getDomainSSOModule() {
			FacilioModule domainSSOModule = new FacilioModule();
			domainSSOModule.setName("domainSSO");
			domainSSOModule.setDisplayName("Domain SSO");
			domainSSOModule.setTableName("Domain_SSO");
			return domainSSOModule;
		}
		
		public static FacilioModule getAccountSSOModule() {
			FacilioModule accountSSOModule = new FacilioModule();
			accountSSOModule.setName("accountSSO");
			accountSSOModule.setDisplayName("Account SSO");
			accountSSOModule.setTableName("Account_SSO");

			return accountSSOModule;
		}

		public static FacilioModule getAppDomainLinkModule() {
			FacilioModule domainLink = new FacilioModule();
			domainLink.setName("appDomainLink");
			domainLink.setDisplayName("App Domain Links");
			domainLink.setTableName("Domain_Links");

			return domainLink;
		}

		public static List<FacilioField> getDomainSSOFields() {
			FacilioModule module = getDomainSSOModule();
			List<FacilioField> fields = new ArrayList<>();

			FacilioField id = new FacilioField();
			id.setName("id");
			id.setDataType(FieldType.ID);
			id.setColumnName("ID");
			id.setModule(module);
			fields.add(id);

			FacilioField appDomainId = new FacilioField();
			appDomainId.setName("appDomainId");
			appDomainId.setDataType(FieldType.NUMBER);
			appDomainId.setColumnName("APP_DOMAIN_ID");
			appDomainId.setModule(module);
			fields.add(appDomainId);

			FacilioField name = new FacilioField();
			name.setName("name");
			name.setDataType(FieldType.STRING);
			name.setColumnName("NAME");
			name.setModule(module);
			fields.add(name);

			FacilioField type = new FacilioField();
			type.setName("ssoType");
			type.setDataType(FieldType.NUMBER);
			type.setColumnName("TYPE");
			type.setModule(module);
			fields.add(type);

			FacilioField config = new FacilioField();
			config.setName("configJSON");
			config.setDataType(FieldType.STRING);
			config.setColumnName("CONFIG");
			config.setModule(module);
			fields.add(config);

			FacilioField isActive = new FacilioField();
			isActive.setName("isActive");
			isActive.setDataType(FieldType.BOOLEAN);
			isActive.setColumnName("IS_ACTIVE");
			isActive.setModule(module);
			fields.add(isActive);

			FacilioField createUser = new FacilioField();
			createUser.setName("isCreateUser");
			createUser.setDataType(FieldType.BOOLEAN);
			createUser.setColumnName("CREATE_USER");
			createUser.setModule(module);
			fields.add(createUser);

			FacilioField showSSOLink = new FacilioField();
			showSSOLink.setName("showSSOLink");
			showSSOLink.setDataType(FieldType.BOOLEAN);
			showSSOLink.setColumnName("SHOW_SSO_LINK");
			showSSOLink.setModule(module);
			fields.add(showSSOLink);

			FacilioField createdTime = new FacilioField();
			createdTime.setName("createdTime");
			createdTime.setDataType(FieldType.NUMBER);
			createdTime.setColumnName("CREATED_TIME");
			createdTime.setModule(module);
			fields.add(createdTime);

			FacilioField createdBy = new FacilioField();
			createdBy.setName("createdBy");
			createdBy.setDataType(FieldType.NUMBER);
			createdBy.setColumnName("CREATED_BY");
			createdBy.setModule(module);
			fields.add(createdBy);

			FacilioField modifiedTime = new FacilioField();
			modifiedTime.setName("modifiedTime");
			modifiedTime.setDataType(FieldType.NUMBER);
			modifiedTime.setColumnName("MODIFIED_TIME");
			modifiedTime.setModule(module);
			fields.add(modifiedTime);

			FacilioField modifiedBy = new FacilioField();
			modifiedBy.setName("modifiedBy");
			modifiedBy.setDataType(FieldType.NUMBER);
			modifiedBy.setColumnName("MODIFIED_BY");
			modifiedBy.setModule(module);
			fields.add(modifiedBy);

			return fields;
		}

		
		public static List<FacilioField> getAccountSSOFields() {
			
			FacilioModule module = getAccountSSOModule();
			
			List<FacilioField> fields = new ArrayList<>();

			FacilioField id = new FacilioField();
			id.setName("id");
			id.setDataType(FieldType.ID);
			id.setColumnName("ID");
			id.setModule(module);
			fields.add(id);

			FacilioField orgId = new FacilioField();
			orgId.setName("orgId");
			orgId.setDataType(FieldType.NUMBER);
			orgId.setColumnName("ORGID");
			orgId.setModule(module);
			fields.add(orgId);

			FacilioField name = new FacilioField();
			name.setName("name");
			name.setDataType(FieldType.STRING);
			name.setColumnName("NAME");
			name.setModule(module);
			fields.add(name);
			
			FacilioField type = new FacilioField();
			type.setName("ssoType");
			type.setDataType(FieldType.NUMBER);
			type.setColumnName("TYPE");
			type.setModule(module);
			fields.add(type);
			
			FacilioField config = new FacilioField();
			config.setName("configJSON");
			config.setDataType(FieldType.STRING);
			config.setColumnName("CONFIG");
			config.setModule(module);
			fields.add(config);
			
			FacilioField isActive = new FacilioField();
			isActive.setName("isActive");
			isActive.setDataType(FieldType.BOOLEAN);
			isActive.setColumnName("IS_ACTIVE");
			isActive.setModule(module);
			fields.add(isActive);
			
			FacilioField createdTime = new FacilioField();
			createdTime.setName("createdTime");
			createdTime.setDataType(FieldType.NUMBER);
			createdTime.setColumnName("CREATED_TIME");
			createdTime.setModule(module);
			fields.add(createdTime);
			
			FacilioField createdBy = new FacilioField();
			createdBy.setName("createdBy");
			createdBy.setDataType(FieldType.NUMBER);
			createdBy.setColumnName("CREATED_BY");
			createdBy.setModule(module);
			fields.add(createdBy);
			
			FacilioField modifiedTime = new FacilioField();
			modifiedTime.setName("modifiedTime");
			modifiedTime.setDataType(FieldType.NUMBER);
			modifiedTime.setColumnName("MODIFIED_TIME");
			modifiedTime.setModule(module);
			fields.add(modifiedTime);
			
			FacilioField modifiedBy = new FacilioField();
			modifiedBy.setName("modifiedBy");
			modifiedBy.setDataType(FieldType.NUMBER);
			modifiedBy.setColumnName("MODIFIED_BY");
			modifiedBy.setModule(module);
			fields.add(modifiedBy);
			
			return fields;
		}

		public static List<FacilioField> getAppDomainLinkFields() {
			FacilioModule module = getAppDomainLinkModule();
			List<FacilioField> fields = new ArrayList<>();

			FacilioField id = new FacilioField();
			id.setName("id");
			id.setDataType(FieldType.ID);
			id.setColumnName("ID");
			id.setModule(module);
			fields.add(id);

			FacilioField appDomainId = new FacilioField();
			appDomainId.setName("appDomainId");
			appDomainId.setDataType(FieldType.NUMBER);
			appDomainId.setColumnName("APP_DOMAIN_ID");
			appDomainId.setModule(module);
			fields.add(appDomainId);

			FacilioField name = new FacilioField();
			name.setName("name");
			name.setDataType(FieldType.STRING);
			name.setColumnName("NAME");
			name.setModule(module);
			fields.add(name);

			FacilioField type = new FacilioField();
			type.setName("linkType");
			type.setDataType(FieldType.NUMBER);
			type.setColumnName("LINK_TYPE");
			type.setModule(module);
			fields.add(type);

			FacilioField isExternalURL = new FacilioField();
			isExternalURL.setName("isExternalURL");
			isExternalURL.setDataType(FieldType.BOOLEAN);
			isExternalURL.setColumnName("IS_EXTERNAL_URL");
			isExternalURL.setModule(module);
			fields.add(isExternalURL);

			FacilioField content = new FacilioField();
			content.setName("content");
			content.setDataType(FieldType.STRING);
			content.setColumnName("CONTENT");
			content.setModule(module);
			fields.add(content);

			FacilioField externalURL = new FacilioField();
			externalURL.setName("externalURL");
			externalURL.setDataType(FieldType.STRING);
			externalURL.setColumnName("EXTERNAL_URL");
			externalURL.setModule(module);
			fields.add(externalURL);

			FacilioField showInMenu = new FacilioField();
			showInMenu.setName("showInMenu");
			showInMenu.setDataType(FieldType.BOOLEAN);
			showInMenu.setColumnName("SHOW_IN_MENU");
			showInMenu.setModule(module);
			fields.add(showInMenu);

			FacilioField createdTime = new FacilioField();
			createdTime.setName("createdTime");
			createdTime.setDataType(FieldType.NUMBER);
			createdTime.setColumnName("CREATED_TIME");
			createdTime.setModule(module);
			fields.add(createdTime);

			FacilioField createdBy = new FacilioField();
			createdBy.setName("createdBy");
			createdBy.setDataType(FieldType.NUMBER);
			createdBy.setColumnName("CREATED_BY");
			createdBy.setModule(module);
			fields.add(createdBy);

			FacilioField modifiedTime = new FacilioField();
			modifiedTime.setName("modifiedTime");
			modifiedTime.setDataType(FieldType.NUMBER);
			modifiedTime.setColumnName("MODIFIED_TIME");
			modifiedTime.setModule(module);
			fields.add(modifiedTime);

			FacilioField modifiedBy = new FacilioField();
			modifiedBy.setName("modifiedBy");
			modifiedBy.setDataType(FieldType.NUMBER);
			modifiedBy.setColumnName("MODIFIED_BY");
			modifiedBy.setModule(module);
			fields.add(modifiedBy);

			return fields;
		}
}
