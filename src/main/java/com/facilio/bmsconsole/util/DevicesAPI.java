package com.facilio.bmsconsole.util;

import java.util.Map;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.DeviceContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;

public class DevicesAPI {


	public static DeviceContext getDevice(long deviceID) throws Exception
	{
		
		FacilioChain fetchDetail = ReadOnlyChainFactory.fetchModuleDataDetailsChain();
        FacilioContext context =  fetchDetail.getContext();        
        context.put(FacilioConstants.ContextNames.ID, deviceID);
        context.put(FacilioConstants.ContextNames.MODULE_NAME, FacilioConstants.ModuleNames.DEVICES);
        fetchDetail.execute();
        DeviceContext deviceDetail = (DeviceContext) context.get(FacilioConstants.ContextNames.RECORD);
        return deviceDetail;
        
	}
//	public Map<String, Object>> getLogBookDetails(long deviceId)
//	{
//		GenericSelectRecordBuilder 
//	}
	
}
