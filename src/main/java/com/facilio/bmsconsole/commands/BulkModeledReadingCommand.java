package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.ReadingContext;
import com.facilio.bmsconsole.modules.FacilioField;
import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class BulkModeledReadingCommand implements Command {
	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		
		Map<String,Map<Long,Map<String, String>>> historicalDataMap = (Map<String,Map<Long,Map<String, String>>>) 
				context.get(FacilioConstants.ContextNames.BULK_DEVICE_DATA);
		
		if(historicalDataMap==null || historicalDataMap.isEmpty()) {
			throw new Exception("No historical data found in Unmodeled data.");
		}
		
		
		 Map<String,Map<String,Map<String,Object>>> deviceMapping= ReadingsAPI.getDeviceMapping();
		 if(deviceMapping==null || deviceMapping.isEmpty()) {
			 throw new Exception("No device and instance mapping found.");
		 }
		
		Map<String,List<ReadingContext>> moduleVsReading = new HashMap<String,List<ReadingContext>> ();
		
		for(Map.Entry<String,Map<Long,Map<String, String>>> historicalData:historicalDataMap.entrySet())
		{

			String deviceName=historicalData.getKey();
			Map<Long,Map<String, String>> deviceData=historicalData.getValue();

			for(Map.Entry<Long, Map<String,String>> data:deviceData.entrySet()) {

				Map<String,ReadingContext> iModuleVsReading = new HashMap<String,ReadingContext> ();
				Long timeStamp=data.getKey();
				Map<String,String> instanceMap= data.getValue();
				Map<String,Map<String,Object>> instanceMapping=  deviceMapping.get(deviceName);
				if(instanceMapping==null) {
					continue;
				}
				Iterator<String> instanceList = instanceMap.keySet().iterator();
				while(instanceList.hasNext()) {
					String instanceName=instanceList.next();
					String instanceVal=instanceMap.get(instanceName);
					Map<String, Object> stat = instanceMapping.get(instanceName);
					if(stat==null || instanceVal.equalsIgnoreCase("NaN"))  {
						//need to decide whether  Nan can be ignored..
						continue;
					}
					Long assetId= (Long) stat.get("assetId");
					Long fieldId= (Long) stat.get("fieldId");

					if(fieldId!=null && assetId!=null) {
						ModuleBean bean = (ModuleBean) BeanFactory.lookup("ModuleBean");
						FacilioField field =bean.getField(fieldId);
						String moduleName=field.getModule().getName();
						ReadingContext reading=iModuleVsReading.get(moduleName);
						if(reading==null) {
							reading = new ReadingContext();
							iModuleVsReading.put(moduleName, reading);
						}
						reading.addReading(field.getName(), instanceVal);
						reading.setParentId(assetId);
						reading.setTtime(timeStamp);
						//removing here to avoid going into unmodeled instance..
						instanceList.remove();
					}
				}

				for(Map.Entry<String, ReadingContext> iMap:iModuleVsReading.entrySet()) {
					String moduleName=iMap.getKey();
					ReadingContext reading=iMap.getValue();
					List<ReadingContext> readings=moduleVsReading.get(moduleName);
					if(readings==null) {
						readings= new ArrayList<ReadingContext>();
						moduleVsReading.put(moduleName, readings);
					}
					readings.add(reading);
				}
			}
		}
		
		context.put(FacilioConstants.ContextNames.READINGS_MAP,moduleVsReading);
		context.put(FacilioConstants.ContextNames.UPDATE_LAST_READINGS,false);
		context.put(FacilioConstants.ContextNames.HISTORY_READINGS, true);
		
		return false;
	}

	

}
