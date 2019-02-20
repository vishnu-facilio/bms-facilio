package com.facilio.timeseries;

import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.v2.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.types.InitializationInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ProcessRecordsInput;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownInput;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.aws.util.AwsUtil;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.commands.util.CommonCommandUtil;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.events.tasker.tasks.EventProcessor;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericUpdateRecordBuilder;

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
	/*private final FacilioField orgIdField = FieldFactory.getOrgIdField();*/
	private Producer<String, String> producer;
	private HashMap<String, HashMap<String, Long>> deviceMessageTime = new HashMap<>();

	TimeSeriesProcessor(long orgId, String orgDomainName){
        this.orgId = orgId;
        this.orgDomainName = orgDomainName;
        this.errorStream = orgDomainName + "-error";
		try {
			producer = new KafkaProducer<>(getKafkaProducerProperties());
			log.info("Initialized kafka producer for stream " + errorStream);
		} catch (Exception e) {
			log.info("Exception while constructing kafka producer for stream "+ errorStream +" ", e);
		}
    }

	private Properties getKafkaProducerProperties() {
		Properties props = new Properties();
		props.put("bootstrap.servers", AwsUtil.getKafkaProducer());
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);
		props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
		return  props;
	}

	private void sendToKafka(Record record, String data) {
		JSONObject dataMap = new JSONObject();
		try {
			dataMap.put("timestamp", ""+record.getApproximateArrivalTimestamp().getTime());
			dataMap.put("key", record.getPartitionKey());
			dataMap.put("data", data);
			dataMap.put("sequenceNumber", record.getSequenceNumber());
			producer.send(new ProducerRecord<>(errorStream, record.getPartitionKey(), dataMap.toString()));
		} catch (Exception e) {
			log.info(errorStream + " : " + dataMap);
			log.info("Exception while producing to kafka ", e);
		}
	}

	@Override
	public void initialize(InitializationInput initializationInput) {
		Thread thread = Thread.currentThread();
		String threadName = orgDomainName +"-timeseries";
		thread.setName(threadName);
		this.shardId = initializationInput.getShardId();

		deviceDetailsModule = ModuleFactory.getDeviceDetailsModule();
		//orgIdField.setModule(deviceDetailsModule);

		//orgIdCondition.setField(orgIdField);
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

				String deviceId = orgDomainName;
				if (payLoad.containsKey("deviceId")) {
					deviceId = payLoad.get("deviceId").toString();
				}
				long lastMessageReceivedTime = System.currentTimeMillis();
				if (payLoad.containsKey("timestamp")) {
					Object lastTime = payLoad.get("timestamp");
					lastMessageReceivedTime = lastTime instanceof Long ? (Long) lastTime : Long.parseLong(lastTime.toString());
				}

				if(dataType!=null ) {
					HashMap<String, Long> dataTypeLastMessageTime = deviceMessageTime.getOrDefault(deviceId, new HashMap<>());
					long deviceLastMessageTime = dataTypeLastMessageTime.getOrDefault(dataType, 0L);

					if(deviceLastMessageTime != lastMessageReceivedTime) {
						switch (dataType) {
							case "timeseries":
								processTimeSeries(record, payLoad, processRecordsInput, true);
								break;
							case "devicepoints":
								processDevicePoints(payLoad);
								break;
							case "ack":
								processAck(payLoad);
								break;
							case "cov":
								processTimeSeries(record, payLoad, processRecordsInput, false);
								break;
						}
						dataTypeLastMessageTime.put(dataType, lastMessageReceivedTime);
						deviceMessageTime.put(deviceId, dataTypeLastMessageTime);
					} else {
						log.info("Duplicate message for device " + deviceId + " and type " + dataType);
					}
//					Temp fix
//					processRecordsInput.getCheckpointer().checkpoint(record);
				}
            } catch (Exception e) {
				try {
					if(AwsUtil.isProduction()) {
						log.info("Sending data to " + errorStream);
						sendToKafka(record, data);
						/*PutRecordResult recordResult = AwsUtil.getKinesisClient().putRecord(errorStream, ByteBuffer.wrap(data.getBytes(Charset.defaultCharset())), record.getPartitionKey());
						int status = recordResult.getSdkHttpMetadata().getHttpStatusCode();
						if (status != 200) {
							log.info("Couldn't add data to " + errorStream + " " + record.getSequenceNumber());
						}*/
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
	
	private void processAck(JSONObject payLoad) throws Exception {
		// TODO Auto-generated method stub
		long msgId = (long) payLoad.get("msgid");
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
		bean.acknowledgePublishedMessage(msgId);
	}

	private void processTimeSeries(Record record, JSONObject payLoad, ProcessRecordsInput processRecordsInput, boolean isTimeSeries) throws Exception {
		long timeStamp=	record.getApproximateArrivalTimestamp().getTime();
		long startTime = System.currentTimeMillis();
        LOGGER.info("TIMESERIES DATA PROCESSED TIME::: ORGID::::::: "+orgId + " TIME::::" +timeStamp);
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
		bean.processTimeSeries(timeStamp, payLoad, record, processRecordsInput.getCheckpointer(), isTimeSeries);
		LOGGER.info("TIMESERIES DATA PROCESSED TIME::: ORGID::::::: "+orgId + "COMPLETED:::::::TIME TAKEN : "+(System.currentTimeMillis() - startTime));
	}
	
	private void processDevicePoints (JSONObject payLoad) throws Exception {
		long instanceNumber = (Long)payLoad.get("instanceNumber");
		String destinationAddress = "";
		if(payLoad.containsKey("macAddress")) {
			destinationAddress = (String) payLoad.get("macAddress");
		}
		long subnetPrefix = (Long)payLoad.get("subnetPrefix");
		long networkNumber = -1;
		if(payLoad.containsKey("networkNumber")) {
			networkNumber = (Long) payLoad.get("networkNumber");
		}
		String broadcastAddress = (String) payLoad.get("broadcastAddress");
		String deviceName = (String) payLoad.get("deviceName");
		
		String deviceId = instanceNumber+"_"+destinationAddress+"_"+networkNumber;
		if( ! deviceMap.containsKey(deviceId)) {
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
            ControllerContext controller = bean.getController(deviceId);
            if(controller == null) {
                controller = new ControllerContext();
                controller.setName(deviceName);
                controller.setBroadcastIp(broadcastAddress);
                controller.setDestinationId(destinationAddress);
                controller.setInstanceNumber(instanceNumber);
                controller.setNetworkNumber(networkNumber);
                controller.setSubnetPrefix(Math.toIntExact(subnetPrefix));
                controller.setMacAddr(deviceId);
                controller = bean.addController(controller);
            }
			long controllerSettingsId = controller.getId();
			if(controllerSettingsId > -1) {
				JSONArray points = (JSONArray)payLoad.get("points");
				LOGGER.info("Device Points : "+points);
				TimeSeriesAPI.addUnmodeledInstances(points, controllerSettingsId);
			}
		}
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
		return  CriteriaAPI.getCondition("DEVICE_ID", "DEVICE_ID", deviceId, StringOperators.IS);
		/*Condition condition = new Condition();
		condition.setField(deviceIdField);
		condition.setOperator(NumberOperators.EQUALS);
		condition.setValue(deviceId);
		return condition;*/
	}

	private Map<String, Long> getDeviceMap() {
		try {
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
			return bean.getDeviceMap();
		} catch (Exception e) {
			LOGGER.info("Exception while getting device data", e);
			return new HashMap<>();
		}
	}

	private void addDeviceId(String deviceId) throws Exception {
		ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD", orgId);
		deviceMap.put(deviceId, bean.addDeviceId(deviceId));
	}

	@Override
	public void shutdown(ShutdownInput shutdownInput) {
		 System.out.println("Shutting down record processor for stream: "+ orgDomainName +" and shard: " + shardId);

	}

}
