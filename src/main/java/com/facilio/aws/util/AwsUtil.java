package com.facilio.aws.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

import com.amazonaws.services.iot.client.*;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.services.iot.AWSIotClientBuilder;
import com.amazonaws.services.iot.model.Action;
import com.amazonaws.services.iot.model.AttachPolicyRequest;
import com.amazonaws.services.iot.model.AttachPolicyResult;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.amazonaws.services.iot.model.CreatePolicyRequest;
import com.amazonaws.services.iot.model.CreatePolicyResult;
import com.amazonaws.services.iot.model.CreatePolicyVersionRequest;
import com.amazonaws.services.iot.model.CreatePolicyVersionResult;
import com.amazonaws.services.iot.model.CreateTopicRuleRequest;
import com.amazonaws.services.iot.model.CreateTopicRuleResult;
import com.amazonaws.services.iot.model.GetPolicyRequest;
import com.amazonaws.services.iot.model.GetPolicyResult;
import com.amazonaws.services.iot.model.KinesisAction;
import com.amazonaws.services.iot.model.ResourceAlreadyExistsException;
import com.amazonaws.services.iot.model.TopicRulePayload;
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
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.RawMessage;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;
import com.amazonaws.services.simpleemail.model.SendRawEmailRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.sql.DBUtil;
import com.facilio.transaction.FacilioConnectionPool;

public class AwsUtil 
{

	private static final Logger LOGGER = LogManager.getLogger(AwsUtil.class.getName());
	
	private static final String AWS_PROPERTY_FILE = "conf/awsprops.properties";
	
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

	private static final Properties PROPERTIES = new Properties();

	private static final String SERVERNAME = getConfig("servername");
	private static final String USER_ID="668907905219";

	private static boolean productionEnvironment = false;
	private static boolean developmentEnvironment = true;

	static {
		loadProperties();
	}

	private static void loadProperties() {
		URL resource = AwsUtil.class.getClassLoader().getResource(AWS_PROPERTY_FILE);
		if (resource != null) {
			try (InputStream stream = resource.openStream()) {
				PROPERTIES.load(stream);
				String environment = PROPERTIES.getProperty("environment");
				productionEnvironment = "production".equalsIgnoreCase(environment);
				developmentEnvironment = "development".equalsIgnoreCase(environment);
			} catch (IOException e) {
				LOGGER.info("Exception while trying to load property file " + AWS_PROPERTY_FILE);
			}
		}
	}

	public static String getConfig(String name) {
    	String value = PROPERTIES.getProperty(name);
        if (value != null ) {
        	value = value.trim();
        	if(value.length() > 0) {
        		return value;
        	}
        }
        return null;
    }

	public static String getClientVersion() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs;
		String clientVersion = null;
		try {
			String environment = getConfig("environment");
			if( environment != null ) {
				conn = FacilioConnectionPool.INSTANCE.getConnection();
				pstmt = conn.prepareStatement("SELECT version FROM ClientApp WHERE environment=?");
				pstmt.setString(1, environment);
				rs = pstmt.executeQuery();
				if (rs.next()) {
					clientVersion = rs.getString("version");
				}
			}
		} catch(SQLException | RuntimeException e) {
			LOGGER.info("Exception while verifying password, ", e);
		} finally {
			DBUtil.closeAll(conn, pstmt);
		}
		return clientVersion;
	}

	public static int updateClientVersion(String newVersion) {
		int updatedRows = 0;
		if(newVersion != null) {
			newVersion = newVersion.trim();
			newVersion = newVersion.replace("/", "");
			Connection conn = null;
			PreparedStatement pstmt = null;
			try {
				if(checkIfVersionExistsInS3(newVersion)) {
					String environment = getConfig("environment");
					com.facilio.accounts.dto.User currentUser = AccountUtil.getCurrentUser();
					if (environment != null && currentUser != null) {
						conn = FacilioConnectionPool.INSTANCE.getConnection();
						pstmt = conn.prepareStatement("Update ClientApp set version=?, updatedTime=?, updatedBy=? WHERE environment=?");
						pstmt.setString(1, newVersion);
						pstmt.setLong(2, System.currentTimeMillis());
						pstmt.setLong(3, currentUser.getId());
						pstmt.setString(4, environment);
						updatedRows = pstmt.executeUpdate();
						if(updatedRows > 0) {
						    LOGGER.info("Updated client version successfully");
                        }
					}
				}
			} catch (SQLException | RuntimeException e) {
				LOGGER.info("Exception while verifying password, ", e);
			} finally {
				DBUtil.closeAll(conn, pstmt);
			}
		}
		return updatedRows;
	}

	private static boolean checkIfVersionExistsInS3(String newVersion) {
		boolean objectExists = false;
		String staticBucket = AwsUtil.getConfig("static.bucket");
		if(staticBucket != null) {
			AmazonS3 s3Client = getAmazonS3Client();
			objectExists = s3Client.doesObjectExist(staticBucket, newVersion+"/app.css");
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
    	AWSIotMqttClient awsIotClient = new AWSIotMqttClient(AwsUtil.getConfig("iot.endpoint"), clientId, AwsUtil.getConfig(AwsUtil.AWS_ACCESS_KEY_ID), AwsUtil.getConfig(AwsUtil.AWS_SECRET_KEY_ID));
    	awsIotClient.connect();
    	AWS_IOT_MQTT_CLIENTS.put(clientId, awsIotClient);
		return awsIotClient;
    }
    
    public static AmazonS3 getAmazonS3Client() {
    	if (AWS_S3_CLIENT == null) {
        	AWS_S3_CLIENT = AmazonS3ClientBuilder.standard().withRegion(region).withCredentials(getAWSCredentialsProvider()).build();
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
		String secretKey = AwsUtil.getConfig("secretKeyId");
        String dateStamp = new SimpleDateFormat("yyyyMMdd").format(new Date()); 	//"20170525";
        String regionName = AwsUtil.getConfig("region");		//"us-west-2";

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
    	String canonicalHeader = "content-type:application/json\nhost:" + AwsUtil.getConfig("host") + "\nx-amz-date:"+xAmzDate+"\n";
        String signedHeader = "content-type;host;x-amz-date";
        String canonicalRequest = "POST" + "\n" + path + "\n" + "" + "\n" + canonicalHeader + "\n" + signedHeader + "\n" + hash256(payload).toLowerCase();
        
        String scope = new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + AwsUtil.getConfig("region") + "/"+ AwsUtil.AWS_IOT_SERVICE_NAME + "/aws4_request";
        return "AWS4-HMAC-SHA256" + "\n" + xAmzDate + "\n" + scope + "\n" + hash256(canonicalRequest).toLowerCase();
    }
    
    public static Map<String, String> getAuthHeaders(String signature, String xAmzDate)
    {
    	Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        headers.put("Host", AwsUtil.getConfig("host"));
        headers.put("X-Amz-Date", xAmzDate);
        headers.put("Authorization", "AWS4-HMAC-SHA256 Credential=" + AwsUtil.getConfig("accessKeyId") + "/" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + "/" + AwsUtil.getConfig("region") + "/" + AwsUtil.AWS_IOT_SERVICE_NAME + "/aws4_request, SignedHeaders=content-type;host;x-amz-date, Signature=" + signature);
        return headers;
    }
    
    public static String doHttpPost(String url, Map<String, String> headers, Map<String, String> params, String bodyContent) throws IOException
    {
    	StringBuilder result = new StringBuilder();
    	CloseableHttpClient client = HttpClients.createDefault();
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
			LOGGER.info("\nSending 'POST' request to URL : " + url);
			LOGGER.info("Post parameters : " + post.getEntity());
			LOGGER.info("Response Code : " +  response.getStatusLine().getStatusCode());
	 
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) 
			{
				result.append(line);
			}
    	}
		catch (Exception e) 
    	{
			LOGGER.info("Executing doHttpPost ::::url:::" + url, e);
		} 
    	finally 
    	{
			client.close();
		}
    	return result.toString();
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
	
	public static void sendEmail(JSONObject mailJson) throws Exception 
	{
		String appUrl = AwsUtil.getConfig("app.url");
		if(appUrl == null || appUrl.contains("localhost")) {
//			mailJson.put("subject", "Local - "+mailJson.get("subject"));
			return;
		}

		String toAddress = (String)mailJson.get("to");
		boolean sendEmail = true;
		if( ! "production".equals(AwsUtil.getConfig("environment")) ) {
			if(toAddress != null) {
				String to = "";
				for(String address : toAddress.split(",")) {
					 if(address.contains("facilio.com")) {
						 to = address + ",";
					 }
				}
				if(to.length() == 0 ) {
					sendEmail = false;
				} else {
					toAddress = to;
				}
			} else {
				sendEmail = false;
			}
		}
		if(sendEmail) {
			Destination destination = new Destination().withToAddresses(toAddress.split("\\s*,\\s*"));
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
				if (AccountUtil.getCurrentOrg() != null && (AccountUtil.getCurrentOrg().getId() == 104 || AccountUtil.getCurrentOrg().getId() == 155)) {
					LOGGER.info("Sending email : "+mailJson.toJSONString());
				}
				SendEmailRequest request = new SendEmailRequest().withSource((String) mailJson.get("sender"))
						.withDestination(destination).withMessage(message);
				AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
						.withRegion(Regions.US_WEST_2).withCredentials(getAWSCredentialsProvider()).build();
				client.sendEmail(request);
				LOGGER.info("Email sent!");
				if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 104) {
					LOGGER.info(mailJson.toJSONString());
				}
			} catch (Exception ex) {
				LOGGER.info("Error message: " + ex.getMessage());
				throw ex;
			}
		}
	}
	
	public static void sendEmail(JSONObject mailJson, Map<String,String> files) throws Exception 
	{
		if(files == null || files.isEmpty()) {
			sendEmail(mailJson);
			return;
		}
		String toAddress = (String)mailJson.get("to");
		boolean sendEmail = true;
		if( ! isProduction()) {
			if(toAddress != null) {
				String to = "";
				for(String address : toAddress.split(",")) {
					if(address.contains("facilio.com")) {
						to = address + ",";
					}
				}
				if(to.length() == 0 ) {
					sendEmail = false;
				} else {
					mailJson.put("to", to);
				}
			} else {
				sendEmail = false;
			}
		}
		if(sendEmail) {
			try {
				if (AwsUtil.getConfig("app.url") == null || AwsUtil.getConfig("app.url").contains("localhost")) {
//					mailJson.put("subject", "Local - " + mailJson.get("subject"));
					return;
				}

				MimeMessage message = getEmailMessage(mailJson, files);
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
				message.writeTo(outputStream);
				RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
				SendRawEmailRequest request = new SendRawEmailRequest(rawMessage);

				AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
						.withRegion(Regions.US_WEST_2).withCredentials(getAWSCredentialsProvider()).build();
				client.sendRawEmail(request);
				LOGGER.info("Email sent!");
			} catch (Exception ex) {
				LOGGER.info("The email was not sent.");
				LOGGER.info("Error message: " + ex.getMessage());
				throw ex;
			}
		}
	}

	private static AWSCredentials getBasicAwsCredentials() {
		if(basicCredentials == null) {
			synchronized (LOCK) {
				if (basicCredentials == null) {
					basicCredentials = new BasicAWSCredentials(AwsUtil.getConfig(AWS_ACCESS_KEY_ID), AwsUtil.getConfig(AWS_SECRET_KEY_ID));
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
    				region = AwsUtil.getConfig("region");
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

	public static AmazonSQS getSQSClient() {
		if(awsSQS == null) {
			synchronized (LOCK) {
				if(awsSQS == null) {
					awsSQS = AmazonSQSClientBuilder.standard()
							.withCredentials(getAWSCredentialsProvider())
							.withRegion(getRegion()).build();
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
    	return USER_ID;
	}

	public static void publishIotMessage(String client, JSONObject message) throws Exception {
		// AWSCredentials awsCredentials = getAWSCredentialsProvider().getCredentials();
		LOGGER.info(AwsUtil.getConfig("iot.endpoint") +" " + client+"-facilio" + " " + client+"/msgs" + " " + message);
		// AWSIotMqttClient mqttClient = new AWSIotMqttClient(AwsUtil.getConfig("iot.endpoint"), client+"-facilio", awsCredentials.getAWSAccessKeyId(), awsCredentials.getAWSSecretKey());
		AWSIotMqttClient mqttClient = new AWSIotMqttClient(AwsUtil.getConfig("iot.endpoint"), client+"-facilio", "AKIAIVXNCI3T7TCHFGPA", "jK6uCU3HUhI06r6Udl0P2u6aMaNAvxfwhn+HR1Eg");
		try {
			mqttClient.connect();
			if(mqttClient.getConnectionStatus() == AWSIotConnectionStatus.CONNECTED) {
				mqttClient.publish(new AWSIotMessage(client + "/msgs", AWSIotQos.QOS0, message.toJSONString()));
			}
		} catch (AWSIotException e) {
			LOGGER.info("exception while publishing message ", e);
			throw e;
		} finally {
			try {
				if(mqttClient.getConnectionStatus() == AWSIotConnectionStatus.CONNECTED) {
					mqttClient.disconnect();
				}
			} catch (AWSIotException e) {
				LOGGER.info("exception while disconnecting ", e);
			}
		}
	}

	public static void addIotClient(String policyName, String clientId) {
    	try {
			AWSIot client = getIotClient();
			GetPolicyRequest request = new GetPolicyRequest().withPolicyName(policyName);
			GetPolicyResult result = client.getPolicy(request);
			JSONParser parser = new JSONParser();
			JSONObject object = (JSONObject) parser.parse(result.getPolicyDocument());

			JSONArray array = (JSONArray) object.get("Statement");
			List<String> clients = new ArrayList<>();
			for (int i = 0; i < array.size(); i++) {
				JSONObject stat = (JSONObject) array.get(i);
				String action = (String) stat.get("Action");
				if ("iot:Connect".equalsIgnoreCase(action)) {
					JSONArray resourceArray = (JSONArray) stat.get("Resource");
					for (int j = 0; j < resourceArray.size(); j++) {
						clients.add((String) resourceArray.get(j));
					}
					break;
				}
			}
			clients.add(getIotArnClientId(clientId));
			CreatePolicyVersionRequest versionRequest = new CreatePolicyVersionRequest().withPolicyName(policyName)
					.withPolicyDocument(getPolicyDoc(policyName, clients.toArray(new String[]{})).toString())
					.withSetAsDefault(true);
			CreatePolicyVersionResult versionResult = client.createPolicyVersion(versionRequest);
			LOGGER.info("Policy updated for " + policyName + ", with " + versionResult.getPolicyDocument() + ", status: " + versionResult.getSdkHttpMetadata().getHttpStatusCode());
		} catch (Exception e){
    		LOGGER.info("Error ",e);
		}
	}

	private static String getIotArnClientId(String clientId){
    	return getIotArn() + ":client/" + clientId;
	}

	private static String getIotArnTopic(String topic) {
    	return getIotArn() +":topic/"+ topic;
	}

	private static JSONObject getPolicyInJson(String action, String[] resource){
		JSONObject object = new JSONObject();
		object.put("Effect", "Allow");
		object.put("Action", action);
		JSONArray array = new JSONArray();
		for(String str : resource) {
			array.add(str);
		}
		object.put("Resource", array);
		return object;
	}

	private static JSONObject getPolicyDoc(String name, String[] clientIds ){
		JSONArray statements = new JSONArray();
		statements.add(getPolicyInJson("iot:Connect", clientIds));
		statements.add(getPolicyInJson("iot:Publish", new String[] {getIotArnTopic(name)}));
		JSONObject policyDocument = new JSONObject();
		policyDocument.put("Version", "2012-10-17"); //Refer the versions available in AWS policy document before changing.
		policyDocument.put("Statement", statements);

		return policyDocument;
	}

	private static  String getIotArn(){
		return "arn:aws:iot:" + getRegion() + ":" + getUserId();
	}

	private static void createIotPolicy(AWSIot iotClient, String name) {
    	try {
			CreatePolicyRequest policyRequest = new CreatePolicyRequest().withPolicyName(name).withPolicyDocument(getPolicyDoc(name, new String[] { getIotArnClientId(name)}).toString());
			CreatePolicyResult policyResult = iotClient.createPolicy(policyRequest);
			LOGGER.info("Policy created : " + policyResult.getPolicyArn() + " version " + policyResult.getPolicyVersionId());
		} catch (ResourceAlreadyExistsException resourceExists){
    		LOGGER.info("Policy already exists for name : " + name);
		}
	}

	private static CreateKeysAndCertificateResult createCertificate(AWSIot iotClient){
		CreateKeysAndCertificateRequest certificateRequest = new CreateKeysAndCertificateRequest().withSetAsActive(true);
		return iotClient.createKeysAndCertificate(certificateRequest);
	}

	private static void attachPolicy(AWSIot iotClient, CreateKeysAndCertificateResult certificateResult, String policyName){
		AttachPolicyRequest attachPolicyRequest = new AttachPolicyRequest().withPolicyName(policyName).withTarget(certificateResult.getCertificateArn());
		AttachPolicyResult attachPolicyResult = iotClient.attachPolicy(attachPolicyRequest);
		LOGGER.info("Attached policy : " + attachPolicyResult.getSdkHttpMetadata().getHttpStatusCode());
	}

	public static AmazonKinesis getKinesisClient() {
    	if(kinesis == null) {
    		synchronized (LOCK) {
    			if(kinesis == null) {
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
    	try {
			CreateStreamResult streamResult = kinesisClient.createStream(streamName, 1);
			LOGGER.info("Stream created : " + streamName + " with status " + streamResult.getSdkHttpMetadata().getHttpStatusCode());
		} catch (ResourceInUseException resourceInUse){
    		LOGGER.info("Stream exists for name : " + streamName);
		}
	}

	private static void createIotTopicRule(AWSIot iotClient, String topicName) {
    	try {
			KinesisAction kinesisAction = new KinesisAction().withStreamName(topicName)
					.withPartitionKey(KINESIS_PARTITION_KEY)
					.withRoleArn(IAM_ARN_PREFIX + getUserId() + KINESIS_PUT_ROLE_SUFFIX);

			Action action = new Action().withKinesis(kinesisAction);

			TopicRulePayload rulePayload = new TopicRulePayload()
					.withActions(action)
					.withSql("SELECT * FROM '" + topicName + "'")
					.withAwsIotSqlVersion(IOT_SQL_VERSION); //Refer the versions available in AWS iot sql version document before changing.

			CreateTopicRuleRequest topicRuleRequest = new CreateTopicRuleRequest().withRuleName(topicName).withTopicRulePayload(rulePayload);

			CreateTopicRuleResult topicRuleResult = iotClient.createTopicRule(topicRuleRequest);

			LOGGER.info("Topic Rule created : " + topicRuleResult.getSdkHttpMetadata().getHttpStatusCode());
		} catch (ResourceAlreadyExistsException resourceExists ){
    		LOGGER.info("Topic Rule already exists for name : " + topicName);
		}
	}

	private static CreateKeysAndCertificateResult createIotToKinesis(String name){
    	AWSIot iotClient = getIotClient();
    	createIotPolicy(iotClient, name);
    	CreateKeysAndCertificateResult certificateResult = createCertificate(iotClient);
    	attachPolicy(iotClient, certificateResult, name);
    	createKinesisStream(getKinesisClient(), name);
    	// createKinesisStream(getKinesisClient(), name+"-error");
    	createIotTopicRule(iotClient, name);
    	return certificateResult;
	}

	public static CreateKeysAndCertificateResult signUpIotToKinesis(String orgDomainName){
		String name = getIotKinesisTopic(orgDomainName);
		return AwsUtil.createIotToKinesis(name);
	}

	public static String getIotKinesisTopic(String orgDomainName){
    	return orgDomainName;
	}
	
	
	private static MimeMessage getEmailMessage(JSONObject mailJson, Map<String,String> files) throws Exception {
	 	String DefaultCharSet = MimeUtility.getDefaultJavaCharset();
	 	
		String sender = (String) mailJson.get("sender");
		
		Session session = Session.getDefaultInstance(new Properties());
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(sender));
	    message.setRecipients(javax.mail.Message.RecipientType.TO, InternetAddress.parse((String) mailJson.get("to")));
	    message.setSubject((String) mailJson.get("subject"));
	    
	    MimeMultipart messageBody = new MimeMultipart("alternative");
	    MimeBodyPart textPart = new MimeBodyPart();
	    textPart.setContent(MimeUtility.encodeText((String) mailJson.get("message"),DefaultCharSet,"B"), "text/plain; charset=UTF-8");
        textPart.setHeader("Content-Transfer-Encoding", "base64");
        messageBody.addBodyPart(textPart);
        
        MimeBodyPart wrap = new MimeBodyPart();
        wrap.setContent(messageBody);
	    
	    	MimeMultipart messageContent = new MimeMultipart("mixed");
	    messageContent.addBodyPart(wrap);
	    
	    for (Map.Entry<String, String> file : files.entrySet()) {
	    		String fileUrl = file.getValue();
	    		if(fileUrl == null) {	// Temporary check for local filestore.
	    			continue;
	    		}
	    		MimeBodyPart attachment = new MimeBodyPart();
	    		DataSource fileDataSource = null;
	    		if (isDevelopment()) {
	    			fileDataSource = new FileDataSource(fileUrl);
	    		} else {
	    			URL url = new URL(fileUrl);
	    			fileDataSource = new URLDataSource(url);
	    		}
		    attachment.setDataHandler(new DataHandler(fileDataSource));
		    attachment.setFileName(file.getKey());
		    messageContent.addBodyPart(attachment);
	    }
	    
	    message.setContent(messageContent);
	    if(SERVERNAME!=null)
	    {
	       message.addHeader("host", SERVERNAME);
	    }
	    return message;
	}

	public static boolean isProduction() {
		return productionEnvironment;
	}

	public static boolean isDevelopment() {
		return developmentEnvironment;
	}
}
