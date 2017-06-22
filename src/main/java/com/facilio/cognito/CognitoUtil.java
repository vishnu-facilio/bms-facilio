package com.facilio.cognito;

import java.util.HashMap;
import java.util.Map;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentity;
import com.amazonaws.services.cognitoidentity.AmazonCognitoIdentityClientBuilder;
import com.amazonaws.services.cognitoidentity.model.GetIdRequest;
import com.amazonaws.services.cognitoidentity.model.GetIdResult;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
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
