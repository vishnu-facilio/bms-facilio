package com.facilio.bmsconsole.commands;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.PreventiveMaintenance;
import com.facilio.modules.FacilioStatus;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.db.criteria.operators.PickListOperators;
import com.facilio.modules.fields.FacilioField;
import com.facilio.modules.SelectRecordsBuilder;
import com.facilio.bmsconsole.util.TicketAPI;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;
import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

public class GetPMWorkOrders implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		PreventiveMaintenance pm = (PreventiveMaintenance) context.get(FacilioConstants.ContextNames.PREVENTIVE_MAINTENANCE);
		if(pm != null) {
			FacilioStatus facilioStatus = TicketAPI.getStatus("preopen");
			ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
			FacilioField pmField = modBean.getField("pm", FacilioConstants.ContextNames.WORK_ORDER);
			FacilioField status = modBean.getField("status", FacilioConstants.ContextNames.WORK_ORDER);
			SelectRecordsBuilder<WorkOrderContext> selectBuilder = new SelectRecordsBuilder<WorkOrderContext>()
																		.beanClass(WorkOrderContext.class)
																		.moduleName(FacilioConstants.ContextNames.WORK_ORDER)
																		.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
																		.andCondition(CriteriaAPI.getCondition(pmField, String.valueOf(pm.getId()), PickListOperators.IS))
																		.andCondition(CriteriaAPI.getCondition(status, String.valueOf(facilioStatus.getId()), PickListOperators.ISN_T))
																		.orderBy("WorkOrders.CREATED_TIME DESC")
																		;
			pm.setWorkorders(selectBuilder.get());
		}
		return false;
	}

}
