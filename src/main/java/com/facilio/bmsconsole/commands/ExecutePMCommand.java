package com.facilio.bmsconsole.commands;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ExecutePMCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		long pmId = (long) context.get(FacilioConstants.ContextNames.RECORD_ID);
		if(pmId != -1) {
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
			WorkOrderContext wo = bean.addWorkOrderFromPM(pmId);
			context.put(FacilioConstants.ContextNames.WORK_ORDER, wo);
			context.put(FacilioConstants.ContextNames.ID, wo.getId());
		}
		return false;
	}

}
