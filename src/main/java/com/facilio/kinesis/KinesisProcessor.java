package com.facilio.kinesis;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;

import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.model.LimitExceededException;
import com.amazonaws.services.kinesis.model.ListStreamsResult;
import com.amazonaws.services.kinesis.model.ResourceNotFoundException;
import com.facilio.accounts.dto.Organization;
import com.facilio.aws.util.AwsUtil;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.iam.accounts.util.IAMOrgUtil;
import com.facilio.processor.ProcessorFactory;

public class KinesisProcessor {

    private static final HashSet<String> STREAMS = new HashSet<>();
    private static org.apache.log4j.Logger log = LogManager.getLogger(KinesisProcessor.class.getName());
    private static final HashSet<String> EXISTING_ORGS = new HashSet<>();


    private static Properties getLoggingProps(){
        Properties properties = new Properties();
        properties.put("log4j.rootLogger", "ERROR,stdout");
        properties.put("log4j.appender.stdout", "org.apache.log4j.ConsoleAppender" );
        properties.put("log4j.appender.stdout.layout", "org.apache.log4j.PatternLayout");
        return properties;
    }
    private static void updateStream() {
        try {
            AmazonKinesis kinesis = AwsUtil.getKinesisClient();
            ListStreamsResult streamList = kinesis.listStreams();
            List<String> streamNames = streamList.getStreamNames();
//            List<String> streamNames = Collections.singletonList("cofelybesix");
            if (streamNames != null && STREAMS.isEmpty()) {
                STREAMS.addAll(streamNames);
            } else {
                if (streamNames != null) {
                    for (String stream : streamNames) {
                        if( ! STREAMS.contains(stream)) {
                            STREAMS.add(stream);
                        }
                    }
                }
            }
        } catch (Exception e){
            log.info("Exception occurred ", e);
        }
    }
    static {
        EXISTING_ORGS.add("spinfo");
    }
    private static void startProcessor() {

//        PropertyConfigurator.configure(getLoggingProps());

       
    	
        try {
        	 List<Organization> orgs = IAMOrgUtil.getOrgs();
        	 if(CollectionUtils.isNotEmpty(orgs)) {
	        	 for (Organization org : orgs) {
	                Long orgId = org.getOrgId();
	                String orgDomainName = org.getDomain();
	                if( ! EXISTING_ORGS.contains(orgDomainName)) {
	                    try {
	                        startProcessor(orgId, orgDomainName);
	                    } catch (Exception e) {
	                        try {
	                            CommonCommandUtil.emailException("KinesisProcessor", "Exception while starting stream " + orgDomainName, new Exception("Exception while starting stream will retry after 10 sec"));
	                            Thread.sleep(10000L);
	                            startProcessor(orgId, orgDomainName);
	                        } catch (InterruptedException | LimitExceededException interrupted) {
	                            log.info("Exception occurred ", interrupted);
	                            CommonCommandUtil.emailException("KinesisProcessor", "Exception while starting stream " + orgDomainName, interrupted);
	                        }
	                    }
	                }
	            }
        	 }
        } catch (Exception e){
            log.info("Exception occurred ", e);
        }
    }

    private static void startProcessor(long orgId, String orgDomainName) {
        try {
            if(orgDomainName != null && STREAMS.contains(orgDomainName)) {
                System.out.println("Starting kinesis processor for org : " + orgDomainName + " id " + orgId);
                initiateProcessFactory(orgId, orgDomainName,"processor");
                /*initiateProcessFactory(orgId, orgDomainName, "event");
                initiateProcessFactory(orgId, orgDomainName, "timeSeries");
                initiateProcessFactory(orgId, orgDomainName, "agent");*/


                EXISTING_ORGS.add(orgDomainName);
            }
        } catch (ResourceNotFoundException e){
            log.info("Kinesis stream not found for org : " + orgDomainName +" id "+ orgId);
        } catch (Exception e){
            log.info("Exception occurred ", e);
        }
    }
    
    
    private static void initiateProcessFactory(long orgId, String orgDomainName, String type) {
    	try {
    		new Thread(() -> StreamProcessor.run(orgId, orgDomainName, type, getProcessorFactory(orgId,orgDomainName,type))).start();
    	}
    	catch (Exception e){
    		log.info("Exception occurred ", e);
    	}

    }

    private static IRecordProcessorFactory getProcessorFactory(long orgId, String orgDomainName, String type) {

       /* switch(type){
            case "event" :{
                return new EventProcessorFactory(orgId, orgDomainName);
            }
            case "timeSeries" :{
                return new TimeSeriesProcessorFactory(orgId, orgDomainName);
            }
            case "agent":{
                return new AgentProcessorFactory(orgId,orgDomainName);
            }
        }
        return null;*/
        return new ProcessorFactory(orgId,orgDomainName);
    }

    public static void startKinesis() {
        updateStream();
        startProcessor();
    }

}
