package com.facilio.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.amazonaws.util.IOUtils;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.service.FacilioHttpUtilsFW;
import com.facilio.services.factory.FacilioFactory;
import com.facilio.services.filestore.FileStore;

public class FacilioHttpUtils extends FacilioHttpUtilsFW {
    private static final Logger LOGGER = LogManager.getLogger(FacilioHttpUtils.class.getName());

    public static long doHttpGetWithFileResponse(String url, Map<String,String> headers, Map<String,String> params,String fileName,String fileType) throws IOException
    {
        CloseableHttpClient client = com.facilio.aws.util.AwsUtil.getHttpClient(-1);

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
            
            ClassLoader classLoader = FacilioHttpUtils.class.getClassLoader();
			String path = classLoader.getResource("").getFile() + File.separator + "facilio-temp-files" + File.separator + AccountUtil.getCurrentOrg().getOrgId();

			File fileFolder = new File(path);
			if (!(fileFolder.exists() && fileFolder.isDirectory())) {
				fileFolder.mkdirs();
			}
			String filePath = path + File.separator + fileName ;
			
			File blobFile = new File(filePath);
	            
            try(OutputStream outputStream = new FileOutputStream(blobFile)){
    		    IOUtils.copy(response.getEntity().getContent(), outputStream);
    		} 
            
            FileStore fs = FacilioFactory.getFileStore();
		    
            return fs.addFile(fileName, blobFile, fileType);

        } catch (Exception e) {
            LOGGER.info("Executing doHttpGet ::::url:::" + url, e);
        } finally {
            client.close();
        }
        return -1;
    }
}
