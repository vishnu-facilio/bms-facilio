package com.facilio.procon.processor;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.procon.message.FacilioRecord;
import com.facilio.procon.consumer.FacilioConsumer;
import com.facilio.procon.producer.FacilioProducer;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.List;

public abstract class FacilioProcessor implements  Runnable {

    private long orgId;
    private String orgDomainName;
    private FacilioProducer producer;
    private FacilioConsumer consumer;
    private String topic;
    private String eventType;

    private static final Logger LOGGER = LogManager.getLogger(FacilioProcessor.class.getName());

    public FacilioProcessor(long orgId, String orgDomainName){
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        this.topic = orgDomainName;
    }

    private void initialize() {
        Thread thread = Thread.currentThread();
        String threadName = orgDomainName + "-" + eventType;
        thread.setName(threadName);
    }

    public FacilioProducer getProducer() {
        return producer;
    }

    public void setProducer(FacilioProducer producer) {
        this.producer = producer;
    }

    public FacilioConsumer getConsumer() {
        return consumer;
    }

    public void setConsumer(FacilioConsumer consumer) {
        this.consumer = consumer;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Object put(FacilioRecord record) {
        return producer.putRecord(record);
    }

    public List<FacilioRecord> get() {
        return consumer.getRecords(topic);
    }

    public List<FacilioRecord> get(long timeout) {
        return consumer.getRecords(timeout);
    }

    public abstract void processRecords(List<FacilioRecord> records);

    public void run() {
        try {
            AccountUtil.setCurrentAccount(orgId);
            initialize();
            while (true) {
                try {
                    List<FacilioRecord> records = get(5000);
                    processRecords(records);
                } catch (Exception e) {
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException in) {
                        LOGGER.info("Interrupted exception ", in);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Exception while starting facilio processor ", e);
        }
    }
}