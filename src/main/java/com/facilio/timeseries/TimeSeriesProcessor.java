package com.facilio.timeseries;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.events.tasker.tasks.EventProcessor;
import com.facilio.fw.BeanFactory;

public class TimeSeriesProcessor implements IRecordProcessor {
	
	private static final Logger LOGGER = LogManager.getLogger(TimeSeriesProcessor.class.getName());

	private static Logger log = LogManager.getLogger(TimeSeriesProcessor.class.getName());

	private long orgId;
	private String orgDomainName;
	private String shardId;
	private String errorStream;
	private final CharsetDecoder decoder = Charset.forName("UTF-8").newDecoder();
	
	public TimeSeriesProcessor(long orgId, String orgDomainName){
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        this.errorStream = orgDomainName + "-error";
    }
	@Override
	public void initialize(InitializationInput initializationInput) {
		Thread thread = Thread.currentThread();
		String threadName = orgDomainName +"-timeseries";
		thread.setName(threadName);
		this.shardId = initializationInput.getShardId();
	}

	@Override
	public void processRecords(ProcessRecordsInput processRecordsInput) {
		long processStartTime = System.currentTimeMillis();
		LOGGER.info("TOTAL TIMESERIES DATA PROCESSED TIME::: ORGID::::::: "+orgId + "RECORD SIZE::::::: "+processRecordsInput.getRecords().size());
		for (Record record : processRecordsInput.getRecords()) {
			String data = "";
			try {
				data = decoder.decode(record.getData()).toString();
				if(data.isEmpty()){
					continue;
				}
				JSONParser parser = new JSONParser();
				JSONObject payLoad = (JSONObject) parser.parse(data);
				String dataType = (String)payLoad.remove(EventProcessor.DATA_TYPE);

				if(dataType!=null && "timeseries".equals(dataType)) {
					long timeStamp=	record.getApproximateArrivalTimestamp().getTime();
					long startTime = System.currentTimeMillis();
		            LOGGER.info("TIMESERIES DATA PROCESSED TIME::: ORGID::::::: "+orgId + " TIME::::" +timeStamp);
					ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
					bean.processTimeSeries(timeStamp, payLoad, record,processRecordsInput.getCheckpointer());
					LOGGER.info("TIMESERIES DATA PROCESSED TIME::: ORGID::::::: "+orgId + "COMPLETED:::::::TIME TAKEN : "+(System.currentTimeMillis() - startTime));
//					Temp fix
//					processRecordsInput.getCheckpointer().checkpoint(record);
				}
            } catch (Exception e) {
				try {
					if(AwsUtil.isProduction()) {
						log.info("Sending data to " + errorStream);
						PutRecordResult recordResult = AwsUtil.getKinesisClient().putRecord(errorStream, record.getData(), record.getPartitionKey());
						int status = recordResult.getSdkHttpMetadata().getHttpStatusCode();
						if (status != 200) {
							log.info("Couldn't add data to " + errorStream + " " + record.getSequenceNumber());
						}
					}
				} catch (Exception e1) {
					log.info("Exception while sending data to " + errorStream, e1);
				}
				CommonCommandUtil.emailException("TimeSeriesProcessor", "Error in processing records : "
            		+record.getSequenceNumber()+ " in TimeSeries ", e, data);
                 log.info("Exception occurred ", e);
            }
		}
		LOGGER.info("TOTAL TIMESERIES DATA PROCESSED TIME::: ORGID::::::: "+orgId + "COMPLETED::TIME TAKEN : "+(System.currentTimeMillis() - processStartTime));
	}
	
	@Override
	public void shutdown(ShutdownInput shutdownInput) {
		 System.out.println("Shutting down record processor for stream: "+ orgDomainName +" and shard: " + shardId);

	}

}
