package com.facilio.agentIntegration.wattsense;

import com.facilio.agentIntegration.AgentIntegrationKeys;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class Wattsense
{


    private String clientId;
    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    private String certificateStoreId;
    public String getCertificateStoreId() { return certificateStoreId; }
    public void setCertificateStoreId(String certificateStoreId) { this.certificateStoreId = certificateStoreId; }

    private String mqttConnectionId;
    public String getMqttConnectionId() { return mqttConnectionId; }
    public void setMqttConnectionId(String mqttConnectionId) { this.mqttConnectionId = mqttConnectionId; }

    private String authStringEncoded;
    public String getAuthStringEncoded() { return authStringEncoded; }
    public void setAuthStringEncoded(String authStringEncoded) { this.authStringEncoded = authStringEncoded; }

    private int type =1 ;
    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    private int integrationStatus = WattsenseUtil.NOT_INTEGRATED;
    public int getIntegrationStatus() { return integrationStatus; }
    public void setIntegrationStatus(int integrationStatus) { this.integrationStatus = integrationStatus; }

    public String getDeletedTime() { return deletedTime; }
    public void setDeletedTime(String deletedTime) { this.deletedTime = deletedTime; }
    private String deletedTime;

    private String userName;
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Map<String,Object> getAsMap(){
        Map<String , Object> map = new HashMap<>();
        map.put(AgentIntegrationKeys.MQTT_ID, getMqttConnectionId());
        map.put(AgentIntegrationKeys.CERTIFICATE_STORE_ID, getCertificateStoreId());
        map.put(AgentIntegrationKeys.CLIENT_ID, getClientId());
        map.put(AgentIntegrationKeys.INTEGRATION_STATUS, getIntegrationStatus());
        map.put(AgentIntegrationKeys.USER_NAME,getUserName());
        map.put(AgentIntegrationKeys.DELETED_TIME,getDeletedTime());
        return map;
    }
    private static final Logger LOGGER = LogManager.getLogger(Wattsense.class.getName());


}
