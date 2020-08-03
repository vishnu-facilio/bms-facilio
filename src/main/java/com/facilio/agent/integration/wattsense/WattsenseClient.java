package com.facilio.agent.integration.wattsense;

import com.facilio.agentv2.misc.MiscController;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
/** Usage Sample
 * <pre>
 * {@code
 *       WattsenseClient client = new WattsenseClient();
 *       client.setApiKey("XdwqbEv0aOgrVXBQZPD7Lxp5A89GP68wR064gW3eq4lz6JkKMwdYRmbyo2N1JR4Y");
 *       client.setSecretKey("YPreE3g20A8wX9dBVNYpL1QzvMW5QzM18QKZtoWleDbR6OKJa4yoxm7qr5PZkmRM");
 *       List<MiscControllers> = client.getDevices();
 * }
 * </pre>
 */
public class WattsenseClient
{
    private static final Logger LOGGER = LogManager.getLogger(WattsenseClient.class.getName());
    

    private String clientId;
    private String certificateStoreId;
    private String mqttConnectionId;
    private String authStringEncoded;
    private int type =1 ;
    private int integrationStatus = WattsenseUtil.NOT_INTEGRATED;
    private String deletedTime;
    private String userName;
    private long id;

    private static HttpClient httpclient = HttpClientBuilder.create().build();

    private static final String HMAC_SHA512 = "HmacSHA512";
    private static final String BASE_URL = "https://api.wattsense.com";
    private static final String GET = "GET";
    private static final String GET_DEVICES_PATH = "/v1/devices";

    private String apiKey;

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    private String secretKey;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public String getCertificateStoreId() { return certificateStoreId; }
    public void setCertificateStoreId(String certificateStoreId) { this.certificateStoreId = certificateStoreId; }

    public String getMqttConnectionId() { return mqttConnectionId; }
    public void setMqttConnectionId(String mqttConnectionId) { this.mqttConnectionId = mqttConnectionId; }

    public String getAuthStringEncoded() { return authStringEncoded; }
    public void setAuthStringEncoded(String authStringEncoded) { this.authStringEncoded = authStringEncoded; }

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public int getIntegrationStatus() { return integrationStatus; }
    public void setIntegrationStatus(int integrationStatus) { this.integrationStatus = integrationStatus; }

    public String getDeletedTime() { return deletedTime; }
    public void setDeletedTime(String deletedTime) { this.deletedTime = deletedTime; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public List<MiscController> getDevices() throws Exception{
        List<MiscController> controllers = new ArrayList<>();
        long timestamp = System.currentTimeMillis();
        HttpResponse response = get(BASE_URL,GET_DEVICES_PATH,timestamp);
        int responseCode = response.getStatusLine().getStatusCode();
        LOGGER.info("responseCode : "+responseCode);
        if (responseCode != HttpURLConnection.HTTP_OK   &&  responseCode != HttpURLConnection.HTTP_CREATED ) {
            //handle unsuccessful response
            return null;
        }
        JSONParser parser = new JSONParser();
        LOGGER.info("Response : "+response);
        JSONArray jsonArray = (JSONArray) parser.parse(WattsenseUtil.getResponseString(response));
        LOGGER.info("Json array : " + jsonArray );
        if (jsonArray.size()!=0){
            jsonArray.forEach(wattSenseDevice -> {
                JSONObject device = (JSONObject) wattSenseDevice;
                if (device.containsKey("deviceId") && device.containsKey("name")){
                    MiscController miscController = new MiscController();
                    miscController.setName(((JSONObject) wattSenseDevice).get("deviceId").toString());
                    miscController.setDisplayName(((JSONObject) wattSenseDevice).get("name").toString());
                    controllers.add(miscController);
                }else{
                    //handle invalid json from wattsense
                }
            });
        }else{
            //throw empty list exception
        }
        return controllers;
    }


    private static String getHmac(String method, String path, String payload, long timestamp, WattsenseClient client){
        Mac sha512Hmac;
        String hash = null;
        try {
            final byte[] byteKey = client.getSecretKey().getBytes(StandardCharsets.UTF_8);
            sha512Hmac = Mac.getInstance(HMAC_SHA512);
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, HMAC_SHA512);
            sha512Hmac.init(keySpec);
            String message = method + "\n" + path + "\n";
            if (payload!=null){
                message = message + payload +"\n";
            }
            message = message + timestamp;
            byte[] macData = sha512Hmac.doFinal(message.getBytes(StandardCharsets.UTF_8));

            // Can either base64 encode or put it right into hex
            hash = Base64.getEncoder().encodeToString(macData);
            LOGGER.info("Hash : " + hash);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {

        }
        return hash;
    }

    private HttpResponse get(String base,String path,long timestamp) throws IOException {
        HttpGet get = new HttpGet(base+path);
        get.addHeader("X-API-Auth",getApiKey()+":"+WattsenseClient.getHmac(GET,path,null,
                timestamp,this));
        get.addHeader("X-API-Timestamp",""+timestamp);
        return httpclient.execute(get);
    }

}
