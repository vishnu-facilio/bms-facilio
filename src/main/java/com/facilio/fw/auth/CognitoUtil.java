package com.facilio.fw.auth;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentity;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClientBuilder;
import com.amazonaws.services.cognitoidentity.model.GetIdRequest;
import com.amazonaws.services.cognitoidentity.model.GetIdResult;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminCreateUserResult;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesRequest;
import com.amazonaws.services.cognitoidp.model.AdminUpdateUserAttributesResult;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.facilio.aws.util.AwsUtil;
import com.facilio.constants.FacilioConstants;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;

public class CognitoUtil {

	private static AWSCognitoIdentityProvider IDP_PROVIDER = null;
	
	private static AmazonCognitoIdentity IDENTITY_CLIENT = null;
	
	public static AWSCognitoIdentityProvider getIdpProvider() {
		if (IDP_PROVIDER == null) {
			BasicAWSCredentials awsCreds = new BasicAWSCredentials(AwsUtil.getConfig("accessKeyId"), AwsUtil.getConfig("secretKeyId"));
		
			IDP_PROVIDER = AWSCognitoIdentityProviderClientBuilder.standard().withRegion("us-west-2").withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
		}
		return IDP_PROVIDER;
	}
	
	public static AmazonCognitoIdentity getIdentityClient() {
		if (IDENTITY_CLIENT == null) {
			BasicAWSCredentials awsCreds = new BasicAWSCredentials(AwsUtil.getConfig("accessKeyId"), AwsUtil.getConfig("secretKeyId"));

			IDENTITY_CLIENT = AmazonCognitoIdentityClientBuilder.standard().withRegion("us-west-2").withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
		}
		return IDENTITY_CLIENT;
	}
	
	public static boolean createUser(String name, String username, String email, boolean emailVerified, String password, String phone, String orgName) {
		
		List<AttributeType> attributes = new ArrayList<>();
		attributes.add(new AttributeType().withName("email").withValue(email));
		attributes.add(new AttributeType().withName("email_verified").withValue(emailVerified+""));
		attributes.add(new AttributeType().withName("name").withValue(name));
		attributes.add(new AttributeType().withName("custom:orgName").withValue(orgName));
		if (phone != null) {
			attributes.add(new AttributeType().withName("phone_number").withValue(phone));
		}
		
		AdminCreateUserRequest createUserReq = new AdminCreateUserRequest()
				.withUserPoolId(FacilioConstants.CognitoUserPool.getUserPoolId())
				.withUsername(email)
				.withUserAttributes(attributes)
				.withDesiredDeliveryMediums("EMAIL")
				.withTemporaryPassword(password);

		AdminCreateUserResult result = CognitoUtil.getIdpProvider().adminCreateUser(createUserReq);
		if (result != null) {
			return true;
		}
		return false;
	}
	
	public static boolean updateUserAttributes(String username, String phone) {
		
		if (phone == null) {
			phone = "";
		}
		
		List<AttributeType> attributes = new ArrayList<>();
		attributes.add(new AttributeType().withName("phone_number").withValue(phone));
		
		AdminUpdateUserAttributesRequest updateAttrReq = new AdminUpdateUserAttributesRequest()
				.withUserPoolId(FacilioConstants.CognitoUserPool.getUserPoolId())
				.withUsername(username)
				.withUserAttributes(attributes);

		AdminUpdateUserAttributesResult result = CognitoUtil.getIdpProvider().adminUpdateUserAttributes(updateAttrReq);
		if (result != null) {
			return true;
		}
		return false;
	}
	
	public static AdminGetUserResult getUser(String username) {
		
		AdminGetUserRequest adminGetReq = new AdminGetUserRequest().withUserPoolId(FacilioConstants.CognitoUserPool.getUserPoolId()).withUsername(username);
		
		return getIdpProvider().adminGetUser(adminGetReq);
	}
	
	public static boolean isEmailExists(String email) {
		
		try {
			AdminGetUserRequest adminGetReq = new AdminGetUserRequest().withUserPoolId(FacilioConstants.CognitoUserPool.getUserPoolId()).withUsername(email);
			
			AdminGetUserResult userObj = getIdpProvider().adminGetUser(adminGetReq);
			if (userObj != null && email.equalsIgnoreCase(userObj.getUsername())) {
				return true;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static String getIdentityId(String idToken) {
		
		Map<String, String> logins = new HashMap<String, String>();
		logins.put("cognito-idp.us-west-2.amazonaws.com/"+FacilioConstants.CognitoUserPool.getUserPoolId(), idToken);

		GetIdRequest idReq = new GetIdRequest()
				.withAccountId(FacilioConstants.CognitoUserPool.getAWSAccountId())
				.withIdentityPoolId(FacilioConstants.CognitoUserPool.getIdentityPoolId())
				.withLogins(logins);
		
		GetIdResult result = getIdentityClient().getId(idReq);
		
		return result.getIdentityId();
	}
	
	public static JSONObject getUserAttributes(String email) {
		
		JSONObject jobj = new JSONObject();
		String emailDomain = null;
		try {
			// temp code
			emailDomain = email.split("@")[1];
			emailDomain = emailDomain.split("\\.")[0];
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		AdminGetUserResult userResult = CognitoUtil.getUser(email);
		List<AttributeType> li = userResult.getUserAttributes();
		for (AttributeType attr : li) {
			if (!jobj.containsKey(attr.getName())) {
				jobj.put(attr.getName(), attr.getValue());
			}
		}
		if (!jobj.containsKey("custom:orgDomain") && emailDomain != null) {
			jobj.put("custom:orgDomain", emailDomain);
		}
		return jobj;
	}
	
	public static CognitoUser verifyIDToken(String idToken) throws Exception {
		
		if (idToken == null || "".equals(idToken.trim())) {
			return null;
		}
		
		ConfigurableJWTProcessor jwtProcessor = new DefaultJWTProcessor();
		
		JWKSet jwkSet = JWKSet.load(new File(CognitoUtil.class.getClassLoader().getResource("conf/jwks.json").getFile()));
		
		JWKSource keySource = new ImmutableJWKSet(jwkSet);

		JWSAlgorithm expectedJWSAlg = JWSAlgorithm.RS256;

		JWSKeySelector keySelector = new JWSVerificationKeySelector(expectedJWSAlg, keySource);
		jwtProcessor.setJWSKeySelector(keySelector);

		JWTClaimsSet claimsSet = jwtProcessor.process(idToken, null);

		if (!FacilioConstants.CognitoUserPool.getIssURL().equals(claimsSet.getIssuer())) {
			throw new Exception("Invalid Issuer.");
		}
		
		CognitoUser userInfo = new CognitoUser();
		
		net.minidev.json.JSONObject jsonObj = claimsSet.toJSONObject();
		Iterator<String> keys = jsonObj.keySet().iterator();
		
		while (keys.hasNext()) {
			String key = keys.next();
			String value = jsonObj.get(key).toString();
			
			if ("sub".equals(key)) {
				userInfo.setCognitoId(value);
			}
			else if ("cognito:username".equals(key)) {
				userInfo.setUserName(value);
			}
			else if ("name".equals(key)) {
				userInfo.setName(value);
			}
			else if ("email".equals(key)) {
				userInfo.setEmail(value);
			}
			else if ("email_verified".equals(key)) {
				userInfo.setEmailVerified(Boolean.parseBoolean(value));
			}
			else if ("phone_number".equals(key)) {
				userInfo.setPhoneNumber(value);
			}
			else if ("phone_number_verified".equals(key)) {
				userInfo.setPhoneNumberVerified(Boolean.parseBoolean(value));
			}
			else if ("locale".equals(key)) {
				userInfo.setLocale(value);
			}
			else if ("zoneinfo".equals(key)) {
				userInfo.setTimezone(value);
			}
			else {
				userInfo.addAdditionalProperty(key, value);
			}
		}
				
		return userInfo;
	}
	
	public static class CognitoUser {
		private String cognitoId = "";
		private String userName;
		private String name;
		private String email;
		private boolean emailVerified;
		private String phoneNumber;
		private boolean phoneNumberVerified;
		private String locale;
		private String timezone;
		private JSONObject additionalProps;
		
		public String getCognitoId() {
			return cognitoId;
		}

		public void setCognitoId(String cognitoId) {
			this.cognitoId = cognitoId;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}
		
		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public boolean isEmailVerified() {
			return emailVerified;
		}

		public void setEmailVerified(boolean emailVerified) {
			this.emailVerified = emailVerified;
		}

		public String getPhoneNumber() {
			return phoneNumber;
		}

		public void setPhoneNumber(String phoneNumber) {
			this.phoneNumber = phoneNumber;
		}

		public boolean isPhoneNumberVerified() {
			return phoneNumberVerified;
		}

		public void setPhoneNumberVerified(boolean phoneNumberVerified) {
			this.phoneNumberVerified = phoneNumberVerified;
		}

		public String getLocale() {
			return locale;
		}

		public void setLocale(String locale) {
			this.locale = locale;
		}

		public String getTimezone() {
			return timezone;
		}

		public void setTimezone(String timezone) {
			this.timezone = timezone;
		}
		
		public void addAdditionalProperty(String key, String value) {
			if (this.additionalProps == null) {
				this.additionalProps = new JSONObject();
			}
			this.additionalProps.put(key, value);
		}
		
		public JSONObject getAdditionalProps() {
			return this.additionalProps;
		}
		
		public String getAdditionalProperty(String key) {
			if (this.additionalProps != null) {
				return this.additionalProps.get(key).toString();
			}
			return null;
		}
	}
}