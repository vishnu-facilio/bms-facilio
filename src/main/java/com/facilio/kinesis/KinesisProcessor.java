package com.facilio.kinesis;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.model.ResourceNotFoundException;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.events.tasker.tasks.EventProcessorFactory;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.timeseries.TimeSeriesProcessorFactory;

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
                String orgDomainName = (String) prop.get("domain");
                startProcessor(orgId, orgDomainName);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void startProcessor(long orgId, String orgDomainName) {
        AmazonKinesis kinesis = AwsUtil.getKinesisClient();
        try {
            if(orgDomainName != null) {
                kinesis.describeStream(orgDomainName);
                System.out.println("Starting kinesis processor for org : " + orgDomainName + " id " + orgId);
                initiateProcessFactory(orgId, orgDomainName, "event");
                initiateProcessFactory(orgId, orgDomainName, "timeSeries");            }
        } catch (ResourceNotFoundException e){
            System.out.println("Kinesis stream not found for org : " + orgDomainName +" id "+ orgId);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    
    private static void initiateProcessFactory(long orgId, String orgDomainName, String type) {

    	try {

    		new Thread(() -> StreamProcessor.run(orgId, orgDomainName, type, getProcessorFactory(orgId,orgDomainName,type))).start();
    	}
    	catch (Exception e){
    		e.printStackTrace();
    	}

    }

    private static IRecordProcessorFactory getProcessorFactory(long orgId, String orgDomainName,String type) {

    	switch(type){

    	case "event" :{
    		return new EventProcessorFactory(orgId, orgDomainName);
    	}
    	case "timeSeries" :{

    		return new TimeSeriesProcessorFactory(orgId, orgDomainName);
    	}

    	}
    	return null;
    }
    
    
}
