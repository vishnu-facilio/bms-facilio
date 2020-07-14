package com.facilio.services.impls.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.facilio.aws.util.FacilioProperties;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class AwsUtil 
{

	private static final Logger LOGGER = LogManager.getLogger(AwsUtil.class.getName());

	private static final String AWS_ACCESS_KEY_ID = "accessKeyId";
	private static final String AWS_SECRET_KEY_ID = "secretKeyId";



	private static volatile AWSCredentials basicCredentials = null;
	private static volatile AWSCredentialsProvider credentialsProvider = null;


	private static String region = null;
	private static final Object LOCK = new Object();

	private static AWSCredentials getBasicAwsCredentials() {
		if(basicCredentials == null) {
			synchronized (LOCK) {
				if (basicCredentials == null) {
					basicCredentials = new BasicAWSCredentials(FacilioProperties.getConfig(AWS_ACCESS_KEY_ID), FacilioProperties.getConfig(AWS_SECRET_KEY_ID));
				}
			}
		}
		return basicCredentials;
	}

	public static AWSCredentialsProvider getAWSCredentialsProvider() {
		if(credentialsProvider == null){
			synchronized (LOCK) {
				if(credentialsProvider == null){
					credentialsProvider = InstanceProfileCredentialsProvider.createAsyncRefreshingProvider(false);
				}
			}
		}
		return credentialsProvider;
	}

	public static String getRegion() {
    	if(region == null) {
    		synchronized (LOCK) {
    			if(region == null) {
    				region = FacilioProperties.getRegion();
				}
			}
		}
    	return region;
	}




}