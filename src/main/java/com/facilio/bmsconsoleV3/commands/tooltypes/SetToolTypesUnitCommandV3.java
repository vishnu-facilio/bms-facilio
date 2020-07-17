package com.facilio.bmsconsoleV3.commands.tooltypes;

import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Context;
import org.apache.commons.collections.MapUtils;

import com.facilio.bmsconsole.commands.FacilioCommand;
import com.facilio.bmsconsoleV3.context.V3ItemTypesContext;
import com.facilio.bmsconsoleV3.context.V3ToolTypesContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.unitconversion.Unit;
import com.facilio.v3.context.Constants;

public class SetToolTypesUnitCommandV3 extends FacilioCommand{

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
					if(record != null && (V3ToolTypesContext)record != null)
					{
						V3ToolTypesContext item = (V3ToolTypesContext)record;
						if (item.getUnit() > 0) {
							item.setUnit(Unit.valueOf(item.getUnit()));
						}
					}
				}
			}
		}
	
		return false;
	}

}
