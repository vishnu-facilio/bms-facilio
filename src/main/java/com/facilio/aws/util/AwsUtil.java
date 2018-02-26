package com.facilio.aws.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagement;
import com.amazonaws.services.identitymanagement.AmazonIdentityManagementClientBuilder;
import com.amazonaws.services.identitymanagement.model.GetUserResult;
import com.amazonaws.services.identitymanagement.model.User;
import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.services.iot.AWSIotClientBuilder;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
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

public class AwsUtil 
{
	private static Logger logger = Logger.getLogger(AwsUtil.class.getName());
	
	private static final String AWS_PROPERTY_FILE = "conf/awsprops.properties";
	
	public static final String AWS_ACCESS_KEY_ID = "accessKeyId";
	public static final String AWS_SECRET_KEY_ID = "secretKeyId";
	
	public static final String AWS_IOT_SERVICE_NAME = "iotdata";
	public static final String AWS_IOT_DYNAMODB_TABLE_NAME = "IotData";

	private static final String KINESIS_PARTITION_KEY = "${clientid()}";
	private static final String IAM_ARN_PREFIX = "arn:aws:iam::";
	private static final String KINESIS_PUT_ROLE_SUFFIX = ":role/service-role/kinesisput";
	private static final String IOT_SQL_VERSION = "2016-03-23";//Refer the versions available in AWS iot sql version document before changing.
	
	private static Map<String, AWSIotMqttClient> AWS_IOT_MQTT_CLIENTS = new HashMap<>();
	
	private static AmazonS3 AWS_S3_CLIENT = null;

	private static AWSCredentials basicCredentials = null;
	private static AWSCredentialsProvider credentialsProvider = null;
	private static AWSIot awsIot = null;
	private static User user = null;
	private static AmazonKinesis kinesis = null;
	private static String region = null;
	private static final Object LOCK = new Object();

    public static String getConfig(String name) 
    {
        Properties prop = new Properties();
        URL resource = AwsUtil.class.getClassLoader().getResource(AWS_PROPERTY_FILE);
        if (resource == null) 
        {
            return null;
        }
        try (InputStream stream = resource.openStream()) 
        {
            prop.load(stream);
        } 
        catch (IOException e) 
        {
            return null;
        }
        
        String value = prop.getProperty(name);
        if (value == null || value.trim().length() == 0) 
        {
            return null;
        } 
        else 
        {
            return value;
        }
    }
    
    public static CreateKeysAndCertificateResult getCertificateResult()
    {
    	BasicAWSCredentials awsCreds = new BasicAWSCredentials(AwsUtil.getConfig("accessKeyId"), AwsUtil.getConfig("secretKeyId"));
    	AWSIot awsIot = AWSIotClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
    
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
    	AWSIotMqttClient awsIotClient = new AWSIotMqttClient(AwsUtil.getConfig("clientEndpoint"), clientId, AwsUtil.getConfig(AwsUtil.AWS_ACCESS_KEY_ID), AwsUtil.getConfig(AwsUtil.AWS_SECRET_KEY_ID));
    	awsIotClient.connect();
    	AWS_IOT_MQTT_CLIENTS.put(clientId, awsIotClient);
		return awsIotClient;
    }
    
    public static AmazonS3 getAmazonS3Client() {
    	if (AWS_S3_CLIENT == null) {
    		BasicAWSCredentials awsCreds = new BasicAWSCredentials(AwsUtil.getConfig("accessKeyId"), AwsUtil.getConfig("secretKeyId"));
        	AWS_S3_CLIENT = AmazonS3ClientBuilder.standard().withRegion(AwsUtil.getConfig("region")).withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
    	}
    	return AWS_S3_CLIENT;
    }
    
    public static String getSignature(String payload, String xAmzDate, String path) throws Exception
    {
		String secretKey = AwsUtil.getConfig("secretKeyId");
        String dateStamp = new SimpleDateFormat("yyyyMMdd").format(new Date()); 	//"20170525";
        String regionName = AwsUtil.getConfig("region");		//"us-west-2";
        String serviceName = AwsUtil.AWS_IOT_SERVICE_NAME;
        
    	byte[] kSecret = ("AWS4" + secretKey).getBytes("UTF8");
        byte[] kDate = HmacSHA256(dateStamp, kSecret);
        byte[] kRegion = HmacSHA256(regionName, kDate);
        byte[] kService = HmacSHA256(serviceName, kRegion);
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
    	StringBuffer result = new StringBuffer();
    	CloseableHttpClient client = HttpClients.createDefault();
    	try
    	{
			HttpPost post = new HttpPost(url);
			if(headers != null)
			{
				Iterator<String> headerIterator = headers.keySet().iterator();
				while(headerIterator.hasNext())
				{
					String key = headerIterator.next();
					String value = headers.get(key);
					post.setHeader(key, value);
				}
			}
			if(bodyContent != null)
			{
			    HttpEntity entity = new ByteArrayEntity(bodyContent.getBytes("UTF-8"));
			    post.setEntity(entity);
			}		    
		    if(params != null)
		    {
		    	List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		    	Iterator<String> paramIterator = params.keySet().iterator();
				while(paramIterator.hasNext())
				{
					String key = paramIterator.next();
					String value = params.get(key);
					postParameters.add(new BasicNameValuePair(key, value));
				}
		        post.setEntity(new UrlEncodedFormEntity(postParameters));
		    }
			
		    CloseableHttpResponse response = client.execute(post);
			System.out.println("\nSending 'POST' request to URL : " + url);
			System.out.println("Post parameters : " + post.getEntity());
			System.out.println("Response Code : " +  response.getStatusLine().getStatusCode());
	 
			BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) 
			{
				result.append(line);
			}
		//	System.out.println(result.toString());
    	}
		catch (Exception e) 
    	{
			logger.log(Level.SEVERE, "Executing doHttpPost ::::url:::" + url, e);
		} 
    	finally 
    	{
			client.close();
		}
    	return result.toString();
    }
    
    public static byte[] HmacSHA256(String data, byte[] key) throws Exception 
	{
        String algorithm = "HmacSHA256";
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(data.getBytes("UTF8"));
    }
	
	public static String hash256(String data) throws NoSuchAlgorithmException 
	{
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(data.getBytes());
        return bytesToHex(md.digest());
    }
	
	public static String bytesToHex(byte[] bytes) 
	{
        StringBuffer result = new StringBuffer();
        for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }
	
	public static void sendEmail(JSONObject mailJson) throws Exception 
	{
		mailJson.put("sender", "support@facilio.com");
        Destination destination = new Destination().withToAddresses(new String[] { (String) mailJson.get("to") });
        Content subjectContent = new Content().withData((String) mailJson.get("subject"));
        Content bodyContent = new Content().withData((String) mailJson.get("message"));
        Body body = new Body().withText(bodyContent);

        Message message = new Message().withSubject(subjectContent).withBody(body);

        try
        {
	        SendEmailRequest request = new SendEmailRequest().withSource((String) mailJson.get("sender"))
	                .withDestination(destination).withMessage(message);
	        BasicAWSCredentials awsCreds = new BasicAWSCredentials(AwsUtil.getConfig("accessKeyId"), AwsUtil.getConfig("secretKeyId"));
	        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
	                .withRegion(Regions.US_WEST_2).withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
	        client.sendEmail(request);
	        System.out.println("Email sent!");
	    } 
        catch (Exception ex) 
        {
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
            throw ex;
        }
	}
	
	public static void sendEmail(JSONObject mailJson, Map<String,String> files) throws Exception 
	{
		if(files == null || files.isEmpty()) {
			sendEmail(mailJson);
			return;
		}

        try
        {
        		MimeMessage message = getEmailMessage(mailJson, files);
        		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        		message.writeTo(outputStream);
        		RawMessage rawMessage = new RawMessage(ByteBuffer.wrap(outputStream.toByteArray()));
        		SendRawEmailRequest request = new SendRawEmailRequest(rawMessage);
        		
	        BasicAWSCredentials awsCreds = new BasicAWSCredentials(AwsUtil.getConfig("accessKeyId"), AwsUtil.getConfig("secretKeyId"));
	        AmazonSimpleEmailService client = AmazonSimpleEmailServiceClientBuilder.standard()
	                .withRegion(Regions.US_WEST_2).withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();
	        client.sendRawEmail(request);
	        System.out.println("Email sent!");
	    } 
        catch (Exception ex) 
        {
            System.out.println("The email was not sent.");
            System.out.println("Error message: " + ex.getMessage());
            throw ex;
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
					credentialsProvider = new AWSStaticCredentialsProvider(getBasicAwsCredentials());
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

	private static String getUserId() {
    	if(user == null) {
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
		return user.getUserId();
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
			logger.info("Policy updated for " + policyName + ", with " + versionResult.getPolicyDocument() + ", status: " + versionResult.getSdkHttpMetadata().getHttpStatusCode());
		} catch (Exception e){
    		e.printStackTrace();
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
			logger.info("Policy created : " + policyResult.getPolicyArn() + " version " + policyResult.getPolicyVersionId());
		} catch (ResourceAlreadyExistsException resourceExists){
    		logger.info("Policy already exists for name : " + name);
		}
	}

	private static CreateKeysAndCertificateResult createCertificate(AWSIot iotClient){
		CreateKeysAndCertificateRequest certificateRequest = new CreateKeysAndCertificateRequest().withSetAsActive(true);
		return iotClient.createKeysAndCertificate(certificateRequest);
	}

	private static void attachPolicy(AWSIot iotClient, CreateKeysAndCertificateResult certificateResult, String policyName){
		AttachPolicyRequest attachPolicyRequest = new AttachPolicyRequest().withPolicyName(policyName).withTarget(certificateResult.getCertificateArn());
		AttachPolicyResult attachPolicyResult = iotClient.attachPolicy(attachPolicyRequest);
		logger.info("Attached policy : " + attachPolicyResult.getSdkHttpMetadata().getHttpStatusCode());
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
			logger.info("Stream created : " + streamResult.getSdkHttpMetadata().getHttpStatusCode());
		} catch (ResourceInUseException resourceInUse){
    		logger.info("Stream exists for name : " + streamName);
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

			logger.info("Topic Rule created : " + topicRuleResult.getSdkHttpMetadata().getHttpStatusCode());
		} catch (ResourceAlreadyExistsException resourceExists ){
    		logger.info("Topic Rule already exists for name : " + topicName);
		}
	}

	private static CreateKeysAndCertificateResult createIotToKinesis(String name){
    	AWSIot iotClient = getIotClient();
    	createIotPolicy(iotClient, name);
    	CreateKeysAndCertificateResult certificateResult = createCertificate(iotClient);
    	attachPolicy(iotClient, certificateResult, name);
    	createKinesisStream(getKinesisClient(), name);
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
	 	
		String sender = "support@facilio.com";
		
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
	    		String environment = AwsUtil.getConfig("environment"); 
	    		DataSource fileDataSource = null;
	    		if ("development".equalsIgnoreCase(environment)) {
	    			fileDataSource = new FileDataSource(fileUrl);
	    		}
	    		else {
	    			URL url = new URL(fileUrl);
	    			fileDataSource = new URLDataSource(url);
	    		}
		    attachment.setDataHandler(new DataHandler(fileDataSource));
		    attachment.setFileName(file.getKey());
		    messageContent.addBodyPart(attachment);
	    }
	    
	    message.setContent(messageContent);
	    return message;
	}
}
