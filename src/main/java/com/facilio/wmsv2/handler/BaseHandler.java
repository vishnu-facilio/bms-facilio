package com.facilio.wmsv2.handler;

import com.facilio.wmsv2.message.Group;
import com.facilio.wmsv2.message.Message;
import com.facilio.wmsv2.message.TopicHandler;

@TopicHandler(
        priority = Integer.MAX_VALUE,
        topic = "#"
)
public class BaseHandler {
    private String[] topics;
    private int priority;
    private TopicHandler.DELIVER_TO deliverTo;
    private Group group;

    private int recordTimeout;

//    private boolean sendToAllWorkers;

    final void setTopics(String[] topics) {
        this.topics = topics;
    }

    public final String[] getTopics() {
        return topics;
    }

    final void setPriority(int priority) {
        this.priority = priority;
    }

    final int getPriority() {
        return priority;
    }

    public final TopicHandler.DELIVER_TO getDeliverTo() {
        return deliverTo;
    }

    final void setDeliverTo(TopicHandler.DELIVER_TO deliverTo) {
        this.deliverTo = deliverTo;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public int getRecordTimeout() {
        return recordTimeout;
    }

    public void setRecordTimeout(int recordTimeout) {
        this.recordTimeout = recordTimeout;
    }

    //    public boolean isSendToAllWorkers() {
//        return sendToAllWorkers;
//    }

//    public void setSendToAllWorkers(boolean sendToAllWorkers) {
//        this.sendToAllWorkers = sendToAllWorkers;
//    }

    public void processIncomingMessage(Message message) {
    }

    public Message processOutgoingMessage(Message message) {
        return message;
    }
}
