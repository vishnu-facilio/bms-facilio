package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agentv2.AgentConstants;
import com.facilio.beans.ModuleBean;
import com.facilio.command.FacilioCommand;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericInsertRecordBuilder;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.builder.GenericUpdateRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.CommonOperators;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.db.criteria.operators.StringOperators;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.*;

public class UnModeledDataCommand extends FacilioCommand {

	private static final Logger LOGGER = LogManager.getLogger(UnModeledDataCommand.class.getName());
	private boolean isV2;
	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		if (context.containsKey(AgentConstants.IS_NEW_AGENT) && (context.get(AgentConstants.IS_NEW_AGENT) != null) && (context.get(AgentConstants.IS_NEW_AGENT) instanceof Boolean)) {
			if ((boolean) context.get(AgentConstants.IS_NEW_AGENT)) {
				isV2 = true;
			}
		}
		Map<String, Map<String, String>> deviceData;
		if (isV2) deviceData = (Map<String, Map<String, String>>) context.get("DEVICE_DATA_2");
		else deviceData = (Map<String, Map<String, String>>) context.get(FacilioConstants.ContextNames.DEVICE_DATA);
		long timeStamp = Long.parseLong(context.get(FacilioConstants.ContextNames.TIMESTAMP).toString());
		Long controllerId = (Long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		List<Map<String, Object>> records = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> pointsRecords = (List<Map<String, Object>>) context.get("POINTS_DATA_RECORD");
		for (Map.Entry<String, Map<String, String>> data : deviceData.entrySet()) {
			String deviceName = data.getKey();// controller name
			Map<String, String> instanceMap = data.getValue(); // timeseries data
			for (Map.Entry<String, String> map : instanceMap.entrySet()) {
				String pointName = map.getKey();
				String instanceVal = map.getValue();
				if (instanceVal.equalsIgnoreCase("NaN")) {
					continue;
				}
				Map<String, Object> record = new HashMap<String, Object>();
				record.put("value", instanceVal);
				record.put("ttime", timeStamp);
				if (isV2) {
					long pointId;
					if (controllerId==-1) {
						pointId = getPointIdWithNullController(pointName);
					}
					else {
						pointId = getPointId(controllerId, pointName);
					}
					if (pointId >= 0) {
						record.put("instanceId", pointId);
					}

				}else{
					long newInstanceId = getPointsUnmodledInstance(deviceName,pointName,controllerId,pointsRecords);
					if (newInstanceId >=0 ) {
						record.put("newInstanceId", newInstanceId);
					}
				}
				records.add(record);
			}
		}
		insertUnmodeledData(records);
		return false;
	}

	private long getPointIdWithNullController(String pointName) throws Exception {
		ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule pointModule = moduleBean.getModule(AgentConstants.POINT);
		List<FacilioField>fields = new ArrayList<>();
		if (pointModule == null){
			pointModule = ModuleFactory.getPointModule();
			fields = FieldFactory.getPointFields();
		}
		else {
			fields = moduleBean.getAllFields(AgentConstants.POINT);
		}
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.table(pointModule.getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(pointModule),pointName, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(pointModule), CommonOperators.IS_EMPTY));
		List<Map<String, Object>> rows = selectRecordBuilder.get();
		if (rows.size()>0){
			if(rows.get(0).containsKey(AgentConstants.ID))
				return Long.parseLong(rows.get(0).get(AgentConstants.ID).toString());
		}
		return -1;
	}

	private long getPointsId(Long controllerId, String pointName) throws Exception {
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getPointsModule().getTableName())
				.select(FieldFactory.getPointsFields())
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(ModuleFactory.getPointsModule()),pointName, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(ModuleFactory.getPointsModule()), Collections.singleton(controllerId),NumberOperators.EQUALS));
		List<Map<String, Object>> rows = selectRecordBuilder.get();
		if (rows.size()>0){
			if(rows.get(0).containsKey(AgentConstants.ID))
				return Long.parseLong(rows.get(0).get(AgentConstants.ID).toString());
		}
		return -1;
	}
	private long getPointId(Long controllerId, String pointName) throws Exception {
		ModuleBean moduleBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule pointModule = moduleBean.getModule(AgentConstants.POINT);
		List<FacilioField>fields = new ArrayList<>();
		if (pointModule == null){
			pointModule = ModuleFactory.getPointModule();
			fields = FieldFactory.getPointFields();
		}
		else {
			fields = moduleBean.getAllFields(AgentConstants.POINT);
		}
		GenericSelectRecordBuilder selectRecordBuilder = new GenericSelectRecordBuilder()
				.table(pointModule.getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getNameField(pointModule),pointName, StringOperators.IS))
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getControllerIdField(pointModule), Collections.singleton(controllerId),NumberOperators.EQUALS));
		List<Map<String, Object>> rows = selectRecordBuilder.get();
		if (rows.size()>0){
			if(rows.get(0).containsKey(AgentConstants.ID))
			return Long.parseLong(rows.get(0).get(AgentConstants.ID).toString());
		}
		return -1;
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

	private  Long getPointsUnmodledInstance(String deviceName, String instanceName, Long controllerId,List<Map<String, Object>> pointsRecords) throws Exception {
		Iterator<Map<String,Object>> itr= pointsRecords.iterator();
		while (itr.hasNext()) {
			Map<String,Object> map= itr.next();
			Long id= (Long) map.get("id");
			String mDeviceName=(String) map.get("device");
			String mInstanceName=(String) map.get("instance");
			if((mDeviceName.equals(deviceName) && mInstanceName.equals(instanceName))){
				itr.remove();
				return id;
			}
		}
		return null;

	}

	private void updateControllerForInstance(long instanceId, long controllerId) {

		try {
			FacilioModule module = ModuleFactory.getUnmodeledInstancesModule();
			List<FacilioField> fields = FieldFactory.getUnmodeledInstanceFields();

			Map<String, Object> prop = Collections.singletonMap("controllerId", controllerId);
			GenericUpdateRecordBuilder updateBuilder = new GenericUpdateRecordBuilder()
					.table(module.getTableName())
					.fields(fields)
//					.andCondition(CriteriaAPI.getCurrentOrgIdCondition(module))
					.andCustomWhere("ID=?", instanceId);
			;
			updateBuilder.update(prop);


		}
		catch(Exception e) {
			System.err.println("Exception while updating the controller Id: "+e.getMessage());

		}


	}


	private  Long getUnmodeledInstanceAfterInsert(String deviceName, String instanceName, Long controllerId) throws SQLException {

		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		Map<String, Object> value=new HashMap<String,Object>();
		value.put("orgId", orgId);
		value.put("device",deviceName);
		value.put("instance", instanceName);
		value.put("createdTime", System.currentTimeMillis());
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
