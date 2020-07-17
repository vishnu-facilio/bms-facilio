package com.facilio.bmsconsoleV3.commands.client;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import com.facilio.bmsconsole.commands.FacilioChainFactory;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.LocationContext;
import com.facilio.bmsconsoleV3.context.V3ClientContext;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.v3.context.Constants;

public class UpdateAddressForClientLocationCommandV3 extends FacilioCommand {	
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
				
						if (address != null && address.getLat() != -1 && address.getLng() != -1) {
							FacilioChain locationChain = null;
							address.setName(client.getName() + "_Location");

							if (address.getId() > 0) {
								locationChain = FacilioChainFactory.updateLocationChain();
								locationChain.getContext().put(FacilioConstants.ContextNames.RECORD, address);
								locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, address.getId());
								locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST,
										Collections.singletonList(address.getId()));

								locationChain.execute();
								client.setAddress(address);
							} else {
								locationChain = FacilioChainFactory.addLocationChain();
								locationChain.getContext().put(FacilioConstants.ContextNames.RECORD, address);
								locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID, address.getId());
								locationChain.getContext().put(FacilioConstants.ContextNames.RECORD_ID_LIST,
										Collections.singletonList(address.getId()));

								locationChain.execute();
								long locationId = (long) locationChain.getContext()
										.get(FacilioConstants.ContextNames.RECORD_ID);
								address.setId(locationId);
							}
						} else {
							client.setAddress(null);
						}
					}
				}
			}
		}
		return false;
	}	
}