package com.facilio.bmsconsole.commands;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleCRUDBean;
import com.facilio.constants.FacilioConstants;
import com.facilio.fw.BeanFactory;

public class ExecutePMsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> pmIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(pmIds != null && !pmIds.isEmpty()) {
			ModuleCRUDBean bean = (ModuleCRUDBean) BeanFactory.lookup("ModuleCRUD");
			List<Long> woIds = new ArrayList<>();
			for(long pmId : pmIds) {
				long woId = bean.addWorkOrderFromPM(pmId);
				if(woId != -1) {
					woIds.add(woId);
				}
			}
			context.put(FacilioConstants.ContextNames.WORK_ORDER_LIST, woIds);
		}
		return false;
	}

}
