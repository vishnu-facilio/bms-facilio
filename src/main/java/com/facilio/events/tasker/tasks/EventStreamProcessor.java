package com.facilio.events.tasker.tasks;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.aws.util.AwsUtil;

import java.net.InetAddress;

public class EventStreamProcessor {

    public static void run(long orgId, String orgName) {

        try {
            AccountUtil.setCurrentAccount(orgId);
            String streamName = AwsUtil.getIotKinesisTopic(orgName);
            String clientName = orgId + AwsUtil.getConfig("environment");
            java.security.Security.setProperty("networkaddress.cache.ttl", "60");

            AWSCredentials credentials = new BasicAWSCredentials(AwsUtil.getConfig("accessKeyId"), AwsUtil.getConfig("secretKeyId"));
            AWSCredentialsProvider provider = new AWSStaticCredentialsProvider(credentials);

            String workerId = orgId + InetAddress.getLocalHost().getCanonicalHostName();

            KinesisClientLibConfiguration kinesisClientLibConfiguration =
                    new KinesisClientLibConfiguration(clientName, streamName, provider, workerId)
                            .withRegionName(AwsUtil.getConfig("region"))
                            .withKinesisEndpoint(AwsUtil.getConfig("kinesisEndpoint"));

            IRecordProcessorFactory recordProcessorFactory = new EventProcessorFactory(orgId, orgName);

            Worker worker = new Worker.Builder()
                    .recordProcessorFactory(recordProcessorFactory)
                    .config(kinesisClientLibConfiguration)
                    .build();

            System.out.printf("Running %s to process stream %s as worker %s...\n", clientName, streamName, workerId);

            worker.run();
        } catch (Exception e){
            e.printStackTrace();
        }

    }
}
