package com.facilio.timeseries;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.facilio.accounts.util.AccountUtil;

public class TimeSeriesProcessor implements IRecordProcessor {

	private long orgId;
	private String orgName;
	private String shardId;
	
	public TimeSeriesProcessor(long orgId, String orgName){
        this.orgId = orgId;
        this.orgName = orgName;
    }
	@Override
	public void initialize(InitializationInput initializationInput) {
		
		 this.shardId = initializationInput.getShardId();
	        try {
	            AccountUtil.setCurrentAccount(orgId);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        System.out.println("Initializing record processor for stream : " + orgName + " and shard : " + shardId);

	}

	@Override
	public void processRecords(ProcessRecordsInput processRecordsInput) {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown(ShutdownInput shutdownInput) {
		 System.out.println("Shutting down record processor for stream: "+ orgName +" and shard: " + shardId);

	}

}
