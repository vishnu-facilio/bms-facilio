package com.facilio.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class FacilioHttpUtils {
    private static final Logger LOGGER = LogManager.getLogger(FacilioHttpUtils.class.getName());
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

    public static String doHttpPost(String url, Map<String,String> headers, Map<String,String> params, String bodyContent, int timeOutInSeconds) throws IOException
    {
        StringBuilder result = new StringBuilder();

        CloseableHttpClient client = com.facilio.aws.util.AwsUtil.getHttpClient(timeOutInSeconds);

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
            if(bodyContent != null)							// first preference to body content
            {
                HttpEntity entity = new ByteArrayEntity(bodyContent.getBytes(StandardCharsets.UTF_8));
                post.setEntity(entity);
            }
            else if(params != null && !params.isEmpty())
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
            	
            LOGGER.info("\nSending 'POST' request to URL : " + url);
            LOGGER.info("Post parameters : " + post.getEntity());
            LOGGER.info("Response Code : " + status);
            
            try(BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));) {
            	String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                LOGGER.info("result : " + result);
            }

        } catch (Exception e) {
            LOGGER.info("Executing doHttpPost ::::url:::" + url, e);
        } finally {
            client.close();
        }
        return result.toString();
    }
    
    public static String doHttpGet(String url, Map<String,String> headers, Map<String,String> params, int timeOutInSeconds) throws IOException
    {
        StringBuilder result = new StringBuilder();

        CloseableHttpClient client = com.facilio.aws.util.AwsUtil.getHttpClient(timeOutInSeconds);

        try
        {
        	
        	URIBuilder builder = new URIBuilder(url);
        	
        	if(params != null)
            {
                for (String key : params.keySet()) {
                    String value = params.get(key);
                    builder.setParameter(key, value);
                }
            }
        	
            HttpGet get = new HttpGet(builder.build());
            
            if(headers != null)
            {
                for (String key : headers.keySet()) {
                    String value = headers.get(key);
                    get.setHeader(key, value);
                }
            }
            

            CloseableHttpResponse response = client.execute(get);
            int status = response.getStatusLine().getStatusCode();
            
            LOGGER.info("\nSending 'GET' request to URL : " + url);
            LOGGER.info("get parameters : " + params);
            LOGGER.info("Response Code : " + status);
            
            try(BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));) {
            	
            	String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                LOGGER.info("result : " + result);
            }

        } catch (Exception e) {
            LOGGER.info("Executing doHttpGet ::::url:::" + url, e);
        } finally {
            client.close();
        }
        return result.toString();
    }
    
    public static String doHttpPost(String url, Map<String, String> headers, Map<String, String> params, String bodyContent) throws IOException
    {
        return doHttpPost(url, headers, params, bodyContent,-1);
    }
    public static String doHttpGet(String url, Map<String, String> headers, Map<String, String> params) throws IOException
    {
        return doHttpGet(url, headers, params, -1);
    }

}
