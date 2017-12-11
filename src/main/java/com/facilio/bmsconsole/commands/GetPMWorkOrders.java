package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class GetPMWorkOrders implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		if(pm != null) {
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			SelectRecordsBuilder<WorkOrderContext> selectBuilder = new SelectRecordsBuilder<WorkOrderContext>()
																		.beanClass(WorkOrderContext.class)
																		.moduleName(FacilioConstants.ContextNames.WORK_ORDER)
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
																		.innerJoin("PM_To_WO")
																		.on("WorkOrders.ID = PM_To_WO.WO_ID")
																		.andCustomWhere("PM_To_WO.PM_ID = ?", pm.getId())
																		.orderBy("WorkOrders.CREATED_TIME DESC")
																		;
			
			pm.setWorkorders(selectBuilder.get());
		}
		return false;
	}

}
