package com.facilio.aws.util;

import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.services.iot.AWSIotClientBuilder;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.model.*;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.CreateStreamResult;
import com.amazonaws.services.kinesis.model.ResourceInUseException;
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
import com.facilio.bmsconsole.util.CommonAPI;
import com.facilio.bmsconsole.util.CommonAPI.NotificationType;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.email.EmailUtil;
import com.facilio.service.FacilioService;
import com.facilio.services.email.EmailClient;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.messageQueue.MessageQueueFactory;
import com.facilio.time.DateTimeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.transaction.SystemException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

public class AwsUtil 
{

	private static final Logger LOGGER = LogManager.getLogger(AwsUtil.class.getName());

	private static final String AWS_ACCESS_KEY_ID = "accessKeyId";
	private static final String AWS_SECRET_KEY_ID = "secretKeyId";

	private static final String AWS_IOT_SERVICE_NAME = "iotdata";

	private static final String KINESIS_PARTITION_KEY = "${clientid()}";
	private static final String IAM_ARN_PREFIX = "arn:aws:iam::";
	private static final String KINESIS_PUT_ROLE_SUFFIX = ":role/service-role/kinesisput";
	private static final String IOT_SQL_VERSION = "2016-03-23";//Refer the versions available in AWS iot sql version document before changing.
	
	private static Map<String, AWSIotMqttClient> AWS_IOT_MQTT_CLIENTS = new HashMap<>();
	
	private static AmazonS3 AWS_S3_CLIENT = null;
	private static AmazonRekognition AWS_REKOGNITION_CLIENT = null;

	private static volatile AWSCredentials basicCredentials = null;
	private static volatile AWSCredentialsProvider credentialsProvider = null;
	private static volatile AWSIot awsIot = null;
	private static volatile AmazonSQS awsSQS = null;
	// private static volatile User user = null;
	private static volatile AmazonKinesis kinesis = null;
	private static String region = null;
	private static final Object LOCK = new Object();

	public static Map<String, Object> getClientInfoAsService() throws Exception {
		return FacilioService.runAsServiceWihReturn(() -> getClientInfo());
	}

	public static Map<String, Object> getClientInfo() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs;
		String clientVersion = null;
		boolean isNewClientBuild=false;
				
		try {
			if(FacilioProperties.getEnvironment() != null ) {
				conn = FacilioConnectionPool.INSTANCE.getConnection();
				pstmt = conn.prepareStatement("SELECT * FROM ClientApp WHERE environment=?");
				pstmt.setString(1, FacilioProperties.getEnvironment());
				rs = pstmt.executeQuery();
				if (rs.next()) {
					clientVersion = rs.getString("version");
					isNewClientBuild=rs.getBoolean("is_new_client_build");
					
				}
			}
		} catch(SQLException | RuntimeException e) {
			LOGGER.info("Exception while verifying password, ", e);
		} finally {
			DBUtil.closeAll(conn, pstmt);
		}
		Map<String, Object> clientInfo=new HashMap<String, Object>();
		clientInfo.put("version",clientVersion);
		clientInfo.put("isNewClientBuild", isNewClientBuild);
		return clientInfo;
	}

	public static int updateClientVersion(String newVersion, boolean isNewClientBuild) throws Exception {
		com.facilio.accounts.dto.User currentUser = AccountUtil.getCurrentUser();
		if (currentUser != null) {
			return FacilioService.runAsServiceWihReturn(() -> updateClientVersionervice(newVersion, isNewClientBuild, currentUser.getId()));
		}
		else {
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

	public static JSONArray getPolicyGist() throws Exception {
		return AwsPolicyUtils.getAllPolicyGist(getIotClient());
	}

	private static int updateClientVersionervice(String newVersion,boolean isNewClientBuild, long userId) throws SystemException {
		int updatedRows = 0;
		if(newVersion != null) {
			newVersion = newVersion.trim();
			newVersion = newVersion.replace("/", "");
			Connection conn = null;
			PreparedStatement pstmt = null;
			try {
				if(checkIfVersionExistsInS3(newVersion)) {
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
						if(updatedRows > 0) {
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
		if(staticBucket != null) {
			AmazonS3 s3Client = getAmazonS3Client();
			objectExists = s3Client.doesObjectExist(staticBucket, newVersion+"/js/app.js");
		}
		return objectExists;
		
	}

	public static CreateKeysAndCertificateResult getCertificateResult()
    {
    	AWSIot awsIot = AWSIotClientBuilder.standard().withCredentials(InstanceProfileCredentialsProvider.createAsyncRefreshingProvider(false)).build();
    
    	CreateKeysAndCertificateRequest cr = new CreateKeysAndCertificateRequest().withSetAsActive(true);
    	CreateKeysAndCertificateResult certResult = awsIot.createKeysAndCertificate(cr);
    	
    	AttachPrincipalPolicyRequest policyResult = new AttachPrincipalPolicyRequest().withPolicyName("EM-Policy").withPrincipal(certResult.getCertificateArn());
    	awsIot.attachPrincipalPolicy(policyResult);
		return certResult;
    }
    
    public static AWSIotMqttClient getAwsIotMqttClient(String clientId) throws AWSIotException
    {
    	if(AWS_IOT_MQTT_CLIENTS.containsKey(clientId))
    	{
    		return AWS_IOT_MQTT_CLIENTS.get(clientId);
    	}
    	AWSIotMqttClient awsIotClient = new AWSIotMqttClient(FacilioProperties.getConfig("iot.endpoint"), clientId, FacilioProperties.getConfig(AwsUtil.AWS_ACCESS_KEY_ID), FacilioProperties.getConfig(AwsUtil.AWS_SECRET_KEY_ID));
    	awsIotClient.connect();
    	AWS_IOT_MQTT_CLIENTS.put(clientId, awsIotClient);
		return awsIotClient;
    }
    
    public static AmazonS3 getAmazonS3Client() {
    	if (AWS_S3_CLIENT == null) {
    		ClientConfiguration configuration = new ClientConfiguration().withMaxConnections(100).withConnectionTimeout(3000).withMaxErrorRetry(3);
        	AWS_S3_CLIENT = AmazonS3ClientBuilder.standard().withRegion(region).withCredentials(getAWSCredentialsProvider()).withClientConfiguration(configuration).build();
    	}
    	return AWS_S3_CLIENT;
    }
    
    public static AmazonRekognition getAmazonRekognitionClient() {
    	if (AWS_REKOGNITION_CLIENT == null) {
    		AWS_REKOGNITION_CLIENT = AmazonRekognitionClientBuilder.standard().withRegion(region).withCredentials(getAWSCredentialsProvider()).build();
    	}
    	return AWS_REKOGNITION_CLIENT;
    }
    
    public static String getSignature(String payload, String xAmzDate, String path) throws Exception
    {
		String secretKey = FacilioProperties.getConfig("secretKeyId");
        String dateStamp = new SimpleDateFormat("yyyyMMdd").format(new Date()); 	//"20170525";
        String regionName = FacilioProperties.getConfig("region");		//"us-west-2";

		byte[] kSecret = ("AWS4" + secretKey).getBytes(StandardCharsets.UTF_8);
        byte[] kDate = HmacSHA256(dateStamp, kSecret);
        byte[] kRegion = HmacSHA256(regionName, kDate);
        byte[] kService = HmacSHA256(AwsUtil.AWS_IOT_SERVICE_NAME, kRegion);
        byte[] kSigning = HmacSHA256("aws4_request", kService);
        
        String stringToSign = getStringToSign(payload, xAmzDate, path);
        return bytesToHex(HmacSHA256(stringToSign, kSigning));
    }
    
    public static String getStringToSign(String payload, String xAmzDate, String path) throws NoSuchAlgorithmException
    {
    	String canonicalHeader = "content-type:application/json\nhost:" + FacilioProperties.getConfig("host") + "\nx-amz-date:"+xAmzDate+"\n";
        String signedHeader = "content-type;host;x-amz-date";
        String canonicalRequest = "POST" + "\n" + path + "\n" + "" + "\n" + canonicalHeader + "\n" + signedHeader + "\n" + hash256(payload).toLowerCase();
        
        String scope = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + FacilioProperties.getConfig("region") + "/"+ AwsUtil.AWS_IOT_SERVICE_NAME + "/aws4_request";
        return "AWS4-HMAC-SHA256" + "\n" + xAmzDate + "\n" + scope + "\n" + hash256(canonicalRequest).toLowerCase();
    }
    
    public static Map<String, String> getAuthHeaders(String signature, String xAmzDate)
    {
    	Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("Host", FacilioProperties.getConfig("host"));
        headers.put("X-Amz-Date", xAmzDate);
        headers.put("Authorization", "AWS4-HMAC-SHA256 Credential=" + FacilioProperties.getConfig("accessKeyId") + "/" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + FacilioProperties.getConfig("region") + "/" + AwsUtil.AWS_IOT_SERVICE_NAME + "/aws4_request, SignedHeaders=content-type;host;x-amz-date, Signature=" + signature);
        return headers;
    }
    
    public static CloseableHttpClient getHttpClient(int timeOutInSeconds)
    {
    	if(timeOutInSeconds!=-1L)
    	{
    		RequestConfig config = RequestConfig.custom()
    		    	  .setSocketTimeout(timeOutInSeconds * 1000).build();
    		return HttpClientBuilder.create().setDefaultRequestConfig(config).build();
    	}
  
   		 return HttpClients.createDefault();
    }
    
    public static String doHttpPost(String url,Map<String,String> headers,Map<String,String> params,String bodyContent,int timeOutInSeconds) throws IOException
    {
    	StringBuilder result = new StringBuilder();
    	
    	CloseableHttpClient client = AwsUtil.getHttpClient(timeOutInSeconds);
    	
    	try
    	{
			HttpPost post = new HttpPost(url);
			if(headers != null)
			{
				for (String key : headers.keySet()) {
					String value = headers.get(key);
					post.setHeader(key, value);
				}
			}
			if(bodyContent != null)
			{
			    HttpEntity entity = new ByteArrayEntity(bodyContent.getBytes(StandardCharsets.UTF_8));
			    post.setEntity(entity);
			}		    
		    if(params != null)
		    {
		    	List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
				for (String key : params.keySet()) {
					String value = params.get(key);
					postParameters.add(new BasicNameValuePair(key, value));
				}
		        post.setEntity(new UrlEncodedFormEntity(postParameters));
		    }
			
		    CloseableHttpResponse response = client.execute(post);
		    int status = response.getStatusLine().getStatusCode();
		    if(status != 200) {
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
    public static String doHttpPost(String url, Map<String, String> headers, Map<String, String> params, String bodyContent) throws IOException
    {
    	return AwsUtil.doHttpPost(url, headers, params, bodyContent,-1);
    }
    
    private static byte[] HmacSHA256(String data, byte[] key) throws Exception
	{
        String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
    }
	
	private static String hash256(String data) throws NoSuchAlgorithmException
	{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }
	
	private static String bytesToHex(byte[] bytes)
	{
        StringBuilder result = new StringBuilder();
        for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }
	@Deprecated
	public static void sendEmail(JSONObject mailJson) throws Exception  {
		if(FacilioProperties.isDevelopment()) {
			mailJson.put("subject", "Local - "+mailJson.get("subject"));
			logEmail(mailJson);
			return;
		}
		if(FacilioProperties.isSmtp()) {
			EmailUtil.sendEmail(mailJson);
		} else {
			sendEmailViaAws(mailJson);
		}
		logEmail(mailJson);
	}

	private static void sendEmailViaAws(JSONObject mailJson) throws Exception  {
		String toAddress = (String)mailJson.get("to");
		String ccAddress = (String)mailJson.get("cc");
		String bccAddress = (String)mailJson.get("bcc");
		boolean sendEmail = true;
		HashSet<String> to = new HashSet<>();
		HashSet<String> cc = new HashSet<>();
		HashSet<String> bcc = new HashSet<>();
		if( !FacilioProperties.isProduction() ) {
			if(toAddress != null) {
				for(String address : toAddress.split(",")) {
					if(address.contains("@facilio.com")) {
						to.add(address);
					}
				}
				if (ccAddress != null && StringUtils.isNotEmpty(ccAddress)) {
					for(String address : ccAddress.split(",")) {
						if(address.contains("@facilio.com")) {
							cc.add(address);
						}
					}
				}
				if (bccAddress != null && StringUtils.isNotEmpty(bccAddress)) {
					for(String address : bccAddress.split(",")) {
						if(address.contains("@facilio.com")) {
							bcc.add(address);
						}
					}
				}
				if(to.size() == 0 ) {
					sendEmail = false;
				}
			} else {
				sendEmail = false;
			}
		} else {
			for(String address : toAddress.split(",")) {
				if(address!= null && address.contains("@")) {
					to.add(address);
				}
			}
		}
		if(sendEmail && to.size() > 0) {
			sendMailViaMessage(mailJson, to, cc, bcc);
		}
	}

	public static void sendMailViaMessage(JSONObject mailJson,HashSet<String> to, HashSet<String> cc, HashSet<String> bcc) {

		Destination destination = new Destination().withToAddresses(to);
		if (CollectionUtils.isNotEmpty(cc)) {
			destination.withCcAddresses(cc);
		}
		if (CollectionUtils.isNotEmpty(bcc)) {
			destination.withBccAddresses(bcc);
		}
		Content subjectContent = new Content().withData((String) mailJson.get("subject"));
		Content bodyContent = new Content().withData((String) mailJson.get("message"));

		Body body = null;
		if(mailJson.get("mailType") != null && mailJson.get("mailType").equals("html")) {
			body = new Body().withHtml(bodyContent);
		}
		else {
			body = new Body().withText(bodyContent);
		}

		Message message = new Message().withSubject(subjectContent).withBody(body);

		try {
			SendEmailRequest request = new SendEmailRequest().withSource((String) mailJson.get("sender"))
					.withDestination(destination).withMessage(message);
			AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
					.withRegion(Regions.US_WEST_2).withCredentials(getAWSCredentialsProvider()).build();
			client.sendEmail(request);
			if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 151) {
				LOGGER.info("Email sent to "+to.toString()+"\n"+mailJson);
			}
		} catch (Exception ex) {
			LOGGER.info("Error message: " + to.toString() + " " + ex.getMessage());
			throw ex;
		}
	}

	private static void logEmail (JSONObject mailJson) throws Exception {

		try {
			String toAddress = (String) mailJson.get("to");
			if (!"error+alert@facilio.com".equals(toAddress) && !"error@facilio.com".equals(toAddress)) {
				toAddress = toAddress == null ? "" : toAddress;
				JSONObject info = new JSONObject();
				info.put("subject", mailJson.get("subject"));
				if (mailJson.get("cc") != null) {
					info.put("cc", mailJson.get("cc"));
				}
				if (mailJson.get("bcc") != null) {
					info.put("bcc", mailJson.get("bcc"));
				}
				CommonAPI.addNotificationLogger(NotificationType.EMAIL, toAddress, info);
			}
		}
		catch (Exception e) {
			LOGGER.error("Error occurred while logging email", e);
		}
	}
	@Deprecated
	public static void sendEmail(JSONObject mailJson, Map<String,String> files) throws Exception  {

		FacilioFactory.getEmailClient().sendEmail(mailJson,files);
		if(files == null || files.isEmpty()) {
			sendEmail(mailJson);
			return;
		}
		logEmail(mailJson);
		if(FacilioProperties.isSmtp()) {
			EmailUtil.sendEmail(mailJson, files);
		} else {
			sendEmailViaAws(mailJson, files);
		}
	}

	private static void sendEmailViaAws(JSONObject mailJson, Map<String,String> files) throws Exception  {
		if(files == null || files.isEmpty()) {
			sendEmail(mailJson);
			return;
		}
		String toAddress = (String)mailJson.get("to");
		HashSet<String> to = new HashSet<>();
		boolean sendEmail = true;
		if( !FacilioProperties.isProduction() ) {
			if(toAddress != null) {
				for(String address : toAddress.split(",")) {
					if(address.contains("@facilio.com")) {
						to.add(address);
					}
				}
			} else {
				sendEmail = false;
			}
		} else {
			for(String address : toAddress.split(",")) {
				if(address != null && address.contains("@")) {
					to.add(address);
				}
			}
		}
		if(sendEmail && to.size() > 0) {
			try {
				if (FacilioProperties.isDevelopment()) {
//					mailJson.put("subject", "Local - " + mailJson.get("subject"));
					return;
				}
				sendEmailViaMimeMessage(mailJson, files);
				
				if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 151) {
					LOGGER.info("Email sent to "+toAddress+"\n"+mailJson);
				}
				
			} catch (Exception ex) {
				LOGGER.info("The email was not sent.");
				LOGGER.info("Error message: " + toAddress+" " + ex.getMessage());
				throw ex;
			}
		}
	}

	public static void sendEmailViaMimeMessage(JSONObject mailJson, Map<String, String> files) throws Exception {
		MimeMessage message = getEmailMessage(mailJson, files);
		try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
			message.writeTo(outputStream);
			RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
			SendRawEmailRequest request = new SendRawEmailRequest(rawMessage);
			AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
					.withRegion(Regions.US_WEST_2).withCredentials(getAWSCredentialsProvider()).build();
			client.sendRawEmail(request);
		}
	}

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
    				region = FacilioProperties.getConfig("region");
				}
			}
		}
    	return region;
	}

	private static AWSIot getIotClient() {
    	if(awsIot == null) {
    		synchronized (LOCK) {
    			if(awsIot == null) {
    				awsIot = AWSIotClientBuilder.standard()
							.withCredentials(getAWSCredentialsProvider())
							.withRegion(getRegion()).build();
				}
			}
		}
		return awsIot;
	}

	static  String getIotArn(){
		return "arn:aws:iot:" + getRegion() + ":" + getUserId();
	}

	public static AmazonSQS getSQSClient() {
		if(awsSQS == null) {
			synchronized (LOCK) {
				if(awsSQS == null) {
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

	public static String createIotPolicy(String clientName, String domain, String type) throws Exception {
		IotPolicy policy = AwsPolicyUtils.createOrUpdateIotPolicy(clientName, domain, type, getIotClient());
		return policy.getPolicyDocument().toString();
	}

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

	public static AmazonKinesis getKinesisClient() {
		if (kinesis == null) {
			synchronized (LOCK) {
				if (kinesis == null) {
    				kinesis = AmazonKinesisClientBuilder.standard()
							.withCredentials(getAWSCredentialsProvider())
							.withRegion(getRegion())
							.build();
				}
			}
		}
		return kinesis;
	}

	private static void createKinesisStream(AmazonKinesis kinesisClient, String streamName) {
		LOGGER.info(" creating kenisis stream " + streamName);
		Objects.requireNonNull(kinesisClient, " kinesis client can't be null");
		Objects.requireNonNull(streamName, "stream name can't be null");
		try {
			CreateStreamResult streamResult = kinesisClient.createStream(streamName, 1);
			LOGGER.info("Stream created : " + streamName + " with status " + streamResult.getSdkHttpMetadata().getHttpStatusCode());
		} catch (ResourceInUseException resourceInUse) {
			LOGGER.info(" Exception Stream exists for name : " + streamName);
		}
	}


	private static void createIotTopicRule(AWSIot iotClient, String policyAndOrgDomainName) {
		Objects.requireNonNull(iotClient, "iotClient null");
		Objects.requireNonNull(policyAndOrgDomainName, "policyAndOrgDomainName null");
		LOGGER.info(" creating iot rule ");
		try {
			KinesisAction kinesisAction = new KinesisAction().withStreamName(policyAndOrgDomainName)
					.withPartitionKey(KINESIS_PARTITION_KEY)
					.withRoleArn(IAM_ARN_PREFIX + getUserId() + KINESIS_PUT_ROLE_SUFFIX);

			Action action = new Action().withKinesis(kinesisAction);

			TopicRulePayload rulePayload = new TopicRulePayload()
					.withActions(action)
					.withSql("SELECT * FROM '" + policyAndOrgDomainName + "'")
					.withAwsIotSqlVersion(IOT_SQL_VERSION); //Refer the versions available in AWS iot sql version document before changing.

			CreateTopicRuleRequest topicRuleRequest = new CreateTopicRuleRequest().withRuleName(policyAndOrgDomainName).withTopicRulePayload(rulePayload);

			CreateTopicRuleResult topicRuleResult = iotClient.createTopicRule(topicRuleRequest);

			LOGGER.info("Topic Rule created : " + topicRuleResult.getSdkHttpMetadata().getHttpStatusCode());
		} catch (ResourceAlreadyExistsException resourceExists) {
			LOGGER.info("Topic Rule already exists for name : " + policyAndOrgDomainName);
		}


	}


	private static CreateKeysAndCertificateResult createIotToKinesis(String clientName, String policyAndOrgDomainName, String type) throws Exception {
		Objects.requireNonNull(policyAndOrgDomainName, "policyAndOrgDomainName can't be null");
		Objects.requireNonNull(type, " type can't be null");
		LOGGER.info(" create Iot Kenesis " + policyAndOrgDomainName);
		AWSIot iotClient = null;
		iotClient = getIotClient();
		IotPolicy policy;
		policy = AwsPolicyUtils.createOrUpdateIotPolicy(clientName, policyAndOrgDomainName, type, iotClient);
		CreateKeysAndCertificateResult certificateResult = createCertificate(iotClient);
		attachPolicy(iotClient, certificateResult, policyAndOrgDomainName);
		createKinesisStream(getKinesisClient(), policyAndOrgDomainName);
		// Creating topic in kafka
		MessageQueueFactory.getMessageQueue().createQueue(policyAndOrgDomainName);
		policy.setStreamName(policyAndOrgDomainName);
		createIotTopicRule(iotClient, policyAndOrgDomainName);
		return certificateResult;
	}


	public static CreateKeysAndCertificateResult signUpIotToKinesis(String orgDomainName, String clientName, String type) throws Exception {
		LOGGER.info(" signing up to kinesis policyname " + orgDomainName);
		return AwsUtil.createIotToKinesis(clientName, orgDomainName, type);
	}

	public static String getIotKinesisTopic(String orgDomainName) {
		return orgDomainName;
	}

	@Deprecated
	public static void sendErrorMail(long orgid, long ml_id, String error) {
		try
		{
			JSONObject json = new JSONObject();
			json.put("sender", "mlerror@facilio.com");
			json.put("to", "ai@facilio.com");
			json.put("subject", orgid+" - "+ml_id);
			
			StringBuilder body = new StringBuilder()
									.append(error)
									.append("\n\nInfo : \n--------\n")
									.append("\n Org Time : ").append(DateTimeUtil.getDateTime())
									.append("\n Indian Time : ").append(DateTimeUtil.getDateTime(ZoneId.of("Asia/Kolkata")))
									.append("\n\nMsg : ")
									.append(error)
									.append("\n\nOrg Info : \n--------\n")
									.append(orgid)
									;
			json.put("message", body.toString());
			
			sendEmail(json);
		}
		catch(Exception e)
		{
			LOGGER.error("Error while sending mail ",e);
		}
	}

	private static MimeMessage getEmailMessage(JSONObject mailJson, Map<String, String> files) throws Exception {
		Session session = Session.getDefaultInstance(new Properties());
		MimeMessage message = EmailClient.constructMimeMessageContent(mailJson, session, files);
		message.addHeader("host", FacilioProperties.getAppDomain());
		return message;
	}

	public static void addClientToPolicy(String agentName, String policyName, String type) throws Exception {
		AwsPolicyUtils.createOrUpdateIotPolicy(agentName, policyName, type, getIotClient());
	}
}