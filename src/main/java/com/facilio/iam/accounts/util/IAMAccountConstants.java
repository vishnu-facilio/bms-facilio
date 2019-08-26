package com.facilio.iam.accounts.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.accounts.dto.Permissions;
import com.facilio.accounts.dto.Role;
import com.facilio.accounts.util.AccountConstants.CommonPermission;
import com.facilio.accounts.util.AccountConstants.GroupMemberRole;
import com.facilio.accounts.util.AccountConstants.ModulePermission;
import com.facilio.accounts.util.AccountConstants.Permission;
import com.facilio.accounts.util.AccountConstants.PermissionGroup;
import com.facilio.accounts.util.AccountConstants.SessionType;
import com.facilio.accounts.util.AccountConstants.UserType;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

public class IAMAccountConstants {

			
		
		
		public static enum SessionType {
			USER_LOGIN_SESSION(1),
			PERMALINK_SESSION(2)
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
		
		public static FacilioModule getAccountsUserModule() {
			FacilioModule userModule = new FacilioModule();
			userModule.setName("accountsuser");
			userModule.setDisplayName("Accounts Users");
			userModule.setTableName("Account_Users");

			return userModule;
		}

		public static FacilioModule getOrgModule() {
			FacilioModule orgModule = new FacilioModule();
			orgModule.setName("org");
			orgModule.setDisplayName("Organizations");
			orgModule.setTableName("Organizations");

			return orgModule;
		}

		public static FacilioModule getAccountsOrgUserModule() {
			FacilioModule orgModule = new FacilioModule();
			orgModule.setName("accountsorguser");
			orgModule.setDisplayName("Accounts Org Users");
			orgModule.setTableName("Account_ORG_Users");

			return orgModule;
		}
		
		public static FacilioModule getUserSessionModule() {
			FacilioModule userSession = new FacilioModule();
			userSession.setName("usersession");
			userSession.setDisplayName("User Sessions");
			userSession.setTableName("UserSessions");

			return userSession;
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
			
			FacilioField dataSourceName = new FacilioField();
			dataSourceName.setName("dataSource");
			dataSourceName.setDataType(FieldType.STRING);
			dataSourceName.setColumnName("DATASOURCE");
			dataSourceName.setModule(module);
			fields.add(dataSourceName);
			
			FacilioField dbName = new FacilioField();
			dbName.setName("database_name");
			dbName.setDataType(FieldType.STRING);
			dbName.setColumnName("DATABASE_NAME");
			dbName.setModule(module);
			fields.add(dbName);

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
			name.setDataType(FieldType.STRING);
			name.setColumnName("NAME");
			name.setModule(module);
			fields.add(name);
			
			FacilioField domainName = new FacilioField();
			domainName.setName("domainName");
			domainName.setDataType(FieldType.STRING);
			domainName.setColumnName("DOMAIN_NAME");
			domainName.setModule(module);
			fields.add(domainName);

		/*	FacilioField cognitoId = new FacilioField();
			cognitoId.setName("cognitoId");
			cognitoId.setDataType(FieldType.STRING);
			cognitoId.setColumnName("COGNITO_ID");
			cognitoId.setModule(module);
			fields.add(cognitoId);*/

			FacilioField userVerified = new FacilioField();
			userVerified.setName("userVerified");
			userVerified.setDataType(FieldType.BOOLEAN);
			userVerified.setColumnName("USER_VERIFIED");
			userVerified.setModule(module);
			fields.add(userVerified);

			FacilioField email = new FacilioField();
			email.setName("email");
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

			return fields;
		}

		public static List<FacilioField> getAccountsOrgUserFields() {
			FacilioModule module = getAccountsOrgUserModule();
			List<FacilioField> fields = new ArrayList<>();

			FacilioField ouid = new FacilioField();
			ouid.setName("ouid");
			ouid.setDataType(FieldType.ID);
			ouid.setColumnName("ORG_USERID");
			ouid.setModule(module);
			fields.add(ouid);

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
		
			return fields;
		}
		
		public static FacilioField getOrgUserDeletedTimeField() {
			FacilioField deletedTime = new FacilioField();
			deletedTime.setName("deletedTime");
			deletedTime.setDataType(FieldType.NUMBER);
			deletedTime.setColumnName("DELETED_TIME");
			deletedTime.setModule(getAccountsOrgUserModule());
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
		
		
		

}
