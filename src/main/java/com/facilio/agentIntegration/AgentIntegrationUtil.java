package com.facilio.agentIntegration;

import com.facilio.aws.util.AwsUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class AgentIntegrationUtil {
    private static final Logger LOGGER = LogManager.getLogger(AgentIntegrationUtil.class.getName());

    static {
        loadProperties();
    }
    private static String wattsenseAuthApi;
    private static String wattsenseCertificateStoreApi;
    private static String mqttConnectionApi;
    private static String awsMqttEndpoint;
    private static String initiateMqttApi1;
    private static String initiateMqttApi2;
    private static String deleteCertificateStoreApi;
    private static String deleteMqttConnectionApi;


    public static String getWattsenseAuthApi() {
        return wattsenseAuthApi;
    }

    public static String getWattsenseCertificateStoreApi() {
        return wattsenseCertificateStoreApi;
    }

    public static String getMqttConnectionApi() {
        return mqttConnectionApi;
    }

    public static String getAwsMqttEndpoint() {
        return awsMqttEndpoint;
    }

    public static String getInitiateMqttApi1() {
        return initiateMqttApi1;
    }

    public static String getInitiateMqttApi2() {
        return initiateMqttApi2;
    }

    public static String getDeleteCertificateStoreApi() {
        return deleteCertificateStoreApi;
    }

    public static String getDeleteMqttConnectionApi() {
        return deleteMqttConnectionApi;
    }

    public static String getAgentIntegrationPropertyFile() {
        return AGENT_INTEGRATION_PROPERTY_FILE;
    }

    private static final String AGENT_INTEGRATION_PROPERTY_FILE = "conf/agentIntegration.properties";

    private static void loadProperties() {
        Properties PROPERTIES = new Properties();
        URL resource = AwsUtil.class.getClassLoader().getResource(AGENT_INTEGRATION_PROPERTY_FILE);
        if (resource != null) {
            try (InputStream stream = resource.openStream()) {
                PROPERTIES.load(stream);
                PROPERTIES.forEach((k, v) -> PROPERTIES.put(k.toString().trim(), v.toString().trim()));
                wattsenseAuthApi = PROPERTIES.getProperty(AgentIntegrationKeys.WATT_AUTH_API);
                wattsenseCertificateStoreApi = PROPERTIES.getProperty(AgentIntegrationKeys.WATT_CERTIFICATE_STORE_API);
                mqttConnectionApi = PROPERTIES.getProperty(AgentIntegrationKeys.WATT_MQTT_CONNECTION_API);
                awsMqttEndpoint = PROPERTIES.getProperty(AgentIntegrationKeys.WATT_AWS_MQTT_ENDPOINT);
                initiateMqttApi1 = PROPERTIES.getProperty(AgentIntegrationKeys.WATT_INITIATE_MQTT_API_1);
                initiateMqttApi2 = PROPERTIES.getProperty(AgentIntegrationKeys.WATT_INITIATE_MQTT_API_2);
                deleteCertificateStoreApi = PROPERTIES.getProperty(AgentIntegrationKeys.WATT_DELETE_CERT_STORE_API);
                deleteMqttConnectionApi = PROPERTIES.getProperty(AgentIntegrationKeys.WATT_DELETE_MQTT_CONNECTION_API);
            } catch (IOException e) {
                LOGGER.info("Exception occurred ",e);

            }
        }
    }

}