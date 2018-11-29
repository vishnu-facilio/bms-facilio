package com.facilio.timeseries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.chain.Chain;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.model.Record;
import com.facilio.accounts.util.AccountUtil;
import com.facilio.bacnet.BACNetUtil.InstanceType;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingType;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.CommonOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.ModuleFactory;
import com.facilio.bmsconsole.util.ControllerAPI;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;
import com.facilio.sql.GenericUpdateRecordBuilder;

public class TimeSeriesAPI {

	public static void processPayLoad(long ttime, JSONObject payLoad) throws Exception {
		processPayLoad(ttime, payLoad, null, null);
	}
	
	public static void processPayLoad(long ttime, JSONObject payLoad, Record record, IRecordProcessorCheckpointer checkpointer) throws Exception {
		
		long timeStamp = getTimeStamp(ttime, payLoad);
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TIMESTAMP , timeStamp);
		context.put(FacilioConstants.ContextNames.PAY_LOAD , payLoad);
		//Temp code. To be removed later *START*
		if (record != null) {
			context.put(FacilioConstants.ContextNames.KINESIS_RECORD, record);
			context.put(FacilioConstants.ContextNames.KINESIS_CHECK_POINTER, checkpointer);
			//even if the above two lines are removed.. please do not remove the below partitionKey..
			ControllerContext controller=  ControllerAPI.getController(record.getPartitionKey());
			if(controller!=null) {
				context.put(FacilioConstants.ContextNames.CONTROLLER_ID, controller.getId());
			}
		}
		//Temp code. To be removed later *END*
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.KINESIS);
		Chain processDataChain = TransactionChainFactory.getProcessDataChain();
		processDataChain.execute(context);
	}
	

	private static long getTimeStamp(long ttime, JSONObject payLoad) {

		long timeStamp=-1;
		Object timeString=payLoad.remove("timestamp");
		if(timeString!=null) {
			try {
				timeStamp = Long.parseLong(timeString.toString());
			}
			catch(NumberFormatException nfe) {}
		}
		if(timeStamp==-1)
		{
			timeStamp=	ttime;
		}
		return timeStamp;
	}
	
	public static Map<String, List<String>> getAllDevices() throws Exception {
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getUnmodeledInstanceFields())
				.table("Unmodeled_Instance")
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
		return metaList.stream().collect(Collectors.toMap(meta -> meta.getResourceId()+"|"+meta.getFieldId(), Function.identity()));
	}
	
	private static void checkForInputType(long assetId, long fieldId, String instanceName, Map<String, ReadingDataMeta> metaMap, boolean isUpdate) throws Exception {
		ReadingDataMeta meta = metaMap.get(assetId+"|"+fieldId);
		switch (meta.getInputTypeEnum()) {
			case CONTROLLER_MAPPED:
				if (!isUpdate) {
					throw new IllegalArgumentException("Field with ID "+fieldId+" for instance "+instanceName+" is already mapped");
				}
			case FORMULA_FIELD:
			case HIDDEN_FORMULA_FIELD:
				throw new IllegalArgumentException("Field with ID "+fieldId+" is formula field and therefore cannot be mapped");
			default:
				break;
		}
	}
	
	public static void insertOrUpdateInstance(String deviceName, long assetId, long categoryId, Long controllerId, String instance, long fieldId) throws Exception {
		Map<String, Object> modeledData = getMappedInstance(assetId, fieldId);
		if (modeledData == null) {
			insertInstanceAssetMapping(deviceName, assetId, categoryId, controllerId, Collections.singletonMap(instance, fieldId));			
		}
		else {
			updateInstanceAssetMapping(deviceName, assetId, categoryId, instance, fieldId);
		}
	}
	
	
	public static void insertInstanceAssetMapping(String deviceName, long assetId, long categoryId, Long controllerId, Map<String,Long> instanceFieldMap) throws Exception {
		
		List<Map<String, Object>> instanceDetails = getUnmodeledInstances(deviceName, instanceFieldMap.keySet(), null);
		Map<String,Map<String, Object>> instanceMap = instanceDetails.stream().collect(Collectors.toMap(instance -> (String) instance.get("instance"), Function.identity()));
		
		List<ReadingDataMeta> writableReadingList = new ArrayList<>();
		List<ReadingDataMeta> remainingReadingList = new ArrayList<>();
		
		Map<String, ReadingDataMeta> metaMap = getMetaMap(assetId, instanceFieldMap);
		List<Map<String, Object>> records = new ArrayList<>();
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		
		for (Map.Entry<String, Long> entry : instanceFieldMap.entrySet()) {
			String instanceName = entry.getKey();
			long fieldId = entry.getValue();
			checkForInputType(assetId, fieldId, instanceName, metaMap, false);
			Map<String, Object> record = new HashMap<String,Object>();
			record.put("orgId", orgId);
			record.put("device", deviceName);
			record.put("assetId", assetId);
			record.put("categoryId", categoryId);
			record.put("instance", instanceName);
			record.put("fieldId", fieldId);
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
			
			ReadingDataMeta meta = metaMap.get(assetId+"|"+fieldId);
			if (rType!=null && rType.isWritable()) {
				writableReadingList.add(meta);
			}
			else {
				remainingReadingList.add(meta);
			}
		};
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
	
	public static int updateInstanceAssetMapping(String deviceName, long assetId, long categoryId, String instanceName, long fieldId) throws Exception {
		
		Map<String, ReadingDataMeta> metaMap = getMetaMap(assetId, Collections.singletonMap(instanceName, fieldId));
		checkForInputType(assetId, fieldId, instanceName, metaMap, true);
		
		Map<String, Object> prop = new HashMap<>();
		prop.put("assetId", assetId);
		prop.put("categoryId", categoryId);
		prop.put("fieldId", fieldId);
		
		FacilioModule module = ModuleFactory.getInstanceMappingModule();
		List<FacilioField> fields = FieldFactory.getInstanceMappingFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
				.fields(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("device"), deviceName, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("instance"), instanceName, StringOperators.IS))
				;
		
		return builder.update(prop);
	}
	
	public static Map<String, Long> getDefaultInstanceFieldMap() throws Exception {
		Map<String, Long> fieldMap = new HashMap<>();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(FieldFactory.getInstanceMappingFields())
				.table("Instance_To_Asset_Mapping")
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
		FacilioModule module = ModuleFactory.getInstanceMappingModule();
		List<FacilioField> fields = FieldFactory.getInstanceMappingFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("assetId"), String.valueOf(assetId), NumberOperators.EQUALS))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("fieldId"), String.valueOf(fieldId), NumberOperators.EQUALS))
				;
		List<Map<String, Object>> props = builder.get();
		if(props!=null && !props.isEmpty()) {
			return props.get(0);
		}
		return null;
	}
	
	public static Map<String, Object> getMappedInstances(long controllerId) throws Exception {
		FacilioModule module = ModuleFactory.getInstanceMappingModule();
		List<FacilioField> fields = FieldFactory.getInstanceMappingFields();
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("controllerId"), String.valueOf(controllerId), NumberOperators.EQUALS))
				;
		List<Map<String, Object>> props = builder.get();
		Map<String, Object> deviceMap = new HashMap();
		if(props!=null && !props.isEmpty()) {
			for(Map<String, Object> prop: props) {
				String device = (String) prop.get("device");
				Map<String, Object> instanceMap = null;
				if (deviceMap.containsKey(device)) {
					Map<String, Object> deviceDetails = (Map<String, Object>) deviceMap.get(device);
					instanceMap = (Map<String, Object>) deviceDetails.get("instances");
				}
				else {
					Map<String, Object> deviceDetails = new HashMap<>();
					deviceDetails.put("assetId", prop.get("assetId"));
					deviceDetails.put("categoryId", prop.get("categoryId"));
					instanceMap = new HashMap<>();
					deviceDetails.put("instances", instanceMap);
					deviceMap.put(device, deviceDetails);
				}
				String instanceName = (String) prop.get("instance");
				instanceMap.put(instanceName, (Long) prop.get("fieldId"));
			}
			return deviceMap;
		}
		return null;
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
	
	public static  void addUnmodeledInstances(JSONArray instanceArray, Long controllerId) throws Exception {
		
		/*jsonObject should consists
		object.put("device",deviceName);
		object.put("instance", instanceName);
		object.put("objectInstanceNumber", objInstNumbr);
		object.put("instanceDescription",description);
		object.put("instanceType", instanceType);
		 */
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
	
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.fields(FieldFactory.getUnmodeledInstanceFields())
				.table("Unmodeled_Instance");
		for(Object instance:instanceArray) {
	
			JSONObject instanceObj = (JSONObject) instance;
			instanceObj.put("orgId", orgId);
			if(controllerId!=null) {
				//this will ensure the new inserts after addition of controller gets proper controller id
				instanceObj.put("controllerId", controllerId);
			}
			insertBuilder.addRecord(instanceObj);
		}
		insertBuilder.save();
	}
	
	public static List<Map<String, Object>> getUnmodeledInstancesForController (long controllerId, Boolean... configuredOnly) throws Exception {
		FacilioModule module = ModuleFactory.getUnmodeledInstancesModule();
		List<FacilioField> fields = FieldFactory.getUnmodeledInstanceFields();
		fields.add(FieldFactory.getIdField(module));
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("controllerId"), String.valueOf(controllerId), NumberOperators.EQUALS));
		
		Criteria criteria = new Criteria();
		criteria.addAndCondition(CriteriaAPI.getCondition(fieldMap.get("instanceType"), CommonOperators.IS_EMPTY));
		criteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("instanceType"), String.valueOf(60), NumberOperators.LESS_THAN));
		
		builder.andCriteria(criteria);
		
		if (configuredOnly != null && configuredOnly.length > 0 && configuredOnly[0] != null) {
			Criteria inUseCriteria = new Criteria();
			inUseCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("inUse"), String.valueOf(configuredOnly[0]), BooleanOperators.IS));
			if (configuredOnly[0]) {
				inUseCriteria.addOrCondition(CriteriaAPI.getCondition(fieldMap.get("objectInstanceNumber"), CommonOperators.IS_EMPTY));				
			}
			builder.andCriteria(inUseCriteria);
		}

		 List<Map<String, Object>> props =  builder.get();
		 if (props != null && !props.isEmpty()) {
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
		return getUnmodeledInstances(null, null, ids);
	}
	
	public static List<Map<String, Object>> getUnmodeledInstances (String device, Collection<String> instances, List<Long> ids) throws Exception {
		FacilioModule module = ModuleFactory.getUnmodeledInstancesModule();
		List<FacilioField> fields = FieldFactory.getUnmodeledInstanceFields();
		fields.add(FieldFactory.getIdField(module));
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				;
		
		if (ids != null ) {
			builder.andCondition(CriteriaAPI.getIdCondition(ids, module));
		}
		else if (instances != null) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("device"), device, StringOperators.IS))
				   .andCondition(CriteriaAPI.getCondition(fieldMap.get("instance"), StringUtils.join(instances, ","), StringOperators.IS));
		}
		
		return builder.get();
	}
	
	public static Map<String, Object> getUnmodeledInstance (long assetId, long fieldId) throws Exception {
		Map<String, Object> mappedInstance = getMappedInstance(assetId, fieldId);
		if (mappedInstance != null) {
			String instance = (String) mappedInstance.get("instance");
			List<Map<String, Object>> unmodeledInstances = getUnmodeledInstances((String)mappedInstance.get("device"), Collections.singletonList(instance), null);
			if (unmodeledInstances != null && !unmodeledInstances.isEmpty()) {
				return unmodeledInstances.get(0);
			}
		}
		return null;
	}
	
	public static int markUnmodeledInstancesAsUsed(List<Long> ids) throws Exception {
		return updateUnmodeledInstances(ids, Collections.singletonMap("inUse", true));
	}
	
	public static int updateUnmodeledInstances(List<Long> ids, Map<String, Object> instance) throws Exception{
		FacilioModule module = ModuleFactory.getUnmodeledInstancesModule();
		List<FacilioField> fields = FieldFactory.getUnmodeledInstanceFields();
		fields.add(FieldFactory.getIdField(module));
		
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
											.fields(fields)
											.table(module.getTableName())
											.andCondition(CriteriaAPI.getIdCondition(ids, module));
		
		return builder.update(instance);
	}
	
	public static JSONObject constructIotMessage (List<Map<String, Object>> instances, String command) throws Exception {
		
		ControllerContext controller = ControllerAPI.getController((long) instances.get(0).get("controllerId"));
		
		JSONObject obj = new JSONObject();
		obj.put("command", command);
		
		obj.put("deviceName", controller.getName());
		obj.put("macAddress", controller.getMacAddr());
		obj.put("subnetPrefix", controller.getSubnetPrefix());
		obj.put("networkNumber", controller.getNetworkNumber());
		obj.put("instanceNumber", controller.getInstanceNumber());
		obj.put("broadcastAddress", controller.getBroadcastIp());
		
		JSONArray points = new JSONArray();
		obj.put("points", points);
		
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		for(Map<String, Object> instance : instances) {
			JSONObject point = new JSONObject();
			point.put("instance", instance.get("instance"));
			point.put("instanceType", instance.get("instanceType"));
			point.put("device", instance.get("device"));
			point.put("objectInstanceNumber", instance.get("objectInstanceNumber"));
			point.put("instanceDescription", instance.get("instanceDescription"));
			if (instance.get("value") != null) {
				point.put("newValue", instance.get("value"));
				point.put("valueType", getValueType(modBean.getField((long) instance.get("fieldId")).getDataTypeEnum()));
			}
			points.add(point);
		}
		
		return obj;
	}
	
	private static String getValueType(FieldType fieldType) {
		String type = null;
		switch(fieldType) {
			case NUMBER:
				type = "signed";
				break;
			case DECIMAL:
				type = "double";
				break;
			case BOOLEAN:
				type = "boolean";
				break;
			case STRING:
				type = "string";
		}
		return type;
	}
}
