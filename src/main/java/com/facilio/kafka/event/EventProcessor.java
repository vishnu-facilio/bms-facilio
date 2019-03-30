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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventProcessor extends FacilioProcessor {

    private List<EventRuleContext> eventRules = new ArrayList<>();
    private Map<String, Integer> eventCountMap = new HashMap<>();
    private long lastEventTime = System.currentTimeMillis();

    private static final String DATA_TYPE = "PUBLISH_TYPE";
    private static final Logger LOGGER = LogManager.getLogger(EventProcessor.class.getName());


    public EventProcessor(long orgId, String orgDomainName, String type) {
        super(orgId, orgDomainName);
        String clientName = orgDomainName +"-event-";
        String environment = AwsUtil.getConfig("environment");
        String consumerGroup = clientName + environment;
        setConsumer(new FacilioKafkaConsumer(ServerInfo.getHostname(), consumerGroup, getTopic()));
        setProducer(new FacilioKafkaProducer(getTopic()));
        setEventType(type);
        LOGGER.info("Initializing event processor " + orgDomainName);
    }


    public void processRecords(List<FacilioRecord> records) {
        for (FacilioRecord record : records) {
            boolean alarmCreated = false;
            JSONObject object = record.getData();
            try {
                if (object.containsKey(DATA_TYPE)) {
                    String dataType = (String) object.get(DATA_TYPE);
                    if ("event".equalsIgnoreCase(dataType)) {
                         alarmCreated = processEvents(record);
                    }
                } else {
                     alarmCreated = processEvents(record);
                }
            } catch (Exception e) {
                CommonCommandUtil.emailException("KEventProcessor", "Error in processing records in EventProcessor ", e,  object.toJSONString());
                LOGGER.error("Exception occurred ", e);
            }
            finally {
            	if (alarmCreated) {
            		getConsumer().commit(record);
            	}
            }
        }
    }

    private boolean processEvents(FacilioRecord record) throws Exception {

        long timeStamp = record.getTimeStamp();
        JSONObject object = record.getData();
        if (object.containsKey("timestamp")) {
            timeStamp = Long.parseLong(String.valueOf(object.get("timestamp")));
        }
        String partitionKey = record.getPartitionKey();

        ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", getOrgId());
        long currentExecutionTime = bean.processEvents(timeStamp, object, eventRules, eventCountMap, lastEventTime, partitionKey);
        if(currentExecutionTime != -1) {
            lastEventTime = currentExecutionTime;
            return true;
        }
        return false;
    }
}
