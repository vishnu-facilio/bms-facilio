package com.facilio.bmsconsoleV3.commands.client;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.ClientContext;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class AddAddressForClientLocationCommandV3 extends FacilioCommand {	
	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        String moduleName = Constants.getModuleName(context);
		
		if(moduleName != null && !moduleName.isEmpty() && recordMap != null && MapUtils.isNotEmpty(recordMap)) 
		{
			List records = recordMap.get(moduleName);
			if(records != null && !records.isEmpty()) 
			{				
				for(Object record:records) 
				{
					if(record != null && (V3ClientContext)record != null)
					{
						V3ClientContext client = (V3ClientContext)record;		
						LocationContext address = client.getAddress();
						if (address == null) {
							address = new LocationContext();
							address.setLat(1.1);
							address.setLng(1.1);
						}
						address.setName(client.getName() + "_location");
						FacilioChain addLocation = FacilioChainFactory.addLocationChain();
						addLocation.getContext().put(FacilioConstants.ContextNames.RECORD, address);
						addLocation.execute();
						long locationId = (long) addLocation.getContext().get(FacilioConstants.ContextNames.RECORD_ID);
						address.setId(locationId);
						client.setAddress(address);
					}
				}
			}
		}
		return false;
	}	
}
