package com.facilio.bmsconsole.commands;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.chain.Command;
import org.apache.commons.chain.Context;

import com.facilio.beans.ModuleBean;
import com.facilio.bmsconsole.context.TicketContext;
import com.facilio.constants.FacilioConstants;
import com.facilio.db.criteria.CriteriaAPI;
import com.facilio.fw.BeanFactory;
import com.facilio.modules.FacilioModule;
import com.facilio.modules.SelectRecordsBuilder;

public class SplitDependentTicketsCommand implements Command {

	@Override
	public boolean execute(Context context) throws Exception {
		// TODO Auto-generated method stub
		List<Long> recordIds = (List<Long>) context.get(FacilioConstants.ContextNames.RECORD_ID_LIST);
		if(recordIds != null && !recordIds.isEmpty()) {
			String moduleName = (String) context.get(FacilioConstants.ContextNames.MODULE_NAME);
			Set<Long> dependentIds = null;
			switch (moduleName) {
				case FacilioConstants.ContextNames.WORK_ORDER:
						dependentIds = getRecords(recordIds, FacilioConstants.ContextNames.ALARM);
						if (dependentIds != null && !dependentIds.isEmpty()) {
							dependentIds = new HashSet<>(dependentIds);
							dependentIds.addAll(getRecords(recordIds, FacilioConstants.ContextNames.WORK_ORDER_REQUEST));
						}
						else {
							dependentIds = getRecords(recordIds, FacilioConstants.ContextNames.WORK_ORDER_REQUEST);
						}
						break;
				case FacilioConstants.ContextNames.WORK_ORDER_REQUEST:
				case FacilioConstants.ContextNames.ALARM:
				case FacilioConstants.ContextNames.READING_ALARM:
					dependentIds = getRecords(recordIds, FacilioConstants.ContextNames.WORK_ORDER);
						break;
				case FacilioConstants.ContextNames.TASK:
						break;
			}
			
			if (dependentIds != null && !dependentIds.isEmpty()) {
				recordIds.removeAll(dependentIds);
				context.put(FacilioConstants.ContextNames.DEPENDENT_TICKET_IDS, dependentIds);
			}
		}
		
		return false;
	}
	
	private Set<Long> getRecords(List<Long> recordIds, String moduleName) throws Exception {
		ModuleBean modBean = (ModuleBean) BeanFactory.lookup("ModuleBean");
		FacilioModule module = modBean.getModule(moduleName);
		SelectRecordsBuilder<? extends TicketContext> builder = new SelectRecordsBuilder<TicketContext>()
																					.select(modBean.getAllFields(moduleName))
																					.module(module)
																					.beanClass(FacilioConstants.ContextNames.getClassFromModule(module))
																					.andCondition(CriteriaAPI.getIdCondition(recordIds, module))
																					;
		return builder.getAsMap().keySet();
	}

}
