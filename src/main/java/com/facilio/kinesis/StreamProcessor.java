package com.facilio.kinesis;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.services.kinesis.metrics.interfaces.MetricsLevel;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.net.InetAddress;

public class StreamProcessor {

    private static Logger log = LogManager.getLogger(StreamProcessor.class.getName());

    public static void run(long orgId, String orgDomainName, String eventType, IRecordProcessorFactory recordProcessorFactory) {

        try {
            AccountUtil.setCurrentAccount(orgId);
            String streamName = AwsUtil.getIotKinesisTopic(orgDomainName);
            String clientName = orgDomainName +"-" + eventType + "-";
            String environment = AwsUtil.getConfig("environment");
            String applicationName = clientName + environment;
            java.security.Security.setProperty("networkaddress.cache.ttl", "60");

            String workerId = InetAddress.getLocalHost().getHostName();
            KinesisClientLibConfiguration kinesisClientLibConfiguration =
                        new KinesisClientLibConfiguration(applicationName, streamName, AwsUtil.getAWSCredentialsProvider(), workerId)
                                .withRegionName(AwsUtil.getRegion())
                                .withKinesisEndpoint(AwsUtil.getConfig("kinesisEndpoint"))
                                .withMaxLeaseRenewalThreads(3)
                                .withInitialLeaseTableReadCapacity(1)
                                .withInitialLeaseTableWriteCapacity(1)
                                .withMetricsLevel(MetricsLevel.NONE);
/*
            if("production".equals(environment)) {
                kinesisClientLibConfiguration = kinesisClientLibConfiguration.withMetricsLevel(MetricsLevel.NONE);
            } else {
                kinesisClientLibConfiguration = kinesisClientLibConfiguration.withMetricsLevel(MetricsLevel.NONE);
            }*/

            //IRecordProcessorFactory recordProcessorFactory = new EventProcessorFactory(orgId, orgDomainName);

            Worker worker = new Worker.Builder()
                    .recordProcessorFactory(recordProcessorFactory)
                    .config(kinesisClientLibConfiguration)
                    .build();

            System.out.printf("Running %s to process stream %s as worker %s...\n", applicationName, streamName, workerId);

            worker.run();
        } catch (Exception e){
            log.info("Exception occurred ", e);
        }

    }
}
