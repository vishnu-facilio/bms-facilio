package com.facilio.fw.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
