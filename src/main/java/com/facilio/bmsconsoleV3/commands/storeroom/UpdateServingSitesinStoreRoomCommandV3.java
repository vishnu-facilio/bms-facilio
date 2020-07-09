package com.facilio.bmsconsoleV3.commands.storeroom;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.StoreRoomContext;
import com.facilio.bmsconsoleV3.commands.TransactionChainFactoryV3;
import com.facilio.chain.FacilioChain;
import com.facilio.constants.FacilioConstants;
import com.facilio.modules.FieldUtil;
import com.facilio.modules.ModuleBaseWithCustomFields;

public class UpdateServingSitesinStoreRoomCommandV3 extends FacilioCommand {
    @Override
    public boolean executeCommand(Context context) throws Exception {
    	
		Map<String, List> recordMap = (Map<String, List>) context.get(FacilioConstants.ContextNames.RECORD_MAP);
		String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
		
		if(moduleName != null && !moduleName.isEmpty() && recordMap != null && MapUtils.isNotEmpty(recordMap)) {
			List records = recordMap.get(moduleName);
			if(records != null && !records.isEmpty()) {
				for(Object record:records) 
				{		
					if(record != null && record instanceof ModuleBaseWithCustomFields && (ModuleBaseWithCustomFields)record != null){					
						context.put(FacilioConstants.ContextNames.RECORD_ID, ((ModuleBaseWithCustomFields)record).getId());
						Map<String, Object> recordProps = FieldUtil.getAsProperties(record);
						if(recordProps != null && recordProps.get(FacilioConstants.ContextNames.SITE_LIST) != null) {
							context.put(FacilioConstants.ContextNames.SITES_FOR_STORE_ROOM, recordProps.get(FacilioConstants.ContextNames.SITE_LIST));
						}
				    	FacilioChain chain = TransactionChainFactoryV3.getUpdateServingSitesinStoreRoom();
				    	chain.execute(context);
					}		
				}		
			}
		}
		return false;	
    }
}
