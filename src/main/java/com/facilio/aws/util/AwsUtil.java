package com.facilio.aws.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONObject;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIot;
import com.amazonaws.services.iot.AWSIotClientBuilder;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.Body;
import com.amazonaws.services.simpleemail.model.Content;
import com.amazonaws.services.simpleemail.model.Destination;
import com.amazonaws.services.simpleemail.model.Message;
import com.amazonaws.services.simpleemail.model.SendEmailRequest;

public class AwsUtil 
{
	private static Logger logger = Logger.getLogger(AwsUtil.class.getName());
	
	private static final String AWS_PROPERTY_FILE = "conf/awsprops.properties";
	
	public static final String AWS_ACCESS_KEY_ID = "accessKeyId";
	public static final String AWS_SECRET_KEY_ID = "secretKeyId";
	
	public static final String AWS_IOT_SERVICE_NAME = "iotdata";
	public static final String AWS_IOT_DYNAMODB_TABLE_NAME = "IotData";
	
	private static Map<String, AWSIotMqttClient> AWS_IOT_MQTT_CLIENTS = new HashMap<>();
	
	private static AmazonS3 AWS_S3_CLIENT = null;

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
    		HttpClientContext context = HttpClientContext.create();
			HttpPost post = new HttpPost(url);
			Iterator<String> headerIterator = headers.keySet().iterator();
			while(headerIterator.hasNext())
			{
				String key = headerIterator.next();
				String value = headers.get(key);
				post.setHeader(key, value);
			}
			if(bodyContent != null)
			{
			    HttpEntity entity = new ByteArrayEntity(bodyContent.getBytes("UTF-8"));
			    post.setEntity(entity);
			}		    
		    if(params != null)
		    {
		    	List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
		    	Iterator<String> paramIterator = headers.keySet().iterator();
				while(paramIterator.hasNext())
				{
					String key = paramIterator.next();
					String value = params.get(key);
					postParameters.add(new BasicNameValuePair(key, value));
				}
		        post.setEntity(new UrlEncodedFormEntity(postParameters));
		    }
			
		    CloseableHttpResponse response = client.execute(post, context);
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
		mailJson.put("sender", "shivaraj@facilio.com");
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
}
