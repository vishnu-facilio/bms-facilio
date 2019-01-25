package com.facilio.procon.processor;

import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.procon.consumer.FacilioConsumer;
import com.facilio.procon.message.FacilioRecord;
import com.facilio.procon.producer.FacilioProducer;

public abstract class FacilioProcessor implements  Runnable {

    private long orgId;
    private String orgDomainName;
    private FacilioProducer producer;
    private FacilioConsumer consumer;
    private String topic;
    private String eventType;
    private boolean isRunning;

    private static final Logger LOGGER = LogManager.getLogger(FacilioProcessor.class.getName());

    public FacilioProcessor(long orgId, String orgDomainName){
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        this.topic = orgDomainName;
        isRunning = true;
    }

    private void initialize() {
        Thread thread = Thread.currentThread();
        String threadName = "kafka-"+orgDomainName + "-" + eventType;
        thread.setName(threadName);
        LOGGER.info("Initialized processor");
    }

    public long getOrgId() {
        return orgId;
    }

    public String getOrgDomainName() {
        return orgDomainName;
    }

    protected String getTopic() {
        return topic;
    }

    protected FacilioProducer getProducer() {
        return producer;
    }

    protected void setProducer(FacilioProducer producer) {
        this.producer = producer;
    }

    protected FacilioConsumer getConsumer() {
        return consumer;
    }

    protected void setConsumer(FacilioConsumer consumer) {
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
        return consumer.getRecords();
    }

    public List<FacilioRecord> get(long timeout) {
        return consumer.getRecords(timeout);
    }

    public abstract void processRecords(List<FacilioRecord> records);

    public void run() {
        try {
            AccountUtil.setCurrentAccount(orgId);
            initialize();
            while (isRunning) {
                try {
                    List<FacilioRecord> records = get(5000);
                    processRecords(records);
                } catch (Exception e) {
                	LOGGER.error("Error occurred during processing of records", e);
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException in) {
                        LOGGER.error("Interrupted exception ", in);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception while starting facilio processor ", e);
        } finally {
            getConsumer().close();
        }
    }
}