package com.facilio.aws.util;

import com.facilio.agent.AgentType;
import com.facilio.events.tasker.tasks.EventUtil;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class IotPolicy
{
    private String type;
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }


    private List<String> publishtopics;
    public List<String> getPublishtopics() { return publishtopics; }
    public void setPublishtopics(List<String> publishtopics) { this.publishtopics = publishtopics; }

    private List<String> publishTypes;
    public List<String> getPublishTypes() { return publishTypes; }
    public void setPublishTypes(List<String> publishTypes) { this.publishTypes = publishTypes; }

    private boolean toModify;
    public boolean isToModify() { return toModify; }
    public void setToModify(boolean toModify) { this.toModify = toModify; }

    private String streamName;
    public String getStreamName() { return streamName; }
    public void setStreamName(String streamName) { this.streamName = streamName; }

    private List<String> subscribeTopics;
    public List<String> getSubscribeTopics() { return subscribeTopics; }
    public void setSubscribeTopics(List<String> subscribeTopics) { this.subscribeTopics = subscribeTopics; }

    private List<String> receiveTopics;
    public List<String> getReceiveTopics() { return receiveTopics; }
    public void setReceiveTopics(List<String> receiveTopics) { this.receiveTopics = receiveTopics; }

    public List<String> getClientIds() { return clientIds; }
    public void setClientIds(List<String> clientIds) { this.clientIds = clientIds; }
    private List<String> clientIds;

    private String name;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Map<String, String> getPublishTypeAndTopicMap() { return publishTypeAndTopicMap; }
    public void setPublishTypeAndTopicMap(Map<String, String> publishTypeAndTopicMap) { this.publishTypeAndTopicMap = publishTypeAndTopicMap; }
    private Map<String,String> publishTypeAndTopicMap;

    public Map<String,String> getMappedTopicAndPublished(){
            Map<String, String> topicAndPublishTypeMap = new HashMap<>();
        for (String publishtopic : publishtopics) {
            topicAndPublishTypeMap.put(publishtopic,publishTypes.get(publishtopics.indexOf(publishtopic)));
        }
        setPublishTypeAndTopicMap(topicAndPublishTypeMap);
        return topicAndPublishTypeMap;
    }

    public String getSql(String topic,String publishType) {
        if(AgentType.Wattsense.getLabel().equalsIgnoreCase(type) && ( publishType != null ) ){
            return "SELECT * as data , '" + publishType +"' as "+ EventUtil.DATA_TYPE +" FROM '" + topic + "'";
        }
        return "SELECT * FROM '" + topic + "'";
    }

    public JSONObject getPolicyDocument() {
      return policyDocument;
    }

    public void setPolicyDocument(JSONObject policyDocument) {
        this.policyDocument = policyDocument;
    }

    private JSONObject policyDocument;
}
