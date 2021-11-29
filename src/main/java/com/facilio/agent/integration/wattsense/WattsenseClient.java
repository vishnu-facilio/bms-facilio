package com.facilio.agent.integration.wattsense;

import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentApiV2;
import com.facilio.agentv2.FacilioAgent;
import com.facilio.agentv2.controller.Controller;
import com.facilio.agentv2.controller.ControllerApiV2;
import com.facilio.agentv2.device.Device;
import com.facilio.agentv2.device.FieldDeviceApi;
import com.facilio.agentv2.misc.MiscController;
import com.facilio.agentv2.misc.MiscPoint;
import com.facilio.bmsconsole.jobs.DataFetcherJob;
import com.facilio.bmsconsole.util.DeviceAPI;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/** Usage Sample
 * <pre>
 * {@code
 *       WattsenseClient client = new WattsenseClient();
 *       client.setApiKey("XdwqbEv0aOgrVXBQZPD7Lxp5A89GP68wR064gW3eq4lz6JkKMwdYRmbyo2N1JR4Y");
 *       client.setSecretKey("YPreE3g20A8wX9dBVNYpL1QzvMW5QzM18QKZtoWleDbR6OKJa4yoxm7qr5PZkmRM");
 *       List<MiscControllers> controllers = client.getDevices();
 * }
 * </pre>
 */
public class WattsenseClient
{
    private static final Logger LOGGER = LogManager.getLogger(WattsenseClient.class.getName());
    public static final String PROPERTY = "property";
    public static final String NAME = "name";

    private FacilioAgent agent;
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

    public FacilioAgent getAgent() {
        //select agent from wattsense table where api key = getApiKey()
        return agent;
    }

    public WattsenseClient(FacilioAgent agent) {
        this.agent = agent;
    }

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

    public List<Device> getDevices() throws Exception {
        List<Device> controllers = new ArrayList<>();
        long timestamp = System.currentTimeMillis();
        HttpResponse response = get(BASE_URL,GET_DEVICES_PATH,timestamp);
        JSONArray jsonArray = getArrayFromResponse(response);
        LOGGER.info("Json array : " + jsonArray );
        if (jsonArray.size()!=0){
            jsonArray.forEach(wattSenseDevice -> {
                JSONObject device = (JSONObject) wattSenseDevice;
                if (device.containsKey("deviceId") && device.containsKey(NAME)) {
                    Device fieldDevice = new Device();
                    fieldDevice.setControllerType(FacilioControllerType.MISC.asInt());
                    fieldDevice.setName(((JSONObject) wattSenseDevice).get("deviceId").toString());
                    //fieldDevice.setDisplayName(((JSONObject) wattSenseDevice).get("name").toString());
                    fieldDevice.setAgentId(getAgent().getId());
                    controllers.add(fieldDevice);
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
            LOGGER.info("calculating hash for secret key" + client.getSecretKey());
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
            LOGGER.info("Message : " + message);
        } catch (Exception e) {
            LOGGER.info("Error while calculating hash", e);
        }
        return hash;
    }

    private HttpResponse get(String base,String path,long timestamp) throws IOException {
        HttpGet get = new HttpGet(base+path);
        String hash = WattsenseClient.getHmac(GET, path, null,
                timestamp, this);
        LOGGER.info("x-api-auth : " + getApiKey() + ":" + hash);
        get.addHeader("X-API-Auth", getApiKey() + ":" + hash);
        get.addHeader("X-API-Timestamp",""+timestamp);
        HttpResponse res = null;
        try {
            res = httpclient.execute(get);
        } catch (Exception ex) {
            LOGGER.info("Error while sending request ", ex);
        }
        return res;
    }

    private JSONArray getProperties(String deviceId) throws IOException, ParseException {
        LOGGER.info("Getting Properties for device " + deviceId);
        HttpResponse res = get(BASE_URL, GET_DEVICES_PATH + "/" + deviceId + "/properties", System.currentTimeMillis());
        JSONArray jsonArray = new JSONArray();
        if (res != null) {
            LOGGER.info("Response : " + res);
            try {
                jsonArray = getArrayFromResponse(res);
            } catch (Exception ex) {
                LOGGER.info("Error while converting response to JSON", ex);
            }
        }
        return jsonArray;
    }

    public Map<String, JSONArray> getData() throws Exception {
        LOGGER.info("Getting data");
        Map<String, JSONArray> controllerVsValues = new HashMap<>();
        FacilioAgent agent = getAgent();
        if (agent != null && agent.getId() > 0) {
            List<Controller> controllers = ControllerApiV2.getControllersUsingAgentId(agent.getId());
            // List<Map<String, Object>> devices = FieldDeviceApi.getDevicesForAgent(agent.getId());
            LOGGER.info("devices size :" + controllers.size());
            for (Controller c : controllers) {
                String deviceName = c.getName();
                JSONArray jsonArray = getProperties(deviceName);
                LOGGER.info("Properties : " + jsonArray);
                controllerVsValues.put(deviceName, jsonArray);
            }
        }
        return controllerVsValues;
    }

    private JSONArray getArrayFromResponse(HttpResponse response) throws ParseException {
        int responseCode = response.getStatusLine().getStatusCode();
        LOGGER.info("responseCode : " + responseCode);
        if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
            //handle unsuccessful response
            return null;
        }
        JSONParser parser = new JSONParser();
        return (JSONArray) parser.parse(WattsenseUtil.getResponseString(response));
    }

    public List<MiscPoint> getPoints(Controller controller) throws IOException, ParseException {
        List<MiscPoint> pointsList = new ArrayList<>();
        String deviceName = controller.getName();
        JSONArray points = getProperties(deviceName);
        for (Object p : points) {
            JSONObject point = (JSONObject) p;
            MiscPoint miscPoint = new MiscPoint(controller.getAgentId());
            String name = point.get(PROPERTY).toString();
            miscPoint.setName(name);
            miscPoint.setPath(name);
            miscPoint.setDisplayName(point.get(NAME).toString());
            miscPoint.setDeviceName(deviceName);
            pointsList.add(miscPoint);
        }
        return pointsList;
    }
}
