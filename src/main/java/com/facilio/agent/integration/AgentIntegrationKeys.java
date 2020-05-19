package com.facilio.agent.integration;

public class AgentIntegrationKeys
{

    public static final String TABLE_NAME = "Wattsense_Integrations";
    public static final String API_KEY = "apiKey";
    public static final String API_SECRET = "apiSecret";
    public static final String AUTH_STRING_ENC= "authStringEncoded";
    public static final String INTEGRATION_TYPE = "type";
    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String INTEGRATION_STATUS = "integrationStatus";



    //wattsense
    public static final String MQTT_ID = "mqttConnectionId";
    public static final String CERTIFICATE_STORE_ID = "certificateStoreId";
    public static final String CLIENT_ID = "clientId";
    public static final String CERTIFICATE = "certificate";
    public static final String CERTIFICATE_KEY = "certificateKey";
    public static final String CLIENT_ID_TAG = "wattsense";
    public static final String CERT_KEY_FILE = "wattsense_fedge.zip";
    public static final String USER_NAME = "userName";
    public static final String DELETED_TIME = "deletedTime";
    public static final String TOPIC_WT_EVENTS = "/wt/events";
    public static final String TOPIC_WT_ALARMS = "/wt/alarms";
    public static final String TOPIC_WT_VALUES = "/wt/values";
    public static final String TOPIC_WT_CMD = "/wt/cmd";
    public static final String TOPIC_WT_WILDCARD = "/wt";
    public static final String WATT_FEDGE_ZIP = "/wattsenst_fedge.zip";
    public static final String WATT_FEDGE_FILE_ID = "wattFedgeFileId";
    public static final String WATTSENSE_IOT_POLICY = "wattsense";


    //APIs
    public static final String WATT_AUTH_API  = "wattsenseAuthApi";
    public static final String WATT_MQTT_PATH = "wattsenseMqttPath";
    public static final String WATT_CERTIFICATE_STORE_API ="WattsenseCertificateStoreApi";
    public static final String WATT_BASE_PATH = "wattsenseBasePath";
    public static final String WATT_AWS_MQTT_ENDPOINT_BASE = "WattsenseAwsMqttEndpointBase";
    public static final String WATT_INITIATE_MQTT_API = "wattsenseInitiateMqttApi";
    public static final String CERT_FILE_NAME = "facilio.crt";
    public static final String KEY_FILE_NAME = "facilio-private.key";

    //reformat
    public static final String MESSAGE = "message";
    public static final String ARRIVAL_TIME = "arrivalTime";
    public static final String DEVICE = "device";
    public static final String DATA = "data";
    public static final String PROPERTY ="property";
    public static final String DEVICE_ID = "deviceId";
    public static final String PAYLOAD = "payload";


}
