package com.facilio.bmsconsole.commands;

import com.facilio.bmsconsole.util.ReadingsAPI;
import com.facilio.constants.FacilioConstants;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HistoricalReadingsCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		List<String> deviceList =(List<String>) context.get(FacilioConstants.ContextNames.DEVICE_LIST);
		List<Map<String, Object>> unmodeledData= ReadingsAPI.getUnmodeledData(deviceList);
		if(unmodeledData==null || unmodeledData.isEmpty()) {
			return false;
		}
		Map<String,Map<Long,Map<String, String>>> historicalData= processData(unmodeledData);
		context.put(FacilioConstants.ContextNames.BULK_DEVICE_DATA, historicalData);
		return false;
	}

	
	private Map<String,Map<Long,Map<String, String>>> processData(List<Map<String, Object>> unmodeledData) {
		
		Map<String,Map<Long,Map<String, String>>> deviceData= new HashMap<String, Map<Long ,Map<String, String>>>();
		for(Map<String,Object> unmodeledInstance:unmodeledData) {
			
			String deviceName=(String)unmodeledInstance.get("device");
			String instanceName=(String)unmodeledInstance.get("instance");
			String instanceVal=(String)unmodeledInstance.get("value");
			Long timeStamp=(Long)unmodeledInstance.get("ttime");
			
			Map<Long,Map<String, String>> timeData=	deviceData.get(deviceName);
			if(timeData==null){
				timeData=new HashMap<Long ,Map<String, String>>();
				deviceData.put(deviceName, timeData);
			}
			Map<String, String> instanceData=  timeData.get(timeStamp);
			if(instanceData==null) {
				instanceData=new HashMap<String, String>();
				timeData.put(timeStamp, instanceData);
			}
			instanceData.put(instanceName, instanceVal);
		}
		return deviceData;
		
	}


	
	
}


