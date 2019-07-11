package com.facilio.aws.util;

import com.facilio.events.tasker.tasks.EventUtil;

import java.util.HashMap;
import java.util.Map;

public class IotPolicy
{
    private String type;
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    private String[] publishtopics;
    public String[] getPublishtopics() { return publishtopics; }
    public void setPublishtopics(String[] publishtopics) { this.publishtopics = publishtopics; }

    private String[] publishTypes;
    public String[] getPublishTypes() { return publishTypes; }
    public void setPublishTypes(String[] publishTypes) { this.publishTypes = publishTypes; }

    private boolean toModify;
    public boolean isToModify() { return toModify; }
    public void setToModify(boolean toModify) { this.toModify = toModify; }

    private String[] subscribeTopics;
    public String[] getSubscribeTopics() { return subscribeTopics; }
    public void setSubscribeTopics(String[] subscribeTopics) { this.subscribeTopics = subscribeTopics; }

    private String[] receiveTopics;
    public String[] getReceiveTopics() { return receiveTopics; }
    public void setReceiveTopics(String[] receiveTopics) { this.receiveTopics = receiveTopics; }

    public String[] getClientIds() { return clientIds; }
    public void setClientIds(String[] clientIds) { this.clientIds = clientIds; }
    private String[] clientIds;

    private String name;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    private String policyName;
    public String getPolicyName() { return policyName; }
    public void setPolicyName(String policyName) { this.policyName = policyName; }


    public Map<String,String> getMappedTopicAndPublished(){
            Map<String, String> topicAndPublishTypeMap = new HashMap<>();
            for (int i = 0; i < publishtopics.length; i++) {
                System.out.println(publishtopics[i]+"   "+publishTypes[i]);
                topicAndPublishTypeMap.put(publishtopics[i], publishTypes[i]);
            }
        return topicAndPublishTypeMap;
    }

    public String getSql(String topic, Map<String,String> publishTypeMap) {
        if(isToModify()){
            return "SELECT * ," + publishTypeMap.get(topic) +" as"+ EventUtil.DATA_TYPE +" FROM '" + topic + "'";
        }
        return "SELECT * FROM '" + topic + "'";
    }
}
