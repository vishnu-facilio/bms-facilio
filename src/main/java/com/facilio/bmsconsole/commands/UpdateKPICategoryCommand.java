package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.bmsconsole.context.KPICategoryContext;
import com.facilio.bmsconsole.util.KPIUtil;

public class UpdateKPICategoryCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		
		KPICategoryContext kpiCategoryContext = (KPICategoryContext) context.get(KPIUtil.KPI_CATEGORY_CONTEXT);
		
		KPIUtil.updateKPICategoryContext(kpiCategoryContext);
		return false;
	}

}
