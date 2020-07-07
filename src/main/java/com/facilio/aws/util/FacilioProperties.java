package com.facilio.aws.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.facilio.agent.AgentKeys;
import com.fasterxml.jackson.databind.ObjectMapper;

public class FacilioProperties {

    private static final Logger LOGGER = LogManager.getLogger(FacilioProperties.class.getName());
    private static final String AWS_PROPERTY_FILE = "conf/awsprops.properties";


    private static final Properties PROPERTIES = new Properties();
    private static boolean productionEnvironment = false;
    private static boolean developmentEnvironment = true;
    private static boolean scheduleServer = false;
    private static boolean isSmtp = false;
    private static String deployment;
    private static String db;
    private static String dbClass;
    private static boolean userServer = true;
    private static boolean messageProcessor = false;
    private static String appDomain;
    private static String clientAppUrl;
    private static String allowedPortalDomains;
    private static String pushNotificationKey;
    private static String portalPushNotificationKey;
    private static String environment;
    private static String kafkaProducer;
    private static String kafkaConsumer;
    private static String pdfjs;
    private static String anomalyTempDir;
    private static String anomalyCheckServiceURL;
    private static String anomalyBucket;
    private static String anomalyBucketDir;
    private static String anomalyPeriodicity;
    private static String anomalyRefreshWaitTimeInSeconds;
    private static String anomalyDetectWaitTimeInSeconds;
    private static String anomalyPredictAPIURL;
    private static String iotEndPoint;
    private static String defaultDB;
    private static String defaultAppDB;
    private static String defaultDataSource;
    private static String messageQueue;
    private static boolean isOnpremise = false;
    private static boolean sysLogEnabled;
    private static boolean sentryEnabled;
    private static HashSet<String> dbIdentifiers = new HashSet<String>();
    private static Long messageReprocessInterval;
    private static String domain;
    private static String iotUser;
    private static String iotPassword;
    private static String iotVirtualHost;
    private static int iotEndPointPort;
    private static String iotExchange;
    private static String bridgeUrl;
    private static String sentrydsn;
    private static String sentryslownessdsn;
    private static String sentryschedulerdsn;
    private static String pythonAI;
    private static String pythonPath;
    private static String allowedAppDomains;

    private static String portalDomains;
    private static String emailClient;
    private static String fileStore;
    private static boolean isServicesEnabled;

    private static String localFileStorePath;
    private static boolean facilioResponse;
    private static String clientVersion;

    static {
        loadProperties();
    }

    private static void loadProperties() {
        URL resource = AwsUtil.class.getClassLoader().getResource(AWS_PROPERTY_FILE);
        if (resource != null) {
            try (InputStream stream = resource.openStream()) {
                PROPERTIES.load(stream);
                PROPERTIES.forEach((k, v) -> PROPERTIES.put(k.toString().trim(), v.toString().trim()));
                environment = PROPERTIES.getProperty("environment");
                deployment = PROPERTIES.getProperty("deployment", "facilio");
                HashMap<String, String> awsSecret = getPassword(environment +"-app.properties");
                awsSecret.forEach((k,v) -> PROPERTIES.put(k.trim(), v.trim()));
                productionEnvironment = ("demo".equalsIgnoreCase(environment) || "production".equalsIgnoreCase(environment));
                developmentEnvironment = "development".equalsIgnoreCase(environment);
                isOnpremise = "true".equals(PROPERTIES.getProperty("onpremise", "false").trim());
                scheduleServer = "true".equals(getConfig("schedulerServer"));
                messageProcessor = "true".equalsIgnoreCase(PROPERTIES.getProperty("messageProcessor"));
                userServer = !scheduleServer;
                db = PROPERTIES.getProperty("db.name");
                dbClass = PROPERTIES.getProperty("db.class");
                appDomain = PROPERTIES.getProperty("app.domain");
                pushNotificationKey = PROPERTIES.getProperty("push.notification.key");
                portalPushNotificationKey = PROPERTIES.getProperty("portal.push.notification.key");
                clientAppUrl = "https://"+ appDomain;
                allowedPortalDomains = PROPERTIES.getProperty("allowedportal.domains");
                allowedAppDomains = PROPERTIES.getProperty("allowedapp.domains");
                portalDomains = PROPERTIES.getProperty("portal.domain");

                kafkaProducer = PROPERTIES.getProperty("kafka.producer");
                kafkaConsumer = PROPERTIES.getProperty("kafka.consumer");
                isSmtp = "smtp".equalsIgnoreCase(PROPERTIES.getProperty("email.type"));
                isServicesEnabled="enabled".equalsIgnoreCase(PROPERTIES.getProperty("services.isEnabled"));
                emailClient = PROPERTIES.getProperty("service.email");
                fileStore=PROPERTIES.getProperty("service.file.store");
                anomalyTempDir = PROPERTIES.getProperty("anomalyTempDir", "/tmp");
                anomalyCheckServiceURL = PROPERTIES.getProperty("anomalyCheckServiceURL", "http://localhost:7444/api");
                anomalyBucket = PROPERTIES.getProperty("anomalyBucket","facilio-analytics");
                anomalyBucketDir = PROPERTIES.getProperty("anomalyBucketDir","stage/anomaly");
                anomalyPeriodicity = PROPERTIES.getProperty("anomalyPeriodicity","30");
                anomalyRefreshWaitTimeInSeconds = PROPERTIES.getProperty("anomalyRefreshWaitTimeInSeconds","10");
                anomalyDetectWaitTimeInSeconds = PROPERTIES.getProperty("anomalyDetectWaitTimeInSeconds","3");
                anomalyPredictAPIURL = PROPERTIES.getProperty("anomalyPredictServiceURL","http://localhost:7444/api");
                sysLogEnabled = "true".equals(PROPERTIES.getProperty("syslog.enabled", "false"));
                sentryEnabled = "true".equals(PROPERTIES.getProperty( "sentry.enabled", "false"));
                iotEndPoint = (String) PROPERTIES.get("iot.endpoint");
                messageReprocessInterval = Long.parseLong(PROPERTIES.getProperty(AgentKeys.MESSAGE_REPROCESS_INTERVAL,"300000"));
                defaultDataSource = PROPERTIES.getProperty("db.default.ds");
                defaultDB = PROPERTIES.getProperty("db.default.db");
                defaultAppDB = PROPERTIES.getProperty("db.default.app.db");
                messageQueue = PROPERTIES.getProperty("service.mQueue");
                domain = PROPERTIES.getProperty("domain");
                iotUser = PROPERTIES.getProperty("iot.accessKeyId");
                iotPassword = PROPERTIES.getProperty("iot.secretKeyId");
                iotVirtualHost = PROPERTIES.getProperty("iot.virtual.host");
                iotExchange = PROPERTIES.getProperty("iot.exchange");
                bridgeUrl = PROPERTIES.getProperty("bridge.url");
                sentrydsn = PROPERTIES.getProperty("sentry.dsn");
                sentryslownessdsn = PROPERTIES.getProperty("sentry.slowness.dsn");
                sentryschedulerdsn=PROPERTIES.getProperty("sentry.scheduler.dsn");
                pythonAI = PROPERTIES.getProperty("pythonai.url");
                pythonPath = PROPERTIES.getProperty("pythonPath");
                facilioResponse = "true".equals(PROPERTIES.get("response.size"));
                if(PROPERTIES.containsKey("iot.endpoint.port")) {
                    try {
                        iotEndPointPort = Integer.parseInt(PROPERTIES.getProperty("iot.endpoint.port"));
                    } catch (NumberFormatException e) {
                        LOGGER.info("Exception while parsing iot.endpoint.port, " + PROPERTIES.getProperty("iot.endpoint.port"));
                    }
                }

                localFileStorePath = PROPERTIES.getProperty("files.localFileStore.path");
                clientVersion = PROPERTIES.getProperty("client.version");
                PROPERTIES.put("clientapp.url", clientAppUrl);
                URL resourceDir = AwsUtil.class.getClassLoader().getResource("");
                if(resourceDir != null) {
                    File file = new File(resourceDir.getPath());
                    if (file.getParentFile() != null) {
                        pdfjs = file.getParentFile().getParentFile().getAbsolutePath() + "/js";
                    }
                }

                String db = PROPERTIES.getProperty("db.identifiers");
                if(db != null) {
                    String[] dbNames = db.split(",");
                    for(String dbName : dbNames) {
                        String identifier = dbName.trim();
                        dbIdentifiers.add(identifier);
                    }
                }
                LOGGER.info(getIotEndPoint() + "iot endpoint");
            } catch (IOException e) {
                LOGGER.info("Exception while trying to load property file " + AWS_PROPERTY_FILE);
            }
        }
    }

    public static String getLocalFileStorePath() {
        return localFileStorePath;
    }

    public static Long getMessageReprocessInterval() {
        return messageReprocessInterval;
    }

    public static boolean isProduction() {
        return productionEnvironment;
    }

    public static boolean isDevelopment() {
        return developmentEnvironment;
    }

    public static String getServerName() {
        return getAppDomain();
    }

    public static String getDBClass() {
        return dbClass;
    }

    public static boolean isScheduleServer() {
        return scheduleServer;
    }

    public static boolean isUserServer() {
        return userServer;
    }

    public static boolean isMessageProcessor() {
        return messageProcessor;
    }

    public static String getAppDomain() {
        return appDomain;
    }

    public static String getClientAppUrl() {
        return clientAppUrl;
    }
    
    public static String getAllowedPortalDomainsUrl() {
        return allowedPortalDomains;
    }

    public static String getPortalDomain() {
        return portalDomains;
    }

    public static String getAllowedAppDomains() {
        return allowedAppDomains;
    }

    public static String getPushNotificationKey() {
        return pushNotificationKey;
    }

    public static String getPortalPushNotificationKey() {
        return portalPushNotificationKey;
    }

    public static String getKafkaProducer() {
        return kafkaProducer;
    }

    public static String getKafkaConsumer() {
        return kafkaConsumer;
    }

    public static boolean isSmtp() {
        return isSmtp;
    }

    public static String getPdfjsLocation() {
        return pdfjs;
    }

    public static String getAnomalyTempDir() {
        return anomalyTempDir;
    }

    public static String getAnomalyCheckServiceURL() {
        return anomalyCheckServiceURL;
    }

    public static String getAnomalyBucket() {
        return anomalyBucket;
    }

    public static String getAnomalyBucketDir() {
        return anomalyBucketDir;
    }

    public static String getAnomalyPeriodicity() {
        return anomalyPeriodicity;
    }

    public static String getAnomalyRefreshWaitTimeInSeconds() {
        return anomalyRefreshWaitTimeInSeconds;
    }

    public static String getAnomalyDetectWaitTimeInSeconds() {
        return anomalyDetectWaitTimeInSeconds;
    }

    public static String getAnomalyPredictAPIURL() {
        return anomalyPredictAPIURL;
    }

    public static boolean isSysLogEnabled() {
        return sysLogEnabled;
    }

    public static HashSet<String> getDBIdentifiers() {
        return dbIdentifiers;
    }

    public static String getDefaultDataSource() {
        return defaultDataSource;
    }

    public static String getDefaultDB() {
        return defaultDB;
    }

    public static boolean isSentryEnabled() {return sentryEnabled;}

    public static String getDefaultAppDB() {
        if (StringUtils.isEmpty(defaultAppDB)) {
            return getDefaultDB();
        }
        return defaultAppDB;
    }

    public static String getMessageQueue() {
        return messageQueue;
    }

    public static boolean isOnpremise() {
        return isOnpremise;
    }

    public static String getIotEndPoint() { return iotEndPoint; }

    public static String getConfig(String name) {
        return PROPERTIES.getProperty(name);
    }

    public static String getDomain() {
        return domain;
    }

    public static HashMap<String, String> getPassword(String secretKey) {

        HashMap<String, String> secretMap = new HashMap<>();

        switch (deployment) {
            case "aws":
                secretMap.putAll(getSecretFromAws(secretKey));
                break;
            case "moro":
                secretMap.putAll(getSecretFromFile(secretKey));
                break;
            case "machinestalk":
                secretMap.putAll(getSecretFromFile(secretKey));
                break;
            default:
                secretMap.putAll(getSecretFromFile(secretKey));
        }
        return secretMap;
    }

    private static Map<String, String> getSecretFromFile(String secretKey) {
        HashMap<String, String> secret = new HashMap<>();
        if("db".equals(secretKey)) {
            secret.put("host", PROPERTIES.getProperty("db.host"));
            secret.put("port", PROPERTIES.getProperty("db.port"));
            secret.put("username", PROPERTIES.getProperty("db.username"));
            secret.put("password", PROPERTIES.getProperty("db.password"));
            secret.put("db.default.db", PROPERTIES.getProperty("db.default.db"));
        }
        return secret;

    }

    private static HashMap<String, String> getSecretFromAws(String secretKey) {
        HashMap<String, String> secretMap = new HashMap<>();
        AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard().withCredentials(AwsUtil.getAWSCredentialsProvider()).withRegion(Regions.US_WEST_2).build();
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest().withSecretId(secretKey);
        GetSecretValueResult getSecretValueResult = null;
        try {
            getSecretValueResult = client.getSecretValue(getSecretValueRequest);
        } catch (Exception e) {
            LOGGER.info("Exception while getting secret ", e);
        }
        if (getSecretValueResult != null) {
            String secretBinaryString = getSecretValueResult.getSecretString();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                secretMap.putAll(objectMapper.readValue(secretBinaryString, HashMap.class));
                String connections = FacilioProperties.getConfig(secretKey+".connections");
                if(connections != null) {
                    secretMap.put("maxConnections", connections);
                }
            } catch (IOException e) {
                LOGGER.info("exception while reading value from secret manager ", e);
            }
        }
        return secretMap;
    }

    public static String getIotUser() {
        return iotUser;
    }

    public static String getIotPassword() {
        return iotPassword;
    }

    public static String getIotVirtualHost() {
        return iotVirtualHost;
    }

    public static int getIotEndPointPort() {
        return iotEndPointPort;
    }

    public static String getIotExchange() {
        return iotExchange;
    }

    public static String getEmailClient(){return emailClient;}

    public static String getBridgeUrl() {
        return bridgeUrl;
    }

    public static String getsentrydsn() {return sentrydsn; }

    public static String getSentryslownessdsn() {return sentryslownessdsn;}

    public static String getSentryschedulerdsn() {return sentryschedulerdsn;}
    
    public static String getPythonAI() {
        return pythonAI;
    }
    
    public static String getPythonPath() {
        return pythonPath;
    }

    public static String getFileStore() {
        return fileStore;
    }

    public static boolean isServicesEnabled() {
        return isServicesEnabled;
    }

    public static String getEnvironment() {
        return environment;
    }

    public static boolean enableFacilioResponse() {
        return facilioResponse;
    }


    public static String getClientVersion() {
        return clientVersion;
    }
}