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

public class ModeledDataCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		
		Map<String, Map<String,String>> deviceData =(Map<String, Map<String,String>>) context.get(FacilioConstants.ContextNames.DEVICE_DATA);
		Long timeStamp=(Long)context.get(FacilioConstants.ContextNames.TIMESTAMP);
		
		Map<String,List<ReadingContext>> moduleVsReading = new HashMap<String,List<ReadingContext>> ();
		for(Map.Entry<String, Map<String,String>> data:deviceData.entrySet()) {
			
			Map<String,ReadingContext> iModuleVsReading = new HashMap<String,ReadingContext> ();
			String deviceName=data.getKey();
			Map<String,String> instanceMap= data.getValue();
			Iterator<String> instanceList = instanceMap.keySet().iterator();
			while(instanceList.hasNext()) {
				String instanceName=instanceList.next();
				String instanceVal=instanceMap.get(instanceName);
				Map<String, Object> stat = ReadingsAPI.getInstanceMapping(deviceName, instanceName);
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
		
		context.put(FacilioConstants.ContextNames.MODELED_DATA,moduleVsReading);
		
		return false;
	}

	
}
