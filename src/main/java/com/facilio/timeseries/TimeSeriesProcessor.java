package com.facilio.timeseries;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.*;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;
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
	private final List<FacilioField> fields = new ArrayList<>();
	private final Condition orgIdCondition = new Condition();
	private final FacilioField deviceIdField = new FacilioField();
	private final HashMap<String, Long> deviceMap = new HashMap<>();
	private FacilioModule deviceDetailsModule;
	private final FacilioField orgIdField = FieldFactory.getOrgIdField();

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

		deviceDetailsModule = ModuleFactory.getDeviceDetailsModule();
		orgIdField.setModule(deviceDetailsModule);

		orgIdCondition.setField(orgIdField);
		orgIdCondition.setOperator(NumberOperators.EQUALS);
		orgIdCondition.setValue(String.valueOf(orgId));

		deviceIdField.setName("deviceId");
		deviceIdField.setDataType(FieldType.STRING);
		deviceIdField.setColumnName("DEVICE_ID");
		deviceIdField.setModule(deviceDetailsModule);

		fields.addAll(FieldFactory.getDeviceDetailsFields());

		deviceMap.putAll(getDeviceMap());
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
					bean.processTimeSeries(timeStamp, payLoad, record, processRecordsInput.getCheckpointer());
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
            } finally {
				updateDeviceTable(record.getPartitionKey());
			}
		}
		LOGGER.info("TOTAL TIMESERIES DATA PROCESSED TIME::: ORGID::::::: "+orgId + "COMPLETED::TIME TAKEN : "+(System.currentTimeMillis() - processStartTime));
	}

	private void updateDeviceTable(String deviceId) {
		try {
			if( ! deviceMap.containsKey(deviceId)) {
				addDeviceId(deviceId);
			}
			if(deviceMap.containsKey(deviceId)) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("lastUpdatedTime", System.currentTimeMillis());
				GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder().table(deviceDetailsModule.getTableName())
						.fields(fields).andCondition(getDeviceIdCondition(deviceId)).andCondition(orgIdCondition);
				builder.update(map);
			}
		} catch (Exception e) {
			LOGGER.info("Exception while updating time for device id " + deviceId, e);
		}
	}

	private Condition getDeviceIdCondition(String deviceId) {
		Condition condition = new Condition();
		condition.setField(deviceIdField);
		condition.setOperator(NumberOperators.EQUALS);
		condition.setValue(deviceId);
		return condition;
	}

	private HashMap<String, Long> getDeviceMap() {
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder().table(deviceDetailsModule.getTableName()).andCondition(orgIdCondition).select(fields);
		HashMap<String, Long> deviceData = new HashMap<>();
		try {
			List<Map<String, Object>> data = builder.get();
			for(Map<String, Object> obj : data) {
				String deviceId = (String)obj.get("deviceId");
				Long id = (Long)obj.get("id");
				deviceData.put(deviceId, id);
			}
		} catch (Exception e) {
			LOGGER.info("Exception while getting device data", e);
		}

		return deviceData;
	}

	private void addDeviceId(String deviceId) throws Exception {
		GenericInsertRecordBuilder builder = new GenericInsertRecordBuilder().table(deviceDetailsModule.getTableName()).fields(fields);
		HashMap<String, Object> device = new HashMap<>();
		device.put("orgId", orgId);
		device.put("deviceId", deviceId);
		device.put("inUse", true);
		device.put("lastUpdatedTime", System.currentTimeMillis());
		long id = builder.insert(device);
		deviceMap.put(deviceId, id);
	}

	@Override
	public void shutdown(ShutdownInput shutdownInput) {
		 System.out.println("Shutting down record processor for stream: "+ orgDomainName +" and shard: " + shardId);

	}

}
