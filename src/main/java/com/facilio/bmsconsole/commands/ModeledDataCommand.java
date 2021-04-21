package com.facilio.bmsconsole.commands;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.agent.controller.FacilioControllerType;
import com.facilio.agentv2.AgentConstants;
import com.facilio.agentv2.commands.AgentV2Command;
import com.facilio.agentv2.misc.MiscPoint;
import com.facilio.agentv2.point.PointsAPI;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.NumberOperators;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;
import com.facilio.timeseries.TimeSeriesAPI;
import com.facilio.util.FacilioUtil;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import java.util.*;

public class ModeledDataCommand extends AgentV2Command {
	private static final Logger LOGGER = LogManager.getLogger(ModeledDataCommand.class.getName());
	private boolean isV2;

	@SuppressWarnings("unchecked")
	@Override
	public boolean executeCommand(Context context) throws Exception {
		Long timeStamp = Long.parseLong(context.get(FacilioConstants.ContextNames.TIMESTAMP).toString());
		if (context.containsKey(AgentConstants.IS_NEW_AGENT) && (context.get(AgentConstants.IS_NEW_AGENT) != null) && (context.get(AgentConstants.IS_NEW_AGENT) instanceof Boolean)) {
			if ((boolean) context.get(AgentConstants.IS_NEW_AGENT)) {
				isV2 = true;
			}
		}
		if (isV2) {
//			LOGGER.info(" is v2");
			Map<String, Map<String, String>> deviceData2 = (Map<String, Map<String, String>>) context.get("DEVICE_DATA_2");
			List<Map<String, Object>> dataPointRecords = (List<Map<String, Object>>) context.get("DATA_POINTS");
			Map<String, ReadingContext> iModuleVsReading = new HashMap<String, ReadingContext>();
			Map<String, List<ReadingContext>> moduleVsReading = new HashMap<String, List<ReadingContext>>();
			Long controllerId =-1L;
			LOGGER.debug(" deviceData2 "+deviceData2);
			LOGGER.debug(" dataPointsValue "+dataPointRecords);
			for (Map.Entry<String, Map<String, String>> data : deviceData2.entrySet()){
				String deviceName = data.getKey(); // controller name
				if (deviceName.equals("UNKNOWN") ) {
					processUnknownController(context,data);
				}

				else{
					controllerId = (Long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
					LOGGER.debug("DataPointRecords : "+ dataPointRecords);
					processKnownController(controllerId,iModuleVsReading,dataPointRecords,deviceName,timeStamp,data);

				}

			}
			//oldPublish data
			for (Map.Entry<String, Map<String, String>> data : deviceData2.entrySet()) {

				String deviceName = data.getKey();
				if (deviceName.equalsIgnoreCase("UNKNOWN")) {
					continue;
				}
				Map<String, String> instanceMap = data.getValue();
				Iterator<String> instanceList = instanceMap.keySet().iterator();
				while (instanceList.hasNext()) {
					String instanceName = instanceList.next();
//				String instanceVal=instanceMap.get(instanceName);
					//for now sending as null till migration..
					Map<String, Object> stat = ReadingsAPI.getInstanceMapping(deviceName, instanceName, controllerId);
					if (stat == null) {
						continue;
					}
				}


			}

			addPointDataToContext(context, iModuleVsReading, moduleVsReading, dataPointRecords);
			//logModelledDataCommand(context);

			return false;
		} else {

			Map<String, Map<String, String>> deviceData = (Map<String, Map<String, String>>) context.get(FacilioConstants.ContextNames.DEVICE_DATA);
			Map<String, List<ReadingContext>> moduleVsReading = new HashMap<String, List<ReadingContext>>();
			Map<String, ReadingContext> iModuleVsReading = new HashMap<String, ReadingContext>();
			Long controllerId = (Long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);

			List<Map<String, Object>> dataPointsValue = (List<Map<String, Object>>) context.get("DATA_POINTS"); // points table rows
			if (TimeSeriesAPI.isStage()) {
				LOGGER.debug(dataPointsValue + "Points data incoming");
			}
			List<Map<String, Object>> insertNewPointsData = new ArrayList<>();
			Map<String, Object> dataPoints = null;
			long orgId = AccountUtil.getCurrentOrg().getOrgId();

			if (TimeSeriesAPI.isStage()) {
				LOGGER.debug("Inside ModeledDataCommand####### deviceData: " + deviceData);
			}
			
			for (Map.Entry<String, Map<String, String>> data : deviceData.entrySet()) {
				String deviceName = data.getKey(); // controller name

				Map<String, String> pointsMap = data.getValue(); // pointname-value map
				Iterator<String> pointsList = pointsMap.keySet().iterator(); // pointname list
				while (pointsList.hasNext()) {
					String pointName = pointsList.next();
					String pointValue = pointsMap.get(pointName);
					if (deviceName == null || pointName == null) {
						continue;
					}
					dataPoints = getValueContainsPointsData(deviceName, pointName, controllerId, dataPointsValue);
					if (dataPoints == null) {      //if it is a (new point and old agent) add to Points Table
						//if it is a (new point and new agent) do nothing
						LOGGER.info(" point is missing -> " + pointName);
						Map<String, Object> value = new HashMap<String, Object>();
						value.put("orgId", orgId);
						value.put("device", deviceName);
						value.put("instance", pointName);
						value.put("createdTime", System.currentTimeMillis());
						if (controllerId != null) {
							//this will ensure the new inserts after addition of controller gets proper controller id
							value.put("controllerId", controllerId);
						}
						insertNewPointsData.add(value);

					} else {                                //if it is not a new point,
						Long resourceId = (Long) dataPoints.get("resourceId");
						Long fieldId = (Long) dataPoints.get("fieldId");
						if (fieldId != null && resourceId != null) {
							ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
							FacilioField field = bean.getField(fieldId);
							FieldType type = field.getDataTypeEnum();
							String moduleName = field.getModule().getName();
							if (pointValue != null && (pointValue.equalsIgnoreCase("NaN") ||
									(type.equals(FieldType.DECIMAL) && pointValue.equalsIgnoreCase("infinity")))) {
								generateEvent(resourceId, timeStamp, field.getDisplayName());
								//								//Generate event with resourceId : assetId &
								continue;
							}
							String readingKey = moduleName + "|" + resourceId;
							ReadingContext reading = iModuleVsReading.get(readingKey);
							if (reading == null) {
								reading = new ReadingContext();
								iModuleVsReading.put(readingKey, reading);
							}
							reading.addReading(field.getName(), pointValue);
							reading.setParentId(resourceId);
							reading.setTtime(timeStamp);
							//removing here to avoid going into unmodeled instance..
							// remove deviceData is important
							pointsList.remove();
							dataPointsValue.remove(dataPoints);
							//construct the reading to add in their respective module..????
						}

					}
				}

			}
			if (!insertNewPointsData.isEmpty()) {
				TimeSeriesAPI.insertPoints(insertNewPointsData);

				dataPointsValue.addAll(insertNewPointsData);
			}
			//	}

			//oldPublish data
			for (Map.Entry<String, Map<String, String>> data : deviceData.entrySet()) {

				String deviceName = data.getKey();
				if (deviceName.equalsIgnoreCase("UNKNOWN")) {
					continue;
				}
				Map<String, String> instanceMap = data.getValue();
				Iterator<String> instanceList = instanceMap.keySet().iterator();
				while (instanceList.hasNext()) {
					String instanceName = instanceList.next();
//				String instanceVal=instanceMap.get(instanceName);
					//for now sending as null till migration..
					Map<String, Object> stat = ReadingsAPI.getInstanceMapping(deviceName, instanceName, controllerId);
					if (stat == null) {
						continue;
					}

				}


			}

			for (Map.Entry<String, ReadingContext> iMap : iModuleVsReading.entrySet()) { //send the data to their's module eg.Energy_Meter...
				String key = iMap.getKey();
				String moduleName = key.substring(0, key.indexOf("|"));
				ReadingContext reading = iMap.getValue();
				List<ReadingContext> readings = moduleVsReading.getOrDefault(moduleName, new ArrayList<>());
				readings.add(reading);
				moduleVsReading.put(moduleName, readings);
			}

			if (TimeSeriesAPI.isStage()) {
				LOGGER.debug("Inside ModeledDataCommand####### moduleVsReading: " + moduleVsReading);
			}

			context.put(FacilioConstants.ContextNames.READINGS_MAP, moduleVsReading);
			context.put(FacilioConstants.ContextNames.HISTORY_READINGS, false);
			context.put("POINTS_DATA_RECORD", dataPointsValue);
			return false;
		}
	}

	private void processKnownController(Long controllerId ,Map<String, ReadingContext> iModuleVsReading, List<Map<String, Object>> dataPointsValue, String deviceName, Long timeStamp, Map.Entry<String, Map<String, String>> data) throws Exception {
		Map<String, Object> dataPoints = null;


		List<String> pointsInDb = new ArrayList<>();
		for (Map<String, Object> dataPointName:dataPointsValue){
			if (dataPointName.containsKey("name") && !dataPointName.get("name").toString().isEmpty()){
				pointsInDb.add(dataPointName.get("name").toString());
			}
		}
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
		Map<String, String> pointsMap = data.getValue();
		Iterator<String> pointsList = pointsMap.keySet().iterator(); // pointname list
		while (pointsList.hasNext()) {
			String pointName = pointsList.next();
			String pointValue = pointsMap.get(pointName);
			if (deviceName == null || pointName == null) {
				continue;
			}
			dataPoints = getValueContainsPointsData(deviceName, pointName, controllerId, dataPointsValue);
			if (dataPoints==null) LOGGER.info(" dataPoints is null");
			if(pointsInDb.contains(pointName) && dataPoints!=null){
				Long resourceId = (Long) dataPoints.get("resourceId");
				Long fieldId = (Long) dataPoints.get("fieldId");
				if (fieldId != null && resourceId != null) {
					ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioField field = bean.getField(fieldId);
					FieldType type = field.getDataTypeEnum();
					String moduleName = field.getModule().getName();
					if (pointValue != null && (pointValue.equalsIgnoreCase("NaN") ||
							(type.equals(FieldType.DECIMAL) && pointValue.equalsIgnoreCase("infinity")))) {
						generateEvent(resourceId, timeStamp, field.getDisplayName());
						//								//Generate event with resourceId : assetId &
						continue;
					}
					String readingKey = moduleName + "|" + resourceId;
					ReadingContext reading = iModuleVsReading.get(readingKey);
					if (reading == null) {
						reading = new ReadingContext();
						iModuleVsReading.put(readingKey, reading);
					}
                    try {
                        reading.addReading(field.getName(), FacilioUtil.castOrParseValueAsPerType(field, pointValue));
                        reading.setParentId(resourceId);
                        reading.setTtime(timeStamp);
                    } catch (NumberFormatException ex) {
                        LOGGER.info("Error while converting to reading ", ex);
                    }
					//removing here to avoid going into unmodeled instance..
					// remove deviceData is important
					pointsList.remove();
					dataPointsValue.remove(dataPoints);
					//construct the reading to add in their respective module..????
				}
			}
		}
	}

	private void logModelledDataCommand(Context context) {
		LOGGER.info("--------Modelled data Command-----------");
		for (Object key : context.keySet()) {
			LOGGER.info(key + "->" + context.get(key));
		}
		LOGGER.info("-----------------------------");
	}

	private void addPointDataToContext(Context context, Map<String, ReadingContext> iModuleVsReading, Map<String, List<ReadingContext>> moduleVsReading, List<Map<String, Object>> dataPointsValue) {
		for (Map.Entry<String, ReadingContext> iMap : iModuleVsReading.entrySet()) { //send the data to their's module eg.Energy_Meter...
			String key = iMap.getKey();
			String moduleName = key.substring(0, key.indexOf("|"));
			ReadingContext reading = iMap.getValue();
			List<ReadingContext> readings = moduleVsReading.get(moduleName);
			if (readings == null) {
				readings = new ArrayList<ReadingContext>();
				moduleVsReading.put(moduleName, readings);
			}
			readings.add(reading);
		}
		if (TimeSeriesAPI.isStage()) {
			LOGGER.debug("Inside ModeledDataCommand####### moduleVsReading: " + moduleVsReading);
		}

		context.put(FacilioConstants.ContextNames.READINGS_MAP, moduleVsReading);
		context.put(FacilioConstants.ContextNames.HISTORY_READINGS, false);
		context.put("POINTS_DATA_RECORD", dataPointsValue);
	}

	private void processUnknownController( Context context,Map.Entry<String, Map<String, String>> data) throws Exception {
		Map<String, String> pointsMap = data.getValue(); // pointname-value map
		Iterator<String> pointsList = pointsMap.keySet().iterator(); // pointname list
		List<String> unknownPointNameList = new ArrayList<>();
		List<String> dbPointNameList = new ArrayList<>();
		while (pointsList.hasNext()) {//check pointList with pointsFrom db
			String pointName = pointsList.next();
			unknownPointNameList.add(pointName);
		}
		if (context.containsKey("DATA_POINTS_WITHOUT_CONTROLLER")) {
			List<Map<String, Object>> pointsFromDb = (List<Map<String, Object>>) context.get("DATA_POINTS_WITHOUT_CONTROLLER");
			for (Map<String, Object> point : pointsFromDb) {
				if (point != null && point.get("name") != null && !point.get("name").toString().isEmpty()) { //if point name equals pointname from db
					dbPointNameList.add(point.get("name").toString());
				}
			}
		}
		unknownPointNameList.removeAll(dbPointNameList);
		for (String pointName : unknownPointNameList) {
			long agentId = Long.parseLong(context.get(AgentConstants.AGENT_ID).toString());
			MiscPoint miscPoint = new MiscPoint(agentId);
			miscPoint.setDeviceName("UNKNOWN");
			miscPoint.setName(pointName);
			PointsAPI.addPoint(miscPoint);

		}
		dbPointNameList.addAll(unknownPointNameList);
		List<Map<String, Object>> newPointsFromDb = PointsAPI.getPointsFromDb(dbPointNameList, null);
		context.put("DATA_POINTS_WITHOUT_CONTROLLER", newPointsFromDb);

	}

	private FacilioControllerType getPointTypeFromControllerId(Long controllerId) throws Exception {
		List<FacilioField> fields = new ArrayList<>();
		fields.add(FieldFactory.getControllerTypeField());
		GenericSelectRecordBuilder genericSelectRecordBuilder = new GenericSelectRecordBuilder()
				.table(ModuleFactory.getNewControllerModule().getTableName())
				.select(fields)
				.andCondition(CriteriaAPI.getCondition(FieldFactory.getIdField(ModuleFactory.getNewControllerModule()), Collections.singleton(controllerId), NumberOperators.EQUALS));

		List<Map<String, Object>> row = genericSelectRecordBuilder.get();
		if (row.size()>0) {
			int type = Integer.parseInt(row.get(0).get("controllerType").toString());
			return FacilioControllerType.valueOf(type);
		}else{
			LOGGER.info("No row found in points for controllerId "+ controllerId);
			return null;
		}
	}

	private Map<String,Object> getValueContainsPointsData(String deviceName, String instanceName,Long controllerId ,List<Map<String , Object>> points_Data) throws Exception {
		//LOGGER.info("instanceName->"+instanceName);
		String mDeviceName = "";
		String mInstanceName = "";
		Long mControllerId = -1L;
		for (Map<String, Object> map : points_Data) {
			mDeviceName = (String) map.get("device");
			mInstanceName = (String) map.get("instance");
			mControllerId = (Long) map.get("controllerId");
			if (deviceName.equals(mDeviceName) && instanceName.equals(mInstanceName)) {

				if (controllerId == null || controllerId.equals(mControllerId)) {
					// if controller is null.. then return map..
					// if not null.. then it should be equal to return map..
					return map;
				}
			}

		}
		LOGGER.info(" point not found");
		return null;
	}
	public static void generateEvent(Long assetId,Long timeStamp,String displayName) throws Exception {
		JSONObject json= new JSONObject();
		json.put("resourceId", assetId);
		json.put("message", "Invalid value received for "+displayName);
		json.put("timestamp", timeStamp);
		FacilioContext addEventContext = new FacilioContext();
		addEventContext.put(EventConstants.EventContextNames.EVENT_PAYLOAD, json);
		FacilioChain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
		getAddEventChain.execute(addEventContext);
	}
}
