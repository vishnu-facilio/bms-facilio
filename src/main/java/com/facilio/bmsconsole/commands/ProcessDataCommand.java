package com.facilio.bmsconsole.commands;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.json.simple.JSONObject;

import com.facilio.constants.FacilioConstants;

public class ProcessDataCommand implements Command {

	@SuppressWarnings("unchecked")
	@Override
	public boolean execute(Context context) throws Exception {
		
		JSONObject payLoad =(JSONObject)context.get(FacilioConstants.ContextNames.PAY_LOAD);
		Iterator<String> keyList = payLoad.keySet().iterator();
		Map<String, Map<String,String>> deviceData= new HashMap<String, Map<String,String>>();
		System.err.println( Thread.currentThread().getName()+"Inside ProcessDataCommand####### incoming JSON: "+payLoad);
		while(keyList.hasNext())
		{
			String actualKey = keyList.next();
			JSONObject record = (JSONObject)payLoad.get(actualKey);
			String keyName=actualKey;
			
			if(actualKey.startsWith("DEVICE_") || actualKey.startsWith("POINT_")) {
				int firstIndex= actualKey.indexOf("_");
				keyName=actualKey.substring(firstIndex+1);
			}
			Iterator<String> innerKeyList = record.keySet().iterator();
			while(innerKeyList.hasNext())
			{
				String iKeyName=innerKeyList.next();
				String instanceVal=record.get(iKeyName).toString();
				String deviceName=iKeyName;//incase of POINT_ inner keyName is deviceName
				String instanceName=keyName;//incase of POINT_ keyName is instanceName

				if(!actualKey.startsWith("POINT_")) {
					deviceName=keyName;//incase of DEVICE_ & others keyName is deviceName
					instanceName=iKeyName;//incase of DEVICE_ & others inner KeyName is instanceName
				}
				Map<String,String> data= deviceData.get(deviceName);
				if(data==null) {
					data= new HashMap<String,String> ();
					deviceData.put(deviceName, data);
				}
				data.put(instanceName,instanceVal);
			}
		}
		System.err.println( Thread.currentThread().getName()+"Finished ProcessDataCommand####### : ");
		context.put(FacilioConstants.ContextNames.DEVICE_DATA, deviceData);
		return false;
	}

}
