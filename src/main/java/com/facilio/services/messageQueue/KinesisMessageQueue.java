package com.facilio.services.messageQueue;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.*;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.services.impls.aws.AwsUtil;
import com.facilio.services.kinesis.KinesisIRecordProcessorFactory;
import com.facilio.services.kinesis.KinesisStreamProcessor;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.services.procon.processor.FacilioProcessor;
import org.apache.log4j.LogManager;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.facilio.accounts.dto.Organization;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import org.json.simple.JSONObject;

public class KinesisMessageQueue extends MessageQueue {
    private static org.apache.log4j.Logger log = LogManager.getLogger(KinesisMessageQueue.class.getName());
    private static final KinesisMessageQueue INSTANCE =new KinesisMessageQueue();

    private static volatile AmazonKinesis kinesis = null;

    static MessageQueue getClient() {
        return INSTANCE;
    }


    private KinesisMessageQueue(){}
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

    @Override
    void startProcessor(long orgId, String orgDomainName) {
        try {
            if (orgDomainName != null && getSTREAMS().contains(orgDomainName)) {
                log.info("Starting kafka processor for org : " + orgDomainName + " id " + orgId);
                new Thread(() -> KinesisStreamProcessor.run(orgId, orgDomainName, "processor", getProcessorFactory(orgId, orgDomainName, "type"))).start();
                getExistingOrgs().add(orgDomainName);
            }
        } catch (Exception e) {
            log.info("Exception occurred ", e);
        }
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


    private static IRecordProcessorFactory getProcessorFactory(long orgId, String orgDomainName, String type) {
        return new KinesisIRecordProcessorFactory(orgId,orgDomainName);
    }

    public void put(long orgId, String orgDomainName, String type, FacilioRecord record) throws Exception {

        if (AccountUtil.getCurrentOrg() != null && FacilioProperties.isProduction()) {
            PutRecordResult recordResult = com.facilio.aws.util.AwsUtil.getKinesisClient().putRecord(orgDomainName, ByteBuffer.wrap(record.getData().toJSONString().getBytes(Charset.defaultCharset())), orgDomainName);
            int status = recordResult.getSdkHttpMetadata().getHttpStatusCode();
            if (status != 200) {
                log.info("Couldn't add data to stream - " + orgDomainName);
            }
        }
    }

}
