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
    private static String initiateMqttApi;
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

    public static String getInitiateMqttApi() {
        return initiateMqttApi;
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
                String wattBasePath = PROPERTIES.getProperty(AgentIntegrationKeys.WATT_BASE_PATH);
                String wattMqttPath = PROPERTIES.getProperty(AgentIntegrationKeys.WATT_MQTT_PATH);
                wattsenseAuthApi =  wattBasePath + PROPERTIES.getProperty(AgentIntegrationKeys.WATT_AUTH_API);
                mqttConnectionApi = wattBasePath + wattMqttPath ;
                wattsenseCertificateStoreApi = mqttConnectionApi + PROPERTIES.getProperty(AgentIntegrationKeys.WATT_CERTIFICATE_STORE_API);
                awsMqttEndpoint = PROPERTIES.getProperty(AgentIntegrationKeys.WATT_AWS_MQTT_ENDPOINT_BASE) + AwsUtil.getIotEndPoint()+":8883";
                initiateMqttApi = PROPERTIES.getProperty(AgentIntegrationKeys.WATT_INITIATE_MQTT_API);
                deleteCertificateStoreApi = wattsenseCertificateStoreApi;
                deleteMqttConnectionApi = mqttConnectionApi;
            } catch (IOException e) {
                LOGGER.info("Exception occurred ",e);

            }
        }
    }

}