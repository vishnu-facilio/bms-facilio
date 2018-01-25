package com.facilio.timeseries;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.StringOperators;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.bmsconsole.modules.FieldType;
import com.facilio.bmsconsole.modules.InsertRecordBuilder;
import com.facilio.bmsconsole.reports.ReportsUtil;
import com.facilio.fw.BeanFactory;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

public class TimeSeriesAPI {



	@SuppressWarnings("unchecked")
	public static void processPayLoad(long ttime, JSONObject payLoad) throws Exception {
		long timeStamp = getTimeStamp(ttime, payLoad);
		Iterator<String> deviceList = payLoad.keySet().iterator();
		while(deviceList.hasNext())
		{
			String deviceName = deviceList.next();
			JSONObject instanceRecord = (JSONObject)payLoad.get(deviceName);
			insertModeledReading(timeStamp,deviceName,instanceRecord);
			//by this time instanceRecord will have only unmodeled instances data..
			Iterator<String> instanceList = instanceRecord.keySet().iterator();
			while(instanceList.hasNext())
			{
				String instanceName=instanceList.next();
				String instanceVal=instanceRecord.get(instanceName).toString();

				try {
					Long instanceId= getUnmodledInstance( deviceName,instanceName);
					if(instanceId==null) {
						instanceId=getUnmodeledInstanceAfterInsert(deviceName, instanceName);
					}
					insertUnmodeledData(timeStamp, instanceVal, instanceId);
				}
				catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}


	@SuppressWarnings("unchecked")
	public static void insertModeledReading(long timeStamp,String deviceName,JSONObject instanceRecord) {

		String moduleName="energydata";
		Iterator<String> instanceList = instanceRecord.keySet().iterator();
		ReadingContext reading = new ReadingContext();
		while(instanceList.hasNext())
		{
			String instanceName=instanceList.next();
			String instanceVal=instanceRecord.get(instanceName).toString();

			try {
				Map<String, Object> stat = getInstanceMapping(deviceName, instanceName);
				if(stat==null) {
					continue;
				}
				Long assetId= (Long) stat.get("assetId");
				Long fieldId= (Long) stat.get("fieldId");

				if(fieldId!=null && assetId!=null) {
					ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioField field =bean.getField(fieldId);
					moduleName=field.getModule().getName();
					reading.addReading(field.getName(), instanceVal);
					reading.setParentId(assetId);
					//removing here to avoid going into unmodeled instance..
					instanceList.remove();
				}
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		if(reading.getReadings()!=null ) {

			try {
				reading.setTtime(timeStamp);
				insertReading(moduleName, reading);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	

	private static void insertReading(String moduleName, ReadingContext reading) throws Exception {

		ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		InsertRecordBuilder<ReadingContext> readingBuilder = new InsertRecordBuilder<ReadingContext>()
				.moduleName(moduleName)
				.fields(bean.getAllFields(moduleName))
				.addRecord(reading);
		readingBuilder.save();
	}
	private static Map<String, Object> getInstanceMapping(String deviceName, String instanceName) throws Exception {

		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(getInstanceMappingFields())
				.table("Instance_To_Asset_Mapping")
				.andCustomWhere("ORGID=?",orgId)
				.andCustomWhere("DEVICE_NAME= ?",deviceName)
				.andCustomWhere("INSTANCE_NAME=?",instanceName);

		List<Map<String, Object>> stats = builder.get();	
		if(stats!=null && !stats.isEmpty()) {

			return stats.get(0); 
		}
		return null;
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

	private static Long getUnmodledInstance(String deviceName, String instanceName) throws Exception {

		Long id =null;
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		List<FacilioField> instanceFields = new ArrayList<>();
		instanceFields.add(FieldFactory.getIdField());
		GenericSelectRecordBuilder unmodeledBuilder = new GenericSelectRecordBuilder()
				.select(instanceFields)
				.table("Unmodeled_Instance")
				.andCustomWhere("ORGID=?",orgId)
				.andCustomWhere("DEVICE_NAME= ?",deviceName)
				.andCustomWhere("INSTANCE_NAME=?",instanceName);

		List<Map<String, Object>> stats = unmodeledBuilder.get();	
		if(stats!=null && !stats.isEmpty()) {
			Map<String, Object> stat = stats.get(0);
			id=(Long)stat.get("id");
		}
		return id;
	}

	private static Long getUnmodeledInstanceAfterInsert(String deviceName, String instanceName) throws SQLException {

		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		Map<String, Object> value=new HashMap<String,Object>();
		value.put("orgId", orgId);
		value.put("device",deviceName);
		value.put("instance", instanceName);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.fields(getUnmodeledInstanceFields())
				.table("Unmodeled_Instance")
				.addRecord(value);
		insertBuilder.save();
		Long instanceId = (Long) value.get("id");
		return instanceId;
	}

	private static void insertUnmodeledData(long timeStamp, String instanceVal, long id) throws SQLException {
		Map<String, Object> value=new HashMap<String,Object>();
		value.put("instanceId", id);
		value.put("ttime",timeStamp);
		value.put("value", instanceVal);
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.fields(getUnmodeledDataFields())
				.table("Unmodeled_Data")
				.addRecord(value);
		insertBuilder.save();
	}
	
	

public static List<Map<String, Object>> fetchUnmodeledData(String deviceList) throws Exception {
	
	List<Map<String, Object>> result=null;
	if(deviceList==null) {
		return result;
	}
	List<FacilioField> fields = new ArrayList<>();
	FacilioField timeFld = ReportsUtil.getField("ttime","TTIME",FieldType.NUMBER);
	FacilioField deviceFld = ReportsUtil.getField("device","DEVICE_NAME",FieldType.STRING);
	FacilioField instanceFld = ReportsUtil.getField("instance","INSTANCE_NAME",FieldType.STRING);
	FacilioField valueFld = ReportsUtil.getField("value","VALUE",FieldType.STRING);
	
	
	fields.add(timeFld);
	fields.add(deviceFld);
	fields.add(instanceFld);
	fields.add(valueFld);
	
	 long orgId = AccountUtil.getCurrentOrg().getOrgId();
     GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(fields)
				.table("Unmodeled_Instance")
				.innerJoin("Unmodeled_Data")
				.on("Unmodeled_Instance.ID=Unmodeled_Data.INSTANCE_ID")
             .andCustomWhere("ORGID=?",orgId)
				.andCondition(CriteriaAPI.getCondition("DEVICE_NAME","DEVICE_NAME", deviceList, StringOperators.IS))
				.orderBy("TTIME ASC");
		try {
			result = builder.get();
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		return result;
}

@SuppressWarnings("unchecked")
public static void migrateUnmodeledData(List<Map<String, Object>> result) throws Exception  {
	

	if(result==null || result.isEmpty()) {
		return;
	}
	Map<Long,Map<String,JSONObject>> unmodeledDataMap = new HashMap<Long,Map<String,JSONObject>>();
		for(Map<String,Object> rowData: result)
		{
			Long timeStamp=(Long)	rowData.get("ttime");
			String deviceName=(String) rowData.get("device");
			String instanceName=(String) rowData.get("instance");
			String value=(String) rowData.get("value");
			Map<String,JSONObject> deviceData=  unmodeledDataMap.get(timeStamp);
			
			if(deviceData==null) {
				 deviceData = new HashMap<String,JSONObject>();
			}
			JSONObject instanceMapping= deviceData.get(deviceName);
			if(instanceMapping==null) {
				instanceMapping = new JSONObject();
			}
			instanceMapping.put(instanceName, value);
			deviceData.put(deviceName, instanceMapping);
			unmodeledDataMap.put(timeStamp,deviceData);
		}
		
	
	for(Map.Entry<Long,Map<String,JSONObject>> unmodeledData: unmodeledDataMap.entrySet()) {
		
		long timeStamp=unmodeledData.getKey();
		Map<String,JSONObject> deviceDataMap =unmodeledData.getValue();
		for(Map.Entry<String, JSONObject> deviceData :deviceDataMap.entrySet()) {
			
			String deviceName=deviceData.getKey();
			JSONObject instanceData=deviceData.getValue();
			TimeSeriesAPI.insertModeledReading(timeStamp, deviceName, instanceData);
		}
	}
	
}


	private static List<FacilioField> getInstanceMappingFields() {
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("orgId", "ORGID",FieldType.NUMBER ));
		fields.add(FieldFactory.getField("device", "DEVICE_NAME",FieldType.STRING ));
		fields.add(FieldFactory.getField("instance", "INSTANCE_NAME",FieldType.STRING ));
		fields.add(FieldFactory.getField("assetId", "ASSET_ID",FieldType.NUMBER ));
		fields.add(FieldFactory.getField("fieldId", "FIELD_ID",FieldType.NUMBER ));
		return fields;
	}

	private static List<FacilioField> getUnmodeledDataFields() {
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("instanceId", "INSTANCE_ID",FieldType.NUMBER ));
		fields.add(FieldFactory.getField("ttime", "TTIME",FieldType.NUMBER ));
		fields.add(FieldFactory.getField("value", "VALUE",FieldType.STRING ));
		return fields;
	}

	private static List<FacilioField> getUnmodeledInstanceFields() {
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getField("orgId", "ORGID",FieldType.NUMBER ));
		fields.add(FieldFactory.getField("device", "DEVICE_NAME",FieldType.STRING ));
		fields.add(FieldFactory.getField("instance", "INSTANCE_NAME",FieldType.STRING ));
		return fields;
	}

	public static Map<String, List<String>> getAllDevices() throws Exception {
		
		GenericSelectRecordBuilder builder = new GenericSelectRecordBuilder()
				.select(getUnmodeledInstanceFields())
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
				.fields(getInstanceMappingFields())
				.table("Instance_To_Asset_Mapping")
				.addRecords(records);
		insertBuilder.save();
	}
	

}
