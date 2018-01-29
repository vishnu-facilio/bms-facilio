package com.facilio.kinesis;

import java.net.InetAddress;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.services.kinesis.metrics.interfaces.MetricsLevel;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;

public class StreamProcessor {

    public static void run(long orgId, String orgDomainName, String eventType, IRecordProcessorFactory recordProcessorFactory) {

        try {
            AccountUtil.setCurrentAccount(orgId);
            String streamName = AwsUtil.getIotKinesisTopic(orgDomainName);
            String clientName = orgDomainName +"-" + eventType + "-";
            String environment = AwsUtil.getConfig("environment");
            String applicationName = clientName + environment;
            java.security.Security.setProperty("networkaddress.cache.ttl", "60");

            String workerId = clientName + InetAddress.getLocalHost().getCanonicalHostName();

            KinesisClientLibConfiguration kinesisClientLibConfiguration =
                    new KinesisClientLibConfiguration(applicationName, streamName, AwsUtil.getAWSCredentialsProvider(), workerId)
                            .withRegionName(AwsUtil.getRegion())
                            .withKinesisEndpoint(AwsUtil.getConfig("kinesisEndpoint"))
                            .withInitialLeaseTableReadCapacity(1)
                            .withInitialLeaseTableWriteCapacity(1);
            if("production".equals(environment)) {
                kinesisClientLibConfiguration = kinesisClientLibConfiguration.withMetricsLevel(MetricsLevel.SUMMARY);
            } else {
                kinesisClientLibConfiguration = kinesisClientLibConfiguration.withMetricsLevel(MetricsLevel.NONE);
            }

            //IRecordProcessorFactory recordProcessorFactory = new EventProcessorFactory(orgId, orgDomainName);

            Worker worker = new Worker.Builder()
                    .recordProcessorFactory(recordProcessorFactory)
                    .config(kinesisClientLibConfiguration)
                    .build();

            System.out.printf("Running %s to process stream %s as worker %s...\n", applicationName, streamName, workerId);

            worker.run();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
