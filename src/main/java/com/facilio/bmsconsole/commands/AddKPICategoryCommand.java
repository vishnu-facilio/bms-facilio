package com.facilio.bmsconsole.commands;

import com.facilio.command.FacilioCommand;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.KPICategoryContext;
import com.facilio.bmsconsole.util.KPIUtil;

public class AddKPICategoryCommand extends FacilioCommand {

	@Override
	public boolean executeCommand(Context context) throws Exception {
		
		KPICategoryContext kpiCategoryContext = (KPICategoryContext) context.get(KPIUtil.KPI_CATEGORY_CONTEXT);
		
		KPIUtil.addKPICategoryContext(kpiCategoryContext);
		return false;
	}

}
