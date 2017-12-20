package com.facilio.bmsconsole.commands;

import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.bmsconsole.criteria.CriteriaAPI;
import com.facilio.bmsconsole.modules.FacilioModule;
import com.facilio.bmsconsole.modules.SelectRecordsBuilder;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ExecutePMCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long pmId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if(pmId != -1) {
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
			long woId = bean.addWorkOrderFromPM(pmId);
			context.put(FacilioConstants.ContextNames.WORK_ORDER, getWo(woId));
		}
		return false;
	}
	
	private WorkOrderContext getWo(long woId) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(FacilioConstants.ContextNames.WORK_ORDER);
		SelectRecordsBuilder<WorkOrderContext> builder = new SelectRecordsBuilder<WorkOrderContext>()
															.module(module)
															.beanClass(WorkOrderContext.class)
															.select(modBean.getAllFields(FacilioConstants.ContextNames.WORK_ORDER))
															.andCondition(CriteriaAPI.getIdCondition(woId, module));
		
		List<WorkOrderContext> workOrders = builder.get();
		if(workOrders.size() > 0) {
			WorkOrderContext workOrder = workOrders.get(0);
			return workOrder;
		}
		return null;
	}

}
