package com.facilio.cognito;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminGetUserRequest;
import com.amazonaws.services.cognitoidp.model.AdminGetUserResult;
import com.facilio.aws.util.AwsUtil;
import com.facilio.constants.FacilioConstants;

public class CognitoUtil {

	private static AWSCognitoIdentityProvider IDP_PROVIDER = null; 
	
	public static AWSCognitoIdentityProvider getIdpProvider() {
		if (IDP_PROVIDER == null) {
			BasicAWSCredentials awsCreds = new BasicAWSCredentials(AwsUtil.getConfig("accessKeyId"), AwsUtil.getConfig("secretKeyId"));
		
			IDP_PROVIDER = AWSCognitoIdentityProviderClientBuilder.standard().withRegion("us-west-2").withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
		}
		return IDP_PROVIDER;
	}
	
	public static AdminGetUserResult getUser(String username) {
		
		AdminGetUserRequest adminGetReq = new AdminGetUserRequest().withUserPoolId(FacilioConstants.CognitoUserPool.getUserPoolId()).withUsername(username);
		
		return getIdpProvider().adminGetUser(adminGetReq);
	}
}
