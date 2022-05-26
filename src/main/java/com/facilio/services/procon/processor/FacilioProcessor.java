package com.facilio.services.procon.processor;

import java.util.List;

import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.metrics.LongCounter;
import io.opentelemetry.api.metrics.Meter;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Scope;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.services.procon.consumer.FacilioConsumer;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.services.procon.producer.FacilioProducer;

public abstract class FacilioProcessor implements  Runnable {

    private long orgId;
    private String orgDomainName;
    private FacilioProducer producer;
    private FacilioConsumer consumer;
    private String topic;
    private String eventType;
    private boolean isRunning;

    private static final Logger LOGGER = LogManager.getLogger(FacilioProcessor.class.getName());
    private static final Tracer TRACER = GlobalOpenTelemetry.getTracer(FacilioProcessor.class.getName());
    private static final Meter SAMPLE_METER =
            GlobalOpenTelemetry.getMeter(FacilioProcessor.class.getName());
    private static final LongCounter COUNTER =
            SAMPLE_METER
                    .counterBuilder("directories_search_count")
                    .setDescription("Counts directories accessed while searching for files.")
                    .setUnit("unit")
                    .build();

    public FacilioProcessor(long orgId, String orgDomainName) {
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        this.topic = orgDomainName;
        isRunning = true;
    }

    private void initialize() {
        Thread thread = Thread.currentThread();
        String threadName = getThreadName();
        thread.setName(threadName);
        LOGGER.info("Initialized processor");
    }

    protected abstract String getThreadName();

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

    public Object put(FacilioRecord record) throws Exception {
        return producer.putRecord(getTopic(), record);
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
            Span span = TRACER.spanBuilder(FacilioProcessor.class.getName()).setSpanKind(SpanKind.CONSUMER).startSpan();
            AccountUtil.setCurrentAccount(orgId);
            initialize();
            while (isRunning) {
                try (Scope scope = span.makeCurrent()) {
                    List<FacilioRecord> records = get(5000);
                    processRecords(records);
                } catch (Exception e) {
                    LOGGER.error("Error occurred during processing of records", e);
                    try {
                        Thread.sleep(5000L);
                    } catch (InterruptedException in) {
                        LOGGER.error("Interrupted exception ", in);
                    }
                } finally {
                    span.end();
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception while starting facilio processor ", e);
        } finally {
            getConsumer().close();
            LOGGER.info("Closing consumer");
        }
    }
}