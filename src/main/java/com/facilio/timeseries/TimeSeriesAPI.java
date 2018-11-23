package com.facilio.timeseries;

import java.util.ArrayList;
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
import com.facilio.bacnet.BACNetUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.commands.TransactionChainFactory;
import com.facilio.bmsconsole.context.ControllerContext;
import com.facilio.bmsconsole.context.ReadingContext.SourceType;
import com.facilio.bmsconsole.context.ReadingDataMeta;
import com.facilio.bmsconsole.context.ReadingDataMeta.ReadingInputType;
import com.facilio.bmsconsole.criteria.BooleanOperators;
import com.facilio.bmsconsole.criteria.Condition;
import com.facilio.bmsconsole.criteria.Criteria;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.DateOperators;
import com.facilio.bmsconsole.criteria.NumberOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.FieldFactory;
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
		String timeString=(String)payLoad.remove("timestamp");
		if(timeString!=null) {
			try {
				timeStamp=Long.parseLong(timeString);
			}
			catch(NumberFormatException nfe) {}
		}
		if(timeStamp==-1)
		{
			timeStamp=	ttime;
		}
		return timeStamp;
	}

	
	public static void processHistoricalData(List<String>deviceList) throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.DEVICE_LIST , deviceList);
		context.put(FacilioConstants.ContextNames.READINGS_SOURCE, SourceType.KINESIS);
		Chain processDataChain = TransactionChainFactory.getProcessHistoricalDataChain();
		processDataChain.execute(context);
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
	
	private static List<ReadingDataMeta> getMetaList(long assetId, Map<String,Long> instanceFieldMap) throws Exception {
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
		return ReadingsAPI.getReadingDataMetaList(assetId, fields);
	}
	
	private static void checkForInputType(long assetId, long fieldId, String instanceName, Map<String, ReadingDataMeta> metaMap) throws Exception {
		ReadingDataMeta meta = metaMap.get(assetId+"|"+fieldId);
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
	
	public static void insertInstanceAssetMapping(String deviceName, long assetId, Map<String,Long> instanceFieldMap) throws Exception {
		List<ReadingDataMeta> metaList = getMetaList(assetId, instanceFieldMap);
		Map<String, ReadingDataMeta> metaMap = metaList.stream().collect(Collectors.toMap(meta -> meta.getResourceId()+"|"+meta.getFieldId(), Function.identity()));
		List<Map<String, Object>> records = new ArrayList<>();
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		for (Map.Entry<String, Long> entry : instanceFieldMap.entrySet()) {
			String instanceName = entry.getKey();
			long fieldId = entry.getValue();
			checkForInputType(assetId, fieldId, instanceName, metaMap);
			Map<String, Object> record = new HashMap<String,Object>();
			record.put("orgId", orgId);
			record.put("device", deviceName);
			record.put("assetId", assetId);
			record.put("instance", instanceName);
			record.put("fieldId", fieldId);
			records.add(record);
		};
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.fields(FieldFactory.getInstanceMappingFields())
				.table("Instance_To_Asset_Mapping")
				.addRecords(records);
		insertBuilder.save();
		ReadingsAPI.updateReadingDataMetaInputType(metaList, ReadingInputType.CONTROLLER_MAPPED);
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
	
	public static List<Map<String, Object>> getInstancesForController (long controllerId, Boolean... configuredOnly) throws Exception {
		FacilioModule module = ModuleFactory.getUnmodeledInstancesModule();
		List<FacilioField> fields = FieldFactory.getUnmodeledInstanceFields();
		fields.add(FieldFactory.getIdField(module));
		Map<String, FacilioField> fieldMap = FieldFactory.getAsMap(fields);
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table(module.getTableName())
				.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
				.andCondition(CriteriaAPI.getCondition(fieldMap.get("controllerId"), String.valueOf(controllerId), NumberOperators.EQUALS));
		
		if (configuredOnly != null && configuredOnly.length > 0) {
			builder.andCondition(CriteriaAPI.getCondition(fieldMap.get("inUse"), String.valueOf(configuredOnly[0]), BooleanOperators.IS));
		}

		 List<Map<String, Object>> props =  builder.get();
		 if (props != null && !props.isEmpty()) {
			 return props.stream().map(prop -> {
				 if (prop.get("instanceType") != null) {
					 prop.put("instanceTypeVal", BACNetUtil.getObjectType((int) prop.get("instanceType")));
				 }
				 return prop;
			}).collect(Collectors.toList());
		 }
		 return props;

	}
	
	public static int markInstancesAsUsed(List<Long> ids) throws Exception {
		Map<String, Object> prop = new HashMap<>();
		prop.put("inUse", true);
		return updateInstances(ids, prop);
	}
	
	public static int updateInstances(List<Long> ids, Map<String, Object> instance) throws Exception{
		FacilioModule module = ModuleFactory.getUnmodeledInstancesModule();
		List<FacilioField> fields = FieldFactory.getUnmodeledInstanceFields();
		fields.add(FieldFactory.getIdField(module));
		
		GenericUpdateRecordBuilder builder = new GenericUpdateRecordBuilder()
											.fields(fields)
											.table(module.getTableName())
											.andCondition(CriteriaAPI.getIdCondition(ids, module));
		
		return builder.update(instance);
	}
}
