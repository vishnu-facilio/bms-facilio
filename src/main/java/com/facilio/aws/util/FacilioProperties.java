package com.facilio.aws.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerClientBuilder;
import com.amazonaws.services.secretsmanager.model.GetSecretValueRequest;
import com.amazonaws.services.secretsmanager.model.GetSecretValueResult;
import com.facilio.agent.AgentKeys;
import com.facilio.util.FacilioUtil;
import com.facilio.util.RequestUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensymphony.xwork2.ActionContext;

public class FacilioProperties {

    private static final Logger LOGGER = LogManager.getLogger(FacilioProperties.class.getName());
    private static final String AWS_PROPERTY_FILE = "conf/awsprops.properties";


    private static final Properties PROPERTIES = new Properties();
    private static boolean productionEnvironment = false;
    private static boolean developmentEnvironment = true;
    private static boolean securityFilterEnabled = false;
    private static boolean scheduleServer = false;
    private static boolean isSmtp = false;
    private static String region;
    private static String deployment;
    private static String db;
    private static String dbClass;
    private static boolean userServer = true;
    private static boolean messageProcessor = false;
    private static String appDomain;
    private static String pushNotificationKey;
    private static String portalPushNotificationKey;
    private static String environment;
    private static String nodejs;
    private static String pdfjs;
    private static String anomalyTempDir;
    private static String anomalyCheckServiceURL;
    private static String anomalyBucket;
    private static String anomalyBucketDir;
    private static String anomalyPeriodicity;
    private static String anomalyRefreshWaitTimeInSeconds;
    private static String anomalyDetectWaitTimeInSeconds;
    private static String anomalyPredictAPIURL;
    private static String mlModelBuildingApi;
    private static String iotEndPoint;
    private static String defaultDB;
    private static String defaultAppDB;
    private static String defaultAppDBForNewOrg;
    private static String defaultDataSource;
    private static String queueSource;
    private static boolean isOnpremise = false;
    private static boolean sysLogEnabled;
    private static boolean sentryEnabled;
    private static HashSet<String> dbIdentifiers = new HashSet<String>();
    private static Long messageReprocessInterval;
    private static String domain;
    private static String mailDomain;
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
    private static List<String> stageDomains;

    private static String wmsBroadcaster;

    private static String emailClient;
    private static String fileStore;
    private static boolean isServicesEnabled;

    private static String localFileStorePath;
    private static boolean facilioResponse;
    private static String clientVersion;

    private static String mainAppDomain;
    private static String tenantAppDomain;
    private static String clientAppDomain;
    private static String occupantAppDomain;
    private static String vendorAppDomain;

    private static String esDomain;
    private static String esIndex;

    private static String mobileMainAppScheme;
    private static String workQAppScheme;
    private static String mobileServiceportalAppScheme;
    private static String mobileTenantportalAppScheme;
    private static String mobileVendorportalAppScheme;
    private static String mobileClientportalAppScheme;

    private static int authTokenCookieLifespan = 7 * 24 * 60 * 60;
    private static long responseSizeThreshold = 5000000; // In Bytes
    private static List<String> thresholdWhiteListedUrls = null;

    private static boolean userAccessLog = false;
    private static boolean accessLog = false;

    private static String senderEmail;
    private static String senderName;
    private static String mandrillUrl;
    private static String mandrillApiKey;
    
    private static String iamUrl;
    private static String iamDCLookupUrl;
    private static String iamAddDCUserUrl;
    private static String iamregion;

    private static String passwordHashingFunction;

    private static String incomingEmailS3Bucket;
    private static String secretManager;
    private static String service;
    
    private static String cloudAgentUrl;
    private static int maxProcessorThreads;
    @Getter
    private static String developerAppDomain;

    @Getter
    private static String hydraUrl;

    static {
        loadProperties();
    }

    private static List<String> parseCommaSeparatedProps(String propName, String prop) {
        if (StringUtils.isNotEmpty(prop)) {
            try {
                return Collections.unmodifiableList(Arrays.asList(FacilioUtil.splitByComma(prop)));
            }
            catch (Exception e) {
                LOGGER.info(MessageFormat.format("Error occurred while parsing prop {0} with value {1} and so considering it as null", propName, prop));
            }
        }
        return null;
    }

    private static void loadProperties() {
        URL resource = AwsUtil.class.getClassLoader().getResource(AWS_PROPERTY_FILE);
        if (resource != null) {
            try (InputStream stream = resource.openStream()) {
                PROPERTIES.load(stream);
                PROPERTIES.forEach((k, v) -> PROPERTIES.put(k.toString().trim(), v.toString().trim()));
                environment = PROPERTIES.getProperty("environment");

                mailDomain = PROPERTIES.getProperty("mail.domain");
                if (StringUtils.isEmpty(mailDomain)){
                    mailDomain = "facilio.com";
                }

                deployment = PROPERTIES.getProperty("deployment", "facilio");
                region = PROPERTIES.getProperty("region", "us-west-2");
                HashMap<String, String> awsSecret = getPassword(environment +"-app.properties");
                awsSecret.forEach((k,v) -> PROPERTIES.put(k.trim(), v.trim()));
                productionEnvironment = ("demo".equalsIgnoreCase(environment) || "production".equalsIgnoreCase(environment));
                developmentEnvironment = "development".equalsIgnoreCase(environment);
                isOnpremise = "true".equals(PROPERTIES.getProperty("onpremise", "false").trim());
                securityFilterEnabled = Boolean.parseBoolean(PROPERTIES.getProperty("security.filter", "false").trim());
                scheduleServer = "true".equals(getConfig("schedulerServer"));
                messageProcessor = "true".equalsIgnoreCase(PROPERTIES.getProperty("messageProcessor"));
                maxProcessorThreads = Integer.parseInt(PROPERTIES.getOrDefault("processor.max.threads", 50).toString().trim());
                userServer = !scheduleServer;
                db = PROPERTIES.getProperty("db.name");
                dbClass = PROPERTIES.getProperty("db.class");
                appDomain = PROPERTIES.getProperty("app.domain");
                pushNotificationKey = PROPERTIES.getProperty("push.notification.key");
                portalPushNotificationKey = PROPERTIES.getProperty("portal.push.notification.key");
                allowedAppDomains = PROPERTIES.getProperty("allowedapp.domains");
                stageDomains = parseCommaSeparatedProps("stage.domains", PROPERTIES.getProperty("stage.domains"));

                nodejs = PROPERTIES.getProperty("nodejs");
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
                mlModelBuildingApi = PROPERTIES.getProperty("mlModelBuildingApiURL","http://localhost:7444/api/trainingModel");
                sysLogEnabled = "true".equals(PROPERTIES.getProperty("syslog.enabled", "false"));
                sentryEnabled = "true".equals(PROPERTIES.getProperty( "sentry.enabled", "false"));
                userAccessLog = "true".equalsIgnoreCase(PROPERTIES.getProperty("user.access.log", "false"));
                iotEndPoint = (String) PROPERTIES.get("iot.endpoint");
                messageReprocessInterval = Long.parseLong(PROPERTIES.getProperty(AgentKeys.MESSAGE_REPROCESS_INTERVAL,"300000"));
                defaultDataSource = PROPERTIES.getProperty("db.default.ds");
                defaultDB = PROPERTIES.getProperty("db.default.db");
                defaultAppDB = PROPERTIES.getProperty("db.default.app.db");
                defaultAppDBForNewOrg = PROPERTIES.getProperty("db.default.app.db.new.org");
                queueSource = PROPERTIES.getProperty("mQueue.source");
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

                mainAppDomain = PROPERTIES.getProperty("mainapp.domain");
                tenantAppDomain = PROPERTIES.getProperty("tenantportal.domain");
                clientAppDomain = PROPERTIES.getProperty("clientportal.domain");
                vendorAppDomain = PROPERTIES.getProperty("vendorportal.domain");
                occupantAppDomain = PROPERTIES.getProperty("occupantportal.domain");

                mobileMainAppScheme = PROPERTIES.getProperty("mobile.mainapp.scheme");
                mobileServiceportalAppScheme = PROPERTIES.getProperty("mobile.serviceportal.scheme");
                mobileTenantportalAppScheme = PROPERTIES.getProperty("mobile.tenantportal.scheme");
                mobileVendorportalAppScheme = PROPERTIES.getProperty("mobile.vendorportal.scheme");
                mobileClientportalAppScheme = PROPERTIES.getProperty("mobile.clientportal.scheme");
                workQAppScheme = PROPERTIES.getProperty("mobile.workQ.scheme");

                senderEmail = PROPERTIES.getProperty("sender.email");
                senderName = PROPERTIES.getProperty("sender.name");
                mandrillUrl = PROPERTIES.getProperty("mandrill.url");
                mandrillApiKey = PROPERTIES.getProperty("mandrill.apikey");
                
                iamUrl = PROPERTIES.getProperty("iam.url", "app.facilio.com");
                iamDCLookupUrl = getIAMURL() + PROPERTIES.getProperty("iam.dc.lookupurl", "/api/v3/internal/dc/lookup");
                iamAddDCUserUrl = getIAMURL() + PROPERTIES.getProperty("iam.dc.addurl", "/api/v3/internal/dc/add");

                accessLog = "true".equalsIgnoreCase(PROPERTIES.getProperty("facilio.access.log", "false"));

                passwordHashingFunction = PROPERTIES.getProperty("password.hashing.function");
                incomingEmailS3Bucket = PROPERTIES.getProperty("incoming.email.s3.name");
                secretManager = PROPERTIES.getProperty("secretmanger");
                iamregion = PROPERTIES.getProperty("iamregion");
                
                cloudAgentUrl = PROPERTIES.getProperty("agent.cloud.url", "facilioagent.com");

                service = PROPERTIES.getProperty("service");
                developerAppDomain = PROPERTIES.getProperty("facilioapisdomain", "facilioapis.com");
                hydraUrl = PROPERTIES.getProperty("hydraUrl");

                String cookieLifespanProp = PROPERTIES.getProperty("token.cookie.lifespan");
                if (StringUtils.isNotEmpty(cookieLifespanProp)) {
                    try {
                        int timeout = Integer.parseInt(cookieLifespanProp);
                        if (timeout > 0) {
                            authTokenCookieLifespan = timeout * 60;
                        }
                        else {
                            LOGGER.info(MessageFormat.format("Negative value ({0}) for 'user.token.timeout'. So using default timeout ({1})", timeout, authTokenCookieLifespan));
                        }
                    }
                    catch (NumberFormatException e) {
                        LOGGER.info(MessageFormat.format("Exception while parsing 'user.token.timeout' with value : {0}. So using default timeout ({1})",cookieLifespanProp, authTokenCookieLifespan));
                    }
                }
                String responseThreshold = PROPERTIES.getProperty("response.size.threshold");
                if (StringUtils.isNotEmpty(responseThreshold)) {
                    try {
                        long threshold = Long.parseLong(responseThreshold);
                        if (threshold > 0) {
                            responseSizeThreshold = threshold;
                        }
                        else {
                            LOGGER.error(MessageFormat.format("Negative value ({0}) for 'response.size.threshold'. So using default timeout ({1})", responseThreshold, responseSizeThreshold));
                        }
                    }
                    catch (NumberFormatException e) {
                        LOGGER.error(MessageFormat.format("Exception while parsing 'response.size.threshold' with value : {0}. So using default timeout ({1})",responseThreshold, responseSizeThreshold), e);
                    }
                }
                String whiteListedUrls = PROPERTIES.getProperty("response.size.threshold.whitelist");
                thresholdWhiteListedUrls = parseCommaSeparatedProps("response.size.threshold.whitelist", whiteListedUrls);
                esDomain = PROPERTIES.getProperty("es.domain");
                esIndex = PROPERTIES.getProperty("es.index");

                wmsBroadcaster = PROPERTIES.getProperty("wms.broadcaster");

                if(PROPERTIES.containsKey("iot.endpoint.port")) {
                    try {
                        iotEndPointPort = Integer.parseInt(PROPERTIES.getProperty("iot.endpoint.port"));
                    } catch (NumberFormatException e) {
                        LOGGER.info("Exception while parsing iot.endpoint.port, " + PROPERTIES.getProperty("iot.endpoint.port"));
                    }
                }

                localFileStorePath = PROPERTIES.getProperty("files.localFileStore.path");
                clientVersion = PROPERTIES.getProperty("client.version");
                PROPERTIES.put("clientapp.url", getClientAppUrl());
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

    public static String getIamregion() { return iamregion; }

    public static String getMailDomain() {
        return mailDomain;
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
        StringBuilder builder = new StringBuilder();
        HttpServletRequest request = ActionContext.getContext() != null ? ServletActionContext.getRequest() : null;
        builder.append(RequestUtil.getProtocol(request))
                .append("://")
                .append(appDomain);
        return builder.toString();
    }
    
    public static String getAllowedAppDomains() {
        return allowedAppDomains;
    }

    public static List<String> getStageDomains() {
        return stageDomains;
    }

    public static String getPushNotificationKey() {
        return pushNotificationKey;
    }

    public static String getPortalPushNotificationKey() {
        return portalPushNotificationKey;
    }

    public static String getNodeJSLocation() {
    	if (nodejs != null && !nodejs.trim().isEmpty()) {
    		return nodejs;
    	}
    	return "/usr/bin/node";
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
    
    public static String getMlModelBuildingApi() {
    	return mlModelBuildingApi;
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

    public static int getAuthTokenCookieLifespan() {
        return authTokenCookieLifespan;
    }

    public static long getResponseSizeThreshold() {
        return responseSizeThreshold;
    }

    public static List<String> getResponseSizeThresholdWhiteListedUrls() {
        return thresholdWhiteListedUrls;
    }

    public static String getDefaultAppDB() {
        if (StringUtils.isEmpty(defaultAppDB)) {
            return getDefaultDB();
        }
        return defaultAppDB;
    }

    public static String getDefaultAppDBForNewOrg() {
        return defaultAppDBForNewOrg;
    }

    public static String getQueueSource() {
        return queueSource;
    }

    public static boolean isOnpremise() {
        return isOnpremise;
    }

    public static boolean isSecurtiyFilterEnabled() {
        return securityFilterEnabled;
    }

    public static String getIotEndPoint() { return iotEndPoint; }

    public static String getConfig(String name) {
        return PROPERTIES.getProperty(name);
    }

    public static String getDomain() {
        return domain;
    }

    public static String getEsDomain() {
        return esDomain;
    }

    public static String getEsIndex() {
        return esIndex;
    }

    public static String getDatadogClientID() throws Exception {
        String secret = getConfig("dataDogClientId");
        if (!FacilioUtil.isEmptyOrNull(secret)) {
            return secret;
        }
        return "";
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
        AWSSecretsManager client = AWSSecretsManagerClientBuilder.standard().withCredentials(AwsUtil.getAWSCredentialsProvider()).withRegion(region).build();
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

    public static String getRegion() {
        return region;
    }

    public static String getMainAppDomain() {
        return mainAppDomain;
    }

    public static String getTenantAppDomain() {
        return tenantAppDomain;
    }

    public static String getVendorAppDomain() {
        return vendorAppDomain;
    }

    public static String getOccupantAppDomain() {
        return occupantAppDomain;
    }

    public static String getPortalAppDomains() {
        return occupantAppDomain + "," + tenantAppDomain + "," + vendorAppDomain + "," + clientAppDomain;
    }

    public static String getClientAppDomain() {
        return clientAppDomain;
    }

    public static String getMobileMainAppScheme() {
        return mobileMainAppScheme;
    }

    public static String getWorkQAppScheme() {
        return workQAppScheme;
    }

    public static String getMobileServiceportalAppScheme() {
        return mobileServiceportalAppScheme;
    }

    public static String getMobileTenantportalAppScheme() {
        return mobileTenantportalAppScheme;
    }

    public static void setMobileTenantportalAppScheme(String mobileTenantportalAppScheme) {
        FacilioProperties.mobileTenantportalAppScheme = mobileTenantportalAppScheme;
    }

    public static String getMobileVendorportalAppScheme() {
        return mobileVendorportalAppScheme;
    }

    public static void setMobileVendorportalAppScheme(String mobileVendorportalAppScheme) {
        FacilioProperties.mobileVendorportalAppScheme = mobileVendorportalAppScheme;
    }

    public static String getMobileClientportalAppScheme() {
        return mobileClientportalAppScheme;
    }

    public static void setMobileClientportalAppScheme(String mobileClientportalAppScheme) {
        FacilioProperties.mobileClientportalAppScheme = mobileClientportalAppScheme;
    }

    public static void setMobileServiceportalAppScheme(String mobileServiceportalAppScheme) {
        FacilioProperties.mobileServiceportalAppScheme = mobileServiceportalAppScheme;
    }

    public static void setMobileMainAppScheme(String mobileMainAppScheme) {
        FacilioProperties.mobileMainAppScheme = mobileMainAppScheme;
    }

    public static String getWmsBroadcaster() {
        if (StringUtils.isEmpty(wmsBroadcaster)) {
            wmsBroadcaster = "default";
        }
        return wmsBroadcaster;
    }

    public static boolean logUserAccessLog() {
        return userAccessLog;
    }

    public static String senderEmail() {
        return senderEmail;
    }

    public static String senderName() {
        return senderName;
    }

    public static String getMandrillUrl() {
        return mandrillUrl;
    }

    public static String getMandrillApiKey() {
        return mandrillApiKey;
    }

    public static boolean isAccessLogEnable(){
        return accessLog;
    }

    public static String getIAMURL() {
    		if (developmentEnvironment) {
    			return getAppDomain();
    		}
        return iamUrl;
    }
    public static String getIAMDCLookupURL() {
        return iamDCLookupUrl;
    }
    public static String getIAMAddUserURL() {
        return iamAddDCUserUrl;
    }

	public static String getPasswordHashingFunction() {
		return passwordHashingFunction;
	}
	public static String getIncomingEmailS3Bucket() {
        return incomingEmailS3Bucket;
    }
	
    public static long getBuildNumber() {
        Properties buildInfo = (Properties)ServletActionContext.getServletContext().getAttribute("buildinfo");
        return Long.parseLong(buildInfo.getProperty("build.number")); // Shouldn't be null
    }

	public static boolean isInstantJobServer(){
        return Boolean.parseBoolean(getConfig("instantJobServer"));
    }

    public static String getSecretManager() {
        return secretManager;
    }

    public static String getService() {
        return service;
    }

    public static int getMaxProcessorThreads() {
        return maxProcessorThreads;
    }
}