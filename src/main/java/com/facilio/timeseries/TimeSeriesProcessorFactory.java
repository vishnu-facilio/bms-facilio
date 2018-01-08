package com.facilio.timeseries;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessorFactory;

public class TimeSeriesProcessorFactory implements IRecordProcessorFactory {


	private long orgId;
	private String orgName;

	public TimeSeriesProcessorFactory(long orgId, String orgName){
		this.orgId = orgId;
		this.orgName = orgName;
	}

	@Override
	public IRecordProcessor createProcessor() {
		return new TimeSeriesProcessor(orgId, orgName);
	}

}
