package com.facilio.iam.accounts.impl;

import com.facilio.aws.util.FacilioProperties;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import lombok.var;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

@Log4j
public class IamClient {
    private static HttpClient httpclient = HttpClientBuilder.create().build();

    @SneakyThrows
    public static String lookupUser(String name) throws Exception {
        var url = FacilioProperties.getIAMUserLookupURL();
        HttpGet get = new HttpGet(url);
        HttpResponse res = null;
        res = httpclient.execute(get);
        var jsonObject = getJsonFromResponse(res);
        return (String) jsonObject.get("dc");
    }

    private static JSONObject getJsonFromResponse(HttpResponse response) throws ParseException {
        int responseCode = response.getStatusLine().getStatusCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            //handle unsuccessful response
            return null;
        }
        JSONParser parser = new JSONParser();
        return (JSONObject) parser.parse(getResponseString(response));
    }

    private static String getResponseString(HttpResponse response){
        HttpEntity ent = response.getEntity();
        InputStream is = null;
        try {
            is = ent.getContent();
            InputStreamReader isr = new InputStreamReader(is);
            int numCharsRead;
            char[] charArray = new char[1024];
            StringBuffer sb = new StringBuffer();
            while ((numCharsRead = isr.read(charArray)) > 0) {
                sb.append(charArray, 0, numCharsRead);
            }
            String result = sb.toString();
            return result;
        } catch (IOException e) {
            LOGGER.info("Exception occurred ",e);
        }
        return "_";
    }
}
