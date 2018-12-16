package com.facilio.kafka.event;

import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.events.context.EventRuleContext;
import com.facilio.fw.BeanFactory;
import com.facilio.kafka.FacilioKafkaConsumer;
import com.facilio.kafka.FacilioKafkaProducer;
import com.facilio.procon.message.FacilioRecord;
import com.facilio.procon.processor.FacilioProcessor;
import com.facilio.server.ServerInfo;
import org.apache.kafka.common.TopicPartition;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;

public class EventProcessor extends FacilioProcessor {

    private List<EventRuleContext> eventRules = new ArrayList<>();
    private Map<String, Integer> eventCountMap = new HashMap<>();
    private long lastEventTime = System.currentTimeMillis();

    private TopicPartition topicPartition;
    private FacilioKafkaConsumer consumer;
    private FacilioKafkaProducer producer;

    private static final String DATA_TYPE = "PUBLISH_TYPE";
    private static final Logger LOGGER = LogManager.getLogger(EventProcessor.class.getName());


    public EventProcessor(long orgId, String orgDomainName) {

        super(orgId, orgDomainName);
        String clientName = orgDomainName +"-event-";
        String environment = AwsUtil.getConfig("environment");
        String consumerGroup = clientName + environment;
        consumer = new FacilioKafkaConsumer(ServerInfo.getHostname(), consumerGroup);
        consumer.subscribe(getTopic());
        setConsumer(consumer);
        producer = new FacilioKafkaProducer(getTopic());
    }


    public void processRecords(List<FacilioRecord> records) {
        for (FacilioRecord record : records) {
            boolean alarmCreated = false;
            JSONObject object = record.getData();
            try {
                if (object.containsKey(DATA_TYPE)) {
                    String dataType = (String) object.get(DATA_TYPE);
                    if ("event".equalsIgnoreCase(dataType)) {
                        alarmCreated = processEvents(object);
                    }
                } else {
                    alarmCreated = processEvents(object);
                }
            } catch (Exception e) {
                CommonCommandUtil.emailException("KEventProcessor", "Error in processing records in EventProcessor ", e,  object.toJSONString());
                LOGGER.info("Exception occurred ", e);
            }
        }
    }

    private boolean processEvents(JSONObject object) throws Exception {

        long timeStamp = System.currentTimeMillis();
        if (object.containsKey("timestamp")) {
            timeStamp = Long.parseLong(String.valueOf(object.get("timestamp")));
        }
        String partitionKey = (String) object.get("key");

        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", getOrgId());
        long currentExecutionTime = bean.processEvents(timeStamp, object, eventRules, eventCountMap, lastEventTime, partitionKey);
        if(currentExecutionTime != -1) {
            lastEventTime = currentExecutionTime;
            return true;
        }
        return false;
    }
}
