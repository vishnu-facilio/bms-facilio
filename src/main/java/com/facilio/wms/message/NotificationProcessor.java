package com.facilio.wms.message;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.metrics.interfaces.MetricsLevel;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.aws.util.AwsUtil;
import com.facilio.server.ServerInfo;
import com.facilio.wms.endpoints.SessionManager;
import com.facilio.wms.util.WmsApi;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;


public class NotificationProcessor implements IRecordProcessor {

    private static final Logger LOGGER = LogManager.getLogger(NotificationProcessor.class.getName());
    private static final CharsetDecoder DECODER = Charset.forName("UTF-8").newDecoder();
    private static final NotificationProcessorCheckpointer CHECKPOINTER = new NotificationProcessorCheckpointer();
    private String shardId = "";

    public void initialize(InitializationInput initializationInput) {
        Thread thread = Thread.currentThread();
        String threadName = "facilio-notifications";
        thread.setName(threadName);
        shardId = initializationInput.getShardId();
        new Thread(CHECKPOINTER, threadName+"-checkpointer").start();
        LOGGER.info("Starting Notification processor " + initializationInput.getExtendedSequenceNumber());
    }

    public void processRecords(ProcessRecordsInput processRecordsInput) {
        long startTime = System.currentTimeMillis();
        long timeToSendMessage = 0L;
        for (Record record : processRecordsInput.getRecords()) {
            String data = "";
            try {
                record.getSequenceNumber();
                data = DECODER.decode(record.getData()).toString();
                if(data.isEmpty()){
                    continue;
                }
                JSONParser parser = new JSONParser();
                JSONObject payLoad = (JSONObject) parser.parse(data);
                Message message = Message.getMessage(payLoad);
                LOGGER.debug("Going to send message to " + message.getTo() + " from " + message.getFrom());

                timeToSendMessage = timeToSendMessage + SessionManager.getInstance().sendMessage(message);

                CHECKPOINTER.addCheckPointer(shardId, processRecordsInput.getCheckpointer());
                CHECKPOINTER.addProcessedSequenceNumber(shardId, record.getSequenceNumber());
            }
            catch (Exception e) {
                LOGGER.info("Exception occurred "+ data + " , " , e);
            }
        }
        LOGGER.debug("Processed " + processRecordsInput.getRecords().size() +  " in " + (System.currentTimeMillis() - startTime) + " ms. Time to send Messaage " + timeToSendMessage);
    }

    public void shutdown(ShutdownInput shutdownInput) {
        LOGGER.info("Shutting down at check point " + shutdownInput.getCheckpointer().toString());
    }

    public static void run(IRecordProcessorFactory recordProcessorFactory) {

        try {
            String streamName =  WmsApi.getKinesisNotificationTopic();
            String environment = AwsUtil.getConfig("environment");
            String applicationName = environment+"-"+ ServerInfo.getHostname();
            java.security.Security.setProperty("networkaddress.cache.ttl", "60");

            String workerId = ServerInfo.getHostname();
            KinesisClientLibConfiguration kinesisClientLibConfiguration =
                    new KinesisClientLibConfiguration(applicationName, streamName, AwsUtil.getAWSCredentialsProvider(), workerId)
                            .withRegionName(AwsUtil.getRegion())
                            .withKinesisEndpoint(AwsUtil.getConfig("kinesisEndpoint"))
                            .withMaxRecords(500)
                            .withMaxLeaseRenewalThreads(3)
                            .withIdleMillisBetweenCalls(200L)
                            .withIdleTimeBetweenReadsInMillis(200L)
                            .withInitialLeaseTableReadCapacity(1)
                            .withInitialLeaseTableWriteCapacity(1);

            if("production".equals(environment)) {
                kinesisClientLibConfiguration = kinesisClientLibConfiguration.withMetricsLevel(MetricsLevel.SUMMARY);
            } else {
                kinesisClientLibConfiguration = kinesisClientLibConfiguration.withMetricsLevel(MetricsLevel.NONE);
            }

            Worker worker = new Worker.Builder()
                    .recordProcessorFactory(recordProcessorFactory)
                    .config(kinesisClientLibConfiguration)
                    .build();

            LOGGER.info("Running "+ applicationName +" to process stream " + streamName + " as worker ..." + workerId);
            worker.run();
        } catch (Exception e){
            LOGGER.info("Exception occurred ", e);
        }

    }
}
