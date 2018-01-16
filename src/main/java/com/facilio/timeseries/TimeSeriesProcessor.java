package com.facilio.timeseries;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.events.tasker.tasks.EventProcessor;

public class TimeSeriesProcessor implements IRecordProcessor {

	private long orgId;
	private String orgDomainName;
	private String shardId;
	private final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
	
	public TimeSeriesProcessor(long orgId, String orgDomainName){
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
    }
	@Override
	public void initialize(InitializationInput initializationInput) {
		
		 this.shardId = initializationInput.getShardId();
	        try {
	            AccountUtil.setCurrentAccount(orgId);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        System.out.println("Initializing record processor for stream : " + orgDomainName + " and shard : " + shardId);

	}

	@Override
	public void processRecords(ProcessRecordsInput processRecordsInput) {

		for (Record record : processRecordsInput.getRecords()) {
			try {
				String data = decoder.decode(record.getData()).toString();
				JSONParser parser = new JSONParser();
				JSONObject payLoad = (JSONObject) parser.parse(data);
				String dataType = (String)payLoad.remove(EventProcessor.DATA_TYPE);
				
				if(dataType!=null & dataType.equals("timeseries")) {
					long timeStamp=	record.getApproximateArrivalTimestamp().getTime();
					TimeSeriesAPI.processPayLoad(timeStamp, payLoad);
					processRecordsInput.getCheckpointer().checkpoint(record);
				}
            }
            catch (Exception e) {
                e.printStackTrace();
            }
		}
	}
	
	@Override
	public void shutdown(ShutdownInput shutdownInput) {
		 System.out.println("Shutting down record processor for stream: "+ orgDomainName +" and shard: " + shardId);

	}

}
