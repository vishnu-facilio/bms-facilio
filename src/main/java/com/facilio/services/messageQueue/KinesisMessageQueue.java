package com.facilio.services.messageQueue;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.*;
import com.facilio.queue.source.MessageSource;
import com.facilio.services.impls.aws.AwsUtil;
import com.facilio.services.kinesis.KinesisIRecordProcessorFactory;
import com.facilio.services.kinesis.KinesisStreamProcessor;
import com.facilio.services.procon.message.FacilioRecord;

import lombok.NonNull;

import org.apache.log4j.LogManager;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;


class KinesisMessageQueue extends MessageQueue {
	
	private static org.apache.log4j.Logger log = LogManager.getLogger(KinesisMessageQueue.class.getName());
	
    private static volatile AmazonKinesis kinesis = null;

    private static final Object LOCK = new Object();

    private static AmazonKinesis getKinesisClient() {
        if(kinesis == null) {
            synchronized (LOCK) {
                if(kinesis == null) {
                    kinesis = AmazonKinesisClientBuilder.standard()
                            .withCredentials(AwsUtil.getAWSCredentialsProvider())
                            .withRegion(AwsUtil.getRegion())
                            .build();
                }
            }
        }
        return kinesis;
    }

    KinesisMessageQueue(@NonNull MessageSource messageQueueSource) {
		super(messageQueueSource);
	}
    
    @Override
	protected MessageSource getSource() {
    	// Implement kinesis message source when needed
		return messageQueueSource;
	}

    @Override
    public List<String> getTopics(){
        AmazonKinesis kinesis = getKinesisClient();
        ListStreamsResult streamList = kinesis.listStreams();
        return streamList.getStreamNames();
    }


    @Override
    public void createQueue(String streamName) {
        log.info(" creating kinesis stream "+streamName);
        try {
            CreateStreamResult streamResult = getKinesisClient().createStream(streamName, 1);
            log.info("Stream created : " + streamName + " with status " + streamResult.getSdkHttpMetadata().getHttpStatusCode());
        } catch (ResourceInUseException resourceInUse){
            log.info(" Exception Stream exists for name : " + streamName);
        }
    }

    @Override
    public Object put(String streamName, FacilioRecord record) {
        return com.facilio.aws.util.AwsUtil.getKinesisClient().putRecord(streamName, ByteBuffer.wrap(record.getData().toJSONString().getBytes(Charset.defaultCharset())), record.getPartitionKey());
    }

    @Override
    public Object put(String streamName, List<FacilioRecord> records) {
        List<PutRecordsRequestEntry> entries = new ArrayList<>();
        records.forEach(record -> {
            PutRecordsRequestEntry entry = new PutRecordsRequestEntry()
                    .withData(ByteBuffer.wrap(record.getData().toJSONString().getBytes(Charset.defaultCharset())))
                    .withPartitionKey(record.getPartitionKey());
            entries.add(entry);
        });
        return com.facilio.aws.util.AwsUtil.getKinesisClient().putRecords(new PutRecordsRequest().withStreamName(streamName).withRecords(entries));
    }


    private IRecordProcessorFactory getProcessorFactory(long orgId, String orgDomainName) {
        return new KinesisIRecordProcessorFactory(orgId,orgDomainName, messageQueueSource);
    }

    protected void initiateProcessFactory(long orgId, String topic, String applicationName, int processorId) throws Exception {
        try {
            new Thread(() -> KinesisStreamProcessor.run(orgId, topic, applicationName, getProcessorFactory(orgId, topic))).start();
        } catch (Exception e) {
            log.info("Exception occurred ", e);
        }
    }


	@Override
	protected int getConsumersOnlineCount(String topic) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

}
