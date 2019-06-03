package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Chain;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.simple.JSONObject;

import com.facilio.accounts.util.AccountUtil;
import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FieldType;
import com.facilio.modules.fields.FacilioField;
import com.facilio.timeseries.TimeSeriesAPI;

public class ModeledDataCommand implements Command {
	private static final Logger LOGGER = LogManager.getLogger(ModeledDataCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		
		Map<String, Map<String,String>> deviceData =(Map<String, Map<String,String>>) context.get(FacilioConstants.ContextNames.DEVICE_DATA);
		Long timeStamp=(Long)context.get(FacilioConstants.ContextNames.TIMESTAMP);
		
		Map<String,List<ReadingContext>> moduleVsReading = new HashMap<String,List<ReadingContext>> ();
		Map<String,ReadingContext> iModuleVsReading = new HashMap<String,ReadingContext> ();
		Long controllerId= (Long) context.get(FacilioConstants.ContextNames.CONTROLLER_ID);
		List<Map<String,Object>> dataPointsValue=(List<Map<String, Object>>) context.get(FacilioConstants.ContextNames.DATA_POINTS);
		if(TimeSeriesAPI.isStage()) {
			LOGGER.debug(dataPointsValue+"Points data incomming");
		}
		List<Map<String, Object>> insertNewPointsData= new ArrayList< >();
		Map<String,Object>  dataPoints= null;
		long orgId = AccountUtil.getCurrentOrg().getOrgId();
	
//	if(TimeSeriesAPI.isStage()) {
			LOGGER.debug("Inside ModeledDataCommand####### deviceData: "+deviceData);

			for(Map.Entry<String, Map<String,String>> data:deviceData.entrySet()) {

				String deviceName=data.getKey();
				Map<String,String> instanceMap= data.getValue();
				Iterator<String> instanceList = instanceMap.keySet().iterator();
				while(instanceList.hasNext()) {
					String instanceName=instanceList.next();                
					String instanceVal=instanceMap.get(instanceName);
					if(deviceName ==null || instanceName==null) {
						continue;
					}
						dataPoints=  getValueContainsPointsData( deviceName,  instanceName, controllerId , dataPointsValue);
						if(dataPoints==null) {

						Map<String, Object> value=new HashMap<String,Object>();
						value.put("orgId", orgId);
						value.put("device",deviceName);
						value.put("instance", instanceName);
						value.put("createdTime", System.currentTimeMillis());
						if(controllerId!=null) {
							//this will ensure the new inserts after addition of controller gets proper controller id
							value.put("controllerId", controllerId);
						}
						insertNewPointsData.add(value);
						continue;
					}
					else {
						Long resourceId= (Long) dataPoints.get("resourceId");
						Long fieldId= (Long) dataPoints.get("fieldId");
						if(fieldId!=null && resourceId!=null){
//							ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
//							FacilioField field =bean.getField(fieldId);
//							FieldType type = field.getDataTypeEnum();
//							String moduleName=field.getModule().getName();
//							if(instanceVal!=null && (instanceVal.equalsIgnoreCase("NaN")||
//									(type.equals(FieldType.DECIMAL) && instanceVal.equalsIgnoreCase("infinity")))) {
//								generateEvent(resourceId,timeStamp,field.getDisplayName());
//								//Generate event with resourceId : assetId & 
//								continue;
//							}
//							String readingKey=moduleName+"|"+resourceId;
//							ReadingContext reading=iModuleVsReading.get(readingKey);
//							if(reading == null) {
//								reading = new ReadingContext();
//								iModuleVsReading.put(readingKey, reading);
//							}
//							reading.addReading(field.getName(), instanceVal);
//							reading.setParentId(resourceId);
//							reading.setTtime(timeStamp);
							//removing here to avoid going into unmodeled instance..
							// remove deviceData is important 
//							instanceList.remove();
							dataPointsValue.remove(dataPoints);
							//construct the reading to add in their respective module..????
						}

					}
				}
			}	
			if(!insertNewPointsData.isEmpty()) {
					TimeSeriesAPI.insertPoints(insertNewPointsData);
				
				dataPointsValue.addAll(insertNewPointsData);
			}
//	}

		//oldPublish data
		if(!TimeSeriesAPI.isStage()) {
		for(Map.Entry<String, Map<String,String>> data:deviceData.entrySet()) {
			
			
			String deviceName=data.getKey();
			Map<String,String> instanceMap= data.getValue();
			Iterator<String> instanceList = instanceMap.keySet().iterator();
			while(instanceList.hasNext()) {
				String instanceName=instanceList.next();
				String instanceVal=instanceMap.get(instanceName);
				//for now sending as null till migration..
				Map<String, Object> stat = ReadingsAPI.getInstanceMapping(deviceName, instanceName,controllerId);
				if(stat==null)  {
					continue;
				}
				
				Long assetId= (Long) stat.get("resourceId");
				Long fieldId= (Long) stat.get("fieldId");
				
				if(fieldId!=null && assetId!=null) {
					ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioField field =bean.getField(fieldId);
					FieldType type = field.getDataTypeEnum();
					String moduleName=field.getModule().getName();
					
					if(instanceVal!=null && (instanceVal.equalsIgnoreCase("NaN")||
							(type.equals(FieldType.DECIMAL) && instanceVal.equalsIgnoreCase("infinity")))) {
						generateEvent(assetId,timeStamp,field.getDisplayName());
						//Generate event with resourceId : assetId & 
						continue;
					}
					String readingKey=moduleName+"|"+assetId;
					ReadingContext reading=iModuleVsReading.get(readingKey);
					if(reading == null) {
						reading = new ReadingContext();
						iModuleVsReading.put(readingKey, reading);
					}
					reading.addReading(field.getName(), instanceVal);
					reading.setParentId(assetId);
					reading.setTtime(timeStamp);
					
					//removing here to avoid going into unmodeled instance..
					instanceList.remove();
				}
			}
			
			
		}
		}
		for(Map.Entry<String, ReadingContext> iMap:iModuleVsReading.entrySet()) { //send the data to their's module eg.Energy_Meter...
			String key=iMap.getKey();
			String moduleName=key.substring(0, key.indexOf("|"));
			ReadingContext reading=iMap.getValue();
			List<ReadingContext> readings=moduleVsReading.get(moduleName);
			if(readings==null) {
				readings= new ArrayList<ReadingContext>();
				moduleVsReading.put(moduleName, readings);
			}
			readings.add(reading);
		}
		LOGGER.debug("Inside ModeledDataCommand####### moduleVsReading: "+moduleVsReading);


		context.put(FacilioConstants.ContextNames.READINGS_MAP,moduleVsReading);
		context.put(FacilioConstants.ContextNames.HISTORY_READINGS, false);
//		if(TimeSeriesAPI.isStage()) {
			context.put(FacilioConstants.ContextNames.POINTS_DATA_RECORD, dataPointsValue);
//		}
		return false;
	}
	private Map<String,Object> getValueContainsPointsData(String deviceName, String instanceName,Long controllerId ,List<Map<String , Object>> points_Data) throws Exception{

		for (Map<String, Object> map : points_Data) {
			String mDeviceName=(String) map.get("device");
			String mInstanceName=(String) map.get("instance");
			Long mControllerId=(Long)map.get("controllerId");
			
			if(deviceName.equals(mDeviceName) && instanceName.equals(mInstanceName)) {
				
				if(controllerId==null || controllerId.equals(mControllerId) ) {
					// if controller is null.. then return map..
					// if not null.. then it should be equal to return map..
					return map;
				}
			}

		}
		return null;
	}
	public static void generateEvent(Long assetId,Long timeStamp,String displayName) throws Exception {
		JSONObject json= new JSONObject();
		json.put("resourceId", assetId);
		json.put("message", "Invalid value received for "+displayName);
		json.put("timestamp", timeStamp);
		FacilioContext addEventContext = new FacilioContext();
		addEventContext.put(EventConstants.EventContextNames.EVENT_PAYLOAD, json);
		Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
		getAddEventChain.execute(addEventContext);
}
}
