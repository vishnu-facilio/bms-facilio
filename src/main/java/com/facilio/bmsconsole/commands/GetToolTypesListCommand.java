package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.facilio.bmsconsole.context.ToolTypesContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.unitconversion.Unit;

public class GetToolTypesListCommand implements Command{
	private static Logger log = LogManager.getLogger(GetToolTypesListCommand.class.getName());
	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<ToolTypesContext> records = (List<ToolTypesContext>)context.get(FacilioConstants.ContextNames.RECORD_LIST);
		if(CollectionUtils.isNotEmpty(records)) {
			for(ToolTypesContext tool: records) {
				if (tool.getUnit() > 0) {
					tool.setUnit(Unit.valueOf(tool.getUnit()));
				}
			}
		}
		return false;
	}
}

