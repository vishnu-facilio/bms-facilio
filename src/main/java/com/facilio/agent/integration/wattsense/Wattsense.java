package com.facilio.agent.integration.wattsense;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class Wattsense
{

    private String clientId;
    private String certificateStoreId;
    private String mqttConnectionId;
    private String authStringEncoded;
    private int type =1 ;
    private int integrationStatus = WattsenseUtil.NOT_INTEGRATED;
    private String deletedTime;
    private String userName;
    private long id;

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

    private static final Logger LOGGER = LogManager.getLogger(Wattsense.class.getName());


}
