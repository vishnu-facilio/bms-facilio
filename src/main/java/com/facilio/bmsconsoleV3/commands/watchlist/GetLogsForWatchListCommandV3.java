package com.facilio.bmsconsoleV3.commands.watchlist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsole.context.VisitorContext;
import com.facilio.bmsconsole.context.VisitorLoggingContext;
import com.facilio.bmsconsole.context.WatchListContext;
import com.facilio.bmsconsole.util.VisitorManagementAPI;
import com.facilio.bmsconsoleV3.context.V3VisitorContext;
import com.facilio.bmsconsoleV3.context.V3VisitorLoggingContext;
import com.facilio.bmsconsoleV3.context.V3WatchListContext;
import com.facilio.bmsconsoleV3.util.V3VisitorManagementAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.fields.FacilioField;
import com.facilio.v3.context.Constants;

public class GetLogsForWatchListCommandV3 extends FacilioCommand{

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
					if(record != null && (V3WatchListContext)record != null)
					{
						V3WatchListContext watchList = (V3WatchListContext)record;
						if(watchList != null) {
							V3VisitorContext visitor = V3VisitorManagementAPI.getVisitor(-1l, watchList.getPhone());
							if(visitor != null) {
								List<V3VisitorLoggingContext> visitorLoggingList = V3VisitorManagementAPI.getAllVisitorLogging(visitor.getId());
								watchList.setVisitorLogs(visitorLoggingList);
							}
						}
					}
				}
			}
		}
	
		return false;
	}

}

