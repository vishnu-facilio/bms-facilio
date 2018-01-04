package com.facilio.events.tasker.tasks;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.ResourceNotFoundException;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.sql.GenericSelectRecordBuilder;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class KinesisProcessor {

    private static Properties getLoggingProps(){
        Properties properties = new Properties();
        properties.put("log4j.rootLogger", "ERROR,stdout");
        properties.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender" );
        properties.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        return properties;
    }

    public static void startProcessor() {

//        PropertyConfigurator.configure(getLoggingProps());

        List<FacilioField> columnList = AccountConstants.getOrgFields();

        GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table("Organizations")
                .select(columnList);

        try {
            List<Map<String, Object>> props = builder.get();
            for (Map<String, Object> prop : props) {
                Long orgId = (Long) prop.get("orgId");
                String orgName = (String) prop.get("domain");
                startProcessor(orgId, orgName);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void startProcessor(long orgId, String orgName) {
        AmazonKinesis kinesis = AwsUtil.getKinesisClient();
        try {
            if(orgName != null) {
                kinesis.describeStream(orgName);
                System.out.println("Starting kinesis processor for org : " + orgName + " id " + orgId);
                new Thread(() -> EventStreamProcessor.run(orgId, orgName)).start();
            }
        } catch (ResourceNotFoundException e){
            System.out.println("Kinesis stream not found for org : " + orgName +" id "+ orgId);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
