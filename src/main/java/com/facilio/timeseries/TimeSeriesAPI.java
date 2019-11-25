package com.facilio.timeseries;

import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.facilio.accounts.util.AccountConstants;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.point.PointEnum;
import com.facilio.aws.util.AwsUtil;
import com.facilio.aws.util.FacilioProperties;
import com.facilio.bacnet.BACNetUtil.InstanceType;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingType;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.bmsconsole.util.IoTMessageAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.Condition;
import com.facilio.db.criteria.Criteria;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.*;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.services.procon.message.FacilioRecord;
import com.facilio.tasker.FacilioTimer;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TimeSeriesAPI {

	private static final Logger LOGGER = LogManager.getLogger(TimeSeriesAPI.class.getName());
	
	public static void processPayLoad(long ttime, JSONObject payLoad, String macAddr) throws Exception {
		LOGGER.debug(payLoad);
		if(AccountUtil.getCurrentOrg() != null && FacilioProperties.isProduction()) {
			String stream = AccountUtil.getCurrentOrg().getDomain();
			if (macAddr == null) {
				macAddr = stream;
			}
			PutRecordResult recordResult = AwsUtil.getKinesisClient().putRecord(stream, ByteBuffer.wrap(payLoad.toJSONString().getBytes(Charset.defaultCharset())), macAddr);
			int status = recordResult.getSdkHttpMetadata().getHttpStatusCode();
			if (status != 200) {
				LOGGER.info("Couldn't add data to " + stream);
			}
		}
		// processPayLoad(ttime, payLoad, null, null, macAddr, true);
	}
	
	public static void processPayLoad(long ttime, JSONObject payLoad, String macAddr, boolean adjustTime) throws Exception {
		processPayLoad(ttime, payLoad, null, macAddr, adjustTime);
	}
	
	public static void processPayLoad(long ttime, JSONObject payLoad, FacilioRecord record, String macAddr, boolean adjustTime) throws Exception {

		long timeStamp = getTimeStamp(ttime, payLoad);
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TIMESTAMP , timeStamp);
		context.put(FacilioConstants.ContextNames.PAY_LOAD , payLoad);
		//Temp code. To be removed later *START*
		if (record != null) {
			context.put(FacilioConstants.ContextNames.FACILIO_RECORD, record);
/*
			context.put(FacilioConstants.ContextNames.KINESIS_CHECK_POINTER, checkpointer);
*/
			//even if the above two lines are removed.. please do not remove the below partitionKey..
			macAddr = record.getPartitionKey();
		}
		if (macAddr != null){
			ControllerContext controller=  ControllerAPI.getController(macAddr);
			if(controller!=null) {
				context.put(FacilioConstants.ContextNames.CONTROLLER_ID, controller.getId());
			}
		}
		//Temp code. To be removed later *END*
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.KINESIS);
		context.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, adjustTime);
		FacilioChain processDataChain = TransactionChainFactory.getProcessDataChain();
		processDataChain.execute(context);
	}
	
	public static void processFacilioRecord(FacilioRecord record) throws Exception {
		if (record == null) {
			return;
		}
		long timeStamp = getTimeStamp(record.getTimeStamp(), record.getData());
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TIMESTAMP , timeStamp);
		context.put(FacilioConstants.ContextNames.PAY_LOAD , record.getData());
		context.put(FacilioConstants.ContextNames.FACILIO_RECORD, record);
/*
		context.put(FacilioConstants.ContextNames.FACILIO_CONSUMER, consumer);
*/
		//even if the above two lines are removed.. please do not remove the below partitionKey..
		ControllerContext controller=  ControllerAPI.getController(record.getPartitionKey());
		if(controller!=null) {
			context.put(FacilioConstants.ContextNames.CONTROLLER_ID, controller.getId());
		}
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.KINESIS);
		FacilioChain processDataChain = TransactionChainFactory.getProcessDataChain();
		processDataChain.execute(context);
	}

/*
	public static void processPayLoadOld(long ttime, JSONObject payLoad, Record record, IRecordProcessorCheckpointer checkpointer, String macAddr, boolean adjustTime) throws Exception {

		long timeStamp = getTimeStamp(ttime, payLoad);
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TIMESTAMP , timeStamp);
		context.put(FacilioConstants.ContextNames.PAY_LOAD , payLoad);
		//Temp code. To be removed later *START*
		if (record != null) {
			context.put(FacilioConstants.ContextNames.KINESIS_RECORD, record);
			context.put(FacilioConstants.ContextNames.KINESIS_CHECK_POINTER, checkpointer);
			//even if the above two lines are removed.. please do not remove the below partitionKey..
			macAddr = record.getPartitionKey();
		}
		if (macAddr != null){
			ControllerContext controller=  ControllerAPI.getController(macAddr);
			if(controller!=null) {
				context.put(FacilioConstants.ContextNames.CONTROLLER_ID, controller.getId());
			}
		}
		//Temp code. To be removed later *END*
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.KINESIS);
		context.put(FacilioConstants.ContextNames.ADJUST_READING_TTIME, adjustTime);
		FacilioChain processDataChain = TransactionChainFactory.getProcessDataChain();
		processDataChain.execute(context);
	}
*/

	/*public static void processFacilioRecordOld(FacilioConsumer consumer, FacilioRecord record) throws Exception {
		if (record == null) {
			return;
		}
		long timeStamp = getTimeStamp(record.getTimeStamp(), record.getData());
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TIMESTAMP , timeStamp);
		context.put(FacilioConstants.ContextNames.PAY_LOAD , record.getData());
		context.put(FacilioConstants.ContextNames.FACILIO_RECORD, record);
		context.put(FacilioConstants.ContextNames.FACILIO_CONSUMER, consumer);
		//even if the above two lines are removed.. please do not remove the below partitionKey..
		ControllerContext controller=  ControllerAPI.getController(record.getPartitionKey());
		if(controller!=null) {
			context.put(FacilioConstants.ContextNames.CONTROLLER_ID, controller.getId());
		}
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.KINESIS);
		FacilioChain processDataChain = TransactionChainFactory.getProcessDataChain();
		processDataChain.execute(context);
	}*/



	private static long getTimeStamp(long ttime, JSONObject payLoad) {

		long timeStamp = -1;
		Object timeString = payLoad.remove("timestamp");
		if(timeString != null) {
			try {
				timeStamp = Long.parseLong(timeString.toString());
			}
			catch(NumberFormatException nfe) {}
		}

		if(timeStamp!= -1)
		{
			return timeStamp ;
		}
		else if (ttime!=-1){
			return ttime;
		}
		//both are not set.. assuming current time
		return System.currentTimeMillis();
	}
	
	public static Map<String, List<String>> getAllDevices() throws Exception {
		
		FacilioModule module = ModuleFactory.getPointsModule();
		List<FacilioField> fields = FieldFactory.getPointsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), CommonOperators.IS_EMPTY))
				.andCustomWhere("ORGID=?",AccountUtil.getCurrentOrg().getOrgId());

		List<Map<String, Object>> props = builder.get();
		
		Map<String, List<String>> deviceInstanceMap = new HashMap<String, List<String>>();
		for(Map<String, Object> prop : props) {
			String deviceName = (String) prop.get("device");
			if(!deviceInstanceMap.containsKey(deviceName)) {
				deviceInstanceMap.put(deviceName,new ArrayList<>());
			}
			List<String> instances = deviceInstanceMap.get(deviceName);
			instances.add((String)prop.get("instance"));
		} 
		return deviceInstanceMap;
	}
	
	private static Map<String, ReadingDataMeta> getMetaMap(long assetId, Map<String,Long> instanceFieldMap) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		for (Map.Entry<String, Long> entry : instanceFieldMap.entrySet()) {
			long fieldId = entry.getValue();
			FacilioField field = modBean.getField(fieldId);
			if (field == null) {
				throw new IllegalArgumentException("Invalid fieldId during Instance to asset mapping");
			}
			fields.add(field);
		}
		List<ReadingDataMeta> metaList = ReadingsAPI.getReadingDataMetaList(assetId, fields);
		return metaList.stream().collect(Collectors.toMap(meta -> getRDMKey(meta.getResourceId(), meta.getFieldId()), Function.identity()));
	}
	
	private static void checkForInputType(long assetId, long fieldId, String instanceName, Map<String, ReadingDataMeta> metaMap) throws Exception {
		ReadingDataMeta meta = metaMap.get(getRDMKey(assetId, fieldId));
		switch (meta.getInputTypeEnum()) {
			case CONTROLLER_MAPPED:
				throw new IllegalArgumentException("Field with ID "+fieldId+" for instance "+instanceName+" is already mapped");
			case FORMULA_FIELD:
			case HIDDEN_FORMULA_FIELD:
				throw new IllegalArgumentException("Field with ID "+fieldId+" is formula field and therefore cannot be mapped");
			default:
				break;
		}
	}
	
	
	
	// Temp
	public static void migrateUnmodelledData(long controllerId, List<Map<String, Object>> instances) throws Exception {
		if (instances == null) {
			instances = TimeSeriesAPI.getMappedInstances(controllerId);
		}
		if (instances != null && !instances.isEmpty()) {
			int i = 0;	// Temp
			 for(Map<String, Object> instance: instances) {
				FacilioContext context = new FacilioContext();
				context.put(FacilioConstants.ContextNames.DEVICE_DATA, instance.get("device"));
				context.put(FacilioConstants.ContextNames.INSTANCE_INFO, instance.get("instance"));
				context.put(FacilioConstants.ContextNames.ASSET_ID, instance.get("assetId"));
				context.put(FacilioConstants.ContextNames.FIELD_ID, instance.get("fieldId"));
				context.put(FacilioConstants.ContextNames.CONTROLLER_ID, controllerId);
				if (i < 10) {
					LOGGER.info("Migrating UnmodelledData, device: " + instance.get("device") + ",instance: " + instance.get("instance") + ", controllerId: " + controllerId + ", assetId:" + instance.get("assetId"));
				}
					
				FacilioTimer.scheduleInstantJob("ProcessUnmodelledHistoricalData", context);
				i++;
				
			 }
		}
	}
	
	
public static void insertInstanceAssetMapping(String deviceName, long assetId, long categoryId, Long controllerId, String instanceName, Long fieldId, Integer unit) throws Exception {
		
		List<Map<String, Object>> instanceDetails = getUnmodeledInstances(deviceName, instanceName, controllerId, null);
		//List<Map<String, Object>> instancePointsDetails = getPoinstUnmodeledInstances(deviceName, instanceFieldMap.keySet(), controllerId, null);
		Map<String,Map<String, Object>> instanceMap = instanceDetails.stream().collect(Collectors.toMap(instance -> (String) instance.get("instance"), Function.identity()));
		//Map<String,Map<String, Object>> instancepointsMap = instancePointsDetails.stream().collect(Collectors.toMap(instance -> (String) instance.get("instance"), Function.identity()));
		
		List<ReadingDataMeta> writableReadingList = new ArrayList<>();
		List<ReadingDataMeta> remainingReadingList = new ArrayList<>();
		
		Map<String, ReadingDataMeta> metaMap = getMetaMap(assetId, Collections.singletonMap(instanceName, fieldId));
		List<Map<String, Object>> records = new ArrayList<>();
		
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		
		
		checkForInputType(assetId, fieldId, instanceName, metaMap);

//		if(isStage()) {
			Map<String, Object> pointsRecord = (Map<String, Object>) getNewPointsData(assetId,categoryId,fieldId);
//			pointsRecord.put("unit", unit);
			updatePointsData(deviceName, instanceName, pointsRecord);

//		}

		Map<String, Object> record = new HashMap<String,Object>();
		record.put("orgId", orgId);
		record.put("device", deviceName);
		record.put("assetId", assetId);
		record.put("categoryId", categoryId);
		record.put("instance", instanceName);
		record.put("fieldId", fieldId);
		record.put("mappedTime", System.currentTimeMillis());
		if(controllerId!=null) {
			record.put("controllerId", controllerId);
		}
		records.add(record);
		
		Map<String, Object> instance = instanceMap.get(instanceName);
		InstanceType rType=null;
		if(instance!=null) {
			Object typeObj= instance.get("instanceType");
			if(typeObj!=null) {
				rType =  InstanceType.valueOf((int)typeObj );
			}
		}
		
		ReadingDataMeta meta = metaMap.get(getRDMKey(assetId, fieldId));
		if (rType==null || rType.isWritable()) {
			writableReadingList.add(meta);
		}
		else {
			remainingReadingList.add(meta);
		}
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.fields(FieldFactory.getInstanceMappingFields())
				.table("Instance_To_Asset_Mapping")
				.addRecords(records);
		insertBuilder.save();
		if (!writableReadingList.isEmpty()) {
			ReadingsAPI.updateReadingDataMetaInputType(writableReadingList, ReadingInputType.CONTROLLER_MAPPED, ReadingType.WRITE);
		}
		if (!remainingReadingList.isEmpty()){
			ReadingsAPI.updateReadingDataMetaInputType(remainingReadingList, ReadingInputType.CONTROLLER_MAPPED, null);
		}
	}

	private static void updatePointsData(String deviceName, String instanceName, Map<String, Object> pointsRecord)
			throws SQLException {

		FacilioModule module = ModuleFactory.getPointsModule();
		GenericUpdateRecordBuilder builderPoints = new GenericUpdateRecordBuilder()
				.fields(FieldFactory.getPointsFields())
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getDeviceField(module),deviceName.replace(",", StringOperators.DELIMITED_COMMA), StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getInstanceField(module),instanceName.replace(",", StringOperators.DELIMITED_COMMA), StringOperators.IS))
				;
		builderPoints.update(pointsRecord);
	}
	
	public static int updateInstanceAssetMapping(String deviceName, long assetId, long categoryId, String instanceName, long fieldId, Map<String, Object> oldData,Integer unit) throws Exception {
		long oldFieldId = (long) oldData.get("fieldId");
		long oldAssetId = (long) oldData.get("resourceId");
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		List<Pair<Long, FacilioField>> pairs = new ArrayList<>();
		pairs.add(Pair.of(oldAssetId, modBean.getField(oldFieldId)));
		pairs.add(Pair.of(assetId, modBean.getField(fieldId)));
		
		Map<String, ReadingDataMeta> metaMap = ReadingsAPI.getReadingDataMetaMap(pairs);
		checkForInputType(assetId, fieldId, instanceName, metaMap);
		
		ReadingDataMeta meta = metaMap.get(getRDMKey(oldAssetId, oldFieldId));
		long lastMappedTime = (long) oldData.get("mappedTime");
		// Checking if the data is coming for more than a month
		if (meta.getActualValue() != null && !meta.getActualValue().equals("-1") && ((meta.getTtime()-lastMappedTime) > (30 * 24 * 60 * 60000L) ) ) {
			throw new IllegalArgumentException("Field cannot be changed. Please contact the support");
		}
		Map<String, Object> prop=(Map<String, Object>) getNewPointsData(assetId,categoryId,fieldId);
		Map<String, Object> props=(Map<String, Object>) getUpdateMappedData(assetId,categoryId,fieldId);
		
		FacilioModule module = ModuleFactory.getInstanceMappingModule();
		List<FacilioField> fields = FieldFactory.getInstanceMappingFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		
		
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.fields(fields)
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("device"), deviceName.replace(",", StringOperators.DELIMITED_COMMA), StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("instance"), instanceName.replace(",", StringOperators.DELIMITED_COMMA), StringOperators.IS))
				;
			updatePointsData(deviceName, instanceName,prop );

			int count = builder.update(props);
		
		return count;
	}
	
	private static String getRDMKey(long resourceId, long fieldId) {
		return resourceId+"_"+fieldId;
	}
	
	public static Map<String, Long> getDefaultInstanceFieldMap() throws Exception {
		Map<String, Long> fieldMap = new HashMap<>();
		FacilioModule module = ModuleFactory.getPointsModule();
		List<FacilioField> fields = FieldFactory.getPointsFields();
		Map<String, FacilioField> fieldMaps = FieldFactory.getAsMap(fields);

		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMaps.get("resourceId"), CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMaps.get("fieldId"), CommonOperators.IS_NOT_EMPTY))
				.andCustomWhere("ORGID=?", AccountUtil.getCurrentOrg().getOrgId());
		List<Map<String, Object>> props = builder.get();	
		if(props!=null && !props.isEmpty()) {
			for(Map<String, Object> prop : props) {
				String instanceName = (String) prop.get("instance");
				fieldMap.put(instanceName, (Long) prop.get("fieldId"));
			}
		}
		return fieldMap;
	}
	
	public static Map<String, Object> getMappedInstance(Long assetId, long fieldId) throws Exception {
		FacilioModule module = ModuleFactory.getPointsModule();
		List<FacilioField> fields = FieldFactory.getPointsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), String.valueOf(assetId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), String.valueOf(fieldId), NumberOperators.EQUALS))
				;
		List<Map<String, Object>> props = builder.get();
		if(props!=null && !props.isEmpty()) {
			return props.get(0);
		}
		return null;
	}
	
	public static Map<String, Object> getMappedInstance(String device, String instance, long controllerId) throws Exception {
		FacilioModule module = ModuleFactory.getPointsModule();
		List<FacilioField> fields = FieldFactory.getPointsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("device"), device, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("instance"), instance.replace(",", StringOperators.DELIMITED_COMMA), StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("controllerId"), String.valueOf(controllerId), StringOperators.IS))
				;
		List<Map<String, Object>> props = builder.get();
		if(props!=null && !props.isEmpty()) {
			return props.get(0);
		}
		return null;
	}
	
	public static List<Map<String, Object>> getMappedInstances(Collection<Pair<Long, Long>> assetFieldPairs) throws Exception {
		FacilioModule module = ModuleFactory.getPointsModule();
		List<FacilioField> fields = FieldFactory.getPointsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				;
		Criteria criteriaList = new Criteria();
		for(Pair<Long, Long> pair: assetFieldPairs) {
			Criteria criteria = new Criteria();
			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), String.valueOf(pair.getLeft()), NumberOperators.EQUALS));
			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), String.valueOf(pair.getRight()), NumberOperators.EQUALS));
			criteriaList.orCriteria(criteria);
		}
		builder.andCriteria(criteriaList);
		return builder.get();
	}
	
	public static List<Map<String, Object>> getMappedInstances(long controllerId) throws Exception {
		FacilioModule module = ModuleFactory.getPointsModule();
		List<FacilioField> fields = FieldFactory.getPointsFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("controllerId"), String.valueOf(controllerId), NumberOperators.EQUALS))
				;
		return builder.get();
	}
	
	public static List<Map<String,Object>> getMarkedReadings(Criteria criteria) throws Exception {
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getMarkedReadingFields())
				.table(ModuleFactory.getMarkedReadingModule().getTableName())
				.andCustomWhere("ORGID=?", AccountUtil.getCurrentOrg().getOrgId());
		if(!criteria.isEmpty()) {
			builder.andCriteria(criteria);
		}
		return builder.get();
				
	}
	
	public static List<Map<String,Object>> getMarkedReadings(Criteria criteria, FacilioField label, FacilioField value) throws Exception {
		
		List<FacilioField> fields = new ArrayList<FacilioField>();
		fields.add(label);
		fields.add(value);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(ModuleFactory.getMarkedReadingModule().getTableName())
				.andCustomWhere("ORGID=?", AccountUtil.getCurrentOrg().getOrgId());
		if(!criteria.isEmpty()) {
			builder.andCriteria(criteria);
		}
		builder.groupBy(label.getName());
		
		return builder.get();
	}
	
	public static Criteria getCriteria(List<Long> timeRange, List<Long> deviceList, List<Long> moduleList,List<Long> fieldList,List<Integer> markTypeList) {
		
		Criteria criteria = new Criteria(); 
		if(timeRange!=null && !timeRange.isEmpty()) {
			if (timeRange.size() == 1) {
				Integer operatorId = Integer.parseInt(timeRange.get(0).toString());
				//array[0]: operator
				Condition condition = new Condition();
				condition.setFieldName("TTIME");
				condition.setColumnName("TTIME");
				condition.setOperatorId(operatorId);
				
				criteria.addAndCondition(condition);
			}
			else {
				//array[0]: startTime 
				//array[1]: endTime
				criteria.addAndCondition(CriteriaAPI.getCondition("TTIME","TTIME", 
						StringUtils.join(timeRange, ","), DateOperators.BETWEEN));
			}
		}
		if(deviceList!=null && !deviceList.isEmpty()) {
			criteria.addAndCondition(CriteriaAPI.getCondition("RESOURCE_ID","RESOURCE_ID", 
					StringUtils.join(deviceList,","), NumberOperators.EQUALS));
		}
		if(moduleList!=null && !moduleList.isEmpty()) {
		criteria.addAndCondition(CriteriaAPI.getCondition("MODULEID","MODULEID", 
				StringUtils.join(moduleList,","), NumberOperators.EQUALS));
		}
		if(fieldList!=null && !fieldList.isEmpty()) {
		criteria.addAndCondition(CriteriaAPI.getCondition("FIELD_ID","FIELD_ID", 
				StringUtils.join(fieldList,","), NumberOperators.EQUALS));
		}
		if(markTypeList!=null && !markTypeList.isEmpty()) {
		criteria.addAndCondition(CriteriaAPI.getCondition("MARK_TYPE","MARK_TYPE", 
				StringUtils.join(markTypeList,","), NumberOperators.EQUALS));
		}
		return criteria;
	}
	
	public static int addUnmodeledInstances(JSONArray instanceArray, Long controllerId) throws Exception {
		
		/*jsonObject should consists
		object.put("device",deviceName);
		object.put("instance", instanceName);
		object.put("objectInstanceNumber", objInstNumbr);
		object.put("instanceDescription",description);
		object.put("instanceType", instanceType);
		 */
		
		List<String> instanceNames = null;
		if (controllerId != null) {
			List<Map<String, Object>> instances = getUnmodeledInstancesForController(controllerId, true);
			if (instances != null && !instances.isEmpty()) {
				instanceNames = instances.stream().map(instance -> instance.get("device") + "|" + instance.get("instance")).collect(Collectors.toList());
			}
		}
		
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
	
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.fields(FieldFactory.getUnmodeledInstanceFields())
				.table("Unmodeled_Instance");
		for(Object instance:instanceArray) {
			JSONObject instanceObj = (JSONObject) instance;
			
			if(instanceObj==null || instanceObj.isEmpty()) {
				continue;
			}
			if (instanceNames != null) {
				// Checking if the instance already exists. No need to add again on re-discovering of points
				String name = instanceObj.get("device") + "|" + instanceObj.get("instance");
				if (instanceNames.contains(name)) {
					continue;
				}
			}
			instanceObj.put("orgId", orgId);
			instanceObj.put("createdTime", System.currentTimeMillis());
			if(controllerId!=null) {
				//this will ensure the new inserts after addition of controller gets proper controller id
				instanceObj.put("controllerId", controllerId);
			}
			insertBuilder.addRecord(instanceObj);
		}
		try {
			insertBuilder.save();
			return insertBuilder.getRecords().size();
		}
		catch (Exception e) {
			LOGGER.error("Insertion failed while adding instances: " + instanceNames, e);
			throw e;
		}
	}
	public static  int addPointsInstances(JSONArray instanceArray, Long controllerId) throws Exception {

		/*jsonObject should consists
		object.put("device",deviceName);
		object.put("instance", instanceName);
		object.put("objectInstanceNumber", objInstNumbr);
		object.put("instanceDescription",description);
		object.put("instanceType", instanceType);
		 */
		List<String> instanceNames = null;
		if (controllerId != null) {
			if(AccountUtil.getCurrentOrg().getId()==152) {
				LOGGER.info("## Device Points in come payload " + instanceArray);
			}
			List<Map<String, Object>> instances = getPointsInstancesForController(controllerId, true);
			if(AccountUtil.getCurrentOrg().getId()==152) {
				LOGGER.info("## Device Points Data already added in Points Table" + instances);
			}
			if (instances != null && !instances.isEmpty()) {
				instanceNames = instances.stream().map(instance -> instance.get("device") + "|" + instance.get("instance")).collect(Collectors.toList());
			}
		}

		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		FacilioModule module = ModuleFactory.getPointsModule();
		GenericInsertRecordBuilder insertBuilderPoints = new GenericInsertRecordBuilder()
				.fields(FieldFactory.getPointsFields())
				.table(module.getTableName());
		for(Object instance:instanceArray) {
			JSONObject instanceObj = (JSONObject) instance;
			if(instanceObj==null || instanceObj.isEmpty()) {
				continue;
			}
			if (instanceNames != null) {
				// Checking if the instance already exists. No need to add again on re-discovering of points
				String name = instanceObj.get("device") + "|" + instanceObj.get("instance");
				if (instanceNames.contains(name)) {
					continue;
				}
			}
			instanceObj.put("orgId", orgId);
			instanceObj.put("createdTime", System.currentTimeMillis());
			if(controllerId!=null) {
				//this will ensure the new inserts after addition of controller gets proper controller id
				instanceObj.put("controllerId", controllerId);
			}
			insertBuilderPoints.addRecord(instanceObj);
			if(AccountUtil.getCurrentOrg().getId()==152) {
				LOGGER.info("## Device Points Data comes form ORG 152 check" + instanceObj);
			}
		}
		insertBuilderPoints.save();
		return insertBuilderPoints.getRecords().size();
	}
	public static List<Map<String, Object>> getUnmodeledInstancesForController (long controllerId, Boolean...fetchAllTypes) throws Exception {
		return getUnmodeledInstancesForController(controllerId, null, null, null, null, false, null, fetchAllTypes.length > 0 && fetchAllTypes[0]);
	}
	public static List<Map<String, Object>> getPointsInstancesForController (long controllerId, Boolean...fetchAllTypes) throws Exception {
		return getPointsInstancesForController(controllerId, null, null, null, null, false, null, fetchAllTypes.length > 0 && fetchAllTypes[0]);
	}
	public static List<Map<String, Object>> getUnmodeledInstancesForController (long controllerId, Boolean configuredOnly, Boolean fetchMapped, JSONObject pagination, Boolean isSubscribed, boolean fetchCount, String searchText, boolean fetchAllTypes) throws Exception {
		FacilioModule module = ModuleFactory.getUnmodeledInstancesModule();
		List<FacilioField> fields = FieldFactory.getUnmodeledInstanceFields();
		fields.add(FieldFactory.getIdField(module));
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("controllerId"), String.valueOf(controllerId), NumberOperators.EQUALS));
		
		if (!fetchCount) {
			builder.select(fields).orderBy(module.getTableName()+ "." + fieldMap.get("instance").getColumnName() + " ASC");
		}
		else {
			builder.select(FieldFactory.getCountField(module, fieldMap.get("controllerId")));
		}
		
		Criteria criteria = new Criteria();
		if (!fetchAllTypes) {
			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("instanceType"), CommonOperators.IS_EMPTY));
			criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("instanceType"), String.valueOf(6), NumberOperators.LESS_THAN));
			
		}
		if (searchText != null) {
    			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("instance"), searchText, StringOperators.CONTAINS));
   		}
		
		if (!criteria.isEmpty()) {
			builder.andCriteria(criteria);
		}
		
		if (configuredOnly != null) {
			Criteria inUseCriteria = new Criteria();
			inUseCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("inUse"), String.valueOf(configuredOnly), BooleanOperators.IS));
			if (configuredOnly) {
				inUseCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("objectInstanceNumber"), CommonOperators.IS_EMPTY));				
			}
			builder.andCriteria(inUseCriteria);
		}
		if (isSubscribed != null) {
			Criteria isSubscribedCriteria = new Criteria();
			isSubscribedCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("subscribed"), String.valueOf(isSubscribed), BooleanOperators.IS));
			builder.andCriteria(isSubscribedCriteria);
		}
				
		if (fetchMapped != null) {
			FacilioModule mappedModule = ModuleFactory.getInstanceMappingModule();
			List<FacilioField> mappedFields = FieldFactory.getInstanceMappingFields();
			Map<String, FacilioField> mappedFieldMap = FieldFactory.getAsMap(mappedFields);
			
			fields.addAll(mappedFields);
			
			FacilioField device = fieldMap.get("device");
			FacilioField instance = fieldMap.get("instance");
			FacilioField controller = fieldMap.get("controllerId");
			FacilioField mappedDevice = mappedFieldMap.get("device");
			FacilioField mappedInstance = mappedFieldMap.get("instance");
			FacilioField mappedController = mappedFieldMap.get("controllerId");
			String orgIdColumnName = AccountConstants.getOrgIdField().getColumnName();
			
			String joinOn = module.getTableName()+"."+orgIdColumnName+ "=" + mappedModule.getTableName()+"."+orgIdColumnName + " AND " +
			module.getTableName()+"."+controller.getColumnName()+ "=" + mappedModule.getTableName()+"."+mappedController.getColumnName()+ " AND " +
			module.getTableName() + "." + device.getColumnName()+"="+mappedModule.getTableName()+"."+mappedDevice.getColumnName()
			+ " AND " + module.getTableName() +"." + instance.getColumnName() + "=" + mappedModule.getTableName()+"."+mappedInstance.getColumnName();
			
			if (!fetchMapped) {
				builder.leftJoin(mappedModule.getTableName())
				.on(joinOn)
				.andCondition(CriteriaAPI.getCondition(mappedInstance, CommonOperators.IS_EMPTY));
			}
			else {
				builder.innerJoin(mappedModule.getTableName()).on(joinOn)
				.orderBy(mappedFieldMap.get("mappedTime").getColumnName() + " DESC");
			}
		}
		
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");

			int offset = ((page-1) * perPage);
			if (offset < 0) {
				offset = 0;
			}

			builder.offset(offset);
			builder.limit(perPage);
		}

		 List<Map<String, Object>> props =  builder.get();
		 
		 LOGGER.debug("Query in getting instances : "+builder);
		 
		 if (props != null && !props.isEmpty() && !fetchCount) {
			 return props.stream().map(prop -> {
				 if (prop.get("instanceType") != null) {
					 InstanceType type = InstanceType.valueOf((int) prop.get("instanceType"));
					 if (type != null) {
						 prop.put("instanceTypeVal", type.name());
					 }
				 }
				 return prop;
			}).collect(Collectors.toList());
		 }
		 return props;

	}
	public static List<Map<String, Object>> getPointsInstancesForController (long controllerId, Boolean configuredOnly, Boolean fetchMapped, JSONObject pagination, Boolean isSubscribed, boolean fetchCount, String searchText, boolean fetchAllTypes) throws Exception {
		FacilioModule module = ModuleFactory.getPointsModule();
		List<FacilioField> fields = FieldFactory.getPointsFields();
//		fields.add(FieldFactory.getIdField(module));
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		FacilioField fieldId = fieldMap.get("fieldId");
		FacilioField resourceId = fieldMap.get("resourceId");
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("controllerId"), String.valueOf(controllerId), NumberOperators.EQUALS));

		if (!fetchCount) {
			builder.select(fields).orderBy(module.getTableName()+ "." + fieldMap.get("instance").getColumnName() + " ASC");
		}
		else {
			builder.select(FieldFactory.getCountField(module, fieldMap.get("controllerId")));
		}

		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("instanceType"), CommonOperators.IS_EMPTY));
		criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("instanceType"), String.valueOf(6), NumberOperators.LESS_THAN));
		
		if (searchText != null) {
			criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("instance"), searchText, StringOperators.CONTAINS));
		}

		builder.andCriteria(criteria);

		if (configuredOnly != null) {
			Criteria inUseCriteria = new Criteria();
			if (configuredOnly) {
				ControllerContext controller = ControllerAPI.getController(controllerId);
				if (controller.getControllerTypeEnum() != null && controller.getControllerTypeEnum().isConfigurable()) {
					inUseCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("configureStatus"), String.valueOf(PointEnum.ConfigureStatus.CONFIGURED.getIndex()), NumberOperators.EQUALS));
				}
			}
			else {
				inUseCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("configureStatus"), String.valueOf(PointEnum.ConfigureStatus.CONFIGURED.getIndex()), NumberOperators.NOT_EQUALS));
			}
			if (!inUseCriteria.isEmpty()) {
				builder.andCriteria(inUseCriteria);
			}
		}
		if (isSubscribed != null) {
			Criteria isSubscribedCriteria = new Criteria();
			NumberOperators operator = isSubscribed ? NumberOperators.EQUALS : NumberOperators.NOT_EQUALS;
			isSubscribedCriteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("subscribeStatus"), String.valueOf(PointEnum.SubscribeStatus.SUBSCRIBED.getIndex()), operator));
			builder.andCriteria(isSubscribedCriteria);
		}
		
		if(fetchMapped!=null) {
			if (!fetchMapped) {
				builder
				.andCondition(CriteriaAPI.getCondition(resourceId, CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldId, CommonOperators.IS_EMPTY));
			}
			else {
				builder
				.andCondition(CriteriaAPI.getCondition(resourceId, CommonOperators.IS_NOT_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldId, CommonOperators.IS_NOT_EMPTY))
				.orderBy(fieldMap.get("mappedTime").getColumnName() + " DESC");

			}
		}
		if (pagination != null) {
			int page = (int) pagination.get("page");
			int perPage = (int) pagination.get("perPage");

			int offset = ((page-1) * perPage);
			if (offset < 0) {
				offset = 0;
			}

			builder.offset(offset);
			builder.limit(perPage);
		}

		List<Map<String, Object>> props =  builder.get();

		LOGGER.debug("Query in getting instances : "+builder);

		if (props != null && !props.isEmpty() && !fetchCount) {
			return props.stream().map(prop -> {
				if (prop.get("instanceType") != null) {
					InstanceType type = InstanceType.valueOf((int) prop.get("instanceType"));
					if (type != null) {
						prop.put("instanceTypeVal", type.name());
					}
				}
				return prop;
			}).collect(Collectors.toList());
		}
		return props;

	}
	public static List<Map<String, Object>> getUnmodeledInstances (List<Long> ids) throws Exception {
		return getUnmodeledInstances(null, null, null, ids);
	}
	
	public static List<Map<String, Object>> getUnmodeledInstances (String device, String instance, Long controllerId, List<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getPointsModule();
		List<FacilioField> fields = FieldFactory.getPointsFields();
//		fields.add(FieldFactory.getIdField(module));
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				;
		
		if (ids != null ) {
			builder.andCondition(CriteriaAPI.getIdCondition(ids, module));
		}
		else if (instance != null) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("device"), device.replace(",", StringOperators.DELIMITED_COMMA), StringOperators.IS))
				   .andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), CommonOperators.IS_EMPTY))
				   .andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), CommonOperators.IS_EMPTY))
				   .andCondition(CriteriaAPI.getCondition(fieldMap.get("instance"), instance.replace(",", StringOperators.DELIMITED_COMMA), StringOperators.IS))
				   .andCondition(CriteriaAPI.getCondition(fieldMap.get("controllerId"), String.valueOf(controllerId), StringOperators.IS));
		}
		
		return builder.get();
	}
	
	public static List<Map<String, Object>> getSubscribedInstances (long controllerId) throws Exception {
		FacilioModule module = ModuleFactory.getPointsModule();
		List<FacilioField> fields = FieldFactory.getPointsFields();
//		fields.add(FieldFactory.getIdField(module));
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
//				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				 .andCondition(CriteriaAPI.getCondition(fieldMap.get("resourceId"), CommonOperators.IS_EMPTY))
				 .andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), CommonOperators.IS_EMPTY))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("controllerId"), String.valueOf(controllerId), StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("subscribed"), String.valueOf(true), BooleanOperators.IS))
				;
		
		return builder.get();
	}

	public static void setControlValue (long resourceId, long fieldId, String value) throws Exception {
		Map<String, Object> instance = getMappedInstance(resourceId, fieldId);
		if (instance != null) {
			instance.put("value", value);
			instance.put("fieldId", fieldId);
			LOGGER.info("Pusblishing message for control action : "+instance);
			IoTMessageAPI.publishIotMessage(Collections.singletonList(instance), IoTMessageAPI.IotCommandType.SET);
		}
	}
	
	public static int updateInstances(List<Long> ids, Map<String, Object> instance) throws Exception{
		FacilioModule module = ModuleFactory.getPointsModule();
		List<FacilioField> fields = FieldFactory.getPointsFields();
//		fields.add(FieldFactory.getIdField(module));
		
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
											.fields(fields)
											.table(module.getTableName())
											.andCondition(CriteriaAPI.getIdCondition(ids, module));
		
		return builder.update(instance);
	}
	
	public static List<Map<String, Object>> getUnmodeledData(String device, String instance, long controllerId) throws Exception {

		List<FacilioField> fields= FieldFactory.getUnmodeledDataFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		FacilioModule module = ModuleFactory.getUnmodeledDataModule();
		
		Map<String, Object> unmodeledInstance = getMappedInstance(device, instance, controllerId);
		if (unmodeledInstance == null) {
			LOGGER.info("Unmodelled Instance null check, device: " + device + ",instance: " + instance + ", controllerId: " + controllerId);
		}
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("newInstanceId"), String.valueOf(unmodeledInstance.get("id")), NumberOperators.EQUALS))
				.orderBy(fieldMap.get("ttime").getColumnName());

		List<Map<String, Object>> stats = builder.get();	
		if(stats!=null && !stats.isEmpty()) {
			return stats; 
		}
		return null;
	}
	public static List<Map<String, Object>> getPointsData() throws Exception {
		// TODO Auto-generated method stub
		FacilioModule module = ModuleFactory.getPointsModule();
		List<FacilioField> fields = FieldFactory.getPointsFields();
//		fields.add(FieldFactory.getIdField(module));

		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getOrgIdCondition(orgId, module))
				;
		return builder.get();
	}
	
	public static void insertPoints(List<Map<String, Object>> insertNewPointsData) throws SQLException {
		FacilioModule module = ModuleFactory.getPointsModule();
		GenericInsertRecordBuilder insertBuilderPoints = new GenericInsertRecordBuilder()
				.fields(FieldFactory.getPointsFields())
				.table(module.getTableName())
				.addRecords(insertNewPointsData);
		insertBuilderPoints.save();
	}
	
	public static Map<String, Object> getNewPointsData(long resourceId,long categoryId,long fieldId) throws Exception {
		Map<String, Object> pointsRecord = new HashMap<String,Object>();
		pointsRecord.put("resourceId", resourceId);
		pointsRecord.put("categoryId", categoryId);
		pointsRecord.put("fieldId", fieldId);
		pointsRecord.put("mappedTime", System.currentTimeMillis());
		return pointsRecord;
	}

	public static Map<String, Object> getUpdateMappedData(long assetId,long categoryId,long fieldId) throws Exception {
		Map<String, Object> pointsRecord = new HashMap<String,Object>();
		pointsRecord.put("assetId", assetId);
		pointsRecord.put("categoryId", categoryId);
		pointsRecord.put("fieldId", fieldId);
		pointsRecord.put("mappedTime", System.currentTimeMillis());
		return pointsRecord;
	}
	

	public static boolean isStage() {	

		return !FacilioProperties.isProduction();
	}

}
