package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.criteria.PickListOperators;
import com.facilio.bmsconsole.modules.FacilioField;
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
			FacilioField pmField = modBean.getField("pm", FacilioConstants.ContextNames.WORK_ORDER);
			SelectRecordsBuilder<WorkOrderContext> selectBuilder = new SelectRecordsBuilder<WorkOrderContext>()
																		.beanClass(WorkOrderContext.class)
																		.moduleName(FacilioConstants.ContextNames.WORK_ORDER)
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
																		.andCondition(CriteriaAPI.getCondition(pmField, String.valueOf(pm.getId()), PickListOperators.IS))
																		.orderBy("WorkOrders.CREATED_TIME DESC")
																		;
			
			pm.setWorkorders(selectBuilder.get());
		}
		return false;
	}

}
