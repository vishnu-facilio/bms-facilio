package com.facilio.aws.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

public class AwsUtil 
{
	private static Logger logger = Logger.getLogger(AwsUtil.class.getName());
	private static final String AWS_PROPERTY_FILE = "conf/awsprops.properties";

	public static final String AWS_IOT_SERVICE_NAME = "iotdata";

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
        System.out.println(prop);
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
    
    public static String getSignature(String secretKey, String dateStamp, String regionName, String serviceName, String payload, String xAmzDate) throws Exception
    {
    	byte[] kSecret = ("AWS4" + secretKey).getBytes("UTF8");
        byte[] kDate = HmacSHA256(dateStamp, kSecret);
        byte[] kRegion = HmacSHA256(regionName, kDate);
        byte[] kService = HmacSHA256(serviceName, kRegion);
        byte[] kSigning = HmacSHA256("aws4_request", kService);
        
        String stringToSign = getStringToSign(payload, xAmzDate);
        return bytesToHex(HmacSHA256(stringToSign, kSigning));
    }
    
    public static String getStringToSign(String payload, String xAmzDate) throws NoSuchAlgorithmException
    {
    	String canonicalHeader = "content-type:application/json\nhost:a2ak8t6zogzde5.iot.us-west-2.amazonaws.com\nx-amz-date:"+xAmzDate+"\n";
        String signedHeader = "content-type;host;x-amz-date";
        String canonicalRequest = "POST" + "\n" + "/things/EM/shadow" + "\n" + "" + "\n" + canonicalHeader + "\n" + signedHeader + "\n" + hash256(payload).toLowerCase();
        
        String scope = "20170525/us-west-2/iotdata/aws4_request";
        return "AWS4-HMAC-SHA256" + "\n" + xAmzDate + "\n" + scope + "\n" + hash256(canonicalRequest).toLowerCase();
    }
    
    public static void doHttpPost(String url, Map<String, String> headers, Map<String, String> params, String bodyContent) throws IOException
    {
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
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) 
			{
				result.append(line);
			}
			System.out.println(result.toString());
    	}
		catch (Exception e) 
    	{
			logger.log(Level.SEVERE, "Executing doHttpPost ::::url:::" + url, e);
		} 
    	finally 
    	{
			client.close();
		}
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
	
	public static String getDeviceData() throws IOException		//Sample Code
	{
		String user = "admin";
    	String pass = "Admin@1234";
    	String ipAddress = "192.168.1.66";
    	
    	String authString = user  + ":" + pass;
		byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
		String authStringEnc = new String(authEncBytes);
    	
    	String Base64Str = "Basic "+authStringEnc;
    	String url = "http://"+ipAddress+"/api/rest/v1/";
    	
    	CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpClientContext context = HttpClientContext.create();
        HttpGet method = new HttpGet(url);
        method.addHeader("Authorization", Base64Str);
        String cookieName = null;
        String cookieValue = null;
        try 
        {
        	CloseableHttpResponse response = httpclient.execute(method, context);
        	BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = rd.readLine()) != null) 
			{
				result.append(line);
			}
		    CookieStore cookieStore = context.getCookieStore();
		    List<Cookie> cookies = cookieStore.getCookies();
            cookieName = cookies.get(0).getName();
            cookieValue = cookies.get(0).getValue();
            System.out.println(cookieName);
            System.out.println(cookieValue);
        }
        catch (Exception e) 
        {
        	logger.log(Level.SEVERE, "Executing getDeveiceData ::::url:::" + url, e);
        } 
        finally 
        {
        	httpclient.close();
        }
        
        String url2 = "http://"+ipAddress+"/api/rest/v1/info/device";
        CloseableHttpClient httpclient2 = HttpClients.createDefault();
        HttpClientContext context2 = HttpClientContext.create();
        HttpGet method2 = new HttpGet(url2);
        method2.addHeader("cookie", cookieName+"="+cookieValue);
        StringBuffer result = new StringBuffer();
        try 
        {
        	CloseableHttpResponse response2 = httpclient2.execute(method2, context2);
        	BufferedReader rd = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));
			String line = "";
			while ((line = rd.readLine()) != null) 
			{
				result.append(line);
			}
            System.out.println(result);
        }
        catch (Exception e) 
        {
        	logger.log(Level.SEVERE, "Executing getDeveiceData ::::url:::" + url2, e);
        } 
        finally 
        {
        	httpclient2.close();
        }
        return result.toString();
	}
}
