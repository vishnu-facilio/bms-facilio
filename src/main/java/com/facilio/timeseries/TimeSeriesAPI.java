package com.facilio.timeseries;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioContext;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

public class TimeSeriesAPI {

	
	@SuppressWarnings({ "unchecked"})
	public static void processPayLoad(long ttime, JSONObject payLoad) throws Exception {
		
		long timeStamp = getTimeStamp(ttime, payLoad);
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.TIMESTAMP , timeStamp);
		context.put(FacilioConstants.ContextNames.PAY_LOAD , payLoad);
		Chain processDataChain = FacilioChainFactory.getProcessDataChain();
		processDataChain.execute(context);
		Map<String,List<ReadingContext>> moduleVsReading = (Map<String,List<ReadingContext>>)context.get(FacilioConstants.ContextNames.MODELED_DATA);
		insertRecords(moduleVsReading,true);
	}
	

	private static void insertRecords(Map<String,List<ReadingContext>> moduleVsReading, boolean updateLastReading)
			throws InstantiationException, IllegalAccessException, Exception {
		
		for(Map.Entry<String, List<ReadingContext>> map:moduleVsReading.entrySet()) {
			String moduleName=map.getKey();
			List<ReadingContext> readingsList=map.getValue();

			FacilioContext context = new FacilioContext();
			context.put(FacilioConstants.ContextNames.MODULE_NAME, moduleName);
			context.put(FacilioConstants.ContextNames.READINGS, readingsList);
			context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS,updateLastReading);
			context.put(FacilioConstants.ContextNames.HISTORY_READINGS, !updateLastReading);
			Chain addReading = FacilioChainFactory.getAddOrUpdateReadingValuesChain();
			addReading.execute(context);
		}
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

	
	@SuppressWarnings({ "unchecked"})
	public static void processHistoricalData(List<String>deviceList) throws Exception {
		
		FacilioContext context = new FacilioContext();
		context.put(FacilioConstants.ContextNames.DEVICE_LIST , deviceList);
		Chain processDataChain = FacilioChainFactory.getProcessHistoricalDataChain();
		processDataChain.execute(context);
		Map<String,List<ReadingContext>> moduleVsReading = (Map<String,List<ReadingContext>>)context.get(FacilioConstants.ContextNames.MODELED_DATA);
		insertRecords(moduleVsReading,false);
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
	
	public static void insertInstanceAssetMapping(String deviceName, long assetId, Map<String,Long> instanceFieldMap) throws Exception {
		List<Map<String, Object>> records = new ArrayList<>();
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		instanceFieldMap.forEach((instaceName, fieldId) -> {
			Map<String, Object> record = new HashMap<String,Object>();
			record.put("orgId", orgId);
			record.put("device", deviceName);
			record.put("assetId", assetId);
			record.put("instance", instaceName);
			record.put("fieldId", fieldId);
			records.add(record);
		});
		
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.fields(FieldFactory.getInstanceMappingFields())
				.table("Instance_To_Asset_Mapping")
				.addRecords(records);
		insertBuilder.save();
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
	

}
