package com.facilio.aws.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.agent.AgentType;
import com.facilio.events.tasker.tasks.EventUtil;
import com.google.api.client.util.ArrayMap;

public class IotPolicy
{
    private String type;
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }


    private List<String> publishtopics = new ArrayList<>();
    public List<String> getPublishtopics() { return publishtopics; }
    public void setPublishtopics(List<String> publishtopics) { this.publishtopics = publishtopics; }

    private List<String> arnPublishtopics = new ArrayList<>();
    public List<String> getArnPublishtopics() { return arnPublishtopics; }
    public void setArnPublishtopics(List<String> arnPublishtopics) { this.arnPublishtopics = arnPublishtopics; }

    private List<String> publishTypes = new ArrayList<>();
    public List<String> getPublishTypes() { return publishTypes; }
    public void setPublishTypes(List<String> publishTypes) { this.publishTypes = publishTypes; }

    private List<String> arnPublishTypes = new ArrayList<>();
    public List<String> getArnPublishTypes() {
        return arnPublishTypes;
    }
    public void setArnPublishTypes(List<String> arnPublishTypes) {
        this.arnPublishTypes = arnPublishTypes;
    }


    private boolean toModify;
    public boolean isToModify() { return toModify; }
    public void setToModify(boolean toModify) { this.toModify = toModify; }

    private String streamName;
    public String getStreamName() { return streamName; }
    public void setStreamName(String streamName) { this.streamName = streamName; }

    private List<String> subscribeTopics = new ArrayList<>();
    public List<String> getSubscribeTopics() { return subscribeTopics; }
    public void setSubscribeTopics(List<String> subscribeTopics) { this.subscribeTopics = subscribeTopics; }

    private List<String> arnSubscribeTopics = new ArrayList<>();
    public List<String> getArnSubscribeTopics() {
        return arnSubscribeTopics;
    }
    public void setArnSubscribeTopics(List<String> arnSubscribeTopics) {
        this.arnSubscribeTopics = arnSubscribeTopics;
    }

    private List<String> receiveTopics = new ArrayList<>();
    public List<String> getReceiveTopics() { return receiveTopics; }
    public void setReceiveTopics(List<String> receiveTopics) { this.receiveTopics = receiveTopics; }

    private List<String> arnReceiveTopics = new ArrayList<>();
    public List<String> getArnReceiveTopics() {
        return arnReceiveTopics;
    }
    public void setArnReceiveTopics(List<String> arnReceiveTopics) {
        this.arnReceiveTopics = arnReceiveTopics;
    }


    private List<String> clientIds = new ArrayList<>();
    public List<String> getClientIds() { return clientIds; }
    public void setClientIds(List<String> clientIds) {
        this.clientIds = clientIds;
    }

    private List<String> arnClientIds = new ArrayList<>();
    public List<String> getArnClientIds() {
        return arnClientIds;
    }
    public void setArnClientIds(List<String> arnClientIds) {
        this.arnClientIds = arnClientIds;
    }

    private String name;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    private Map<String,String> publishTypeAndTopicMap = new ArrayMap<>();
    public Map<String, String> getPublishTypeAndTopicMap() { return publishTypeAndTopicMap; }
    public void setPublishTypeAndTopicMap(Map<String, String> publishTypeAndTopicMap) { this.publishTypeAndTopicMap = publishTypeAndTopicMap; }

    public Map<String,String> getMappedTopicAndPublished(){
            Map<String, String> topicAndPublishTypeMap = new HashMap<>();
        for (String publishtopic : publishtopics) {
            topicAndPublishTypeMap.put(publishtopic,publishTypes.get(publishtopics.indexOf(publishtopic)));
        }
        setPublishTypeAndTopicMap(topicAndPublishTypeMap);
        return topicAndPublishTypeMap;
    }

    public String getSql(String topic,String publishType) {
        if(AgentType.WATTSENSE.getLabel().equalsIgnoreCase(type) && ( publishType != null ) ){
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
