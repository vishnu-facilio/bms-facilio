package com.facilio.aws.util;

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
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.AgentKeys;
import com.facilio.agent.FacilioAgent;
import com.facilio.bmsconsole.util.CommonAPI;
import com.facilio.bmsconsole.util.CommonAPI.NotificationType;
import com.facilio.db.builder.DBUtil;
import com.facilio.db.transaction.FacilioConnectionPool;
import com.facilio.db.transaction.FacilioTransactionManager;
import com.facilio.email.EmailUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.json.simple.parser.JSONParser;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.activation.URLDataSource;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.Session;
import javax.mail.internet.*;
import javax.transaction.SystemException;
import java.io.*;
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
import java.util.*;

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

	private static boolean productionEnvironment = false;
	private static boolean developmentEnvironment = true;
	private static boolean disableCSP = false;
	private static boolean scheduleServer = false;
	private static boolean isSmtp = false;

	private static String db;
	private static String dbClass;
	private static boolean userServer = true;
	private static boolean messageProcessor = false;
	private static String appDomain;
	private static String clientAppUrl;
	private static String pushNotificationKey;
	private static String portalPushNotificationKey;
	private static String environment;
	private static String kafkaProducer;
	private static String kafkaConsumer;
	private static String pdfjs;
	private static String anomalyTempDir;
	private static String anomalyCheckServiceURL;
	private static String anomalyBucket;
	private static String anomalyBucketDir;
	private static String anomalyPeriodicity;
	private static String anomalyRefreshWaitTimeInSeconds;
	private static String anomalyDetectWaitTimeInSeconds;
	private static String anomalyPredictAPIURL;

	private static String iotEndPoint;
	public static String getIotEndPoint() { return iotEndPoint; }

	private static boolean sysLogEnabled;
    private static HashSet<String> dbIdentifiers = new HashSet<>();

    public static Long getMessageReprocessInterval() {
			return messageReprocessInterval;
	}
	private static Long messageReprocessInterval;

	static {
		loadProperties();
	}

	private static void loadProperties() {
		URL resource = AwsUtil.class.getClassLoader().getResource(AWS_PROPERTY_FILE);
		if (resource != null) {
			try (InputStream stream = resource.openStream()) {
				PROPERTIES.load(stream);
				PROPERTIES.forEach((k,v) -> PROPERTIES.put(k.toString().trim(), v.toString().trim()));
				environment = PROPERTIES.getProperty("environment");
				HashMap<String, String> awsSecret = getPassword(environment+"-app.properties");
				awsSecret.forEach((k,v) ->  PROPERTIES.put(k.trim(), v.trim()));
				productionEnvironment = "production".equalsIgnoreCase(environment);
				developmentEnvironment = "development".equalsIgnoreCase(environment);
				disableCSP = "true".equals(PROPERTIES.getProperty("onpremise", "false").trim());
				scheduleServer = "true".equals(AwsUtil.getConfig("schedulerServer"));
				messageProcessor = "true".equalsIgnoreCase(PROPERTIES.getProperty("messageProcessor"));
				userServer = ! scheduleServer;
				db = PROPERTIES.getProperty("db.name");
				dbClass = PROPERTIES.getProperty("db.class");
				appDomain = PROPERTIES.getProperty("app.domain");
				pushNotificationKey = PROPERTIES.getProperty("push.notification.key");
				portalPushNotificationKey = PROPERTIES.getProperty("portal.push.notification.key");
				clientAppUrl = "https://"+appDomain;
				kafkaProducer = PROPERTIES.getProperty("kafka.producer");
				kafkaConsumer = PROPERTIES.getProperty("kafka.consumer");
				isSmtp = "smtp".equalsIgnoreCase(PROPERTIES.getProperty("email.type"));
				anomalyTempDir = PROPERTIES.getProperty("anomalyTempDir", "/tmp");
				anomalyCheckServiceURL = PROPERTIES.getProperty("anomalyCheckServiceURL", "http://localhost:7444/api");
				anomalyBucket = PROPERTIES.getProperty("anomalyBucket","facilio-analytics");
				anomalyBucketDir = PROPERTIES.getProperty("anomalyBucketDir","stage/anomaly");
				anomalyPeriodicity = PROPERTIES.getProperty("anomalyPeriodicity","30");
				anomalyRefreshWaitTimeInSeconds = PROPERTIES.getProperty("anomalyRefreshWaitTimeInSeconds","10");
				anomalyDetectWaitTimeInSeconds = PROPERTIES.getProperty("anomalyDetectWaitTimeInSeconds","3");
				anomalyPredictAPIURL = PROPERTIES.getProperty("anomalyPredictServiceURL","http://localhost:7444/api");
				sysLogEnabled = "true".equals(PROPERTIES.getProperty("syslog.enabled", "false"));
				iotEndPoint = (String) PROPERTIES.get("iot.endpoint");
				messageReprocessInterval = Long.parseLong(PROPERTIES.getProperty(AgentKeys.MESSAGE_REPROCESS_INTERVAL,"300000"));
				PROPERTIES.put("clientapp.url", clientAppUrl);
				URL resourceDir = AwsUtil.class.getClassLoader().getResource("");
				if(resourceDir != null) {
					File file = new File(resourceDir.getPath());
					if (file.getParentFile() != null) {
						pdfjs = file.getParentFile().getParentFile().getAbsolutePath() + "/js";
					}
				}

				String db = PROPERTIES.getProperty("db.identifiers");
				if(db != null) {
				    String[] dbNames = db.split(",");
				    for(String dbName : dbNames) {
				        String identifier = dbName.trim();
				        dbIdentifiers.add(identifier);
                    }
                }
				LOGGER.info(getIotEndPoint() + "iot endpoint");
			} catch (IOException e) {
				LOGGER.info("Exception while trying to load property file " + AWS_PROPERTY_FILE);
			}
		}
	}

	public static String getConfig(String name) {
    	return PROPERTIES.getProperty(name);
    }

	public static Map<String, Object> getClientInfo() {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs;
		String clientVersion = null;
		boolean isNewClientBuild=false;
				
		try {
			if( environment != null ) {
				conn = FacilioConnectionPool.INSTANCE.getConnection();
				pstmt = conn.prepareStatement("SELECT * FROM ClientApp WHERE environment=?");
				pstmt.setString(1, environment);
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

	public static int updateClientVersion(String newVersion,boolean isNewClientBuild) throws SystemException {
		int updatedRows = 0;
		if(newVersion != null) {
			newVersion = newVersion.trim();
			newVersion = newVersion.replace("/", "");
			Connection conn = null;
			PreparedStatement pstmt = null;
			try {
				if(checkIfVersionExistsInS3(newVersion)) {
					com.facilio.accounts.dto.User currentUser = AccountUtil.getCurrentUser();
					if (environment != null && currentUser != null) {
						FacilioTransactionManager.INSTANCE.getTransactionManager().begin();
						conn = FacilioConnectionPool.INSTANCE.getConnection();
						pstmt = conn.prepareStatement("Update ClientApp set version=?, updatedTime=?, updatedBy=?, is_new_client_build=?  WHERE environment=?");
						pstmt.setString(1, newVersion);
						pstmt.setLong(2, System.currentTimeMillis());
						pstmt.setLong(3, currentUser.getId());
						pstmt.setBoolean(4, isNewClientBuild);
						pstmt.setString(5, environment);

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
		boolean objectExists = false;
		String staticBucket = AwsUtil.getConfig("static.bucket");
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
	
	public static void sendEmail(JSONObject mailJson) throws Exception  {
		logEmail(mailJson);
		if(AwsUtil.isDevelopment()) {
//			mailJson.put("subject", "Local - "+mailJson.get("subject"));
			return;
		}
		if(isSmtp()) {
			EmailUtil.sendEmail(mailJson);
		} else {
			sendEmailViaAws(mailJson);
		}
	}

	private static void sendEmailViaAws(JSONObject mailJson) throws Exception  {
		String toAddress = (String)mailJson.get("to");
		boolean sendEmail = true;
		HashSet<String> to = new HashSet<>();
		if( ! AwsUtil.isProduction() ) {
			if(toAddress != null) {
				for(String address : toAddress.split(",")) {
					if(address.contains("@facilio.com")) {
						to.add(address);
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
			Destination destination = new Destination().withToAddresses(to);
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
				if (AccountUtil.getCurrentOrg() != null && (AccountUtil.getCurrentOrg().getId() == 104 || AccountUtil.getCurrentOrg().getId() == 151)) {
					LOGGER.info("Sending email : "+mailJson.toJSONString());
				}
				SendEmailRequest request = new SendEmailRequest().withSource((String) mailJson.get("sender"))
						.withDestination(destination).withMessage(message);
				AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
						.withRegion(Regions.US_WEST_2).withCredentials(getAWSCredentialsProvider()).build();
				client.sendEmail(request);
				if (AccountUtil.getCurrentOrg() != null && AccountUtil.getCurrentOrg().getId() == 151) {
					LOGGER.info("Email sent to "+toAddress+"\n"+mailJson);
				}
			} catch (Exception ex) {
				LOGGER.info("Error message: " + toAddress + " " + ex.getMessage());
				throw ex;
			}
		}
	}

    public static HashMap<String, String> getPassword(String secretKey) {

		HashMap<String, String> secretMap = new HashMap<>();

        AWSSecretsManager client  = AWSSecretsManagerClientBuilder.standard().withCredentials(getAWSCredentialsProvider()).withRegion(Regions.US_WEST_2).build();

        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretKey);

        GetSecretValueResult getSecretValueResult = null;

        try {
            getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            LOGGER.info("Exception while getting secret ", e);
        }
        if(getSecretValueResult != null) {
			String secretBinaryString = getSecretValueResult.getSecretString();
			ObjectMapper objectMapper = new ObjectMapper();
			try {
				secretMap.putAll(objectMapper.readValue(secretBinaryString, HashMap.class));
			} catch (IOException e) {
				LOGGER.info("exception while reading value from secret manager ", e);
			}
		}
        return secretMap;
    }

    private static void logEmail (JSONObject mailJson) {
		try {
			if (AccountUtil.getCurrentOrg() != null) {
				String toAddress = (String) mailJson.get("to");
				if (!"error+alert@facilio.com".equals(toAddress) && !"error@facilio.com".equals(toAddress)) {
					toAddress = toAddress == null ? "" : toAddress;
					JSONObject info = new JSONObject();
					info.put("subject", mailJson.get("subject"));
					CommonAPI.addNotificationLogger(NotificationType.EMAIL, toAddress, info);
				}
			}
		}
		catch (Exception e) {
			LOGGER.error("Error occurred while logging email", e);
		}
	}
	
	public static void sendEmail(JSONObject mailJson, Map<String,String> files) throws Exception  {
		if(files == null || files.isEmpty()) {
			sendEmail(mailJson);
			return;
		}
		logEmail(mailJson);
		if(isSmtp()) {
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
		if( ! AwsUtil.isProduction() ) {
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
				if (AwsUtil.isDevelopment()) {
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
				// LOGGER.info("Email sent!");
				
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
    	return getConfig("user.id");
	}
	public static void addIotClient(String policyName, String clientId){
		addAwsIotClient(policyName,clientId);
	}

	public static boolean addAwsIotClient(String policyName, String clientId) {
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
					.withPolicyDocument(getPolicyDoc(policyName, new String[] { getIotArnClientId(policyName)},new String[] {getIotArnTopic(policyName)},new String[]{getIotArnTopicFilter(policyName)+"/msgs"},new String[]{getIotArnTopic(policyName)+"/msgs"}).toString())
					.withSetAsDefault(true);
			CreatePolicyVersionResult versionResult = client.createPolicyVersion(versionRequest);
			LOGGER.info("Policy updated for " + policyName + ", with " + versionResult.getPolicyDocument() + ", status: " + versionResult.getSdkHttpMetadata().getHttpStatusCode());
		return true;
    	} catch (Exception e){
    		LOGGER.info("Error ",e);
		}
    	return false;
	}

	public static String getIotArnClientId(String clientId){
    	return getIotArn() + ":client/" + clientId;
	}

	private static String getIotArnTopic(String topic) {
    	return getIotArn() +":topic/"+ topic;
	}

	public static String getIotArnTopicFilter(String topic) {
		return getIotArn() +":topicfilter/"+ topic;
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


	private static JSONObject getPolicyDoc(String name, String[] clientIds, String[] publish, String[] subscribe, String[] receive ){
		JSONArray statements = new JSONArray();
		statements.add(getPolicyInJson("iot:Connect", clientIds));
		statements.add(getPolicyInJson("iot:Publish",publish ));
		statements.add(getPolicyInJson("iot:Subscribe", subscribe));
		statements.add(getPolicyInJson("iot:Receive", receive));
		JSONObject policyDocument = new JSONObject();
		policyDocument.put("Version", "2012-10-17"); //Refer the versions available in AWS policy document before changing.
		policyDocument.put("Statement", statements);
		return policyDocument;
	}

	private static  String getIotArn(){
		return "arn:aws:iot:" + getRegion() + ":" + getUserId();
	}

    private static void createIotPolicy( AWSIot iotClient , String name , IotPolicy rule) {
        LOGGER.info(" creating Iot policy for "+name);
        String[] publish = rule.getPublishtopics();
        String[] receive = rule.getReceiveTopics();
        for(int i=0;i<publish.length;i++){
            publish[i] = getIotArnTopic(publish[i]);
        }
        for(int i=0;i<receive.length;i++){
            receive[i] = getIotArnTopic(receive[i]);
        }
        try {
            CreatePolicyRequest policyRequest;
            policyRequest = new CreatePolicyRequest().withPolicyName(rule.getPolicyName()).withPolicyDocument(getPolicyDoc(name, rule.getClientIds(), publish, rule.getSubscribeTopics(), receive).toString());
            CreatePolicyResult policyResult = iotClient.createPolicy(policyRequest);
            LOGGER.info("Policy created : " + policyResult.getPolicyArn() + " version " + policyResult.getPolicyVersionId());
        } catch (ResourceAlreadyExistsException resourceExists){
            LOGGER.info("Policy already exists for name : " + rule.getPolicyName());
        }
    }

	private static CreateKeysAndCertificateResult createCertificate(AWSIot iotClient){
	    LOGGER.info(" creating certificate ");
		CreateKeysAndCertificateRequest certificateRequest = new CreateKeysAndCertificateRequest().withSetAsActive(true);
		return iotClient.createKeysAndCertificate(certificateRequest);
	}

	private static void attachPolicy(AWSIot iotClient, CreateKeysAndCertificateResult certificateResult, String policyName){
	    LOGGER.info(" attaching policy for "+policyName);
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
	    LOGGER.info(" creating kenisis stream ");
    	try {
			CreateStreamResult streamResult = kinesisClient.createStream(streamName, 1);
			LOGGER.info("Stream created : " + streamName + " with status " + streamResult.getSdkHttpMetadata().getHttpStatusCode());
		} catch (ResourceInUseException resourceInUse){
    		LOGGER.info("Stream exists for name : " + streamName);
		}
	}


	private static void createIotTopicRule(IotPolicy policy, AWSIot iotClient) {
	    LOGGER.info(" creating iot rule ");
        Map<String,String> publishTypeMap = policy.getMappedTopicAndPublished();
		for(String topic : publishTypeMap.keySet()){
			try {
				KinesisAction kinesisAction = new KinesisAction().withStreamName(topic)
						.withPartitionKey(KINESIS_PARTITION_KEY)
						.withRoleArn(IAM_ARN_PREFIX + getUserId() + KINESIS_PUT_ROLE_SUFFIX);

				Action action = new Action().withKinesis(kinesisAction);
				TopicRulePayload rulePayload = new TopicRulePayload();
                rulePayload.withActions(action);
                rulePayload.withSql(policy.getSql(topic,publishTypeMap));
				rulePayload.withAwsIotSqlVersion(IOT_SQL_VERSION); //Refer the versions available in AWS iot sql version document before changing.
                LOGGER.info(" rulepayload sql  "+rulePayload.getSql());
				CreateTopicRuleRequest topicRuleRequest = new CreateTopicRuleRequest().withRuleName(topic).withTopicRulePayload(rulePayload);

				CreateTopicRuleResult topicRuleResult = iotClient.createTopicRule(topicRuleRequest);

				LOGGER.info("Topic Rule created : " + topicRuleResult.getSdkHttpMetadata().getHttpStatusCode());
			} catch (ResourceAlreadyExistsException resourceExists ){
				LOGGER.info("Topic Rule already exists for name : "+topic);
			}
		}

	}


	private static CreateKeysAndCertificateResult createIotToKinesis(String name, String policyName, String type){
		LOGGER.info(" create Iot Kenesis "+policyName);
		AWSIot iotClient = getIotClient();
		IotPolicy policy = new IotPolicy();
		policy = FacilioAgent.getIotRule(name,type);
		policy.setPolicyName(policyName);
		policy.setType(type);
		createIotPolicy(iotClient, name, policy);
    	CreateKeysAndCertificateResult certificateResult = createCertificate(iotClient);
    	attachPolicy(iotClient, certificateResult, policyName);
    	createKinesisStream(getKinesisClient(), name);
    	createIotTopicRule(policy,iotClient);
    	return certificateResult;
	}

	public static CreateKeysAndCertificateResult signUpIotToKinesis(String orgDomainName, String policyName, String type){
		LOGGER.info(" signing up to kinesis policyname "+policyName);
		String name = getIotKinesisTopic(orgDomainName);
		return AwsUtil.createIotToKinesis(name, policyName, type);
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
	    
	    String type = "text/plain; charset=UTF-8";
	    if(mailJson.get("mailType") != null && mailJson.get("mailType").equals("html")) {
	    	type = "text/html; charset=UTF-8";
		}
	    textPart.setContent(MimeUtility.encodeText((String) mailJson.get("message"),DefaultCharSet,"B"), type);
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
	    message.addHeader("host", getAppDomain());
	    return message;
	}

	public static boolean isProduction() {
		return productionEnvironment;
	}

	public static boolean isDevelopment() {
		return developmentEnvironment;
	}

	public static boolean disableCSP() {
		return disableCSP;
	}

	public static String getServerName() {
		return getAppDomain();
	}

	public static String getDBClass() {
		return dbClass;
	}

	public static boolean isScheduleServer() {
		return scheduleServer;
	}

	public static boolean isUserServer() {
		return userServer;
	}

	public static boolean isMessageProcessor() {
		return messageProcessor;
	}
	
	public static String getAppDomain() {
		return appDomain;
	}

	public static String getClientAppUrl() {
		return clientAppUrl;
	}

	public static String getPushNotificationKey() {
		return pushNotificationKey;
	}

	public static String getPortalPushNotificationKey() {
		return portalPushNotificationKey;
	}

	public static String getKafkaProducer() {
		return kafkaProducer;
	}


	public static String getKafkaConsumer() {
		return kafkaConsumer;
	}

	public static boolean isSmtp() {
		return isSmtp;
	}

	public static String getPdfjsLocation() {
		return pdfjs;
	}
	
	public static String getAnomalyTempDir() {
		return anomalyTempDir;
	}

	public static String getAnomalyCheckServiceURL() {
		return anomalyCheckServiceURL;
	}

	public static String getAnomalyBucket() {
		return anomalyBucket;
	}

	public static String getAnomalyBucketDir() {
		return anomalyBucketDir;
	}

	public static String getAnomalyPeriodicity() {
		return anomalyPeriodicity;
	}

	public static String getAnomalyRefreshWaitTimeInSeconds() {
		return anomalyRefreshWaitTimeInSeconds;
	}

	public static String getAnomalyDetectWaitTimeInSeconds() {
		return anomalyDetectWaitTimeInSeconds;
	}
	
	public static String getAnomalyPredictAPIURL() {
		return anomalyPredictAPIURL;
	}

    public static boolean isSysLogEnabled() {
		return sysLogEnabled;
    }

    public static HashSet<String> getDBIdentifiers() {
	    return dbIdentifiers;
    }
}