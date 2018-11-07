package com.facilio.bmsconsole.commands;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.modules.FieldFactory;
import com.facilio.constants.FacilioConstants;
import com.facilio.sql.GenericInsertRecordBuilder;
import com.facilio.sql.GenericSelectRecordBuilder;

public class UnModeledDataCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {

		Map<String, Map<String,String>> deviceData =(Map<String, Map<String,String>>) context.get(FacilioConstants.ContextNames.DEVICE_DATA);
		long timeStamp=(long)context.get(FacilioConstants.ContextNames.TIMESTAMP);
		Long controllerId=(Long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		List<Map<String, Object>> records=new ArrayList<Map<String,Object>>();

		for(Map.Entry<String, Map<String,String>> data:deviceData.entrySet()) {
			String deviceName=data.getKey();
			Map<String,String> instanceMap= data.getValue();
			for(Map.Entry<String,String> map:instanceMap.entrySet()) {
				String instanceName=map.getKey();
				String instanceVal=map.getValue();
				if(instanceVal.equalsIgnoreCase("NaN")) {
					continue;
				}
				//for now passing controllerid as null till DB migration
				Long instanceId= getUnmodledInstance(deviceName,instanceName,null);
				if(instanceId==null) {
					instanceId=getUnmodeledInstanceAfterInsert(deviceName,instanceName,controllerId);
				}
				Map<String, Object> record=new HashMap<String,Object>();
				record.put("instanceId", instanceId);
				record.put("ttime",timeStamp);
				record.put("value", instanceVal);
				records.add(record);
			}
		}
		insertUnmodeledData(records);
		return false;
	}




	private  Long getUnmodledInstance(String deviceName, String instanceName, Long controllerId) throws Exception {

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
		if(controllerId!=null) {
			unmodeledBuilder=unmodeledBuilder.andCustomWhere("CONTROLLER_ID=?", controllerId);
		}

		List<Map<String, Object>> stats = unmodeledBuilder.get();	
		if(stats!=null && !stats.isEmpty()) {
			Map<String, Object> stat = stats.get(0);
			id=(Long)stat.get("id");
		}
		return id;
	}

	private  Long getUnmodeledInstanceAfterInsert(String deviceName, String instanceName, Long controllerId) throws SQLException {

		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		Map<String, Object> value=new HashMap<String,Object>();
		value.put("orgId", orgId);
		value.put("device",deviceName);
		value.put("instance", instanceName);
		if(controllerId!=null) {
			//this will ensure the new inserts after addition of controller gets proper controller id
			value.put("controllerId", controllerId);
		}
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.fields(FieldFactory.getUnmodeledInstanceFields())
				.table("Unmodeled_Instance")
				.addRecord(value);
		insertBuilder.save();
		Long instanceId = (Long) value.get("id");
		return instanceId;
	}

	private void insertUnmodeledData(List<Map<String, Object>> records) throws SQLException {

		if(records.isEmpty()) {
			return;
		}
		GenericInsertRecordBuilder insertBuilder = new GenericInsertRecordBuilder()
				.fields(FieldFactory.getUnmodeledDataFields())
				.table("Unmodeled_Data")
				.addRecords(records);
		insertBuilder.save();
	}
}
