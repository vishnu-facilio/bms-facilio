package com.facilio.bmsconsole.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.facilio.bmsconsole.commands.ReadOnlyChainFactory;
import com.facilio.bmsconsole.context.DeviceContext;
import com.facilio.bmsconsole.context.VisitorKioskContext;
import com.facilio.chain.FacilioChain;
import com.facilio.chain.FacilioContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.builder.GenericSelectRecordBuilder;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.FieldFactory;
import com.facilio.modules.ModuleFactory;
import com.facilio.modules.fields.FacilioField;

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
	
	public static VisitorKioskContext getVisitorKioskConfig(long deviceId) throws Exception
	{
		
		FacilioChain visitorKioskConfigDetailsChain = ReadOnlyChainFactory.getVisitorKioskConfigDetailsChain();
        FacilioContext context =  visitorKioskConfigDetailsChain.getContext();        
        context.put(FacilioConstants.ContextNames.ID, deviceId);
        visitorKioskConfigDetailsChain.execute();
        
        Object record =context.get(FacilioConstants.ContextNames.RECORD);
        if(record!=null)
        {
            VisitorKioskContext visitorKioskConfig = (VisitorKioskContext) context.get(FacilioConstants.ContextNames.RECORD);
            return visitorKioskConfig;
        }
        else {
        	return null;
        }

	}
//	public Map<String, Object>> getLogBookDetails(long deviceId)
//	{
//		GenericSelectRecordBuilder 
//	}
	
}
