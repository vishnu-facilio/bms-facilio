package com.facilio.agent.integration.wattsense;

import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.*;
import com.facilio.agent.integration.AgentIntegrationKeys;
import com.facilio.agent.integration.AgentIntegrationUtil;
import com.facilio.agent.integration.DownloadCertFile;
import com.facilio.agent.integration.MultipartHttpPost;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.events.tasker.tasks.EventUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class WattsenseUtil
{
    private static HttpClient httpclient = HttpClientBuilder.create().build();
    private static final Logger LOGGER = LogManager.getLogger(WattsenseUtil.class.getName());
    private static final String basic ="Basic ";// Basic_ for basic authentication,can't be changed.

    // can be made public in future
    private static final Integer INTEGRATION_DONE = 200;
    private static final Integer CERT_ID_GENERATED = 1;
    private static final Integer MQTT_ID_GENERATED = 2;
    static final Integer NOT_INTEGRATED = 0;

    private static final String NOT_DELETED = "-1";

    private static boolean notNullAndEmpty(String s){
        return (s != null) && (!s.isEmpty());
    }

    private static boolean authenticate(String username,String password) {
            return authStringEnc(getEncodedBasicAuthString(username, password));
    }

    private static boolean authStringEnc(String authStr) {
        try {
            HttpGet get = new HttpGet(AgentIntegrationUtil.getWattsenseAuthApi());
            get.addHeader(HttpHeaders.AUTHORIZATION, authStr);
            HttpResponse response = httpclient.execute(get);
            if ((response.getStatusLine().getStatusCode()) == 200) {
                return true;
            } else {
                LOGGER.info("authenticating with Wattsense failed");
            }
        } catch (Exception e) {
            LOGGER.info("Exception while authenticating with Wattsense", e);
        }
        return false;
    }

    public static boolean authenticateAndConfigureWattsense(String username, String password)  {
        try {
            if( notNullAndEmpty(username) || password == null){
                LOGGER.info("Authentication params not proper");
                return false;
            }
            Wattsense wattsense = new Wattsense();
            wattsense.setUserName(username);

            if (authenticate(username,password)) { // if status is 200 proceed
                String wattClientId = AgentIntegrationKeys.CLIENT_ID_TAG + "-" + Objects.requireNonNull(AccountUtil.getCurrentOrg()).getDomain();
                LOGGER.info("wattsense client id generated - " + wattClientId);
                wattsense.setClientId(wattClientId);
                wattsense.setAuthStringEncoded(getEncodedBasicAuthString(username, password));
                return integrateWattsense(wattsense);
            }
        }catch (Exception e){
            LOGGER.info("Exception while authentication ",e);
            return false;
        }
        return false;
    }

    private static String getEncodedBasicAuthString(String userName, String password){
        byte[] authEncodedBytes = Base64.getEncoder().encode((userName + ":" + password).getBytes());
        String authStringEncoded = basic + new String(authEncodedBytes);
        return authStringEncoded;
    }

    private static boolean integrateWattsense(Wattsense wattsense){
        LOGGER.info(" iamcvijaylogs wattsense clientId "+wattsense.getClientId());
        String userName = wattsense.getUserName();
        String authStringEncoded = wattsense.getAuthStringEncoded();

        AgentUtil util = new AgentUtil(AccountUtil.getCurrentOrg().getOrgId(), AccountUtil.getCurrentOrg().getDomain());
        util.populateAgentContextMap(null,AgentType.Wattsense); // must be null, to get agent count
        Map<String, FacilioAgent> wattAgentMap = util.getAgentMap();
        String clientId = wattsense.getClientId();
        int wattIntegrationCount = wattAgentMap.size();
        if( ! wattAgentMap.isEmpty()) {
            wattsense.setClientId(clientId); //naming starts with 0
            if(wattIntegrationCount > 1){
                wattsense.setClientId(clientId+"-" + (wattIntegrationCount - 1)); //naming starts with 0
            }
            if (util.getFacilioAgent(wattsense.getClientId(), AgentType.Wattsense) != null) {
                wattsense = getWattsense(wattsense.getClientId()); // getting the last added wattsense connection
                wattsense.setAuthStringEncoded(authStringEncoded);
                wattsense.setUserName(userName);
                if (  INTEGRATION_DONE.equals(wattsense.getIntegrationStatus()) ) {
                    wattsense.setClientId(clientId +"-"+ wattIntegrationCount);
                } else if (  CERT_ID_GENERATED.equals(wattsense.getIntegrationStatus()) ) {
                    return createMqttConnection(wattsense);
                } else if (  MQTT_ID_GENERATED.equals(wattsense.getIntegrationStatus()) ) {
                    return initiateMQTTConnection(wattsense);
                } else if (NOT_INTEGRATED.equals(wattsense.getIntegrationStatus())) {
                    return createCertificateStoreId(wattsense);
                }

            }
        } // no else because
        FacilioAgent agent = new FacilioAgent();
        agent.setAgentName(wattsense.getClientId());
        agent.setAgentConnStatus(Boolean.FALSE);
        agent.setInterval(15L);
        agent.setAgentState(1);
        agent.setAgentType(AgentType.Wattsense.getLabel());
        agent.setId(util.addAgent(agent));
        if (agent.getId() < 1) {
            LOGGER.info(" Exception occurred while creating agent ");
            return false;
        }

        wattsense.setClientId(wattsense.getClientId());
        if (makeWattsenseEntry(AgentIntegrationKeys.USER_NAME, wattsense.getUserName(), wattsense)
                && makeWattsenseEntry(AgentIntegrationKeys.INTEGRATION_STATUS, NOT_INTEGRATED.toString(), wattsense)
                && makeWattsenseEntry(AgentIntegrationKeys.DELETED_TIME,NOT_DELETED,wattsense) ){

            return createCertificateStoreId(wattsense); //create certificate store id
        }


       return false;
    }

    private static Wattsense getWattsense(String wattClientId) {
        Wattsense wattsense = new Wattsense();
        ModuleCRUDBean bean;
        FacilioContext context = new FacilioContext();
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getAgentIntegrationFields());
        context.put(FacilioConstants.ContextNames.TABLE_NAME, AgentIntegrationKeys.TABLE_NAME);
        context.put(FacilioConstants.ContextNames.FIELDS, FieldFactory.getAgentIntegrationFields());
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentIntegrationKeys.NAME), wattClientId,StringOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentIntegrationKeys.INTEGRATION_TYPE), String.valueOf(AgentType.Wattsense.getKey()), NumberOperators.EQUALS));
        context.put(FacilioConstants.ContextNames.CRITERIA,criteria);
        try {
            bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentOrg().getOrgId());
            List<Map<String, Object>> rows = bean.getRows(context);
            if(rows.isEmpty()){
                return new Wattsense();
            }
        wattsense = getWattsenseFromList(rows);
        } catch (Exception e) {
            LOGGER.info("Exception Occurred ",e);
        }
        return wattsense;
    }


    private static boolean createCertificateStoreId(Wattsense wattsense){
        String certificateStoreId = null;
        MultipartHttpPost multipart = null;
        try {
            multipart = new MultipartHttpPost(AgentIntegrationUtil.getWattsenseCertificateStoreApi(),"UTF-8",wattsense.getAuthStringEncoded());
            Map<String ,InputStream> inputStreamMap = new HashMap<>();
            String policyName = AccountUtil.getCurrentOrg().getDomain()+"_"+AgentIntegrationKeys.WATTSENSE_IOT_POLICY;
            /*if(DownloadCertFile.checkForCertificates(AgentType.Wattsense.getLabel())) {
                if (!AwsUtil.addAwsIotClient(policyName, wattsense.getClientId())) {
                    LOGGER.info("Exception occured while adding IotClient ");
                    return false;
                }
            }*/

            String url = DownloadCertFile.downloadCertificate( policyName,AgentType.Wattsense.getLabel() );
            LOGGER.info("wattsense file download url "+url);
            //DownloadCertFile.getCertAndKeyFileAsInputStream(policyName , AgentType.Wattsense.getLabel());
            inputStreamMap = DownloadCertFile.getCertKeyFileInputStreamsFromFileStore(AgentType.Wattsense.getLabel());
            if(inputStreamMap.isEmpty()){
                LOGGER.info(" Exception occurred certfileInputstream map is empty ");
                return false;
            }

            if(inputStreamMap.get(AgentIntegrationKeys.CERT_FILE_NAME) != null){
                multipart.addFile(AgentIntegrationKeys.CERTIFICATE,AgentIntegrationKeys.CERTIFICATE,inputStreamMap.get(AgentIntegrationKeys.CERT_FILE_NAME));
            }else{
                LOGGER.info(" Exception occurred "+AgentIntegrationKeys.CERT_FILE_NAME+" missing form input stream map ");
                return false;
            }
            if(inputStreamMap.get(AgentIntegrationKeys.KEY_FILE_NAME) != null){
                multipart.addFile(AgentIntegrationKeys.CERTIFICATE_KEY,AgentIntegrationKeys.CERTIFICATE_KEY,inputStreamMap.get(AgentIntegrationKeys.KEY_FILE_NAME));
            }else {
                LOGGER.info(" Exception occurred "+AgentIntegrationKeys.KEY_FILE_NAME+" missing form input stream map ");
                return false;
            }
            String response = multipart.finish();
            JSONParser parser = new JSONParser();
            JSONObject certificateStoreResponse = (JSONObject) parser.parse(response);
            if(certificateStoreResponse.containsKey(AgentIntegrationKeys.CERTIFICATE_STORE_ID)){
                certificateStoreId = (String) certificateStoreResponse.get(AgentIntegrationKeys.CERTIFICATE_STORE_ID);
                wattsense.setCertificateStoreId(certificateStoreId);
                if(makeWattsenseEntry(AgentIntegrationKeys.CERTIFICATE_STORE_ID,certificateStoreId,wattsense) && updateStatus(wattsense,CERT_ID_GENERATED.toString())){
                    if(createMqttConnection(wattsense)){
                        return true;
                    }
                }
                // enter certificate storeId.
            }
        } catch (IOException e) {
            LOGGER.info("Exception occurred ",e);
        } catch (ParseException e1) {
            LOGGER.info("Exception while parsing CertificateStoreApi response ",e1);
        }
        return false;
    }

    private static boolean createMqttConnection(Wattsense wattsense){
        // get
        if(wattsense.getCertificateStoreId() == null){
            return false;
        }
        HttpPost post = new HttpPost(AgentIntegrationUtil.getMqttConnectionApi());
        post.addHeader(HttpHeaders.AUTHORIZATION,  wattsense.getAuthStringEncoded());
        String connectorJsonStr = getConnectorJSON(wattsense);
        try {
            StringEntity entity = new StringEntity(connectorJsonStr);
            post.setHeader(HttpHeaders.CONTENT_TYPE,ContentType.APPLICATION_JSON.getMimeType());
            post.setEntity(entity);
        } catch (UnsupportedEncodingException e) {
            LOGGER.info("Exception Occurred ",e);
        }

        try {
            HttpResponse response = httpclient.execute(post);
            String result = getResponseString(response);
            int responseCode = response.getStatusLine().getStatusCode();
            if ( responseCode != HttpURLConnection.HTTP_OK   &&  responseCode != HttpURLConnection.HTTP_CREATED ){
                LOGGER.info(" response not ok ");
                return false;
            }
            JSONObject jsonObject = new JSONObject();
            JSONParser parser = new JSONParser();
            try {
                jsonObject = (JSONObject) parser.parse(result);
                if(jsonObject.containsKey(AgentIntegrationKeys.ID)){
                    String mqttConId = (String) jsonObject.get(AgentIntegrationKeys.ID);
                    wattsense.setMqttConnectionId(mqttConId);
                    if(makeWattsenseEntry(AgentIntegrationKeys.MQTT_ID,mqttConId,wattsense) && updateStatus(wattsense,MQTT_ID_GENERATED.toString())) {
                        if (initiateMQTTConnection(wattsense)) {
                            return true;
                        }
                    }
                }
            } catch (ParseException e) {
                LOGGER.info("Exception while parsing MQTTConnection response "+result);
            }

        } catch (IOException e) {
            LOGGER.info("Exception in MQTTConnection ",e);
        }
        return false;
    }

    private static boolean initiateMQTTConnection(Wattsense wattsense){
        HttpPut put = new HttpPut(AgentIntegrationUtil.getMqttConnectionApi()+wattsense.getMqttConnectionId()+ AgentIntegrationUtil.getInitiateMqttApi());
        put.addHeader(HttpHeaders.AUTHORIZATION,  wattsense.getAuthStringEncoded());
        try {
            HttpResponse response = httpclient.execute(put);
            String result = getResponseString(response);
            if(response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK || response.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_CREATED){
                if(updateStatus(wattsense, INTEGRATION_DONE.toString())) {
                    return true;
                }
            }
        } catch (IOException e) {
            LOGGER.info("Exception Occurred while adding agent");
        }
        return false;
    }

    private static boolean deleteCertificateStore(Wattsense wattsense){
        HttpDelete delete = new HttpDelete(AgentIntegrationUtil.getDeleteCertificateStoreApi()+"/"+wattsense.getCertificateStoreId());
        delete.addHeader(HttpHeaders.AUTHORIZATION,wattsense.getAuthStringEncoded());
        try {
            HttpResponse response = httpclient.execute(delete);
            int status = response.getStatusLine().getStatusCode();
            String result = getResponseString(response);
            if(status== HttpURLConnection.HTTP_CREATED || status == HttpURLConnection.HTTP_OK){
                return true;
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ",e);
        }
        return false;
    }

    private static  boolean deleteMqttConnection(Wattsense wattsense){
        HttpDelete delete = new HttpDelete(AgentIntegrationUtil.getDeleteMqttConnectionApi()+"/"+wattsense.getCertificateStoreId());
        delete.addHeader(HttpHeaders.AUTHORIZATION, wattsense.getAuthStringEncoded());
        try {
            HttpResponse response = httpclient.execute(delete);
            int status = response.getStatusLine().getStatusCode();
            String result = getResponseString(response);
            if(status== HttpURLConnection.HTTP_CREATED || status == HttpURLConnection.HTTP_OK) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.info("Exception occurred ",e);
        }
        return false;
    }

    private static boolean makeWattsenseEntry(String propKey,String propValue,Wattsense wattsense) {
        FacilioChain chain = TransactionChainFactory.createWattChain();
        Map<String,Object> row = new HashMap<>();
        List<Map<String,Object>> toInsert = new ArrayList<>();
            row.put(AgentIntegrationKeys.PROP_KEY, propKey);
            row.put(AgentIntegrationKeys.PROP_VALUE, propValue);
            row.put(AgentIntegrationKeys.INTEGRATION_TYPE, AgentType.Wattsense.getKey());
            row.put(AgentIntegrationKeys.NAME, wattsense.getClientId());
            toInsert.add(row);
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.TABLE_NAME, AgentIntegrationKeys.TABLE_NAME);
        context.put(FacilioConstants.ContextNames.FIELDS, FieldFactory.getAgentIntegrationFields());
        context.put(FacilioConstants.ContextNames.TO_INSERT_MAP, toInsert);
            try {
                boolean insertionStatus = chain.execute(context);
                if(!insertionStatus){
                    LOGGER.info(" failed while making wattsense entry ");
                    return false;
                }
            } catch (Exception e) {
                LOGGER.info("Exception occured ",e);
                return false;
            }

            toInsert.clear();
        return true;
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

    private static Map getCertAndKeyFiles(String policyName,String clientId){
        Map<String,InputStream> filesInputStreamMap = new HashMap<>();
        CreateKeysAndCertificateResult certificateResult = null ; //AwsUtil.signUpIotToKinesis(AccountUtil.getCurrentOrg().getDomain(), policyName ,AgentType.Wattsense.getLabel(),clientId );
        String certificateFileString = certificateResult.getCertificatePem();
        String keyFileString =  certificateResult.getKeyPair().getPrivateKey();
        LOGGER.info(" certificate String generated is "+certificateFileString);
        LOGGER.info(" key String generated is "+keyFileString);
        filesInputStreamMap.put(AgentIntegrationKeys.CERT_FILE_NAME,new ByteArrayInputStream(certificateFileString.getBytes(StandardCharsets.UTF_8)));
        filesInputStreamMap.put(AgentIntegrationKeys.KEY_FILE_NAME, new ByteArrayInputStream(keyFileString.getBytes(StandardCharsets.UTF_8)));
        return filesInputStreamMap;
    }


    private static String getConnectorJSON(Wattsense wattsense){
        String wattClientId = wattsense.getClientId();
        JSONObject mqttConnJson = new JSONObject();
        mqttConnJson.put(AgentIntegrationKeys.NAME, wattClientId);
        JSONObject brokerConfig = new JSONObject();
        brokerConfig.put("brokerURI", AgentIntegrationUtil.getAwsMqttEndpoint());
        brokerConfig.put(AgentIntegrationKeys.CLIENT_ID, wattClientId);
        brokerConfig.put("brokerType","AWS");
        JSONObject certificateStore = new JSONObject();
        certificateStore.put(AgentIntegrationKeys.CERTIFICATE_STORE_ID,wattsense.getCertificateStoreId());
        brokerConfig.put("certificateAuthentication",certificateStore);
        mqttConnJson.put("brokerConfig",brokerConfig);
        mqttConnJson.put("description",AccountUtil.getCurrentOrg().getDomain()+"_Wattsense_MQTT");
        JSONObject qosJSON = new JSONObject();
        qosJSON.put("subscribeQoS",1);
        qosJSON.put("publishQoS",1);
        mqttConnJson.put("qos",qosJSON);

        String topic = AccountUtil.getCurrentOrg().getDomain();
        JSONObject publishTopicJSON = new JSONObject();
        publishTopicJSON.put("eventsTopic",topic+AgentIntegrationKeys.TOPIC_WT_EVENTS);
        publishTopicJSON.put("valuesTopic",topic+AgentIntegrationKeys.TOPIC_WT_VALUES);
        publishTopicJSON.put("alarmsTopic",topic+AgentIntegrationKeys.TOPIC_WT_ALARMS);
        mqttConnJson.put("publishTopics",publishTopicJSON);
        JSONObject subscribeTopicsJSON = new JSONObject();
        subscribeTopicsJSON.put("propertyCommandTopic", topic+AgentIntegrationKeys.TOPIC_WT_CMD);
        mqttConnJson.put("subscribeTopics", subscribeTopicsJSON);
        mqttConnJson.put("tags", new JSONObject());
        return mqttConnJson.toString();
    }

    private static boolean updateStatus(Wattsense wattsense,String status){ // working good
        FacilioChain chain = TransactionChainFactory.updateWattStatusChain();
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getAgentIntegrationFields());
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.TABLE_NAME, AgentIntegrationKeys.TABLE_NAME);
        context.put(FacilioConstants.ContextNames.FIELDS,FieldFactory.getAgentIntegrationFields());
        Map<String,Object> toUpdate = new HashMap<>();
        toUpdate.put(AgentIntegrationKeys.PROP_KEY, AgentIntegrationKeys.INTEGRATION_STATUS);
        toUpdate.put(AgentIntegrationKeys.PROP_VALUE,status);
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentIntegrationKeys.NAME),wattsense.getClientId(),StringOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentIntegrationKeys.INTEGRATION_TYPE), String.valueOf(AgentType.Wattsense.getKey()),NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentIntegrationKeys.PROP_KEY), AgentIntegrationKeys.INTEGRATION_STATUS,StringOperators.IS));
        context.put(FacilioConstants.ContextNames.CRITERIA,criteria);
        context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP,toUpdate);
        try {
            return chain.execute(context);
        } catch (Exception e) {
           LOGGER.info("Exception Occurred");
        }
        return false;
    }

    public static List<Map<String, Object>> getWattsenseList(){
        ModuleCRUDBean bean;
        Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getAgentIntegrationFields());
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.TABLE_NAME, AgentIntegrationKeys.TABLE_NAME);
        context.put(FacilioConstants.ContextNames.FIELDS,FieldFactory.getAgentIntegrationFields());
        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentIntegrationKeys.INTEGRATION_TYPE), String.valueOf(AgentType.Wattsense.getKey()),NumberOperators.EQUALS));
        context.put(FacilioConstants.ContextNames.CRITERIA,criteria);
        context.put(FacilioConstants.ContextNames.SORT_FIELDS,"ID desc");
        List<Map<String, Object>> rows = new ArrayList<>();
        try {
            bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", AccountUtil.getCurrentOrg().getOrgId());
            rows = bean.getRows(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String,Object>> wattsenseList = getWattsenseList(rows);
        return wattsenseList;
    }

    public static boolean deleteIntegration(String clientId,String userName, String password) {
        Wattsense wattsense = getWattsense(clientId);
        String authString = getEncodedBasicAuthString(userName,password);
        wattsense.setAuthStringEncoded(authString);
        if(deleteMqttConnection(wattsense)){
            if(deleteCertificateStore(wattsense)){
                LOGGER.info(" deleted certificate ");
                    if(deleteWattsense(clientId)){
                        return true;
                    }
            }
        }
        LOGGER.info("Exception while deleting Integration");
        return false;
    }

    private static boolean deleteWattsense(String clientId){
        FacilioChain chain = TransactionChainFactory.deleteWattsenseChain();
        Map<String,FacilioField> fieldMap = FieldFactory.getAsMap(FieldFactory.getAgentIntegrationFields());
        FacilioContext context = new FacilioContext();
        context.put(FacilioConstants.ContextNames.TABLE_NAME, AgentIntegrationKeys.TABLE_NAME);
        context.put(FacilioConstants.ContextNames.FIELDS,FieldFactory.getAgentIntegrationFields());
        context.put(AgentIntegrationKeys.CLIENT_ID,clientId);

        Criteria criteria = new Criteria();
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentIntegrationKeys.NAME),clientId,StringOperators.IS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentIntegrationKeys.INTEGRATION_TYPE), String.valueOf(AgentType.Wattsense.getKey()),NumberOperators.EQUALS));
        criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get(AgentIntegrationKeys.PROP_KEY), AgentIntegrationKeys.DELETED_TIME,StringOperators.IS));

        context.put(FacilioConstants.ContextNames.CRITERIA,criteria);
        HashMap<String, Object> toUpdate = new HashMap<>();
        toUpdate.put(AgentIntegrationKeys.PROP_VALUE, String.valueOf(System.currentTimeMillis()));
        context.put(FacilioConstants.ContextNames.TO_UPDATE_MAP,toUpdate);
        try {
            return  chain.execute(context);
        } catch (Exception e) {
            LOGGER.info(" Exception occurred ");
        }
        return false;
    }

    public static Wattsense getWattsenseFromList(List<Map<String, Object>> rows){
        Wattsense wattsense = new Wattsense();
        if(rows.isEmpty()){
            return wattsense;
        }
        String clientId = (String) rows.get(0).get(AgentIntegrationKeys.NAME);
        wattsense.setClientId(clientId);
        for(Map<String, Object> row:rows){
            String key = (String) row.get(AgentIntegrationKeys.PROP_KEY);
            Object value = row.get(AgentIntegrationKeys.PROP_VALUE);
            if(AgentIntegrationKeys.CERTIFICATE_STORE_ID.equalsIgnoreCase(key)) {
                wattsense.setCertificateStoreId((String) value);
            }
            else if(AgentIntegrationKeys.INTEGRATION_STATUS.equalsIgnoreCase(key)){
                wattsense.setIntegrationStatus(Integer.parseInt((String) value));
            }
            else if(AgentIntegrationKeys.MQTT_ID.equalsIgnoreCase(key)){
                wattsense.setMqttConnectionId((String) value);
            }else if(AgentIntegrationKeys.USER_NAME.equalsIgnoreCase(key)){
                wattsense.setUserName((String) value);
            }else if(AgentIntegrationKeys.DELETED_TIME.equalsIgnoreCase(key)){
                wattsense.setDeletedTime(value.toString());
            }
        }
        LOGGER.info(wattsense.getClientId()+" "+wattsense.getCertificateStoreId()+" "+wattsense.getType());
        return wattsense;

    }

    public static List<Map<String, Object>> getWattsenseList(List<Map<String, Object>> rows) {
        Map<String,Wattsense> wattsenseMap =  new HashMap<>();
        List<Map<String, Object>> wattsenseList = new ArrayList<>();
        for(Map<String,Object> row:rows){
            Wattsense wattsense;
            String clientId = (String) row.get(AgentIntegrationKeys.NAME);
            if(!wattsenseMap.containsKey(clientId)){
                wattsense = new Wattsense();
                wattsense.setClientId(clientId);
                wattsenseMap.put(clientId,wattsense);
            }
            wattsense = wattsenseMap.get(clientId);

            String key = (String) row.get(AgentIntegrationKeys.PROP_KEY);
            Object value = row.get(AgentIntegrationKeys.PROP_VALUE);
            if(AgentIntegrationKeys.CERTIFICATE_STORE_ID.equalsIgnoreCase(key)) {
                wattsense.setCertificateStoreId((String) value);
            }
            else if(AgentIntegrationKeys.INTEGRATION_STATUS.equalsIgnoreCase(key)){
                wattsense.setIntegrationStatus(Integer.parseInt((String) value));
            }
            else if(AgentIntegrationKeys.MQTT_ID.equalsIgnoreCase(key)){
                wattsense.setMqttConnectionId((String) value);
            }else if(AgentIntegrationKeys.USER_NAME.equalsIgnoreCase(key)){
                wattsense.setUserName((String) value);
            }else if(AgentIntegrationKeys.DELETED_TIME.equalsIgnoreCase(key)){
                wattsense.setDeletedTime(value.toString());
            }

            wattsenseMap.replace(clientId,wattsense);
        }
        for(String clientId:wattsenseMap.keySet()){
            Wattsense wattsense = wattsenseMap.get(clientId);
            if(wattsense.getDeletedTime().equalsIgnoreCase(NOT_DELETED)) {
                wattsenseList.add((wattsenseMap.get(clientId).getAsMap()));
            }
        }
        return wattsenseList;
    }

    public static JSONObject reFormatTimeSeriesData(JSONObject payload){
        System.out.println("payload in  "+payload);
        JSONObject wattPayload = new JSONObject();
        Map<String,JSONObject> controllerJsonMap = new HashMap<>();
        Long timeStamp = System.currentTimeMillis();
            if (payload.containsKey(AgentIntegrationKeys.DATA)) {
                JSONArray points = (JSONArray) payload.get(AgentIntegrationKeys.DATA);
                for (Object point : points) {
                    JSONObject pointData = (JSONObject) point;
                    timeStamp = (Long) pointData.get(AgentKeys.TIMESTAMP);
                    if (controllerJsonMap.containsKey(pointData.get(AgentIntegrationKeys.DEVICE_ID))) {
                        (controllerJsonMap.get(pointData.get(AgentIntegrationKeys.DEVICE_ID))).put(pointData.get(AgentIntegrationKeys.PROPERTY), pointData.get(AgentIntegrationKeys.PAYLOAD));
                    } else {
                        controllerJsonMap.put(String.valueOf(pointData.get(AgentIntegrationKeys.DEVICE_ID)), new JSONObject());
                    }
            }
        }
        JSONObject timeSeriesJson = new JSONObject();
        for(String controller : controllerJsonMap.keySet()){
            timeSeriesJson.put(controller,controllerJsonMap.get(controller));
        }
        wattPayload.putAll(timeSeriesJson);
        wattPayload.put(EventUtil.DATA_TYPE, PublishType.TIMESERIES.getValue());
        wattPayload.put(AgentKeys.TIMESTAMP,timeStamp);
        LOGGER.info(" wattsense payload "+wattPayload);
        return wattPayload;
    }

    public static JSONObject reFormatPayload(JSONObject payload,String dataType){
        JSONObject wattPayload = new JSONObject();
        String timeseries = PublishType.TIMESERIES.getValue();
        if(payload.containsKey(AgentIntegrationKeys.MESSAGE)) {
            JSONObject message = new JSONObject();
            message = (JSONObject) payload.get(AgentIntegrationKeys.MESSAGE);
            if (message.containsKey(EventUtil.DATA_TYPE)) {
                dataType = (String) message.get(EventUtil.DATA_TYPE);
            }
        }
        if(dataType == null){
                return wattPayload;
            }
            switch (dataType){
                case "timeseries":
                    LOGGER.info(" timeseries watt payload detected ");
                        return reFormatTimeSeriesData(payload);

            }
            return wattPayload;
        }
}
