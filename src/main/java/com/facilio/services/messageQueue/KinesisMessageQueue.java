package com.facilio.services.messageQueue;

import java.util.List;
import java.util.Map;

import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.*;
import com.facilio.services.impls.aws.AwsUtil;
import com.facilio.services.kinesis.KinesisIRecordProcessorFactory;
import com.facilio.services.kinesis.KinesisStreamProcessor;
import org.apache.log4j.LogManager;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.facilio.accounts.dto.Organization;
import com.facilio.iam.accounts.util.IAMOrgUtil;

public class KinesisMessageQueue extends MessageQueue {
    private static org.apache.log4j.Logger log = LogManager.getLogger(KinesisMessageQueue.class.getName());
    private static final KinesisMessageQueue INSTANCE =new KinesisMessageQueue();

    private static volatile AmazonKinesis kinesis = null;

    static MessageQueue getClient() {
        return INSTANCE;
    }


    private KinesisMessageQueue(){}
    private static final Object LOCK = new Object();
    public static AmazonKinesis getKinesisClient() {
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
    public List<String> getTopics(){
        AmazonKinesis kinesis = getKinesisClient();
        ListStreamsResult streamList = kinesis.listStreams();
        List<String> streamNames = streamList.getStreamNames();
        return streamNames;
    }

    @Override
    List<Organization> getOrgs() throws Exception {
        return IAMOrgUtil.getOrgs();
    }


    public void initiateProcessFactory(long orgId, String orgDomainName, String type) {
    	try {
    		new Thread(() -> KinesisStreamProcessor.run(orgId, orgDomainName, type, getProcessorFactory(orgId,orgDomainName,type))).start();
    	}
    	catch (Exception e){
    		log.info("Exception occurred ", e);
    	}

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

}
