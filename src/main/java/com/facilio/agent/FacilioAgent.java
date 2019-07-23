package com.facilio.agent;

import java.util.HashSet;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.agentIntegration.AgentIntegrationKeys;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.IotPolicy;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.constants.FacilioConstants;

public class FacilioAgent
{
    private  String name;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAgentName() { return name; }
    public void setAgentName(String agentName) { this.name = agentName; }

    private  String deviceDetails;
    public String getDeviceDetails() { return deviceDetails; }
    public void setDeviceDetails(String deviceDetails) { this.deviceDetails = deviceDetails; }

    private  String version;
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    private  Integer state;
    public Integer getState() { return state; }
    public void setState(Integer state) { this.state = state; }

    private  Boolean connectionStatus;
    public Boolean getConnectionStatus() { return connectionStatus; }
    public void setConnectionStatus(Boolean connectionStatus) { this.connectionStatus = connectionStatus; }

    private  Long dataInterval;
    public Long getDataInterval() { return dataInterval; }
    public void setDataInterval(Long dataInterval) { this.dataInterval = dataInterval; }

    private  Integer numberOfControllers;
    public Integer getNumberOfControllers() { return numberOfControllers; }
    public void setNumberOfControllers(Integer numberOfControllers) { this.numberOfControllers = numberOfControllers; }

    private  String type;
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    private Long id;
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    private Long siteId;
    public Long getSiteId() { return siteId; }
    public void setSiteId(Long siteId) { this.siteId = siteId; }

    private Long deletedTime;
    public Long getDeletedTime() { return deletedTime; }
    public void setDeletedTime(Long deletedTime) { this.deletedTime = deletedTime; }

    private Boolean writable;
    public Boolean getWritable() { return writable; }
    public void setWritable(Boolean writable) { this.writable = writable; }

    private  Long lastModifiedTime;
    public Long getLastModifiedTime() { return lastModifiedTime; }
    public void setLastModifiedTime(Long lastModifiedTime) { this.lastModifiedTime = lastModifiedTime; }

    private  Long lastUpdatedTime;
    public Long getLastUpdatedTime() { return lastUpdatedTime; }
    public void setLastUpdatedTime(Long lastUpdatedTime) { this.lastUpdatedTime = lastUpdatedTime; }

    public String getAgentDeviceDetails() { return deviceDetails; }
    public void setAgentDeviceDetails(String agentDeviceDetails) { this.deviceDetails = agentDeviceDetails; }

    public String getAgentVersion() { return version; }
    public void setAgentVersion(String agentVersion) { this.version = agentVersion; }

    public Integer getAgentState() { return state; }
    public void setAgentState(Integer agentState) { this.state = agentState; }

    public Boolean getAgentConnStatus() { return connectionStatus; }
    public void setAgentConnStatus(Boolean agentConnStatus) { this.connectionStatus = agentConnStatus; }

    public Long getAgentDataInterval() { return dataInterval; }
    public void setAgentDataInterval(Long agentDataInterval) { this.dataInterval = agentDataInterval; }

    public Integer getAgentNumberOfControllers() { return numberOfControllers; }
    public void setAgentNumberOfControllers(Integer agentNumberOfControllers) { this.numberOfControllers = agentNumberOfControllers; }

    public String getAgentType() { return type; }
    public void setAgentType(String agentType) { this.type = agentType; }

    private Long lastDataRecievedTime;
    public Long getLastDataRecievedTime() { return lastDataRecievedTime; }

    public void setLastDataRecievedTime(Long lastDataRecievedTime) { this.lastDataRecievedTime = lastDataRecievedTime; }
    private HashSet<ControllerContext> controllers;
    public HashSet<ControllerContext> getControllers() { return controllers; }
    public void setControllers(HashSet<ControllerContext> controllers) { this.controllers = controllers; }
    public void addController(ControllerContext controller) {
        if(controllers == null) {
            controllers = new HashSet<>();
        }
        controllers.add(controller);
    }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    private String displayName;


    public static String getCertFileName(String type) {
        if(AgentType.Wattsense.getLabel().equalsIgnoreCase(type)){
            return AgentIntegrationKeys.WATT_FEDGE_ZIP;
        }
        else {
            return FacilioConstants.ContextNames.FEDGE_ZIP;
        }
    }

    public static String getCertFileId(String type) {
        if(AgentType.Wattsense.getLabel().equalsIgnoreCase(type)){
            return AgentIntegrationKeys.WATT_FEDGE_FILE_ID;
        }
        else {
            return FacilioConstants.ContextNames.FEDGE_CERT_FILE_ID;
        }
    }
    public static IotPolicy getIotRule(String name,String type){
        LOGGER.info(" creating IotRule for "+name+"   and type "+type);
        IotPolicy policy = new IotPolicy();
        if(AgentType.Wattsense.getLabel().equalsIgnoreCase(type)){
            policy.setToModify(true);
            policy.setClientIds(new String[] {AwsUtil.getIotArnClientId(name)});
            policy.setPublishtopics(new String[] { (name +AgentIntegrationKeys.TOPIC_WT_EVENTS) ,
                    ( name+ AgentIntegrationKeys.TOPIC_WT_ALARMS ),
                    ( name+ AgentIntegrationKeys.TOPIC_WT_VALUES )});
            policy.setReceiveTopics(new String[]{name+AgentIntegrationKeys.TOPIC_WT_CMD});
            policy.setSubscribeTopics(new String[]{AwsUtil.getIotArnTopicFilter(name)+AgentIntegrationKeys.TOPIC_WT_CMD});
            policy.setPublishTypes(new String[]{PublishType.event.getValue(),PublishType.event.getValue(),PublishType.timeseries.getValue()});
            LOGGER.info("topic and pubtype "+policy.getMappedTopicAndPublished());
            return policy;
        }
        else {
            policy.setClientIds(new String[] { AwsUtil.getIotArnClientId(name)});
            policy.setPublishtopics(new String[] {name});
            policy.setSubscribeTopics(new String[]{ AwsUtil.getIotArnTopicFilter(name)+"/msgs"});
            policy.setReceiveTopics(new String[]{name+"/msgs"});
            return policy;
        }
    }
    private static final Logger LOGGER = LogManager.getLogger(FacilioAgent.class.getName());
}
