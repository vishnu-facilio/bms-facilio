package com.facilio.bmsconsoleV3.commands.watchlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.WatchListContext;
import com.facilio.bmsconsole.util.RecordAPI;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsoleV3.context.V3VisitorContext;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.bmsconsoleV3.context.V3WatchListContext;
import com.facilio.bmsconsoleV3.util.V3RecordAPI;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.ModuleBaseWithCustomFields;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class CheckForExisitingWatchlistRecordsCommandV3 extends FacilioCommand {

    @Override
	public boolean executeCommand(Context context) throws Exception {
		
    	Map<String, List> recordMap = (Map<String, List>) context.get(Constants.RECORD_MAP);
        String moduleName = Constants.getModuleName(context);
		
		if(moduleName != null && !moduleName.isEmpty() && recordMap != null && MapUtils.isNotEmpty(recordMap)) 
		{
			List records = recordMap.get(moduleName);
			if(records != null && !records.isEmpty()) 
			{		
				ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
				FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WATCHLIST);
				List<FacilioField> fields = modBean.getAllFields(FacilioConstants.ContextNames.WATCHLIST);
				List<V3WatchListContext> toBeAdded = new ArrayList<V3WatchListContext>();
				
				for(Object record:records) 
				{
					if(record != null && (V3WatchListContext)record != null)
					{
						V3WatchListContext wL = (V3WatchListContext)record;
						V3WatchListContext existing = V3VisitorManagementAPI.getBlockedWatchListRecordForPhoneNumber(wL.getPhone(), wL.getEmail());
						if(existing != null) {
							wL.setId(existing.getId());
							if(wL.getIsBlocked() == null) {
								wL.setIsBlocked(false);
							}
							if(wL.getIsVip() == null) {
								wL.setIsVip(false);
							}
		                    V3RecordAPI.updateRecord(wL, module, fields);
						}
						else {
							toBeAdded.add(wL);
						}					
					}				
				}
				
				if(CollectionUtils.isNotEmpty(toBeAdded)) {
					context.put(Constants.RECORD_MAP, Collections.singletonMap(moduleName, toBeAdded));
				}
				else {
					return true;
				}
			}
		}
		
	
		return false;   	
    }
}
