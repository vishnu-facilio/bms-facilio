package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.bmsconsole.context.WorkOrderContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ExecutePMsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> pmIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(pmIds != null && !pmIds.isEmpty()) {
			Map<Long, Long> pmToWo = new HashMap<>();
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
			List<Long> woIds = new ArrayList<>();
			for(long pmId : pmIds) {
				WorkOrderContext wo = bean.addWorkOrderFromPM(pmId);
				if(wo != null) {
					woIds.add(wo.getId());
					pmToWo.put(pmId, wo.getId());
				}
			}
			context.put(FacilioConstants.ContextNames.WORK_ORDER_LIST, woIds);
			context.put(FacilioConstants.ContextNames.PM_TO_WO, pmToWo);
		}
		return false;
	}

}
