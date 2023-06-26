package com.facilio.aws.util;

import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.services.iot.AWSIotClientBuilder;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.model.*;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.fw.FacilioException;
import com.facilio.queue.source.KafkaMessageSource;
import com.facilio.queue.source.MessageSourceUtil;
import com.facilio.service.FacilioService;
import com.facilio.util.FacilioUtil;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.Document;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextRequest;
import software.amazon.awssdk.services.textract.model.DetectDocumentTextResponse;
import software.amazon.awssdk.services.textract.model.Block;
import software.amazon.awssdk.services.textract.model.TextractException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.transaction.SystemException;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AwsUtil extends BaseAwsUtil{

	private static final Logger LOGGER = LogManager.getLogger(AwsUtil.class.getName());

	private static final String AWS_ACCESS_KEY_ID = "accessKeyId";
	private static final String AWS_SECRET_KEY_ID = "secretKeyId";

	private static final String AWS_IOT_SERVICE_NAME = "iotdata";

	private static final String KAFKA_PARTITION_KEY = "${clientid()}";

	private static Map<String, AWSIotMqttClient> AWS_IOT_MQTT_CLIENTS = new HashMap<>();

	private static AmazonS3 AWS_S3_CLIENT = null;
	private static TextractClient AWS_TEXTRACT_CLIENT = null;
	private static AmazonRekognition AWS_REKOGNITION_CLIENT = null;

	private static volatile AWSCredentials basicCredentials = null;
	private static volatile AWSIot awsIot = null;
	private static volatile AmazonSQS awsSQS = null;
	// private static volatile User user = null;
	private static String region = null;
	private static final String IOT_RULE_SECURITY_PROTOCOL = "iot.rule.security.protocol";
	private static final String IOT_RULE_SASL_MECHANISM = "iot.rule.sasl.mechanism";
	private static final String BROKER = "broker";
	private static final String IOT_RULE_SASL_SCRAM_USERNAME = "iot.rule.sasl.scram.username";
	private static final String IOT_RULE_SASL_SCRAM_PASSWORD = "iot.rule.sasl.scram.password";
	private static final String IOT_SQL_VERSION = "2016-03-23";//Refer the versions available in AWS iot sql version document before changing.
	private static final String IOT_RULE_ARN = "iot.rule.arn";
	private static final String IOT_RULE_DISABLED = "iot.rule.disabled";

	public static Map<String, Object> getClientInfoAsService() throws Exception {
		return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.DEFAULT_SERVICE, () -> getClientInfo());
	}

	public static Map<String, Object> getClientInfo() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs;
		String clientVersion = null;
		boolean isNewClientBuild = false;
		Map<String, Object> clientInfo = new HashMap<>();
		if (FacilioProperties.getClientVersion() != null) {
			clientVersion = FacilioProperties.getClientVersion();
			isNewClientBuild = true;
		} else {
			try {
				if (FacilioProperties.getEnvironment() != null) {
					conn = FacilioConnectionPool.INSTANCE.getConnection();
					pstmt = conn.prepareStatement("SELECT * FROM ClientApp WHERE environment=?");
					pstmt.setString(1, FacilioProperties.getEnvironment());
					rs = pstmt.executeQuery();
					if (rs.next()) {
						clientVersion = rs.getString("version");
						isNewClientBuild = rs.getBoolean("is_new_client_build");

					}
				}
			} catch (SQLException | RuntimeException e) {
				LOGGER.info("Exception while verifying password, ", e);
			} finally {
				DBUtil.closeAll(conn, pstmt);
			}
		}
		clientInfo.put("version", clientVersion);
		clientInfo.put("isNewClientBuild", isNewClientBuild);
		return clientInfo;
	}

	public static int updateClientVersion(String newVersion, boolean isNewClientBuild) throws Exception {
		com.facilio.accounts.dto.User currentUser = AccountUtil.getCurrentUser();
		if (currentUser != null) {
			return FacilioService.runAsServiceWihReturn(FacilioConstants.Services.IAM_SERVICE, () -> updateClientVersionervice(newVersion, isNewClientBuild, currentUser.getId()));
		} else {
			throw new IllegalArgumentException("Current User cannot be null while updating Client Version");
		}
	}


	public static String generateCSRFToken() throws Exception {
		MessageDigest salt = MessageDigest.getInstance("SHA-256");
		salt.update(UUID.randomUUID().toString().getBytes("UTF-8"));
		StringBuilder sb = new StringBuilder();
		for (byte aDigested : salt.digest()) {
			sb.append(Integer.toHexString(0xff & aDigested));
		}
		return sb.toString();
	}

	public static JSONObject getPolicyGist() throws Exception {
		return AwsPolicyUtils.getPolicyGist(getIotClient());
	}

	private static int updateClientVersionervice(String newVersion, boolean isNewClientBuild, long userId) throws SystemException {
		int updatedRows = 0;
		if (newVersion != null) {
			newVersion = newVersion.trim();
			newVersion = newVersion.replace("/", "");
			Connection conn = null;
			PreparedStatement pstmt = null;
			try {
				if (checkIfVersionExistsInS3(newVersion)) {
					if (FacilioProperties.getEnvironment() != null && userId != -1) {
						FacilioTransactionManager.INSTANCE.getTransactionManager().begin();
						conn = FacilioConnectionPool.INSTANCE.getConnection();
						pstmt = conn.prepareStatement("Update ClientApp set version=?, updatedTime=?, updatedBy=?, is_new_client_build=?  WHERE environment=?");
						pstmt.setString(1, newVersion);
						pstmt.setLong(2, System.currentTimeMillis());
						pstmt.setLong(3, userId);
						pstmt.setBoolean(4, isNewClientBuild);
						pstmt.setString(5, FacilioProperties.getEnvironment());

						updatedRows = pstmt.executeUpdate();
						if (updatedRows > 0) {
							LOGGER.info("Updated client version successfully");
						}
						FacilioTransactionManager.INSTANCE.getTransactionManager().commit();
					}
				}
			} catch (Exception e) {
				FacilioTransactionManager.INSTANCE.getTransactionManager().rollback();
				LOGGER.info("Exception while updating client version, ", e);
			} finally {
				DBUtil.closeAll(conn, pstmt);
			}
		}
		return updatedRows;
	}

	private static boolean checkIfVersionExistsInS3(String newVersion) {
		if (FacilioProperties.isDevelopment()) {
			return true;
		}
		boolean objectExists = false;
		String staticBucket = FacilioProperties.getConfig("static.bucket");
		if (staticBucket != null) {
			AmazonS3 s3Client = getAmazonS3Client();
			objectExists = s3Client.doesObjectExist(staticBucket, newVersion + FacilioUtil.normalizePath("/js/app.js"));
		}
		return objectExists;

	}

	public static CreateKeysAndCertificateResult getCertificateResult() {
		AWSIot awsIot = AWSIotClientBuilder.standard().withCredentials(InstanceProfileCredentialsProvider.createAsyncRefreshingProvider(false)).build();

		CreateKeysAndCertificateRequest cr = new CreateKeysAndCertificateRequest().withSetAsActive(true);
		CreateKeysAndCertificateResult certResult = awsIot.createKeysAndCertificate(cr);

		AttachPrincipalPolicyRequest policyResult = new AttachPrincipalPolicyRequest().withPolicyName("EM-Policy").withPrincipal(certResult.getCertificateArn());
		awsIot.attachPrincipalPolicy(policyResult);
		return certResult;
	}

	public static AWSIotMqttClient getAwsIotMqttClient(String clientId) throws AWSIotException {
		if (AWS_IOT_MQTT_CLIENTS.containsKey(clientId)) {
			return AWS_IOT_MQTT_CLIENTS.get(clientId);
		}
		AWSIotMqttClient awsIotClient = new AWSIotMqttClient(FacilioProperties.getConfig("iot.endpoint"), clientId, FacilioProperties.getConfig(AwsUtil.AWS_ACCESS_KEY_ID), FacilioProperties.getConfig(AwsUtil.AWS_SECRET_KEY_ID));
		awsIotClient.connect();
		AWS_IOT_MQTT_CLIENTS.put(clientId, awsIotClient);
		return awsIotClient;
	}

	public static AmazonS3 getAmazonS3Client() {
		if (AWS_S3_CLIENT == null) {
			ClientConfiguration configuration = new ClientConfiguration().withMaxConnections(200).withConnectionTimeout(30000).withMaxErrorRetry(3);
			AWS_S3_CLIENT = AmazonS3ClientBuilder.standard().withRegion(getRegion()).withCredentials(getAWSCredentialsProvider()).withClientConfiguration(configuration).build();
		}
		return AWS_S3_CLIENT;
	}

	public static TextractClient getAmazonTextractClient() {
		if (AWS_TEXTRACT_CLIENT == null) {
			Region region = getRegionV2(getRegion());
			LOGGER.info("AWS TEXTRACT - "+region);
			software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider awsCred = getAwsTextractCreds();
			AWS_TEXTRACT_CLIENT = TextractClient.builder().region(region).credentialsProvider(awsCred).build();
		}
		return AWS_TEXTRACT_CLIENT;
	}

	public static Region getRegionV2(String regionString){
		Region region;
		if(regionString == null){
			region = Region.US_EAST_1;
			return region;
		}
		region = Region.of(regionString);
		return region;
	}

	public static software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider getAwsTextractCreds() {
		software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider credentials = software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider.create();
		return credentials;
	}

	public static AmazonRekognition getAmazonRekognitionClient() {
		if (AWS_REKOGNITION_CLIENT == null) {
			AWS_REKOGNITION_CLIENT = AmazonRekognitionClientBuilder.standard().withRegion(getRegion()).withCredentials(getAWSCredentialsProvider()).build();
		}
		return AWS_REKOGNITION_CLIENT;
	}

	public static String getSignature(String payload, String xAmzDate, String path) throws Exception {
		String secretKey = FacilioProperties.getConfig("secretKeyId");
		String dateStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());    //"20170525";
		String regionName = FacilioProperties.getConfig("region");        //"us-west-2";

		byte[] kSecret = ("AWS4" + secretKey).getBytes(StandardCharsets.UTF_8);
		byte[] kDate = HmacSHA256(dateStamp, kSecret);
		byte[] kRegion = HmacSHA256(regionName, kDate);
		byte[] kService = HmacSHA256(AwsUtil.AWS_IOT_SERVICE_NAME, kRegion);
		byte[] kSigning = HmacSHA256("aws4_request", kService);

		String stringToSign = getStringToSign(payload, xAmzDate, path);
		return bytesToHex(HmacSHA256(stringToSign, kSigning));
	}

	public static String getStringToSign(String payload, String xAmzDate, String path) throws NoSuchAlgorithmException {
		String canonicalHeader = "content-type:application/json\nhost:" + FacilioProperties.getConfig("host") + "\nx-amz-date:" + xAmzDate + "\n";
		String signedHeader = "content-type;host;x-amz-date";
		String canonicalRequest = "POST" + "\n" + path + "\n" + "" + "\n" + canonicalHeader + "\n" + signedHeader + "\n" + hash256(payload).toLowerCase();

		String scope = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + FacilioProperties.getConfig("region") + "/" + AwsUtil.AWS_IOT_SERVICE_NAME + "/aws4_request";
		return "AWS4-HMAC-SHA256" + "\n" + xAmzDate + "\n" + scope + "\n" + hash256(canonicalRequest).toLowerCase();
	}

	public static Map<String, String> getAuthHeaders(String signature, String xAmzDate) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/json");
		headers.put("Host", FacilioProperties.getConfig("host"));
		headers.put("X-Amz-Date", xAmzDate);
		headers.put("Authorization", "AWS4-HMAC-SHA256 Credential=" + FacilioProperties.getConfig("accessKeyId") + "/" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + FacilioProperties.getConfig("region") + "/" + AwsUtil.AWS_IOT_SERVICE_NAME + "/aws4_request, SignedHeaders=content-type;host;x-amz-date, Signature=" + signature);
		return headers;
	}

	public static CloseableHttpClient getHttpClient(int timeOutInSeconds) {
		if (timeOutInSeconds != -1L) {
			RequestConfig config = RequestConfig.custom()
					.setSocketTimeout(timeOutInSeconds * 1000).build();
			return HttpClientBuilder.create().setDefaultRequestConfig(config).build();
		}

		return HttpClients.createDefault();
	}

	public static String doHttpPost(String url, Map<String, String> headers, Map<String, String> params, String bodyContent, int timeOutInSeconds) throws IOException {
		StringBuilder result = new StringBuilder();

		CloseableHttpClient client = AwsUtil.getHttpClient(timeOutInSeconds);

		try {
			HttpPost post = new HttpPost(url);
			if (headers != null) {
				for (String key : headers.keySet()) {
					String value = headers.get(key);
					post.setHeader(key, value);
				}
			}
			if (bodyContent != null) {
				HttpEntity entity = new ByteArrayEntity(bodyContent.getBytes(StandardCharsets.UTF_8));
				post.setEntity(entity);
			}
			if (params != null) {
				List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
				for (String key : params.keySet()) {
					String value = params.get(key);
					postParameters.add(new BasicNameValuePair(key, value));
				}
				post.setEntity(new UrlEncodedFormEntity(postParameters));
			}

			CloseableHttpResponse response = client.execute(post);
			int status = response.getStatusLine().getStatusCode();
			LOGGER.info("push notification awsutil :" + url + headers + bodyContent + status);
			if (status != 200) {
				LOGGER.info("\nSending 'POST' request to URL : " + url);
				LOGGER.info("Post parameters : " + post.getEntity());
				LOGGER.info("Response Code : " + status);
			}

			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) {
				result.append(line);
			}
		} catch (Exception e) {
			LOGGER.info("Executing doHttpPost ::::url:::" + url, e);
		} finally {
			client.close();
		}
		return result.toString();
	}

	public static String doHttpPost(String url, Map<String, String> headers, Map<String, String> params, String bodyContent) throws IOException {
		return AwsUtil.doHttpPost(url, headers, params, bodyContent, -1);
	}

	private static byte[] HmacSHA256(String data, byte[] key) throws Exception {
		String algorithm = "HmacSHA256";
		Mac mac = Mac.getInstance(algorithm);
		mac.init(new SecretKeySpec(key, algorithm));
		return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
	}

	private static String hash256(String data) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		md.update(data.getBytes());
		return bytesToHex(md.digest());
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuilder result = new StringBuilder();
		for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
		return result.toString();
	}

	public static void sendVerificationMailForFromAddressConfig(String email) {

		try {

			VerifyEmailIdentityRequest verifyEmailAddressResult = new VerifyEmailIdentityRequest()
					.withEmailAddress(email);

			AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
					.withRegion(Regions.US_WEST_2).withCredentials(getAWSCredentialsProvider()).build();

			VerifyEmailIdentityResult result = client.verifyEmailIdentity(verifyEmailAddressResult);

			LOGGER.info("verification response -- " + result);

		} catch (Exception ex) {
			LOGGER.info("Error During send Verification mail to " + email + " " + ex.getMessage());
			throw ex;
		}
	}


	public static Map<String, Boolean> getVerificationMailStatus(List<String> emails) {

		try {

			Map<String, Boolean> facilioResultMap = new HashMap<String, Boolean>();

			GetIdentityVerificationAttributesRequest getIdentityVerificationAttributesRequest = new GetIdentityVerificationAttributesRequest()
					.withIdentities(emails);

			AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
					.withRegion(Regions.US_WEST_2).withCredentials(getAWSCredentialsProvider()).build();

			GetIdentityVerificationAttributesResult result = client.getIdentityVerificationAttributes(getIdentityVerificationAttributesRequest);

			Map<String, IdentityVerificationAttributes> resultmap = result.getVerificationAttributes();

			LOGGER.error("resultmap -- " + resultmap);

			for (String email : emails) {

				IdentityVerificationAttributes status = resultmap.get(email);

				if (status != null) {

					if (status.getVerificationStatus().equals("Success")) {
						facilioResultMap.put(email, Boolean.TRUE);
					} else {
						facilioResultMap.put(email, Boolean.FALSE);
					}
				}

			}

			return facilioResultMap;

		} catch (Exception ex) {
			LOGGER.info("Error During Verification mail " + emails + " " + ex.getMessage());
		}
		return null;
	}

	private static AWSCredentials getBasicAwsCredentials() {
		if (basicCredentials == null) {
			synchronized (LOCK) {
				if (basicCredentials == null) {
					basicCredentials = new BasicAWSCredentials(FacilioProperties.getConfig(AWS_ACCESS_KEY_ID), FacilioProperties.getConfig(AWS_SECRET_KEY_ID));
				}
			}
		}
		return basicCredentials;
	}

	public static String getRegion() {
		if (region == null) {
			synchronized (LOCK) {
				if (region == null) {
					region = FacilioProperties.getConfig("region");
				}
			}
		}
		return region;
	}

	private static AWSIot getIotClient() {
		if (awsIot == null) {
			synchronized (LOCK) {
				if (awsIot == null) {
					awsIot = AWSIotClientBuilder.standard()
							.withCredentials(getAWSCredentialsProvider())
							.withRegion(getRegion()).build();
				}
			}
		}
		return awsIot;
	}

	static String getIotArn() {
		return "arn:aws:iot:" + getRegion() + ":" + getUserId();
	}

	public static AmazonSQS getSQSClient() {
		if (awsSQS == null) {
			synchronized (LOCK) {
				if (awsSQS == null) {
					awsSQS = AmazonSQSClientBuilder.standard()
							.withCredentials(getAWSCredentialsProvider())
							.withRegion(getRegion()).build();

					ExtendedClientConfiguration configuration = new ExtendedClientConfiguration();
					configuration.setAlwaysThroughS3(false);
					configuration.setLargePayloadSupportEnabled(getAmazonS3Client(), "fsqss3");
					awsSQS = new AmazonSQSExtendedClient(awsSQS, configuration);
				}
			}
		}
		return awsSQS;
	}

	private static String getUserId() {
    	/*if(user == null) {
    		synchronized (LOCK) {
    			if(user == null) {
					AmazonIdentityManagement iam = AmazonIdentityManagementClientBuilder.standard()
							.withCredentials(getAWSCredentialsProvider())
							.withRegion(getRegion()).build();

					GetUserResult result = iam.getUser();
    				user =  result.getUser();
				}
			}
		}
		return user.getUserId();*/
		return FacilioProperties.getConfig("user.id");
	}
	/*public static void addIotClient(String policyName, String clientId){
		addAwsIotClient(policyName,clientId);
	}

	public static boolean addAwsIotClient(String policyName, String clientId) {
		try {
			AWSIot client = getIotClient();
			JSONObject policyDocumentJson = getPolicyDocumentJson(getPolicy(policyName, client));
			JSONObject newPolicyDocumentJson = addClientToPolicyDoc(policyName, clientId, policyDocumentJson);
			createPolicyVersion(policyName, client, newPolicyDocumentJson);
			return true;
    	} catch (VersionsLimitExceededException limitException ){
    		LOGGER.info("policy limit exceeded ",limitException);
		} catch (ParseException e) {
			LOGGER.info(" Exception while creating policy ",e);
		}
		return false;
	}
*/


/*	private static void deletePolicyVersion(AWSIot client, String policyName) {
		AWSIotClient iotClient = (AWSIotClient) client;
		//iotClient.attachPolicy();
		GetPolicyVersionRequest policyVersionRequest = new GetPolicyVersionRequest()
				.withPolicyName(policyName);
		GetPolicyVersionResult policyVersionResult = client.getPolicyVersion(policyVersionRequest);

		DeletePolicyVersionRequest deletePolicyVersionRequest = new DeletePolicyVersionRequest()
				.withPolicyName(policyName);
	}*/




	/*private static void createPolicy(AWSIotClient client,IotPolicy policy) {
		CreatePolicyVersionRequest createPolicyVersionRequest = new CreatePolicyVersionRequest();
		createPolicyVersionRequest.withPolicyDocument()
	}*/


	private static CreateKeysAndCertificateResult createCertificate(AWSIot iotClient) throws Exception {
		if (iotClient != null) {
			CreateKeysAndCertificateRequest certificateRequest = new CreateKeysAndCertificateRequest().withSetAsActive(true);
			return iotClient.createKeysAndCertificate(certificateRequest);
		} else {
			throw new Exception(" awsiot client can't be null");
		}
	}


	private static void attachPolicy(AWSIot iotClient, CreateKeysAndCertificateResult certificateResult, String policyName) throws Exception {
		Objects.requireNonNull(iotClient, "iotclient cant be null");
		Objects.requireNonNull(certificateResult, "certificate result not null");
		Objects.requireNonNull(policyName, "policy name can't be null");
		LOGGER.info(" attaching policy for " + policyName);
		AttachPolicyRequest attachPolicyRequest = new AttachPolicyRequest().withPolicyName(policyName).withTarget(certificateResult.getCertificateArn());
		AttachPolicyResult attachPolicyResult = iotClient.attachPolicy(attachPolicyRequest);
		LOGGER.info("Attached policy : " + attachPolicyResult.getSdkHttpMetadata().getHttpStatusCode());

	}

	private static void createIotTopicRule(AWSIot iotClient, String policyAndOrgDomainName) throws Exception {
		Objects.requireNonNull(iotClient, "iotClient null");
		Objects.requireNonNull(policyAndOrgDomainName, "policyAndOrgDomainName null");
		LOGGER.info(" creating iot rule ");
		try {

			Map<String, String> ruleConfig = new HashMap<>();
			KafkaMessageSource defaultSource = MessageSourceUtil.getDefaultSource();
			Map<String, Object> configs = defaultSource.getConfigs();
			if (configs != null ) {
				if(configs.containsKey(IOT_RULE_DISABLED) && (Boolean) configs.get(IOT_RULE_DISABLED)){
					return;
				}
				if (defaultSource.isAuthEnabled() && defaultSource.getSecurityProtocol() == KafkaMessageSource.SecProtocol.SASL_SSL) {
					if (configs.containsKey(IOT_RULE_SECURITY_PROTOCOL)
							&& configs.containsKey(IOT_RULE_SASL_MECHANISM)
							&& configs.containsKey(IOT_RULE_SASL_SCRAM_USERNAME)
							&& configs.containsKey(IOT_RULE_SASL_SCRAM_PASSWORD)
							&& configs.containsKey(IOT_RULE_ARN)) {
						ruleConfig.put("security.protocol", configs.get(IOT_RULE_SECURITY_PROTOCOL).toString());
						ruleConfig.put("sasl.mechanism", configs.get(IOT_RULE_SASL_MECHANISM).toString());
						ruleConfig.put("sasl.scram.username", configs.get(IOT_RULE_SASL_SCRAM_USERNAME).toString());
						ruleConfig.put("sasl.scram.password", configs.get(IOT_RULE_SASL_SCRAM_PASSWORD).toString());
					} else {
						throw new Exception("Required keys not found in  : " + configs);
					}
				}
				ruleConfig.put("bootstrap.servers", defaultSource.getBroker());
			} else {
				throw new FacilioException("Config is null for message source" + defaultSource.getName());
			}
			if (FacilioProperties.isProduction()) {
				KafkaAction kafkaAction = new KafkaAction()
						.withTopic(policyAndOrgDomainName)
						.withDestinationArn(configs.get(IOT_RULE_ARN).toString())
						.withKey(KAFKA_PARTITION_KEY)
						.withClientProperties(ruleConfig);

				Action action = new Action().withKafka(kafkaAction);
				TopicRulePayload rulePayload = new TopicRulePayload()
						.withActions(action)
						.withSql("SELECT * FROM '" + policyAndOrgDomainName + "'")
						.withAwsIotSqlVersion(IOT_SQL_VERSION); //Refer the versions available in AWS iot sql version document before changing.

				CreateTopicRuleRequest topicRuleRequest = new CreateTopicRuleRequest().withRuleName(policyAndOrgDomainName).withTopicRulePayload(rulePayload);

				CreateTopicRuleResult topicRuleResult = iotClient.createTopicRule(topicRuleRequest);

				LOGGER.info("Topic Rule created : " + topicRuleResult.getSdkHttpMetadata().getHttpStatusCode());
			} else {
				LOGGER.info("Topic Rule Creation skipped with configs : " + ruleConfig + " :" + configs);
			}
		} catch (ResourceAlreadyExistsException resourceExists) {
			LOGGER.info("Topic Rule already exists for name : " + policyAndOrgDomainName);
		}


	}


	public static CreateKeysAndCertificateResult createIotToKafkaLink(String clientName, String policyAndOrgDomainName, String type) throws Exception {
		Objects.requireNonNull(policyAndOrgDomainName, "policyAndOrgDomainName can't be null");
		Objects.requireNonNull(type, " type can't be null");
		AWSIot iotClient = getIotClient();
		IotPolicy policy = AwsPolicyUtils.createOrUpdateIotPolicy(clientName, policyAndOrgDomainName, type, iotClient);
		CreateKeysAndCertificateResult certificateResult = createCertificate(iotClient);
		attachPolicy(iotClient, certificateResult, policyAndOrgDomainName);
		//createKinesisStream(getKinesisClient(), policyAndOrgDomainName);
		policy.setStreamName(policyAndOrgDomainName);
		createIotTopicRule(iotClient, policyAndOrgDomainName);
		return certificateResult;
	}

	public static void addClientToPolicy(String agentName, String policyName, String type) throws Exception {
		AwsPolicyUtils.createOrUpdateIotPolicy(agentName, policyName, type, getIotClient());
	}

	public static List<Block> detectDocText(InputStream sourceStream) {
		TextractClient textractClient=getAmazonTextractClient();
		List<Block> result = new ArrayList<>();
		String environment = FacilioProperties.getConfig("environment");
		if ("development".equalsIgnoreCase(environment)) {
			return null;
		}
		try {

			SdkBytes sourceBytes = SdkBytes.fromInputStream(sourceStream);

			// Get the input Document object as bytes
			Document myDoc = Document.builder()
					.bytes(sourceBytes)
					.build();

			DetectDocumentTextRequest detectDocumentTextRequest = DetectDocumentTextRequest.builder()
					.document(myDoc)
					.build();

			// Invoke the Detect operation
			DetectDocumentTextResponse textResponse = textractClient.detectDocumentText(detectDocumentTextRequest);

			List<Block> docInfo = textResponse.blocks();

			Iterator<Block> blockIterator = docInfo.iterator();

			while(blockIterator.hasNext()) {
				Block block = blockIterator.next();
				result.add(block);
			}

		} catch (TextractException e) {
			LOGGER.error(e);
		}
		return result;
	}
}