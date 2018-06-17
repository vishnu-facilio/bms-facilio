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

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.events.constants.EventConstants;
import com.facilio.fw.BeanFactory;

public class ModeledDataCommand implements Command {
	private static final Logger LOGGER = LogManager.getLogger(ModeledDataCommand.class.getName());
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		
		Map<String, Map<String,String>> deviceData =(Map<String, Map<String,String>>) context.get(FacilioConstants.ContextNames.DEVICE_DATA);
		Long timeStamp=(Long)context.get(FacilioConstants.ContextNames.TIMESTAMP);
		
		Map<String,List<ReadingContext>> moduleVsReading = new HashMap<String,List<ReadingContext>> ();
		Map<String,ReadingContext> iModuleVsReading = new HashMap<String,ReadingContext> ();
		
		LOGGER.debug("Inside ModeledDataCommand####### deviceData: "+deviceData);
		for(Map.Entry<String, Map<String,String>> data:deviceData.entrySet()) {
			
			
			String deviceName=data.getKey();
			Map<String,String> instanceMap= data.getValue();
			Iterator<String> instanceList = instanceMap.keySet().iterator();
			while(instanceList.hasNext()) {
				String instanceName=instanceList.next();
				String instanceVal=instanceMap.get(instanceName);
				Map<String, Object> stat = ReadingsAPI.getInstanceMapping(deviceName, instanceName);
				if(stat==null)  {
					continue;
				}
				
				Long assetId= (Long) stat.get("assetId");
				Long fieldId= (Long) stat.get("fieldId");
				
				if(fieldId!=null && assetId!=null) {
					ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
					FacilioField field =bean.getField(fieldId);
					String moduleName=field.getModule().getName();
					if(instanceVal!=null && instanceVal.equalsIgnoreCase("NaN")) {
						JSONObject json= new JSONObject();
						json.put("resourceId", assetId);
						json.put("message", "Invalid value received for "+field.getDisplayName());
						json.put("timestamp", timeStamp);
						FacilioContext addEventContext = new FacilioContext();
						addEventContext.put(EventConstants.EventContextNames.EVENT_PAYLOAD, json);
						Chain getAddEventChain = EventConstants.EventChainFactory.getAddEventChain();
						getAddEventChain.execute(addEventContext);
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
		for(Map.Entry<String, ReadingContext> iMap:iModuleVsReading.entrySet()) {
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
		context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS,true);
		context.put(FacilioConstants.ContextNames.HISTORY_READINGS, false);
		
		return false;
	}

	
}
